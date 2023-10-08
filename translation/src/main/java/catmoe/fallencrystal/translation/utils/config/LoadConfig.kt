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

package catmoe.fallencrystal.translation.utils.config

import catmoe.fallencrystal.translation.CPlatform
import com.typesafe.config.Config
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Level

@Suppress("SpellCheckingInspection")
class LoadConfig {
    private val path = CPlatform.instance.getPluginFolder().absolutePath

    val configFile = File(path, "config.conf")
    val messageFile = File(path, "message.conf")
    val proxyFile = File(path, "proxy.conf")
    val antibotFile = File(path, "antibot.conf")
    val limboFile = File(path, "limbo.conf")

    private val version = CPlatform.instance.pluginVersion()

    init { instance =this }

    private val defaultConfig = """
                version="$version"
                # 启用调试? 启用后可以获得更多的用法用于调试
                # 不要在生产环境中使用这个! 它可能会导致发送大量垃圾邮件降低性能或泄露你的服务器的关键信息.
                debug=false
                
                # f3服务端标识. 在较高版本的客户端上按F3即可看到.
                # gradient和其它标签e.x <newline> 不可在此处使用
                # 占位符: %bungee%: BungeeCord名称, %version%: BungeeCord版本, %backend%: 后端服务器名称
                f3-brand {
                    enabled=true
                    custom="<light_purple>MoeFilter <aqua><- <green>%backend%"
                }
                
                # 客户端标签检查
                client-brand {
                    # 工作模式
                    # WHITELIST = 仅允许指定客户端加入
                    # BLACKLIST = 不允许指定客户端加入
                    mode=BLACKLIST
                    # 匹配模式
                    # CONTAINS: 如果brand包含指定在列表中的信息 则匹配
                    # EQUAL: 如果brand匹配列表中列出的其中一个的信息 则匹配
                    # IGNORE_CASE: 跟EQUAL无异 但不区分大小写
                    # (推荐) REGEX: 使用正则表达式进行匹配
                    equal-mode=CONTAINS
                    # 列表
                    # 小提示: 尽可能地使用1个规则避免开销, 如果有多个需要匹配的规则 则推荐使用正则.
                    list=[""]
                }

                # 域名检查: 仅允许指定的域连接到服务器
                domain-check {
                    # 是否限制域名
                    # 开: 基本检查 + 指定域名限制
                    # 关: 仅开启域名有效检查
                    enabled=false
                    # 允许的列表
                    allow-lists=["127.0.0.1", "localhost", "mc.miaomoe.net", "catmoe.realms.moe"]
                }
                
                # Ping选项
                ping {
                    # 缓存选项 该选项仅当antibot.conf中的mode为PIPELINE时有效
                    cache {
                        # 缓存有效时间. 单位为秒
                        max-life-time=5
                        # 是否为独立的域创建MOTD缓存?
                        stable-domain-cache=true
                        # 在攻击时仅使用缓存而不是呼叫ProxyPingEvent
                        full-cache-during-attack=true
                        # 五分钟内只发送一次avatar
                        send-icon-once=true
                        # 在攻击时不发送图标信息. 覆盖send-icon-once选项.
                        cancel-send-icon-during-attack=true
                    }
                    # 服务端标识选项 通常会在客户端版本不支持时显示
                    # 仅支持经典MC16色. 为空 = 不更改
                    brand="<red>Requires MC 1.8 - 1.20"
                    # 无论协议是否支持都向客户端发送不支持的协议? 这么做会导致brand总是显示在motd中.
                    # 如果motd缓存可用 此行为也会影响到缓存返回的内容 并不需要因为版本更改而刷新motd
                    protocol-always-unsupported=false
                    # 欺骗FML客户端服务器属于哪些类型的服务器
                    # VANILLA, FML, BUKKIT, UNKNOWN
                    type=VANILLA
                }
                
                # 每秒连接数限制. 当您正在使用PIPELINE模式时非常有用.
                # EVENT模式此throttle关闭连接的效率比BungeeCord自带的低
                throttle-limit=3
                
                # HAProxy配置, 使用前请打开代理配置文件中的proxy_protocol
                # 实验性功能: 仅用于调试 除非另有说明 否则请使用EVENT模式(对于反机器人而言)
                ha-proxy {
                    # 启用支持
                    enable=false
                    # 如果通过以下包含的IP连接 则使用HAProxy
                    #
                    # 请注意: 目前技术上暂不可能区分使用和不使用HAProxy的客户端
                    # 一旦启用 就无法正确接受来自非HAProxy的连接.
                    # CIDR将在后续添加支持
                    list=["127.0.0.1"]
                }
                
                # 提醒
                notifications {
                    # discord webhook.
                    # 此选项将会通过proxy.conf中的代理设置发送webhook 而不是直连
                    # 注意! ——如果有人要求您的配置文件 请将此段删除或将url留空 避免遭到webhook垃圾邮件滥用.
                    webhook {
                        # 当在使用/moefilter webhook (debug命令 首先打开debug模式) 时发送的webhook 
                        test {
                            # false = 不发送
                            enabled=false
                            # discord webhook url
                            # 将鼠标悬浮在频道上 然后点击 "编辑频道",
                            # 整合 -> 创建 Webhook -> 创建 Webhook -> 点击 "Captain Hook" -> 
                            # 设置头像和频道 (可选) -> 复制 Webhook URL 然后粘贴在此处
                            url=""
                            # webhook消息的名称 (不遵循discord中的webhook名称设置)
                            username="MoeFilter Webhook"
                            # @ 设置 - 设置此选项之后将会在webhook发送时@某个人/组
                            # 禁用 = 留空
                            # ping用户 = 直接键入用户id
                            # ping组 (role) = &组id (保留& 就像&1234567890这样)
                            # 如何获取id? 设置 -> 高级设置 -> 开发者模式 = 开
                            # 对于用户: 右键用户 点击 "复制用户ID"
                            # 对于role: 左键拥有需要@的role的用户 右键role 然后点击 "复制身份组ID" 
                            ping=""
                            # Webhook标题
                            title="MoeFilter Test Webhook"
                            # 内容
                            format=[
                                "> This is a webhook for test!"
                            ]
                            # 嵌入消息设置
                            embed {
                                # 是否嵌入消息?
                                enabled=true
                                # 嵌入边框颜色设置
                                color {
                                    r=255
                                    g=255
                                    b=180
                                }
                            }
                        }
                    }
                }
            """.trimIndent()

