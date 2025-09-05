package me.unariginal.genesisforms.items.helditems;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.items.BasePolymerItem;
import net.minecraft.item.Item;

import java.util.List;

public class HeldBattleItem extends BasePolymerItem {
    private final String showdownID;

    public HeldBattleItem(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore, String showdownID) {
        super(settings, polymerItem, modelData, itemID, lore);
        this.showdownID = showdownID;
    }

    public String getShowdownID() {
        return showdownID;
    }
}
