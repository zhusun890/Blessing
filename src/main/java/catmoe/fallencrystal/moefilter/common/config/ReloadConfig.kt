package catmoe.fallencrystal.moefilter.common.config

import catmoe.fallencrystal.moefilter.api.event.EventListener
import catmoe.fallencrystal.moefilter.api.event.FilterEvent
import catmoe.fallencrystal.moefilter.api.event.events.PluginReloadEvent
import catmoe.fallencrystal.moefilter.api.proxy.ProxyCache
import catmoe.fallencrystal.moefilter.network.bungee.util.ExceptionCatcher
import catmoe.fallencrystal.moefilter.network.bungee.util.kick.FastDisconnect
import catmoe.fallencrystal.moefilter.util.message.MessageUtil
import catmoe.fallencrystal.moefilter.util.message.notification.Notifications
import catmoe.fallencrystal.moefilter.util.plugin.LoadCommand

class ReloadConfig : EventListener {
    @FilterEvent
    fun reloadConfig(event: PluginReloadEvent) {
        val executor = event.executor
        if (executor != null) { LoadConfig.loadConfig(); ObjectConfig.reloadConfig(); ProxyCache.reload(); warnMessage(event) }
        Notifications.reload()
        FastDisconnect.initMessages()
        // Executor is null == Starting plugin.
        if (executor != null) LoadCommand().reload() else LoadCommand().load()
        ExceptionCatcher.reload()
    }

    private fun warnMessage(event: PluginReloadEvent) {
        val sender = event.executor ?: return
        val messageConfig = ObjectConfig.getMessage()
        val message = "${messageConfig.getString("prefix")}${messageConfig.getString("reload-warn")}"
        MessageUtil.sendMessage(sender, MessageUtil.colorizeMiniMessage(message))
    }
}