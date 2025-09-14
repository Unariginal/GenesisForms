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
import me.unariginal.genesisforms.items.keyitems.accessories.TeraAccessory;
import me.unariginal.genesisforms.polymer.*;
import me.unariginal.genesisforms.utils.TextUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

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
                                                                            ItemStack toGive = MegastonesGroup.megastones.get(StringArgumentType.getString(ctx, "mega-stone")).getDefaultStack();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, StringArgumentType.getString(ctx, "mega-stone"), 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, StringArgumentType.getString(ctx, "mega-stone"), 1)));
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
                                                                            ItemStack toGive = ZCrystalsGroup.zCrystals.get(StringArgumentType.getString(ctx, "z-crystal")).getDefaultStack();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, StringArgumentType.getString(ctx, "z-crystal"), 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, StringArgumentType.getString(ctx, "z-crystal"), 1)));
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
                                                                            ItemStack toGive = HeldItemsGroup.allHeldItems.get(itemId).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, itemId, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, itemId, 1)));
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
                                                                            ItemStack toGive = KeyItemsGroup.allKeyItems.get(StringArgumentType.getString(ctx, "key-item")).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, StringArgumentType.getString(ctx, "key-item"), 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, StringArgumentType.getString(ctx, "key-item"), 1)));
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
                                                                            ItemStack toGive = BagItemsGroup.bagItems.get(StringArgumentType.getString(ctx, "bag-item")).getDefaultStack();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, StringArgumentType.getString(ctx, "bag-item"), 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, StringArgumentType.getString(ctx, "bag-item"), 1)));
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
                                                                            ItemStack toGive = TeraShardsGroup.teraShards.get(StringArgumentType.getString(ctx, "tera-shard")).getDefaultStack();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, StringArgumentType.getString(ctx, "tera-shard"), 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, StringArgumentType.getString(ctx, "tera-shard"), 1)));
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

                                                            gf.getPlayersWithMega().remove(player.getUuid());

                                                            GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);
                                                            playerData.getKeyItems().removeIf(identifier ->
                                                                    identifier.equals(MiscUtilsKt.cobblemonResource("key_stone")) ||
                                                                            identifier.equals(MiscUtilsKt.cobblemonResource("tera_orb")) ||
                                                                            identifier.equals(MiscUtilsKt.cobblemonResource("z_ring")) ||
                                                                            identifier.equals(MiscUtilsKt.cobblemonResource("dynamax_band")));

                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("reset_data_command"), player)));

                                                            return 1;
                                                        })
                                        )
                        )
                        .then(
                                CommandManager.literal("setOrbUsage")
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
                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("reload_command"))));
                                            return 1;
                                        })
                        )
        ));
    }
}
