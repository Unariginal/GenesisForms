package me.unariginal.genesisforms.data;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import me.unariginal.genesisforms.GenesisForms;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public abstract class DataComponents {
    public static final ComponentType<String> MEGA_STONE = register("mega_stone", builder -> builder.codec(Codec.STRING));
    public static final ComponentType<String> TERA_SHARD = register("tera_shard", builder -> builder.codec(Codec.STRING));
    public static final ComponentType<String> Z_CRYSTAL = register("z_crystal", builder -> builder.codec(Codec.STRING));
    public static final ComponentType<String> HELD_ITEM = register("held_item", builder -> builder.codec(Codec.STRING));
    public static final ComponentType<String> KEY_ITEM = register("key_item", builder -> builder.codec(Codec.STRING));

    private static <T> ComponentType<T> register (String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        ComponentType<T> component = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(GenesisForms.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
        PolymerComponent.registerDataComponent(component);
        return component;
    }

    public static void init () {}
}
