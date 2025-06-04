package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.handlers.MegaEvolutionHandler;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

public class MegaCuff extends SimplePolymerItem {
    PolymerModelData modelData;

    public MegaCuff(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get("mega_cuff"));
        NbtUtils.setNbtString(itemStack, GenesisForms.MOD_ID, DataKeys.NBT_KEY_ITEM, "mega_cuff");
        return super.getPolymerItem(itemStack, player);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        this.modelData = KeyItems.megaCuffModelData;
        return this.modelData.value();
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("mega_cuff")) return ActionResult.PASS;
        if (GenesisForms.INSTANCE.getConfig().enableMegaEvolution && GenesisForms.INSTANCE.getConfig().allowMegaOutsideBattles) {
            if (entity instanceof PokemonEntity pokemonEntity) {
                ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
                if (player != null) {
                    boolean isMega = pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"));
                    if (!isMega) {
                        if (!GenesisForms.INSTANCE.getPlayersWithMega().containsKey(player.getUuid())) {
                            MegaEvolutionHandler.megaEvolve(pokemonEntity, player, false);
                        }
                    } else {
                        MegaEvolutionHandler.devolveMega(pokemonEntity.getPokemon(), false);
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
