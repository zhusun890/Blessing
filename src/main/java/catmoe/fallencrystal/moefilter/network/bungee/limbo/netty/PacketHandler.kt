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

package catmoe.fallencrystal.moefilter.network.bungee.limbo.netty

import catmoe.fallencrystal.moefilter.network.bungee.limbo.LimboHandler
import catmoe.fallencrystal.moefilter.network.bungee.limbo.packet.c2s.PacketClientPositionLook
import catmoe.fallencrystal.moefilter.network.bungee.limbo.packet.c2s.PacketHandshake
import catmoe.fallencrystal.moefilter.network.bungee.limbo.packet.c2s.PacketInitLogin
import catmoe.fallencrystal.moefilter.network.bungee.limbo.packet.c2s.PacketStatusRequest
import catmoe.fallencrystal.moefilter.network.bungee.limbo.packet.common.PacketStatusPing
import catmoe.fallencrystal.moefilter.network.bungee.limbo.packet.s2c.PacketPingResponse
import io.netty.channel.ChannelFutureListener
import java.net.InetSocketAddress
import java.util.concurrent.CompletableFuture

class PacketHandler {

    fun handle(handler: LimboHandler, packet: PacketHandshake) {
        CompletableFuture.runAsync {
            handler.host = InetSocketAddress(packet.host, packet.port)
        }
        handler.updateVersion(packet.version, packet.nextState)
    }

    fun handle(handler: LimboHandler, packet: PacketInitLogin) {
        handler.fireLoginSuccess()
    }

    fun handle(handler: LimboHandler, packet: PacketStatusRequest) {
        handler.sendPacket(PacketPingResponse())
    }

    fun handle(handler: LimboHandler, packet: PacketClientPositionLook) {
        handler.location=packet.readLoc
    }

    fun handle(handler: LimboHandler, packet: PacketStatusPing) {
        if (handler.channel.isActive) {
            handler.channel.writeAndFlush(packet).addListeners(ChannelFutureListener.CLOSE)
        }
    }

}