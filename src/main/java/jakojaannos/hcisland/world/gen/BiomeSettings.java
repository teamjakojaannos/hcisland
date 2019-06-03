package jakojaannos.hcisland.world.gen;

import jakojaannos.hcisland.util.BlockHelper;
import jakojaannos.hcisland.world.biome.BlockLayer;
import lombok.AllArgsConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.Arrays;

public class BiomeSettings {
    public final int seaLevel;
    public final IBlockState oceanBlock;
    public final IBlockState stoneBlock;
    public final BlockLayer[] layers;
    public final BlockLayer[] layersUnderwater;

    public BiomeSettings(Factory factory) {
        this.seaLevel = factory.seaLevel;
        this.oceanBlock = BlockHelper.stringToBlockstateWithFallback(Blocks.WATER.getDefaultState(), factory.oceanBlock);
        this.stoneBlock = BlockHelper.stringToBlockstateWithFallback(Blocks.STONE.getDefaultState(), factory.stoneBlock);
        this.layers = Arrays.stream(factory.layers).map(BlockLayer::new).toArray(BlockLayer[]::new);
        this.layersUnderwater = Arrays.stream(factory.layersUnderwater).map(BlockLayer::new).toArray(BlockLayer[]::new);
    }


    @AllArgsConstructor
    public static class Factory {
        public int seaLevel;
        public String oceanBlock;
        public String stoneBlock;
        public String[] layers;
        public String[] layersUnderwater;
    }

    public static class Island extends BiomeSettings {
        public final boolean generateFalls;
        public final boolean generateLakes;
        public final boolean generateLakesLava;

        public Island(Island.Factory factory) {
            super(factory);
            this.generateFalls = factory.generateFalls;
            this.generateLakes = factory.generateLakes;
            this.generateLakesLava = factory.generateLakesLava;
        }

        public static class Factory extends BiomeSettings.Factory {
            private boolean generateFalls;
            private boolean generateLakes;
            private boolean generateLakesLava;

            public Factory(int seaLevel, String oceanBlock, String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava) {
                super(seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater);
                this.generateFalls = generateFalls;
                this.generateLakes = generateLakes;
                this.generateLakesLava = generateLakesLava;
            }
        }
    }

    public static class Forest extends Island {
        public final int treesPerChunk;
        public final int grassPerChunk;
        public final int flowersPerChunk;

        public Forest(Forest.Factory factory) {
            super(factory);
            this.treesPerChunk = factory.treesPerChunk;
            this.grassPerChunk = factory.grassPerChunk;
            this.flowersPerChunk = factory.flowersPerChunk;
        }


        public static class Factory extends Island.Factory {
            private int treesPerChunk;
            private int grassPerChunk;
            private int flowersPerChunk;

            public Factory(int seaLevel, String oceanBlock, String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava, int treesPerChunk, int grassPerChunk, int flowersPerChunk) {
                super(seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
                this.treesPerChunk = treesPerChunk;
                this.grassPerChunk = grassPerChunk;
                this.flowersPerChunk = flowersPerChunk;
            }
        }
    }

    public static class Beach extends Island {
        public final int cactiPerChunk;

        public Beach(Beach.Factory factory) {
            super(factory);
            this.cactiPerChunk = factory.cactiPerChunk;
        }


        public static class Factory extends Island.Factory {
            private int cactiPerChunk;

            public Factory(int seaLevel, String oceanBlock, String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava, int cactiPerChunk) {
                super(seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
                this.cactiPerChunk = cactiPerChunk;
            }
        }
    }

    public static class Wasteland extends Island {
        public final boolean generateFire;

        public Wasteland(Wasteland.Factory factory) {
            super(factory);
            this.generateFire = factory.generateFire;
        }


        public static class Factory extends Island.Factory {
            private boolean generateFire;

            public Factory(int seaLevel, String oceanBlock, String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava, boolean generateFire) {
                super(seaLevel, oceanBlock, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
                this.generateFire = generateFire;
            }
        }
    }
}
