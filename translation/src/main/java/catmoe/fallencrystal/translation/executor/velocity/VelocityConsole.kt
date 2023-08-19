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

package catmoe.fallencrystal.translation.executor.velocity

import catmoe.fallencrystal.translation.executor.CommandExecutor
import com.velocitypowered.api.command.CommandSource
import net.kyori.adventure.text.Component

@Suppress("MemberVisibilityCanBePrivate")
class VelocityConsole(val orig: CommandSource) : CommandExecutor {

    override fun getName(): String { return "CONSOLE" }

    override fun sendMessage(component: Component) { orig.sendMessage(component) }

    override fun hasPermission(permission: String): Boolean {
        return true // Console has any permission.
    }

}