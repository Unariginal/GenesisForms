package me.unariginal.genesisforms.items.helditems;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.items.BasePolymerItem;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class HeldFormItem extends BasePolymerItem {
    private final FormSetting formData;
    private final String showdownID;

    public HeldFormItem(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore, String showdownID, FormSetting formData) {
        super(settings, polymerItem, modelData, itemID, lore);
        this.formData = formData;
        this.showdownID = showdownID;
    }

    public List<Species> getSpeciesList() {
        List<Species> speciesList = new ArrayList<>();
        for (String speciesName : formData.species) {
            Species species = PokemonSpecies.INSTANCE.getByName(speciesName);
            if (species != null) speciesList.add(species);
        }
        return speciesList;
    }

    public FormSetting getFormData() {
        return formData;
    }

    public String getShowdownID() {
        return showdownID;
    }
}
