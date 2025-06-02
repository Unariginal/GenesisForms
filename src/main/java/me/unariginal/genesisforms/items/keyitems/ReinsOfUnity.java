package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.*;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import me.unariginal.genesisforms.utils.PokemonUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

public class ReinsOfUnity extends SimplePolymerItem {
    PolymerModelData modelData;

    public ReinsOfUnity(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get("reins_of_unity"));
        return super.getPolymerItem(itemStack, player);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        this.modelData = KeyItems.reinsOfUnityModelData;
        return this.modelData.value();
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().enableFusions) {
            if (entity instanceof PokemonEntity pokemonEntity && pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase("calyrex")) {
                ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
                if (player != null) {
                    if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                        PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
                        if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("ice"))) {
                            PokemonProperties properties = PokemonUtils.loadFromNBT(pokemonEntity.getPokemon().getPersistentData());
                            properties.setSpecies("glastrier");
                            new StringSpeciesFeature("king_steed", "none").apply(pokemonEntity.getPokemon());
                            partyStore.add(properties.create());
                        } else if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("shadow"))) {
                            PokemonProperties properties = PokemonUtils.loadFromNBT(pokemonEntity.getPokemon().getPersistentData());
                            properties.setSpecies("spectrier");
                            new StringSpeciesFeature("king_steed", "none").apply(pokemonEntity.getPokemon());
                            partyStore.add(properties.create());
                        } else {
                            for (Pokemon pokemon : partyStore) {
                                if (pokemon != null) {
                                    if (pokemon.getSpecies().getName().equalsIgnoreCase("glastrier")) {
                                        new StringSpeciesFeature("king_steed", "ice").apply(pokemonEntity.getPokemon());
                                        pokemonEntity.getPokemon().setPersistentData$common(PokemonUtils.saveToNBT(pokemon.createPokemonProperties(PokemonPropertyExtractor.ALL)));
                                        partyStore.remove(pokemon);
                                        break;
                                    }
                                    if (pokemon.getSpecies().getName().equalsIgnoreCase("spectrier")) {
                                        new StringSpeciesFeature("king_steed", "shadow").apply(pokemonEntity.getPokemon());
                                        pokemonEntity.getPokemon().setPersistentData$common(PokemonUtils.saveToNBT(pokemon.createPokemonProperties(PokemonPropertyExtractor.ALL)));
                                        partyStore.remove(pokemon);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
