package com.wertycraft.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import org.simpleyaml.configuration.file.YamlFile;

import javax.security.auth.login.LoginException;

public class WertyBot {

    public static YamlFile config = new YamlFile("config.yml");

    private static final String token() {
        return config.getString("token");
    }

    public static void main(String[] args) {
        try {
            if (!config.exists()) config.createNewFile(true);
            config.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JDABuilder builder = new JDABuilder(token());
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.ZLIB);
        builder.setActivity(Activity.watching(config.getString("server")));

        builder.addEventListeners(new WertyListener());

        try {
            builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
