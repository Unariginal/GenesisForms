package me.unariginal.genesisforms.items.bagitems.terashards;

import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.utils.TextUtils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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

public class TeraShardBagItems {
    private final static TeraShardBagItems INSTANCE = new TeraShardBagItems();
    private final LinkedHashMap<String, TeraType> TERA_SHARD_IDS = new LinkedHashMap<>();

    public static TeraShardBagItems getInstance() {
        return INSTANCE;
    }

    public ItemStack getTeraShard(String id) {
        if (!TERA_SHARD_IDS.containsKey(id)) return ItemStack.EMPTY;
        return teraShardPolymerItems.get(id).getDefaultStack();
    }

    public Set<String> getAllTeraShardIds() {
        return TERA_SHARD_IDS.keySet();
    }

    public TeraType getTeraType(String id) {
        return TERA_SHARD_IDS.get(id);
    }

    public void loadTeraShardIds() {
        TERA_SHARD_IDS.put("bug_tera_shard", TeraTypes.getBUG());
        TERA_SHARD_IDS.put("dark_tera_shard", TeraTypes.getDARK());
        TERA_SHARD_IDS.put("dragon_tera_shard", TeraTypes.getDRAGON());
        TERA_SHARD_IDS.put("electric_tera_shard", TeraTypes.getELECTRIC());
        TERA_SHARD_IDS.put("fairy_tera_shard", TeraTypes.getFAIRY());
        TERA_SHARD_IDS.put("fighting_tera_shard", TeraTypes.getFIGHTING());
        TERA_SHARD_IDS.put("fire_tera_shard", TeraTypes.getFIRE());
        TERA_SHARD_IDS.put("flying_tera_shard", TeraTypes.getFLYING());
        TERA_SHARD_IDS.put("ghost_tera_shard", TeraTypes.getGHOST());
        TERA_SHARD_IDS.put("grass_tera_shard", TeraTypes.getGRASS());
        TERA_SHARD_IDS.put("ground_tera_shard", TeraTypes.getGROUND());
        TERA_SHARD_IDS.put("ice_tera_shard", TeraTypes.getICE());
        TERA_SHARD_IDS.put("normal_tera_shard", TeraTypes.getNORMAL());
        TERA_SHARD_IDS.put("poison_tera_shard", TeraTypes.getPOISON());
        TERA_SHARD_IDS.put("psychic_tera_shard", TeraTypes.getPSYCHIC());
        TERA_SHARD_IDS.put("rock_tera_shard", TeraTypes.getROCK());
        TERA_SHARD_IDS.put("steel_tera_shard", TeraTypes.getSTEEL());
        TERA_SHARD_IDS.put("stellar_tera_shard", TeraTypes.getSTELLAR());
        TERA_SHARD_IDS.put("water_tera_shard", TeraTypes.getWATER());
    }

    public LinkedHashMap<String, TeraShardPolymerItem> teraShardPolymerItems = new LinkedHashMap<>();
    public LinkedHashMap<String, PolymerModelData> teraShardPolymerModelData = new LinkedHashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.RARE).fireproof();
    private final Item baseVanillaItem = Items.AMETHYST_SHARD;

    public void fillPolymerItems() {
        for (String key : TERA_SHARD_IDS.keySet()) {
            teraShardPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key), new TeraShardPolymerItem(itemSettings.component(DataComponents.TERA_SHARD, key), baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : TERA_SHARD_IDS.keySet()) {
            teraShardPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key)));
        }
    }

    public void registerItemGroup() {
        ItemGroup TERA_SHARDS = FabricItemGroup.builder()
                .icon(teraShardPolymerItems.get("fire_tera_shard")::getDefaultStack)
                .displayName(Text.literal("Tera Shards"))
                .entries((displayContext, entries) -> {
                    for (String key : teraShardPolymerModelData.keySet()) {
                        entries.add(teraShardPolymerItems.get(key));
                    }
                }).build();

        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(GenesisForms.MOD_ID, "tera_shards"), TERA_SHARDS);
    }

    public static class TeraShardPolymerItem extends SimplePolymerItem {
        PolymerModelData modelData;
        String id;

        public TeraShardPolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
            this.modelData = TeraShardBagItems.getInstance().teraShardPolymerModelData.get(id);
        }

        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
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
        public ActionResult useOnEntity(ItemStack itemStack, PlayerEntity user, LivingEntity livingEntity, Hand hand) {
            if (livingEntity instanceof PokemonEntity pokemonEntity) {
                Pokemon pokemon = pokemonEntity.getPokemon();
                if (pokemon.isPlayerOwned()) {
                    ServerPlayerEntity player = pokemon.getOwnerPlayer();
                    if (player != null) {
                        if (player.getUuid().equals(user.getUuid())) {
                            if (!pokemonEntity.isBattling()) {
                                if (!pokemon.getSpecies().getName().equalsIgnoreCase("ogerpon") && !pokemon.getSpecies().getName().equalsIgnoreCase("terapagos")) {
                                    if (!pokemon.getTeraType().getId().equals(TeraShardBagItems.getInstance().getTeraType(id).getId())) {
                                        if (player.getStackInHand(hand).getCount() >= GenesisForms.INSTANCE.getConfig().teraShardsRequired) {
                                            pokemon.setTeraType(TeraShardBagItems.getInstance().getTeraType(id));
                                            if (GenesisForms.INSTANCE.getItemSettings().consumeTeraShards) {
                                                player.getStackInHand(hand).decrementUnlessCreative(GenesisForms.INSTANCE.getConfig().teraShardsRequired, player);
                                            }
                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(GenesisForms.INSTANCE.getMessagesConfig().getMessage("tera_type_changed"), pokemon)), true);
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
