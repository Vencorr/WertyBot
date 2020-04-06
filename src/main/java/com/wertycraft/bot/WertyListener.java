package com.wertycraft.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WertyListener extends ListenerAdapter {

    private Color pingInd(long ping) {
        if (ping <= 0 || ping >= 500) return Color.RED;
        else if (ping > 300) return Color.ORANGE;
        else if (ping > 100) return Color.YELLOW;
        else return Color.GREEN;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String displayMsg = event.getMessage().getContentDisplay();
        String prefix = "!";

        MCStatus mc = new MCStatus(WertyBot.config.getString("server"), WertyBot.config.getInt("port"));
        RestAction<Void> typing = event.getChannel().sendTyping();
        if (displayMsg.startsWith(prefix))
        {
            switch (displayMsg.substring(1)) {
                default:
                    break;
                case "help":
                    typing.queue();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("WertyBot Help")
                            .setColor(Color.GRAY)
                            .setDescription("Commands for WertyBot.\n`" + prefix +
                                    "help` - Show this message.\n`" + prefix +
                                    "server` - Display information.\n`" + prefix +
                                    "status` - Check if online.\n`" + prefix +
                                    "ping` - Check latency.\n`" + prefix +
                                    "version` - Display Minecraft version.\n`" + prefix +
                                    "players` - Display number of players online.")
                            .build())
                            .queue();
                    break;
                case "server":
                    typing.queue();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Server")
                            .setColor(mc.getOnline() ? Color.GREEN : Color.RED)
                            .setFooter(mc.server + ":" + mc.port)
                            .setDescription(mc.getOnline() ? mc.getMotd() : "\n")
                            .addField("Status", mc.getOnline() ? "Online": "Offline", false)
                            .addField("Version", mc.getVersion(), false)
                            .addField("Latency", mc.getLatency() + "ms", false)
                            .addField("Players", mc.getCurrentPlayers() + "/" + mc.getMaxPlayers(), false)
                            .build())
                            .queue();
                    break;
                case "status":
                    typing.queue();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Status")
                            .setColor(mc.getOnline() ? Color.GREEN : Color.RED)
                            .setFooter(mc.server + ":" + mc.port)
                            .setDescription(mc.getOnline() ? "**Online**": "**Offline**")
                            .build())
                            .queue();
                    break;
                case "ping":
                    typing.queue();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Latency")
                            .setColor(pingInd(mc.getOnline() ? mc.getLatency() : 0))
                            .setFooter(mc.server + ":" + mc.port)
                            .setDescription(mc.getOnline() ? mc.getLatency() + "ms" : "0ms")
                            .build())
                            .queue();
                    break;
                case "ver": case "version":
                    typing.queue();
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Version")
                            .setFooter(mc.server + ":" + mc.port)
                            .setDescription(mc.getOnline() ? mc.getVersion() : "*Unknown*")
                            .build())
                            .queue();
                    break;
                case "plyrs": case "players":
                    String playersTxt = "";
                    typing.queue();
                    List<String> players = mc.getPlayers();
                    if (players != null && !players.isEmpty()) {
                        StringBuilder plyList = new StringBuilder("\n\n");
                        for (int i = 0; i < players.size(); i++) {
                            if (i >= 25) {
                                plyList.append("and ").append(players.size() - i).append(" more.");
                                break;
                            } else {
                                plyList.append(players.get(i));
                                if (i != players.size() - 1) {
                                    plyList.append(", ");
                                }
                            }
                        }
                        playersTxt = plyList.toString();
                    }
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Players")
                            .setFooter(mc.server + ":" + mc.port)
                            .setDescription(mc.getOnline() ? mc.getCurrentPlayers() + "/" + mc.getMaxPlayers() : "0/0")
                            .addField("Player List", ((players == null || players.isEmpty()) ? "*Unavailable*" : playersTxt), true)
                            .build())
                            .queue();
                    break;
            }
        }
    }
}
