package lykrast.prodigytech.common.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lykrast.prodigytech.common.item.ItemFoodPurified;
import lykrast.prodigytech.common.tileentity.TileFoodPurifier;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class FoodPurifierWrapper implements IRecipeWrapper {

    private final ItemStack food;
    private final int time;
    private final IDrawable arrow;

    public FoodPurifierWrapper(IGuiHelper guiHelper, ItemStack stack) {
        if (!(stack.getItem() instanceof ItemFood)) {
            throw new IllegalArgumentException("Food Purifier Wrapper requires ItemFood objects, but got " + stack);
        }
        food = stack;
        time = TileFoodPurifier.getProcessTime(stack);

        arrow = guiHelper.createAnimatedDrawable(
                ProdigyTechJEI.getDefaultProcessArrow(guiHelper), time, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, food);
        ingredients.setOutput(VanillaTypes.ITEM, ItemFoodPurified.make(food));
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX >= 24 && mouseY >= 5 && mouseX <= 24 + arrow.getWidth() && mouseY <= 5 + arrow.getHeight()) {
            List<String> list = new ArrayList<>();
            list.add(I18n.format("container.prodigytech.jei.base_time", time));
            return list;
        } else return Collections.emptyList();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        arrow.draw(minecraft, 24, 5);
    }
}
