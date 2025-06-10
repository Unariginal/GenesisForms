package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.ItemSettingsConfig;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.utils.PokemonUtils;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FusionItems {
    private static final FusionItems INSTANCE = new FusionItems();
    public static FusionItems getInstance() {
        return INSTANCE;
    }

    private final List<String> FUSION_ITEM_IDS = new ArrayList<>();

    public ItemStack getFusionItem(String id) {
        if (!fusionItemPolymerItems.containsKey(id)) return ItemStack.EMPTY;
        return fusionItemPolymerItems.get(id).getDefaultStack();
    }

    public List<String> getAllFusionItemIds() {
        return FUSION_ITEM_IDS;
    }

    public void loadFusionItemIds() {
        FUSION_ITEM_IDS.clear();
        FUSION_ITEM_IDS.addAll(GenesisForms.INSTANCE.getItemSettings().fusionList.keySet());
    }

    public Map<String, FusionItemPolymerItem> fusionItemPolymerItems = new HashMap<>();
    public Map<String, PolymerModelData> fusionItemPolymerModelData = new HashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof();
    private final Item baseVanillaItem = Items.DIAMOND;

    public void fillPolymerItems() {
        for (String key : FUSION_ITEM_IDS) {
            fusionItemPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new FusionItemPolymerItem(itemSettings.component(DataComponents.KEY_ITEM, key), baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : FUSION_ITEM_IDS) {
            fusionItemPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }
    }

    public static class FusionItemPolymerItem extends SimplePolymerItem {
        PolymerModelData modelData;
        String id;

        public FusionItemPolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
            this.modelData = FusionItems.getInstance().fusionItemPolymerModelData.get(id);
        }

        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
            return this.modelData.value();
        }

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get(id)) {
                tooltip.add(TextUtils.deserialize(line));
            }
        }

        @Override
        public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
            if (GenesisForms.INSTANCE.getConfig().disabledItems.contains(id)) return ActionResult.PASS;
            if (GenesisForms.INSTANCE.getConfig().enableFusions) {
                if (entity instanceof PokemonEntity pokemonEntity) {
                    if (GenesisForms.INSTANCE.getItemSettings().fusionList.containsKey(id)) {
                        for (ItemSettingsConfig.Fusion fusion : GenesisForms.INSTANCE.getItemSettings().fusionList.get(id)) {
                            if (pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase(fusion.corePokemon())) {
                                ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
                                if (player != null) {
                                    if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                                        PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
                                        boolean isFused = false;
                                        for (ItemSettingsConfig.FuelPokemon fuelPokemon : fusion.fuelPokemon()) {
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
                                                    for (ItemSettingsConfig.FuelPokemon fuelPokemon : fusion.fuelPokemon()) {
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
}
