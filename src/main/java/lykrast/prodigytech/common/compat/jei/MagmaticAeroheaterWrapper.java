package lykrast.prodigytech.common.compat.jei;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lykrast.prodigytech.common.recipe.MagmaticAeroheaterManager.MagmaticAeroheaterRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MagmaticAeroheaterWrapper implements IRecipeWrapper {

    private final MagmaticAeroheaterRecipe recipe;
    private final ItemStack output;
    private final int time, temperature;
    private final IDrawable arrow;
    private final IDrawable transforming, permanent;

    private static final String AVG_TIME = "container.prodigytech.jei.average_time";
    private static final String TEMPERATURE = "container.prodigytech.jei.aeroheater_temperature";
    private static final String RESIDUAL_TIME = "container.prodigytech.jei.residual_time";
    private static final String PERMANENT = "container.prodigytech.jei.permanent_heat_source";

    public MagmaticAeroheaterWrapper(MagmaticAeroheaterRecipe recipe, IGuiHelper guiHelper) {
        this.recipe = recipe;
        this.temperature = recipe.heatingProfile.getMaxTemperature();
        transforming = guiHelper.createDrawable(ProdigyTechJEI.GUI, 0, 36, 82, 26);
        permanent = guiHelper.createDrawable(ProdigyTechJEI.GUI, 0, 40, 18, 18);
        if (!recipe.isPermanent()) {
            time = (int) (20.0F / recipe.consumptionChance);
            arrow = guiHelper.createAnimatedDrawable(
                    ProdigyTechJEI.getDefaultProcessArrow(guiHelper),
                    time,
                    IDrawableAnimated.StartDirection.LEFT,
                    false);
            Item outputBlockItem = Item.getItemFromBlock(recipe.outputBlock.getBlock());
            int meta = outputBlockItem.getHasSubtypes()
                    ? recipe.outputBlock.getBlock().getMetaFromState(recipe.outputBlock)
                    : 0;
            output = new ItemStack(outputBlockItem, 1, meta);
        } else {
            time = 0;
            arrow = null;
            output = null;
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.FLUID, new FluidStack(recipe.inputFluid, 1000));
        if (recipe.outputBlock != null) {
            ingredients.setOutput(VanillaTypes.ITEM, output);
        }
    }

    public boolean isPermanent() {
        return recipe.outputBlock == null;
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (!isPermanent()
                && mouseX >= 48
                && mouseY >= 4
                && mouseX <= 48 + arrow.getWidth()
                && mouseY <= 4 + arrow.getHeight()) {
            List<String> list = new ArrayList<>();
            list.add(I18n.format(AVG_TIME, time));
            return list;
        } else return Collections.emptyList();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (isPermanent()) {
            permanent.draw(minecraft, 57, 3);
        } else {
            transforming.draw(minecraft, 23, -1);
        }

        if (arrow != null && time > 0) {
            arrow.draw(minecraft, 48, 4);
        }

        int baseX = 4, baseY = 28, deltaY = 11;
        minecraft.fontRenderer.drawString(I18n.format(TEMPERATURE, temperature), baseX, baseY, Color.gray.getRGB());
        if (time > 0) {
            minecraft.fontRenderer.drawString(
                    I18n.format(RESIDUAL_TIME, recipe.residualHeatDuration),
                    baseX,
                    baseY + deltaY,
                    Color.gray.getRGB());
        } else {
            minecraft.fontRenderer.drawString(I18n.format(PERMANENT), baseX, baseY + deltaY, Color.gray.getRGB());
        }
    }
}
