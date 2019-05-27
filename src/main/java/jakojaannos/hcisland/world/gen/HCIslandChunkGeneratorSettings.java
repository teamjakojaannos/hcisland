package jakojaannos.hcisland.world.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakojaannos.hcisland.util.BlockHelper;
import jakojaannos.hcisland.world.biome.BlockLayer;
import jakojaannos.hcisland.config.HCIslandConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.JsonUtils;

import java.util.Arrays;

public class HCIslandChunkGeneratorSettings {
    public final int seaLevelOverride;
    public final IBlockState oceanBlockOverride;

    public final BiomeSettings.Forest island;
    public final BiomeSettings.Beach islandBeach;
    public final BiomeSettings ocean;
    public final BiomeSettings.Wasteland wasteland;
    public final BiomeSettings.Wasteland wastelandBeach;
    public final BiomeSettings.Wasteland wastelandEdge;


    private HCIslandChunkGeneratorSettings(Factory factory) {
        this.seaLevelOverride = factory.seaLevelOverride;
        this.oceanBlockOverride = BlockHelper.stringToBlockstateWithFallback(Blocks.LAVA.getDefaultState(), factory.oceanBlockOverride);

        this.island = new BiomeSettings.Forest(factory.island);
        this.islandBeach = new BiomeSettings.Beach(factory.islandBeach);
        this.ocean = new BiomeSettings(factory.ocean);
        this.wasteland = new BiomeSettings.Wasteland(factory.wasteland);
        this.wastelandBeach = new BiomeSettings.Wasteland(factory.wastelandBeach);
        this.wastelandEdge = new BiomeSettings.Wasteland(factory.wastelandEdge);
    }

    public static class BiomeSettings {
        public final int radius;

        public final IBlockState stoneBlock;
        public final BlockLayer[] layers;
        public final BlockLayer[] layersUnderwater;

        public BiomeSettings(Factory.BiomeSettingsFactory factory) {
            this.radius = factory.radius;

            this.stoneBlock = BlockHelper.stringToBlockstateWithFallback(Blocks.STONE.getDefaultState(), factory.stoneBlock);
            this.layers = Arrays.stream(factory.layers).map(BlockLayer::new).toArray(BlockLayer[]::new);
            this.layersUnderwater = Arrays.stream(factory.layersUnderwater).map(BlockLayer::new).toArray(BlockLayer[]::new);
        }

        public static class Island extends BiomeSettings {
            public final boolean generateFalls;
            public final boolean generateLakes;
            public final boolean generateLakesLava;

            public Island(Factory.BiomeSettingsFactory.Island factory) {
                super(factory);
                this.generateFalls = factory.generateFalls;
                this.generateLakes = factory.generateLakes;
                this.generateLakesLava = factory.generateLakesLava;
            }
        }

        public static class Forest extends Island {
            public final int treesPerChunk;
            public final int grassPerChunk;
            public final int flowersPerChunk;

            public Forest(Factory.BiomeSettingsFactory.Forest factory) {
                super(factory);
                this.treesPerChunk = factory.treesPerChunk;
                this.grassPerChunk = factory.grassPerChunk;
                this.flowersPerChunk = factory.flowersPerChunk;
            }
        }

        public static class Beach extends Island {
            public final int cactiPerChunk;

            public Beach(Factory.BiomeSettingsFactory.Beach factory) {
                super(factory);
                this.cactiPerChunk = factory.cactiPerChunk;
            }
        }

        public static class Wasteland extends Island {
            public final boolean generateFire;

            public Wasteland(Factory.BiomeSettingsFactory.Wasteland factory) {
                super(factory);
                this.generateFire = factory.generateFire;
            }
        }
    }

    public static class Factory {
        private static final Gson JSON_ADAPTER = new GsonBuilder().create();
        private static Factory overrides;

        public int seaLevelOverride;
        public String oceanBlockOverride;

        public BiomeSettingsFactory.Forest island;
        public BiomeSettingsFactory.Beach islandBeach;
        public BiomeSettingsFactory ocean;
        public BiomeSettingsFactory.Wasteland wasteland;
        public BiomeSettingsFactory.Wasteland wastelandBeach;
        public BiomeSettingsFactory.Wasteland wastelandEdge;

        public HCIslandChunkGeneratorSettings build() {
            return new HCIslandChunkGeneratorSettings(this);
        }

        public static Factory jsonToFactory(String json) {
            if (json.isEmpty()) {
                return new Factory();
            }

            try {
                Factory factory = JsonUtils.gsonDeserialize(JSON_ADAPTER, json, Factory.class);
                if (factory == null) {
                    return new Factory();
                }

                return factory;
            } catch (Exception ignored) {
                return new Factory();
            }
        }

        @Override
        public String toString() {
            return JSON_ADAPTER.toJson(this);
        }

