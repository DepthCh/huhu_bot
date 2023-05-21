package tech.chowyijiu.huhu_bot.event.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.chowyijiu.huhu_bot.constant.PostTypeEnum;
import tech.chowyijiu.huhu_bot.event.Event;

/**
 * @author elastic chow
 * @date 18/5/2023
 */
@Getter
@Setter
@ToString
public class RequestEvent extends Event {
    private final String postType = PostTypeEnum.request.name();
    private String echo;
    private String request_type; //friend group

    private Long userId;    //发送请求的 QQ 号
    private String comment; //验证信息
    private String flag;    //请求 flag, 在调用处理请求的 API 时需要传入

    //group 特有
    private String subType; //append、invite	请求子类型, 分别表示加群请求、邀请登录号入群
    private Long groupId;

}
