package jakojaannos.hcisland.world.biome;

import jakojaannos.api.helpers.BlockHelper;
import jakojaannos.api.world.AdvancedBiomeBase;
import jakojaannos.api.world.BlockLayer;
import jakojaannos.hcisland.config.ConfigWorldGen;
import jakojaannos.hcisland.config.HCIslandConfig;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class BiomeHCBase extends AdvancedBiomeBase {
    BiomeHCBase(Biome.BiomeProperties properties, ConfigWorldGen.BiomeConfig config) {
        super(properties, config.config);

        // Global properties (override per-biome settings)

        // Unify sea level of all lake biomes
        setSeaLevelOverride(HCIslandConfig.worldGen.lakeSeaLevel);

        // Set all lake biomes to use the same ocean block
        setOceanBlock(BlockHelper.stringToBlockstateWithFallback(Blocks.LAVA.getDefaultState(), HCIslandConfig.worldGen.blockLakeLiquid));
    }

    @Override
    public int getSkyColorByTemp(float currentTemperature) {
        return MathHelper.hsvToRGB(0.07f, 0.75f, 0.85f);
    }


}
