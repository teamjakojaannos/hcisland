package jakojaannos.hcisland.world.biome;

public abstract class BiomeHCIslandBase extends BiomeHCBase {
    public boolean generateLakesLava;
    public boolean generateLakes;

    BiomeHCIslandBase(BiomeProperties properties) {
        super(properties);
        this.generateLakes = true;
        this.generateLakesLava = true;
    }
}
