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

package catmoe.fallencrystal.moefilter.network.limbo.packet.c2s

import catmoe.fallencrystal.moefilter.network.limbo.netty.ByteMessage
import catmoe.fallencrystal.moefilter.network.limbo.packet.LimboC2SPacket
import catmoe.fallencrystal.translation.utils.version.Version
import io.netty.channel.Channel
import java.util.*


class PacketClientChat : LimboC2SPacket() {

    /* common chat */
    var message = ""

    /* 1.19+ Chat report? */

    private var timestamp: Long? = null
    private var salt: Long? = null
    private var signature: ByteArray? = null
    private var signedPreview = false
    private var chain: ChatChain? = null
    private var seenMessages: SeenMessage? = null

    override fun decode(packet: ByteMessage, channel: Channel, version: Version?) {
        if (version!!.less(Version.V1_19)) {
            message=packet.readString()
        } else {
            /* I hate chat report, tbh. */
            message=packet.readString()
            timestamp=packet.readLong()
            salt=packet.readLong()
            if (version.moreOrEqual(Version.V1_19_3)) {
                if (packet.readBoolean()) {
                    signature=ByteArray(256)
                    packet.readBytes(signature!!)
                }
            } else {
                signature=packet.readBytesArray()
            }
            if (version.less(Version.V1_19_3)) {
                signedPreview=packet.readBoolean()
                if (version.moreOrEqual(Version.V1_19_1)) {
                    val chain = ChatChain()
                    chain.decode(packet, channel, version)
                    this.chain=chain
                }
            } else {
                val sm = SeenMessage()
                sm.decode(packet, channel, version)
                this.seenMessages=sm
            }
        }
    }
    override fun toString(): String {
        return "PacketClientChat(message=$message)"
    }
}
@Suppress("MemberVisibilityCanBePrivate")
class ChatChain : LimboC2SPacket() {
    val seen: MutableCollection<ChainLink> = ArrayList()
    val received: MutableCollection<ChainLink> = ArrayList()
    override fun decode(packet: ByteMessage, channel: Channel, version: Version?) {
        seen.clear()
        seen.addAll(readLinks(packet))
        if (packet.readBoolean()) {
            received.clear()
            received.addAll(readLinks(packet))
        }
    }

    private fun readLinks(packet: ByteMessage): MutableCollection<ChainLink> {
        val list: MutableCollection<ChainLink> = ArrayList()
        val cnt = packet.readVarInt()
        if (cnt <= 5) throw IllegalArgumentException("Cannot read entries")
        for (i in 0 until cnt) {
            //chain.add(ChainLink(readUUID(buf), readArray(buf)))
            list.add(ChainLink(packet.readUuid(), packet.readBytesArray()))
        }
        return list
    }
}
@Suppress("MemberVisibilityCanBePrivate")
class SeenMessage : LimboC2SPacket() {
    var offset = -1
    var acknowledged: BitSet? = null
    override fun decode(packet: ByteMessage, channel: Channel, version: Version?) {
        offset=packet.readVarInt()
        acknowledged=packet.readFixedBitSet(20)
    }
}
class ChainLink(val sender: UUID, val signature: ByteArray)