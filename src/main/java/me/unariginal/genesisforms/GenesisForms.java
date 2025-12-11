package me.unariginal.genesisforms;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import kotlin.Unit;
import me.unariginal.genesisforms.commands.GenesisCommands;
import me.unariginal.genesisforms.config.*;
import me.unariginal.genesisforms.config.items.MiscItemsConfig;
import me.unariginal.genesisforms.config.items.accessories.AccessoriesConfig;
import me.unariginal.genesisforms.config.items.bagitems.MaxItemsConfig;
import me.unariginal.genesisforms.config.items.bagitems.TeraShardsConfig;
import me.unariginal.genesisforms.config.items.helditems.HeldBattleItemsConfig;
import me.unariginal.genesisforms.config.items.helditems.HeldFormItemsConfig;
import me.unariginal.genesisforms.config.items.helditems.ZCrystalsConfig;
import me.unariginal.genesisforms.config.items.keyitems.FusionItemsConfig;
import me.unariginal.genesisforms.config.items.keyitems.KeyFormItemsConfig;
import me.unariginal.genesisforms.config.items.keyitems.PossessionItemsConfig;
import me.unariginal.genesisforms.handlers.*;
import me.unariginal.genesisforms.items.helditems.HeldBattleItem;
import me.unariginal.genesisforms.polymer.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class GenesisForms implements ModInitializer {
    public static final String MOD_ID = "genesisforms";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static GenesisForms INSTANCE;

    private MinecraftServer server;
    private FabricServerAudiences audiences;

    private Config config = new Config();
    private MessagesConfig messagesConfig = new MessagesConfig();

    private final Map<UUID, UUID> playersWithMega = new HashMap<>();

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;

        new GenesisCommands();

        try {
            AccessoriesConfig.load();
            KeyFormItemsConfig.load();
            HeldFormItemsConfig.load();
            HeldBattleItemsConfig.load();
            MegaEvolutionConfig.load();
            ZCrystalsConfig.load();
            TeraShardsConfig.load();
            FusionItemsConfig.load();
            PossessionItemsConfig.load();
            MaxItemsConfig.load();
            MiscItemsConfig.load();
        } catch (IOException e) {
            logError("[GenesisForms] " + e.getMessage());
        }

        PolymerResourcePackUtils.markAsRequired();
        PolymerResourcePackUtils.addModAssets(MOD_ID);

        registerItems();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
            this.audiences = FabricServerAudiences.of(server);
            reload();

            registerEvents();
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            try {
                List<UUID> toRemove = new ArrayList<>();
                for (UUID uuid : CobblemonEventHandler.activeMegaAnimations.keySet()) {
                    CobblemonEventHandler.activeMegaAnimations.put(uuid, CobblemonEventHandler.activeMegaAnimations.get(uuid) - 1);
                    if (CobblemonEventHandler.activeMegaAnimations.get(uuid) <= 0) {
                        toRemove.add(uuid);
                    }
                }
                for (UUID uuid : toRemove) {
                    CobblemonEventHandler.activeMegaAnimations.remove(uuid);
                }
            } catch (ConcurrentModificationException e) {
                // I'm sure this will probably happen because I'm modifying the key set in two locations at the same time :)
                LOGGER.warn("[GenesisForms] ", e);
            }
        });
    }

    public void reload() {
        this.config = new Config();
        this.messagesConfig = new MessagesConfig();
        try {
            EventsConfig.load();
            BattleFormChangeConfig.load();
        } catch (IOException e) {
            logError(e.getMessage());
        }
    }

    private void registerItems() {
        KeyItemsGroup.registerItemGroup();
        HeldItemsGroup.registerItemGroup();
        BagItemsGroup.registerItemGroup();
        MegastonesGroup.registerItemGroup();
        TeraShardsGroup.registerItemGroup();
        ZCrystalsGroup.registerItemGroup();
    }

    private void registerEvents() {
        CobblemonEvents.HELD_ITEM_POST.subscribe(CobblemonEventHandler::heldItemChange);
        CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(CobblemonEventHandler::pokemonReleasedEvent);
        CobblemonEvents.POKEMON_GAINED.subscribe(CobblemonEventHandler::pokemonGainedEvent);
        CobblemonEvents.POKEMON_SENT_POST.subscribe(CobblemonEventHandler::pokemonSentEvent);

        CobblemonEvents.FORME_CHANGE.subscribe(CobblemonEventHandler::formChangeEvent);
        CobblemonEvents.MEGA_EVOLUTION.subscribe(CobblemonEventHandler::megaEvolveEvent);
        CobblemonEvents.TERASTALLIZATION.subscribe(CobblemonEventHandler::terastallizationEvent);
        CobblemonEvents.ZPOWER_USED.subscribe(CobblemonEventHandler::zPowerEvent);
        UltraBurstHandler.register();
        DynamaxHandler.register();

        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(CobblemonEventHandler::battleStartEvent);
        CobblemonEvents.BATTLE_FAINTED.subscribe(CobblemonEventHandler::battleFaintEvent);
        CobblemonEvents.BATTLE_VICTORY.subscribe(CobblemonEventHandler::battleEndEvent);
        CobblemonEvents.BATTLE_FLED.subscribe(CobblemonEventHandler::battleFledEvent);

        // Prevent trading mega pokemon, without stopping other mods from doing their own tradeable changes
        CobblemonEvents.TRADE_EVENT_PRE.subscribe(CobblemonEventHandler::tradeEvent);

        // Macho brace
        CobblemonEvents.EV_GAINED_EVENT_PRE.subscribe(event -> {
            Pokemon pokemon = event.getPokemon();
            Item helditem = pokemon.heldItem().getItem();
            if (event.getSource().isBattle()) {
                if (helditem instanceof HeldBattleItem heldBattleItem) {
                    if (heldBattleItem.getShowdownID().equalsIgnoreCase("machobrace")) {
                        event.setAmount(event.getAmount() * 2);
                    }
                }
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> ScaleHandler.updateScales());

        // Remove the player from the map even if they do have a mega, so we can properly detect their mega pokemon even if the server doesn't restart in between log-ins
        PlatformEvents.SERVER_PLAYER_LOGOUT.subscribe(event -> {
            playersWithMega.remove(event.getPlayer().getUuid());
        });

        // I hope I learn how to do things more efficiently
        PlatformEvents.SERVER_PLAYER_LOGIN.subscribe(event -> {
            ServerPlayerEntity player = event.getPlayer();
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
            PCStore pcStore = Cobblemon.INSTANCE.getStorage().getPC(player);


            List<Pokemon> megaPokemonInParty = playerPartyStore.toGappyList().stream().filter(pokemon -> {
                if (pokemon == null) return false;
                MegaEvolutionConfig.MegaEvolutionData megastoneData = MegaEvolutionConfig.getMegaEvolution(pokemon);
                if (megastoneData == null) return false;
                if (pokemon.getFeatures().stream().anyMatch(speciesFeature -> {
                    if (speciesFeature.getName().equalsIgnoreCase(megastoneData.featureName)) {
                        if (speciesFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                            return stringSpeciesFeature.getValue().equalsIgnoreCase(megastoneData.featureValue);
                        } else if (speciesFeature instanceof FlagSpeciesFeature flagSpeciesFeature) {
                            return Boolean.toString(flagSpeciesFeature.getEnabled()).equalsIgnoreCase(megastoneData.featureValue);
                        }
                    }
                    return false;
                })) {
                    return true;
                }
                return false;
            }).toList();

            // If this whole event does it's job, this should only ever have 1 pokemon in the list. But just in case I do this extra stuff
            for (Pokemon pokemon : megaPokemonInParty) {
                if (pokemon == null) continue;
                if (playersWithMega.containsKey(player.getUuid())) {
                    CobblemonEventHandler.revertForm(pokemon, false);
                } else {
                    playersWithMega.put(player.getUuid(), pokemon.getUuid());
                }
            }

            // Do it with the PC too though
            List<Pokemon> megaPokemonInPC = new ArrayList<>();
            pcStore.getBoxes().forEach(pcBox -> pcBox.getNonEmptySlots().values().forEach(pokemon -> {
                if (pokemon != null) {
                    if (!megaPokemonInPC.contains(pokemon)) {
                        MegaEvolutionConfig.MegaEvolutionData megastoneData = MegaEvolutionConfig.getMegaEvolution(pokemon);
                        if (megastoneData != null) {
                            if (pokemon.getFeatures().stream().anyMatch(speciesFeature -> {
                                if (speciesFeature.getName().equalsIgnoreCase(megastoneData.featureName)) {
                                    if (speciesFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                                        return stringSpeciesFeature.getValue().equalsIgnoreCase(megastoneData.featureValue);
                                    } else if (speciesFeature instanceof FlagSpeciesFeature flagSpeciesFeature) {
                                        return Boolean.toString(flagSpeciesFeature.getEnabled()).equalsIgnoreCase(megastoneData.featureValue);
                                    }
                                }
                                return false;
                            })) {
                                megaPokemonInPC.add(pokemon);
                            }
                        }
                    }
                }
            }));

            for (Pokemon pokemon : megaPokemonInPC) {
                if (pokemon == null) continue;
                if (playersWithMega.containsKey(player.getUuid())) {
                    CobblemonEventHandler.revertForm(pokemon, false);
                } else {
                    playersWithMega.put(player.getUuid(), pokemon.getUuid());
                }
            }
        });
    }

    public void logError(String message) {
        LOGGER.error(message);
    }

    public void logInfo(String message) {
        if (config.debug) {
            LOGGER.info(message);
        }
    }

    public MinecraftServer getServer() {
        return server;
    }

    public FabricServerAudiences getAudiences() {
        return audiences;
    }

    public Config getConfig() {
        return config;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public Map<UUID, UUID> getPlayersWithMega() {
        return playersWithMega;
    }
}