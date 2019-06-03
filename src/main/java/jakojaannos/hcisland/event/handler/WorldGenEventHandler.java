package jakojaannos.hcisland.event.handler;

import jakojaannos.hcisland.HardcoreIsland;
import jakojaannos.hcisland.init.ModLootTables;
import jakojaannos.hcisland.world.biome.BiomeHCIslandBase;
import jakojaannos.hcisland.world.biome.BiomeHCWasteland;
import jakojaannos.hcisland.world.storage.HCIslandWorldSavedData;
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

        // Find biome at position we are generating
        final int cX = event.getChunkX();
        final int cZ = event.getChunkZ();
        final BlockPos blockpos = new BlockPos(cX * 16, 0, cZ * 16);
        final Biome biome = world.getBiome(blockpos.add(16, 0, 16));

        // If it is modded biome with lake generation disabled, deny the event
        if (biome instanceof BiomeHCIslandBase) {
            BiomeHCIslandBase<?> biomeIsland = (BiomeHCIslandBase<?>) biome;

            if (event.getType() == PopulateChunkEvent.Populate.EventType.LAKE && !biomeIsland.generateLakes()) {
                event.setResult(Event.Result.DENY);
            } else if (event.getType() == PopulateChunkEvent.Populate.EventType.LAVA && !biomeIsland.generateLakesLava()) {
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
        if (world.isRemote) {
            return;
        }

        if (world.getWorldType() != HardcoreIsland.WORLD_TYPE) {
            return;
        }

        final int cX = event.getChunkX();
        final int cZ = event.getChunkZ();

        final BlockPos blockpos = new BlockPos(cX * 16, 0, cZ * 16);
        final Biome biome = world.getBiome(blockpos.add(16, 0, 16));

        // Generate fire in the wasteland biome
        if (biome instanceof BiomeHCWasteland && ((BiomeHCWasteland) biome).generateFire()) {
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
            final int chunkX = event.getChunkPos().x;
            final int chunkZ = event.getChunkPos().z;
            final boolean isSpawn = chunkX == 0 && chunkZ == 0;
            final boolean generated = HCIslandWorldSavedData.getInstance(event.getWorld()).starterChestGenerated;

            if (isSpawn && !generated) {
                generateStartingGear(event.getWorld(), event.getRand());
            }
        }
    }

    private static void generateStartingGear(World world, Random rand) {
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

        generateChestAt(world, selectRandomSpot(suitableSpots, world, rand), ModLootTables.STARTING_GEAR);
        generateChestAt(world, selectRandomSpot(suitableSpots, world, rand), ModLootTables.BONUS_GEAR);

        HCIslandWorldSavedData.getInstance(world).starterChestGenerated = true;
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
