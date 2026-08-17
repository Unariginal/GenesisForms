package me.unariginal.genesisforms.blocks;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.callback.PartySelectCallbacks;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import kotlin.Unit;
import me.unariginal.genesisforms.data.FormSetting;
import me.unariginal.genesisforms.data.event.ParticleEvent;
import me.unariginal.genesisforms.polymer.KeyItemsGroup;
import me.unariginal.genesisforms.utils.PokemonUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static me.unariginal.genesisforms.config.ConfigManager.EVENTS;
import static me.unariginal.genesisforms.utils.PokemonUtils.applyFeature;

public class PossessionBlock extends Block implements FactoryBlock {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    private final Block base;
    private final boolean placeable;
    private final FormSetting formSetting;

    public PossessionBlock(AbstractBlock.Settings settings, boolean placeable, FormSetting formSetting) {
        super(settings);
        this.placeable = placeable;
        this.formSetting = formSetting;
        this.setDefaultState(this.getDefaultState());
        this.base = Blocks.ORANGE_TERRACOTTA;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.isSneaking() && player instanceof ServerPlayerEntity serverPlayer) {
            PokemonBattle playerBattle = BattleRegistry.getBattleByParticipatingPlayer(serverPlayer);
            if (playerBattle == null) {
                PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(serverPlayer);
                List<Pokemon> nonNullParty = new ArrayList<>();
                for (Pokemon pokemon : party) {
                    if (pokemon != null) {
                        nonNullParty.add(pokemon);
                    }
                }
                PartySelectCallbacks.INSTANCE.createFromPokemon(serverPlayer, nonNullParty, pokemon -> pokemon.getSpecies().getName().equalsIgnoreCase("rotom"), pokemon -> {
                    PokemonEntity pokemonEntity = pokemon.getEntity();
                    if (pokemon.getFeatures().stream().noneMatch(speciesFeature -> {
                        if (speciesFeature.getName().equalsIgnoreCase(formSetting.featureName)) {
                            if (speciesFeature instanceof StringSpeciesFeature stringSpeciesFeature) {
                                return stringSpeciesFeature.getValue().equalsIgnoreCase(formSetting.defaultValue);
                            } else if (speciesFeature instanceof FlagSpeciesFeature flagSpeciesFeature) {
                                return Boolean.toString(flagSpeciesFeature.getEnabled()).equalsIgnoreCase(formSetting.defaultValue);
                            }
                        }
                        return false;
                    })) {
                        float delay = 0;
                        if (EVENTS.formChanges != null && EVENTS.formChanges.possessions != null) {
                            String eventId = pokemon.getSpecies().getName().toLowerCase() + "_" + formSetting.defaultValue;
                            EVENTS.formChanges.possessions.runEvent(eventId, pokemon, pokemonEntity);
                            ParticleEvent particleEvent = EVENTS.formChanges.possessions.getAnimation(eventId);
                            if (particleEvent != null) delay = particleEvent.formChangeDelaySeconds;
                        }

                        if (pokemonEntity != null) {
                            pokemonEntity.after(delay, () -> {
                                applyFeature(formSetting.featureName, formSetting.defaultValue, pokemon);
                                PokemonUtils.fixRotomMoves(pokemon);
                                pokemon.updateAspects();
                                pokemon.updateForm();
                                return Unit.INSTANCE;
                            });
                        } else {
                            applyFeature(formSetting.featureName, formSetting.defaultValue, pokemon);
                            PokemonUtils.fixRotomMoves(pokemon);
                            pokemon.updateAspects();
                            pokemon.updateForm();
                        }
                    } else {
                        // This is rotom light bulb (default) form
                        if (pokemon.getFeatures().stream().anyMatch(feature -> feature.getName().equalsIgnoreCase(formSetting.featureName))) {
                            pokemon.getFeatures().removeIf(feature -> feature.getName().equalsIgnoreCase(formSetting.featureName));

                            PokemonUtils.fixRotomMoves(pokemon);
                        }
                    }

                    pokemon.updateAspects();
                    pokemon.updateForm();

                    ItemStack returnItem = ItemStack.EMPTY;
                    if (pokemon.getPersistentData().contains("possession_item")) {
                        String possessionItem = pokemon.getPersistentData().getString("possession_item");
                        pokemon.getPersistentData().remove("possession_item");

                        if (KeyItemsGroup.possessionItems.containsKey(possessionItem)) {
                            returnItem = KeyItemsGroup.possessionItems.get(possessionItem).getDefaultStack();
                        }
                    }

                    serverPlayer.getInventory().offerOrDrop(returnItem);

                    return Unit.INSTANCE;
                });
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (!placeable) return false;
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.BARRIER.getDefaultState();
    }

    @Override
    public BlockState getPolymerBreakEventBlockState(BlockState state, ServerPlayerEntity player) {
        return this.base.getDefaultState();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction dir = ctx.getHorizontalPlayerFacing().getOpposite();
        return this.getDefaultState().with(FACING, dir);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    public static final class Model extends BlockModel {
        private final ItemDisplayElement main;

        public Model(BlockState state) {
            this.main = ItemDisplayElementUtil.createSimple(state.getBlock().asItem());
            this.main.setDisplaySize(1, 1);
            this.main.setScale(new Vector3f(1));
            float yaw = state.get(FACING).getOpposite().asRotation();
            this.main.setYaw(yaw);
            this.addElement(this.main);
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
                BlockState state = this.blockState();
                float yaw = state.get(FACING).getOpposite().asRotation();
                this.main.setYaw(yaw);
                this.tick();
            }
        }
    }
}
