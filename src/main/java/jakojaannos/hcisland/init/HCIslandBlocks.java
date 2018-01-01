package jakojaannos.hcisland.init;

import jakojaannos.api.mod.BlocksBase;
import jakojaannos.api.mod.IBlock;
import jakojaannos.hcisland.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(ModInfo.MODID)
public class HCIslandBlocks extends BlocksBase {
    @ObjectHolder("test") public static final Block TEST = null;

    @Override
    public void initBlocks() {
        register("test", (BlockTest) new BlockTest().setCreativeTab(CreativeTabs.MISC));
    }

    private class BlockTest extends Block implements IBlock {
        BlockTest() {
            super(Material.ROCK);
        }
    }
}
