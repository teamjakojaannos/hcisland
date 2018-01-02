package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;

import java.util.function.Function;

public abstract class BiomeHCIslandBase<TSettings extends HCIslandChunkGeneratorSettings.BiomeSettings.Island> extends BiomeHCBase<TSettings> {
    private boolean generateLakesLava;
    private boolean generateLakes;

    public boolean generateLakes() {
        return generateLakes;
    }

    public boolean generateLakesLava() {
        return generateLakesLava;
    }

    BiomeHCIslandBase(BiomeProperties properties, Function<HCIslandChunkGeneratorSettings, TSettings> biomeSettingsMapper) {
        super(properties, biomeSettingsMapper);
        this.generateLakes = true;
        this.generateLakesLava = true;
    }

    @Override
    protected void applyBiomeSettings(TSettings settings) {
        super.applyBiomeSettings(settings);
        this.decorator.generateFalls = settings.generateFalls;
        this.generateLakes = settings.generateLakes;
        this.generateLakesLava = settings.generateLakesLava;
    }
}
