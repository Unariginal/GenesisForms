package me.unariginal.genesisforms;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import kotlin.Unit;
import me.unariginal.genesisforms.commands.GenesisCommands;
import me.unariginal.genesisforms.config.BattleFormChangeConfig;
import me.unariginal.genesisforms.config.Config;
import me.unariginal.genesisforms.config.EventsConfig;
import me.unariginal.genesisforms.config.MessagesConfig;
import me.unariginal.genesisforms.config.items.MiscItemsConfig;
import me.unariginal.genesisforms.config.items.accessories.AccessoriesConfig;
import me.unariginal.genesisforms.config.items.bagitems.MaxItemsConfig;
import me.unariginal.genesisforms.config.items.bagitems.TeraShardsConfig;
import me.unariginal.genesisforms.config.items.helditems.HeldBattleItemsConfig;
import me.unariginal.genesisforms.config.items.helditems.HeldFormItemsConfig;
import me.unariginal.genesisforms.config.items.helditems.MegastonesConfig;
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
        return new Identifier(MOD_ID, path);
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
            MegastonesConfig.load();
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
        CobblemonEvents.HELD_ITEM_POST.subscribe(Priority.NORMAL, CobblemonEventHandler::heldItemChange);
        CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(Priority.NORMAL, CobblemonEventHandler::pokemonReleasedEvent);
        CobblemonEvents.POKEMON_GAINED.subscribe(Priority.NORMAL, CobblemonEventHandler::pokemonGainedEvent);
        CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.NORMAL, CobblemonEventHandler::pokemonSentEvent);

        CobblemonEvents.FORME_CHANGE.subscribe(Priority.NORMAL, CobblemonEventHandler::formChangeEvent);
        CobblemonEvents.MEGA_EVOLUTION.subscribe(Priority.NORMAL, CobblemonEventHandler::megaEvolveEvent);
        CobblemonEvents.TERASTALLIZATION.subscribe(Priority.NORMAL, CobblemonEventHandler::terastallizationEvent);
        CobblemonEvents.ZPOWER_USED.subscribe(Priority.NORMAL, CobblemonEventHandler::zPowerEvent);
        UltraBurstHandler.register();
        DynamaxHandler.register();

        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.NORMAL, CobblemonEventHandler::battleStartEvent);
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.NORMAL, CobblemonEventHandler::battleFaintEvent);
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, CobblemonEventHandler::battleEndEvent);
        CobblemonEvents.BATTLE_FLED.subscribe(Priority.NORMAL, CobblemonEventHandler::battleFledEvent);

        // Prevent trading mega pokemon, without stopping other mods from doing their own tradeable changes
        CobblemonEvents.TRADE_EVENT_PRE.subscribe(Priority.NORMAL, CobblemonEventHandler::tradeEvent);

        // Macho brace
        CobblemonEvents.EV_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, event -> {
            Pokemon pokemon = event.getPokemon();
            Item helditem = pokemon.heldItem().getItem();
            if (event.getSource().isBattle()) {
                if (helditem instanceof HeldBattleItem heldBattleItem) {
                    if (heldBattleItem.getShowdownID().equalsIgnoreCase("machobrace")) {
                        event.setAmount(event.getAmount() * 2);
                    }
                }
            }
            return Unit.INSTANCE;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> ScaleHandler.updateScales());
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