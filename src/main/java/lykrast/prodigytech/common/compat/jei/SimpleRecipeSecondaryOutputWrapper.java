package lykrast.prodigytech.common.compat.jei;

import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lykrast.prodigytech.common.recipe.SimpleRecipeSecondaryOutput;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SimpleRecipeSecondaryOutputWrapper implements IRecipeWrapper {
    private List<List<ItemStack>> in;
    private List<ItemStack> out;
    private final IDrawableAnimated arrow;
    private int chance;
    private int time;

    public SimpleRecipeSecondaryOutputWrapper(SimpleRecipeSecondaryOutput recipe, IGuiHelper guiHelper) {
        List<ItemStack> inputs = new ArrayList<>();

        if (recipe.isOreRecipe()) {
            List<ItemStack> items = OreDictionary.getOres(recipe.getOreInput(), false);
            inputs.addAll(items);
        } else inputs.add(recipe.getInput());

        in = Collections.singletonList(inputs);
        out = ImmutableList.of(recipe.getOutput(), recipe.getSecondaryOutput());
        chance = recipe.hasSecondaryOutput() ? (int) (recipe.getSecondaryChance() * 100) : 100;
        time = recipe.getTimeTicks();

        arrow = guiHelper.createAnimatedDrawable(
                ProdigyTechJEI.getDefaultProcessArrow(guiHelper),
                recipe.getTimeTicks(),
                IDrawableAnimated.StartDirection.LEFT,
                false);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, in);
        ingredients.setOutputs(VanillaTypes.ITEM, out);
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

        if (chance < 100) {
            String chanceString;
            if (chance < 1) chanceString = "< 1%";
            else chanceString = String.format("%d%%", chance);

            int width = minecraft.fontRenderer.getStringWidth(chanceString);
            minecraft.fontRenderer.drawString(chanceString, 95 - width / 2, 28, Color.gray.getRGB());
        }
    }
}
