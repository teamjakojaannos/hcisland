package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class BiomeHCBase<TSettings extends BiomeSettings> extends AdvancedBiomeBase {
    private final Function<HCIslandChunkGeneratorSettings, TSettings> biomeSettingsMapper;

    BiomeHCBase(BiomeProperties properties, Function<HCIslandChunkGeneratorSettings, TSettings> biomeSettingsMapper) {
        super(properties);
        this.biomeSettingsMapper = biomeSettingsMapper;

        this.decorator.generateFalls = false;
        this.decorator.treesPerChunk = -999;
        this.decorator.cactiPerChunk = 0;
        this.decorator.flowersPerChunk = 0;
        this.decorator.grassPerChunk = 0;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.bigMushroomsPerChunk = 0;
        this.decorator.mushroomsPerChunk = 0;
    }

    @Override
    public int getSkyColorByTemp(float currentTemperature) {
        return MathHelper.hsvToRGB(0.07f, 0.75f, 0.85f);
    }


    public void applySettings(HCIslandChunkGeneratorSettings settings) {
        TSettings biomeSettings = biomeSettingsMapper.apply(settings);

        setSeaLevelOverride(settings.seaLevelOverride);
        setStoneBlock(biomeSettings.stoneBlock);
        setOceanBlock(settings.oceanBlockOverride);

        applyBiomeSettings(biomeSettings);
    }

    protected void applyBiomeSettings(TSettings settings) {
        setLayers(settings.layers, settings.layersUnderwater);
    }
}
