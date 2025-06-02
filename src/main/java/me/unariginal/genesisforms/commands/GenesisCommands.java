package me.unariginal.genesisforms.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormParticlePacket;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.handlers.FormHandler;
import me.unariginal.genesisforms.items.bagitems.terashards.TeraShardBagItems;
import me.unariginal.genesisforms.items.helditems.HeldItems;
import me.unariginal.genesisforms.items.helditems.megastones.MegaStoneHeldItems;
import me.unariginal.genesisforms.items.helditems.zcrystals.ZCrystalHeldItems;
import me.unariginal.genesisforms.polymer.BagItems;
import me.unariginal.genesisforms.polymer.KeyItems;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Set;

public class GenesisCommands {
    public GenesisCommands() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(
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
                                                                                player.giveItemStack(MegaStoneHeldItems.getInstance().getMegaStoneItem(
                                                                                        StringArgumentType.getString(ctx, "mega-stone")
                                                                                ));
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
                                                                                for (String id : ZCrystalHeldItems.getInstance().getAllZCrystalIds()) {
                                                                                    builder.suggest(id);
                                                                                }
                                                                                return builder.buildFuture();
                                                                            })
                                                                            .executes(ctx -> {
                                                                                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                                                if (player == null) return 0;
                                                                                player.giveItemStack(ZCrystalHeldItems.getInstance().getZCrystalItem(
                                                                                        StringArgumentType.getString(ctx, "z-crystal")
                                                                                ));
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
                                                                                player.giveItemStack(HeldItems.getInstance().getHeldItem(
                                                                                        StringArgumentType.getString(ctx, "held-item")
                                                                                ));
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
                                                                                player.giveItemStack(KeyItems.keyItemStacks.get(
                                                                                        StringArgumentType.getString(ctx, "key-item")
                                                                                ));
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
                                                                                player.giveItemStack(BagItems.bagItemStacks.get(
                                                                                        StringArgumentType.getString(ctx, "bag-item")
                                                                                ));
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
                                                                                player.giveItemStack(TeraShardBagItems.getInstance().getTeraShard(StringArgumentType.getString(ctx, "tera-shard")));
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
                                                                if (GenesisForms.INSTANCE.getPlayersWithMega().containsKey(player.getUuid())) {
                                                                    FormHandler.revert_forms(GenesisForms.INSTANCE.getPlayersWithMega().get(player.getUuid()), false);
                                                                    GenesisForms.INSTANCE.getPlayersWithMega().remove(player.getUuid());
                                                                }
                                                                GenesisForms.INSTANCE.getMegaEvolvedThisBattle().remove(player.getUuid());
                                                                GenesisForms.INSTANCE.getUltra_burst_this_battle().remove(player.getUuid());
                                                                Set<Identifier> keyItems = Cobblemon.playerDataManager.getGenericData(player).getKeyItems();
                                                                keyItems.remove(Identifier.of("cobblemon", "key_stone"));
                                                                keyItems.remove(Identifier.of("cobblemon", "dynamax_band"));
                                                                keyItems.remove(Identifier.of("cobblemon", "z_ring"));

                                                                return 1;
                                                            })
                                            )
                            )
                            .then(
                                    CommandManager.literal("reload")
                                            .requires(Permissions.require("genesisforms.reload", 4))
                                            .executes(ctx -> {
                                                GenesisForms.INSTANCE.reload();
                                                return 1;
                                            })
                            )
                            .then(
                                    CommandManager.literal("testParticles")
                                            .requires(Permissions.require("genesisforms.testParticles", 4))
                                            .then(
                                                    CommandManager.argument("identifier", StringArgumentType.string())
                                                            .then(
                                                                    CommandManager.argument("boolean", StringArgumentType.string())
                                                                            .suggests((ctx, builder) -> {
                                                                                builder.suggest("true");
                                                                                builder.suggest("false");
                                                                                return builder.buildFuture();
                                                                            })
                                                                            .executes(ctx -> {
                                                                                if (ctx.getSource().getPlayer() == null) return 0;
                                                                                new SpawnSnowstormParticlePacket(Identifier.of("cobblemon:" + StringArgumentType.getString(ctx, "identifier")), ctx.getSource().getPlayer().getPos().add(2, 0.5, 0)).sendToPlayersAround(ctx.getSource().getPlayer().getX(), ctx.getSource().getPlayer().getY(), ctx.getSource().getPlayer().getZ(), 64.0, ctx.getSource().getPlayer().getWorld().getRegistryKey(), (p) -> Boolean.getBoolean(StringArgumentType.getString(ctx, "boolean")));
                                                                                return 1;
                                                                            })
                                                            )
                                            )
                            )
            );
        });
    }
}
