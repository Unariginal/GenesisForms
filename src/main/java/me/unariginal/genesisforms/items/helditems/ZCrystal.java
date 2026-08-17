package me.unariginal.genesisforms.items.helditems;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.items.BasePolymerItem;
import net.minecraft.item.Item;

import java.util.List;

public class ZCrystal extends BasePolymerItem {
    private final List<FormSetting> formChanges;
    private final String showdownId;

    public ZCrystal(Settings settings, Item polymerItem, PolymerModelData modelData, String itemId, List<String> lore, List<FormSetting> formChanges, String showdownId) {
        super(settings, polymerItem, modelData, itemId, lore);
        this.formChanges = formChanges;
        this.showdownId = showdownId;
    }

    public List<FormSetting> getFormChanges() {
        return formChanges;
    }

    public String getShowdownId() {
        return showdownId;
    }
}
