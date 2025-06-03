package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
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

public class GriseousCore extends SimplePolymerItem {
    PolymerModelData modelData;

    public GriseousCore(Item.Settings settings, Item polymerItem) {
        super(settings, polymerItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get("griseous_core"));
        return super.getPolymerItem(itemStack, player);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        this.modelData = KeyItems.griseousCoreModelData;
        return this.modelData.value();
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("griseous_core")) return ActionResult.PASS;
        if (entity instanceof PokemonEntity pokemonEntity && pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase("giratina")) {
            ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
            if (player != null) {
                if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                    if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("origin"))) {
                        new StringSpeciesFeature("orb_forme", "altered").apply(pokemonEntity.getPokemon());
                    } else {
                        new StringSpeciesFeature("orb_forme", "origin").apply(pokemonEntity.getPokemon());
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
