package me.unariginal.genesisforms.items.helditems;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.config.MegaEvolutionConfig;
import me.unariginal.genesisforms.items.BasePolymerItem;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Megastone extends BasePolymerItem {
    private final MegaEvolutionConfig megastoneData;

    public Megastone(Settings settings, Item polymerItem, PolymerModelData modelData, String itemId, List<String> lore, MegaEvolutionConfig megastoneData) {
        super(settings, polymerItem, modelData, itemId, lore);
        this.megastoneData = megastoneData;
    }

    public Species getSpecies() {
        return PokemonSpecies.getByName(megastoneData.required.species);
    }

    @Nullable
    public String getShowdownID() {
        if (megastoneData.itemInformation != null)
            return megastoneData.itemInformation.showdownId;
        return null;
    }

    public MegaEvolutionConfig getMegastoneData() {
        return megastoneData;
    }
}
