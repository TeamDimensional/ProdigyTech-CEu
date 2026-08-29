package lykrast.prodigytech.common.recipe;

import net.minecraft.item.ItemStack;

public interface ISingleInputRecipe {
    /**
     * Say if this recipe has an Ore Dictionary input.
     *
     * @return if this recipe has an Ore Dictionary input
     */
    boolean isOreRecipe();

    ItemStack getInput();

    String getOreInput();

    boolean isValidInput(ItemStack in);
}
