package me.unariginal.genesisforms.items.helditems;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.config.items.helditems.MegastonesConfig;
import me.unariginal.genesisforms.items.BasePolymerItem;
import net.minecraft.item.Item;

import java.util.List;

public class Megastone extends BasePolymerItem {
    private final MegastonesConfig.MegastoneData megastoneData;

    public Megastone(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore, MegastonesConfig.MegastoneData megastoneData) {
        super(settings, polymerItem, modelData, itemID, lore);
        this.megastoneData = megastoneData;
    }

    public String getSpeciesString() {
        return megastoneData.species;
    }

    public Species getSpecies() {
        return PokemonSpecies.INSTANCE.getByName(megastoneData.species);
    }

    public String getShowdownID() {
        return megastoneData.showdownID;
    }

    public MegastonesConfig.MegastoneData getMegastoneData() {
        return megastoneData;
    }
}
