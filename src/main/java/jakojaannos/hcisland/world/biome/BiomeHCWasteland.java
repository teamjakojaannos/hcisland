package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.init.ModBiomes;
import jakojaannos.hcisland.world.gen.LayeredBiomeSettings;
import lombok.val;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class BiomeHCWasteland<TSettings extends LayeredBiomeSettings.Wasteland> extends BiomeHCWastelandBase {
    public BiomeHCWasteland() {
        this(getProperties());
    }

    protected BiomeHCWasteland(BiomeProperties properties) {
        super(properties);
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Wasteland");
        props.setTemperature(HCIslandConfig.world.temperatureWasteland);

        return props;
    }

    @Nullable
    @Override
    public Biome getEdgeBiome() {
        return ModBiomes.WASTELAND_EDGE;
    }

    @Override
    public boolean isCompatibleWith(Biome other) {
        val otherSeaLevel = other instanceof BiomeLayeredBase
                ? ((BiomeLayeredBase) other).getSeaLevelOverride()
                : 64;
        val ownSeaLevel = getSeaLevelOverride() != -1
                ? getSeaLevelOverride()
                : 64;
        return otherSeaLevel == ownSeaLevel;
    }
}
