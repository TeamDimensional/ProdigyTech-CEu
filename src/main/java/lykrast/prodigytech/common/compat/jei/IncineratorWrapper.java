package lykrast.prodigytech.common.compat.jei;

import java.awt.Color;
import lykrast.prodigytech.common.init.ModItems;
import lykrast.prodigytech.common.util.Config;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class IncineratorWrapper implements IRecipeWrapper {
    private ItemStack output;
    private final String chance;

    public IncineratorWrapper() {
        output = new ItemStack(ModItems.ash);

        int outChance = (int) (Config.incineratorChance * 100);

        if (outChance < 1) chance = "< 1%";
        else if (outChance >= 100) chance = "";
        else chance = String.format("%d%%", outChance);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int width = minecraft.fontRenderer.getStringWidth(chance);
        minecraft.fontRenderer.drawString(chance, 69 - width / 2, 28, Color.gray.getRGB());
    }
}
