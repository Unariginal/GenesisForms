package me.unariginal.genesisforms.config.items.helditems;

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
import java.util.LinkedHashMap;
import java.util.List;

public class HeldFormItemsConfig {
    public static LinkedHashMap<String, HeldFormItemData> heldFormItemMap = new LinkedHashMap<>();

    public static class HeldFormItemData {
        @SerializedName(value = "showdown_id", alternate = {"showdownId", "showdownID", "showdownid"})
        public String showdownID;
        public List<String> species;

        @SerializedName(value = "feature_name", alternate = "featureName")
        public String featureName;
        @SerializedName(value = "default_value", alternate = "defaultValue")
        public String defaultValue;
        @SerializedName(value = "alternate_value", alternate = "alternateValue")
        public String alternateValue;

        public List<String> lore;

        public HeldFormItemData(String showdownID, List<String> species, String featureName, String defaultValue, String alternateValue, List<String> lore) {
            this.showdownID = showdownID;
            this.species = species;
            this.featureName = featureName;
            this.defaultValue = defaultValue;
            this.alternateValue = alternateValue;
            this.lore = lore;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String json = "{}";

        File heldFormItemsConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/held_form_items.json").toFile();
        if (!heldFormItemsConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(heldFormItemsConfigFile, "/genesis_configs/items/held_items/held_form_items.json");
        }
        if (heldFormItemsConfigFile.exists()) json = JsonParser.parseReader(new FileReader(heldFormItemsConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, HeldFormItemData>>() {}.getType();
        heldFormItemMap = gson.fromJson(json, mapType);
    }
}
