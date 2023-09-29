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

package catmoe.fallencrystal.moefilter.network.limbo.packet.s2c

import catmoe.fallencrystal.moefilter.network.limbo.dimension.DimensionInterface.ADVENTURE
import catmoe.fallencrystal.moefilter.network.limbo.dimension.DimensionInterface.LLBIT
import catmoe.fallencrystal.moefilter.network.limbo.dimension.adventure.DimensionRegistry.codec_1_16
import catmoe.fallencrystal.moefilter.network.limbo.dimension.adventure.DimensionRegistry.codec_1_18_2
import catmoe.fallencrystal.moefilter.network.limbo.dimension.adventure.DimensionRegistry.codec_1_19
import catmoe.fallencrystal.moefilter.network.limbo.dimension.adventure.DimensionRegistry.codec_1_19_1
import catmoe.fallencrystal.moefilter.network.limbo.dimension.adventure.DimensionRegistry.codec_1_20
import catmoe.fallencrystal.moefilter.network.limbo.dimension.adventure.DimensionRegistry.codec_Legacy
import catmoe.fallencrystal.moefilter.network.limbo.dimension.adventure.DimensionRegistry.defaultDimension1_16
import catmoe.fallencrystal.moefilter.network.limbo.dimension.adventure.DimensionRegistry.defaultDimension1_18_2
import catmoe.fallencrystal.moefilter.network.limbo.dimension.llbit.StaticDimension
import catmoe.fallencrystal.moefilter.network.limbo.handler.MoeLimbo
import catmoe.fallencrystal.moefilter.network.limbo.netty.ByteMessage
import catmoe.fallencrystal.moefilter.network.limbo.packet.LimboS2CPacket
import catmoe.fallencrystal.translation.utils.version.Version
import catmoe.fallencrystal.translation.utils.version.Version.*


@Suppress("MemberVisibilityCanBePrivate")
class PacketJoinGame : LimboS2CPacket() {

    var entityId = 0
    var isHardcore = true
    var gameMode = 2
    var previousGameMode = -1
    var worldName: String = when (MoeLimbo.dimLoaderMode) {
        ADVENTURE -> MoeLimbo.dimensionType.adventure.dimensionName
        LLBIT -> MoeLimbo.dimensionType.llbit.dimension.key
    }
    var worldNames: Array<String?> = arrayOf(worldName)
    var hashedSeed: Long = 0
    var maxPlayers = 1
    var viewDistance = 2
    var reducedDebugInfo = true
    var enableRespawnScreen = true
    var isDebug = false
    var isFlat = true

    override fun encode(packet: ByteMessage, version: Version?) {
        when (MoeLimbo.dimLoaderMode) {
            ADVENTURE -> encodeAdventure(packet, version!!)
            LLBIT -> encodeLLBIT(packet, version!!)
        }
    }

    private fun encodeLLBIT(packet: ByteMessage, version: Version) {
        packet.writeInt(entityId)
        val dim = StaticDimension.dim.dimension
        val tag = StaticDimension.cacheDimension.getIfPresent(version)!!
        // Hardcore
        if (version.moreOrEqual(V1_16_2)) packet.writeBoolean(isHardcore)
        // Game mode
        if (version == V1_7_6) packet.writeByte(if (gameMode == 3) 1 /* 1.7 Not supported spectator */ else gameMode)
        else if (version.less(V1_20_2)) packet.writeByte(gameMode)
        if (version.moreOrEqual(V1_16)) {
            if (version.less(V1_20_2)) packet.writeByte(previousGameMode)
            packet.writeStringsArray(worldNames)
            if (version.less(V1_20_2)) {
                packet.writeTag(tag)
                if ((version.fromTo(V1_19, V1_20) || version.fromTo(V1_16, V1_16_1)))
                    packet.writeString(worldName)
                else packet.writeTag(dim.getAttributes(version))
                packet.writeString(worldName)
            }
        }
        if (version.fromTo(V1_7_6, V1_9)) packet.writeByte(dim.dimensionId)
        else if (version.fromTo(V1_9_1, V1_15_2)) packet.writeInt(dim.dimensionId)
        if (version.moreOrEqual(V1_15) && version.less(V1_20_2)) packet.writeLong(hashedSeed)
        if (version.fromTo(V1_7_6, V1_13_2)) packet.writeByte(0) // Difficulty
        if (version.moreOrEqual(V1_16_2)) packet.writeVarInt(maxPlayers) else packet.writeByte(maxPlayers)
        if (version.fromTo(V1_7_6, V1_15_2)) packet.writeString("flat")
        if (version.moreOrEqual(V1_14)) packet.writeVarInt(viewDistance)
        if (version.moreOrEqual(V1_18)) packet.writeVarInt(viewDistance)
        if (version.moreOrEqual(V1_8)) packet.writeBoolean(reducedDebugInfo)
        if (version.moreOrEqual(V1_15)) packet.writeBoolean(enableRespawnScreen)

        if (version.moreOrEqual(V1_20_2)) { // 1.20.2: doLimitedCrafting
            packet.writeBoolean(true) // doLimitedCrafting
            packet.writeString(worldName) // World type
            packet.writeString(worldName)
            packet.writeLong(hashedSeed)
            packet.writeByte(gameMode)
            packet.writeByte(previousGameMode)
        }
        if (version.moreOrEqual(V1_16)) {
            packet.writeBoolean(isDebug)
            packet.writeBoolean(isFlat)
        }
        if (version.moreOrEqual(V1_19)) {
            packet.writeBoolean(false)
            if (version.moreOrEqual(V1_20)) packet.writeVarInt(0)
        }
    }

