package me.unariginal.genesisforms.items.helditems.zcrystals;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.utils.TextUtils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ZCrystalHeldItems {
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

    public Set<String> getAllZCrystalIds() {
        Set<String> ids = new HashSet<>(Z_CRYSTAL_IDS.keySet());
        ids.addAll(SPECIAL_Z_CRYSTAL_IDS.keySet());
        return ids;
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
        for (String key : getAllZCrystalIds()) {
            zCrystalPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new ZCrystalPolymerItem(itemSettings.component(DataComponents.Z_CRYSTAL, key), baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : getAllZCrystalIds()) {
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
        for (String key : getAllZCrystalIds()) {
            CobblemonHeldItemManager.INSTANCE.registerRemap(zCrystalPolymerItems.get(key), key);
        }
    }

    public static class ZCrystalPolymerItem extends SimplePolymerItem {
        PolymerModelData modelData;
        String id;

        public ZCrystalPolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
            this.modelData = ZCrystalHeldItems.getInstance().zCrystalPolymerModelData.get(id);
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
    }
}
