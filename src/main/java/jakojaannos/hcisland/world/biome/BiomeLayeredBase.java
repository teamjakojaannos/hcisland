package jakojaannos.hcisland.world.biome;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * Adds some convenience features for allowing a bit more customized biomes.
 */
public abstract class BiomeLayeredBase extends Biome {
    private static final IBlockState[] LOOKUP = new IBlockState[256];

    private int seaLevelOverride;
    private int bedrockDepth;

    private float seaLevelFuzzScale;
    private float seaLevelFuzzOffset;

    private IBlockState oceanBlock;
    private IBlockState stoneBlock;

    private BlockLayer[] underwaterLayers;
    private BlockLayer[] layers;


    /**
     * Gets the sea level override. Negative value means that world default will be used instead.
     */
    public int getSeaLevelOverride() {
        return seaLevelOverride;
    }

    /**
     * Sets the sea level override. Set to negative value to use world default.
     */
    public BiomeLayeredBase setSeaLevelOverride(int seaLevel) {
        this.seaLevelOverride = seaLevel;
        return this;
    }

    /**
     * Gets the block used as water substitute for blocks below sea level
     */
    public IBlockState getOceanBlock() {
        return oceanBlock;
    }

    /**
     * Sets the block used as water substitute for blocks below sea level
     */
    public BiomeLayeredBase setOceanBlock(IBlockState oceanBlock) {
        this.oceanBlock = oceanBlock;
        return this;
    }

    /**
     * Gets the number of bedrock layers generated
     */
    public int getBedrockDepth() {
        return bedrockDepth;
    }

    /**
     * Sets the number of bedrock layers generated
     */
    public BiomeLayeredBase setBedrockDepth(int bedrockDepth) {
        this.bedrockDepth = bedrockDepth;
        return this;
    }

    /**
     * Gets the block to use as stone substitute
     */
    public IBlockState getStoneBlock() {
        return stoneBlock;
    }

    /**
     * Sets the block to use as stone substitute
     */
    public BiomeLayeredBase setStoneBlock(IBlockState stoneBlock) {
        this.stoneBlock = stoneBlock;
        return this;
    }

    /**
     * Gets the edge biome to be used when neighboring an incompatible biome.
     *
     * @return the biome to be used as edge
     */
    @Nullable
    public Biome getEdgeBiome() {
        return null;
    }

    /**
     * Checks if this biome can be neighbor with given biome. Ignored if biome does not have an edge.
     *
     * @param other biome to check against
     * @return <code>true</code> if biome is compatible and no edge is needed, <code>false</code> otherwise
     */
    public boolean isCompatibleWith(Biome other) {
        return true;
    }

    /**
     * Whether or not this biome is oceanic, requiring a special beach-like biome as edge when neighboring non-oceanic
     * biomes.
     *
     * @return <code>true</code> if biome is oceanic and requires beaches, <code>false</code> otherwise
     */
    public boolean isOceanic() {
        return getBeachBiome() != null;
    }

    public boolean isCompatibleWithOceanic(Biome other) {
        return true;
    }

    @Nullable
    public Biome getBeachBiome() {
        return null;
    }


    /**
     * Gets the fuzz-scale when transitioning from overwater layers to underwater ones
     */
    public float getSeaLevelFuzzScale() {
        return seaLevelFuzzScale;
    }

    /**
     * Gets the fuzz-offset when transitioning from overwater layers to underwater ones
     */
    public float getSeaLevelFuzzOffset() {
        return seaLevelFuzzOffset;
    }

    /**
     * Sets the underwater border fuzz properties
     */
    public void setSeaLevelFuzz(float scale, float offset) {
        this.seaLevelFuzzScale = scale;
        this.seaLevelFuzzOffset = offset;
    }


    /**
     * Gets the sea level for this biome. If no override is set, world default will be used.
     */
    public final int getSeaLevel(World world) {
        return seaLevelOverride < 0 ? world.getSeaLevel() : seaLevelOverride;
    }

