package jakojaannos.hcisland.world.gen;

import jakojaannos.hcisland.util.BlockHelper;
import jakojaannos.hcisland.world.biome.BlockLayer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.Arrays;

public class BiomeSettings {
    public final IBlockState stoneBlock;
    public final BlockLayer[] layers;
    public final BlockLayer[] layersUnderwater;

    public BiomeSettings(Factory factory) {
        this.stoneBlock = BlockHelper.stringToBlockstateWithFallback(Blocks.STONE.getDefaultState(), factory.stoneBlock);
        this.layers = Arrays.stream(factory.layers).map(BlockLayer::new).toArray(BlockLayer[]::new);
        this.layersUnderwater = Arrays.stream(factory.layersUnderwater).map(BlockLayer::new).toArray(BlockLayer[]::new);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Factory {
        public String stoneBlock = "minecraft:stone";
        public String[] layers = new String[0];
        public String[] layersUnderwater = new String[0];
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

        @NoArgsConstructor
        public static class Factory extends BiomeSettings.Factory {
            private boolean generateFalls = true;
            private boolean generateLakes = true;
            private boolean generateLakesLava = true;

            public Factory(String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava) {
                super(stoneBlock, layers, layersUnderwater);
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

        @NoArgsConstructor
        public static class Factory extends Island.Factory {
            private int treesPerChunk = 7;
            private int grassPerChunk = 4;
            private int flowersPerChunk = 25;

            public Factory(String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava, int treesPerChunk, int grassPerChunk, int flowersPerChunk) {
                super(stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
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

        @NoArgsConstructor
        public static class Factory extends Island.Factory {
            private int cactiPerChunk = 12;

            public Factory(String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava, int cactiPerChunk) {
                super(stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
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

        @NoArgsConstructor
        public static class Factory extends Island.Factory {
            private boolean generateFire = true;

            public Factory(String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava, boolean generateFire) {
                super(stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
                this.generateFire = generateFire;
            }
        }
    }
}
