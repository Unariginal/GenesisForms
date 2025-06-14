package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonFaintedEvent;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import kotlin.Unit;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataComponents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BattleHandler {
    private final static GenesisForms gf = GenesisForms.INSTANCE;

    public static Unit battle_started(BattleStartedPreEvent event) {
        for (ServerPlayerEntity player : event.getBattle().getPlayers()) {
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);

            for (Pokemon pokemon : playerPartyStore) {
                FormHandler.revert_forms(pokemon, true);
            }
            gf.getPlayersWithMega().remove(player.getUuid());
            gf.getMegaEvolvedThisBattle().remove(player.getUuid());

            boolean has_keyStone = false;
            boolean has_dynamaxBand = false;
            boolean has_zRing = false;
            boolean has_teraOrb = false;

            List<ItemStack> inventory = new ArrayList<>();
            if (gf.getConfig().useHotbarInventory) {
                for (ItemStack itemStack : player.getInventory().main) {
                    if (PlayerInventory.isValidHotbarIndex(player.getInventory().indexOf(itemStack))) {
                        inventory.add(itemStack);
                    }
                }
            }
            if (gf.getConfig().useMainInventory) {
                for (ItemStack itemStack : player.getInventory().main) {
                    if (!inventory.contains(itemStack)) {
                        inventory.add(itemStack);
                    }
                }
            }
            if (gf.getConfig().useMainHandInventory) {
                if (!inventory.contains(player.getMainHandStack())) {
                    inventory.add(player.getMainHandStack());
                }
            }
            if (gf.getConfig().useOffHandInventory) {
                if (!inventory.contains(player.getOffHandStack())) {
                    inventory.add(player.getOffHandStack());
                }
            }
            if (gf.getConfig().useArmorInventory) {
                for (ItemStack itemStack : player.getArmorItems()) {
                    if (!inventory.contains(itemStack)) {
                        inventory.add(itemStack);
                    }
                }
            }
            for (int slot : gf.getConfig().specificSlots) {
                ItemStack stack = player.getInventory().getStack(slot);
                if (!stack.isEmpty() && !inventory.contains(stack)) {
                    inventory.add(stack);
                }
            }

            for (ItemStack itemStack : inventory) {
                if (!itemStack.getComponents().contains(DataComponents.KEY_ITEM)) continue;
                String key_item = itemStack.getComponents().get(DataComponents.KEY_ITEM);
                if (key_item == null || key_item.isEmpty()) continue;
                if (key_item.equalsIgnoreCase("key_stone") ||
                        key_item.equalsIgnoreCase("mega_bracelet") ||
                        key_item.equalsIgnoreCase("mega_charm") ||
                        key_item.equalsIgnoreCase("mega_cuff") ||
                        key_item.equalsIgnoreCase("mega_ring")) {
                    gf.logInfo("[Genesis] Found key stone in inventory!");
                    has_keyStone = true;
                } else if (key_item.equalsIgnoreCase("dynamax_band")) {
                    gf.logInfo("[Genesis] Found dynamax band in inventory!");
                    has_dynamaxBand = true;
                } else if (key_item.equalsIgnoreCase("z_ring") ||
                        key_item.equalsIgnoreCase("z_power_ring")) {
                    gf.logInfo("[Genesis] Found z ring in inventory!");
                    has_zRing = true;
                } else if (key_item.equalsIgnoreCase("tera_orb")) {
                    gf.logInfo("[Genesis] Found tera orb in inventory!");
                    has_teraOrb = true;
                }
            }

            GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);
            playerData.getKeyItems().remove(Identifier.of("cobblemon", "key_stone"));
            playerData.getKeyItems().remove(Identifier.of("cobblemon", "dynamax_band"));
            playerData.getKeyItems().remove(Identifier.of("cobblemon", "z_ring"));
            playerData.getKeyItems().remove(Identifier.of("cobblemon", "tera_orb"));

            if (!gf.getConfig().disabledItems.contains("key_stone") && gf.getConfig().enableMegaEvolution && has_keyStone) {
                playerData.getKeyItems().add(Identifier.of("cobblemon", "key_stone"));
            }

            if (!gf.getConfig().disabledItems.contains("dynamax_band") && gf.getConfig().enableDynamax && has_dynamaxBand && !has_teraOrb) {
                playerData.getKeyItems().add(Identifier.of("cobblemon", "dynamax_band"));
            }

            if (!gf.getConfig().disabledItems.contains("z_ring") && gf.getConfig().enableZCrystals && has_zRing) {
                playerData.getKeyItems().add(Identifier.of("cobblemon", "z_ring"));
            }

            if (!gf.getConfig().disabledItems.contains("tera_orb") && gf.getConfig().enableTera && has_teraOrb) {
                playerData.getKeyItems().add(Identifier.of("cobblemon", "tera_orb"));
            }
        }

        return Unit.INSTANCE;
    }

    public static Unit battle_ended(BattleVictoryEvent event) {
        gf.logInfo("[Genesis] Battle ended (victory event)!");
        event.getBattle().getPlayers().forEach(player -> {
            gf.logInfo("[Genesis] Player loop " + player.getNameForScoreboard());
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : playerPartyStore) {
                gf.logInfo("[Genesis] Reverting player's pokemon: " + pokemon.getSpecies().getName());
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
        boolean isUltra = pokemon.getAspects().contains("ultra-fusion");
        if (isMega ||
                isGmax ||
                isUltra ||
                pokemon.getSpecies().getName().equalsIgnoreCase("Aegislash") ||
                pokemon.getSpecies().getName().equalsIgnoreCase("Morpeko")) {
            FormHandler.revert_forms(pokemon, true);
        }
    }
}