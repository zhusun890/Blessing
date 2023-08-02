/*
 * Copyright 2023. CatMoe / FallenCrystal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package catmoe.fallencrystal.moefilter.network.limbo.check.impl

import catmoe.fallencrystal.moefilter.common.check.info.impl.AddressCheck
import catmoe.fallencrystal.moefilter.common.check.info.impl.Joining
import catmoe.fallencrystal.moefilter.common.check.misc.AlreadyOnlineCheck
import catmoe.fallencrystal.moefilter.common.check.misc.CountryCheck
import catmoe.fallencrystal.moefilter.common.check.misc.DomainCheck
import catmoe.fallencrystal.moefilter.common.check.misc.ProxyCheck
import catmoe.fallencrystal.moefilter.common.check.mixed.MixedCheck
import catmoe.fallencrystal.moefilter.common.check.name.similarity.SimilarityCheck
import catmoe.fallencrystal.moefilter.common.check.name.valid.ValidNameCheck
import catmoe.fallencrystal.moefilter.network.common.kick.DisconnectType
import catmoe.fallencrystal.moefilter.network.common.kick.FastDisconnect
import catmoe.fallencrystal.moefilter.network.common.kick.ServerKickType
import catmoe.fallencrystal.moefilter.network.limbo.check.Checker
import catmoe.fallencrystal.moefilter.network.limbo.check.LimboCheckType
import catmoe.fallencrystal.moefilter.network.limbo.check.LimboChecker
import catmoe.fallencrystal.moefilter.network.limbo.handler.LimboHandler
import catmoe.fallencrystal.moefilter.network.limbo.listener.HandlePacket
import catmoe.fallencrystal.moefilter.network.limbo.listener.ILimboListener
import catmoe.fallencrystal.moefilter.network.limbo.packet.LimboPacket
import catmoe.fallencrystal.moefilter.network.limbo.packet.c2s.PacketHandshake
import catmoe.fallencrystal.moefilter.network.limbo.packet.c2s.PacketInitLogin
import catmoe.fallencrystal.moefilter.network.limbo.packet.common.Disconnect
import catmoe.fallencrystal.moefilter.network.limbo.packet.protocol.Protocol
import catmoe.fallencrystal.moefilter.network.limbo.packet.s2c.PacketJoinGame
import com.github.benmanes.caffeine.cache.Caffeine
import java.net.InetAddress
import java.net.InetSocketAddress

@Checker(LimboCheckType.TRANSLATE_JOIN_CHECK)
@HandlePacket(PacketHandshake::class, PacketInitLogin::class, PacketJoinGame::class, Disconnect::class)
@Suppress("unused")
object CommonJoinCheck : LimboChecker, ILimboListener {

    private val onlineAddress = Caffeine.newBuilder().build<InetAddress, Boolean>()
    private val onlineUser = Caffeine.newBuilder().build<String, Boolean>()

    override fun received(packet: LimboPacket, handler: LimboHandler) {
        val inetSocketAddress = handler.address as InetSocketAddress
        val inetAddress = inetSocketAddress.address
        if (packet is PacketHandshake && packet.nextState == Protocol.LOGIN) {
            val addressCheck = AddressCheck(inetSocketAddress, handler.host)
            // Domain check
            if (DomainCheck.instance.increase(addressCheck)) { kick(handler, DisconnectType.INVALID_HOST); return }
            // Country check
            if (CountryCheck().increase(addressCheck)) { kick(handler, DisconnectType.COUNTRY); return }
            // Proxy check
            if (ProxyCheck().increase(addressCheck)) { kick(handler, DisconnectType.PROXY); return }
            // Limbo online check (address)
            if (onlineAddress.getIfPresent(inetAddress) != null) { kick(handler, DisconnectType.ALREADY_ONLINE); return }
        }
        val protocol = handler.version!!.protocolNumber
        if (packet is PacketInitLogin) {
            val username = packet.username
            val joining = Joining(username, inetAddress, protocol)
            // Invalid name check
            if (ValidNameCheck.instance.increase(joining)) { kick(handler, DisconnectType.INVALID_NAME); return }
            // Ping & Join mixin check
            val mixinKick = MixedCheck.increase(joining)
            if (mixinKick != null) { kick(handler, mixinKick); return }
            // Limbo & Bungee online check (username)
            if (onlineUser.getIfPresent(username.lowercase()) == true || AlreadyOnlineCheck().increase(joining)) {
                kick(handler, DisconnectType.ALREADY_ONLINE); return
            }
            // Similarity name check
            if (SimilarityCheck.instance.increase(joining)) { kick(handler, DisconnectType.INVALID_NAME); return }
        }
        if (packet is PacketJoinGame) {
            onlineAddress.put(inetAddress, true)
            onlineUser.put(handler.profile.username!!.lowercase(), true)
        }
        if (packet is Disconnect) {
            onlineAddress.invalidate(inetAddress)
            onlineUser.invalidate(handler.profile.username!!.lowercase())
        }
    }

    private fun kick(handler: LimboHandler, type: DisconnectType) {
        FastDisconnect.disconnect(handler.channel, type, ServerKickType.MOELIMBO)
    }

    override fun send(packet: LimboPacket, handler: LimboHandler, cancelled: Boolean): Boolean { return cancelled }
}