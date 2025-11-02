package me.unariginal.genesisforms.config;

import com.cobblemon.mod.common.pokemon.Pokemon;
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
import java.util.List;

public class MegaEvolutionConfig {
    public static LinkedHashMap<String, MegaEvolutionData> megaEvolutionMap = new LinkedHashMap<>();
    public static List<String> itemlessMegas = new ArrayList<>();

    public static class MegaEvolutionData {
        public RequiredData required;

        @SerializedName(value = "feature_name", alternate = "featureName")
        public String featureName;
        @SerializedName(value = "feature_value", alternate = "featureValue")
        public String featureValue;

        @SerializedName(value = "has_item", alternate = "hasItem")
        public boolean hasItem;
        @SerializedName(value = "item_information", alternate = "itemInformation")
        public ItemData itemInformation;

        public MegaEvolutionData(RequiredData required, String featureName, String featureValue, boolean hasItem, ItemData itemInformation) {
            this.required = required;
            this.featureName = featureName;
            this.featureValue = featureValue;
            this.hasItem = hasItem;
            this.itemInformation = itemInformation;
        }

        public static class RequiredData {
            public String species;
            public List<String> aspects;
            public List<String> moves;

            public RequiredData(String species, List<String> aspects, List<String> moves) {
                this.species = species;
                this.aspects = aspects;
                this.moves = moves;
            }
        }

        public static class ItemData {
            @SerializedName(value = "showdown_id", alternate = "showdownID")
            public String showdownID;
            @SerializedName(value = "item_id", alternate = "itemID")
            public String itemID;
            public List<String> lore;

            public ItemData(String showdownID, String itemID, List<String> lore) {
                this.showdownID = showdownID;
                this.itemID = itemID;
                this.lore = lore;
            }
        }

        public boolean canMegaEvolve(Pokemon pokemon) {
            if (!required.species.equalsIgnoreCase(pokemon.getSpecies().getName()))
                return false;
            for (String aspect : required.aspects) {
                if (!pokemon.getAspects().contains(aspect))
                    return false;
            }
            for (String moveName : required.moves) {
                if (pokemon.getMoveSet().getMoves().stream().noneMatch(move -> move.getTemplate().getName().equalsIgnoreCase(moveName)))
                    return false;
            }
            return true;
        }
    }

    public static void load() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .create();

        String json = "{}";

        File megaEvolutionConfigFile = FabricLoader.getInstance().getConfigDir().resolve("GenesisForms/mega_evolutions.json").toFile();
        if (!megaEvolutionConfigFile.exists())
            ConfigUtils.create(megaEvolutionConfigFile, "/genesis_configs/mega_evolutions.json");
        if (megaEvolutionConfigFile.exists()) json = JsonParser.parseReader(new FileReader(megaEvolutionConfigFile)).toString();

        Type mapType = new TypeToken<LinkedHashMap<String, MegaEvolutionData>>() {}.getType();
        megaEvolutionMap = gson.fromJson(json, mapType);

        megaEvolutionMap.forEach((key, value) -> {
            if (!value.hasItem)
                itemlessMegas.add(key);
        });
    }

    public static MegaEvolutionData getMegaEvolution(Pokemon pokemon) {
        for (MegaEvolutionData megaEvolutionData : megaEvolutionMap.values()) {
            if (megaEvolutionData.canMegaEvolve(pokemon))
                return megaEvolutionData;
        }
        return null;
    }
}