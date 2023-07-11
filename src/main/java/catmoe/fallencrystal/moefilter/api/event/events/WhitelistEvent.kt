package catmoe.fallencrystal.moefilter.api.event.events

import catmoe.fallencrystal.moefilter.api.event.MoeAsyncEvent
import catmoe.fallencrystal.moefilter.common.whitelist.WhitelistType
import java.net.InetAddress

@Suppress("unused")
class WhitelistEvent(val address: InetAddress, val type: WhitelistType) : MoeAsyncEvent