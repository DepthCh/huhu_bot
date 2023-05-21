package tech.chowyijiu.huhu_bot.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tech.chowyijiu.huhu_bot.annotation.BotPlugin;
import tech.chowyijiu.huhu_bot.annotation.MessageHandler;
import tech.chowyijiu.huhu_bot.annotation.NoticeHandler;
import tech.chowyijiu.huhu_bot.event.Event;
import tech.chowyijiu.huhu_bot.event.message.MessageEvent;
import tech.chowyijiu.huhu_bot.event.notice.NoticeEvent;
import tech.chowyijiu.huhu_bot.ws.Bot;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author elastic chow
 * @date 15/5/2023
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatcherCore {

    private final ApplicationContext ioc;

    private final List<Handler> MESSAGE_HANDLER_CONTAINER = new ArrayList<>();
    private final List<Handler> NOTICE_HANDLER_CONTAINER = new ArrayList<>();

    @PostConstruct
    private void loadPlugin() {
        //获取所有插件Bean
        Map<String, Object> botPluginMap = ioc.getBeansWithAnnotation(BotPlugin.class);
        //创建两个临时存储的List
        List<Handler> messageHandlers = new ArrayList<>();
        List<Handler> noticeHandlers = new ArrayList<>();
        if (!botPluginMap.isEmpty()) {
            log.info("[DispatcherCore] Start Load Plugin...");
            int count = 1;
            for (String pluginName : botPluginMap.keySet()) {
                Object plugin = botPluginMap.get(pluginName);
                //插件功能名, 用于打印日志
                List<String> handlerNames = new ArrayList<>();
                Arrays.stream(plugin.getClass().getMethods()).forEach(method -> {
                    if (method.isAnnotationPresent(MessageHandler.class)) {
                        Handler handler = Handler.buildMessageHandler(plugin, method);
                        handlerNames.add(handler.name);
                        messageHandlers.add(handler);
                    } else if (method.isAnnotationPresent(NoticeHandler.class)) {
                        Handler handler = Handler.buildNoticeHandler(plugin, method);
                        handlerNames.add(handler.name);
                        noticeHandlers.add(handler);
                    }
                });
                log.info("Load plugin [{}], progress[{}/{}], function set: {}",
                        pluginName, count++, botPluginMap.size(), Arrays.toString(handlerNames.toArray()));
            }
        }
        if (messageHandlers.isEmpty() && noticeHandlers.isEmpty()) {
            throw new RuntimeException("[DispatcherCore] No plugins were found");
        }
        //根据weight对handler进行排序, 并全部加入到handlerContainer中
        MESSAGE_HANDLER_CONTAINER.addAll(messageHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority))
                .collect(Collectors.toList())
        );
        NOTICE_HANDLER_CONTAINER.addAll(noticeHandlers.stream()
                .sorted(Comparator.comparingInt(handler -> handler.priority))
                .collect(Collectors.toList()));
    }

    public void matchMessageHandler(final Bot bot, final MessageEvent event) {
        log.info("{} start match handler", event);
        outer:
        for (Handler handler : MESSAGE_HANDLER_CONTAINER) {
            if (handler.commands != null && handler.commands.length > 0) {
                for (String command : handler.commands) {
                    if (event.getMessage().startsWith(command)) {
                        if (handler.eventType.isAssignableFrom(event.getClass())) {
                            log.info("{} will be handled by Plugin[{}], Command[{}], Priority[{}]",
                                    event, handler.plugin.getClass().getSimpleName(), command, handler.priority);
                            handler.execute(bot, event);
                            if (handler.block) {
                                //停止向低优先级传递
                                break outer;
                            }
                        }
                    }
                }
                continue;
            }
            if (handler.keywords != null && handler.keywords.length > 0) {
                for (String keyword : handler.keywords) {
                    if (event.getMessage().contains(keyword)) {
                        if (handler.eventType.isAssignableFrom(event.getClass())) {
                            log.info("{} will be handled by Plugin[{}], Keyword[{}], Priority[{}]",
                                    event, handler.plugin.getClass().getSimpleName(), keyword, handler.priority);
                            handler.execute(bot, event);
                            if (handler.block) {
                                //停止向低优先级传递
                                break outer;
                            }
                        }
                    }
                }
            }
        }
        log.info("{} match handler end", event);
    }

    private void matchCommand() {

    }

    public void matchNoticeHandler(final Bot bot, final NoticeEvent event) {
        log.info("{} start match handler", event);
        for (Handler handler : NOTICE_HANDLER_CONTAINER) {
            if (handler.eventType.isAssignableFrom(event.getClass())) {
                log.info("{} will be handled by Plugin[{}] Function[{}] Priority[{}]",
                        event, handler.plugin.getClass().getSimpleName(), handler.name, handler.priority);
                handler.execute(bot, event);
                if (handler.block) {
                    break;
                }
            }
        }
        log.info("{} match handler end", event);
    }


    static class Handler {
        private final Object plugin;
        private final Method method;

        public Class<?> eventType;  //用于isAssignableFrom 匹配事件类型
        public String name;         //Handler注解里的name
        public int priority;
        public boolean block;

        //MessageHandler
        public String[] commands;
        public String[] keywords;

        private Handler(Object plugin, Method method) {
            this.plugin = plugin;
            this.method = method;
            for (Class<?> clazz : method.getParameterTypes()) {
                if (Event.class.isAssignableFrom(clazz)) {
                    eventType = clazz;
                    break;
                }
            }
        }

        public static Handler buildMessageHandler(Object plugin, Method method) {
            Handler handler = new Handler(plugin, method);
            MessageHandler mh = method.getAnnotation(MessageHandler.class);
            handler.name = mh.name();
            handler.block = mh.block();
            handler.priority = mh.priority();
            handler.commands = mh.commands();
            handler.keywords = mh.keywords();
            return handler;
        }

        public static Handler buildNoticeHandler(Object plugin, Method method) {
            Handler handler = new Handler(plugin, method);
            NoticeHandler nh = method.getAnnotation(NoticeHandler.class);
            handler.name = nh.name();
            handler.priority = nh.priority();
            return handler;
        }

        public void execute(Object... args) {
            try {
                method.invoke(plugin, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
