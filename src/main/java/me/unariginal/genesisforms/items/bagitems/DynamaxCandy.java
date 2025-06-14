package me.unariginal.genesisforms.items.bagitems;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.polymer.BagItems;
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

public class DynamaxCandy extends SimplePolymerItem {
    PolymerModelData modelData;

    public DynamaxCandy(Item.Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = BagItems.dynamaxCandyModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("dynamax_candy")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("dynamax_candy")) return ActionResult.PASS;
        if (GenesisForms.INSTANCE.getConfig().enableDynamax) {
            if (entity instanceof PokemonEntity pokemonEntity) {
                ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
                if (player != null) {
                    if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                        int currentDmaxLevel = pokemonEntity.getPokemon().getDmaxLevel();
                        if (currentDmaxLevel < 10) {
                            pokemonEntity.getPokemon().setDmaxLevel(currentDmaxLevel + 1);
                            if (GenesisForms.INSTANCE.getItemSettings().consumableBagItems.contains("dynamax_candy")) {
                                player.getStackInHand(hand).decrementUnlessCreative(1, player);
                            }
                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("dynamax_level_changed"), pokemonEntity.getPokemon())), true);
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
