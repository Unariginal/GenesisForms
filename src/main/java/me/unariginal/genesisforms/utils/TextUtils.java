package me.unariginal.genesisforms.utils;

import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.genesisforms.GenesisForms;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static me.unariginal.genesisforms.config.ConfigManager.MESSAGES;

public class TextUtils {
    public static Text deserialize(String text) {
        return GenesisForms.INSTANCE.audiences.toNative(MiniMessage.miniMessage().deserialize("<!i>" + text));
    }

    public static String parse(String text) {
        return text.replace("%prefix%", MESSAGES.prefix);
    }

    public static String parse(String text, ServerPlayerEntity player) {
        text = parse(text);
        text = text
                .replace("%player%", player.getNameForScoreboard())
                .replace("%player.uuid%", player.getUuid().toString());
        return text;
    }

    public static String parse(String text, ServerPlayerEntity player, String itemName, int count) {
        text = parse(text, player);
        text = text
                .replace("%item%", Text.translatable("item.genesisforms." + itemName).getString())
                .replace("%count%", String.valueOf(count));
        return text;
    }

    public static String parse(String text, Pokemon pokemon) {
        text = parse(text);
        text = text
                .replace("%pokemon%", pokemon.getDisplayName(false).getString())
                .replace("%pokemon.uuid%", pokemon.getUuid().toString())
                .replace("%pokemon.tera_type%", pokemon.getTeraType().getDisplayName().getString())
                .replace("%pokemon.dmax_level%", String.valueOf(pokemon.getDmaxLevel()));
        return text;
    }
}
