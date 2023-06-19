package catmoe.fallencrystal.moefilter.api.proxy

import catmoe.fallencrystal.moefilter.common.config.ObjectConfig
import catmoe.fallencrystal.moefilter.common.utils.proxy.type.ProxyResult
import com.github.benmanes.caffeine.cache.Caffeine
import java.net.InetAddress

object ProxyCache {
    private val cache = Caffeine.newBuilder().build<InetAddress, ProxyResult>()
    private val whitelistedAddress = listOf("/127.0.0.1")

    init { fetchProxy() }

    private fun fetchProxy() { if (ObjectConfig.getProxy().getBoolean("internal.enabled")) { FetchProxy() } }

    fun isProxy(address: InetAddress): Boolean {
        if (whitelistedAddress.contains(address.toString())) { return false }
        return cache.getIfPresent(address) != null
    }

    fun getProxy(address: InetAddress): ProxyResult? { return cache.getIfPresent(address) }

    fun addProxy(proxy: ProxyResult) { cache.put(proxy.ip, proxy) }
}