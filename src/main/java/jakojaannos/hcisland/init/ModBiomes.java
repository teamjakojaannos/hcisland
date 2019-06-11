package jakojaannos.hcisland.init;

import jakojaannos.hcisland.ModInfo;
import jakojaannos.hcisland.world.biome.*;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.adapter.*;
import lombok.val;
import net.minecraft.block.BlockSand;
import net.minecraft.init.Blocks;
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
    public static final Biome BEACH = null;
    public static final Biome OCEAN = null;
    public static final Biome WASTELAND = null;
    public static final Biome WASTELAND_BEACH = null;
    public static final Biome WASTELAND_EDGE = null;

    @SubscribeEvent
    public static void onRegisterBiomes(RegistryEvent.Register<Biome> event) {
        val r = event.getRegistry();
        register(r, "island", 0, BiomeType.WARM, new BiomeHCIsland(), Type.FOREST, Type.SPARSE);
        register(r, "beach", 0, BiomeType.WARM, new BiomeHCBeach(), Type.BEACH);
        register(r, "ocean", 0, BiomeType.WARM, new BiomeHCOcean(), Type.OCEAN, Type.DEAD);
        register(r, "wasteland", 0, BiomeType.DESERT, new BiomeHCWasteland(), Type.WASTELAND, Type.DEAD, Type.HOT);
        register(r, "wasteland_beach", 0, BiomeType.DESERT, new BiomeHCWastelandBeach(), Type.WASTELAND, Type.BEACH);
        register(r, "wasteland_edge", 0, BiomeType.DESERT, new BiomeHCWastelandEdge(), Type.WASTELAND, Type.HOT);
    }

    private static void register(IForgeRegistry<Biome> registry, String key, int weight, BiomeManager.BiomeType managerType, Biome biome, BiomeDictionary.Type... types) {
        biome.setRegistryName(new ResourceLocation(ModInfo.MODID, key));
        registry.register(biome);
        BiomeDictionary.addTypes(biome, types);

        if (weight > 0) {
            BiomeManager.addBiome(managerType, new BiomeManager.BiomeEntry(biome, weight));
        }
    }

    @SubscribeEvent
    public static void onRegisterSettingsAdapters(RegistryEvent.Register<BiomeSettingsAdapter> event) {
        event.getRegistry().registerAll(
                new ForestBiomeSettingsAdapter(
                        new ResourceLocation(ModInfo.MODID, "island"),
                        () -> new BiomeSettings.Forest(
                                48,
                                Blocks.WATER.getDefaultState(),
                                Blocks.STONE.getDefaultState(),
                                new BlockLayer[]{
                                        new BlockLayer(1, Blocks.GRASS),
                                        new BlockLayer(5, Blocks.DIRT),
                                        new BlockLayer(1, Blocks.CLAY),
                                        new BlockLayer(2, Blocks.GRAVEL),
                                },
                                new BlockLayer[]{
                                },
                                false,
                                false,
                                false,
                                7,
                                4,
                                25
                        )),
                new BeachBiomeSettingsAdapter(
                        new ResourceLocation(ModInfo.MODID, "beach"),
                        () -> new BiomeSettings.Beach(
                                48,
                                Blocks.WATER.getDefaultState(),
                                Blocks.STONE.getDefaultState(),
                                new BlockLayer[]{
                                        new BlockLayer(4, Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND)),
                                        new BlockLayer(2, Blocks.RED_SANDSTONE),
                                        new BlockLayer(1, Blocks.CLAY),
                                        new BlockLayer(2, Blocks.GRAVEL),
                                },
                                new BlockLayer[]{
                                        new BlockLayer(2, Blocks.OBSIDIAN),
                                        new BlockLayer(1, Blocks.SANDSTONE),
                                        new BlockLayer(1, Blocks.HARDENED_CLAY),
                                        new BlockLayer(2, Blocks.NETHERRACK),
                                },
                                false,
                                false,
                                false,
                                12
                        )),
                new AdvancedBiomeSettingsAdapter(
                        new ResourceLocation(ModInfo.MODID, "ocean"),
                        () -> new BiomeSettings(
                                48,
                                Blocks.WATER.getDefaultState(),
                                Blocks.STONE.getDefaultState(),
                                new BlockLayer[]{
                                        new BlockLayer(3, Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND)),
                                        new BlockLayer(1, Blocks.RED_SANDSTONE),
                                },
                                new BlockLayer[]{
                                        new BlockLayer(2, Blocks.OBSIDIAN),
                                        new BlockLayer(1, Blocks.SANDSTONE),
                                        new BlockLayer(1, Blocks.HARDENED_CLAY),
                                        new BlockLayer(2, Blocks.NETHERRACK),
                                }
                        )),
                new WastelandBiomeSettingsAdapter(
                        new ResourceLocation(ModInfo.MODID, "wasteland"),
                        () -> new BiomeSettings.Wasteland(
                                48,
                                Blocks.WATER.getDefaultState(),
                                Blocks.NETHERRACK.getDefaultState(),
                                new BlockLayer[]{
                                        new BlockLayer(8, Blocks.NETHERRACK),
                                },
                                new BlockLayer[]{
                                        new BlockLayer(2, Blocks.OBSIDIAN),
                                        new BlockLayer(1, Blocks.SANDSTONE),
                                        new BlockLayer(1, Blocks.HARDENED_CLAY),
                                        new BlockLayer(2, Blocks.NETHERRACK),
                                },
                                true,
                                false,
                                true,
                                true
                        )),
                new WastelandBiomeSettingsAdapter(
                        new ResourceLocation(ModInfo.MODID, "wasteland_beach"),
                        () -> new BiomeSettings.Wasteland(
                                48,
                                Blocks.WATER.getDefaultState(),
                                Blocks.NETHERRACK.getDefaultState(),
                                new BlockLayer[]{
                                        new BlockLayer(16, Blocks.NETHERRACK),
                                },
                                new BlockLayer[]{
                                        new BlockLayer(2, Blocks.OBSIDIAN),
                                        new BlockLayer(1, Blocks.SANDSTONE),
                                        new BlockLayer(1, Blocks.HARDENED_CLAY),
                                        new BlockLayer(2, Blocks.NETHERRACK),
                                },
                                true,
                                false,
                                true,
                                true
                        )),
                new WastelandBiomeSettingsAdapter(
                        new ResourceLocation(ModInfo.MODID, "wasteland_edge"),
                        () -> new BiomeSettings.Wasteland(
                                64,
                                Blocks.WATER.getDefaultState(),
                                Blocks.NETHERRACK.getDefaultState(),
                                new BlockLayer[]{
                                        new BlockLayer(8, Blocks.NETHERRACK),
                                        new BlockLayer(2, Blocks.GRAVEL),
                                },
                                new BlockLayer[]{
                                        new BlockLayer(8, Blocks.NETHERRACK),
                                },
                                true,
                                true,
                                true,
                                true
                        ))
        );
    }
}
