package me.unariginal.genesisforms.items.bagitems.terashards;

import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.api.types.tera.TeraTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.utils.NbtUtils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import java.util.Map;
import java.util.Set;

public class TeraShardBagItems {
    private final static TeraShardBagItems INSTANCE = new TeraShardBagItems();
    private final Map<String, TeraType> TERA_SHARD_IDS = new HashMap<>();

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
        TERA_SHARD_IDS.put("normal", TeraTypes.getNORMAL());
        TERA_SHARD_IDS.put("fire", TeraTypes.getFIRE());
        TERA_SHARD_IDS.put("water", TeraTypes.getWATER());
        TERA_SHARD_IDS.put("electric", TeraTypes.getELECTRIC());
        TERA_SHARD_IDS.put("grass", TeraTypes.getGRASS());
        TERA_SHARD_IDS.put("ice", TeraTypes.getICE());
        TERA_SHARD_IDS.put("fighting", TeraTypes.getFIGHTING());
        TERA_SHARD_IDS.put("poison", TeraTypes.getPOISON());
        TERA_SHARD_IDS.put("ground", TeraTypes.getGROUND());
        TERA_SHARD_IDS.put("flying", TeraTypes.getFLYING());
        TERA_SHARD_IDS.put("psychic", TeraTypes.getPSYCHIC());
        TERA_SHARD_IDS.put("bug", TeraTypes.getBUG());
        TERA_SHARD_IDS.put("rock", TeraTypes.getROCK());
        TERA_SHARD_IDS.put("ghost", TeraTypes.getGHOST());
        TERA_SHARD_IDS.put("dragon", TeraTypes.getDRAGON());
        TERA_SHARD_IDS.put("dark", TeraTypes.getDARK());
        TERA_SHARD_IDS.put("steel", TeraTypes.getSTEEL());
        TERA_SHARD_IDS.put("fairy", TeraTypes.getFAIRY());
        TERA_SHARD_IDS.put("stellar", TeraTypes.getSTELLAR());
    }

    public Map<String, TeraShardPolymerItem> teraShardPolymerItems = new HashMap<>();
    public Map<String, PolymerModelData> teraShardPolymerModelData = new HashMap<>();
    private final Item.Settings itemSettings = new Item.Settings().rarity(Rarity.RARE).fireproof();
    private final Item baseVanillaItem = Items.AMETHYST_SHARD;

    public void fillPolymerItems() {
        for (String key : TERA_SHARD_IDS.keySet()) {
            teraShardPolymerItems.put(key, Registry.register(Registries.ITEM, Identifier.of(GenesisForms.MOD_ID, key + "_tera_shard"), new TeraShardPolymerItem(itemSettings, baseVanillaItem, key)));
        }
    }

    public void fillPolymerModelData() {
        for (String key : TERA_SHARD_IDS.keySet()) {
            teraShardPolymerModelData.put(key, PolymerResourcePackUtils.requestModel(baseVanillaItem, Identifier.of(GenesisForms.MOD_ID, "item/" + key + "_tera_shard")));
        }
    }

    public void registerItemGroup() {
        ItemGroup TERA_SHARDS = FabricItemGroup.builder()
                .icon(teraShardPolymerItems.get("fire")::getDefaultStack)
                .displayName(Text.literal("Tera Shards"))
                .entries((displayContext, entries) -> {
                    for (String key : teraShardPolymerModelData.keySet()) {
                        entries.add(teraShardPolymerItems.get(key));
                    }
                }).build();

        Registry.register(Registries.ITEM_GROUP, Identifier.of(GenesisForms.MOD_ID, "tera_shards"), TERA_SHARDS);
    }

    public static class TeraShardPolymerItem extends SimplePolymerItem {
        PolymerModelData modelData;
        String id;

        public TeraShardPolymerItem(Settings settings, Item polymerItem, String id) {
            super(settings, polymerItem);
            this.id = id;
        }

        @Override
        public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
            if (GenesisForms.INSTANCE.getItemSettings().item_lore.containsKey(id)) {
                NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get(id));
            }
            NbtUtils.setNbtString(itemStack, GenesisForms.MOD_ID, DataKeys.NBT_TERA_SHARD, id);
            return super.getPolymerItem(itemStack, player);
        }

        @Override
        public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
            this.modelData = TeraShardBagItems.getInstance().teraShardPolymerModelData.get(id);
            return this.modelData.value();
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
                                        pokemon.setTeraType(TeraShardBagItems.getInstance().getTeraType(id));
                                        player.getStackInHand(hand).decrement(1);
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