    private val defaultMessage = """
                version="$version"
                # API v6已经切换到MiniMessage.
                prefix="<gradient:#F9A8FF:#97FFFF>MoeFilter</gradient> <gray>>> "
                reload-warn="<green>已重新加载配置文件 部分内容可能需要重启代理生效."
                
                statistics {
                    # 可用占位符描述已被移除 如果您想知道各个占位符有什么用 请看chat-format中使用的占位符..
                    actionbar-format {
                            idle="%prefix% <aqua>CPU <gray>proc. <white>%process_cpu%% <gray>sys. <white>%system_cpu%%   <aqua>CPS <white>%cps%  <aqua>Total <white>%total%  <aqua>Blocked <white>%blocked%"
                            attack="%prefix% <aqua>Type %type%  <aqua>CPS <white>%cps%  <aqua>Peak <white>%peak_cps_session%  <aqua>Total <white>%total_session%  <aqua>Blocked <white>%blocked_session%  <aqua>CPU <white>%process_cpu%%  <aqua>Duration <white>%duration%"
                    }
                    chat-format {
                        idle=[
                            "",
                            " <aqua>是否正在遭到攻击: <green>否",
                            " <aqua>CPU使用率: <white>%process_cpu%% <gray>/ <white>%system_cpu%% <gray>(进程, 系统)",
                            " <aqua>共计连接过服务器的IP数: <white>%total_ips%",
                            " <aqua>已阻止的连接数: <white>%blocked%",
                            " <aqua>接受连接次数: <white>%total%",
                            " <aqua>每秒接受连接数纪录: <white>%peak_cps%",
                            ""
                        ]
                        attack=[
                            "",
                            " <aqua>是否正在遭到攻击: <red>是",
                            " <aqua>防御模式: %type%",
                            " <aqua>CPU使用率: <white>%process_cpu%% <gray>/ <white>%system_cpu%% <gray>(进程, 系统)",
                            "",
                            "<yellow>在攻击发生期间发生了什么?:",
                            " <aqua>此攻击持续了 <white>%duration%  <aqua>每秒连接数最高纪录为 <white>%peak_cps_session% <aqua>!",
                            " <aqua>一共传入了 <white>%total_session% <aqua>个连接, 但已经阻止了 <white>%blocked_session% <aqua>个传入的连接",
                            " <aqua>从攻击开始到现在一共有 <white>%total_ips_session% <aqua>个地址尝试连接到服务器",
                            ""
                        ]
                    }
                    types {
                        firewall="<yellow>Firewall</yellow>"
                        join="<yellow>Join</yellow>"
                        ping="<yellow>Ping</yellow>"
                        not-handled="<yellow>NotHandled</yellow>"
                        lockdown="<yellow>Lockdown</yellow>"
                        
                        null="<white>Unknown</white>"
                        join-to-line="<white>, </white>"
                    }
                    # cps = connection per second (每秒连接数)
                    # 如果您想让cps始终为白色, 将其预设删除或注释掉即可
                    replaced-cps {
                        # 格式:
                        # 连接数="你想要的cps占位符", [value]为值
                        # 例如当cps等于(或大于)100时 且设置了 100="<green>[value]</green>" 
                        # 就会显示绿色的100 (请以实际情况为准), 同时建议闭合颜色, 以防干扰到使用此占位符的消息.
                        
                        # 很明显 这些占位符所挑选的颜色十分简陋: 我想我不会专门找合适的hex渐变颜色 :P
                        0="<white>[value]</white>"
                        100="<green>[value]</green>"
                        1000="<gradient:green:yellow>[value]</gradient>"
                        2800="<yellow>[value]</yellow>"
                        3800="<gradient:yellow:gold>[value]</gradient>"
                        4500="<gold>[value]</gold>"
                        6000="<gradient:gold:red>[value]</gradient>"
                        8000="<red>[value]</red>"
                        10000="<gradient:red:dark_red>[value]</gradient>"
                        12000="<dark_red>[value]</dark_red>"
                    }
                    # 更新频率 (tick为单位)
                    actionbar-update-delay=1
                    command {
                        actionbar {
                            description="在actionbar查看MoeFilter状态."
                            enable="<hover:show_text:'<red>点击来切换actionbar'><click:run_command:/moefilter actionbar><green>已切换actionbar</click>"
                            disable="<hover:show_text:'<green>点击来切换actionbar'><click:run_command:/moefilter actionbar><red>已切换actionbar</click>"
                        }
                        chat-description="查看MoeFilter的状态"
                    }
                }
                
                # 锁定服务器命令
                lockdown {
                    enabled="<green>启用"
                    disabled="<red>禁用"
                    description="锁定您的服务器"
                    enable-first="<red>要想添加或移除白名单 请先打开锁定模式"
                    toggle {
                        enable="<red>已开启锁定模式"
                        disable="<green>已关闭锁定模式"
                    }
                    parse-error="<red>请键入一个有效的地址"
                    already-added="<red>此地址已在允许连接的列表中了!"
                    not-found="<red>允许连接的列表中没有该地址!"
                    add="<green>已将此地址加入允许连接的列表中"
                    remove="<green>已将此地址从允许连接的列表中移除"
                    state-message=[
                        "",
                        " <aqua>Lockdown状态 <white>: [state]<white>",
                        "",
                        " lockdown toggle <aqua>-<white> 开启/关闭锁定模式",
                        " lockdown add <Address> <aqua>-<white> 允许指定地址可以在锁定状态下加入",
                        " lockdown remove <Address> <aqua>-<white> 禁止指定地址可以在锁定状态下加入",
                        ""
                    ]
                }
                
                # 清空防火墙/黑名单地址命令
                drop-command {
                    description="丢弃已被列入防火墙/黑名单的地址"
                    dropped-all="<green>所有防火墙/黑名单中的地址已被清空 他们可以再次连接到服务器."
                    dropped="<green>此地址已成功从防火墙中移除."
                    invalid-address="<red>无效的地址, 请检查您输入的地址是否有误, 然后再试."
                    not-found="<red>此地址未被列入防火墙"
                }
                
                
                # [permission] = 当前命令权限占位符
                command {
                    not-found="<red>未找到命令."
                    no-permission="<red>缺少权限: [permission]"
                    only-player="<red>该命令仅可以由在线玩家执行."
                    # 各类子命令描述
                    description {
                        reload="快速重载MoeFilter配置文件(不推荐)"
                        help="列出所有已注册的命令 并针对指定命令提供帮助"
                    }
                    # 命令补全描述. 颜色符号无法在此处使用.
                    tabComplete {
                        # 当玩家没有权限使用MoeFilter的命令时 应该返回什么tab补全
                        no-permission="You don't have permission to use this command."
                        # 当玩家没有权限使用MoeFilter的命令但补全了它时 应该返回什么tab补全. 
                        no-subcommand-permission="You need permission [permission] to use this command."
                    }
                    # 当玩家没有权限使用那个子命令时 是否完全隐藏命令 (启用后忽略tabComplete的"no-subcommand-permission"和"no-permission")
                    full-hide-command=false
                }
                
                # 踢出消息设置
                # 注意: 当您使用渐变消息时 踢出系统并不负责旧版本的颜色格式匹配兼容
                # 这意味着如果您的服务器有<1.16的玩家 在他们的视角内可能会看着怪怪的 (ex. 很抽象的颜色且随机)
                kick {
                    placeholders {
                        custom1="这个是属于你的占位符! 在消息中使用[custom1]来使用它."
                        custom2="你可以增加无限行占位符 来使你的消息模板化更加轻松!"
                        server-ip="mc.miaomoe.net"
                    }
                    # 在此处指定占位符格式. %[placeholder]% 就是为 %placeholder% 例如%custom1%就会返回上面的值
                    placeholder-pattern="%[placeholder]%"
                    already-online=[
                        "",
                        "<red>You are already connected this server!",
                        "<red>Contact server administrator for more information.",
                        ""
                    ]
                    rejoin=[
                        "",
                        "<green>You are successfully passed first-join check",
                        "<white>Please rejoin server to join.",
                        ""
                    ]
                    ping=[
                        "",
                        "<yellow>Please ping server first.",
                        ""
                    ]
                    invalid-name=[
                        "",
                        "<red>Your name is invalid or not allowed on this server.",
                        ""
                    ]
                    invalid-host=[
                        "",
                        "<red>Please join server from %server-ip%",
                        ""
                    ]
                    country=[
                        "",
                        "<red>Your country is not allowed on this server.",
                        ""
                    ]
                    proxy=[
                        "",
                        "<red>This server is not allowed proxy/vpn.",
                        ""
                    ]
                    # 仅限Limbo
                    unexpected-ping=[
                        "",
                        "<red>Your ping looks a little strange. Please try again later.",
                        ""
                    ]
                    detected=[
                        "",
                        "<red>You act like a robot.",
                        "<red>If not. Please try again.",
                        ""
                    ]
                    passed-check=[
                        "",
                        "<green>You have passed all the checks",
                        "<white>Rejoin the server to enjoy game!",
                        ""
                    ]
                    recheck=[
                        "",
                        "<red>There is an exception in your session",
                        "<red>Please complete the certification again."
                        ""
                    ]
                    brand-not-allowed=[
                        ""
                        "<red>Your client is not allowed on this server.",
                        ""
                    ]
                    # 仅限Limbo
                    cannot-chat=[
                        "",
                        "<red>You can't speak until you passed the bot detection.",
                        ""
                    ]
                    unsupported-version=[
                        "",
                        "<red>Your version of the Minecraft client is not supported",
                        "<red>The currently supported version is 1.8-1.20.1",
                        ""
                    ]
                }
            """.trimIndent()

