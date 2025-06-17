package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DynamaxBand extends SimplePolymerItem {
    PolymerModelData modelData;

    public DynamaxBand(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = KeyItems.dynamaxBandModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("dynamax_band")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("dynamax_band") || !GenesisForms.INSTANCE.getConfig().enableDynamax || !GenesisForms.INSTANCE.getConfig().enableGigantamax) return ActionResult.PASS;
        if (entity instanceof PokemonEntity pokemonEntity) {
            ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
            if (player != null) {
                if (player.getUuid().equals(user.getUuid())) {
                    if (pokemonEntity.getPokemon().getGmaxFactor()) {
                        player.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("has_gmax_factor"), pokemonEntity.getPokemon())), true);
                    } else {
                        player.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("does_not_have_gmax_factor"), pokemonEntity.getPokemon())), true);
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
