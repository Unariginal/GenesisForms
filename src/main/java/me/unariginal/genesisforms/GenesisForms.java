package me.unariginal.genesisforms;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.helditem.HeldItemProvider;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import me.unariginal.genesisforms.commands.GenesisCommands;
import me.unariginal.genesisforms.config.AnimationConfig;
import me.unariginal.genesisforms.config.Config;
import me.unariginal.genesisforms.config.ItemSettingsConfig;
import me.unariginal.genesisforms.handlers.*;
import me.unariginal.genesisforms.items.bagitems.terashards.TeraShardBagItems;
import me.unariginal.genesisforms.items.helditems.HeldItems;
import me.unariginal.genesisforms.items.helditems.megastones.MegaStoneHeldItems;
import me.unariginal.genesisforms.items.helditems.zcrystals.ZCrystalHeldItems;
import me.unariginal.genesisforms.polymer.BagItems;
import me.unariginal.genesisforms.polymer.KeyItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
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
    private ItemSettingsConfig itemSettings = new ItemSettingsConfig();
    private AnimationConfig animationConfig = new AnimationConfig();

    private final Map<UUID, Pokemon> players_with_mega = new HashMap<>();
    private final List<UUID> mega_evolved_this_battle = new ArrayList<>();
    private final List<UUID> ultra_burst_this_battle = new ArrayList<>();
    private final Map<UUID, String> tera_pokemon_entities = new HashMap<>();

    @Override
    public void onInitialize() {
        INSTANCE = this;

        new GenesisCommands();

        PolymerResourcePackUtils.markAsRequired();
        PolymerResourcePackUtils.addModAssets(MOD_ID);

        KeyItems.requestModel();
        KeyItems.registerItemGroup();

        BagItems.requestModel();
        BagItems.registerItemGroup();

        HeldItems.getInstance().loadHeldItemIds();
        HeldItems.getInstance().fillPolymerItems();
        HeldItems.getInstance().fillPolymerModelData();
        HeldItems.getInstance().registerItemGroup();

        MegaStoneHeldItems.getInstance().loadMegaStoneIds();
        MegaStoneHeldItems.getInstance().fillPolymerItems();
        MegaStoneHeldItems.getInstance().fillPolymerModelData();
        MegaStoneHeldItems.getInstance().registerItemGroup();

        TeraShardBagItems.getInstance().loadTeraShardIds();
        TeraShardBagItems.getInstance().fillPolymerItems();
        TeraShardBagItems.getInstance().fillPolymerModelData();
        TeraShardBagItems.getInstance().registerItemGroup();

        ZCrystalHeldItems.getInstance().loadZCrystalIds();
        ZCrystalHeldItems.getInstance().fillPolymerItems();
        ZCrystalHeldItems.getInstance().fillPolymerModelData();
        ZCrystalHeldItems.getInstance().registerItemGroup();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
            this.audiences = FabricServerAudiences.of(server);

            HeldItemProvider.INSTANCE.register(MegaStoneHeldItems.getInstance(), Priority.HIGH);
            HeldItemProvider.INSTANCE.register(ZCrystalHeldItems.getInstance(), Priority.HIGH);
            HeldItemProvider.INSTANCE.register(HeldItems.getInstance(), Priority.HIGH);

            CobblemonEvents.HELD_ITEM_POST.subscribe(Priority.NORMAL, HeldItemHandler::held_item_change);

            CobblemonEvents.MEGA_EVOLUTION.subscribe(Priority.NORMAL, MegaEvolutionHandler::mega_event);
            CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(Priority.NORMAL, MegaEvolutionHandler::pokemon_released);
            CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.NORMAL, MegaEvolutionHandler::handleMegaRayquaza);

            CobblemonEvents.TERASTALLIZATION.subscribe(Priority.NORMAL, TeraHandler::teraEvent);
            CobblemonEvents.POKEMON_SENT_POST.subscribe(Priority.NORMAL, TeraHandler::revertTera);
            CobblemonEvents.POKEMON_GAINED.subscribe(Priority.NORMAL, TeraHandler::setProperTeraTypes);

            CobblemonEvents.ZPOWER_USED.subscribe(Priority.NORMAL, ZPowerHandler::playAnimation);

            CobblemonEvents.FORME_CHANGE.subscribe(Priority.NORMAL, FormHandler::form_changes);

            CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.NORMAL, BattleHandler::battle_started);
            CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.NORMAL, BattleHandler::battle_faint);
            CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, BattleHandler::battle_ended);
            CobblemonEvents.BATTLE_FLED.subscribe(Priority.NORMAL, BattleHandler::battle_fled);
            CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.NORMAL, BattleHandler::pokemon_faint);

            DynamaxHandler.register();
        });

        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, server) -> {
            ServerPlayerEntity player = serverPlayNetworkHandler.player;
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : playerPartyStore) {
                if (pokemon != null) {
                    FormHandler.revert_forms(pokemon, false);
                }
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, server) -> {
            ServerPlayerEntity player = serverPlayNetworkHandler.player;
            PlayerPartyStore playerPartyStore = Cobblemon.INSTANCE.getStorage().getParty(player);
            for (Pokemon pokemon : playerPartyStore) {
                if (pokemon != null) {
                    FormHandler.revert_forms(pokemon, false);
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

    public void reload() {
        this.config = new Config();
        this.itemSettings = new ItemSettingsConfig();
        this.animationConfig = new AnimationConfig();
    }

    public Config getConfig() {
        return config;
    }

    public ItemSettingsConfig getItemSettings() {
        return itemSettings;
    }

    public AnimationConfig getAnimationConfig() {
        return animationConfig;
    }

    public Map<UUID, Pokemon> getPlayersWithMega() {
        return players_with_mega;
    }

    public List<UUID> getMegaEvolvedThisBattle() {
        return mega_evolved_this_battle;
    }

    public List<UUID> getUltra_burst_this_battle() {
        return ultra_burst_this_battle;
    }

    public Map<UUID, String> getTeraPokemonEntities() {
        return tera_pokemon_entities;
    }
}
