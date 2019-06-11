package jakojaannos.hcisland.world.biome;

import jakojaannos.hcisland.world.gen.BiomeSettings;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Biomes;

public abstract class BiomeHCWastelandBase extends BiomeHCIslandBase<BiomeSettings.Wasteland> {
    private boolean generateFire;

    public boolean generateFire() {
        return generateFire;
    }

    protected BiomeHCWastelandBase(BiomeProperties properties) {
        super(properties);

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

    @Override
    protected void applyBiomeSettings(BiomeSettings.Wasteland settings) {
        super.applyBiomeSettings(settings);
        this.generateFire = settings.generateFire;
    }
}
