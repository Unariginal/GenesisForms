package me.unariginal.genesisforms.items;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import net.minecraft.item.Item;

import java.util.List;

public class ConsumablePolymerItem extends BasePolymerItem {
    protected final boolean consumable;

    public ConsumablePolymerItem(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore, boolean consumable) {
        super(settings, polymerItem, modelData, itemID, lore);
        this.consumable = consumable;
    }
}
