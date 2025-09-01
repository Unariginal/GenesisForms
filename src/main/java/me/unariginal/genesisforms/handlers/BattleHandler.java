package me.unariginal.genesisforms.handlers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonFaintedEvent;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
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

    public static Unit battleStarted(BattleStartedEvent.Pre event) {
        for (ServerPlayerEntity player : event.getBattle().getPlayers()) {
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);

            for (Pokemon pokemon : playerPartyStore) {
                FormHandler.revertForms(pokemon, true);

                gf.getMegaEvolvedThisBattle().remove(player.getUuid());
                if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("x"))) {
                    new StringSpeciesFeature(gf.getConfig().megaXFeatureName, "none").apply(pokemon);
                    pokemon.setTradeable(true);
                    gf.getPlayersWithMega().remove(player.getUuid());
                } else if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("y"))) {
                    new StringSpeciesFeature(gf.getConfig().megaYFeatureName, "none").apply(pokemon);
                    pokemon.setTradeable(true);
                    gf.getPlayersWithMega().remove(player.getUuid());
                } else if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"))) {
                    new StringSpeciesFeature(gf.getConfig().megaFeatureName, "none").apply(pokemon);
                    pokemon.setTradeable(true);
                    gf.getPlayersWithMega().remove(player.getUuid());
                }
            }

            boolean hasKeyStone = false;
            boolean hasDynamaxBand = false;
            boolean hasZRing = false;
            boolean hasTeraOrb = false;

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
                String keyItem = itemStack.getComponents().get(DataComponents.KEY_ITEM);
                if (keyItem == null || keyItem.isEmpty()) continue;
                if (keyItem.equalsIgnoreCase("key_stone") ||
                        keyItem.equalsIgnoreCase("mega_bracelet") ||
                        keyItem.equalsIgnoreCase("mega_charm") ||
                        keyItem.equalsIgnoreCase("mega_cuff") ||
                        keyItem.equalsIgnoreCase("mega_ring")) {
                    gf.logInfo("[Genesis] Found key stone in inventory!");
                    hasKeyStone = true;
                } else if (keyItem.equalsIgnoreCase("dynamax_band")) {
                    gf.logInfo("[Genesis] Found dynamax band in inventory!");
                    hasDynamaxBand = true;
                } else if (keyItem.equalsIgnoreCase("z_ring") ||
                        keyItem.equalsIgnoreCase("z_power_ring")) {
                    gf.logInfo("[Genesis] Found z ring in inventory!");
                    hasZRing = true;
                } else if (keyItem.equalsIgnoreCase("tera_orb")) {
                    gf.logInfo("[Genesis] Found tera orb in inventory!");
                    hasTeraOrb = true;
                }
            }

            GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);
            playerData.getKeyItems().remove(Identifier.of("cobblemon", "key_stone"));
            playerData.getKeyItems().remove(Identifier.of("cobblemon", "dynamax_band"));
            playerData.getKeyItems().remove(Identifier.of("cobblemon", "z_ring"));
            playerData.getKeyItems().remove(Identifier.of("cobblemon", "tera_orb"));

            if (!gf.getConfig().disabledItems.contains("key_stone") && gf.getConfig().enableMegaEvolution && hasKeyStone) {
                playerData.getKeyItems().add(Identifier.of("cobblemon", "key_stone"));
            }

            if (!gf.getConfig().disabledItems.contains("dynamax_band") && gf.getConfig().enableDynamax && hasDynamaxBand && !hasTeraOrb) {
                playerData.getKeyItems().add(Identifier.of("cobblemon", "dynamax_band"));
            }

            if (!gf.getConfig().disabledItems.contains("z_ring") && gf.getConfig().enableZCrystals && hasZRing) {
                playerData.getKeyItems().add(Identifier.of("cobblemon", "z_ring"));
            }

            if (!gf.getConfig().disabledItems.contains("tera_orb") && gf.getConfig().enableTera && hasTeraOrb) {
                playerData.getKeyItems().add(Identifier.of("cobblemon", "tera_orb"));
            }
        }

        return Unit.INSTANCE;
    }

    public static Unit battleEnded(BattleVictoryEvent event) {
        event.getBattle().getPlayers().forEach(player -> {
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : playerPartyStore) {
                FormHandler.revertForms(pokemon, true);
                if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("x"))) {
                    new StringSpeciesFeature(gf.getConfig().megaXFeatureName, "none").apply(pokemon);
                    pokemon.setTradeable(true);
                    gf.getPlayersWithMega().remove(player.getUuid());
                    gf.getMegaEvolvedThisBattle().remove(player.getUuid());
                } else if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega") && aspect.contains("y"))) {
                    new StringSpeciesFeature(gf.getConfig().megaYFeatureName, "none").apply(pokemon);
                    pokemon.setTradeable(true);
                    gf.getPlayersWithMega().remove(player.getUuid());
                    gf.getMegaEvolvedThisBattle().remove(player.getUuid());
                } else if (pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"))) {
                    new StringSpeciesFeature(gf.getConfig().megaFeatureName, "none").apply(pokemon);
                    pokemon.setTradeable(true);
                    gf.getPlayersWithMega().remove(player.getUuid());
                    gf.getMegaEvolvedThisBattle().remove(player.getUuid());
                }
            }
        });
        return Unit.INSTANCE;
    }

    public static Unit battleFled(BattleFledEvent event) {
        event.getBattle().getPlayers().forEach(player -> {
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : playerPartyStore) {
                FormHandler.revertForms(pokemon, true);
            }
        });
        return Unit.INSTANCE;
    }

    public static Unit battleFaint(BattleFaintedEvent event) {
        Pokemon pokemon = event.getKilled().getOriginalPokemon();
        ServerPlayerEntity player = pokemon.getOwnerPlayer();
        if (player != null) {
            handleFaint(pokemon, true);
        }
        return Unit.INSTANCE;
    }

    public static Unit pokemonFaint(PokemonFaintedEvent event) {
        Pokemon pokemon = event.getPokemon();
        ServerPlayerEntity player = pokemon.getOwnerPlayer();
        if (player != null) {
            handleFaint(pokemon, false);
        }
        return Unit.INSTANCE;
    }

    public static void handleFaint(Pokemon pokemon, boolean fromBattle) {
        boolean isMega = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("mega"));
        boolean isGmax = pokemon.getAspects().stream().anyMatch(aspect -> aspect.startsWith("gmax"));
        boolean isUltra = pokemon.getAspects().contains("ultra-fusion");
        if (isMega ||
                isGmax ||
                isUltra ||
                pokemon.getSpecies().getName().equalsIgnoreCase("Aegislash") ||
                pokemon.getSpecies().getName().equalsIgnoreCase("Morpeko")) {
            FormHandler.revertForms(pokemon, fromBattle);
        }
    }
}