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

package net.miaomoe.blessing.fallback.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.miaomoe.blessing.nbt.chat.MixedComponent

@Suppress("MemberVisibilityCanBePrivate")
object ComponentUtil {

    val legacy = LegacyComponentSerializer.legacySection()
    val gson = GsonComponentSerializer.gson()
    val miniMessage = MiniMessage.miniMessage()

    fun Component.toLegacyText() = legacy.serialize(this)

    fun Component.toJsonElement() = gson.serializeToTree(this)

    fun Component.toMixedComponent() = MixedComponent(this)

    fun String.toComponent() = miniMessage.deserialize(this)

}