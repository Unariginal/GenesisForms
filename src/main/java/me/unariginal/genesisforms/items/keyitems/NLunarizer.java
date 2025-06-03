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
import me.unariginal.genesisforms.config.Config;
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

public class NLunarizer extends SimplePolymerItem {
    PolymerModelData modelData;

    public NLunarizer(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get("n_lunarizer"));
        return super.getPolymerItem(itemStack, player);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        this.modelData = KeyItems.nLunarizerModelData;
        return this.modelData.value();
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("n_lunarizer")) return ActionResult.PASS;
        if (GenesisForms.INSTANCE.getConfig().enableFusions) {
            if (entity instanceof PokemonEntity pokemonEntity) {
                for (Config.Fusion fusion : GenesisForms.INSTANCE.getConfig().fusionList) {
                    if (fusion.fusionItem().equalsIgnoreCase("n_lunarizer")) {
                        if (pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase(fusion.corePokemon())) {
                            ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
                            if (player != null) {
                                if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                                    PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
                                    boolean isFused = false;
                                    for (Config.FuelPokemon fuelPokemon : fusion.fuelPokemon()) {
                                        if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith(fuelPokemon.featureValue()))) {
                                            isFused = true;

                                            PokemonProperties properties = PokemonUtils.loadFromNBT(pokemonEntity.getPokemon().getPersistentData());
                                            properties.setSpecies(fuelPokemon.species());
                                            new StringSpeciesFeature(fuelPokemon.featureName(), "none").apply(pokemonEntity.getPokemon());
                                            partyStore.add(properties.create());

                                            break;
                                        }
                                    }
                                    if (!isFused) {
                                        partyLoop:
                                        for (Pokemon pokemon : partyStore) {
                                            if (pokemon != null) {
                                                for (Config.FuelPokemon fuelPokemon : fusion.fuelPokemon()) {
                                                    if (pokemon.getSpecies().getName().equalsIgnoreCase(fuelPokemon.species())) {
                                                        new StringSpeciesFeature(fuelPokemon.featureName(), fuelPokemon.featureValue()).apply(pokemonEntity.getPokemon());
                                                        pokemonEntity.getPokemon().setPersistentData$common(PokemonUtils.saveToNBT(pokemon.createPokemonProperties(PokemonPropertyExtractor.ALL)));
                                                        partyStore.remove(pokemon);
                                                        break partyLoop;
                                                    }
                                                }
                                            }
                                        }
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
