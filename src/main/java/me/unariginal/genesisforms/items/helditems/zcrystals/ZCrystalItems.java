package me.unariginal.genesisforms.items.helditems.zcrystals;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.data.FormSetting;
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

public class ZCrystalItems {
    private static final ZCrystalItems INSTANCE = new ZCrystalItems();
    public static ZCrystalItems getInstance() {
        return INSTANCE;
    }

    public final LinkedHashMap<String, TypedZCrystalItem> typedZCrystalItems = new LinkedHashMap<>();
    public final LinkedHashMap<String, SpeciesZCrystalItem> speciesZCrystalItems = new LinkedHashMap<>();

    public void register() {
        loadTypedZCrystals();
        loadSpeciesZCrystals();

        fillPolymerModelData();
        fillPolymerItems();
        registerItemGroup();
    }

    public ItemStack getZCrystalItem(String id) {
        if (!typedZCrystalItems.containsKey(id) && !speciesZCrystalItems.containsKey(id)) return ItemStack.EMPTY;
        return zCrystalPolymerItems.get(id).getDefaultStack();
    }

    public Set<String> getAllZCrystalIds() {
        Set<String> ids = new HashSet<>(typedZCrystalItems.keySet());
        ids.addAll(speciesZCrystalItems.keySet());
        return ids;
    }

    public String getShowdownID(String id) {
        if (speciesZCrystalItems.containsKey(id)) return speciesZCrystalItems.get(id).showdownID();
        if (typedZCrystalItems.containsKey(id)) return typedZCrystalItems.get(id).showdownID();
        return id.replaceAll("_", "").replaceAll("-", "").toLowerCase();
    }

    public List<String> getLore(String id) {
        if (GenesisForms.INSTANCE.getItemSettings().item_lore.containsKey(id))
            return GenesisForms.INSTANCE.getItemSettings().item_lore.get(id);
        /* TODO: Move item lore
        if (speciesZCrystalItems.containsKey(id)) return speciesZCrystalItems.get(id).itemLore();
        if (typedZCrystalItems.containsKey(id)) return typedZCrystalItems.get(id).itemLore();
         */
        return List.of();
    }

