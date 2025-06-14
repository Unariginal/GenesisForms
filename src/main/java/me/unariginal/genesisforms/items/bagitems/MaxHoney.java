package me.unariginal.genesisforms.items.bagitems;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.advancement.CobblemonCriteria;
import com.cobblemon.mod.common.advancement.criterion.PokemonInteractContext;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.callback.PartySelectCallbacks;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.healing.PokemonHealedEvent;
import com.cobblemon.mod.common.api.item.HealingSource;
import com.cobblemon.mod.common.battles.BagItemActionResponse;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.item.battle.BagItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.polymer.BagItems;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MaxHoney extends SimplePolymerItem implements HealingSource {
    PolymerModelData modelData;
    BagItem bagItem = new BagItem() {
        @Override
        public boolean canStillUse(@NotNull ServerPlayerEntity serverPlayerEntity, @NotNull PokemonBattle pokemonBattle, @NotNull BattleActor battleActor, @NotNull BattlePokemon battlePokemon, @NotNull ItemStack itemStack) {
            return false;
        }

        @Override
        public @NotNull String getItemName() {
            return "max_honey";
        }

        @Override
        public @NotNull Item getReturnItem() {
            return Items.AIR;
        }

        @Override
        public boolean canUse(@NotNull PokemonBattle battle, BattlePokemon target) {
            return target.getHealth() <= 0;
        }

        @Override
        public @NotNull String getShowdownInput(@NotNull BattleActor battleActor, @NotNull BattlePokemon battlePokemon, @Nullable String s) {
            return "revive 1";
        }
    };

    public MaxHoney(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = BagItems.maxHoneyModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("max_honey")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("max_honey")) return TypedActionResult.success(user.getStackInHand(hand));
        ServerPlayerEntity player = GenesisForms.INSTANCE.getServer().getPlayerManager().getPlayer(user.getUuid());
        ItemStack stack = user.getStackInHand(hand);
        if (player != null) {
             PokemonBattle battle = BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player);
             if (battle != null) {
                 BattleActor actor = battle.getActor(player);
                 if (actor != null) {
                     List<BattlePokemon> battlePokemonList = actor.getPokemonList();
                     if (!actor.canFitForcedAction()) {
                         player.sendMessageToClient(LocalizationUtilsKt.battleLang("bagitem.cannot").styled(style -> style.withColor(Formatting.RED)), true);
                         return TypedActionResult.consume(stack);
                     } else {
                         try {
                             int turn = battle.getTurn();
                             PartySelectCallbacks.INSTANCE.createBattleSelect(player, battlePokemonList, (battlePokemon) -> bagItem.canUse(battle, battlePokemon), (battlePokemon) ->
                             {
                                 if (actor.canFitForcedAction() && battlePokemon.getHealth() <= 0 && battle.getTurn() == turn) {
                                     player.playSound(CobblemonSounds.ITEM_USE, 1F, 1F);
                                     actor.forceChoose(new BagItemActionResponse(bagItem, battlePokemon, battlePokemon.getUuid().toString()));
                                     Identifier stackName = Registries.ITEM.getId(stack.getItem());
                                     stack.decrementUnlessCreative(1, player);
                                     CobblemonCriteria.INSTANCE.getPOKEMON_INTERACT().trigger(player, new PokemonInteractContext(battlePokemon.getEffectedPokemon().getSpecies().getResourceIdentifier(), stackName));
                                 }
                                 return Unit.INSTANCE;
                             });
                         } catch (NoSuchMethodError e) {
                             GenesisForms.INSTANCE.logError("[Genesis] Suppressing NoSuchMethodError (You're running the 1.6.1 version on the 1.7 snapshot! " + e.getMessage());
                         }
                     }
                 }
             } else {
                 List<Pokemon> pokemon_party = new ArrayList<>();
                 for (Pokemon pokemon : Cobblemon.INSTANCE.getStorage().getParty(player)) {
                     if (pokemon != null) {
                         pokemon_party.add(pokemon);
                     }
                 }
                 PartySelectCallbacks.INSTANCE.createFromPokemon(player, pokemon_party, Pokemon::isFainted, pokemon -> {
                     PokemonBattle playerBattle = BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(player);
                     if (pokemon.isFainted() && playerBattle == null) {
                         AtomicInteger amount = new AtomicInteger(pokemon.getMaxHealth());
                         CobblemonEvents.POKEMON_HEALED.postThen(new PokemonHealedEvent(pokemon, amount.get(), this), event -> Unit.INSTANCE, event ->
                         {
                             amount.set(event.getAmount());
                             return Unit.INSTANCE;
                         });
                         pokemon.setCurrentHealth(amount.get());
                         Identifier stackName = Registries.ITEM.getId(stack.getItem());
                         if (GenesisForms.INSTANCE.getItemSettings().consumableBagItems.contains("max_honey")) {
                             stack.decrementUnlessCreative(1, player);
                         }
                         CobblemonCriteria.INSTANCE.getPOKEMON_INTERACT().trigger(player, new PokemonInteractContext(pokemon.getSpecies().getResourceIdentifier(), stackName));
                     }
                     return Unit.INSTANCE;
                 });
             }
        }

        return TypedActionResult.success(stack);
    }
}
