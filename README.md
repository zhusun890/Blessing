# Moe Filter

一款功能强大的 兼容性极强的反机器人和实用工具接口.  

---

## ~~画大饼的~~公告:  

v1完成了注入管道相关代码 将在不久的将来完全落地数据包检查和Firstjoin PingJoin之类的检查  
感谢那些支持本插件的人, 尽管这在之后会变成付费开源的产物.  

有些抱歉的事情是它不一定兼容全部分叉或插件.  
如果您需要使用的插件设计到修改管道但急需使用那个插件 可以考虑打开issue  
采纳features了之后将会实施 您永远都不需要多余的插件做相同的事情来降低性能.  

---
## 用前须知/免责声明:

MoeFilter作为一个实用工具**BungeeCord**插件 不会迁移到其它平台 (e.x Velocity)  
  
不会支持任何安装了两个或更多的反机器人插件的BungeeCord 这样做非常愚蠢  
不能保证支持任何BungeeCord分叉 如果出现问题 首先询问BungeeCord分叉的开发者  
  
### 不要报告任何慢速反机器人绕过!:  
#### **100%消除机器人是不可能的事情!**  
没有能绝对能杜绝机器人的东西. 要么牺牲玩家体验 要么就存在有潜在的绕过风险.  
任何反机器人皆在与保护您的服务器不在攻击中倒下 而不是完全消除机器人. 
> 在挨打时自动开白名单的插件**真的**能算是一个**反机器人**插件**吗?**  

在遭到攻击时 您应该庆幸的是在攻击期间您的服务器安然无恙 而不是在乎那没有脑子却能比玩家还轻松熟练地通过检查的机器人  
  
### 那反机器人呢? 我用MoeFilter就是为了它的反机器人的! :

您可以将MoeFilter放在BungeeCord以及其分叉上 一切都将按预期工作  
但我无法保证您的服务器在遭受机器人攻击时 MoeFilter不会被绕过  
  
