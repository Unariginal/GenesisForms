package me.unariginal.genesisforms.config.items.keyitems;

import java.util.ArrayList;
import java.util.List;

public class FusionItemsConfig {
    public boolean consumable = false;
    public List<FusionData> fusions = new ArrayList<>();
    public List<String> lore = new ArrayList<>();

    public static class FusionData {
        public String corePokemon;
        public List<FuelPokemonData> fuelPokemon;
    }

    public static class FuelPokemonData {
        public String species;
        public String resultFeatureName;
        public String resultFeatureValue;
    }
}
