package me.unariginal.genesisforms.config.items.keyitems;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

public class PossessionItemsConfig {
    public static LinkedHashMap<String, PossessionItemData> possessionItems = new LinkedHashMap<>();

    public static class PossessionItemData {
        public boolean placeable;
        public List<String> species;
        public String featureName;
        public String featureValue;
        public List<String> lore;

        public PossessionItemData(boolean placeable, List<String> species, String featureName, String featureValue, List<String> lore) {
            this.placeable = placeable;
            this.species = species;
            this.featureName = featureName;
            this.featureValue = featureValue;
            this.lore = lore;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String json = "{}";

        File possessionItemsConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/key_items/possession_items.json").toFile();
        if (!possessionItemsConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/key_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(possessionItemsConfigFile, "/genesis_configs/items/key_items/possession_items.json");
        }
        if (possessionItemsConfigFile.exists()) json = JsonParser.parseReader(new FileReader(possessionItemsConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, PossessionItemData>>() {}.getType();
        possessionItems = gson.fromJson(json, mapType);
    }
}
