package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.handlers.MegaEvolutionHandler;
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

public class MegaRing extends SimplePolymerItem {
    PolymerModelData modelData;

    public MegaRing(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = KeyItems.megaRingModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("mega_ring")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("mega_ring")) return ActionResult.PASS;
        if (GenesisForms.INSTANCE.getConfig().enableMegaEvolution && GenesisForms.INSTANCE.getConfig().allowMegaOutsideBattles) {
            if (entity instanceof PokemonEntity pokemonEntity) {
                ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
                if (player != null) {
                    if (player.getUuid().equals(user.getUuid())) {
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
        }
        return ActionResult.PASS;
    }
}
