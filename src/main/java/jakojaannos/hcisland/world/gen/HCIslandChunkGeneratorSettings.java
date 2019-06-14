package jakojaannos.hcisland.world.gen;

import com.google.common.collect.Lists;
import jakojaannos.hcisland.init.ModBiomes;
import jakojaannos.hcisland.init.ModRegistries;
import lombok.*;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor()
public class HCIslandChunkGeneratorSettings {
    @Getter @Setter private int islandShapeFuzz = 2;
    @Getter @Setter private boolean smoothBiomeEdges = true;
    @Getter @Setter private boolean generateEdges = true;
    @Getter @Setter private int shoreScale = 1;
    @Getter @Setter private int beachSize = 2;
    @Getter @Setter private List<IslandRadialBiome> biomes = Lists.newArrayList(
            new IslandRadialBiome(4, true, ModBiomes.ISLAND.getRegistryName()),
            new IslandRadialBiome(3, true, ModBiomes.ISLAND_LOWLANDS.getRegistryName()),
            new IslandRadialBiome(8, false, ModBiomes.OCEAN.getRegistryName()),
            new IslandRadialBiome(8, false, ModBiomes.WASTELAND.getRegistryName()),
            new IslandRadialBiome(3, false, Biomes.DESERT.getRegistryName())
    );
    private Map<ResourceLocation, BiomeSettings> biomeSettings =
            ModRegistries.BIOME_ADAPTERS.getEntries()
                                        .stream()
                                        .collect(Collectors.toMap(Map.Entry::getKey,
                                                                  e -> e.getValue().createDefaultSettingsFactory()));

    @Nullable
    public Biome getBiomeAtDistanceSq(long distSq, double unitScale) {
        if (biomes.isEmpty()) {
            return null;
        }

        var distance = 0L;
        for (val biomeEntry : biomes) {
            val newDistance = (distance + biomeEntry.radius) * unitScale;
            if (distSq < newDistance * newDistance) {
                return biomeEntry.getBiome();
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

    public void setSettingsFor(ResourceLocation registryName, BiomeSettings biomeSettings) {
        this.biomeSettings.put(registryName, biomeSettings);
    }

    @NoArgsConstructor
    public static class IslandRadialBiome {
        @Getter @Setter private int radius = 8;
        @Getter @Setter private boolean spawn = false;
        @Getter @Setter private ResourceLocation biomeId = new ResourceLocation("minecraft:forest");

        private transient Biome cachedBiome;

        @Nullable
        public Biome getBiome() {
            return cachedBiome == null
                    ? cachedBiome = ForgeRegistries.BIOMES.getValue(this.biomeId)
                    : cachedBiome;
        }

        public IslandRadialBiome(int radius, boolean spawn, ResourceLocation biomeId) {
            this.radius = radius;
            this.spawn = spawn;
            this.biomeId = biomeId;
        }
    }
}
