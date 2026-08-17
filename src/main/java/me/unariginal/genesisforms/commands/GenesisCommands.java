package me.unariginal.genesisforms.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.handlers.CobblemonEventHandler;
import me.unariginal.genesisforms.items.helditems.Megastone;
import me.unariginal.genesisforms.items.helditems.ZCrystal;
import me.unariginal.genesisforms.items.keyitems.accessories.TeraAccessory;
import me.unariginal.genesisforms.polymer.*;
import me.unariginal.genesisforms.utils.TextUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.unariginal.genesisforms.config.ConfigManager.MESSAGES;

public class GenesisCommands {
    private final GenesisForms gf = GenesisForms.INSTANCE;
    
    public GenesisCommands() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(
                CommandManager.literal("genesis")
                        .then(
                                CommandManager.literal("giveMegaStone")
                                        .requires(Permissions.require("genesisforms.giveMegaStone", 4))
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .then(
                                                                CommandManager.argument("mega-stone", StringArgumentType.string())
                                                                        .suggests((context, builder) -> {
                                                                            MegastonesGroup.megastones.keySet().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            String itemId = StringArgumentType.getString(ctx, "mega-stone");
                                                                            Megastone megastone = MegastonesGroup.megastones.get(itemId);
                                                                            if (megastone == null) return 0;
                                                                            ItemStack toGive = megastone.getDefaultStack();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandReceived, player, megastone.getItemId(), 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandFeedback, player, megastone.getItemId(), 1)));
                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                CommandManager.literal("giveZCrystal")
                                        .requires(Permissions.require("genesisforms.giveZCrystal", 4))
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .then(
                                                                CommandManager.argument("z-crystal", StringArgumentType.string())
                                                                        .suggests((ctx, builder) -> {
                                                                            ZCrystalsGroup.zCrystals.keySet().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            String itemId = StringArgumentType.getString(ctx, "z-crystal");
                                                                            ZCrystal zCrystal = ZCrystalsGroup.zCrystals.get(itemId);
                                                                            if (zCrystal == null) return 0;
                                                                            ItemStack toGive = zCrystal.getDefaultStack();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandReceived, player, itemId, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandFeedback, player, itemId, 1)));
                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                CommandManager.literal("giveHeldItem")
                                        .requires(Permissions.require("genesisforms.giveHeldItem", 4))
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .then(
                                                                CommandManager.argument("held-item", StringArgumentType.string())
                                                                        .suggests((context, builder) -> {
                                                                            HeldItemsGroup.allHeldItems.keySet().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            String itemId = StringArgumentType.getString(ctx, "held-item");
                                                                            ItemStack toGive = HeldItemsGroup.allHeldItems.get(itemId);
                                                                            if (toGive == null) return 0;
                                                                            player.giveItemStack(toGive.copy());
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandReceived, player, itemId, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandFeedback, player, itemId, 1)));
                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                CommandManager.literal("giveKeyItem")
                                        .requires(Permissions.require("genesisforms.giveKeyItem", 4))
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .then(
                                                                CommandManager.argument("key-item", StringArgumentType.string())
                                                                        .suggests((context, builder) -> {
                                                                            KeyItemsGroup.allKeyItems.keySet().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            String itemId = StringArgumentType.getString(ctx, "key-item");
                                                                            ItemStack toGive = KeyItemsGroup.allKeyItems.get(itemId);
                                                                            if (toGive == null) return 0;
                                                                            player.giveItemStack(toGive.copy());
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandReceived, player, itemId, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandFeedback, player, itemId, 1)));
                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                CommandManager.literal("giveBagItem")
                                        .requires(Permissions.require("genesisforms.giveBagItem", 4))
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .then(
                                                                CommandManager.argument("bag-item", StringArgumentType.string())
                                                                        .suggests((context, builder) -> {
                                                                            BagItemsGroup.bagItems.keySet().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            String itemId = StringArgumentType.getString(ctx, "bag-item");
                                                                            Item toGive = BagItemsGroup.bagItems.get(itemId);
                                                                            if (toGive == null) return 0;
                                                                            player.giveItemStack(toGive.getDefaultStack());
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandReceived, player, itemId, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandFeedback, player, itemId, 1)));
                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                CommandManager.literal("giveTeraShard")
                                        .requires(Permissions.require("genesisforms.giveTeraShard", 4))
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .then(
                                                                CommandManager.argument("tera-shard", StringArgumentType.string())
                                                                        .suggests((ctx, builder) -> {
                                                                            TeraShardsGroup.teraShards.keySet().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            String itemId = StringArgumentType.getString(ctx, "tera-shard");
                                                                            Item toGive = TeraShardsGroup.teraShards.get(itemId);
                                                                            if (toGive == null) return 0;
                                                                            player.giveItemStack(toGive.getDefaultStack());
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandReceived, player, itemId, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.giveCommandFeedback, player, itemId, 1)));
                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                CommandManager.literal("resetData")
                                        .requires(Permissions.require("genesisforms.resetData", 4))
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .executes(ctx -> {
                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
                                                            PCStore pc = Cobblemon.INSTANCE.getStorage().getPC(player);
                                                            for (Pokemon pokemon : party) {
                                                                if (pokemon != null) {
                                                                    CobblemonEventHandler.revertForm(pokemon, false);
                                                                }
                                                            }
                                                            for (Pokemon pokemon : pc) {
                                                                if (pokemon != null) {
                                                                    CobblemonEventHandler.revertForm(pokemon, false);
                                                                }
                                                            }

                                                            gf.playersWithMega.remove(player.getUuid());

                                                            GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);
                                                            playerData.getKeyItems().removeIf(identifier ->
                                                                    identifier.equals(MiscUtilsKt.cobblemonResource("key_stone")) ||
                                                                            identifier.equals(MiscUtilsKt.cobblemonResource("tera_orb")) ||
                                                                            identifier.equals(MiscUtilsKt.cobblemonResource("z_ring")) ||
                                                                            identifier.equals(MiscUtilsKt.cobblemonResource("dynamax_band")));

                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.resetDataCommand, player)));

