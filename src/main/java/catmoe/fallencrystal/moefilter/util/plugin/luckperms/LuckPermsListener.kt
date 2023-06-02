package catmoe.fallencrystal.moefilter.util.plugin.luckperms

import catmoe.fallencrystal.moefilter.api.event.EventManager
import catmoe.fallencrystal.moefilter.api.user.displaycache.Display
import catmoe.fallencrystal.moefilter.api.user.displaycache.DisplayCache
import catmoe.fallencrystal.moefilter.api.user.displaycache.ReCacheDisplayOnJoin
import catmoe.fallencrystal.moefilter.util.plugin.FilterPlugin
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.user.UserDataRecalculateEvent

object LuckPermsListener {

    fun registerEvent() {
        val luckperms = LuckPermsProvider.get()
        val plugin = FilterPlugin.getPlugin()

        EventManager.registerListener(FilterPlugin.getPlugin()!!, ReCacheDisplayOnJoin())

        // private fun onUserDataRecalculateEvent -> UserDataRecalculateEvent
        luckperms.eventBus.subscribe(plugin!!, UserDataRecalculateEvent::class.java, LuckPermsListener::onUserDataRecalculateEvent)
    }

    private fun onUserDataRecalculateEvent(event: UserDataRecalculateEvent) {
        DisplayCache.updateDisplayCache(event.user.uniqueId, Display(event.user.uniqueId, event.user.cachedData.metaData.prefix ?: "", event.user.cachedData.metaData.suffix ?: ""))
    }
}