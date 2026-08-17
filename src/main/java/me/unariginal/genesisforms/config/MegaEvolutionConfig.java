package me.unariginal.genesisforms.config;

import com.cobblemon.mod.common.pokemon.Pokemon;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.unariginal.genesisforms.config.ConfigManager.MEGA_EVOLUTIONS;

public class MegaEvolutionConfig {
    @Nullable
    public RequiredData required = new RequiredData();
    public String featureName = "mega_evolution";
    public String featureValue = "mega";
    public boolean hasItem = false;
    @Nullable
    public ItemData itemInformation = new ItemData();

    public static class RequiredData {
        public String species = "";
        public List<String> aspects = new ArrayList<>();
        public List<String> moves = new ArrayList<>();
    }

    public static class ItemData {
        public String showdownId;
        public String itemId;
        public List<String> lore = new ArrayList<>();
    }

    public boolean canMegaEvolve(Pokemon pokemon) {
        if (required == null) return true;

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

    public static MegaEvolutionConfig getMegaEvolution(Pokemon pokemon) {
        for (MegaEvolutionConfig megaEvolutionConfig : MEGA_EVOLUTIONS.values()) {
            if (megaEvolutionConfig.canMegaEvolve(pokemon)) {
                return megaEvolutionConfig;
            }
        }
        return null;
    }
}