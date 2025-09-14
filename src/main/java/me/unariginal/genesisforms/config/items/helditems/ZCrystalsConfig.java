package me.unariginal.genesisforms.config.items.helditems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;

public class ZCrystalsConfig {
    public static ZCrystalData zCrystalData;

    public static class ZCrystalData {
        @SerializedName(value = "typed")
        public LinkedHashMap<String, TypedZCrystalData> typedZCrystalMap;
        @SerializedName(value = "species")
        public LinkedHashMap<String, SpeciesZCrystalData> speciesZCrystalMap;

        public ZCrystalData(LinkedHashMap<String, TypedZCrystalData> typedZCrystalMap, LinkedHashMap<String, SpeciesZCrystalData> speciesZCrystalMap) {
            this.typedZCrystalMap = typedZCrystalMap;
            this.speciesZCrystalMap = speciesZCrystalMap;
        }
    }

    public static class TypedZCrystalData {
        @SerializedName(value = "showdown_id", alternate = {"showdownId", "showdownID", "showdownid"})
        public String showdownID;
        public String type;

        @SerializedName(value = "form_changes", alternate = "formChanges")
        public List<FormSetting> formChanges;

        public List<String> lore;

        public TypedZCrystalData(String showdownID, String type, List<FormSetting> formChanges, List<String> lore) {
            this.showdownID = showdownID;
            this.type = type;
            this.formChanges = formChanges;
            this.lore = lore;
        }

        @Override
        public String toString() {
            return "Showdown ID: " + showdownID + "\nType: " + type + "\nForm Changes: " + formChanges + "\nLore: " + lore;
        }
    }

    public static class SpeciesZCrystalData {
        @SerializedName(value = "showdown_id", alternate = {"showdownId", "showdownID", "showdownid"})
        public String showdownID;
        public List<String> lore;

        public SpeciesZCrystalData(String showdownID, List<String> lore) {
            this.showdownID = showdownID;
            this.lore = lore;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String json = "{}";

        File zCrystalConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/z_crystals.json").toFile();
        if (!zCrystalConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/held_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(zCrystalConfigFile, "/genesis_configs/items/held_items/z_crystals.json");
        }
        if (zCrystalConfigFile.exists()) json = JsonParser.parseReader(new FileReader(zCrystalConfigFile)).toString();

        zCrystalData = gson.fromJson(json, ZCrystalData.class);
    }
}