    private val defaultAntiBot = """
                version="$version"
                
                # 反机器人工作模式.
                # PIPELINE(推荐): 接管BungeeCord管道 实现无效数据包检查和EVENT模式拥有的所有检查
                # 并且比EVENT更快. 但无法保证所有东西都兼容. 有关兼容的BungeeCord 请移步:
                # https://github.com/CatMoe/MoeFilter#%E5%AE%8C%E5%85%A8%E5%85%BC%E5%AE%B9%E7%9A%84bungeecord
                # EVENT: 使用传统事件组合的反机器人. 可以实现一些普通的检查 例如PingJoin, FirstJoin, etc
                # 理论上兼容所有BungeeCord分叉. 如果您在使用PIPELINE时出现了一些问题 可以选择使用该模式.
                # DISABLED: 什么也不做. (作为纯实用工具使用)
                mode=PIPELINE
                
                # 如果您在使用MoeFilter的InitialHandler出现了一些问题 请使用这个
                # 注意: 此项仅在PIPELINE模式下生效
                # 建议启用Limbo来提供足够强大的防护 因为开启此项会导致BungeeCord禁用部分检查
                #
                # 补充: nah, 考虑到Limbo默认是开启状态, 有越来越多的插件使用反射来做许多东西.. 我想我不会考虑损坏插件兼容性.
                use-original-handler=true
                
                # 选择事件呼叫时机 该选项仅当mode为PIPELINE时有效.
                # AFTER_INIT: 当连接传入时立马呼叫事件 无论它们是否被阻止 (不推荐)
                # NON_FIREWALL: 当连接没被防火墙切断时呼叫事件
                # READY_DECODING: 在解码器解码前呼叫事件 (推荐)
                # AFTER_DECODER: 当解码器完成解码后呼叫事件 (不推荐, 但如果您正在使用反向代理(e.x HAProxy) 请使用此模式或DISABLED)
                # DISABLED: 不呼叫事件以保留性能(推荐 但如果遇到问题 请将其设为以上模式的其中一种.)
                event-call-mode=DISABLED
                
                # 攻击模式激活设置
                attack-mode { 
                    # 当一秒内的连接超过此设定的值 将激活攻击模式
                    incoming=5
                    # 模式激活设置
                    mode {
                        # 当阻止总数在1秒内达到此值 将会将状态设置为被此模式攻击
                        count=4
                    }
                    un-attacked {
                        # 当1秒内没有任何连接传入时 立即解除攻击模式
                        instant=true
                        # 如果instant模式为false且 x 秒内没有传入连接 则解除戒备模式
                        wait=5
                    }
                }
                
                # 当服务器遭受攻击时 对于新连接的超时时间应该为多少?
                # -1则为BungeeCord的默认值.
                # 修改此项需要重启服务器.
                dynamic-timeout=1000
                
                mixed-check {
                    # RECONNECT: 仅重新连接
                    # JOIN_AFTER_PING: Ping后加入
                    # RECONNECT_AFTER_PING: Ping后重新连接
                    # PING_AFTER_RECONNECT: 重新连接后Ping
                    # STABLE: 独立模块互相工作
                    # DISABLED: 禁用
                    default=DISABLED
                    in-attack=PING_AFTER_RECONNECT
                    
                    # join+ping的检查的缓存过期时间 (秒)
                    # 如果您或您的玩家遇到连续要求的问题
                    # 请尝试将值调高直到适合您或您的玩家通过检查
                    # 但也不要填写一个较大的值.
                    max-cache-time=10
                    
                    # 自动黑名单
                    blacklist {
                        # 如果此IP在经过此检查时被认为嫌疑的次数
                        # 等于或大于此数字 将会被临时列入黑名单
                        suspicion=4
                        # 仅在攻击下将其列入黑名单 
                        # 如果否 无论是否正在遭到攻击 都会立即列入黑名单
                        only-in-attack=false
                        # 每秒衰减嫌疑间隔. 
                        # 在嫌疑数增加之后如果此时间内未增加嫌疑
                        # 则嫌疑-1
                        decay=4
                    }
                }
                
                name-check {
                    # 名称有效性检查
                    valid-check {
                        # 有效名称正则. 默认正则的规则. 如需禁用检查 将其设为 "" 即可.
                        # 即名称不能包含mcstorm, mcdown或bot字样. 名称只能含有数字 字母以及下划线 且长度限制在3-16
                        valid-regex="^(?i)(?!.*(?:mcstorm|mcdown|bot|cuute|doshacker))([A-Za-z0-9_]{3,16})${'$'}"
                        # 自动防火墙配置 (此选项将会将触发此检查的玩家列入临时黑名单)
                        # THROTTLE: 当触发此检查时如果刚好触发连接限制 则黑名单
                        # ALWAYS: 始终将触发此检查的IP列入黑名单
                        # ATTACK: 当在遭到攻击时始终将其列入黑名单
                        # DISABLED: 仅踢出
                        firewall-mode=ATTACK
                    }
                    
                    # 名称相似性检查
                    # 该检查的踢出理由仍然将是 invalid-name
                    similarity {
                        # 是否启用?
                        enable=false
                        # 用户名的采样的最大个数
                        max-list=10
                        # 名称有效时间 如果无效 则丢弃. (秒)
                        # 避免玩家多次加入导致封禁玩家
                        valid-time=4
                        # 当字符串相似度达到该值 则不允许他们加入服务器
                        length=4
                    }
                }
                
                # 防火墙缓解
                firewall {
                    # INTERNAL: 内置L7层缓解
                    # SYSTEM: iptables&ipset L4层缓解
                    # INTERNAL_AND_SYSTEM: 两者结合
                    # SYSTEM需要在Linux系统上使用,运行的实例必须有root权限 且已安装iptables&ipset
                    mode=INTERNAL
                    # 临时封禁有效时间 单位为秒. 需重启服务器生效
                    temp-expire-time=30
                }
                
                # 提醒
                notifications {
                    # 关于webhook的注意事项, 配置以及使用办法可以在config.conf的notifications.webhook.test中找到
                    webhook {
                        attack-start {
                            enabled=false
                            url=""
                            username="MoeFilter Webhook"
                            ping=""
                            title="MoeFilter"
                            format=[
                                "> :warning: The server is under attack!"
                            ]
                            embed {
                                enabled=true
                                color {
                                    r=255
                                    g=255
                                    b=0
                                }
                            }
                        }
                        attack-stopped {
                            enabled=false
                            url=""
                            username="MoeFilter Webhook"
                            ping=""
                            title="MoeFilter"
                            format=[
                                "> The attack seems is stopped."
                            ]
                            embed {
                                enabled=true
                                color {
                                    r=255
                                    g=255
                                    b=0
                                }
                            }
                        }
                    }
                    # 标题设置
                    title {
                        attack-start {
                            enabled=true
                            title="<gradient:#F9A8FF:#97FFFF>MoeFilter</gradient>"
                            sub-title="<yellow>A bot attack is incoming.."
                            fade-in=0
                            stay=60
                            fade-out=10
                        }
                        attack-stopped {
                            enabled=false
                            title=""
                            sub-title=""
                            fade-in=0
                            stay=0
                            fade-out=0
                        }
                    }
                }
    """.trimIndent()

