package jakojaannos.hcisland.world.gen;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakojaannos.hcisland.ModInfo;
import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.init.ModBiomes;
import jakojaannos.hcisland.init.ModRegistries;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HCIslandChunkGeneratorSettings {
    private final List<IslandRadialBiome> biomes;

    private final Map<ResourceLocation, BiomeSettings> biomeSettings;

    private HCIslandChunkGeneratorSettings(Factory factory) {
        this.biomes = factory.biomes.stream()
                                    .map(IslandRadialBiome::new)
                                    .collect(Collectors.toList());

        this.biomeSettings = new HashMap<>();
        for (val biomeEntry : factory.biomeSettings.entrySet()) {
            this.biomeSettings.put(biomeEntry.getKey(), biomeEntry.getValue().build());
        }
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

    public BiomeSettings getSettingsFor(ResourceLocation registryName) {
        if (!biomeSettings.containsKey(registryName)) {
            throw new IllegalStateException("Cannot get settings for biome \"" + registryName + "\"");
        }

        return biomeSettings.get(registryName);
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

        private List<IslandRadialBiome.Factory> biomes;
        private Map<ResourceLocation, BiomeSettings.Factory> biomeSettings;

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

            biomes = Lists.newArrayList(
                    new IslandRadialBiome.Factory(3, ModBiomes.ISLAND.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(2, ModBiomes.ISLAND_BEACH.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(8, ModBiomes.OCEAN.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(2, ModBiomes.WASTELAND_BEACH.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(4, ModBiomes.WASTELAND.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(1, ModBiomes.WASTELAND_EDGE.getRegistryName().toString())
            );

            biomeSettings = new HashMap<>();
            for (val adapterEntry : ModRegistries.BIOME_ADAPTERS.getEntries()) {
                biomeSettings.put(adapterEntry.getKey(), adapterEntry.getValue().createDefaultSettingsFactory());
            }
        }

        public Factory() {
            setDefaults();
        }

        private void setOverrides() {
            biomes = overrides.biomes; // TODO: Does this cause issues?
            biomeSettings = overrides.biomeSettings;
        }

        public static void refreshOverrides() {
            overrides = jsonToFactory(HCIslandConfig.world.generatorSettingsDefaults);
        }

        public BiomeSettings.Factory getSettingsFor(ResourceLocation registryName) {
            return biomeSettings.get(registryName);
        }

        public void updateBiomeSettingsFor(ResourceLocation registryName, BiomeSettings.Factory settings) {
            biomeSettings.put(registryName, settings);
        }
    }
}
