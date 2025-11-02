package me.unariginal.genesisforms.items.helditems;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.config.MegaEvolutionConfig;
import me.unariginal.genesisforms.items.BasePolymerItem;
import net.minecraft.item.Item;

import java.util.List;

public class Megastone extends BasePolymerItem {
    private final MegaEvolutionConfig.MegaEvolutionData megastoneData;

    public Megastone(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore, MegaEvolutionConfig.MegaEvolutionData megastoneData) {
        super(settings, polymerItem, modelData, itemID, lore);
        this.megastoneData = megastoneData;
    }

    public Species getSpecies() {
        return PokemonSpecies.INSTANCE.getByName(megastoneData.required.species);
    }

    public String getShowdownID() {
        return megastoneData.itemInformation.showdownID;
    }

    public MegaEvolutionConfig.MegaEvolutionData getMegastoneData() {
        return megastoneData;
    }
}
