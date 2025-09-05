package me.unariginal.genesisforms.blocks;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class PossessionBlock extends Block implements FactoryBlock {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    private final Block base;
    private final boolean placeable;

    public PossessionBlock(AbstractBlock.Settings settings, boolean placeable) {
        super(settings);
        this.placeable = placeable;
        this.setDefaultState(this.getDefaultState());
        this.base = Blocks.ORANGE_TERRACOTTA;
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
