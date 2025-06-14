package me.unariginal.genesisforms.utils;

import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.genesisforms.GenesisForms;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TextUtils {
    private final static GenesisForms gf = GenesisForms.INSTANCE;

    public static Text deserialize(String text) {
        return GenesisForms.INSTANCE.getAudiences().toNative(MiniMessage.miniMessage().deserialize("<!i>" + text));
    }

    public static String parse(String text) {
        return text.replaceAll("%prefix%", gf.getMessagesConfig().prefix);
    }

    public static String parse(String text, ServerPlayerEntity player) {
        text = parse(text);
        text = text
                .replaceAll("%player%", player.getNameForScoreboard()
                .replaceAll("%player.uuid%", player.getUuid().toString()));
        return text;
    }

    public static String parse(String text, ServerPlayerEntity player, ItemStack originalItem, ItemStack additionalItem, int count) {
        text = parse(text, player);
        text = text
                .replaceAll("%original_item%", originalItem != null ? originalItem.getName().getString() : "")
                .replaceAll("%new_item%", additionalItem != null ? additionalItem.getName().getString() : "")
                .replaceAll("%item%", originalItem != null ? originalItem.getName().getString() : "")
                .replaceAll("%count%", String.valueOf(count));
        return text;
    }

    public static String parse(String text, Pokemon pokemon) {
        text = parse(text);
        text = text
                .replaceAll("%pokemon%", pokemon.getDisplayName().getString())
                .replaceAll("%pokemon.uuid%", pokemon.getUuid().toString())
                .replaceAll("%pokemon.tera_type%", pokemon.getTeraType().getDisplayName().getString());
        return text;
    }
}