    public void loadTypedZCrystals() {
        typedZCrystalItems.put("buginium-z", new TypedZCrystalItem(
                "buginium-z",
                "buginiumz",
                "bug",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "bug"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Bug-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("darkinium-z", new TypedZCrystalItem(
                "darkinium-z",
                "darkiniumz",
                "dark",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "dark"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Dark-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("dragonium-z", new TypedZCrystalItem(
                "dragonium-z",
                "dragoniumz",
                "dragon",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "dragon"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Dragon-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("electrium-z", new TypedZCrystalItem(
                "electrium-z",
                "electriumz",
                "electric",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "electric"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Electric-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("fairium-z", new TypedZCrystalItem(
                "fairium-z",
                "fairiumz",
                "fairy",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "fairy"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Fairy-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("fightinium-z", new TypedZCrystalItem(
                "fightinium-z",
                "fightiniumz",
                "fighting",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "fighting"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Fighting-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("firium-z", new TypedZCrystalItem(
                "firium-z",
                "firiumz",
                "fire",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "fire"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Fire-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("flyinium-z", new TypedZCrystalItem(
                "flyinium-z",
                "flyiniumz",
                "flying",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "flying"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Flying-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("ghostium-z", new TypedZCrystalItem(
                "ghostium-z",
                "ghostiumz",
                "ghost",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "ghost"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Ghost-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("grassium-z", new TypedZCrystalItem(
                "grassium-z",
                "grassiumz",
                "grass",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "grass"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Grass-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("groundium-z", new TypedZCrystalItem(
                "groundium-z",
                "groundiumz",
                "ground",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "ground"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Ground-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("icium-z", new TypedZCrystalItem(
                "icium-z",
                "iciumz",
                "ice",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "ice"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Ice-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("normalium-z", new TypedZCrystalItem(
                "normalium-z",
                "normaliumz",
                "normal",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "normal"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Normal-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("poisonium-z", new TypedZCrystalItem(
                "poisonium-z",
                "poisoniumz",
                "poison",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "poison"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Poison-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("psychium-z", new TypedZCrystalItem(
                "psychium-z",
                "psychiumz",
                "psychic",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "psychic"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Psychic-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("rockium-z", new TypedZCrystalItem(
                "rockium-z",
                "rockiumz",
                "rock",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "rock"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Rock-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("steelium-z", new TypedZCrystalItem(
                "steelium-z",
                "steeliumz",
                "steel",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "steel"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Steel-type moves to Z-Moves."
                ))
        );
        typedZCrystalItems.put("waterium-z", new TypedZCrystalItem(
                "waterium-z",
                "wateriumz",
                "water",
                List.of(
                        new FormSetting(
                                List.of("arceus"),
                                "multitype",
                                "normal",
                                "water"
                        )
                ),
                List.of(
                        "<gray>This is a crystallized form of Z-Power.",
                        "<gray>It upgrades Water-type moves to Z-Moves."
                ))
        );
    }

    public void loadSpeciesZCrystals() {
        speciesZCrystalItems.put("aloraichium-z", new SpeciesZCrystalItem(
                "aloraichium-z",
                "aloraichiumz",
                List.of(
                        new ZCrystalSpecies("raichu", "alolan")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("decidium-z", new SpeciesZCrystalItem(
                "decidium-z",
                "decidiumz",
                List.of(
                        new ZCrystalSpecies("decidueye", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("eevium-z", new SpeciesZCrystalItem(
                "eevium-z",
                "eeviumz",
                List.of(
                        new ZCrystalSpecies("eevee", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("incinium-z", new SpeciesZCrystalItem(
                "incinium-z",
                "inciniumz",
                List.of(
                        new ZCrystalSpecies("incineroar", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("kommonium-z", new SpeciesZCrystalItem(
                "kommonium-z",
                "kommoniumz",
                List.of(
                        new ZCrystalSpecies("kommoo", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("lunalium-z", new SpeciesZCrystalItem(
                "lunalium-z",
                "lunaliumz",
                List.of(
                        new ZCrystalSpecies("lunala", ""),
                        new ZCrystalSpecies("necrozma", "dawn")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("solganium-z", new SpeciesZCrystalItem(
                "solganium-z",
                "solganiumz",
                List.of(
                        new ZCrystalSpecies("solgaleo", ""),
                        new ZCrystalSpecies("necrozma", "dusk")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("lycanium-z", new SpeciesZCrystalItem(
                "lycanium-z",
                "lycaniumz",
                List.of(
                        new ZCrystalSpecies("lycanroc", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("marshadium-z", new SpeciesZCrystalItem(
                "marshadium-z",
                "marshadiumz",
                List.of(
                        new ZCrystalSpecies("marshadow", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("mewnium-z", new SpeciesZCrystalItem(
                "mewnium-z",
                "mewniumz",
                List.of(
                        new ZCrystalSpecies("mew", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("mimikium-z", new SpeciesZCrystalItem(
                "mimikium-z",
                "mimikiumz",
                List.of(
                        new ZCrystalSpecies("mimikyu", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("pikanium-z", new SpeciesZCrystalItem(
                "pikanium-z",
                "pikaniumz",
                List.of(
                        new ZCrystalSpecies("pikachu", "normal")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("pikashunium-z", new SpeciesZCrystalItem(
                "pikashunium-z",
                "pikashuniumz",
                List.of(
                        new ZCrystalSpecies("pikachu", "partner")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("primarium-z", new SpeciesZCrystalItem(
                "primarium-z",
                "primariumz",
                List.of(
                        new ZCrystalSpecies("primarina", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("snorlium-z", new SpeciesZCrystalItem(
                "snorlium-z",
                "snorliumz",
                List.of(
                        new ZCrystalSpecies("snorlax", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("tapunium-z", new SpeciesZCrystalItem(
                "tapunium-z",
                "tapuniumz",
                List.of(
                        new ZCrystalSpecies("tapukoko", ""),
                        new ZCrystalSpecies("tapulele", ""),
                        new ZCrystalSpecies("tapubulu", ""),
                        new ZCrystalSpecies("tapufini", "")
                ),
                List.of("change later"))
        );
        speciesZCrystalItems.put("ultranecrozium-z", new SpeciesZCrystalItem(
                "ultranecrozium-z",
                "ultranecroziumz",
                List.of(
                        new ZCrystalSpecies("necrozma", "dawn"),
                        new ZCrystalSpecies("necrozma", "dusk")
                ),
                List.of("change later"))
        );
    }

    public LinkedHashMap<String, ZCrystalPolymerItem> zCrystalPolymerItems = new LinkedHashMap<>();
    public LinkedHashMap<String, PolymerModelData> zCrystalPolymerModelData = new LinkedHashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.EPIC).fireproof();
    private final Item baseVanillaItem = Items.PRISMARINE_CRYSTALS;

    public void fillPolymerItems() {
        for (String key : typedZCrystalItems.keySet()) {
            zCrystalPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new ZCrystalPolymerItem(itemSettings.component(DataComponents.Z_CRYSTAL, key), baseVanillaItem, key)));
        }

        for (String key : speciesZCrystalItems.keySet()) {
            zCrystalPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new ZCrystalPolymerItem(itemSettings.component(DataComponents.Z_CRYSTAL, key), baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : typedZCrystalItems.keySet()) {
            zCrystalPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }

        for (String key : speciesZCrystalItems.keySet()) {
            zCrystalPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }
    }

    public void registerItemGroup() {
        ItemGroup Z_CRYSTALS = FabricItemGroup.builder()
                .icon(zCrystalPolymerItems.values().stream().toList().getFirst()::getDefaultStack)
                .displayName(Text.literal("Z Crystals"))
                .entries((displayContext, entries) -> {
                    for (String key : zCrystalPolymerModelData.keySet()) {
                        entries.add(zCrystalPolymerItems.get(key));
                    }
                }).build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "z_crystals"), Z_CRYSTALS);
        for (String key : getAllZCrystalIds()) {
            CobblemonHeldItemManager.INSTANCE.registerRemap(zCrystalPolymerItems.get(key), getShowdownID(key));
        }
    }

    public static class ZCrystalPolymerItem extends SimplePolymerItem {
        private final PolymerModelData modelData;
        private final String id;

        public ZCrystalPolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
            this.modelData = ZCrystalItems.getInstance().zCrystalPolymerModelData.get(id);
        }

        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
            return this.modelData.value();
        }

        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            for (String line : ZCrystalItems.getInstance().getLore(id)) {
                tooltip.add(TextUtils.deserialize(line));
            }
        }
    }
}
