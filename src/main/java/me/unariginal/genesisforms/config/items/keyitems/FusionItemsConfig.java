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
import java.util.LinkedHashMap;
import java.util.List;

public class FusionItemsConfig {
    public static LinkedHashMap<String, FusionItemData> fusionItemMap = new LinkedHashMap<>();

    public static class FusionItemData {
        public boolean consumable;
        public List<FusionData> fusions;
        public List<String> lore;

        public FusionItemData(boolean consumable, List<FusionData> fusions, List<String> lore) {
            this.consumable = consumable;
            this.fusions = fusions;
            this.lore = lore;
        }
    }

    public static class FusionData {
        @SerializedName(value = "core_pokemon", alternate = "corePokemon")
        public String corePokemon;
        @SerializedName(value = "fuel_pokemon", alternate = "fuelPokemon")
        public List<FuelPokemonData> fuelPokemon;

        public FusionData(String corePokemon, List<FuelPokemonData> fuelPokemon) {
            this.corePokemon = corePokemon;
            this.fuelPokemon = fuelPokemon;
        }
    }

    public static class FuelPokemonData {
        public String species;
        @SerializedName(value = "result_feature_name", alternate = "resultFeatureName")
        public String resultFeatureName;
        @SerializedName(value = "result_feature_value", alternate = "resultFeatureValue")
        public String resultFeatureValue;

        public FuelPokemonData(String species, String resultFeatureName, String resultFeatureValue) {
            this.species = species;
            this.resultFeatureName = resultFeatureName;
            this.resultFeatureValue = resultFeatureValue;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        String json = "{}";

        File fusionsConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/key_items/fusion_items.json").toFile();
        if (!fusionsConfigFile.exists()) {
            File configFilePath = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/items/key_items/").toFile();
            configFilePath.mkdirs();
            ConfigUtils.create(fusionsConfigFile, "/genesis_configs/items/key_items/fusion_items.json");
        }
        if (fusionsConfigFile.exists()) json = JsonParser.parseReader(new FileReader(fusionsConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, FusionItemData>>() {}.getType();
        fusionItemMap = gson.fromJson(json, mapType);
    }
}
