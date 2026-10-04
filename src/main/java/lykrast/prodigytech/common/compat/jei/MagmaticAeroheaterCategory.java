package lykrast.prodigytech.common.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lykrast.prodigytech.common.recipe.MagmaticAeroheaterManager;
import lykrast.prodigytech.common.recipe.MagmaticAeroheaterManager.MagmaticAeroheaterRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class MagmaticAeroheaterCategory extends ProdigyCategory<MagmaticAeroheaterWrapper> {

    public static final String UID = "ptmagmaticaeroheater";

    public MagmaticAeroheaterCategory(IGuiHelper guiHelper) {
        super(guiHelper, guiHelper.createBlankDrawable(132, 48), UID);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MagmaticAeroheaterWrapper recipe, IIngredients ingredients) {
        if (recipe.isPermanent()) {
            IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
            guiFluidStacks.init(0, true, 58, 4);
            guiFluidStacks.set(ingredients);
        } else {
            IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
            guiItemStacks.init(0, false, 83, 3);
            guiItemStacks.set(ingredients);

            IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
            guiFluidStacks.init(0, true, 24, 4);
            guiFluidStacks.set(ingredients);
        }
    }

    public static void registerRecipes(IModRegistry registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        List<MagmaticAeroheaterWrapper> list = new ArrayList<>();

        Collection<MagmaticAeroheaterRecipe> recipes = MagmaticAeroheaterManager.getAllRecipes();
        recipes.stream().forEachOrdered(recipe -> list.add(new MagmaticAeroheaterWrapper(recipe, guiHelper)));

        registry.addRecipes(list, UID);
    }
}
