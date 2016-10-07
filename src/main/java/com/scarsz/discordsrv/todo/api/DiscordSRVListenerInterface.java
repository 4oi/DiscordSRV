package com.scarsz.discordsrv.todo.api;

import com.scarsz.discordsrv.todo.api.events.ProcessChatEvent;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public interface DiscordSRVListenerInterface {

    void onDiscordMessageReceived(MessageReceivedEvent event);
    void onRawDiscordEventReceived(Event event);
    void onProcessChat(ProcessChatEvent event);

}
