package jakojaannos.hcisland.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigClient {
    @Comment("Should the \"Hardcore Island\" -worldtype be the default option")
    public boolean makeWorldTypeDefault = true;


    @Comment("Fog color R-component in modded biomes")
    public float fogColorR = 0.65f;

    @Comment("Fog color G-component in modded biomes")
    public float fogColorG = 0.25f;

    @Comment("Fog color B-component in modded biomes")
    public float fogColorB = 0.0f;
}
