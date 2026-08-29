package lykrast.prodigytech.common.compat.jei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lykrast.prodigytech.client.gui.GuiExplosionFurnace;
import lykrast.prodigytech.common.recipe.ExplosionFurnaceManager;
import lykrast.prodigytech.common.recipe.ExplosionFurnaceManager.Explosive;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class ExplosionFurnaceExplosiveCategory extends ProdigyCategory<ExplosionFurnaceExplosiveWrapper> {
    public static final String UID = "ptexplosionfurnace_exp";

    public ExplosionFurnaceExplosiveCategory(IGuiHelper guiHelper) {
        super(
                guiHelper,
                guiHelper
                        .drawableBuilder(GuiExplosionFurnace.GUI, 16, 25, 18, 18)
                        .addPadding(0, 0, 0, 107)
                        .build(),
                UID);
    }

    @Override
    public void setRecipe(
            IRecipeLayout recipeLayout, ExplosionFurnaceExplosiveWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 0, 0);

        guiItemStacks.set(ingredients);
    }

    public static void registerRecipes(IModRegistry registry) {
        List<ExplosionFurnaceExplosiveWrapper> list = new ArrayList<>();

        // Sort by generated power so it's nice and easy to navigate (and because the map has no order)
        ExplosionFurnaceManager.EXPLOSIVES.getAllContent().stream()
                .sorted(Comparator.comparingInt(Explosive::getPower))
                .forEach(e -> list.add(new ExplosionFurnaceExplosiveWrapper(e)));
        ;

        registry.addRecipes(list, UID);
    }
}
