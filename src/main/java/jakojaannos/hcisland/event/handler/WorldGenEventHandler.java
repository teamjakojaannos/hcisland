package jakojaannos.hcisland.event.handler;

import jakojaannos.hcisland.HardcoreIsland;
import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.init.HCIslandLootTables;
import jakojaannos.hcisland.world.biome.BiomeHCIsland;
import jakojaannos.hcisland.world.biome.BiomeHCIslandBeach;
import jakojaannos.hcisland.world.biome.BiomeHCWasteland;
import jakojaannos.hcisland.world.biome.BiomeHCWastelandBeach;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class WorldGenEventHandler {
    private static final WorldGenFire fireFeature = new WorldGenFire();
    private static final Logger LOGGER = LogManager.getLogger("hcisland");

    /**
     * Deny lake generation in modded biomes
     */
    @SubscribeEvent
    public static void onPopulateChunk(PopulateChunkEvent.Populate event) {
        // Process only on Hardcore Island world type
        final World world = event.getWorld();
        if (world.getWorldType() != HardcoreIsland.WORLD_TYPE) {
            return;
        }

        final int cX = event.getChunkX();
        final int cZ = event.getChunkZ();

        final BlockPos blockpos = new BlockPos(cX * 16, 0, cZ * 16);
        final Biome biome = world.getBiome(blockpos.add(16, 0, 16));

        // Deny lake generation in wasteland if config flag is not set
        if ((!HCIslandConfig.worldGen.generateLakesWasteland && biome instanceof BiomeHCWasteland)
                // Deny lake generation on island if config flag is not set
                || (!HCIslandConfig.worldGen.generateLakesIsland && biome instanceof BiomeHCIsland)
                // Always deny generation on beaches
                || (biome instanceof BiomeHCIslandBeach || biome instanceof BiomeHCWastelandBeach)) {
            if (event.getType() == PopulateChunkEvent.Populate.EventType.LAKE) {
                event.setResult(Event.Result.DENY);
            }
        }
    }


    /**
     * Generate fire to wasteland biome
     */
    @SubscribeEvent
    public static void onPopulateChunk(PopulateChunkEvent.Post event) {
        final World world = event.getWorld();
        if (world.getWorldType() != HardcoreIsland.WORLD_TYPE) {
            return;
        }

        final int cX = event.getChunkX();
        final int cZ = event.getChunkZ();

        final BlockPos blockpos = new BlockPos(cX * 16, 0, cZ * 16);
        final Biome biome = world.getBiome(blockpos.add(16, 0, 16));

        // Generate fire in the wasteland biome
        if (HCIslandConfig.worldGen.generateFireWasteland && biome instanceof BiomeHCWasteland) {
            Random rand = event.getRand();

            if (TerrainGen.populate(event.getGen(), world, rand, cX, cZ, false, PopulateChunkEvent.Populate.EventType.FIRE)) {
                for (int i1 = 0; i1 < rand.nextInt(rand.nextInt(10) + 1) + 1; ++i1) {
                    fireFeature.generate(world, rand, blockpos.add(rand.nextInt(16) + 8, rand.nextInt(120) + 4, rand.nextInt(16) + 8));
                }
            }
        }
    }


    /**
     * Generate starter chests
     */
    @SubscribeEvent
    public static void onPostDecorate(DecorateBiomeEvent.Post event) {
        if (event.getWorld().getWorldType() == HardcoreIsland.WORLD_TYPE) {
            generateStartingGear(event.getPos().getX(), event.getPos().getZ(), event.getWorld(), event.getRand());
        }
    }

    private static void generateStartingGear(int chunkX, int chunkZ, World world, Random rand) {
        if (chunkX == 0 && chunkZ == 0) {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            List<BlockPos> suitableSpots = new ArrayList<>(128);

            for (int x = -16; x < 16; ++x) {
                for (int z = -16; z < 16; ++z) {
                    for (int y = world.getHeight() - 1; y > 0; --y) {
                        pos.setPos(x, y, z);
                        IBlockState state = world.getBlockState(pos);
                        if (state.getBlock() != Blocks.AIR && state.getMaterial() != Material.LEAVES) {
                            if (state.getBlock() == Blocks.GRASS) {
                                suitableSpots.add(pos.toImmutable());
                            }
                            break;
                        }
                    }
                }
            }

            generateChestAt(world, selectRandomSpot(suitableSpots, world, rand), HCIslandLootTables.STARTING_GEAR);
            generateChestAt(world, selectRandomSpot(suitableSpots, world, rand), HCIslandLootTables.BONUS_GEAR);
        }
    }

    private static BlockPos selectRandomSpot(List<BlockPos> positions, World world, Random random) {
        int index;
        if (positions.isEmpty()) {
            index = world.getHeight(0, 0) + 1;
            LOGGER.warn("Could not find suitable spot for generating gear. Generating gear at 0, {}, 0", index);
            return new BlockPos(0, index, 0);
        } else {
            index = random.nextInt(positions.size());
            return positions.remove(index).up();
        }
    }

    private static void generateChestAt(World world, BlockPos pos, ResourceLocation lootTable) {
        world.setBlockState(pos, Blocks.CHEST.getDefaultState());
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) tileEntity;
            chest.setLootTable(lootTable, world.getSeed());
        } else {
            LOGGER.error("Failed generating starting gear at {}, {}, {}!", pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
