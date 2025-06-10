package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
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

public class Meteorite extends SimplePolymerItem {
    PolymerModelData modelData;

    public Meteorite(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = KeyItems.meteoriteModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("meteorite")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("meteorite")) return ActionResult.PASS;
        if (entity instanceof PokemonEntity pokemonEntity && pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase("deoxys")) {
            ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
            if (player != null) {
                if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                    if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("attack"))) {
                        new StringSpeciesFeature("meteorite_forme", "defense").apply(pokemonEntity.getPokemon());
                    } else if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("defense"))) {
                        new StringSpeciesFeature("meteorite_forme", "speed").apply(pokemonEntity.getPokemon());
                    } else if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("speed"))) {
                        new StringSpeciesFeature("meteorite_forme", "normal").apply(pokemonEntity.getPokemon());
                    } else {
                        new StringSpeciesFeature("meteorite_forme", "attack").apply(pokemonEntity.getPokemon());
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
