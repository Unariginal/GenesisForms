package me.unariginal.genesisforms.utils;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.component.DataComponentTypes.*;

public class NbtUtils {
    public static void setNbtString(@NotNull ItemStack itemStack, @NotNull String namespace, @NotNull String key, @NotNull String value) {
        if (key.isEmpty() || value.isEmpty()) {
            return;
        }

        itemStack.apply(CUSTOM_DATA, NbtComponent.DEFAULT, current -> {
            NbtCompound newNbt = current.copyNbt();

            if (namespace.isEmpty()) {
                // root namespace
                newNbt.putString(key, value);
                return NbtComponent.of(newNbt);
            }

            NbtCompound modNbt = newNbt.getCompound(namespace);
            modNbt.putString(key, value);
            newNbt.put(namespace, modNbt);
            return NbtComponent.of(newNbt);
        });
    }

    public static void setItemName(@NotNull ItemStack itemStack, @NotNull String value, @NotNull Boolean withItalic) {
        itemStack.apply(ITEM_NAME, Text.of("Genesis Item"), current -> {
            MutableText currentText = current.copy();
            Style textStyle = currentText.getStyle().withItalic(withItalic);

            return MutableText.of(PlainTextContent.EMPTY).append(value).setStyle(textStyle);
        });
    }

    public static String getItemName(@NotNull ItemStack itemStack) {
        return itemStack.getName().getString();
    }

    public static void setItemLore(@NotNull ItemStack itemStack, @NotNull List<String> value) {
        List<Text> lore = new ArrayList<>();
        for (String line : value) {
            lore.add(TextUtils.deserialize(line));
        }
        itemStack.applyComponentsFrom(ComponentMap.builder().add(LORE, new LoreComponent(lore)).build());
    }

    public static List<String> getItemLore(@NotNull ItemStack itemStack) {
        List<String> lore = new ArrayList<>();
        if (itemStack.getComponents().contains(LORE)) {
            LoreComponent loreComponent = itemStack.getComponents().get(LORE);
            if (loreComponent != null) {
                List<Text> lore_text = loreComponent.lines();
                for (Text text : lore_text) {
                    lore.add(text.getString());
                }
            }
        }
        return lore;
    }

    @NotNull
    public static NbtCompound getNbt(@NotNull ItemStack itemStack, @NotNull String namespace) {
        NbtCompound nbt = itemStack.getOrDefault(CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        if (namespace.isEmpty()) {
            // root namespace
            return nbt;
        }

        return nbt.getCompound(namespace);
    }
}