> **[!]** 不要添加两个或两个以上的反机器人插件 推荐做法是一个自带反机器人的BungeeCord分叉和一个反机器人  
> **[!]** 不过 我无法保证所有反机器人都可以跟自带缓解攻击的BungeeCord分叉一起使用 例如[nAntiBot](https://en.docs.nickuc.com/v/nantibot/about)  
> **[!]** 至少对于MoeFilter来讲 这么做是最合适的  
  
### 我该选择什么缓解攻击的BungeeCord分叉? :  

首先 如果您不想在这上面花钱 您可以选择 [BotFilter](https://github.com/Leymooo/BungeeCord) 或 [Waterfall](https://papermc.io/downloads/waterfall)
  
#### [FlameCord](https://github.com/arkflame/FlameCord):  
MoeFilter实际上比FlameCord更好 但如果您想要GeoIP IPTables&IPSet 和坏数据包防御  
考虑到它的价格和开放源代码 您可以使用它跟MoeFilter一起使用.  
> 记得关闭除GeoIP和Exception数据包防御之外的功能 MoeFilter比它做的更好  

> MoeFilter 在之后会加入IPTables & IPSet支持
  
#### [BotFilter](https://github.com/Leymooo/BungeeCord):  
BotFilter足以降低坏数据包和机器人带来的影响. 因其虚拟服务器验证码而出名  
您可以将其和MoeFilter一起使用 以防止大量机器人淹没BotFilter.  
  
> 更多BungeeCord分叉将会根据我的使用体验在之后列出 当然我也可能不会再考虑更新该页面:

### 其它反机器人列表:  
  
> 此列表皆在列出那些比较出名的反机器人:

  - [nAntiBot](https://en.docs.nickuc.com/v/nantibot/about)
       - nAntiBot确实是一个很好的反机器人 但由于依赖云服务 对中国用户不是很友好
       - 如果您的服务器可以很好的访问它的云服务 您确实可以使用它而不是MoeFilter
  - AntiAttackRL (链接已死)
       - 曾经陪伴过各个服主的~~反机器人~~ 自动白名单插件
       - 尽管它尽责了 但它仍然不是很值得使用 (读下面看为什么)
       - 不必要的控制台垃圾邮件. 应该只保留必要信息以节省CPU使用率
       - 运用多个事件侦听器来完成检查
       - 不必要的反射注入管道 (也可能没写完? 但至少目前不值得使用 且已经宣布永久停更)
       - 自己的AlreadyOnline检查 甚至对于Collection<ProxiedPlayer>不是使用.contains  
         而是使用for循环遍历所有在线玩家 如果.equals再cancelled事件
  > 加入的一多这for循环就经不起折腾了.
  - [BotSentry](https://www.spigotmc.org/resources/%E2%9A%A1-botsentry-%E2%9A%A1-antibot-antiproxy-resisting-30k-bots-per-second-bungee-spigot-sponge-velocity.55924/)
       - "您已被列入黑名单  如果您认为您不是机器人 请点击下面的链接进行认证"
       - 仅依靠反代理云服务和 FirstJoin检查的狗屎
       - 新版本不如旧版本 资源链接下方的视频都是老视频
       - 如果遭到大型攻击 则您的服务器会立马崩溃
       - 源代码也已经被混淆 但我相信它跟AntiAttackRL那样在Handshake阶段干了相同的事情
  - [Sonar](https://github.com/jonesdevelopment/sonar)
       - 我目前见过最好的开源反机器人插件
       - 但.. 它多少有些囊肿了
       - 倒也不是不可以用
  - [EpicGuard](https://github.com/4drian3d/EpicGuard)
       - 可以很好地防御大部分攻击
       - 尽管不是很具有高性能 但它也可以是好的选择 我的意思是 相比于其它反机器人来讲
  - [Ultimate AntiBot](https://github.com/Kr1S-D/UltimateAntibotRecoded)
       - 万能的PreLoginEvent 在上面实现所有反机器人逻辑
       - 愚蠢的Lockdown策略 结合反VPN的自动白名单插件 但具有没用的自动过审功能
       - 给自己服务器用嘛.. 还开源 不寒碜
       - 很多人正在使用它 尽管在新版本也会变成过度依赖云服务的垃圾
   - [JH AntiBot v4](https://www.jhdev.net/details.php?id=7)
       - 还是写给自己服务器的东西.. (相比于UAB 很多东西是硬编码的)
       - 是一个~~反机器人~~ 每秒链接计数器
       - 需要爬取到代理才能让玩家进服务器 但相比起在攻击时调用API来检查来讲 算是好得多
       - 消失的v5版本
       - ~~它作为MCStorm的测试插件 不是因为JH Antibot是万能的 而是Jheyson曾是MCStorm工作人员.~~
   - [XProtect](https://github.com/LagBug/XProtect)
       - 它甚至不是BungeeCord插件 我只是在此告诉你这个而已 (xD)


> **[?]** 我不会对任何反机器人做出好坏的评价 请根据您自己的测试来平衡  

> **[!]** 无论如何 不要以"能防住就行"的概念来草率地决定您的服务器应该使用什么反机器人  
> **[!]** 如果您不知道如何权重反机器人的好坏 那请将保留CPU使用率为第一目的.

  
### 想为反机器人模块做出贡献? :

首先 通过直接修改BungeeCord的class来侦听监听管道(或类似的侵入性做法)不会得到支持.

支持使用事件的组合来创建反机器人:
  
  - 对于无效数据包或者玩家一般不会触发到的检查  
    快速使用PendingConnection或防火墙切断连接  
      
  - Ping检查应该在Handshake上检查. `requestProtocol == 1`  
    而不是使用ProxyPingEvent (这也可以保证兼容自带MOTD的BungeeCord分叉)  
    
  - 优先使用缓存而不是单独的Map或玩家来缓存某些内容  
    也请做好在单IP上连接数过大而未被及时被防火墙拦截的连接检查做好准备  
  
  - 反机器人不是反崩溃
    
  - 对攻击的方式以及反机器人的原理有充足的理解再做出贡献  
  
如果您或您的提交满足以上条件之后 欢迎对MoeFilter做出贡献.

---

## 此项目仍然在开发中 欢迎贡献!

但在贡献之前 请确保您添加的功能可以模块化(并且你这么做了)  
  
并且确保您的功能有相当的实用性
例如我们的
[ILogger](https://github.com/CatMoe/MoeFilter/blob/main/src/main/java/catmoe/fallencrystal/moefilter/api/logger/ILogger.kt)
和
[LoggerManager](https://github.com/CatMoe/MoeFilter/blob/main/src/main/java/catmoe/fallencrystal/moefilter/api/logger/LoggerManager.kt)
日志过滤器  
尝试用于解决只能设置一个过滤器的问题 之类的**比较有意义**的东西

> 尽量保持String为空 或在配置中指定 目前配置类还在待办事项内 可能将在两三天后实现落地.

---

## TODO


  - [x] 日志接口
  - [x] 命令接口
  - [x] 事件接口
  - [x] HOCON配置
  - [x] MiniMessage 支持 
  - [ ] 基本反机器人检查 (Ping, FirstJoin, etc)
  - [ ] 踢出消息缓存
  - [ ] ~~当切换版本时 自动补全配置文件中缺失的内容.~~ 
 > 遥遥无期 并且我可能并没有想这么做. 最简单的办法是备份他们的配置文件 然后生成一份新的)

> 这只是**临时的** TODO列表 会随着时间的推移而增加 不代表整个项目的process.

---

Made by CatMoe.
