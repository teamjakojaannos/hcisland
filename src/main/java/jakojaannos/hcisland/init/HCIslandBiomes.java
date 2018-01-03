package jakojaannos.hcisland.init;

import jakojaannos.hcisland.ModInfo;
import jakojaannos.hcisland.world.biome.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

@ObjectHolder(ModInfo.MODID)
public class HCIslandBiomes {
    public static final Biome ISLAND = null;
    public static final Biome ISLAND_BEACH = null;
    public static final Biome OCEAN = null;
    public static final Biome WASTELAND = null;
    public static final Biome WASTELAND_BEACH = null;
    public static final Biome WASTELAND_EDGE = null;

    public void initBiomes() {
        // @formatter:off
        register("island",          0, BiomeType.WARM,      new BiomeHCIsland(),         Type.FOREST, Type.SPARSE);
        register("island_beach",    0, BiomeType.WARM,      new BiomeHCIslandBeach(),    Type.BEACH);
        register("ocean",           0, BiomeType.WARM,      new BiomeHCOcean(),          Type.OCEAN, Type.DEAD);
        register("wasteland",       0, BiomeType.DESERT,    new BiomeHCWasteland(),      Type.WASTELAND, Type.DEAD, Type.HOT);
        register("wasteland_beach", 0, BiomeType.DESERT,    new BiomeHCWastelandBeach(), Type.WASTELAND, Type.BEACH);
        register("wasteland_edge",  0, BiomeType.DESERT,    new BiomeHCWastelandEdge(),  Type.WASTELAND, Type.HOT);
        // @formatter:on
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Private implementation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final Map<String, Entry> biomes = new HashMap<>();

    protected void register(String key, int weight, BiomeManager.BiomeType type, Biome biome, BiomeDictionary.Type... types) {
        biomes.put(key, new Entry(biome, weight, type, types));
    }


    @SubscribeEvent
    public void onRegisterBiomes(RegistryEvent.Register<Biome> event) {
        biomes.forEach((s, entry) -> doRegisterBiome(event.getRegistry(), s, entry));
    }

    private void doRegisterBiome(IForgeRegistry<Biome> registry, String key, Entry entry) {
        entry.biome.setRegistryName(new ResourceLocation(ModInfo.MODID, key));
        registry.register(entry.biome);

        BiomeManager.addBiome(entry.type, new BiomeManager.BiomeEntry(entry.biome, entry.weight));
        BiomeDictionary.addTypes(entry.biome, entry.dictTypes);
    }

    private class Entry {
        Biome biome;
        int weight;
        BiomeManager.BiomeType type;
        BiomeDictionary.Type[] dictTypes;

        private Entry(Biome biome, int weight, BiomeManager.BiomeType type, BiomeDictionary.Type... dictTypes) {
            this.biome = biome;
            this.weight = weight;
            this.type = type;
            this.dictTypes = dictTypes;
        }
    }
}
