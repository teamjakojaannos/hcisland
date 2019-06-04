package jakojaannos.hcisland.world.gen;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.init.ModBiomes;
import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.util.UnitHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.var;
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
        private static final Gson JSON_ADAPTER = new GsonBuilder().setLenient()
                                                                  .registerTypeAdapter(Factory.class, (JsonDeserializer<Factory>) (json, typeOfT, context) -> {
                                                                      val result = new Factory(false);
                                                                      val jsonObject = json.getAsJsonObject();
                                                                      result.biomes = context.deserialize(jsonObject.getAsJsonArray("biomes"),
                                                                                                          new TypeToken<List<IslandRadialBiome.Factory>>() {
                                                                                                          }.getType());

                                                                      val resultBiomeSettings = new HashMap<ResourceLocation, BiomeSettings.Factory>();
                                                                      val biomeSettingsJson = jsonObject.getAsJsonObject("biomeSettings");
                                                                      for (val entry : result.biomeSettings.entrySet()) {
                                                                          val entryJson = biomeSettingsJson.getAsJsonObject(entry.getKey().toString());
                                                                          BiomeSettings.Factory biomeSettings = context.deserialize(entryJson, entry.getValue().getClass());

                                                                          resultBiomeSettings.put(entry.getKey(), biomeSettings);
                                                                      }
                                                                      result.biomeSettings = resultBiomeSettings;
                                                                      return result;
                                                                  })
                                                                  .create();

        public static boolean hasOverrides() {
            return !HCIslandConfig.world.generatorSettingsDefaults.isEmpty();
        }

        public static Factory createOverrides() {
            return jsonToFactory(HCIslandConfig.world.generatorSettingsDefaults, false);
        }

        private List<IslandRadialBiome.Factory> biomes;
        private Map<ResourceLocation, BiomeSettings.Factory> biomeSettings;

        public HCIslandChunkGeneratorSettings build() {
            return new HCIslandChunkGeneratorSettings(this);
        }

        public static Factory jsonToFactory(String json) {
            return jsonToFactory(json, true);
        }

        private static Factory jsonToFactory(String json, boolean useOverrides) {
            if (json.isEmpty()) {
                return new Factory(useOverrides);
            }

            try {
                Factory factory = JSON_ADAPTER.fromJson(json, Factory.class);
                if (factory == null) {
                    return new Factory(useOverrides);
                }

                return factory;
            } catch (Exception ignored) {
                return new Factory(useOverrides);
            }
        }

        @Override
        public String toString() {
            return JSON_ADAPTER.toJson(this);
        }

        public void setDefaults() {
            setDefaults(true);
        }

        private void setDefaults(boolean useOverrides) {
            if (useOverrides && hasOverrides()) {
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
            this(false);
        }

        public Factory(boolean useOverrides) {
            setDefaults(useOverrides);
        }

        private void setOverrides() {
            val overrides = createOverrides();
            biomes = overrides.biomes;
            biomeSettings = overrides.biomeSettings;
        }

        public BiomeSettings.Factory getSettingsFor(ResourceLocation registryName) {
            return biomeSettings.get(registryName);
        }

        public void updateBiomeSettingsFor(ResourceLocation registryName, BiomeSettings.Factory settings) {
            biomeSettings.put(registryName, settings);
        }
    }
}