    public BiomeLayeredBase setLayers(BlockLayer[] layers, BlockLayer[] underwaterLayers) {
        this.layers = layers;
        this.underwaterLayers = underwaterLayers;
        return this;
    }


    protected BiomeLayeredBase(BiomeProperties properties) {
        super(properties);
        this.layers = new BlockLayer[0];
        this.underwaterLayers = new BlockLayer[0];

        this.oceanBlock = WATER;
        this.stoneBlock = STONE;
        this.bedrockDepth = 5;
        this.seaLevelOverride = -1;

        this.seaLevelFuzzOffset = 0.0f;
        this.seaLevelFuzzScale = 0.0f;
    }

    @Override
    public void genTerrainBlocks(
            World world,
            Random rand,
            ChunkPrimer primer,
            int globalX,
            int globalZ,
            double noiseVal
    ) {
        // HACK: Vanilla has x<->z swapped, so do we
        int x = globalZ & 15;
        int z = globalX & 15;

        // Set bottom layer to bedrock
        primer.setBlockState(x, 0, z, BEDROCK);

        final int seaLevel = getSeaLevel(world);
        final int fuzzySeaLevel = MathHelper.floor((seaLevel + getSeaLevelFuzzOffset()) + (noiseVal * getSeaLevelFuzzScale()));
        boolean hitSolid = false;
        int solidDepth = 0;

        // Replace blocks
        for (int y = 255; y > 0; y--) {

            final IBlockState state = primer.getBlockState(x, y, z);

            // Keep replacing water blocks with air or water override block until we hit solid
            if (!hitSolid) {
                if (state.getMaterial() == Material.WATER) {
                    final IBlockState newState = y > seaLevel ? Blocks.AIR.getDefaultState() : oceanBlock;
                    primer.setBlockState(x, y, z, newState);
                } else if (state.getMaterial() != Material.AIR) {
                    hitSolid = true;

                    // Generate temporary lookup table to speed up the block selection
                    generateLookup(rand, y, fuzzySeaLevel, globalX, globalZ, noiseVal, LOOKUP);
                }
            }

            // Replace first solid with top block and the rest with filler blocks.
            if (hitSolid) {
                primer.setBlockState(x, y, z, LOOKUP[solidDepth++]);
            }
        }
    }


    /**
     * Generates lookup table for blockstates in a single column. DO NOT CREATE NEW ARRAY ON EACH CALL, use the array
     * provided in "lookup" parameter as it is recycled.
     *
     * @param solidDepth    y-coordinate of the first solid layer
     * @param fuzzySeaLevel sea level with fuzz applied
     * @param x             global x-coordinate of the column
     * @param z             global z-coordinate of the column
     */
    protected void generateLookup(
            Random random,
            int solidDepth,
            int fuzzySeaLevel,
            int x,
            int z,
            double noiseVal,
            IBlockState[] lookup
    ) {
        final boolean underwater = solidDepth <= fuzzySeaLevel;
        final Iterator<BlockLayer> layerIter = Arrays.stream(getLayers(underwater)).iterator();

        int layerBlocksPlaced = 0;
        BlockLayer current = layerIter.hasNext() ? layerIter.next() : null;
        for (int y = 0; y < solidDepth; y++, layerBlocksPlaced++) {
            if (current != null && layerBlocksPlaced >= current.getDepth()) {
                current = layerIter.hasNext() ? layerIter.next() : null;
                layerBlocksPlaced = 0;
            }

            if (isAtBedrock(random, solidDepth, y)) {
                lookup[y] = Blocks.BEDROCK.getDefaultState();
            } else {
                lookup[y] = current != null ? current.getBlock() : stoneBlock;
            }
        }
    }

    private boolean isAtBedrock(Random random, int solidDepth, int y) {
        return y >= solidDepth - random.nextInt(bedrockDepth);
    }

    protected BlockLayer[] getLayers(boolean underwater) {
        return underwater ? underwaterLayers : layers;
    }
}
