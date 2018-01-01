package jakojaannos.hcisland.client.event.handler;

import jakojaannos.hcisland.config.HCIslandConfig;
import jakojaannos.hcisland.world.biome.BiomeHCBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEventHandler {
    @SubscribeEvent
    public static void onFogColors(EntityViewRenderEvent.FogColors event) {
        final Entity renderViewEntity = event.getEntity();
        final World world = renderViewEntity.world;
        final Minecraft mc = Minecraft.getMinecraft();

        int[] ranges = ForgeModContainer.blendRanges;
        int distance = 0;
        if (mc.gameSettings.fancyGraphics && ranges.length > 0) {
            distance = ranges[MathHelper.clamp(mc.gameSettings.renderDistanceChunks, 0, ranges.length - 1)];
        }

        int total = 0;
        int vanillaBiomes = 0;
        final BlockPos center = renderViewEntity.getPosition();
        for (int x = -distance; x <= distance; ++x) {
            for (int z = -distance; z <= distance; ++z) {
                BlockPos pos = center.add(x, 0, z);
                Biome biome = world.getBiome(pos);

                if (!(biome instanceof BiomeHCBase)) {
                    vanillaBiomes++;
                }

                total++;
            }
        }

        double t = vanillaBiomes / (float) total;
        float r = (float) MathHelper.clampedLerp(HCIslandConfig.client.fogColorR, event.getRed(), t);
        float g = (float) MathHelper.clampedLerp(HCIslandConfig.client.fogColorG, event.getGreen(), t);
        float b = (float) MathHelper.clampedLerp(HCIslandConfig.client.fogColorB, event.getBlue(), t);
        event.setRed(r);
        event.setGreen(g);
        event.setBlue(b);
    }
}
