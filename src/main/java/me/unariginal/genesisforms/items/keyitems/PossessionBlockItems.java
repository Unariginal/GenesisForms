package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.blocks.PossessionBlock;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.utils.PokemonUtils;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
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

public class PossessionBlockItems {
    private static final PossessionBlockItems INSTANCE = new PossessionBlockItems();
    public static PossessionBlockItems getInstance() {
        return INSTANCE;
    }

    public record FormInformation(List<String> species, String feature_name, String feature_value) {}
    private final Map<String, FormInformation> POSSESSION_ITEM_IDS = new HashMap<>();

    public ItemStack getPossessionItem(String id) {
        if (!possessionItemPolymerItems.containsKey(id)) return ItemStack.EMPTY;
        return possessionItemPolymerItems.get(id).getDefaultStack();
    }

    public Set<String> getAllPossessionItemIds() {
        return POSSESSION_ITEM_IDS.keySet();
    }

    public FormInformation getPossessionItemForm(String id) {
        if (POSSESSION_ITEM_IDS.containsKey(id)) {
            return POSSESSION_ITEM_IDS.get(id);
        }
        return null;
    }

    public void loadPossessionItemIds() {
        POSSESSION_ITEM_IDS.put("rotom_light_bulb", new FormInformation(List.of("rotom"), "appliance", null));
        POSSESSION_ITEM_IDS.put("rotom_microwave_oven", new FormInformation(List.of("rotom"), "appliance", "heat"));
        POSSESSION_ITEM_IDS.put("rotom_fan", new FormInformation(List.of("rotom"), "appliance", "fan"));
        POSSESSION_ITEM_IDS.put("rotom_refrigerator", new FormInformation(List.of("rotom"), "appliance", "frost"));
        POSSESSION_ITEM_IDS.put("rotom_washing_machine", new FormInformation(List.of("rotom"), "appliance", "wash"));
        POSSESSION_ITEM_IDS.put("rotom_lawn_mower", new FormInformation(List.of("rotom"), "appliance", "mow"));
    }

    public Map<String, PossessionItemPolymerItem> possessionItemPolymerItems = new HashMap<>();
    public Map<String, PossessionBlock> possessionBlocks = new HashMap<>();
    public Map<String, PolymerModelData> possessionItemPolymerModelData = new HashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.RARE).fireproof();
    private final Item baseVanillaItem = Items.IRON_INGOT;

    public void fillPolymerItems() {
        for (String key : POSSESSION_ITEM_IDS.keySet()) {
            possessionBlocks.put(key, Registry.register(Registries.BLOCK, Identifier.of(GenesisForms.MOD_ID, key), new PossessionBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.TERRACOTTA_ORANGE).nonOpaque().solidBlock(Blocks::never)/*, BlockModelType.TRANSPARENT_BLOCK, "block/" + key*/)));
            possessionItemPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new PossessionItemPolymerItem(itemSettings.component(DataComponents.KEY_ITEM, key), possessionBlocks.get(key), key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : POSSESSION_ITEM_IDS.keySet()) {
            possessionItemPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "block/" + key)));
        }
    }

    public static class PossessionItemPolymerItem extends BlockItem implements PolymerItem {
        private final PolymerModelData modelData;
        private final String id;

        public PossessionItemPolymerItem(Settings settings, Block block, String id) {
            super(block, settings);
            this.id = id;
            this.modelData = PossessionBlockItems.getInstance().possessionItemPolymerModelData.get(id);
        }

        @Override
        public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
            return this.modelData.item();
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
            FormInformation formInformation = PossessionBlockItems.getInstance().getPossessionItemForm(id);
            if (entity instanceof PokemonEntity pokemonEntity) {
                boolean match = false;
                Pokemon pokemon = pokemonEntity.getPokemon();
                for (String species : formInformation.species()) {
                    if (pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase(species)) {
                        match = true;
                        break;
                    }
                }
                if (match) {
                    ServerPlayerEntity player = pokemon.getOwnerPlayer();
                    if (player != null) {
                        if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                            boolean alreadyInForm = true;
                            if (formInformation.feature_value != null) {
                                if (pokemon.getAspects().stream().noneMatch(aspect -> aspect.startsWith(formInformation.feature_value()))) {
                                    alreadyInForm = false;
                                    new StringSpeciesFeature(formInformation.feature_name, formInformation.feature_value).apply(pokemon);
                                }
                            } else {
                                if (pokemon.getFeatures().stream().anyMatch(feature -> feature.getName().equalsIgnoreCase(formInformation.feature_name))) {
                                    alreadyInForm = false;
                                    pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(formInformation.feature_name));

                                    PokemonUtils.fixRotomMoves(pokemon);
                                }
                            }

                            pokemon.updateAspects();
                            pokemon.updateForm();

                            if (!alreadyInForm) {
                                NbtCompound data = pokemon.getPersistentData();
                                ItemStack returnItem = ItemStack.EMPTY;
                                if (data.contains("possession_item")) {
                                    String possessionItem = data.getString("possession_item");
                                    if (PossessionBlockItems.getInstance().possessionItemPolymerItems.containsKey(possessionItem)) {
                                        returnItem = PossessionBlockItems.getInstance().getPossessionItem(possessionItem);
                                    }
                                }

                                data.putString("possession_item", id);
                                stack.decrementUnlessCreative(1, player);
                                player.getInventory().offerOrDrop(returnItem);
                                pokemon.setPersistentData$common(data);
                            }
                        }
                    }
                }
            }
            return ActionResult.PASS;
        }
    }
}
