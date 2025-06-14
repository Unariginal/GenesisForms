package me.unariginal.genesisforms.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormParticlePacket;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.config.Config;
import me.unariginal.genesisforms.handlers.FormHandler;
import me.unariginal.genesisforms.items.bagitems.terashards.TeraShardBagItems;
import me.unariginal.genesisforms.items.helditems.HeldItems;
import me.unariginal.genesisforms.items.helditems.megastones.MegaStoneHeldItems;
import me.unariginal.genesisforms.items.helditems.zcrystals.ZCrystalHeldItems;
import me.unariginal.genesisforms.polymer.BagItems;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import me.unariginal.genesisforms.utils.TextUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;

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
                                                                            player.sendMessage(TextUtils.deserialize("[Genesis] Received " + toGive.getName().getString()));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize("[Genesis] Gave " + toGive.getName().getString() + " to " + player.getNameForScoreboard()));
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
                                                                            ItemStack toGive = ZCrystalHeldItems.getInstance().getZCrystalItem(StringArgumentType.getString(ctx, "z-crystal")).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize("[Genesis] Received " + toGive.getName().getString()));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize("[Genesis] Gave " + toGive.getName().getString() + " to " + player.getNameForScoreboard()));
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
                                                                            ItemStack toGive = HeldItems.getInstance().getHeldItem(StringArgumentType.getString(ctx, "held-item")).copy();
                                                                            player.giveItemStack(toGive);
                                                                            player.sendMessage(TextUtils.deserialize("[Genesis] Received " + toGive.getName().getString()));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize("[Genesis] Gave " + toGive.getName().getString() + " to " + player.getNameForScoreboard()));
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
                                                                            player.sendMessage(TextUtils.deserialize("[Genesis] Received " + toGive.getName().getString()));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize("[Genesis] Gave " + toGive.getName().getString() + " to " + player.getNameForScoreboard()));
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
                                                                            player.sendMessage(TextUtils.deserialize("[Genesis] Received " + toGive.getName().getString()));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize("[Genesis] Gave " + toGive.getName().getString() + " to " + player.getNameForScoreboard()));
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
                                                                            player.sendMessage(TextUtils.deserialize("[Genesis] Received " + toGive.getName().getString()));
                                                                            ctx.getSource().sendMessage(TextUtils.deserialize("[Genesis] Gave " + toGive.getName().getString() + " to " + player.getNameForScoreboard()));
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
                                                                FormHandler.revert_forms(gf.getPlayersWithMega().get(player.getUuid()), false);
                                                                gf.getPlayersWithMega().remove(player.getUuid());
                                                            }
                                                            gf.getMegaEvolvedThisBattle().remove(player.getUuid());
                                                            Set<Identifier> keyItems = Cobblemon.playerDataManager.getGenericData(player).getKeyItems();
                                                            keyItems.remove(Identifier.of("cobblemon", "key_stone"));
                                                            keyItems.remove(Identifier.of("cobblemon", "dynamax_band"));
                                                            keyItems.remove(Identifier.of("cobblemon", "z_ring"));
                                                            keyItems.remove(Identifier.of("cobblemon", "tera_orb"));

                                                            ctx.getSource().sendMessage(TextUtils.deserialize("[Genesis] Reset " + player.getNameForScoreboard() + "'s internal form data."));

                                                            return 1;
                                                        })
                                        )
                        )
                        .then(
                                CommandManager.literal("reload")
                                        .requires(Permissions.require("genesisforms.reload", 4))
                                        .executes(ctx -> {
                                            gf.reload();
                                            ctx.getSource().sendMessage(TextUtils.deserialize("<green>[Genesis] Reloaded!"));
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
                        .then(
                                CommandManager.literal("convert-item")
                                        .requires(Permissions.require("genesisforms.convertItem", 4))
                                        .then(
                                                CommandManager.literal("hand")
                                                        .requires(Permissions.require("genesisforms.convertItem.hand", 4))
                                                        .executes(ctx -> {
                                                            if (ctx.getSource().getPlayer() == null) return 0;
                                                            ServerPlayerEntity player = ctx.getSource().getPlayer();

                                                            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
                                                            ItemStack converted = convertItem(stack);
                                                            if (converted == null) return 0;
                                                            player.sendMessage(TextUtils.deserialize("[Genesis] Converted " + stack.getName().getString() + " to " + converted.getName().getString() + "!"));
                                                            stack.decrement(stack.getCount());
                                                            player.giveItemStack(converted);

                                                            return 1;
                                                        })
                                        )
                                        .then(
                                                CommandManager.literal("inventory")
                                                        .requires(Permissions.require("genesisforms.convertItem.inventory", 4))
                                                        .executes(ctx -> {
                                                            if (ctx.getSource().getPlayer() == null) return 0;
                                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                            int convertedCount = 0;
                                                            for (ItemStack stack : player.getInventory().main) {
                                                                ItemStack converted = convertItem(stack);
                                                                if (converted == null) continue;
                                                                stack.decrement(stack.getCount());
                                                                player.giveItemStack(converted);
                                                                convertedCount++;
                                                            }
                                                            for (ItemStack stack : player.getInventory().offHand) {
                                                                ItemStack converted = convertItem(stack);
                                                                if (converted == null) continue;
                                                                stack.decrement(stack.getCount());
                                                                player.giveItemStack(converted);
                                                                convertedCount++;
                                                            }
                                                            for (ItemStack stack : player.getInventory().armor) {
                                                                ItemStack converted = convertItem(stack);
                                                                if (converted == null) continue;
                                                                stack.decrement(stack.getCount());
                                                                player.giveItemStack(converted);
                                                                convertedCount++;
                                                            }
                                                            player.sendMessage(TextUtils.deserialize("[Genesis] Converted " + convertedCount + " items!"));
                                                            return 1;
                                                        })
                                        )
                        )
        ));
    }

    public ItemStack convertItem(ItemStack itemStack) {
        for (Config.ItemConversion itemConversion : gf.getConfig().itemConversions) {
            if (itemConversion.input().contains(":")) {
                if (Registries.ITEM.containsId(Identifier.of(itemConversion.input()))) {
                    if (itemStack.getRegistryEntry().matchesId(Identifier.of(itemConversion.input()))) {
                        if (Registries.ITEM.containsId(Identifier.of(itemConversion.output()))) {
                            ItemStack returnStack = Registries.ITEM.get(Identifier.of(itemConversion.output())).getDefaultStack();
                            returnStack.setCount(itemStack.getCount());
                            return returnStack;
                        } else {
                            gf.logError("Invalid output item: " + itemConversion.output());
                            return null;
                        }
                    }
                }
            }
            String itemName = NbtUtils.getItemName(itemStack);
            if (itemName.toLowerCase().contains(itemConversion.input().toLowerCase())) {
                if (Registries.ITEM.containsId(Identifier.of(itemConversion.output()))) {
                    ItemStack returnStack = Registries.ITEM.get(Identifier.of(itemConversion.output())).getDefaultStack();
                    returnStack.setCount(itemStack.getCount());
                    return returnStack;
                } else {
                    gf.logError("Invalid output item: " + itemConversion.output());
                    return null;
                }
            }
            List<String> itemLore = NbtUtils.getItemLore(itemStack);
            for (String lore : itemLore) {
                if (lore.toLowerCase().contains(itemConversion.input().toLowerCase())) {
                    if (Registries.ITEM.containsId(Identifier.of(itemConversion.output()))) {
                        ItemStack returnStack = Registries.ITEM.get(Identifier.of(itemConversion.output())).getDefaultStack();
                        returnStack.setCount(itemStack.getCount());
                        return returnStack;
                    } else {
                        gf.logError("Invalid output item: " + itemConversion.output());
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
