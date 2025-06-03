package me.unariginal.genesisforms.items.helditems.zcrystals;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.helditem.HeldItemManager;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
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

import java.util.*;

public class ZCrystalHeldItems implements HeldItemManager {
    private static final ZCrystalHeldItems INSTANCE = new ZCrystalHeldItems();
    public static ZCrystalHeldItems getInstance() {
        return INSTANCE;
    }
    private final Map<String, String> Z_CRYSTAL_IDS = new HashMap<>();
    private final Map<String, Map<String, String>> SPECIAL_Z_CRYSTAL_IDS = new HashMap<>();

    public ItemStack getZCrystalItem(String id) {
        if (!Z_CRYSTAL_IDS.containsKey(id) && !SPECIAL_Z_CRYSTAL_IDS.containsKey(id)) return ItemStack.EMPTY;
        return zCrystalPolymerItems.get(id).getDefaultStack();
    }

    @Override
    public void give(@NotNull BattlePokemon battlePokemon, @NotNull String s) {
        battlePokemon.getEffectedPokemon().swapHeldItem(getZCrystalItem(s), false);
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
        if (nbt.isEmpty() || !nbt.contains(DataKeys.NBT_Z_CRYSTAL)) return null;

        String nbtString = nbt.getString(DataKeys.NBT_Z_CRYSTAL);
        if (nbtString.isEmpty()) return null;
        if (!Z_CRYSTAL_IDS.containsKey(nbtString) && !SPECIAL_Z_CRYSTAL_IDS.containsKey(nbtString)) return null;
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

    private ElementalType getType(String name) {
        return ElementalTypes.INSTANCE.get(name);
    }

    private List<Species> getSpecies(String name) {
        List<Species> species = new ArrayList<>();
        for (Map.Entry<String, String> s : SPECIAL_Z_CRYSTAL_IDS.get(name).entrySet()) {
            species.add(PokemonSpecies.INSTANCE.getByName(s.getKey()));
        }
        return species;
    }

    public Set<String> getAllZCrystalIds() {
        Set<String> ids = new HashSet<>(Z_CRYSTAL_IDS.keySet());
        ids.addAll(SPECIAL_Z_CRYSTAL_IDS.keySet());
        return ids;
    }

    public ElementalType getZCrystalType(String id) {
        if (Z_CRYSTAL_IDS.containsKey(id)) {
            return getType(Z_CRYSTAL_IDS.get(id));
        }
        return null;
    }

    public List<Species> getZCrystalSpecies(String id) {
        if (SPECIAL_Z_CRYSTAL_IDS.containsKey(id)) {
            return getSpecies(id);
        }
        return null;
    }

    public String getAspect(String key, String species) {
        if (SPECIAL_Z_CRYSTAL_IDS.containsKey(key)) {
            if (SPECIAL_Z_CRYSTAL_IDS.get(key).containsKey(species)) {
                return SPECIAL_Z_CRYSTAL_IDS.get(key).get(species);
            }
        }
        return null;
    }

    public void loadZCrystalIds() {
        Z_CRYSTAL_IDS.put("buginium-z", "bug");
        Z_CRYSTAL_IDS.put("darkinium-z", "dark");
        Z_CRYSTAL_IDS.put("dragonium-z", "dragon");
        Z_CRYSTAL_IDS.put("electrium-z", "electric");
        Z_CRYSTAL_IDS.put("fairium-z", "fairy");
        Z_CRYSTAL_IDS.put("fightinium-z", "fighting");
        Z_CRYSTAL_IDS.put("firium-z", "fire");
        Z_CRYSTAL_IDS.put("flyinium-z", "flying");
        Z_CRYSTAL_IDS.put("ghostium-z", "ghost");
        Z_CRYSTAL_IDS.put("grassium-z", "grass");
        Z_CRYSTAL_IDS.put("groundium-z", "ground");
        Z_CRYSTAL_IDS.put("icium-z", "ice");
        Z_CRYSTAL_IDS.put("normalium-z", "normal");
        Z_CRYSTAL_IDS.put("poisonium-z", "poison");
        Z_CRYSTAL_IDS.put("psychium-z", "psychic");
        Z_CRYSTAL_IDS.put("rockium-z", "rock");
        Z_CRYSTAL_IDS.put("steelium-z", "steel");
        Z_CRYSTAL_IDS.put("waterium-z", "water");
        loadSpecialCrystals();
    }

    public void loadSpecialCrystals() {
        SPECIAL_Z_CRYSTAL_IDS.put("aloraichium-z", Map.of("raichu", "alolan"));
        SPECIAL_Z_CRYSTAL_IDS.put("decidium-z", Map.of("decidueye", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("eevium-z", Map.of("eevee", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("incinium-z", Map.of("incineroar", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("kommonium-z", Map.of("kommoo", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("lunalium-z", Map.of("lunala", "", "necrozma", "dawn"));
        SPECIAL_Z_CRYSTAL_IDS.put("lycanium-z", Map.of("lycanroc", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("marshadium-z", Map.of("marshadow", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("mewnium-z", Map.of("mew", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("mimikium-z", Map.of("mimikyu", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("pikanium-z", Map.of("pikachu", "normal"));
        SPECIAL_Z_CRYSTAL_IDS.put("pikashunium-z", Map.of("pikachu", "partner"));
        SPECIAL_Z_CRYSTAL_IDS.put("primarium-z", Map.of("primarina", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("snorlium-z", Map.of("snorlax", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("solganium-z", Map.of("solgaleo", "","necrozma", "dusk"));
        SPECIAL_Z_CRYSTAL_IDS.put("tapunium-z", Map.of("tapukoko", "", "tapulele", "", "tapubulu", "", "tapufini", ""));
        SPECIAL_Z_CRYSTAL_IDS.put("ultranecrozium-z", Map.of("necrozma", "ultra"));
    }

    public Map<String, ZCrystalPolymerItem> zCrystalPolymerItems = new HashMap<>();
    public Map<String, PolymerModelData> zCrystalPolymerModelData = new HashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC).fireproof();
    private final Item baseVanillaItem = Items.PRISMARINE_CRYSTALS;

    public void fillPolymerItems() {
        for (String key : Z_CRYSTAL_IDS.keySet()) {
            zCrystalPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new ZCrystalPolymerItem(itemSettings, baseVanillaItem, key)));
        }
        for (String key : SPECIAL_Z_CRYSTAL_IDS.keySet()) {
            zCrystalPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new ZCrystalPolymerItem(itemSettings, baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : Z_CRYSTAL_IDS.keySet()) {
            zCrystalPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }
        for (String key : SPECIAL_Z_CRYSTAL_IDS.keySet()) {
            zCrystalPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }
    }

    public void registerItemGroup() {
        ItemGroup Z_CRYSTALS = FabricItemGroup.builder()
                .icon(zCrystalPolymerItems.get("ultranecrozium-z")::getDefaultStack)
                .displayName(Text.literal("Z Crystals"))
                .entries((displayContext, entries) -> {
                    for (String key : zCrystalPolymerModelData.keySet()) {
                        entries.add(zCrystalPolymerItems.get(key));
                    }
                }).build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "z_crystals"), Z_CRYSTALS);
    }

    public static class ZCrystalPolymerItem extends SimplePolymerItem {
        PolymerModelData modelData;
        String id;

        public ZCrystalPolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
        }

        @Override
        public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
            NbtUtils.setNbtString(itemStack, GenesisForms.MOD_ID, DataKeys.NBT_Z_CRYSTAL, id);
            if (GenesisForms.INSTANCE.getItemSettings().item_lore.containsKey(id)) {
                NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get(id));
            }
            return super.getPolymerItem(itemStack, player);
        }

        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
            this.modelData = ZCrystalHeldItems.getInstance().zCrystalPolymerModelData.get(id);
            return this.modelData.value();
        }
    }
}
