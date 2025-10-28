package me.unariginal.genesisforms.config.items;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MiscItemsConfig {
    public static MiscItemData miscItemData;

    public static class MiscItemData {
        @SerializedName(value = "zygarde_cube", alternate = "zygardeCube")
        public ZygardeCubeData zygardeCube;
        public LinkedHashMap<String, MiscItem> featureless;

        public MiscItemData(ZygardeCubeData zygardeCube, LinkedHashMap<String, MiscItem> featureless) {
            this.zygardeCube = zygardeCube;
            this.featureless = featureless;
        }
    }

    public static class MiscItem {
        @SerializedName(value = "max_count", alternate = "maxCount")
        public Integer maxCount;
        public List<String> lore;
        public MiscItem(int maxCount, List<String> lore) {
            this.maxCount = maxCount;
            this.lore = lore;
        }
    }

    public static class ZygardeCubeData {
        @SerializedName(value = "enable_ability_swap", alternate = "enableAbilitySwap")
        public Boolean enableAbilitySwap;
        @SerializedName(value = "enable_form_swap", alternate = "enableFormSwap")
        public Boolean enableFormSwap;
        @SerializedName(value = "feature_name", alternate = "featureName")
        public String featureName;
        @SerializedName(value = "feature_values", alternate = "featureValues")
        public List<String> featureValues;
        public List<String> lore;

        public ZygardeCubeData(boolean enableAbilitySwap, boolean enableFormSwap, String featureName, List<String> featureValues, List<String> lore) {
            this.enableAbilitySwap = enableAbilitySwap;
            this.enableFormSwap = enableFormSwap;
            this.featureName = featureName;
            this.featureValues = featureValues;
            this.lore = lore;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .create();

        String json = "{}";

        File miscItemsConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/misc_items.json").toFile();
        if (!miscItemsConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(miscItemsConfigFile, "/genesis_configs/items/misc_items.json");
        }
        if (miscItemsConfigFile.exists()) json = JsonParser.parseReader(new FileReader(miscItemsConfigFile)).toString();

        miscItemData = gson.fromJson(json, MiscItemData.class);
        miscItemData.featureless.values().forEach(item -> {
            if (item.maxCount == null) item.maxCount = 1;
            if (item.lore == null) item.lore = new ArrayList<String>();
        });
        save();
    }

    public static void save() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .create();

        FileWriter writer = new FileWriter("config/GenesisForms/items/misc_items.json");
        gson.toJson(miscItemData, MiscItemData.class, writer);
        writer.close();
    }
}
