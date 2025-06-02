package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveSet;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.ShowdownMoveset;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.client.gui.battle.subscreen.BattleGimmickButton;
import com.cobblemon.mod.common.client.gui.battle.subscreen.BattleMoveSelection;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.battle.BattleTransformPokemonPacket;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.handlers.PacketHandler;
import me.unariginal.genesisforms.items.helditems.zcrystals.ZCrystalHeldItems;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ZRing extends SimplePolymerItem {
    PolymerModelData modelData;

    public ZRing(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        NbtUtils.setItemLore(itemStack, GenesisForms.INSTANCE.getItemSettings().item_lore.get("z_ring"));
        NbtUtils.setNbtString(itemStack, GenesisForms.MOD_ID, DataKeys.NBT_KEY_ITEM, "z_ring");
        return super.getPolymerItem(itemStack, player);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        this.modelData = KeyItems.zRingModelData;
        return this.modelData.value();
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PokemonEntity pokemonEntity) {
            ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
            if (player != null) {
                if (player.getUuid().equals(user.getUuid())) {
                    if (pokemonEntity.isBattling()) {
                        UUID battleId = pokemonEntity.getBattleId();
                        if (battleId != null) {
                            ActiveBattlePokemon activeBattlePokemon = null;
                            PokemonBattle battle = Cobblemon.INSTANCE.getBattleRegistry().getBattle(battleId);
                            if (battle != null) {
                                BattlePokemon battlePokemon = null;
                                for (ActiveBattlePokemon activeBattlePokemonLoop : battle.getActivePokemon()) {
                                    battlePokemon = activeBattlePokemonLoop.getBattlePokemon();
                                    if (battlePokemon != null) {
                                        PokemonEntity battlePokemonEntity = battlePokemon.getEntity();
                                        if (battlePokemonEntity != null) {
                                            if (battlePokemonEntity.getUuid().equals(pokemonEntity.getUuid())) {
                                                activeBattlePokemon = activeBattlePokemonLoop;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (battlePokemon != null && activeBattlePokemon != null) {
                                    boolean isDawn = pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("dawn"));
                                    boolean isDusk = pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("dusk"));
                                    if (isDawn || isDusk) {
                                        if (GenesisForms.INSTANCE.getConfig().enableUltraBurst && ZCrystalHeldItems.getInstance().showdownId(pokemonEntity.getPokemon()).equalsIgnoreCase("ultranecrozium-z")) {
                                            if (!GenesisForms.INSTANCE.getUltra_burst_this_battle().contains(player.getUuid())) {
                                                GenesisForms.INSTANCE.getUltra_burst_this_battle().add(player.getUuid());
                                                NbtCompound data = pokemonEntity.getPokemon().getPersistentData();
                                                data.putString("pre_ultra", isDawn ? "dawn" : "dusk");
                                                pokemonEntity.getPokemon().setPersistentData$common(data);

                                                new StringSpeciesFeature("prism_fusion", "ultra").apply(pokemonEntity.getPokemon());
                                                new FlagSpeciesFeature("ultra", true).apply(pokemonEntity.getPokemon());

                                                battle.sendSidedUpdate(activeBattlePokemon.getActor(), new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), battlePokemon, true), new BattleTransformPokemonPacket(activeBattlePokemon.getPNX(), battlePokemon, false), false);
                                                PacketHandler.update_packets(battle, battlePokemon, true);
                                            }
                                        }
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
