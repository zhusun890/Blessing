package catmoe.fallencrystal.moefilter.api.event

import catmoe.fallencrystal.moefilter.util.plugin.FilterPlugin
import net.md_5.bungee.api.ProxyServer
import java.util.concurrent.CopyOnWriteArrayList

object EventManager {

    private val listeners: MutableList<EventListener> = CopyOnWriteArrayList()

    fun triggerEvent(event: Any) {
        ProxyServer.getInstance().scheduler.runAsync(FilterPlugin.getPlugin()) {
            if (listeners.isEmpty()) return@runAsync
            for (it in listeners) {
                val methods = it.javaClass.declaredMethods
                if (methods.isNullOrEmpty()) return@runAsync
                for (method in methods) {
                    if (method.isAnnotationPresent(FilterEvent::class.java) && method.parameterCount == 1 && event::class.java.isAssignableFrom(method.parameterTypes[0])) {
                        try { method.invoke(it, event) } catch (e: Exception) { e.printStackTrace() }
                    }
                }
            }
        }
    }

    fun registerListener(c: EventListener) {
        if (listeners.contains(c)) throw ConcurrentModificationException("$c is already registered")
        listeners.add(c)
    }

    fun unregisterListener(c: EventListener) {
        if (!listeners.contains(c)) throw NoSuchElementException("$c haven't register listener!")
        listeners.remove(c)
    }

    fun listenerList(): MutableList<EventListener> { return listeners }
}
