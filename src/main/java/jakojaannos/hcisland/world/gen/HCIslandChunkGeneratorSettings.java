package jakojaannos.hcisland.world.gen;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.init.ModBiomes;
import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.util.json.BlockStateTypeAdapterFactory;
import lombok.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HCIslandChunkGeneratorSettings {
    @Getter private final int islandShapeFuzz;
    @Getter private final boolean smoothBiomeEdges;
    @Getter private final boolean generateEdges;
    @Getter private final int shoreScale;
    private final List<IslandRadialBiome> biomes;
    private final Map<ResourceLocation, BiomeSettings> biomeSettings;

    private HCIslandChunkGeneratorSettings(Factory factory) {
        this.islandShapeFuzz = factory.islandShapeFuzz;
        this.smoothBiomeEdges = factory.smoothBiomeEdges;
        this.generateEdges = factory.generateEdges;
        this.shoreScale = factory.shoreScale;
        this.biomeSettings = factory.biomeSettings;

        this.biomes = factory.biomes.stream()
                                    .map(IslandRadialBiome::new)
                                    .collect(Collectors.toList());
    }

    @Nullable
    public Biome getBiomeAtDistanceSq(long distSq, double unitScale) {
        if (biomes.isEmpty()) {
            return null;
        }

        var distance = 0L;
        for (val biomeEntry : biomes) {
            val newDistance = (distance + biomeEntry.radius) * unitScale;
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

    public List<Biome> getSpawnBiomes() {
        return biomes.stream()
                     .filter(IslandRadialBiome::isSpawn)
                     .map(IslandRadialBiome::getBiome)
                     .collect(Collectors.toList());
    }

    public static class IslandRadialBiome {
        @Getter private int radius;
        @Getter private ResourceLocation biomeId;
        @Getter private Biome biome;
        @Getter private boolean spawn;

        public IslandRadialBiome(IslandRadialBiome.Factory factory) {
            this.radius = factory.radius + HCIslandConfig.world.generatorBiomeRadiusOffset;
            this.spawn = factory.spawn;
            this.biomeId = new ResourceLocation(factory.biomeId);
            this.biome = ForgeRegistries.BIOMES.getValue(this.biomeId);
        }

        @AllArgsConstructor
        public static class Factory {
            @Getter @Setter private int radius;
            @Getter @Setter private boolean spawn;
            @Getter @Setter private String biomeId;
        }
    }

    public static class Factory {
        private static final Gson JSON_ADAPTER = new GsonBuilder().setLenient()
                                                                  .registerTypeAdapterFactory(new BlockStateTypeAdapterFactory())
                                                                  .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
                                                                  .registerTypeAdapter(Factory.class, new FactorySerializer())
                                                                  .create();

        private static class FactorySerializer implements JsonDeserializer<Factory> {
            @Override
            public Factory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                val result = new Factory(false);
                val jsonObject = json.getAsJsonObject();
                result.biomes = context.deserialize(jsonObject.getAsJsonArray("biomes"),
                                                    new TypeToken<List<IslandRadialBiome.Factory>>() {
                                                    }.getType());

                val resultBiomeSettings = new HashMap<ResourceLocation, BiomeSettings>();
                val biomeSettingsJson = jsonObject.getAsJsonObject("biomeSettings");
                for (val entry : result.biomeSettings.entrySet()) {
                    val entryJson = biomeSettingsJson.getAsJsonObject(entry.getKey().toString());
                    BiomeSettings biomeSettings = context.deserialize(entryJson, entry.getValue().getClass());

                    resultBiomeSettings.put(entry.getKey(), biomeSettings);
                }
                result.biomeSettings = resultBiomeSettings;

                Optional.ofNullable(jsonObject.getAsJsonPrimitive("islandShapeFuzz"))
                        .ifPresent(primitive -> result.islandShapeFuzz = primitive.getAsInt());
                Optional.ofNullable(jsonObject.getAsJsonPrimitive("smoothBiomeEdges"))
                        .ifPresent(primitive -> result.smoothBiomeEdges = primitive.getAsBoolean());
                Optional.ofNullable(jsonObject.getAsJsonPrimitive("generateEdges"))
                        .ifPresent(primitive -> result.smoothBiomeEdges = primitive.getAsBoolean());
                Optional.ofNullable(jsonObject.getAsJsonPrimitive("shoreScale"))
                        .ifPresent(primitive -> result.shoreScale = primitive.getAsInt());


                return result;
            }
        }

        public static boolean hasOverrides() {
            return !HCIslandConfig.world.generatorSettingsDefaults.isEmpty();
        }

        public static Factory createOverrides() {
            return jsonToFactory(HCIslandConfig.world.generatorSettingsDefaults, false);
        }

        @Getter @Setter private int islandShapeFuzz;
        @Getter @Setter private boolean smoothBiomeEdges;
        @Getter @Setter private boolean generateEdges;
        @Getter @Setter private int shoreScale;
        @Getter @Setter private List<IslandRadialBiome.Factory> biomes;
        private Map<ResourceLocation, BiomeSettings> biomeSettings;

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

        public static String toJson(Factory factory) {
            return JSON_ADAPTER.toJson(factory);
        }

        public void setDefaults() {
            setDefaults(true);
        }

        private void setDefaults(boolean useOverrides) {
            if (useOverrides && hasOverrides()) {
                setOverrides();
                return;
            }

            // TODO: make lava lake great again
            // TODO: possibly replace the lake of lava with a void-pit?
            biomes = Lists.newArrayList(
                    new IslandRadialBiome.Factory(5, true, ModBiomes.ISLAND.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(8, false, ModBiomes.OCEAN.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(4, false, ModBiomes.WASTELAND.getRegistryName().toString())
            );

            /*biomes = Lists.newArrayList(
                    new IslandRadialBiome.Factory(3, true, ModBiomes.ISLAND.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(8, false, Biomes.OCEAN.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(4, false, ModBiomes.WASTELAND.getRegistryName().toString()),
                    new IslandRadialBiome.Factory(1, false, ModBiomes.WASTELAND_EDGE.getRegistryName().toString())
            );*/

            islandShapeFuzz = 2;
            smoothBiomeEdges = true;
            generateEdges = true;
            shoreScale = 2;

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

        public BiomeSettings getSettingsFor(ResourceLocation registryName) {
            return biomeSettings.get(registryName);
        }

        public void updateBiomeSettingsFor(ResourceLocation registryName, BiomeSettings settings) {
            biomeSettings.put(registryName, settings);
        }
    }
}
