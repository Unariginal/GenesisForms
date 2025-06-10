package me.unariginal.genesisforms.items.bagitems;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.advancement.CobblemonCriteria;
import com.cobblemon.mod.common.advancement.criterion.PokemonInteractContext;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.BagItemActionResponse;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.item.battle.SimpleBagItemLike;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class MaxMushroomsItem extends BlockItem implements SimpleBagItemLike, PolymerItem {
    private final PolymerModelData polymerModel;

    public BagItem bagItem = new BagItem() {
        @Override
        public boolean canStillUse(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull PokemonBattle pokemonBattle, @NotNull BattleActor battleActor, @NotNull BattlePokemon battlePokemon, @NotNull ItemStack itemStack) {
            return false;
        }

        @Override
        public @NotNull String getItemName() {
            return "max_mushrooms";
        }

        @Override
        public @NotNull Item getReturnItem() {
            return Items.AIR;
        }

        @Override
        public boolean canUse(@NotNull PokemonBattle battle, @NotNull BattlePokemon target) {
            return target.getHealth() > 0;
        }

        @Override
        public @NotNull String getShowdownInput(@NotNull BattleActor battleActor, @NotNull BattlePokemon battlePokemon, @Nullable String s) {
            return "max_mushroom";
        }
    };

    public MaxMushroomsItem(Settings settings, Block block, String modelId) {
        super(block, settings);
        this.polymerModel = PolymerResourcePackUtils.requestModel(Items.BROWN_MUSHROOM, Identifier.of(GenesisForms.MOD_ID, modelId));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.polymerModel.item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.polymerModel.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("max_mushrooms")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public @NotNull BagItem getBagItem() {
        return bagItem;
    }

    @Override
    public @Nullable BagItem getBagItem(@NotNull ItemStack itemStack) {
        return bagItem;
    }

    @Override
    public boolean handleInteraction(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull BattlePokemon battlePokemon, @NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("max_mushrooms")) return TypedActionResult.success(user.getStackInHand(hand));
        ServerPlayerEntity player = GenesisForms.INSTANCE.getServer().getPlayerManager().getPlayer(user.getUuid());
        ItemStack stack = user.getStackInHand(hand);
        if (player != null) {
            PokemonBattle battle = BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player);
            if (battle != null) {
                BattleActor actor = battle.getActor(player);
                if (actor != null) {
                    ActiveBattlePokemon activeBattlePokemon = actor.getActivePokemon().getFirst();
                    BattlePokemon battlePokemon = null;
                    if (activeBattlePokemon != null) {
                        battlePokemon = activeBattlePokemon.getBattlePokemon();
                    }
                    if (battlePokemon == null) {
                        return TypedActionResult.consume(stack);
                    }
                    if (!actor.canFitForcedAction()) {
                        player.sendMessageToClient(LocalizationUtilsKt.battleLang("bagitem.cannot").styled(style -> style.withColor(Formatting.RED)), true);
                        return TypedActionResult.consume(stack);
                    } else {
                        try {
                            int turn = battle.getTurn();
                            if (bagItem.canUse(battle, battlePokemon)) {
                                if (actor.canFitForcedAction() && battlePokemon.getHealth() > 0 && battle.getTurn() == turn) {
                                    player.playSound(CobblemonSounds.ITEM_USE, 1F, 1F);
                                    actor.forceChoose(new BagItemActionResponse(bagItem, battlePokemon, battlePokemon.getUuid().toString()));
                                    Identifier stackName = Registries.ITEM.getId(stack.getItem());
                                    if (!player.isCreative()) {
                                        stack.decrement(1);
                                    }
                                    CobblemonCriteria.INSTANCE.getPOKEMON_INTERACT().trigger(player, new PokemonInteractContext(battlePokemon.getEffectedPokemon().getSpecies().getResourceIdentifier(), stackName));
                                }
                            }
                        } catch (NoSuchMethodError e) {
                            GenesisForms.INSTANCE.logError("[Genesis] Suppressing NoSuchMethodError! " + e.getMessage());
                        }
                    }
                }
            }
        }
        return TypedActionResult.success(stack);
    }
}
