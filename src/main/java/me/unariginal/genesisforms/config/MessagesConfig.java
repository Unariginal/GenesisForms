package me.unariginal.genesisforms.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.unariginal.genesisforms.GenesisForms;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MessagesConfig {
    public String prefix = "<dark_gray>[<#35DEC6>Genesis<dark_gray>]";
    public final Map<String, String> messages = new HashMap<>();

    public MessagesConfig() {
        fillMessages();
        try {
            loadConfig();
        } catch (IOException e) {
            GenesisForms.INSTANCE.logError("[Genesis] Failed to load messages config file. Error: " + e.getMessage());
        }
    }

    public void fillMessages() {
        messages.clear();
        messages.put("reload_command", "%prefix% <green>Reloaded!");
        messages.put("reset_data_command", "%prefix% <green>Reset %player%'s Internal Form Data!");
        messages.put("give_command_received", "%prefix% <green>Received %item%!");
        messages.put("give_command_feedback", "%prefix% <green>Gave %item% to %player%!");
        messages.put("convert_command_hand", "%prefix% <green>Converted %original_item% to %new_item%!");
        messages.put("convert_command_inventory", "%prefix% <green>Converted %count% items!");
        messages.put("cube_mode_feedback", "<gray>Cube Mode: <green>%cube_mode%");
        messages.put("gmax_factor_applied", "<green>%pokemon% can now Gigantamax!");
        messages.put("gmax_factor_removed", "<red>%pokemon% can no longer Gigantamax!");
        messages.put("tera_type_changed", "<green>Set %pokemon%'s tera type to %pokemon.tera_type%!");
        messages.put("dynamax_level_changed", "<green>%pokemon%'s dynamax level is now %pokemon.dmax_level%!");
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

        File messagesFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/messages.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (messagesFile.exists()) {
            root = JsonParser.parseReader(new FileReader(messagesFile)).getAsJsonObject();
        }

        if (root.has("prefix")) {
            prefix = root.get("prefix").getAsString();
        }
        newRoot.addProperty("prefix", prefix);

        JsonObject messages = new JsonObject();
        if (root.has("messages")) {
            messages = root.getAsJsonObject("messages");
        }
        for (String key : messages.keySet()) {
            if (messages.has(key)) {
                this.messages.put(key, messages.get(key).getAsString());
            }
        }
        for (String key : this.messages.keySet()) {
            messages.addProperty(key, this.messages.get(key));
        }
        newRoot.add("messages", messages);

        messagesFile.delete();
        messagesFile.createNewFile();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter(messagesFile);
        gson.toJson(newRoot, writer);
        writer.close();
    }

    public String getMessage(String key) {
        if (messages.containsKey(key)) {
            return messages.get(key);
        }
        return "<red>Failed to load message with key \"" + key + "\".";
    }
}
