package me.unariginal.genesisforms.items.keyitems.accessories;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.handlers.CobblemonEventHandler;
import me.unariginal.genesisforms.items.BasePolymerItem;
import me.unariginal.genesisforms.items.helditems.Megastone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MegaAccessory extends BasePolymerItem implements PokemonSelectingItem {
    public MegaAccessory(Settings settings, Item polymerItem, PolymerModelData modelData, String itemID, List<String> lore) {
        super(settings, polymerItem, modelData, itemID, lore);
    }

    @Override
    public @Nullable BagItem getBagItem() {
        return null;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            return this.use(player, player.getStackInHand(hand));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public @Nullable TypedActionResult<ItemStack> applyToPokemon(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull ItemStack itemStack, @NotNull Pokemon pokemon) {
        if (!canUseOnPokemon(itemStack, pokemon)) return TypedActionResult.fail(itemStack);
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains(itemID) ||
                !(GenesisForms.INSTANCE.getConfig().enableMegaEvolution && GenesisForms.INSTANCE.getConfig().allowMegaOutsideBattles)
        ) return TypedActionResult.fail(itemStack);

        boolean isMega = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"));
        if (!isMega) {
            if (!GenesisForms.INSTANCE.getPlayersWithMega().containsKey(serverPlayerEntity.getUuid())) {
                CobblemonEventHandler.megaEvolveLogic(pokemon);
            }
        } else {
            Item helditem = pokemon.heldItem().getItem();
            if (helditem instanceof Megastone megastone) {
                CobblemonEventHandler.revertMega(pokemon, megastone.getMegastoneData().featureName);
            } else {
                CobblemonEventHandler.revertMega(pokemon, "mega_evolution");
            }
        }

        pokemon.updateAspects();
        pokemon.updateForm();

        return TypedActionResult.success(itemStack);
    }

    @Override
    public boolean canUseOnPokemon(@NotNull ItemStack stack, @NotNull Pokemon pokemon) {
        Item helditem = pokemon.heldItem().getItem();
        if (helditem instanceof Megastone megastone) {
            return megastone.getSpecies().equals(pokemon.getSpecies());
        } else if (pokemon.getSpecies().getName().equalsIgnoreCase("rayquaza")) {
            for (Move move : pokemon.getMoveSet()) {
                if (move.getTemplate().getName().equalsIgnoreCase("dragonascent")) {
                    return true;
                }
            }
        }
        return false;
    }
}
