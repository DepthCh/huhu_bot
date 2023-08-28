package tech.chowyijiu.huhubot.plugins.personal;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhubot.config.BotConfig;
import tech.chowyijiu.huhubot.config.WeiboConfig;
import tech.chowyijiu.huhubot.core.annotation.BotPlugin;
import tech.chowyijiu.huhubot.core.annotation.MessageHandler;
import tech.chowyijiu.huhubot.core.entity.arr_message.Message;
import tech.chowyijiu.huhubot.core.entity.arr_message.MessageSegment;
import tech.chowyijiu.huhubot.core.event.message.GroupMessageEvent;
import tech.chowyijiu.huhubot.core.event.message.MessageEvent;
import tech.chowyijiu.huhubot.core.event.message.PrivateMessageEvent;
import tech.chowyijiu.huhubot.core.rule.Rule;
import tech.chowyijiu.huhubot.core.rule.RuleEnum;
import tech.chowyijiu.huhubot.core.ws.Bot;
import tech.chowyijiu.huhubot.core.ws.Huhubot;
import tech.chowyijiu.huhubot.utils.xiaoai.XiaoAIUtil;

import java.util.Objects;

/**
 * @author elastic chow
 * @date 21/5/2023
 */
@Slf4j
@BotPlugin(name = "huhubot-plugin-seveneight")
@SuppressWarnings("unused")
public class PersonalPlugin {


    //@Scheduled(cron = "0 1 0 * * *")
    public void scheduledCheck0() {
        check(WeiboConfig.pids.get(0));
    }

    //@Scheduled(cron = "0 30 8 * * *")
    public void scheduledCheck1() {
        check(WeiboConfig.pids.get(1));
    }


    public void check(String pid) {
        log.info("开始超话签到");
        boolean ok =  WeiBoClient.check(pid);
        String result = "勇远期岱超话今日" + (ok ? "签到成功" : "签到失败");
        //结果发送到测试群
        Objects.requireNonNull(Huhubot.getBot(BotConfig.superUsers.get(0)))
                .sendGroupMessage(BotConfig.testGroup, result);
        //让小爱播报
        XiaoAIUtil.tts(result);
        log.info("超话签到完毕, " + result);
    }

    Rule replyJyGroupRule = (bot, event) -> "group".equals(((PrivateMessageEvent) event).getSubType());

    @MessageHandler(name = "回复jy群的临时会话", keywords = {"汉化", "英文", "中文"})
    public void replyJyGroup(Bot bot, PrivateMessageEvent event) {
        Message message = Message.text("[bot]").append(MessageSegment.at(event.getUserId()))
                .append("请认真观看教程视频 https://www.bilibili.com/video/BV1Xg411x7S2 不要再发临时会话问我或者其他管理了");
        bot.sendGroupMessage(event.getSender().getGroupId(), message);
    }

    @MessageHandler(name = "文字转语音", commands = {"tts", "文字转语音"}, rule = RuleEnum.superuser)
    @Deprecated
    public void textToTts(Bot bot, GroupMessageEvent event) {
        bot.sendMessage(event, MessageSegment.tts(event.getCommandArgs()));
    }

    @MessageHandler(name = "echo", commands = "echo", rule = RuleEnum.superuser)
    public void echo(Bot bot, MessageEvent event) {
        bot.sendMessage(event, event.getCommandArgs());
    }

    //@MessageHandler(name = "测试发送群转发消息", commands = "转发", rule = RuleEnum.superuser)
    //public void testSendGroupForwardMsg(Bot bot, GroupMessageEvent event) {
    //    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    //    map.put("1", "转发消息是完全不可信的");
    //    map.put("2", "昵称内容头像均可自定义");
    //    map.put("3", "连演员啥的都不需要请捏");
    //    map.put("4", "当然此条也是不可信的哈");
    //    List<ForwardMessage> nodes = ForwardMessage.quickBuild(bot.getUserId(), map);
    //    bot.sendGroupForwardMsg(event.getGroupId(), nodes);
    //}
}