                                                            return 1;
                                                        })
                                        )
                        )
                        .then(
                                CommandManager.literal("setOrbUsage")
                                        .requires(Permissions.require("genesisforms.setOrbUsage", 4))
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .then(
                                                                CommandManager.argument("percent", IntegerArgumentType.integer(0, 100))
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            int percent = IntegerArgumentType.getInteger(ctx, "percent");
                                                                            if (player.getMainHandStack().getItem() instanceof TeraAccessory) {
                                                                                player.getMainHandStack().setDamage(percent);
                                                                            } else {
                                                                                for (ItemStack itemStack : CobblemonEventHandler.getValidKeyItemSlots(player)) {
                                                                                    if (itemStack.getItem() instanceof TeraAccessory) {
                                                                                        itemStack.setDamage(percent);
                                                                                    }
                                                                                }
                                                                            }
                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                CommandManager.literal("rechargeOrb")
                                        .requires(Permissions.require("genesisforms.rechargeOrb", 4))
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .executes(ctx -> {
                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                            if (player.getMainHandStack().getItem() instanceof TeraAccessory) {
                                                                player.getMainHandStack().setDamage(0);
                                                            } else {
                                                                for (ItemStack itemStack : CobblemonEventHandler.getValidKeyItemSlots(player)) {
                                                                    if (itemStack.getItem() instanceof TeraAccessory) {
                                                                        itemStack.setDamage(0);
                                                                    }
                                                                }
                                                            }
                                                            return 1;
                                                        })
                                        )
                        )
                        .then(
                                CommandManager.literal("reload")
                                        .requires(Permissions.require("genesisforms.reload", 4))
                                        .executes(ctx -> {
                                            gf.reload();
                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(MESSAGES.messages.reloadCommand)));
                                            return 1;
                                        })
                        )
        ));
    }
}
