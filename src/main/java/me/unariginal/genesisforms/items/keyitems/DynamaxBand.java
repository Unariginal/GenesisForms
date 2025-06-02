package me.unariginal.genesisforms.items.keyitems;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class DynamaxBand extends SimplePolymerItem {
    PolymerModelData modelData;

    public DynamaxBand(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get("dynamax_band"));
        NbtUtils.setNbtString(itemStack, GenesisForms.MOD_ID, DataKeys.NBT_KEY_ITEM, "dynamax_band");
        return super.getPolymerItem(itemStack, player);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        this.modelData = KeyItems.dynamaxBandModelData;
        return this.modelData.value();
    }

//    @Override
//    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
//        if (entity instanceof PokemonEntity pokemonEntity) {
//            ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
//            if (player != null) {
//                boolean isMega = pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"));
//                if (!isMega) {
//                    if (!GenesisForms.INSTANCE.getPlayersWithMega().containsKey(player.getUuid())) {
//                        MegaEvolution.evolve(pokemonEntity, player, false);
//                    }
//                } else {
//                    MegaEvolution.devolve(pokemonEntity.getPokemon(), false);
//                }
//            }
//        }
//        return ActionResult.PASS;
//    }
}
