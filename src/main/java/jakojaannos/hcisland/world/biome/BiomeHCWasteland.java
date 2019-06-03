package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.gen.BiomeSettings;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Biomes;

import java.util.function.Function;

public class BiomeHCWasteland extends BiomeHCIslandBase<BiomeSettings.Wasteland> {
    private boolean generateFire;

    public boolean generateFire() {
        return generateFire;
    }

    public BiomeHCWasteland() {
        this(getProperties(), settings -> settings.wasteland);
    }

    protected BiomeHCWasteland(BiomeProperties properties, Function<HCIslandChunkGeneratorSettings, BiomeSettings.Wasteland> biomeSettingsMapper) {
        super(properties, biomeSettingsMapper);

        // Disable neutral spawning
        this.spawnableCreatureList.clear();

        // Copy cave creatures from hell
        this.spawnableCaveCreatureList.clear();
        this.spawnableCaveCreatureList = Biomes.HELL.getSpawnableList(EnumCreatureType.AMBIENT);

        // Copy monsters from hell and add blazes
        this.spawnableMonsterList.clear();
        this.spawnableMonsterList = Biomes.HELL.getSpawnableList(EnumCreatureType.MONSTER);
        this.spawnableMonsterList.add(new SpawnListEntry(EntityBlaze.class, 10, 2, 2));
    }

    private static BiomeProperties getProperties() {
        BiomeProperties props = new BiomeProperties("HC Wasteland");
        props.setBaseHeight(0.4f);
        props.setHeightVariation(0.25f);
        props.setTemperature(HCIslandConfig.world.temperatureWasteland);

        return props;
    }

    @Override
    protected void applyBiomeSettings(BiomeSettings.Wasteland settings) {
        super.applyBiomeSettings(settings);
        this.generateFire = settings.generateFire;
    }
}
