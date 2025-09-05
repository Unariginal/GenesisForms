package me.unariginal.genesisforms.utils;

import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.component.DataComponentTypes.*;

public class NbtUtils {
    public static void setNbtString(@NotNull ItemStack itemStack, @NotNull String namespace, @NotNull String key, @NotNull String value) {
        if (key.isEmpty() || value.isEmpty()) {
            return;
        }

        itemStack.apply(CUSTOM_DATA, NbtComponent.DEFAULT, current -> {
            NbtCompound newNbt = current.copyNbt();

            if (namespace.isEmpty()) {
                newNbt.putString(key, value);
                return NbtComponent.of(newNbt);
            }

            NbtCompound modNbt = newNbt.getCompound(namespace);
            modNbt.putString(key, value);
            newNbt.put(namespace, modNbt);
            return NbtComponent.of(newNbt);
        });
    }

    @NotNull
    public static NbtCompound getNbt(@NotNull ItemStack itemStack, @NotNull String namespace) {
        NbtCompound nbt = itemStack.getOrDefault(CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

        if (namespace.isEmpty()) {
            return nbt;
        }

        return nbt.getCompound(namespace);
    }
}