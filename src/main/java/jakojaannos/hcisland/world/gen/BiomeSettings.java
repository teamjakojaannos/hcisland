package jakojaannos.hcisland.world.gen;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BiomeSettings {
    public float baseHeightOverride = 0.2f;
    public float heightVariationOverride = 0.1f;

    public BiomeSettings(float baseHeightOverride, float heightVariationOverride) {
        this.baseHeightOverride = baseHeightOverride;
        this.heightVariationOverride = heightVariationOverride;
    }


}