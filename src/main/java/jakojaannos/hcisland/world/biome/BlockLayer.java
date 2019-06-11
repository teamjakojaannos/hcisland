package jakojaannos.hcisland.world.biome;

import lombok.NoArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

/**
 * Defines a generated block layer when generating {@link BiomeLayeredBase}-based biomes
 */
@NoArgsConstructor
public class BlockLayer {
    private int depth = 1;
    private IBlockState block = Blocks.STONE.getDefaultState();

    public int getDepth() {
        return depth;
    }

    public IBlockState getBlock() {
        return block;
    }

    public BlockLayer(int depth, IBlockState block) {
        this.depth = depth;
        this.block = block;
    }

    public BlockLayer(int depth, Block block) {
        this.depth = depth;
        this.block = block.getDefaultState();
    }
}
