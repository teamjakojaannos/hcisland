package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.world.gen.BiomeSettings;

public abstract class BiomeHCIslandBase<TSettings extends BiomeSettings.Island> extends BiomeHCBase<TSettings> {
    private boolean generateLakesLava;
    private boolean generateLakes;

    public boolean generateLakes() {
        return generateLakes;
    }

    public boolean generateLakesLava() {
        return generateLakesLava;
    }

    BiomeHCIslandBase(BiomeProperties properties) {
        super(properties);
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