    private val defaultProxy = """
                version="$version"
        
                # 内置的反代理. (自动从网站上抓取代理并将其放置在缓存中)
                internal {
                    enabled=true
                    # 启用调试 (大量垃圾邮件警告!)
                    debug=false
                    # 调度器任务设置
                    schedule {
                        # schedule总是会在启动时触发. 所以你不需要担心启动时不同步.
                        # 单位为小时 意味着每三小时更新一次.
                        update-delay=3
                    }
                    lists = [
                        "https://api.proxyscrape.com/?request=getproxies&proxytype=http&timeout=10000&country=all&ssl=all&anonymity=all",
                        "https://www.proxy-list.download/api/v1/get?type=http",
                        "https://www.proxy-list.download/api/v1/get?type=https",
                        "https://www.proxy-list.download/api/v1/get?type=socks4",
                        "https://www.proxy-list.download/api/v1/get?type=socks5",
                        "https://shieldcommunity.net/sockets.txt",
                        "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/http.txt",
                        "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks4.txt",
                        "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks5.txt",
                        "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/http.txt",
                        "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/https.txt",
                        "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks4.txt",
                        "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks5.txt",
                        "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/http.txt",
                        "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks4.txt",
                        "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks5.txt",
                        "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-http.txt",
                        "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-https.txt",
                        "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks4.txt",
                        "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks5.txt",
                        "https://raw.githubusercontent.com/rdavydov/proxy-list/main/proxies/http.txt",
                        "https://raw.githubusercontent.com/rdavydov/proxy-list/main/proxies/socks4.txt",
                        "https://raw.githubusercontent.com/rdavydov/proxy-list/main/proxies/socks5.txt",
                        "https://raw.githubusercontent.com/clarketm/proxy-list/master/proxy-list-raw.txt",
                        "https://raw.githubusercontent.com/mertguvencli/http-proxy-list/main/proxy-list/data.txt",
                        "https://raw.githubusercontent.com/scriptzteam/ProtonVPN-VPN-IPs/main/exit_ips.txt",
                        "https://raw.githubusercontent.com/scriptzteam/ProtonVPN-VPN-IPs/main/entry_ips.txt",
                        "https://raw.githubusercontent.com/mmpx12/proxy-list/master/http.txt",
                        "https://raw.githubusercontent.com/mmpx12/proxy-list/master/https.txt",
                        "https://raw.githubusercontent.com/mmpx12/proxy-list/master/socks4.txt",
                        "https://raw.githubusercontent.com/mmpx12/proxy-list/master/socks5.txt",
                        "https://raw.githubusercontent.com/FoXEY747/NettyBotter/main/lists/all_proxies.txt",
                        "https://check.torproject.org/torbulkexitlist?ip=1.1.1.1",
                        "https://cinsscore.com/list/ci-badguys.txt",
                        "https://lists.blocklist.de/lists/all.txt",
                        "https://blocklist.greensnow.co/greensnow.txt",
                        "https://reputation.alienvault.com/reputation.generic",
                        "https://gist.githubusercontent.com/BBcan177/bf29d47ea04391cb3eb0/raw",
                        "https://gist.githubusercontent.com/BBcan177/d7105c242f17f4498f81/raw",
                        "http://lists.blocklist.de/lists/all.txt"
                    ]
                }
                
                # 设置来自https://proxycheck.io的服务
                # 修改此选项需要重启服务器.
                proxycheck-io {
                    enable=false
                    # API秘钥 您需要在上面注册一个账户才可以使用该服务.
                    key="your-key-here"
                    # 单日可查询次数限制. 这取决于你的计划. 但由于我们并不真正保存使用次数 
                    # 而是作为插件在检查时的请求次数-1 以避免无限制调用接口.
                    limit=1000
                    # 检查并踢出vpn 但可能会导致消耗两次查询机会
                    check-vpn=false
                    # 每分钟请求限制.
                    throttle=350
                    # 如果您因为proxies-config中配置的代理而被proxycheck禁止访问 请将其设置为true
                    direct-response=false
                }
                
                # 是否使用来自ip-api提供的检测代理服务
                # 修改此选项需要重启服务器.
                ip-api {
                    enable=true
                }
                
                # GeoIP设置 @MaxMind Database
                country {
                    # MaxMind Database下载许可key. 建议使用自己的许可证秘钥.
                    # https://dev.maxmind.com/geoip/geolite2-free-geolocation-data#accessing-geolite2-free-geolocation-data
                    key="LARAgQo3Fw7W9ZMS"
                    # 模式. WHITELIST(白名单), BLACKLIST(黑名单) 或 DISABLED(禁用)
                    mode=DISABLED
                    # 下载 & 查询超时时间 (ms)
                    time-out=5000
                    # https://dev.maxmind.com/geoip/legacy/codes/iso3166/
                    list=["CN"]
                }
                
                # 代理上网配置. 适用于所有API和内置反代理 爬取
                # 暂不支持非HTTP/HTTPS以及SOCKS5之外的代理.
                proxies-config {
                    # 工作模式. HTTP: HTTP代理, SOCKS: socks5代理, DIRECT: 直连
                    mode=DIRECT
                    host="localhost"
                    port=8080
                }
                
    """.trimIndent()

