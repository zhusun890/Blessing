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

package catmoe.fallencrystal.moefilter

import catmoe.fallencrystal.moefilter.api.logger.InitLogger
import catmoe.fallencrystal.moefilter.network.InitChannel
import catmoe.fallencrystal.moefilter.network.bungee.util.WorkingMode
import catmoe.fallencrystal.moefilter.util.message.v2.MessageUtil
import catmoe.fallencrystal.moefilter.util.plugin.AsyncLoader
import com.typesafe.config.ConfigFactory
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class MoeFilter : Plugin() {

    private val initLogger = InitLogger()
    val injectPipelineAfterLoad = AtomicBoolean(false)
    private val fastboot = try { ConfigFactory.parseFile(File(dataFolder, "config.conf")).getBoolean("fastboot") } catch (ex: Exception) { false }

    init {
        instance=this
        if (BungeeCord.getInstance().pluginManager.getPlugin("BungeeKotlinLib") == null)
            throw NoClassDefFoundError("BungeeKotlinLib is not installed! Please install it first.")
    }

    override fun onEnable() {
        if(!fastboot) { load() }
        if (injectPipelineAfterLoad.get()) { InitChannel().initPipeline() }
    }

    override fun onDisable() {
        initLogger.onUnload()
        AsyncLoader.instance.unload()
    }

    private fun load() {
        val loader = AsyncLoader(this)
        initLogger.onLoad()
        loader.load()
    }

    override fun onLoad() {
        if (fastboot) { load() }
        MessageUtil.logInfo("[MoeFilter] Using MoeFilter API")
    }

    companion object {
        lateinit var instance: MoeFilter
            private set
        var mode: WorkingMode? = null
    }
}