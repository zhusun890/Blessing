/*
 * Copyright (C) 2023-2023. CatMoe / MoeFilter Contributors
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

package catmoe.fallencrystal.moefilter.util.plugin.luckperms

import catmoe.fallencrystal.moefilter.MoeFilterBungee
import catmoe.fallencrystal.moefilter.api.user.displaycache.Display
import catmoe.fallencrystal.moefilter.api.user.displaycache.DisplayCache
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.user.UserDataRecalculateEvent

object LuckPermsListener {

    fun registerEvent() {
        val luckperms = LuckPermsProvider.get()
        val plugin = MoeFilterBungee.instance

        // private fun onUserDataRecalculateEvent -> UserDataRecalculateEvent
        luckperms.eventBus.subscribe(plugin, UserDataRecalculateEvent::class.java, LuckPermsListener::onUserDataRecalculateEvent)
    }

    private fun onUserDataRecalculateEvent(event: UserDataRecalculateEvent) {
        DisplayCache.updateDisplayCache(event.user.uniqueId, Display(event.user.uniqueId, event.user.cachedData.metaData.prefix ?: "", event.user.cachedData.metaData.suffix ?: ""))
    }
}