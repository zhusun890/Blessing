/*
 * Copyright (C) 2023-2024. CatMoe / Blessing Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.miaomoe.blessing.protocol.packet.status

import net.miaomoe.blessing.protocol.packet.type.MinecraftPacket
import net.miaomoe.blessing.protocol.util.ByteMessage
import net.miaomoe.blessing.protocol.version.Version

@Suppress("MemberVisibilityCanBePrivate")
class PacketStatusPing(var id: Long = 0L) : MinecraftPacket {

    override fun encode(byteBuf: ByteMessage, version: Version) {
        byteBuf.writeLong(id)
    }

    override fun decode(byteBuf: ByteMessage, version: Version) {
        id = byteBuf.readLong()
    }

    override fun toString() = "${this::class.simpleName}(id=$id)"

}