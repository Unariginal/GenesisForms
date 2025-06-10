package me.unariginal.genesisforms.items.keyitems;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import me.unariginal.genesisforms.GenesisForms;
import me.unariginal.genesisforms.data.DataKeys;
import me.unariginal.genesisforms.polymer.KeyItems;
import me.unariginal.genesisforms.utils.NbtUtils;
import me.unariginal.genesisforms.utils.TextUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZygardeCube extends SimplePolymerItem {
    PolymerModelData modelData;

    public ZygardeCube(Settings settings, Item polymerItem) {
        super(settings, polymerItem);
        this.modelData = KeyItems.zygardeCubeModelData;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player){
        return this.modelData.value();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        for (String line : GenesisForms.INSTANCE.getItemSettings().item_lore.get("zygarde_cube")) {
            tooltip.add(TextUtils.deserialize(line));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (GenesisForms.INSTANCE.getConfig().disabledItems.contains("zygarde_cube")) return TypedActionResult.pass(user.getStackInHand(hand));
        if (user.isSneaking()) {
            if (NbtUtils.getNbt(user.getStackInHand(hand), GenesisForms.MOD_ID).contains(DataKeys.NBT_CUBE_MODE)) {
                if (NbtUtils.getNbt(user.getStackInHand(hand), GenesisForms.MOD_ID).getString(DataKeys.NBT_CUBE_MODE).equalsIgnoreCase("form")) {
                    NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "ability");
                    user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false).build());
                    ServerPlayerEntity player = GenesisForms.INSTANCE.getServer().getPlayerManager().getPlayer(user.getUuid());
                    if (player != null) {
                        player.sendActionBar(MiniMessage.miniMessage().deserialize("<gray>Cube Mode: <green>Ability"));
                    }
                } else if (NbtUtils.getNbt(user.getStackInHand(hand), GenesisForms.MOD_ID).getString(DataKeys.NBT_CUBE_MODE).equalsIgnoreCase("ability")) {
                    NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "form");
                    user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).build());
                    ServerPlayerEntity player = GenesisForms.INSTANCE.getServer().getPlayerManager().getPlayer(user.getUuid());
                    if (player != null) {
                        player.sendActionBar(MiniMessage.miniMessage().deserialize("<gray>Cube Mode: <green>Form"));
                    }
                }
            } else {
                NbtUtils.setNbtString(user.getStackInHand(hand), GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "form");
                user.getStackInHand(hand).applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).build());
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.isSneaking()) {
            if (entity instanceof PokemonEntity pokemonEntity && pokemonEntity.getExposedSpecies().getName().equalsIgnoreCase("zygarde")) {
                ServerPlayerEntity player = pokemonEntity.getPokemon().getOwnerPlayer();
                if (player != null) {
                    if (player.getUuid().equals(user.getUuid()) && !pokemonEntity.isBattling()) {
                        if (NbtUtils.getNbt(stack, GenesisForms.MOD_ID).contains(DataKeys.NBT_CUBE_MODE)) {
                            if (NbtUtils.getNbt(stack, GenesisForms.MOD_ID).getString(DataKeys.NBT_CUBE_MODE).equalsIgnoreCase("form")) {
                                GenesisForms.INSTANCE.logInfo("[Genesis] Aspects: " + pokemonEntity.getPokemon().getAspects());
                                Ability ability = pokemonEntity.getPokemon().getAbility();
                                if (pokemonEntity.getPokemon().getAspects().stream().anyMatch(aspect -> aspect.startsWith("10"))) {
                                    new StringSpeciesFeature("percent_cells", "50").apply(pokemonEntity.getPokemon());
                                    pokemonEntity.getPokemon().setAbility$common(ability);
                                } else {
                                    new StringSpeciesFeature("percent_cells", "10").apply(pokemonEntity.getPokemon());
                                    pokemonEntity.getPokemon().setAbility$common(ability);
                                }
                            } else if (NbtUtils.getNbt(stack, GenesisForms.MOD_ID).getString(DataKeys.NBT_CUBE_MODE).equalsIgnoreCase("ability")) {
                                swapAbility(pokemonEntity.getPokemon());
                            }
                        } else {
                            NbtUtils.setNbtString(stack, GenesisForms.MOD_ID, DataKeys.NBT_CUBE_MODE, "ability");
                            stack.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false).build());
                            swapAbility(pokemonEntity.getPokemon());
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    public void swapAbility(Pokemon pokemon) {
        AbilityTemplate powerconstruct = Abilities.INSTANCE.get("powerconstruct");
        AbilityTemplate aurabreak = Abilities.INSTANCE.get("aurabreak");
        if (powerconstruct != null && aurabreak != null) {
            if (pokemon.getAbility().getTemplate().getName().equalsIgnoreCase(aurabreak.getName())) {
                GenesisForms.INSTANCE.logInfo("[Genesis] Switched Zygarde's ability to Power Construct.");
                pokemon.setAbility$common(powerconstruct.create(false, Priority.LOW));
            } else {
                GenesisForms.INSTANCE.logInfo("[Genesis] Switched Zygarde's ability to Aura Break.");
                pokemon.setAbility$common(aurabreak.create(false, Priority.LOW));
            }
        }
    }
}
