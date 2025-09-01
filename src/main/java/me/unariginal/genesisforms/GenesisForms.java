package me.unariginal.genesisforms;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import kotlin.Unit;
import me.unariginal.genesisforms.commands.GenesisCommands;
import me.unariginal.genesisforms.config.AnimationConfig;
import me.unariginal.genesisforms.config.Config;
import me.unariginal.genesisforms.config.ItemConfigs;
import me.unariginal.genesisforms.config.MessagesConfig;
import me.unariginal.genesisforms.data.DataComponents;
import me.unariginal.genesisforms.handlers.*;
import me.unariginal.genesisforms.items.bagitems.terashards.TeraShardBagItems;
import me.unariginal.genesisforms.items.helditems.HeldItems;
import me.unariginal.genesisforms.items.helditems.megastones.MegaStoneHeldItems;
import me.unariginal.genesisforms.items.helditems.zcrystals.ZCrystalItems;
import me.unariginal.genesisforms.items.keyitems.FusionItems;
import me.unariginal.genesisforms.items.keyitems.KeyFormItems;
import me.unariginal.genesisforms.items.keyitems.PossessionBlockItems;
import me.unariginal.genesisforms.polymer.BagItems;
import me.unariginal.genesisforms.polymer.KeyItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GenesisForms implements ModInitializer {
    public static final String MOD_ID = "genesisforms";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static GenesisForms INSTANCE;

    private MinecraftServer server;
    private FabricServerAudiences audiences;

    private Config config = new Config();
    private ItemConfigs itemSettings = new ItemConfigs();
    private AnimationConfig animationConfig = new AnimationConfig();
    private MessagesConfig messagesConfig = new MessagesConfig();

    private GenesisCommands commands;

    private final Map<UUID, Pokemon> players_with_mega = new HashMap<>();
    private final List<UUID> mega_evolved_this_battle = new ArrayList<>();
    private final Map<UUID, String> tera_pokemon_entities = new HashMap<>();

    @Override
    public void onInitialize() {
        INSTANCE = this;

        commands = new GenesisCommands();

        PolymerResourcePackUtils.markAsRequired();
        PolymerResourcePackUtils.addModAssets(MOD_ID);

        registerItems();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
            this.audiences = FabricServerAudiences.of(server);

            registerEvents();
        });

        PlatformEvents.SERVER_PLAYER_LOGIN.subscribe(Priority.NORMAL, event -> {
            if (config.convertOnJoin) {
                ServerPlayerEntity player = event.getPlayer();
                for (ItemStack stack : player.getInventory().main) {
                    ItemStack converted = commands.convertItem(stack);
                    if (converted == null) continue;
                    stack.decrement(stack.getCount());
                    player.giveItemStack(converted);
                }
                for (ItemStack stack : player.getInventory().offHand) {
                    ItemStack converted = commands.convertItem(stack);
                    if (converted == null) continue;
                    stack.decrement(stack.getCount());
                    player.giveItemStack(converted);
                }
                for (ItemStack stack : player.getInventory().armor) {
                    ItemStack converted = commands.convertItem(stack);
                    if (converted == null) continue;
                    stack.decrement(stack.getCount());
                    player.giveItemStack(converted);
                }
                for (Pokemon pokemon : Cobblemon.INSTANCE.getStorage().getParty(player)) {
                    if (pokemon == null) continue;
                    if (pokemon.heldItem().isEmpty()) continue;
                    ItemStack heldItem = pokemon.heldItem();
                    ItemStack converted = commands.convertItem(heldItem);
                    if (converted == null) continue;
                    pokemon.setHeldItem$common(converted);
                }
            }
            return Unit.INSTANCE;
        });
    }

    private void registerItems() {
        KeyFormItems.getInstance().loadKeyItemIds();
        KeyFormItems.getInstance().fillPolymerModelData();
        KeyFormItems.getInstance().fillPolymerItems();

        FusionItems.getInstance().loadFusionItemIds();
        FusionItems.getInstance().fillPolymerModelData();
        FusionItems.getInstance().fillPolymerItems();

        PossessionBlockItems.getInstance().loadPossessionItemIds();
        PossessionBlockItems.getInstance().fillPolymerModelData();
        PossessionBlockItems.getInstance().fillPolymerItems();

        KeyItems.requestModel();
        KeyItems.registerItems();
        KeyItems.registerItemGroup();

        BagItems.requestModel();
        BagItems.registerItems();
        BagItems.registerItemGroup();

        HeldItems.getInstance().register();

        MegaStoneHeldItems.getInstance().loadMegaStoneIds();
        MegaStoneHeldItems.getInstance().fillPolymerModelData();
        MegaStoneHeldItems.getInstance().fillPolymerItems();
        MegaStoneHeldItems.getInstance().registerItemGroup();

        TeraShardBagItems.getInstance().loadTeraShardIds();
        TeraShardBagItems.getInstance().fillPolymerModelData();
        TeraShardBagItems.getInstance().fillPolymerItems();
        TeraShardBagItems.getInstance().registerItemGroup();

        ZCrystalItems.getInstance().register();
    }

    private void registerEvents() {
        CobblemonEvents.HELD_ITEM_POST.subscribe(Priority.NORMAL, HeldItemHandler::heldItemChange);

        CobblemonEvents.MEGA_EVOLUTION.subscribe(Priority.NORMAL, MegaEvolutionHandler::megaEvent);
        CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(Priority.NORMAL, MegaEvolutionHandler::pokemon_released);
        CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.NORMAL, MegaEvolutionHandler::handleMegaRayquaza);

        CobblemonEvents.TERASTALLIZATION.subscribe(Priority.NORMAL, TeraHandler::teraEvent);
        CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.NORMAL, TeraHandler::revertTera);
        CobblemonEvents.POKEMON_GAINED.subscribe(Priority.NORMAL, TeraHandler::setProperTeraTypes);

        CobblemonEvents.ZPOWER_USED.subscribe(Priority.NORMAL, ZPowerHandler::playAnimation);

        CobblemonEvents.FORME_CHANGE.subscribe(Priority.NORMAL, FormHandler::formChanges);

        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.NORMAL, BattleHandler::battleStarted);
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.NORMAL, BattleHandler::battleFaint);
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, BattleHandler::battleEnded);
        CobblemonEvents.BATTLE_FLED.subscribe(Priority.NORMAL, BattleHandler::battleFled);
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.NORMAL, BattleHandler::pokemonFaint);

        CobblemonEvents.EV_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, event -> {
            Pokemon pokemon = event.getPokemon();
            ComponentMap components = pokemon.heldItem().getComponents();
            if (event.getSource().isBattle()) {
                if (components.contains(DataComponents.HELD_ITEM)) {
                    String heldItemComponent = components.get(DataComponents.HELD_ITEM);
                    if (heldItemComponent != null && !heldItemComponent.isEmpty()) {
                        if (heldItemComponent.equalsIgnoreCase("macho_brace") || heldItemComponent.equalsIgnoreCase("machobrace")) {
                            event.setAmount(event.getAmount() * 2);
                        }
                    }
                }
            }
            return Unit.INSTANCE;
        });

        DynamaxHandler.register();
        UltraBurstHandler.register();
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

    public GenesisCommands getGenesisCommands() {
        return commands;
    }

    public void reload() {
        this.config = new Config();
        this.itemSettings = new ItemConfigs();
        this.animationConfig = new AnimationConfig();
        this.messagesConfig = new MessagesConfig();
    }

    public Config getConfig() {
        return config;
    }

    public ItemConfigs getItemSettings() {
        return itemSettings;
    }

    public AnimationConfig getAnimationConfig() {
        return animationConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public Map<UUID, Pokemon> getPlayersWithMega() {
        return players_with_mega;
    }

    public List<UUID> getMegaEvolvedThisBattle() {
        return mega_evolved_this_battle;
    }

    public Map<UUID, String> getTeraPokemonEntities() {
        return tera_pokemon_entities;
    }
}