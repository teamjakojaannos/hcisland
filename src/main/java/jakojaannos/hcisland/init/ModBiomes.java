package jakojaannos.hcisland.init;

import jakojaannos.hcisland.ModInfo;
import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.biome.*;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
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
    public static final Biome __MASK = null;
    public static final Biome ISLAND = null;
    public static final Biome ISLAND_LOWLANDS = null;
    public static final Biome BEACH = null;
    public static final Biome OCEAN = null;
    public static final Biome WASTELAND = null;
    public static final Biome WASTELAND_EDGE = null;

    private static final int DEFAULT_SEA_LEVEL = 48;
    private static final int DEFAULT_SEA_LEVEL_NETHER = 31;

    private static <T> T value(T ifNether, T ifNormal) {
        return HCIslandConfig.world.generateNetherInsteadOfOverworld
                ? ifNether
                : ifNormal;
    }

    @SubscribeEvent
    public static void onRegisterBiomes(RegistryEvent.Register<Biome> event) {
        val r = event.getRegistry();
        register(r, "__mask", 0, BiomeType.DESERT, new Biome(new Biome.BiomeProperties("__mask")) {}, Type.WASTELAND);
        register(r, "island", 0, BiomeType.WARM, new BiomeHCForest(), Type.FOREST, Type.SPARSE);
        register(r, "island_lowlands", 0, BiomeType.WARM, new BiomeHCIslandLowlands(), Type.FOREST, Type.SPARSE);
        register(r, "beach", 0, BiomeType.WARM, new BiomeHCBeach(), Type.BEACH);
        register(r, "ocean", 0, BiomeType.WARM, new BiomeHCOcean(), Type.OCEAN, Type.DEAD);
        register(r, "wasteland", 0, BiomeType.DESERT, new BiomeHCWasteland<>(), Type.WASTELAND, Type.DEAD, Type.HOT);
        register(r, "wasteland_edge", 0, BiomeType.DESERT, new BiomeHCWastelandEdge<>(), Type.WASTELAND, Type.HOT);
    }

    private static void register(
            IForgeRegistry<Biome> registry,
            String key,
            int weight,
            BiomeManager.BiomeType managerType,
            Biome biome,
            BiomeDictionary.Type... types
    ) {
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
                new ForestBiomeSettingsAdapter<>(
                        new ResourceLocation(ModInfo.MODID, "island"),
                        () -> new LayeredBiomeSettings.Forest(
                                value(-0.1f, 0.25f),
                                value(0.1f, 0.1f),
                                value(DEFAULT_SEA_LEVEL_NETHER, DEFAULT_SEA_LEVEL),
                                Blocks.LAVA.getDefaultState(),
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
                                true,
                                false,
                                7,
                                4,
                                25
                        )),
                new IslandBiomeSettingsAdapter<>(
                        new ResourceLocation(ModInfo.MODID, "island_lowlands"),
                        () -> new LayeredBiomeSettings.Island(
                                value(-1.45f, -0.65f),
                                value(0.1f, 0.1f),
                                value(DEFAULT_SEA_LEVEL_NETHER, DEFAULT_SEA_LEVEL),
                                Blocks.LAVA.getDefaultState(),
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
                                false
                        )),
                new BeachBiomeSettingsAdapter<>(
                        new ResourceLocation(ModInfo.MODID, "beach"),
                        () -> new LayeredBiomeSettings.Beach(
                                value(-1.55f, -0.75f),
                                value(0.025f, 0.025f),
                                value(DEFAULT_SEA_LEVEL_NETHER, DEFAULT_SEA_LEVEL),
                                Blocks.LAVA.getDefaultState(),
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
                new LayeredBiomeSettingsAdapter<>(
                        new ResourceLocation(ModInfo.MODID, "ocean"),
                        () -> new LayeredBiomeSettings(
                                value(-1.95f, -1.8f),
                                value(0.0f, 0.1f),
                                value(DEFAULT_SEA_LEVEL_NETHER, DEFAULT_SEA_LEVEL),
                                Blocks.LAVA.getDefaultState(),
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
                                })),
                new WastelandBiomeSettingsAdapter<>(
                        new ResourceLocation(ModInfo.MODID, "wasteland"),
                        () -> new LayeredBiomeSettings.Wasteland(
                                0.4f,
                                0.25f,
                                value(DEFAULT_SEA_LEVEL_NETHER, DEFAULT_SEA_LEVEL),
                                Blocks.LAVA.getDefaultState(),
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
                new WastelandBiomeSettingsAdapter<>(
                        new ResourceLocation(ModInfo.MODID, "wasteland_edge"),
                        () -> new LayeredBiomeSettings.Wasteland(
                                0.2f,
                                0.1f,
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
