package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.battle.BattleTransformPokemonPacket;
import com.cobblemon.mod.common.pokemon.Species;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.handlers.PacketHandler;
import me.unariginal.genesisforms.items.helditems.zcrystals.ZCrystalHeldItems;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.TextUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ZRing extends SimplePolymerItem {
    PolymerModelData modelData;

    public ZRing(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = KeyItems.zRingModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("z_ring")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("z_ring")) return ActionResult.PASS;
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
                                        if (GenesisForms.INSTANCE.getConfig().enableUltraBurst) {
                                            boolean isNecrozma = false;
                                            for (Species species : ZCrystalHeldItems.getInstance().getZCrystalSpecies("ultranecrozium-z")) {
                                                if (species.getName().equalsIgnoreCase(pokemonEntity.getPokemon().getSpecies().getName())) {
                                                    isNecrozma = true;
                                                    break;
                                                }
                                            }
                                            if (!isNecrozma) return ActionResult.PASS;
                                            if (!GenesisForms.INSTANCE.getUltra_burst_this_battle().contains(player.getUuid())) {
                                                player.sendMessage(TextUtils.deserialize("<red>[GenesisForms] Warning! Ultra-burst does <b>not <!b>have full functionality yet!"));
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
