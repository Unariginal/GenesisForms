package me.unariginal.genesisforms.utils;

import me.unariginal.genesisforms.GenesisForms;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.text.Text;

public class TextUtils {
    public static Text deserialize(String text) {
        return GenesisForms.INSTANCE.getAudiences().toNative(MiniMessage.miniMessage().deserialize("<!i>" + text));
    }
}
