package me.unariginal.genesisforms.items;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BasePolymerItem extends SimplePolymerItem {
    private final PolymerModelData modelData;
    protected final String itemID;
    private final List<String> lore;

    public BasePolymerItem(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore) {
        super(settings, polymerItem);
        this.modelData = modelData;
        this.itemID = itemID;
        this.lore = lore;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : lore) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    public String getItemID() {
        return itemID;
    }
}
