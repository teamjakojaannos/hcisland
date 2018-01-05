package jakojaannos.hcisland.world.storage;

import com.google.common.base.Preconditions;
import jakojaannos.hcisland.ModInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class HCIslandWorldSavedData extends WorldSavedData {
    private static final String DATA_NAME = ModInfo.MODID + "_Data";

    public boolean starterChestGenerated;


    public static HCIslandWorldSavedData getInstance(World world) {
        final MapStorage mapStorage = world.getMapStorage();
        Preconditions.checkNotNull(mapStorage);

        HCIslandWorldSavedData instance = (HCIslandWorldSavedData) mapStorage.getOrLoadData(HCIslandWorldSavedData.class, DATA_NAME);
        if (instance == null) {
            mapStorage.setData(DATA_NAME, instance = new HCIslandWorldSavedData());
        }

        return instance;
    }

    public HCIslandWorldSavedData() {
        super(DATA_NAME);
    }

    public HCIslandWorldSavedData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        starterChestGenerated = nbt.getBoolean("starterChestGenerated");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("starterChestGenerated", starterChestGenerated);
        return compound;
    }
}
