package lykrast.prodigytech.common.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lykrast.prodigytech.client.gui.GuiInfusion;
import lykrast.prodigytech.common.init.ModItems;
import lykrast.prodigytech.common.recipe.Infusion;
import lykrast.prodigytech.common.recipe.Infusion.InfusionCost;
import lykrast.prodigytech.common.recipe.SoldererManager.SoldererRecipe;
import lykrast.prodigytech.common.util.Config;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class SoldererWrapper implements IRecipeWrapper, ITooltipCallback<ItemStack> {
    private ItemStack pattern, additive, output;
    private final IDrawableAnimated arrow;
    private final IDrawable goldGauge;
    private final InfusionCost cost;
    private final int time;

    public SoldererWrapper(SoldererRecipe recipe, IGuiHelper guiHelper) {
        pattern = recipe.getPattern();
        additive = recipe.getAdditive();
        output = recipe.getOutput();
        cost = recipe.getInfusion();
        time = recipe.getTimeTicks();

        arrow = guiHelper.createAnimatedDrawable(
                ProdigyTechJEI.getDefaultProcessArrow(guiHelper),
                recipe.getTimeTicks(),
                IDrawableAnimated.StartDirection.LEFT,
                false);
        goldGauge = GuiInfusion.makeJEIDrawable(guiHelper, cost.getInfusionId(), cost.amount, Config.soldererCapacity);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>();
        inputs.add(Collections.singletonList(pattern));
        inputs.add(cost.getRepresentatives());

        inputs.add(Collections.singletonList(additive));
        inputs.add(Collections.singletonList(new ItemStack(ModItems.circuitPlate)));

        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        arrow.draw(minecraft, 60, 19);
        goldGauge.draw(minecraft, 30, 1 + (GuiInfusion.MAX_HEIGHT - goldGauge.getHeight()));
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX >= 30 && mouseX <= 34) {
            List<String> list = new ArrayList<>();
            list.add(cost.localize());
            return list;
        } else if (mouseX >= 60
                && mouseY >= 19
                && mouseX <= 60 + arrow.getWidth()
                && mouseY <= 19 + arrow.getHeight()) {
            List<String> list = new ArrayList<>();
            list.add(I18n.format("container.prodigytech.jei.base_time", time));
            return list;
        } else return Collections.emptyList();
    }

    @Override
    public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        if (slotIndex == 1) {
            int count = Infusion.INFUSIONS.get(cost.infusion).getOutputFor(ingredient);
            if (count > 1) {
                tooltip.add(I18n.format(GuiInfusion.PROVIDES, count));
            }
        }
    }
}