    private val defaultLimbo = """
                # 这些设置皆在调整MoeLimbo
                # 其中的设置大部分不可reload 请提前在投入生产环境中测试
                version=$version
                
                # 是否使用Limbo? 仅在使用PIPELINE模式下有效.
                enabled=true
                
                # 是否只在攻击状态下将连接转发到limbo
                only-connect-during-attack=false
                
                # 如果您在Ping时出现了一点小错误 请尝试启用此项
                instant-close-after-ping=false
                
                # 如何调整dim-loader(NBT引擎)?
                # NETHER对于更高版本的Minecraft不可用, 将会在之后尽快修复.
                # OVERWORLD 使用 LLBIT 最为合适
                # THE_END 使用 ADVENTURE 最为合适
                # 
                # 虽然我可能犯了与NBT引擎无关的错误, 
                # 但在我完全弄清楚群戏数据之前 这么做应该不会有什么问题..
                
                # 加载群戏所使用的NBT引擎.
                # 可用引擎: ADVENTURE, LLBIT
                # 如果您不知道这是什么或这么做会导致什么后果, 应该保持默认值!
                # 追求稳定的环境下应使用推荐值.
                dim-loader=LLBIT
                
                # 维度设置:
                # OVERWORLD, NETHER, THE_END
                dimension=OVERWORLD
                
                # 玩家在通过Limbo检查后 需要在多少秒内重新连接以加入服务器?
                bungee-queue=15
                # 总是检查玩家 如果为false 则无视bungee-queue直接加入服务器
                always-check=true
                
                # KeepAlive设置
                keep-alive {
                    # 多少间隔发送一次 单位为秒
                    delay=10
                    # 等待回应超时时间(单位为毫秒)
                    max-response=1000
                }
                
                # 是否禁用聊天, 如果开启 当玩家尝试聊天时 它们会被踢出服务器
                # 此选项将检测以下行为:
                # 当玩家连接到BungeeCord时, 还未加入到任意后端服务器就开始尝试发送聊天消息
                # 在Limbo里尝试发送聊天内容
                #
                # 此检查可能会误伤到一些玩家, 如果您在考虑这个 
                # ——别担心 一般而言可以放心禁用 其它机器人检查同样强大
                # 大部分愚蠢的SpamBot通常不会拥有强大的绕过.
                disable-chat=true
                
                check {
                    # 掉落检查
                    falling {
                        # 每tick运动的最大速度(方块)
                        # 如果您不知道调整后有什么后果 应保持默认值.
                        max-x=2.88
                        max-y=3.9200038147009764
                        max-z=2.88
                        # 当玩家的Pitch为 > 90.0 或 < -90.0(不可能)时 踢出Limbo
                        wrong-turn=true
                        # 如果玩家试图向上移动 则踢出Limbo
                        invalid-y-up=true
                        # 如果玩家的onGround为true 则踢出Limbo
                        invalid-on-ground=true
                        # 如果玩家尝试移动两次但Y轴却没有改变 则踢出Limbo
                        same-y-position=true
                        # 超时时间 单位为秒
                        timeout=5
                        
                        # 持续掉落检查测量模式:
                        # DISTANCE: 根据掉落的距离测量
                        # COUNT: 根据玩家(发送数据包)的移动次数测量
                        calculate-type=DISTANCE
                        # 测量的距离 如果达到此值 将通过检查
                        range=100
                    }
                    # 变速检查 (依靠监听移动数据包)
                    timer {
                        # 违规点数衰减间隔(秒)
                        decay=4
                        # 违规点数高于或等于多少会被踢出
                        kick-vl=5
                        # 第一次移动与第二次移动的间隔要高于多少(毫秒)才应被认作合法行为
                        allowed-delay=50
                        # 如果玩家通过检查的速度比此规定的时间快 则他们将被踢出服务器(秒)
                        min=3
                        # 如果玩家通过检查的速度比此规定的时间慢 则他们将会被踢出服务器(秒)
                        max=14
                    }
                }
    """.trimIndent()

