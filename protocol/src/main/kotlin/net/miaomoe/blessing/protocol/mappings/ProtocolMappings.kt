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

package net.miaomoe.blessing.protocol.mappings

import com.github.benmanes.caffeine.cache.Caffeine
import net.miaomoe.blessing.protocol.packet.type.MinecraftPacket
import net.miaomoe.blessing.protocol.util.LazyInit
import net.miaomoe.blessing.protocol.version.Version
import kotlin.reflect.KClass

class ProtocolMappings {

    private val registry = Caffeine
        .newBuilder()
        .build<Version, PacketRegistry>()

    fun register(mapping: PacketMapping) {
        for ((range, packetId) in mapping.list) {
            for (version in range) {
                val registry = this.registry.getIfPresent(version) ?: PacketRegistry(version)
                registry.register(packetId, mapping)
                this.registry.put(version, registry)
            }
        }
    }

    @Throws(NullPointerException::class)
    private fun getRegistryFromVersion(version: Version) = this.registry.getIfPresent(version)
        ?: throw NullPointerException("Mappings for this version (${version.name}) is null!")

    @Throws(NullPointerException::class)
    fun getPacket(version: Version, id: Int) = id.let(getRegistryFromVersion(version)::getPacket)
    @Throws(NullPointerException::class)
    fun getPacket(version: Version, `class`: KClass<out MinecraftPacket>) = `class`.let(getRegistryFromVersion(version)::getPacket)

    companion object {
        @JvmStatic
        fun create() = LazyInit { ProtocolMappings() }
    }

}