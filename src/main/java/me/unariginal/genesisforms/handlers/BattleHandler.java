package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonFaintedEvent;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.utils.NbtUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BattleHandler {
    private final static GenesisForms gf = GenesisForms.INSTANCE;

    public static Unit battle_started(BattleStartedPreEvent event) {
        for (ServerPlayerEntity player : event.getBattle().getPlayers()) {
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);

            for (Pokemon pokemon : playerPartyStore) {
                FormHandler.revert_forms(pokemon, true);
            }

            boolean has_keyStone = false;
            boolean has_dynamaxBand = false;
            boolean has_zRing = false;
            boolean has_teraOrb = false;

            List<ItemStack> inventory = new ArrayList<>();
            inventory.addAll(player.getInventory().main);
            inventory.addAll(player.getInventory().offHand);
            inventory.addAll(player.getInventory().armor);
            for (ItemStack itemStack : inventory) {
                NbtCompound nbt = NbtUtils.getNbt(itemStack, GenesisForms.MOD_ID);
                if (nbt.isEmpty() || !nbt.contains(DataKeys.NBT_KEY_ITEM)) continue;
                String nbtString = nbt.getString(DataKeys.NBT_KEY_ITEM);
                gf.logInfo("[Genesis] NBT String: " + nbtString);
                if (nbtString.isEmpty()) continue;
                if (nbtString.equalsIgnoreCase("key_stone")) {
                    gf.logInfo("[Genesis] Found key stone in inventory!");
                    has_keyStone = true;
                } else if (nbtString.equalsIgnoreCase("dynamax_band")) {
                    gf.logInfo("[Genesis] Found dynamax band in inventory!");
                    has_dynamaxBand = true;
                } else if (nbtString.equalsIgnoreCase("z_ring")) {
                    gf.logInfo("[Genesis] Found z ring in inventory!");
                    has_zRing = true;
                } else if (nbtString.equalsIgnoreCase("tera_orb")) {
                    gf.logInfo("[Genesis] Found tera orb in inventory!");
                    has_teraOrb = true;
                }
            }

            Set<Identifier> keyItems = Cobblemon.playerDataManager.getGenericData(player).getKeyItems();

            if (!GenesisForms.INSTANCE.getConfig().disabledItems.contains("key_stone") && gf.getConfig().enableMegaEvolution && has_keyStone) {
                keyItems.add(Identifier.of("cobblemon", "key_stone"));
            } else {
                keyItems.remove(Identifier.of("cobblemon", "key_stone"));
            }

            if (!GenesisForms.INSTANCE.getConfig().disabledItems.contains("dynamax_band") && gf.getConfig().enableDynamax && has_dynamaxBand) {
                keyItems.add(Identifier.of("cobblemon", "dynamax_band"));
                if (!gf.getConfig().useGen9Battles) {
                    try {
                        BattleFormat format = event.getBattle().getFormat();
                        Field field = format.getClass().getDeclaredField("gen");
                        field.setAccessible(true);
                        field.set(format, 8);
                    } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
                        gf.logError("[Genesis] Failed to set battle gen to 8! Dynamax will not work!");
                        gf.logError("[Genesis] Error: " + e.getMessage());
                    }
                }
            } else {
                keyItems.remove(Identifier.of("cobblemon", "dynamax_band"));
            }

            if (!GenesisForms.INSTANCE.getConfig().disabledItems.contains("z_ring") && gf.getConfig().enableZCrystals && has_zRing) {
                keyItems.add(Identifier.of("cobblemon", "z_ring"));
            } else {
                keyItems.remove(Identifier.of("cobblemon", "z_ring"));
            }

            if (!GenesisForms.INSTANCE.getConfig().disabledItems.contains("tera_orb") && gf.getConfig().enableTera && has_teraOrb) {
                keyItems.add(Identifier.of("cobblemon", "tera_orb"));
            } else {
                keyItems.remove(Identifier.of("cobblemon", "tera_orb"));
            }
        }

        return Unit.INSTANCE;
    }

    public static Unit battle_ended(BattleVictoryEvent event) {
        event.getBattle().getPlayers().forEach(player -> {
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : playerPartyStore) {
                FormHandler.revert_forms(pokemon, false);
            }
        });
        return Unit.INSTANCE;
    }

    public static Unit battle_fled(BattleFledEvent event) {
        event.getBattle().getPlayers().forEach(player -> {
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : playerPartyStore) {
                FormHandler.revert_forms(pokemon, false);
            }
        });
        return Unit.INSTANCE;
    }

    public static Unit battle_faint(BattleFaintedEvent event) {
        Pokemon pokemon = event.getKilled().getOriginalPokemon();
        ServerPlayerEntity player = pokemon.getOwnerPlayer();
        if (player != null) {
            handle_faint(pokemon);
        }
        return Unit.INSTANCE;
    }

    public static Unit pokemon_faint(PokemonFaintedEvent event) {
        Pokemon pokemon = event.getPokemon();
        ServerPlayerEntity player = pokemon.getOwnerPlayer();
        if (player != null) {
            handle_faint(pokemon);
        }
        return Unit.INSTANCE;
    }

    public static void handle_faint(Pokemon pokemon) {
        boolean isMega = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"));
        boolean isGmax = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("gmax"));
        boolean isUltra = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("ultra"));
        if (isMega || isGmax || isUltra || pokemon.getSpecies().getName().equalsIgnoreCase("Aegislash")) {
            FormHandler.revert_forms(pokemon, true);
        }
    }
}
