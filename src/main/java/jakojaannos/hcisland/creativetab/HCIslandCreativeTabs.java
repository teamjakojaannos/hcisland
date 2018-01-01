package jakojaannos.hcisland.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HCIslandCreativeTabs extends CreativeTabs {

    public static final CreativeTabs DEFAULT = new HCIslandCreativeTabs("default");

    private HCIslandCreativeTabs(String label) {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.LAVA_BUCKET);
    }
}
