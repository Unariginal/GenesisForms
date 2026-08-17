package me.unariginal.genesisforms.items.helditems;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.items.BasePolymerItem;
import net.minecraft.item.Item;

import java.util.List;

public class HeldBattleItem extends BasePolymerItem {
    private final String showdownId;

    public HeldBattleItem(Settings settings, Item polymerItem, PolymerModelData modelData, String itemId, List<String> lore, String showdownId) {
        super(settings, polymerItem, modelData, itemId, lore);
        this.showdownId = showdownId;
    }

    public String getShowdownId() {
        return showdownId;
    }
}
