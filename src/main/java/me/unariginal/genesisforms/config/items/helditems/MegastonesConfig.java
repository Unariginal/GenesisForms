package me.unariginal.genesisforms.config.items.helditems;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

public class MegastonesConfig {
    public static LinkedHashMap<String, MegastoneData> megastoneMap = new LinkedHashMap<>();

    public static class MegastoneData {
        @SerializedName(value = "showdown_id", alternate = {"showdownId", "showdownID", "showdownid"})
        public String showdownID;
        public String species;

        @SerializedName(value = "feature_name", alternate = "featureName")
        public String featureName;
        @SerializedName(value = "feature_value", alternate = "featureValue")
        public String featureValue;

        public List<String> lore;

        public MegastoneData(String showdownID, String species, String featureName, String featureValue, List<String> lore) {
            this.showdownID = showdownID;
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

        File megastonesConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/megastones.json").toFile();
        if (!megastonesConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(megastonesConfigFile, "/genesis_configs/items/held_items/megastones.json");
        }
        if (megastonesConfigFile.exists()) json = JsonParser.parseReader(new FileReader(megastonesConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, MegastoneData>>() {}.getType();
        megastoneMap = gson.fromJson(json, mapType);
    }
}
