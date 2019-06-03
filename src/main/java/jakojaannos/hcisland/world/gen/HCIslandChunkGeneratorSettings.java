package jakojaannos.hcisland.world.gen;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.init.ModBiomes;
import jakojaannos.hcisland.util.BlockHelper;
import jakojaannos.hcisland.util.UnitHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.var;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class HCIslandChunkGeneratorSettings {
    public final int seaLevelOverride;
    public final IBlockState oceanBlockOverride;

    public final BiomeSettings.Forest island;
    public final BiomeSettings.Beach islandBeach;
    public final BiomeSettings ocean;
    public final BiomeSettings.Wasteland wasteland;
    public final BiomeSettings.Wasteland wastelandBeach;
    public final BiomeSettings.Wasteland wastelandEdge;

    private final List<IslandRadialBiome> biomes;


    private HCIslandChunkGeneratorSettings(Factory factory) {
        this.seaLevelOverride = factory.seaLevelOverride;
        this.oceanBlockOverride = BlockHelper.stringToBlockstateWithFallback(Blocks.LAVA.getDefaultState(), factory.oceanBlockOverride);

        this.island = new BiomeSettings.Forest(factory.island);
        this.islandBeach = new BiomeSettings.Beach(factory.islandBeach);
        this.ocean = new BiomeSettings(factory.ocean);
        this.wasteland = new BiomeSettings.Wasteland(factory.wasteland);
        this.wastelandBeach = new BiomeSettings.Wasteland(factory.wastelandBeach);
        this.wastelandEdge = new BiomeSettings.Wasteland(factory.wastelandEdge);

        this.biomes = factory.biomes.stream()
                                    .map(IslandRadialBiome::new)
                                    .collect(Collectors.toList());
    }

    @Nullable
    public Biome getBiomeAtDistanceSq(long distSq) {
        if (biomes.isEmpty()) {
            return null;
        }

        // TODO: Debug
        var distance = 0L;
        for (val biomeEntry : biomes) {
            val newDistance = (distance + biomeEntry.radius) * UnitHelper.CHUNKS_TO_GEN_LAYER_CONVERSION_RATIO;
            if (distSq < newDistance * newDistance) {
                return biomeEntry.biome;
            }

            distance += biomeEntry.radius;
        }

        return null;
    }

    public int getTotalRadialZoneRadius() {
        return biomes.stream()
                     .filter(b -> b.getBiome() != null)
                     .mapToInt(IslandRadialBiome::getRadius)
                     .sum();
    }

    public static class IslandRadialBiome {
        @Getter private int radius;
        @Getter private ResourceLocation biomeId;
        @Getter private Biome biome;

        public IslandRadialBiome(IslandRadialBiome.Factory factory) {
            this.radius = factory.radius;
            this.biomeId = new ResourceLocation(factory.biomeId);
            this.biome = ForgeRegistries.BIOMES.getValue(this.biomeId);
        }

        @AllArgsConstructor
        public static class Factory {
            @Getter private int radius;
            @Getter private String biomeId;
        }
    }

    public static class Factory {
        private static final Gson JSON_ADAPTER = new GsonBuilder().create();
        private static Factory overrides;

        public int seaLevelOverride;
        public String oceanBlockOverride;

        private List<IslandRadialBiome.Factory> biomes;

        public BiomeSettings.Forest.Factory island;
        public BiomeSettings.Beach.Factory islandBeach;
        public BiomeSettings.Factory ocean;
        public BiomeSettings.Wasteland.Factory wasteland;
        public BiomeSettings.Wasteland.Factory wastelandBeach;
        public BiomeSettings.Wasteland.Factory wastelandEdge;

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

            biomes = Lists.newArrayList(
                    new IslandRadialBiome.Factory(3, ModBiomes.ISLAND.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(2, ModBiomes.ISLAND_BEACH.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(8, ModBiomes.OCEAN.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(2, ModBiomes.WASTELAND_BEACH.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(4, ModBiomes.WASTELAND.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(1, ModBiomes.WASTELAND_EDGE.getRegistryName().toString())
            );

            island = new BiomeSettings.Forest.Factory(
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
            islandBeach = new BiomeSettings.Beach.Factory(
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
            ocean = new BiomeSettings.Factory(
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
            wasteland = new BiomeSettings.Wasteland.Factory(
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
            wastelandBeach = new BiomeSettings.Wasteland.Factory(
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
            wastelandEdge = new BiomeSettings.Wasteland.Factory(
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

            biomes = overrides.biomes; // TODO: Does this cause issues?

            island = overrides.island;
            islandBeach = overrides.islandBeach;
            ocean = overrides.ocean;
            wasteland = overrides.wasteland;
            wastelandBeach = overrides.wastelandBeach;
            wastelandEdge = overrides.wastelandEdge;
        }

        public static void refreshOverrides() {
            overrides = jsonToFactory(HCIslandConfig.world.generatorSettingsDefaults);
        }
    }
}
