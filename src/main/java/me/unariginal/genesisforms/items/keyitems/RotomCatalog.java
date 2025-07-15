package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.PokemonUtils;
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

public class RotomCatalog extends SimplePolymerItem {
    PolymerModelData modelData;

    public RotomCatalog(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = KeyItems.rotomCatalogModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("rotom_catalog")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("rotom_catalog")) return ActionResult.PASS;
        if (entity instanceof PokemonEntity pokemonEntity && pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase("rotom")) {
            Pokemon pokemon = pokemonEntity.getPokemon();
            ServerPlayerEntity player = pokemon.getOwnerPlayer();
            if (player != null) {
                if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                    if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.equals("heat-appliance"))) {
                        new StringSpeciesFeature("appliance", "wash").apply(pokemon);
                    } else if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.equals("wash-appliance"))) {
                        new StringSpeciesFeature("appliance", "frost").apply(pokemon);
                    } else if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.equals("frost-appliance"))) {
                        new StringSpeciesFeature("appliance", "fan").apply(pokemon);
                    } else if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.equals("fan-appliance"))) {
                        new StringSpeciesFeature("appliance", "mow").apply(pokemon);
                    } else if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.equals("mow-appliance"))) {
                        pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase("appliance"));
                        PokemonUtils.fixRotomMoves(pokemon);
                    } else {
                        new StringSpeciesFeature("appliance", "heat").apply(pokemon);
                    }

                    pokemon.updateAspects();
                    pokemon.updateForm();

                    if (GenesisForms.INSTANCE.getItemSettings().consumableKeyItems.contains("rotom_catalog")) {
                        stack.decrementUnlessCreative(1, player);
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}