package me.unariginal.genesisforms.items.bagitems;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.polymer.BagItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

public class DynamaxCandy extends SimplePolymerItem {
    PolymerModelData modelData;

    public DynamaxCandy(Item.Settings settings, Item polymerItem) {
        super(settings, polymerItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get("dynamax_candy"));
        return super.getPolymerItem(itemStack, player);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        this.modelData = BagItems.dynamaxCandyModelData;
        return this.modelData.value();
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().enableDynamax) {
            if (entity instanceof PokemonEntity pokemonEntity) {
                ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
                if (player != null) {
                    if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                        int currentDmaxLevel = pokemonEntity.getPokemon().getDmaxLevel();
                        if (currentDmaxLevel < 10) {
                            pokemonEntity.getPokemon().setDmaxLevel(currentDmaxLevel + 1);
                            player.getStackInHand(hand).decrement(1);
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
