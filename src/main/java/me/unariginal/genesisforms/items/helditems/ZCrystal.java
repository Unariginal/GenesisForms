package me.unariginal.genesisforms.items.helditems;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.items.BasePolymerItem;
import net.minecraft.item.Item;

import java.util.List;

public class ZCrystal extends BasePolymerItem {
    private final List<FormSetting> formChanges;
    private final String showdownID;

    public ZCrystal(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore, List<FormSetting> formChanges, String showdownID) {
        super(settings, polymerItem, modelData, itemID, lore);
        this.formChanges = formChanges;
        this.showdownID = showdownID;
    }

    public List<FormSetting> getFormChanges() {
        return formChanges;
    }

    public String getShowdownID() {
        return showdownID;
    }
}
