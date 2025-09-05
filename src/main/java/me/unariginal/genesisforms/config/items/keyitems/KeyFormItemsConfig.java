package me.unariginal.genesisforms.config.items.keyitems;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class KeyFormItemsConfig {
    public static LinkedHashMap<String, KeyItemData> keyFormItemMap = new LinkedHashMap<>();

    public static class KeyItemData {
        public boolean consumable;
        @SerializedName(value = "max_stack_size", alternate = "maxStackSize")
        public int maxStackSize;
        public List<String> species;
        @SerializedName(value = "feature_name", alternate = "featureName")
        public String featureName;
        @SerializedName(value = "feature_values", alternate = "featureValues")
        public LinkedList<String> featureValues;

        public List<String> lore;

        public KeyItemData(boolean consumable, int maxStackSize, List<String> species, String featureName, LinkedList<String> featureValues) {
            this.consumable = consumable;
            this.maxStackSize = maxStackSize;
            this.species = species;
            this.featureName = featureName;
            this.featureValues = featureValues;
            this.lore = new ArrayList<>();
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String json = "{}";

        File keyItemsConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/key_items/key_form_items.json").toFile();
        if (!keyItemsConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/key_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(keyItemsConfigFile, "/genesis_configs/items/key_items/key_form_items.json");
        }
        if (keyItemsConfigFile.exists()) json = JsonParser.parseReader(new FileReader(keyItemsConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, KeyItemData>>() {}.getType();
        keyFormItemMap = gson.fromJson(json, mapType);
    }
}
