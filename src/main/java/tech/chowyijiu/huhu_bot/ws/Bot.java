package tech.chowyijiu.huhu_bot.ws;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tech.chowyijiu.huhu_bot.constant.GocqActionEnum;
import tech.chowyijiu.huhu_bot.constant.MessageTypeEnum;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.entity.gocq.request.Params;
import tech.chowyijiu.huhu_bot.entity.gocq.request.RequestBox;

/**
 * @author elastic chow
 * @date 17/5/2023
 */
@Slf4j
@Getter
@RequiredArgsConstructor
@ToString
public class Bot {

    private final Long userId;
    private final WebSocketSession session;

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param message    消息
     * @param autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendGroupMessage(Long groupId, String message, boolean autoEscape) {
        RequestBox<Params> requestBox = new RequestBox<>();
        requestBox.setAction(GocqActionEnum.SEND_GROUP_MSG.getAction());

        Params params = new Params();
        params.setMessageType(MessageTypeEnum.group.getType());
        params.setAutoEscape(autoEscape);
        params.setGroupId(groupId);
        params.setMessage(message);

        requestBox.setParams(params);
        sendMessage(JSONObject.toJSONString(requestBox));
    }

    /**
     * 发送私聊消息
     *
     * @param userId     对方qq
     * @param message    消息
     * @param autoEscape 是否以纯文本发送 true:以纯文本发送，不解析cq码
     */
    public void sendPrivateMessage(Long userId, String message, boolean autoEscape) {
        RequestBox<Params> paramsRequestBox = new RequestBox<>();
        paramsRequestBox.setAction(GocqActionEnum.SEND_PRIVATE_MSG.getAction());
        Params params = new Params();
        params.setMessageType(MessageTypeEnum.private_.getType());
        params.setAutoEscape(autoEscape);
        params.setUserId(userId);
        params.setMessage(message);
        paramsRequestBox.setParams(params);
        sendMessage(JSONObject.toJSONString(paramsRequestBox));
    }

    public void sendMessage(Event event, String message, boolean autoEscape) {
        JSONObject jsonObject = event.getJsonObject();
        Long groupId = jsonObject.getLong("group_id");
        if (groupId != null) {
            sendGroupMessage(groupId, message, autoEscape);
        } else {
            Long userId = jsonObject.getLong("user_id");
            if (userId != null) {
                sendPrivateMessage(userId, message, autoEscape);
            }
        }
    }

    private void sendMessage(String text) {
        try {
            session.sendMessage(new TextMessage(text));
        } catch (Exception e) {
            log.error("发送消息发生异常,session:{},消息：{}", session, text, e);
        }
    }


}