        public void setDefaults() {
            if (overrides != null) {
                setOverrides();
                return;
            }

            seaLevelOverride = 48;
            oceanBlockOverride = "minecraft:lava";

            island = new BiomeSettingsFactory.Forest(
                    3,
                    "minecraft:stone",
                    new String[]{
                            "1, minecraft:grass",
                            "5, minecraft:dirt",
                            "1, minecraft:clay",
                            "2, minecraft:gravel"
                    },
                    new String[]{
                    },
                    false,
                    false,
                    false,
                    7,
                    4,
                    25
            );
            islandBeach = new BiomeSettingsFactory.Beach(
                    2,
                    "minecraft:stone",
                    new String[]{
                            "8, minecraft:sand",
                            "2, minecraft:sandstone",
                            "1, minecraft:clay",
                            "2, minecraft:gravel"
                    },
                    new String[]{
                            "2, minecraft:obsidian",
                            "1, minecraft:sandstone",
                            "1, minecraft:hardened_clay",
                            "2, minecraft:netherrack"
                    },
                    false,
                    false,
                    false,
                    12
            );
            ocean = new BiomeSettingsFactory(
                    16,
                    "minecraft:stone",
                    new String[]{
                            "8, minecraft:netherrack"
                    },
                    new String[]{
                            "2, minecraft:obsidian",
                            "1, minecraft:sandstone",
                            "1, minecraft:hardened_clay",
                            "4, minecraft:netherrack"
                    }
            );
            wasteland = new BiomeSettingsFactory.Wasteland(
                    4,
                    "minecraft:netherrack",
                    new String[]{
                            "8, minecraft:netherrack"
                    },
                    new String[]{
                            "1, minecraft:obsidian",
                            "1, minecraft:sandstone",
                            "1, minecraft:hardened_clay",
                            "8, minecraft:netherrack"
                    },
                    true,
                    false,
                    true,
                    true
            );
            wastelandBeach = new BiomeSettingsFactory.Wasteland(
                    2,
                    "minecraft:netherrack",
                    new String[]{
                            "16, minecraft:netherrack"
                    },
                    new String[]{
                            "1, minecraft:obsidian",
                            "1, minecraft:sandstone",
                            "1, minecraft:hardened_clay",
                            "10, minecraft:netherrack"
                    },
                    true,
                    false,
                    true,
                    true
            );
            wastelandEdge = new BiomeSettingsFactory.Wasteland(
                    2,
                    "minecraft:stone",
                    new String[]{
                            "8, minecraft:netherrack",
                            "2, minecraft:gravel"
                    },
                    new String[]{
                            "8, minecraft:netherrack"
                    },
                    true,
                    true,
                    true,
                    true
            );
        }

        public Factory() {
            setDefaults();
        }

        private void setOverrides() {
            seaLevelOverride = overrides.seaLevelOverride;
            oceanBlockOverride = overrides.oceanBlockOverride;

            island = overrides.island;
            islandBeach = overrides.islandBeach;
            ocean = overrides.ocean;
            wasteland = overrides.wasteland;
            wastelandBeach = overrides.wastelandBeach;
            wastelandEdge = overrides.wastelandEdge;
        }

        public static void refreshOverrides() {
            overrides = null;
            overrides = jsonToFactory(HCIslandConfig.world.generatorSettingsDefaults);
        }


        public static class BiomeSettingsFactory {
            public int radius;

            public String stoneBlock;
            public String[] layers;
            public String[] layersUnderwater;

            public BiomeSettingsFactory(int radius, String stoneBlock, String[] layers, String[] layersUnderwater) {
                this.radius = radius;
                this.stoneBlock = stoneBlock;
                this.layers = layers;
                this.layersUnderwater = layersUnderwater;
            }

            public static abstract class Island extends BiomeSettingsFactory {
                public boolean generateFalls;
                public boolean generateLakes;
                public boolean generateLakesLava;

                public Island(int radius, String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava) {
                    super(radius, stoneBlock, layers, layersUnderwater);
                    this.generateFalls = generateFalls;
                    this.generateLakes = generateLakes;
                    this.generateLakesLava = generateLakesLava;
                }
            }

            public static class Forest extends BiomeSettingsFactory.Island {
                public int treesPerChunk;
                public int grassPerChunk;
                public int flowersPerChunk;

                public Forest(int radius, String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava, int treesPerChunk, int grassPerChunk, int flowersPerChunk) {
                    super(radius, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
                    this.treesPerChunk = treesPerChunk;
                    this.grassPerChunk = grassPerChunk;
                    this.flowersPerChunk = flowersPerChunk;
                }
            }

            public static class Beach extends BiomeSettingsFactory.Island {
                public int cactiPerChunk;

                public Beach(int radius, String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava, int cactiPerChunk) {
                    super(radius, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
                    this.cactiPerChunk = cactiPerChunk;
                }
            }

            public static class Wasteland extends BiomeSettingsFactory.Island {
                public boolean generateFire;

                public Wasteland(int radius, String stoneBlock, String[] layers, String[] layersUnderwater, boolean generateFalls, boolean generateLakes, boolean generateLakesLava, boolean generateFire) {
                    super(radius, stoneBlock, layers, layersUnderwater, generateFalls, generateLakes, generateLakesLava);
                    this.generateFire = generateFire;
                }
            }
        }
    }
}
