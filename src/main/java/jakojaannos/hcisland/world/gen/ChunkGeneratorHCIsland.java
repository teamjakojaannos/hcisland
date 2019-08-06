package jakojaannos.hcisland.world.gen;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.WorldTypeHCIsland;
import lombok.val;
import lombok.var;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

import javax.annotation.Nullable;

public class ChunkGeneratorHCIsland extends ChunkGeneratorOverworld {
    @Nullable private final ChunkGeneratorHell hellGenerator;
    private final World world;

    public ChunkGeneratorHCIsland(
            World world,
            String generatorOptions
    ) {
        super(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);

        this.world = world;
        this.hellGenerator = HCIslandConfig.world.generateNetherInsteadOfOverworld
                ? new ChunkGeneratorHCIslandHell(world)
                : null;
    }

    protected final boolean shouldChunkUseNetherGenerator(int x, int z) {
        val worldType = world.getWorldType();
        if (worldType instanceof WorldTypeHCIsland) {
            val settings = ((WorldTypeHCIsland) worldType).getSettings();
            val totalRadialBiomeRadius = settings.getTotalRadialZoneRadius();
            val safetyMargin = Math.pow(2, settings.getIslandShapeFuzz());
            val totalRadiusSq = (totalRadialBiomeRadius + safetyMargin) * (totalRadialBiomeRadius + safetyMargin);

            val distanceSq = (x * x) + (z * z);
            return distanceSq > totalRadiusSq;
        }

        // Useless fallback, but just in case some other mod does something goofy
        return world.provider.getDimensionType() == DimensionType.NETHER;
    }

    protected final boolean canChunkGenerateStructures(int x, int z) {
        val worldType = world.getWorldType();
        if (worldType instanceof WorldTypeHCIsland) {
            val settings = ((WorldTypeHCIsland) worldType).getSettings();
            val totalRadialBiomeRadius = settings.getTotalRadialZoneRadius();
            val noStructuresRadius = 8;
            val safetyMargin = Math.pow(2, settings.getIslandShapeFuzz()) + noStructuresRadius;
            val totalRadiusSq = (totalRadialBiomeRadius + safetyMargin) * (totalRadialBiomeRadius + safetyMargin);

            val distanceSq = (x * x) + (z * z);
            return distanceSq > totalRadiusSq;
        }

        // Useless fallback, but just in case some other mod does something goofy
        return world.provider.getDimensionType() == DimensionType.NETHER;
    }

    @Override
    public void generateHeightmap(int x, int y, int z) {
        super.generateHeightmap(x, y, z);
        if (!HCIslandConfig.world.generateNetherInsteadOfOverworld) {
            return;
        }

        val worldType = world.getWorldType();
        val s = ((WorldTypeHCIsland) worldType).getSettings();
        val totalRadialBiomeRadius = s.getTotalRadialZoneRadius();
        val safetyMargin = Math.pow(2, s.getIslandShapeFuzz()) + 1;
        val fadeDistance = 3;
        val fadeRadiusSqStart = (totalRadialBiomeRadius - fadeDistance) * (totalRadialBiomeRadius - fadeDistance);
        val fadeRadiusSqEnd = (totalRadialBiomeRadius + safetyMargin) * (totalRadialBiomeRadius + safetyMargin);

        val chunkX = x / 4.0;
        val chunkZ = z / 4.0;
        val chunkDistanceSq = (chunkX * chunkX) + (chunkZ * chunkZ);
        if (chunkDistanceSq > fadeRadiusSqStart && chunkDistanceSq < fadeRadiusSqEnd) {
            var i = 0;
            for (var ix = 0; ix < 5; ix++) {
                for (var iz = 0; iz < 5; iz++) {
                    val columnX = chunkX + ix / 5.0;
                    val columnZ = chunkZ + iz / 5.0;
                    val distanceSq = (columnX * columnX) + (columnZ * columnZ);
                    double t = (distanceSq - fadeRadiusSqStart) / (fadeRadiusSqEnd - fadeRadiusSqStart);

                    for (var j = 0; j < 33; j++) {
                        val original = this.heightMap[i];
                        val newValue = (float) MathHelper.clampedLerp(original, -150.0, t);
                        this.heightMap[i++] = newValue;
                    }
                }
            }
        }
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        if (hellGenerator != null && shouldChunkUseNetherGenerator(x, z)) {
            val original = hellGenerator.generateStructures;
            hellGenerator.generateStructures = canChunkGenerateStructures(x, z);
            val chunk = hellGenerator.generateChunk(x, z);
            hellGenerator.generateStructures = original;

            return chunk;
        }

        return super.generateChunk(x, z);
    }

    @Override
    public void populate(int x, int z) {
        if (hellGenerator != null && shouldChunkUseNetherGenerator(x, z)) {
            hellGenerator.populate(x, z);
        } else {
            super.populate(x, z);
        }
    }

    @Override
    public boolean generateStructures(Chunk chunk, int x, int z) {
        return hellGenerator != null && shouldChunkUseNetherGenerator(x, z)
                ? hellGenerator.generateStructures(chunk, x, z)
                : super.generateStructures(chunk, x, z);
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(
            World worldIn, String structureName, BlockPos position, boolean findUnexplored
    ) {
        val overworldNearest = super.getNearestStructurePos(worldIn, structureName, position, findUnexplored);
        if (overworldNearest == null && hellGenerator != null) {
            return hellGenerator.getNearestStructurePos(worldIn, structureName, position, findUnexplored);
        }

        return overworldNearest;
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        val overworldIsInside = super.isInsideStructure(worldIn, structureName, pos);
        if (!overworldIsInside && hellGenerator != null) {
            return hellGenerator.isInsideStructure(worldIn, structureName, pos);
        }

        return overworldIsInside;
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {
        if (hellGenerator != null && shouldChunkUseNetherGenerator(x, z)) {
            hellGenerator.recreateStructures(chunk, x, z);
        } else {
            super.recreateStructures(chunk, x, z);
        }
    }
}
