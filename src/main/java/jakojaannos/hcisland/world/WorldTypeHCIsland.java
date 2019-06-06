package jakojaannos.hcisland.world;

import jakojaannos.hcisland.client.gui.GuiCustomizeHCWorld;
import jakojaannos.hcisland.init.ModRegistries;
import jakojaannos.hcisland.world.biome.BiomeProviderHCIsland;
import jakojaannos.hcisland.world.gen.HCIslandChunkGeneratorSettings;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import lombok.var;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class WorldTypeHCIsland extends WorldType {
    private static final String NAME = "hcisland";

    @Getter private HCIslandChunkGeneratorSettings settings;

    public WorldTypeHCIsland() {
        super(NAME);
        enableInfoNotice();
    }

    @Override
    public boolean isCustomizable() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onCustomizeButton(net.minecraft.client.Minecraft mc, net.minecraft.client.gui.GuiCreateWorld guiCreateWorld) {
        mc.displayGuiScreen(new GuiCustomizeHCWorld(guiCreateWorld, guiCreateWorld.chunkProviderSettingsJson));
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {
        this.settings = HCIslandChunkGeneratorSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();

        if (!world.isRemote) {
            ModRegistries.BIOME_ADAPTERS.getValuesCollection()
                                        .forEach(a -> a.applyBiomeSettings(settings.getSettingsFor(a.getBiome().getRegistryName())));
        }

        return new BiomeProviderHCIsland(world.getWorldInfo(), settings);
    }
}
