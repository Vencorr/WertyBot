package com.wertycraft.bot;

import me.dilley.MineStat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class WertyListener extends ListenerAdapter {

    private final URL serverURL() throws MalformedURLException {
        return new URL(WertyBot.config.getString("server"));
    };

    private boolean online() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(WertyBot.config.getString("server"), WertyBot.config.getInt("port")), WertyBot.config.getInt("timeout"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Color pingInd(long ping) {
        if (ping <= 0 || ping > 300) return Color.RED;
        else if (ping > 100) return Color.ORANGE;
        else if (ping > 80) return Color.YELLOW;
        else return Color.GREEN;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        MineStat ms = new MineStat(WertyBot.config.getString("server"), WertyBot.config.getInt("port"));
        String displayMsg = event.getMessage().getContentDisplay();
        String prefix = "!";
        if (displayMsg.startsWith(prefix))
        {
            switch (displayMsg.substring(1)) {
                default:
                    break;
                case "help":
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
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Server")
                            .setColor(online() ? Color.GREEN : Color.RED)
                            .setFooter(ms.getAddress() + ":" + ms.getPort())
                            .setDescription(online() ?
                                    "*" + ms.getMotd() + "*\n" +
                                            "**Version:** " + ms.getVersion() + "\n" +
                                            "**Latency:** " + ms.getLatency() + "\n" +
                                            "**Players:** " + ms.getCurrentPlayers() + "/" + ms.getMaximumPlayers()
                                    : "***Server is Offline***")
                            .build())
                            .queue();
                    break;
                case "status":
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Status")
                            .setColor(online() ? Color.GREEN : Color.RED)
                            .setFooter(ms.getAddress() + ":" + ms.getPort())
                            .setDescription(online() ? ":green_circle: **Server is Online**": ":red_circle: **Server is Offline**")
                            .build())
                            .queue();
                    break;
                case "ping":
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Latency")
                            .setColor(pingInd(online() ? ms.getLatency() : 0))
                            .setFooter(ms.getAddress() + ":" + ms.getPort())
                            .setDescription(online() ? ms.getLatency() + "ms" : "0ms")
                            .build())
                            .queue();
                    break;
                case "ver": case "version":
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Version")
                            .setFooter(ms.getAddress() + ":" + ms.getPort())
                            .setDescription(online() ? ms.getVersion() : "*Unknown*")
                            .build())
                            .queue();
                    break;
                case "plyrs": case "players":
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Online Players")
                            .setFooter(ms.getAddress() + ":" + ms.getPort())
                            .setDescription(online() ? ms.getCurrentPlayers() + "/" + ms.getMaximumPlayers() : "0/0")
                            .build())
                            .queue();
                    break;
            }
        }
    }
}