    fun loadConfig() {
        createDefaultConfig()
        val config = LocalConfig.getConfig()
        val message = LocalConfig.getMessage()
        val proxy = LocalConfig.getProxy()
        val antibot = LocalConfig.getAntibot()
        val limbo = LocalConfig.getLimbo()
        if (config.getString("version") != version || config.isEmpty) { updateConfig("config", config) }
        if (message.getString("version") != version || message.isEmpty) { updateConfig("message", message) }
        if (proxy.getString("version") != version || proxy.isEmpty) { updateConfig("proxy", proxy) }
        if (antibot.getString("version") != version || antibot.isEmpty) { updateConfig("antibot", antibot) }
        if (limbo.getString("version") != version || limbo.isEmpty) { updateConfig("limbo", limbo) }
        LocalConfig.reloadConfig()
    }

    private fun updateConfig(file: String, config: Config) {
        val version = config.getString("version")
        val nowTime = System.currentTimeMillis()
        val oldFile = Paths.get("$path/$file.conf")
        val newFile = Paths.get("$path/$file-old-$version-$nowTime.conf")
        Files.move(oldFile, newFile)
        createDefaultConfig()
        broadcastUpdate(newFile)
    }

    private fun createDefaultConfig() {
        val pluginFolder = CPlatform.instance.getPluginFolder()
        val defaultConfigMap = mapOf(
            configFile to defaultConfig,
            messageFile to defaultMessage,
            proxyFile to defaultProxy,
            antibotFile to defaultAntiBot,
            limboFile to defaultLimbo
        )
        defaultConfigMap.forEach { (file, config) ->
            val createConfig = CreateConfig(pluginFolder)
            createConfig.setDefaultConfig(config)
            createConfig.setConfigFile(file)
            createConfig.onLoad()
        }
    }

    private fun broadcastUpdate(newFile: Path) {
       listOf(
            "-------------------- MoeFilter --------------------",
            "您的配置文件或语言文件版本不同于您当前使用的MoeFilter版本.",
            "或者您的配置文件为空.",
            "",
            "我们已将您旧的配置文件重命名并生成新的一份.",
            "旧的配置文件将被命名成 ${newFile.fileName}",
            "",
            "插件可能将不按照您的预期工作. 建议编辑配置文件后重新启动代理",
            "即使您仍然不修改任何内容. 在之后时也会使用默认的配置文件.",
            "-------------------- MoeFilter --------------------"
        ).forEach { CPlatform.instance.getPlatformLogger().log(Level.WARNING, it) }
    }

    companion object {
        lateinit var instance: LoadConfig
            private set
    }
}
