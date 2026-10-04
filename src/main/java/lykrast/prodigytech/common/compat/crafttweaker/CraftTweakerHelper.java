package lykrast.prodigytech.common.compat.crafttweaker;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import lykrast.prodigytech.common.recipe.SimpleRecipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CraftTweakerHelper {
    public static ItemStack toItemStack(IItemStack stack) {
        if (stack == null) return ItemStack.EMPTY;
        else return (ItemStack) stack.getInternal();
    }

    public static FluidStack toFluidStack(ILiquidStack stack) {
        if (stack == null) return null;
        else return (FluidStack) stack.getInternal();
    }

    public static IBlockState toBlockState(crafttweaker.api.block.IBlockState stack) {
        if (stack == null) return null;
        else return (IBlockState) stack.getInternal();
    }

    public static SimpleRecipe simpleRecipe(IItemStack in, IItemStack out, int time) {
        return new SimpleRecipe(toItemStack(in), toItemStack(out), time);
    }

    public static SimpleRecipe simpleRecipe(IOreDictEntry in, IItemStack out, int time) {
        return new SimpleRecipe(in.getName(), toItemStack(out), time);
    }
}
