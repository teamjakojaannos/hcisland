package jakojaannos.hcisland.world.gen;

import jakojaannos.hcisland.world.biome.BlockLayer;
import lombok.NoArgsConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

@NoArgsConstructor
public class LayeredBiomeSettings extends BiomeSettings {
    public int seaLevel = 64;
    public IBlockState oceanBlock = Blocks.WATER.getDefaultState();
    public IBlockState stoneBlock = Blocks.STONE.getDefaultState();
    public BlockLayer[] layers = new BlockLayer[0];
    public BlockLayer[] layersUnderwater = new BlockLayer[0];

    public LayeredBiomeSettings(
            float baseHeightOverride,
            float heightVariationOverride,
            int seaLevel,
            IBlockState oceanBlock,
            IBlockState stoneBlock,
            BlockLayer[] layers,
            BlockLayer[] layersUnderwater
    ) {
        super(baseHeightOverride, heightVariationOverride);
        this.seaLevel = seaLevel;
        this.oceanBlock = oceanBlock;
        this.stoneBlock = stoneBlock;
        this.layers = layers;
        this.layersUnderwater = layersUnderwater;
    }

    @NoArgsConstructor
    public static class Island extends LayeredBiomeSettings {
        public boolean generateFalls = true;
        public boolean generateLakes = true;
        public boolean generateLakesLava = false;

        public Island(
                float baseHeightOverride,
                float heightVariationOverride,
                int seaLevel,
                IBlockState oceanBlock,
                IBlockState stoneBlock,
                BlockLayer[] layers,
                BlockLayer[] layersUnderwater,
                boolean generateFalls,
                boolean generateLakes,
                boolean generateLakesLava
        ) {
            super(baseHeightOverride, heightVariationOverride, seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater);
            this.generateFalls = generateFalls;
            this.generateLakes = generateLakes;
            this.generateLakesLava = generateLakesLava;
        }
    }

    @NoArgsConstructor
    public static class Forest extends Island {
        public int treesPerChunk = 7;
        public int grassPerChunk = 12;
        public int flowersPerChunk = 25;

        public Forest(
                float baseHeightOverride,
                float heightVariationOverride,
                int seaLevel,
                IBlockState oceanBlock,
                IBlockState stoneBlock,
                BlockLayer[] layers,
                BlockLayer[] layersUnderwater,
                boolean generateFalls,
                boolean generateLakes,
                boolean generateLakesLava,
                int treesPerChunk,
                int grassPerChunk,
                int flowersPerChunk
        ) {
            super(baseHeightOverride, heightVariationOverride, seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
            this.treesPerChunk = treesPerChunk;
            this.grassPerChunk = grassPerChunk;
            this.flowersPerChunk = flowersPerChunk;
        }
    }

    @NoArgsConstructor
    public static class Beach extends Island {
        public int cactiPerChunk = 12;

        public Beach(
                float baseHeightOverride,
                float heightVariationOverride,
                int seaLevel,
                IBlockState oceanBlock,
                IBlockState stoneBlock,
                BlockLayer[] layers,
                BlockLayer[] layersUnderwater,
                boolean generateFalls,
                boolean generateLakes,
                boolean generateLakesLava,
                int cactiPerChunk
        ) {
            super(baseHeightOverride, heightVariationOverride, seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
            this.cactiPerChunk = cactiPerChunk;
        }
    }

    @NoArgsConstructor
    public static class Wasteland extends Island {
        public boolean generateFire = true;

        public Wasteland(
                float baseHeightOverride,
                float heightVariationOverride,
                int seaLevel,
                IBlockState oceanBlock,
                IBlockState stoneBlock,
                BlockLayer[] layers,
                BlockLayer[] layersUnderwater,
                boolean generateFalls,
                boolean generateLakes,
                boolean generateLakesLava,
                boolean generateFire
        ) {
            super(baseHeightOverride, heightVariationOverride, seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
            this.generateFire = generateFire;
        }
    }
}
