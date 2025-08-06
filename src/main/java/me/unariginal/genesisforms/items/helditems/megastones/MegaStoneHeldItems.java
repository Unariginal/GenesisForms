package me.unariginal.genesisforms.items.helditems.megastones;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.Config;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MegaStoneHeldItems {
    private static final MegaStoneHeldItems INSTANCE = new MegaStoneHeldItems();
    public static MegaStoneHeldItems getInstance() {
        return INSTANCE;
    }
    private final Map<String, String> MEGA_STONE_IDS = new HashMap<>();

    public ItemStack getMegaStoneItem(String id) {
        if (!MEGA_STONE_IDS.containsKey(id)) return ItemStack.EMPTY;
        return megaStonePolymerItems.get(id).getDefaultStack();
    }

    private Species getSpecies(String name) {
        return PokemonSpecies.INSTANCE.getByName(name);
    }

    public Set<String> getAllMegaStoneIds() {
        return MEGA_STONE_IDS.keySet();
    }

    public Species getMegaStoneSpecies(String id) {
        if (MEGA_STONE_IDS.containsKey(id)) {
            return getSpecies(MEGA_STONE_IDS.get(id));
        }
        return null;
    }

    public void loadMegaStoneIds() {
        MEGA_STONE_IDS.put("venusaurite", "venusaur");
        MEGA_STONE_IDS.put("charizardite-x", "charizard");
        MEGA_STONE_IDS.put("charizardite-y", "charizard");
        MEGA_STONE_IDS.put("blastoisinite", "blastoise");
        MEGA_STONE_IDS.put("alakazite", "alakazam");
        MEGA_STONE_IDS.put("gengarite", "gengar");
        MEGA_STONE_IDS.put("kangaskhanite", "kangaskhan");
        MEGA_STONE_IDS.put("pinsirite", "pinsir");
        MEGA_STONE_IDS.put("gyaradosite", "gyarados");
        MEGA_STONE_IDS.put("aerodactylite", "aerodactyl");
        MEGA_STONE_IDS.put("mewtwonite-x", "mewtwo");
        MEGA_STONE_IDS.put("mewtwonite-y", "mewtwo");
        MEGA_STONE_IDS.put("ampharosite", "ampharos");
        MEGA_STONE_IDS.put("scizorite", "scizor");
        MEGA_STONE_IDS.put("heracronite", "heracross");
        MEGA_STONE_IDS.put("houndoominite", "houndoom");
        MEGA_STONE_IDS.put("tyranitarite", "tyranitar");
        MEGA_STONE_IDS.put("blazikenite", "blaziken");
        MEGA_STONE_IDS.put("gardevoirite", "gardevoir");
        MEGA_STONE_IDS.put("mawilite", "mawile");
        MEGA_STONE_IDS.put("aggronite", "aggron");
        MEGA_STONE_IDS.put("medichamite", "medicham");
        MEGA_STONE_IDS.put("manectite", "manectric");
        MEGA_STONE_IDS.put("banettite", "banette");
        MEGA_STONE_IDS.put("absolite", "absol");
        MEGA_STONE_IDS.put("latiasite", "latias");
        MEGA_STONE_IDS.put("latiosite", "latios");
        MEGA_STONE_IDS.put("garchompite", "garchomp");
        MEGA_STONE_IDS.put("lucarionite", "lucario");
        MEGA_STONE_IDS.put("abomasite", "abomasnow");
        MEGA_STONE_IDS.put("beedrillite", "beedrill");
        MEGA_STONE_IDS.put("pidgeotite", "pidgeot");
        MEGA_STONE_IDS.put("slowbronite", "slowbro");
        MEGA_STONE_IDS.put("steelixite", "steelix");
        MEGA_STONE_IDS.put("sceptilite", "sceptile");
        MEGA_STONE_IDS.put("swampertite", "swampert");
        MEGA_STONE_IDS.put("sablenite", "sableye");
        MEGA_STONE_IDS.put("sharpedonite", "sharpedo");
        MEGA_STONE_IDS.put("cameruptite", "camerupt");
        MEGA_STONE_IDS.put("altarianite", "altaria");
        MEGA_STONE_IDS.put("glalitite", "glalie");
        MEGA_STONE_IDS.put("salamencite", "salamence");
        MEGA_STONE_IDS.put("metagrossite", "metagross");
        MEGA_STONE_IDS.put("lopunnite", "lopunny");
        MEGA_STONE_IDS.put("galladite", "gallade");
        MEGA_STONE_IDS.put("audinite", "audino");
        MEGA_STONE_IDS.put("diancite", "diancie");

        for (Config.CustomMega customMega : GenesisForms.INSTANCE.getConfig().customMegaList) {
            MEGA_STONE_IDS.put(customMega.megastoneID(), customMega.baseSpecies());
        }
    }

    public Map<String, MegaStonePolymerItem> megaStonePolymerItems = new HashMap<>();
    public Map<String, PolymerModelData> megaStonePolymerModelData = new HashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC).fireproof();
    private final Item baseVanillaItem = Items.EMERALD;

    public void fillPolymerItems() {
        for (String key : MEGA_STONE_IDS.keySet()) {
            megaStonePolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new MegaStonePolymerItem(itemSettings.component(DataComponents.MEGA_STONE, key), baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : MEGA_STONE_IDS.keySet()) {
            megaStonePolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }
    }

    public void registerItemGroup() {
        ItemGroup MEGA_STONES = FabricItemGroup.builder()
                .icon(megaStonePolymerItems.get("venusaurite")::getDefaultStack)
                .displayName(Text.literal("Mega Stones"))
                .entries((displayContext, entries) -> {
                    for (String key : MEGA_STONE_IDS.keySet()) {
                        entries.add(megaStonePolymerItems.get(key));
                    }
                }).build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "mega_stones"), MEGA_STONES);
        for (String key : MEGA_STONE_IDS.keySet()) {
            CobblemonHeldItemManager.INSTANCE.registerRemap(megaStonePolymerItems.get(key), key);
        }
    }

    public static class MegaStonePolymerItem extends SimplePolymerItem {
        PolymerModelData modelData;
        String id;

        public MegaStonePolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
            this.modelData = MegaStoneHeldItems.getInstance().megaStonePolymerModelData.get(id);
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
