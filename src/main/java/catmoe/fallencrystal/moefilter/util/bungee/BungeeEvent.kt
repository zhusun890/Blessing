package catmoe.fallencrystal.moefilter.util.bungee

import catmoe.fallencrystal.moefilter.api.event.EventManager
import catmoe.fallencrystal.moefilter.api.event.events.bungee.AsyncChatEvent
import catmoe.fallencrystal.moefilter.api.event.events.bungee.AsyncPostLoginEvent
import catmoe.fallencrystal.moefilter.api.event.events.bungee.AsyncServerConnectEvent
import catmoe.fallencrystal.moefilter.api.event.events.bungee.AsyncServerSwitchEvent
import catmoe.fallencrystal.moefilter.util.message.MessageUtil.colorizeMiniMessage
import catmoe.fallencrystal.moefilter.util.message.MessageUtil.sendMessage
import catmoe.fallencrystal.moefilter.util.plugin.FilterPlugin
import catmoe.fallencrystal.moefilter.util.plugin.util.Scheduler
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.*
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

class BungeeEvent : Listener {

    private val proxy = ProxyServer.getInstance()

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: ChatEvent) {
        val player = ProxyServer.getInstance().getPlayer(event.sender.toString())
        if (event.isProxyCommand && event.message.equals("/bungee")) {
            Scheduler(FilterPlugin.getPlugin()!!).runAsync {
                val bungeeMessage = listOf(
                    "<gradient:green:yellow>This server is running <aqua><hover:show_text:'<rainbow>${proxy.name} ${proxy.version}'>${proxy.name}</hover></aqua> & <gradient:#F9A8FF:#97FFFF>MoeFilter ${FilterPlugin.getPlugin()!!.description.version}</gradient> ❤</gradient>",
                    "<gradient:#9BCD9B:#FFE4E1><click:open_url:'https://github.com/CatMoe/MoeFilter/'>CatMoe/MoeFilter</click> @ <click:open_url:'https://www.miaomoe.net/'>miaomoe.net</click></gradient>")
                bungeeMessage.forEach { sendMessage(player, colorizeMiniMessage(it)) };
            }
            event.isCancelled = true
        }
        EventManager.triggerEvent(AsyncChatEvent(
            player,
            event.isProxyCommand,
            (event.isCommand && !event.isProxyCommand),
            event.isCancelled,
            event.message
        )) }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPostLogin(event: PostLoginEvent) { EventManager.triggerEvent(AsyncPostLoginEvent(event.player)) }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onServerConnect(event: ServerConnectEvent){ EventManager.triggerEvent(AsyncServerConnectEvent(event.player, event.target, false, event.isCancelled)) }

    /*
    isCancelled is not available on this event
    so isCancelled is always false.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onServerConnected(event: ServerConnectedEvent) { EventManager.triggerEvent(AsyncServerConnectEvent(event.player, event.server.info, event.server.isConnected, false)) }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onServerSwitch(event: ServerSwitchEvent) { EventManager.triggerEvent(AsyncServerSwitchEvent(event.player, event.from)) }
}