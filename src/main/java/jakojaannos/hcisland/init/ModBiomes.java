package jakojaannos.hcisland.init;

import jakojaannos.hcisland.ModInfo;
import jakojaannos.hcisland.world.biome.*;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.BiomeSettingsAdapter;
import lombok.val;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(ModInfo.MODID)
@Mod.EventBusSubscriber
public class ModBiomes {
    public static final Biome ISLAND = null;
    public static final Biome ISLAND_BEACH = null;
    public static final Biome OCEAN = null;
    public static final Biome WASTELAND = null;
    public static final Biome WASTELAND_BEACH = null;
    public static final Biome WASTELAND_EDGE = null;

    @SubscribeEvent
    public static void onRegisterBiomes(RegistryEvent.Register<Biome> event) {
        val r = event.getRegistry();
        register(r, "island", 0, BiomeType.WARM, new BiomeHCIsland(), Type.FOREST, Type.SPARSE);
        register(r, "island_beach", 0, BiomeType.WARM, new BiomeHCIslandBeach(), Type.BEACH);
        register(r, "ocean", 0, BiomeType.WARM, new BiomeHCOcean(), Type.OCEAN, Type.DEAD);
        register(r, "wasteland", 0, BiomeType.DESERT, new BiomeHCWasteland(), Type.WASTELAND, Type.DEAD, Type.HOT);
        register(r, "wasteland_beach", 0, BiomeType.DESERT, new BiomeHCWastelandBeach(), Type.WASTELAND, Type.BEACH);
        register(r, "wasteland_edge", 0, BiomeType.DESERT, new BiomeHCWastelandEdge(), Type.WASTELAND, Type.HOT);
    }

    private static void register(IForgeRegistry<Biome> registry, String key, int weight, BiomeManager.BiomeType managerType, Biome biome, BiomeDictionary.Type... types) {
        biome.setRegistryName(new ResourceLocation(ModInfo.MODID, key));
        registry.register(biome);

        BiomeManager.addBiome(managerType, new BiomeManager.BiomeEntry(biome, weight));
        BiomeDictionary.addTypes(biome, types);
    }

    @SubscribeEvent
    public static void onRegisterSettingsAdapters(RegistryEvent.Register<BiomeSettingsAdapter> event) {
        event.getRegistry().registerAll(
                new BiomeSettingsAdapter( rl("island"), BiomeSettings.Island.Factory::new),
                new BiomeSettingsAdapter( rl("island_beach"), BiomeSettings.Beach.Factory::new),
                new BiomeSettingsAdapter( rl("ocean"), BiomeSettings.Factory::new),
                new BiomeSettingsAdapter( rl("wasteland"), BiomeSettings.Wasteland.Factory::new),
                new BiomeSettingsAdapter( rl("wasteland_beach"), BiomeSettings.Wasteland.Factory::new),
                new BiomeSettingsAdapter( rl("wasteland_edge"), BiomeSettings.Wasteland.Factory::new)
        );
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(ModInfo.MODID, path);
    }
}