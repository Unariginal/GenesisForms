package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataComponents;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeyFormItems {
    private static final KeyFormItems INSTANCE = new KeyFormItems();
    public static KeyFormItems getInstance() {
        return INSTANCE;
    }

    public record FormInformation(List<String> species, String feature_name, String base_feature_value, String alternative_feature_value) {}
    private final Map<String, FormInformation> KEY_ITEM_IDS = new HashMap<>();

    public ItemStack getKeyItem(String id) {
        if (!KEY_ITEM_IDS.containsKey(id)) return ItemStack.EMPTY;
        return keyItemPolymerItems.get(id).getDefaultStack();
    }

    public Set<String> getAllKeyItemIds() {
        return KEY_ITEM_IDS.keySet();
    }

    public FormInformation getKeyItemForm(String id) {
        if (KEY_ITEM_IDS.containsKey(id)) {
            return KEY_ITEM_IDS.get(id);
        }
        return null;
    }

    public void loadKeyItemIds() {
        KEY_ITEM_IDS.put("adamant_crystal", new FormInformation(List.of("dialga"), "orb_forme", "altered", "origin"));
        KEY_ITEM_IDS.put("gracidea_flower", new FormInformation(List.of("shaymin"), "gracidea_forme", "sky", "land"));
        KEY_ITEM_IDS.put("griseous_core", new FormInformation(List.of("giratina"), "orb_forme", "altered", "origin"));
        KEY_ITEM_IDS.put("lustrous_globe", new FormInformation(List.of("palkia"), "orb_forme", "altered", "origin"));
        KEY_ITEM_IDS.put("pink_nectar", new FormInformation(List.of("oricorio"), "dance_style", "pa'u", null));
        KEY_ITEM_IDS.put("prison_bottle", new FormInformation(List.of("hoopa"), "djinn_state", "confined", "unbound"));
        KEY_ITEM_IDS.put("purple_nectar", new FormInformation(List.of("oricorio"), "dance_style", "sensu", null));
        KEY_ITEM_IDS.put("red_nectar", new FormInformation(List.of("oricorio"), "dance_style", "baile", null));
        KEY_ITEM_IDS.put("reveal_glass", new FormInformation(List.of("landorus", "thundurus", "tornadus", "enamorus"), "mirror_forme", "incarnate", "therian"));
        KEY_ITEM_IDS.put("yellow_nectar", new FormInformation(List.of("oricorio"), "dance_style", "pom_pom", null));
        for (String key : GenesisForms.INSTANCE.getItemSettings().custom_key_form_items.keySet()) {
            FormInformation form = GenesisForms.INSTANCE.getItemSettings().custom_key_form_items.get(key);
            KEY_ITEM_IDS.put(key, form);
        }
    }

    public Map<String, KeyItemPolymerItem> keyItemPolymerItems = new HashMap<>();
    public Map<String, PolymerModelData> keyItemPolymerModelData = new HashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof();
    private final Item baseVanillaItem = Items.DIAMOND;

    public void fillPolymerItems() {
        for (String key : KEY_ITEM_IDS.keySet()) {
            keyItemPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new KeyItemPolymerItem(itemSettings.component(DataComponents.KEY_ITEM, key), baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : KEY_ITEM_IDS.keySet()) {
            keyItemPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }
    }

    public static class KeyItemPolymerItem extends SimplePolymerItem {
        PolymerModelData modelData;
        String id;

        public KeyItemPolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
            this.modelData = KeyFormItems.getInstance().keyItemPolymerModelData.get(id);
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
            FormInformation formInformation = KeyFormItems.getInstance().getKeyItemForm(id);
            if (entity instanceof PokemonEntity pokemonEntity) {
                boolean match = false;
                for (String species : formInformation.species()) {
                    if (pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase(species)) {
                        match = true;
                        break;
                    }
                }
                if (match) {
                    ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
                    if (player != null) {
                        if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                            if (formInformation.alternative_feature_value() != null) {
                                if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith(formInformation.alternative_feature_value()))) {
                                    new StringSpeciesFeature(formInformation.feature_name(), formInformation.base_feature_value()).apply(pokemonEntity.getPokemon());
                                } else {
                                    new StringSpeciesFeature(formInformation.feature_name(), formInformation.alternative_feature_value()).apply(pokemonEntity.getPokemon());
                                }
                                if (GenesisForms.INSTANCE.getItemSettings().consumableKeyItems.contains(id)) {
                                    stack.decrementUnlessCreative(1, player);
                                }
                            } else {
                                if (pokemonEntity.getPokemon().getAspects().stream().noneMatch(aspect -> aspect.startsWith(formInformation.base_feature_value()))) {
                                    new StringSpeciesFeature(formInformation.feature_name(), formInformation.base_feature_value()).apply(pokemonEntity.getPokemon());
                                    if (GenesisForms.INSTANCE.getItemSettings().consumableKeyItems.contains(id)) {
                                        stack.decrementUnlessCreative(1, player);
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
