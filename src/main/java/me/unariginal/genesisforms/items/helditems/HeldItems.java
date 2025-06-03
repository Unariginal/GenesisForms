package me.unariginal.genesisforms.items.helditems;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.helditem.HeldItemManager;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.utils.NbtUtils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HeldItems implements HeldItemManager {
    private static final HeldItems INSTANCE = new HeldItems();
    public static HeldItems getInstance() {
        return INSTANCE;
    }

    private final Map<String, String> HELD_ITEM_IDS = new HashMap<>();

    public ItemStack getHeldItem(String id) {
        if (!HELD_ITEM_IDS.containsKey(id)) return ItemStack.EMPTY;
        return heldItemPolymerItems.get(id).getDefaultStack();
    }

    @Override
    public void give(@NotNull BattlePokemon battlePokemon, @NotNull String s) {
        battlePokemon.getEffectedPokemon().swapHeldItem(getHeldItem(s), false);
    }

    @Override
    public void handleEndInstruction(
            @NotNull BattlePokemon battlePokemon,
            @NotNull PokemonBattle pokemonBattle,
            @NotNull BattleMessage battleMessage
    ) { }

    @Override
    public void handleStartInstruction(
            @NotNull BattlePokemon battlePokemon,
            @NotNull PokemonBattle pokemonBattle,
            @NotNull BattleMessage battleMessage
    ) { }

    @NotNull
    @Override
    public Text nameOf(@NotNull String s) {
        return Text.of(s);
    }

    @Override
    public boolean shouldConsumeItem(
            @NotNull BattlePokemon battlePokemon,
            @NotNull PokemonBattle pokemonBattle,
            @NotNull String s
    ) {
        return false;
    }

    public String showdownId(Pokemon pokemon) {
        NbtCompound nbt = NbtUtils.getNbt(pokemon.heldItem(), GenesisForms.MOD_ID);
        if (nbt.isEmpty() || !nbt.contains(DataKeys.NBT_HELD_ITEM)) return null;

        String nbtString = nbt.getString(DataKeys.NBT_HELD_ITEM);
        if (nbtString.isEmpty()) return null;
        if (!HELD_ITEM_IDS.containsKey(nbtString)) return null;
        return nbtString;
    }

    @Nullable
    @Override
    public String showdownId(@NotNull BattlePokemon battlePokemon) {
        return showdownId(battlePokemon.getEffectedPokemon());
    }

    @Override
    public void take(@NotNull BattlePokemon battlePokemon, @NotNull String s) {
        battlePokemon.getEffectedPokemon().removeHeldItem();
    }

    private Species getSpecies(String name) {
        return PokemonSpecies.INSTANCE.getByName(name);
    }

    public Set<String> getAllHeldItemIds() {
        return HELD_ITEM_IDS.keySet();
    }

    public Species getHeldItemSpecies(String id) {
        if (HELD_ITEM_IDS.containsKey(id)) {
            return getSpecies(HELD_ITEM_IDS.get(id));
        }
        return null;
    }

    public void loadHeldItemIds() {
        HELD_ITEM_IDS.put("rusted_sword", "zacian");
        HELD_ITEM_IDS.put("rusted_shield", "zamazenta");

        HELD_ITEM_IDS.put("insect_plate", "arceus");
        HELD_ITEM_IDS.put("dread_plate", "arceus");
        HELD_ITEM_IDS.put("draco_plate", "arceus");
        HELD_ITEM_IDS.put("zap_plate", "arceus");
        HELD_ITEM_IDS.put("pixie_plate", "arceus");
        HELD_ITEM_IDS.put("fist_plate", "arceus");
        HELD_ITEM_IDS.put("flame_plate", "arceus");
        HELD_ITEM_IDS.put("sky_plate", "arceus");
        HELD_ITEM_IDS.put("spooky_plate", "arceus");
        HELD_ITEM_IDS.put("meadow_plate", "arceus");
        HELD_ITEM_IDS.put("earth_plate", "arceus");
        HELD_ITEM_IDS.put("icicle_plate", "arceus");
        HELD_ITEM_IDS.put("toxic_plate", "arceus");
        HELD_ITEM_IDS.put("mind_plate", "arceus");
        HELD_ITEM_IDS.put("stone_plate", "arceus");
        HELD_ITEM_IDS.put("iron_plate", "arceus");
        HELD_ITEM_IDS.put("splash_plate", "arceus");

        HELD_ITEM_IDS.put("bug_memory", "silvally");
        HELD_ITEM_IDS.put("dark_memory", "silvally");
        HELD_ITEM_IDS.put("dragon_memory", "silvally");
        HELD_ITEM_IDS.put("electric_memory", "silvally");
        HELD_ITEM_IDS.put("fairy_memory", "silvally");
        HELD_ITEM_IDS.put("fighting_memory", "silvally");
        HELD_ITEM_IDS.put("fire_memory", "silvally");
        HELD_ITEM_IDS.put("flying_memory", "silvally");
        HELD_ITEM_IDS.put("ghost_memory", "silvally");
        HELD_ITEM_IDS.put("grass_memory", "silvally");
        HELD_ITEM_IDS.put("ground_memory", "silvally");
        HELD_ITEM_IDS.put("ice_memory", "silvally");
        HELD_ITEM_IDS.put("poison_memory", "silvally");
        HELD_ITEM_IDS.put("psychic_memory", "silvally");
        HELD_ITEM_IDS.put("rock_memory", "silvally");
        HELD_ITEM_IDS.put("steel_memory", "silvally");
        HELD_ITEM_IDS.put("water_memory", "silvally");

        HELD_ITEM_IDS.put("teal_mask", "ogerpon");
        HELD_ITEM_IDS.put("wellspring_mask", "ogerpon");
        HELD_ITEM_IDS.put("hearthflame_mask", "ogerpon");
        HELD_ITEM_IDS.put("cornerstone_mask", "ogerpon");

        HELD_ITEM_IDS.put("red_orb", "groudon");
        HELD_ITEM_IDS.put("blue_orb", "kyogre");

        HELD_ITEM_IDS.put("shock_drive", "genesect");
        HELD_ITEM_IDS.put("burn_drive", "genesect");
        HELD_ITEM_IDS.put("chill_drive", "genesect");
        HELD_ITEM_IDS.put("douse_drive", "genesect");
    }

    public Map<String, HeldItemPolymerItem> heldItemPolymerItems = new HashMap<>();
    public Map<String, PolymerModelData> heldItemPolymerModelData = new HashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC).fireproof();
    private final Item baseVanillaItem = Items.EMERALD;

    public void fillPolymerItems() {
        for (String key : HELD_ITEM_IDS.keySet()) {
            heldItemPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new HeldItemPolymerItem(itemSettings, baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : HELD_ITEM_IDS.keySet()) {
            heldItemPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }
    }

    public void registerItemGroup() {
        ItemGroup HELD_ITEMS = FabricItemGroup.builder()
                .icon(heldItemPolymerItems.get("rusted_sword")::getDefaultStack)
                .displayName(Text.literal("Held Items"))
                .entries((displayContext, entries) -> {
                    for (String key : heldItemPolymerModelData.keySet()) {
                        entries.add(heldItemPolymerItems.get(key));
                    }
                }).build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "held_items"), HELD_ITEMS);
    }

    public static class HeldItemPolymerItem extends SimplePolymerItem {
        PolymerModelData modelData;
        String id;

        public HeldItemPolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
        }

        @Override
        public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
            if (GenesisForms.INSTANCE.getItemSettings().item_lore.containsKey(id)) {
                NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get(id));
            }
            NbtUtils.setNbtString(itemStack, GenesisForms.MOD_ID, DataKeys.NBT_HELD_ITEM, id);
            return super.getPolymerItem(itemStack, player);
        }

        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
            this.modelData = HeldItems.getInstance().heldItemPolymerModelData.get(id);
            return this.modelData.value();
        }
    }
}
