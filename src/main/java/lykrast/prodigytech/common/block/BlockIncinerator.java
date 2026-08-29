package lykrast.prodigytech.common.block;

import lykrast.prodigytech.common.gui.ProdigyTechGuiHandler;
import lykrast.prodigytech.common.item.ItemBlockMachineHotAir;
import lykrast.prodigytech.common.tileentity.TileIncinerator;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockIncinerator extends BlockHotAirMachine<TileIncinerator> implements ICustomItemBlock {

    public BlockIncinerator(float hardness, float resistance, int harvestLevel) {
        super(hardness, resistance, harvestLevel, TileIncinerator.class);
    }

    @Override
    protected int getGuiID() {
        return ProdigyTechGuiHandler.INCINERATOR;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileIncinerator();
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockMachineHotAir(this, 80, 80);
    }
}
