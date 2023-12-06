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

package catmoe.fallencrystal.moefilter.common.state

import catmoe.fallencrystal.moefilter.common.counter.ConnectionStatistics
import catmoe.fallencrystal.moefilter.common.firewall.lockdown.LockdownManager
import catmoe.fallencrystal.moefilter.data.BlockType
import catmoe.fallencrystal.moefilter.data.ticking.TickingAttackProfile
import catmoe.fallencrystal.moefilter.data.ticking.TickingProfile
import catmoe.fallencrystal.moefilter.event.AttackStoppedEvent
import catmoe.fallencrystal.moefilter.event.UnderAttackEvent
import catmoe.fallencrystal.moefilter.state.AttackState
import catmoe.fallencrystal.moefilter.util.plugin.util.Scheduler
import catmoe.fallencrystal.translation.event.EventManager
import catmoe.fallencrystal.translation.utils.config.LocalConfig
import catmoe.fallencrystal.translation.utils.system.CPUMonitor
import catmoe.fallencrystal.translation.utils.time.Duration
import net.md_5.bungee.api.scheduler.ScheduledTask
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Suppress("MemberVisibilityCanBePrivate")
object StateManager {

    val inAttack = AtomicBoolean(false)
    var attackMethods: MutableCollection<AttackState> = CopyOnWriteArrayList()
    val duration = Duration()
    var profile: TickingProfile? = null

    val lastMethod: MutableCollection<AttackState> = ArrayList()

    private val scheduler = Scheduler.getDefault()

    val trigger get() = LocalConfig.getAntibot().getInt("attack-mode.incoming")

    @Suppress("EnumValuesSoftDeprecate")
    fun attackMethodAnalyser(cps: Int) {
        if (!inAttack.get()) return
        if (cps < trigger && attackMethods.isNotEmpty()) return
        val methodSize = AttackState.values().size
        if (cps >= methodSize) {
            val method: MutableCollection<AttackState> = ArrayList()
            if (cps > methodSize * 2) {
                BlockType.values().forEach {
                    if ((ConnectionStatistics.sessionBlocked.getIfPresent(it) ?: 0) > cps / methodSize) { method.add(it.state) }
                }
                lastMethod.clear(); lastMethod.addAll(method)
                setAttackMethod(method); return
            }
        }
    }

    private val endedWaiter = AtomicBoolean(false)
    private val endedCount = AtomicInteger(LocalConfig.getAntibot().getInt("attack-mode.un-attacked.wait") + 1)
    private var endedTask: ScheduledTask? = null
    fun attackEndedDetector(cps: Int) {
        val conf = LocalConfig.getAntibot().getConfig("attack-mode.un-attacked")
        if (cps < trigger) {
            if (conf.getBoolean("instant")) fireNotInAttackEvent() else if (!endedWaiter.get() && endedTask == null) {
                endedTask = scheduler.repeatScheduler(1, TimeUnit.SECONDS) {
                    fun cancel() {
                        val task = endedTask
                        if (task != null) { task.cancel(); endedTask=null }
                        endedWaiter.set(false)
                        endedCount.set(conf.getInt("wait") + 1)
                    }
                    if (!inAttack.get()) cancel() else {
                        val cps1 = ConnectionStatistics.getConnectionPerSec()
                        if (cps1 > trigger) { cancel(); return@repeatScheduler }
                        endedCount.set(endedCount.get() - 1)
                        if (endedCount.get() == 0) {
                            cancel()
                            fireNotInAttackEvent()
                        }
                    }
                }
            }
        }
    }

    fun setAttackMethod(method: Collection<AttackState>) {
        if (!inAttack.get()) return
        attackMethods.clear(); attackMethods.addAll(method)
        EventManager.callEvent(UnderAttackEvent(attackMethods))
    }

    fun fireAttackEvent() {
        EventManager.callEvent(UnderAttackEvent(
            if (LockdownManager.state.get()) listOf(AttackState.LOCKDOWN)
            else if (attackMethods.isEmpty()) listOf(AttackState.NOT_HANDLED)
            else attackMethods
        ))
        inAttack.set(true)
        if (inAttack.get()) { duration.start() }
    }

    fun fireNotInAttackEvent() { EventManager.callEvent(AttackStoppedEvent()); attackMethods.clear(); inAttack.set(false); duration.stop() }

    fun tickProfile(): TickingProfile {
        val attackProfile = if (inAttack.get()) {
            TickingAttackProfile(
                ConnectionStatistics.sessionPeakCps,
                ConnectionStatistics.sessionBlocked.asMap().toMutableMap(),
                ConnectionStatistics.sessionTotal,
                ConnectionStatistics.sessionTotalIps
            )
        } else { null }
        val profile = TickingProfile(
            attackProfile,
            ConnectionStatistics.getConnectionPerSec(),
            ConnectionStatistics.peakCps,
            ConnectionStatistics.total,
            ConnectionStatistics.totalIps,
            ConnectionStatistics.blocked.asMap().toMutableMap(),
            CPUMonitor.getRoundedCpuUsage(),
            ConnectionStatistics.getIncoming(),
            ConnectionStatistics.getOutgoing()
        )
        this.profile=profile
        return profile
    }

}