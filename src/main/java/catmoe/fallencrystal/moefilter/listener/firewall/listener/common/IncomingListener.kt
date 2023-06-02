package catmoe.fallencrystal.moefilter.listener.firewall.listener.common

import catmoe.fallencrystal.moefilter.listener.main.MainListener
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.event.ClientConnectEvent
import net.md_5.bungee.api.event.PlayerHandshakeEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class IncomingListener : Listener {

    private val throttle = BungeeCord.getInstance().connectionThrottle
    private var available = false
    @EventHandler
    fun onIncomingConnect(event: ClientConnectEvent) { event.isCancelled = MainListener.initConnection(event.socketAddress) }

    @EventHandler
    fun onHandshake(event: PlayerHandshakeEvent) { MainListener.onHandshake(event.handshake, event.connection) }

    @EventHandler
    fun onPreLogin(event: PreLoginEvent) { MainListener.onLogin(event) }
}