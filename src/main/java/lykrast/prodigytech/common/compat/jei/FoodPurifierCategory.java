package lykrast.prodigytech.common.compat.jei;

import java.util.ArrayList;
import java.util.List;
import lykrast.prodigytech.common.util.Config;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class FoodPurifierCategory extends ProdigyCategory<FoodPurifierWrapper> {

    public static final String UID = "ptfoodpurifier";

    public FoodPurifierCategory(IGuiHelper guiHelper) {
        super(guiHelper, guiHelper.createDrawable(ProdigyTechJEI.GUI, 0, 36, 82, 26), UID);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, FoodPurifierWrapper wrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 0, 4);
        guiItemStacks.init(1, false, 60, 4);

        guiItemStacks.set(ingredients);
    }

    public static void registerRecipes(IModRegistry registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        List<FoodPurifierWrapper> list = new ArrayList<>();
        for (Item i : ForgeRegistries.ITEMS) {
            if (i instanceof ItemFood) {
                NonNullList<ItemStack> stacks = NonNullList.create();
                i.getSubItems(CreativeTabs.SEARCH, stacks);
                for (ItemStack stack : stacks) {
                    if (!Config.itemMatches(stack, Config.foodPurifierBlacklist)) {
                        list.add(new FoodPurifierWrapper(guiHelper, stack));
                    }
                }
            }
        }

        registry.addRecipes(list, UID);
    }
}
