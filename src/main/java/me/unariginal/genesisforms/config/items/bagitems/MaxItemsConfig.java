package me.unariginal.genesisforms.config.items.bagitems;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

public class MaxItemsConfig {
    public static LinkedHashMap<String, MaxItemData> maxItemMap = new LinkedHashMap<>();

    public static class MaxItemData {
        public boolean consumable;
        public List<String> lore;

        public MaxItemData(boolean consumable, List<String> lore) {
            this.consumable = consumable;
            this.lore = lore;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String json = "{}";

        File maxItemsConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/bag_items/max_items.json").toFile();
        if (!maxItemsConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/bag_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(maxItemsConfigFile, "/genesis_configs/items/bag_items/max_items.json");
        }
        if (maxItemsConfigFile.exists()) json = JsonParser.parseReader(new FileReader(maxItemsConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, MaxItemData>>() {}.getType();
        maxItemMap = gson.fromJson(json, mapType);
    }
}
