package me.unariginal.genesisforms.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.handlers.FormHandler;
import me.unariginal.genesisforms.items.bagitems.terashards.TeraShardBagItems;
import me.unariginal.genesisforms.items.helditems.HeldItems;
import me.unariginal.genesisforms.items.helditems.megastones.MegaStoneHeldItems;
import me.unariginal.genesisforms.items.helditems.zcrystals.ZCrystalItems;
import me.unariginal.genesisforms.polymer.BagItems;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.TextUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

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
                                                                            MegaStoneHeldItems.getInstance().getAllMegaStoneIds().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            ItemStack toGive = MegaStoneHeldItems.getInstance().getMegaStoneItem(StringArgumentType.getString(ctx, "mega-stone")).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, toGive, null, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, toGive, null, 1)));
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
                                                                            for (String id : ZCrystalItems.getInstance().getAllZCrystalIds()) {
                                                                                builder.suggest(id);
                                                                            }
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            ItemStack toGive = ZCrystalItems.getInstance().getZCrystalItem(StringArgumentType.getString(ctx, "z-crystal")).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, toGive, null, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, toGive, null, 1)));
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
                                                                            HeldItems.getInstance().getAllHeldItemIds().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            String itemId = StringArgumentType.getString(ctx, "held-item");
                                                                            ItemStack toGive = HeldItems.getInstance().getHeldItem(itemId).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, toGive, null, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, toGive, null, 1)));
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
                                                                            KeyItems.keyItemStacks.keySet().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            ItemStack toGive = KeyItems.keyItemStacks.get(StringArgumentType.getString(ctx, "key-item")).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, toGive, null, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, toGive, null, 1)));
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
                                                                            BagItems.bagItemStacks.keySet().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            ItemStack toGive = BagItems.bagItemStacks.get(StringArgumentType.getString(ctx, "bag-item")).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, toGive, null, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, toGive, null, 1)));
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
                                                                            TeraShardBagItems.getInstance().getAllTeraShardIds().forEach(builder::suggest);
                                                                            return builder.buildFuture();
                                                                        })
                                                                        .executes(ctx -> {
                                                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                            if (player == null) return 0;
                                                                            ItemStack toGive = TeraShardBagItems.getInstance().getTeraShard(StringArgumentType.getString(ctx, "tera-shard")).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_received"), player, toGive, null, 1)));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("give_command_feedback"), player, toGive, null, 1)));
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
                                                            if (gf.getPlayersWithMega().containsKey(player.getUuid())) {
                                                                FormHandler.revertForms(gf.getPlayersWithMega().get(player.getUuid()), false);
                                                                gf.getPlayersWithMega().remove(player.getUuid());
                                                            }
                                                            gf.getMegaEvolvedThisBattle().remove(player.getUuid());
                                                            Set<Identifier> keyItems = Cobblemon.playerDataManager.getGenericData(player).getKeyItems();
                                                            keyItems.remove(Identifier.of("cobblemon", "key_stone"));
                                                            keyItems.remove(Identifier.of("cobblemon", "dynamax_band"));
                                                            keyItems.remove(Identifier.of("cobblemon", "z_ring"));
                                                            keyItems.remove(Identifier.of("cobblemon", "tera_orb"));

                                                            ctx.getSource().sendMessage(TextUtils.deserialize(TextUtils.parse(gf.getMessagesConfig().getMessage("reset_data_command"), player)));

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