    private fun encodeAdventure(packet: ByteMessage, version: Version) {
        packet.writeInt(entityId)

        // Hardcore
        if (version.moreOrEqual(V1_16_2)) packet.writeBoolean(isHardcore)

        // Game mode
        if (version == V1_7_6) packet.writeByte(if (gameMode == 3) 1 else gameMode)
        else if (version.less(V1_20_2)) packet.writeByte(gameMode)

        // Previous game mode & world names
        if (version.moreOrEqual(V1_16) && version.less(V1_20_2)) packet.writeByte(previousGameMode)

            /*
            Write world(s) names

            In ByteMessage.kt line 119: (WriteStringsArray)
            writeVarInt(array.size)
            array.forEach { writeString(it) }
             */
        if (version.moreOrEqual(V1_16)) packet.writeStringsArray(worldNames)

        // Dimension
        /*
        if (version.fromTo(V1_7_6, V1_9)) packet.writeByte(defaultDimension1_16.dimensionId)
        else if (version.fromTo(V1_9_1, V1_15_2)) packet.writeInt(defaultDimension1_16.dimensionId)
        else if (version.fromTo(V1_16, V1_16_1)) {
            packet.writeCompoundTag(codec_Legacy)
            packet.writeString(defaultDimension1_16.name)
        } else if (version.fromTo(V1_16_2, V1_18)) {
            packet.writeCompoundTag(codec_1_16)
            packet.writeCompoundTag(defaultDimension1_16.data)
        } else if (version == V1_18_2) {
            packet.writeCompoundTag(codec_1_18_2)
            packet.writeCompoundTag(defaultDimension1_18_2.data)
        } else if (version == V1_19) packet.writeCompoundTag(codec_1_19)
        else if (version.fromTo(V1_19_1, V1_19_3)) packet.writeCompoundTag(codec_1_19_1)
        else if (version == V1_19_4) packet.writeCompoundTag(codec_1_19_4)
        else packet.writeCompoundTag(codec_1_20)
         */
        when {
            version.fromTo(V1_7_6, V1_9) -> packet.writeByte(defaultDimension1_16.dimensionId)
            version.fromTo(V1_9_1, V1_15_2) -> packet.writeInt(defaultDimension1_16.dimensionId)
            version.fromTo(V1_16, V1_16_1) -> {
                packet.writeCompoundTag(codec_Legacy)
                packet.writeString(defaultDimension1_16.name)
            }
            version.fromTo(V1_16_2, V1_18) -> {
                packet.writeCompoundTag(codec_1_16)
                packet.writeCompoundTag(defaultDimension1_16.data)
            }
            version == V1_18_2 -> {
                packet.writeCompoundTag(codec_1_18_2)
                packet.writeCompoundTag(defaultDimension1_18_2.data)
            }
            version == V1_19 -> packet.writeCompoundTag(codec_1_19)
            version.fromTo(V1_19_1, V1_19_3) -> packet.writeCompoundTag(codec_1_19_1)
            version == V1_19_4 ->  packet.writeCompoundTag(codec_1_20)
            else -> {}
        }

        // World name
        if (version.moreOrEqual(V1_16) && version.less(V1_20_2)) {
            if (version.moreOrEqual(V1_19) || version.fromTo(V1_16, V1_16_1)) packet.writeString(worldName) // World type
            packet.writeString(worldName)
        }

        // Hashed seed
        if (version.moreOrEqual(V1_15) && version.less(V1_20_2)) packet.writeLong(hashedSeed)

        // Legacy difficulty & maxPlayers
        if (version.fromTo(V1_7_6, V1_13_2)) packet.writeByte(0) // Difficulty
        if (version.moreOrEqual(V1_16_2)) packet.writeVarInt(maxPlayers) else packet.writeByte(maxPlayers)

        // Legacy level type
        if (version.fromTo(V1_7_6, V1_15_2)) packet.writeString("flat")

        // View distance
        if (version.moreOrEqual(V1_14)) packet.writeVarInt(viewDistance)
        if (version.moreOrEqual(V1_18)) packet.writeVarInt(viewDistance) // Simulation Distance

        // reducedDebugInfo && enableRespawnScreen
        if (version.moreOrEqual(V1_8)) packet.writeBoolean(reducedDebugInfo)
        if (version.moreOrEqual(V1_15)) packet.writeBoolean(enableRespawnScreen)

        if (version.moreOrEqual(V1_20_2)) { // 1.20.2: doLimitedCrafting
            packet.writeBoolean(true) // doLimitedCrafting
            packet.writeString(worldName) // World type
            packet.writeString(worldName)
            packet.writeLong(hashedSeed)
            packet.writeByte(gameMode)
            packet.writeByte(previousGameMode)
        }

        // isDebug && isFlat
        if (version.moreOrEqual(V1_16)) {
            packet.writeBoolean(isDebug)
            packet.writeBoolean(isFlat)
        }
        if (version.moreOrEqual(V1_19)) {
            packet.writeBoolean(false) // lastDeathPos
            if (version.moreOrEqual(V1_20)) packet.writeVarInt(0) // Pearl cooldown
        }
    }

    override fun toString(): String {
        return "PacketJoinGame(" +
                "entityId=$entityId," +
                "isHardcore=$isHardcore," +
                "gameMode=$gameMode," +
                "previousGameMode=$previousGameMode," +
                "worldNames=$worldNames," +
                "worldName=$worldName," +
                "hashedSeed=$hashedSeed," +
                "maxPlayers=$maxPlayers," +
                "viewDistance=$viewDistance," +
                "reducedDebugInfo=$reducedDebugInfo," +
                "enableRespawnScreen=$enableRespawnScreen," +
                "isDebug=$isDebug," +
                "isFlat=$isFlat," +
                "Type=${MoeLimbo.dimensionType.name}," +
                "Dimension=${MoeLimbo.dimensionType.name})"
    }
}