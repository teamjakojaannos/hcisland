package jakojaannos.hcisland.world.biome;

import net.minecraft.util.math.MathHelper;

public abstract class BiomeHCBase extends BiomeLayeredBase {
    BiomeHCBase(BiomeProperties properties) {
        super(properties);

        this.decorator.generateFalls = false;
        this.decorator.treesPerChunk = -999;
        this.decorator.cactiPerChunk = 0;
        this.decorator.flowersPerChunk = 0;
        this.decorator.grassPerChunk = 0;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.bigMushroomsPerChunk = 0;
        this.decorator.mushroomsPerChunk = 0;
    }

    @Override
    public int getSkyColorByTemp(float currentTemperature) {
        return MathHelper.hsvToRGB(0.07f, 0.75f, 0.85f);
    }
}
