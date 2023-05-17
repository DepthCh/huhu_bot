package tech.chowyijiu.huhu_bot.plugins;

import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.message.MessageHandler;
import tech.chowyijiu.huhu_bot.entity.gocq.event.GroupMessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.MessageEvent;
import tech.chowyijiu.huhu_bot.entity.gocq.event.PrivateMessageEvent;
import tech.chowyijiu.huhu_bot.utils.MessageSegment;
import tech.chowyijiu.huhu_bot.ws.Bot;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
@BotPlugin(name = "堡垒之夜")
public class FortnitePlugin {

    @MessageHandler(name = "每日vb图", commands = {"vb", "VB"}, priority = 4)
    public void vb(Bot bot, GroupMessageEvent event) {
        bot.sendGroupMessage(event.getGroupId(), "vb图", true);
    }

    @MessageHandler(name = "商城", commands = {"商城"}, priority = 1)
    public void shop(Bot bot, MessageEvent event) {
        String cq = MessageSegment.image("https://cdn.dingpanbao.cn/blzy/shop.png");
        if (event instanceof PrivateMessageEvent) {
            bot.sendPrivateMessage(event.getUserId(), cq ,false);
        } else if (event instanceof GroupMessageEvent) {
            bot.sendGroupMessage(((GroupMessageEvent) event).getGroupId(), cq, false);
        }
    }

    //@Scheduled(cron = "0 1 8 ?")
    //public void scheduleShop() {
    //    String cq = MessageSegment.image("https://cdn.dingpanbao.cn/blzy/shop.png");
    //}
}
