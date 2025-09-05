package me.unariginal.genesisforms.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BasePolymerBlockItem extends BlockItem implements PolymerItem {
    private final PolymerModelData modelData;
    protected final String itemID;
    private final List<String> lore;

    public BasePolymerBlockItem(Block block, Settings settings, PolymerModelData modelData, String itemID, List<String> lore) {
        super(block, settings);
        this.modelData = modelData;
        this.itemID = itemID;
        this.lore = lore;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.modelData.item();
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
