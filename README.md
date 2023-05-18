# huhu_bot
nonebot2, 但是jvav

## 介绍
1. java(springboot)基于go-cqhttp, websocket反向连接的qq机器人;
2. 支持被多个go-cqhttp连接;
3. 类似于nonebot2的插件编写方式

## 架构
Spring Boot
go-cqhttp
websocket

## 使用要点
go-cqhttp 反向ws地址设置如 ws://127.0.0.1:8888/huhu/ws


## 示例插件
```Java
@Slf4j
@BotPlugin(name = "测试插件")
public class TestPlugin {

    @MessageHandler(name = "测试消息", commands = {"测试", "test"})
    public void test1(Bot bot, MessageEvent event) {
        bot.sendMessage(event, "测试消息", false);
    }

    @MessageHandler(name = "群聊测试1", commands = {"echo"}, priority = 3)
    public void test2(Bot bot, GroupMessageEvent event) {
        bot.sendGroupMessage(event.getGroupId(), "群聊测试111", true);
    }

    @MessageHandler(name = "群聊测试2", commands = {"echo"}, priority = 2, block = true)
    public void test3(Bot bot, GroupMessageEvent event) {
        bot.sendGroupMessage(event.getGroupId(), "群聊测试222", true);
    }

    @MessageHandler(name = "群聊测试3", commands = {"echo"}, priority = 1)
    public void test7(Bot bot, GroupMessageEvent event) {
        bot.sendGroupMessage(event.getGroupId(), "群聊测试333", true);
    }

    @MessageHandler(name = "私聊测试", commands = {"echo"})
    public void test4(Bot bot, PrivateMessageEvent event) {
        bot.sendPrivateMessage(event.getUserId(), "测试私聊", true);
    }

    @NoticeHandler(name = "群聊撤回1", priority = 2)
    public void test5(Bot bot, GroupRecallNoticeEvent event) {
        bot.sendMessage(event, "群聊撤回1 优先级2", true);
    }
    @NoticeHandler(name = "群聊撤回2", priority = 1)
    public void test8(Bot bot, GroupRecallNoticeEvent event) {
        bot.sendMessage(event, "群聊撤回2 优先级1", true);
    }

    @NoticeHandler(name = "群头衔变更")
    public void test6(Bot bot, NotifyNoticeEvent event) {
        if (SubTypeEnum.title.name().equals(event.getSubType())) {
            bot.sendMessage(event, "群头衔变更", true);
        }
    }
}

```

参考 [haruhibot](https://gitee.com/Lelouch-cc/haruhibot-server)
