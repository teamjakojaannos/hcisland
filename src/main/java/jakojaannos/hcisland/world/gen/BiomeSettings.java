package jakojaannos.hcisland.world.gen;

import jakojaannos.hcisland.world.biome.BlockLayer;
import lombok.NoArgsConstructor;
import net.minecraft.block.state.IBlockState;

@NoArgsConstructor
public class BiomeSettings {
    public int seaLevel;
    public IBlockState oceanBlock;
    public IBlockState stoneBlock;
    public BlockLayer[] layers;
    public BlockLayer[] layersUnderwater;

    public BiomeSettings(
            int seaLevel,
            IBlockState oceanBlock,
            IBlockState stoneBlock,
            BlockLayer[] layers,
            BlockLayer[] layersUnderwater
    ) {
        this.seaLevel = seaLevel;
        this.oceanBlock = oceanBlock;
        this.stoneBlock = stoneBlock;
        this.layers = layers;
        this.layersUnderwater = layersUnderwater;
    }

    @NoArgsConstructor
    public static class Island extends BiomeSettings {
        public boolean generateFalls;
        public boolean generateLakes;
        public boolean generateLakesLava;

        public Island(
                int seaLevel,
                IBlockState oceanBlock,
                IBlockState stoneBlock,
                BlockLayer[] layers,
                BlockLayer[] layersUnderwater,
                boolean generateFalls,
                boolean generateLakes,
                boolean generateLakesLava
        ) {
            super(seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater);
            this.generateFalls = generateFalls;
            this.generateLakes = generateLakes;
            this.generateLakesLava = generateLakesLava;
        }
    }

    @NoArgsConstructor
    public static class Forest extends Island {
        public int treesPerChunk;
        public int grassPerChunk;
        public int flowersPerChunk;

        public Forest(
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
            super(seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
            this.treesPerChunk = treesPerChunk;
            this.grassPerChunk = grassPerChunk;
            this.flowersPerChunk = flowersPerChunk;
        }
    }

    @NoArgsConstructor
    public static class Beach extends Island {
        public int cactiPerChunk;

        public Beach(
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
            super(seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
            this.cactiPerChunk = cactiPerChunk;
        }
    }

    @NoArgsConstructor
    public static class Wasteland extends Island {
        public boolean generateFire;

        public Wasteland(
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
            super(seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
            this.generateFire = generateFire;
        }
    }
}
