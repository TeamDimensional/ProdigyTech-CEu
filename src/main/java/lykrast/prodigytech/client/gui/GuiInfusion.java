package lykrast.prodigytech.client.gui;

import lykrast.prodigytech.core.ProdigyTech;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;

public class GuiInfusion {
    
	public static final ResourceLocation GUI = ProdigyTech.resource("textures/gui/infusions.png");
    public static final int MIN_HEIGHT = 6;
    public static final int MAX_HEIGHT = 52;
    public static final int WIDTH = 4;
	public static final String PROVIDES = "container.prodigytech.infusion.provides";

    public static void render(Gui gui, int infusionId, int amount, int capacity, int x, int y) {
        if (amount > 0) {
		    Minecraft.getMinecraft().getTextureManager().bindTexture(GUI);
            int height = MIN_HEIGHT + Math.round(Math.min(1.0f, ((float) amount) / capacity) * (MAX_HEIGHT - MIN_HEIGHT));
	        gui.drawTexturedModalRect(x, y + (MAX_HEIGHT - height), (infusionId - 1) * WIDTH, MAX_HEIGHT - height, 4, height);
        }
    }

    @Optional.Method(modid = "jei")
    public static IDrawable makeJEIDrawable(IGuiHelper helper, int infusionId, int amount, int capacity) {
        int height = MIN_HEIGHT + Math.round(Math.min(1.0f, ((float) amount) / capacity) * (MAX_HEIGHT - MIN_HEIGHT));
        return helper.createDrawable(GUI, (infusionId - 1) * WIDTH, MAX_HEIGHT - height, 4, height);
    }

}
