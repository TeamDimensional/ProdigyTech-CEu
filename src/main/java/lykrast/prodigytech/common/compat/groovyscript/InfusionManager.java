package lykrast.prodigytech.common.compat.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyLog.Msg;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import lykrast.prodigytech.common.recipe.Infusion;
import lykrast.prodigytech.common.recipe.Infusion.InfusionItem;
import lykrast.prodigytech.common.recipe.Infusion.InfusionNameDescriptor;
import net.minecraft.item.ItemStack;

@RegistryDescription
public class InfusionManager extends NamedRegistry implements IScriptReloadable {

    private final List<Infusion> removeOnReload = new ArrayList<>();
    private final List<Infusion> revertOnReload = new ArrayList<>();

    @Override
    public void afterScriptLoad() {}

    @Override
    public void onReload() {
        for (Infusion x : removeOnReload) {
            Infusion.unregisterInfusion(x);
        }
        for (Infusion x : revertOnReload) {
            Infusion other = Infusion.INFUSIONS.get(x.name);
            if (other != null) {
                replace(other, x);
            }
        }
        removeOnReload.clear();
        revertOnReload.clear();
    }

    @Override
    public List<String> getAliases() {
        return ImmutableList.of("infusion");
    }

    @Override
    public String getName() {
        return this.getAliases().get(0).toLowerCase(Locale.ENGLISH);
    }

    private void persist(Infusion infusion) {
        Infusion copy = new Infusion(infusion.name, infusion.id, infusion.unlocalizedName);
        copy.machines.addAll(infusion.machines);
        copy.items.addAll(infusion.items);
        copy.naming.addAll(infusion.naming);
        revertOnReload.add(copy);
    }

    private void register(Infusion infusion) {
        Infusion.registerInfusion(infusion);
        removeOnReload.add(infusion);
    }

    private void replace(Infusion target, Infusion source) {
        target.clear();
        target.setBaseName(source.unlocalizedName);
        target.machines.addAll(source.machines);
        target.items.addAll(source.items);
        target.naming.addAll(source.naming);
    }

    @RecipeBuilderDescription(
            example = {
                @Example(".name('gold').machine('solderer', 'atomic_reshaper').input(item('minecraft:iron_ingot'))"),
                @Example(
                        ".name('diamond').id(3).unlocalizedName('pack_infusion.diamond').unlocalizedName('pack_infusion.diamond_block', 90).machine('solderer').input(item('minecraft:obsidian')).input(item('minecraft:diamond'), 10)"),
            })
    public RecipeBuilder builder() {
        return new RecipeBuilder();
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<Infusion> {

        @Property(comp = @Comp(eq = 1, unique = "groovyscript.wiki.prodigytech_ceu.infusion_nonbasic"))
        private String name;

        @Property(comp = @Comp(eq = 1, unique = "groovyscript.wiki.prodigytech_ceu.infusion_nonbasic"))
        private String unlocalizedName;

        @Property(comp = @Comp(gte = 3, unique = "groovyscript.wiki.prodigytech_ceu.infusion_nonbasic"))
        private int id = 0;

        @Property(comp = @Comp(gte = 1))
        private final List<InfusionItem> items = new ArrayList<>();

        @Property(comp = @Comp(gte = 1, unique = "groovyscript.wiki.prodigytech_ceu.machine_types"))
        private final List<String> machines = new ArrayList<>();

        @Property(comp = @Comp(gte = 0))
        private final List<InfusionNameDescriptor> naming = new ArrayList<>();

        @Override
        public @Nullable Infusion register() {
            Infusion infusion = Infusion.INFUSIONS.get(name);
            if (infusion != null && id == 0) {
                id = infusion.id;
            }
            if (infusion != null && unlocalizedName == null) {
                unlocalizedName = infusion.unlocalizedName;
            }
            if (!validate()) return null;
            Infusion result = new Infusion(name, id, unlocalizedName);
            result.naming.addAll(naming);
            result.items.addAll(items);
            result.machines.addAll(machines);
            if (infusion != null) {
                persist(infusion);
                replace(infusion, result);
                return infusion;
            }

            InfusionManager.this.register(result);
            return result;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder name(String name) {
            this.name = name;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder unlocalizedName(String unlocalizedName) {
            this.unlocalizedName = unlocalizedName;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder id(int id) {
            this.id = id;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"items"})
        public RecipeBuilder input(IIngredient item, int count) {
            if (item instanceof OreDictIngredient) {
                OreDictIngredient stack = (OreDictIngredient) item;
                items.add(new InfusionItem(stack.getOreDict(), count));
            } else {
                for (ItemStack stack : item.getMatchingStacks()) {
                    items.add(new InfusionItem(stack, count));
                }
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IIngredient item) {
            return input(item, 1);
        }

        @RecipeBuilderMethodDescription(field = {"machines"})
        public RecipeBuilder machine(String... machines) {
            for (String s : machines) this.machines.add(s);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder naming(String name, int multiplier) {
            naming.add(new InfusionNameDescriptor(name, multiplier));
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error configuring Prodigy Tech CEu Infusion";
        }

        @Override
        public void validate(Msg msg) {
            msg.add(id <= 0, "id must be positive");
            msg.add(name == null || name.isEmpty(), "name must not be empty");
            msg.add(unlocalizedName == null || unlocalizedName.isEmpty(), "unlocalized name must not be empty");
            msg.add(machines.isEmpty(), "machine list must not be empty");
            msg.add(items.isEmpty(), "item list must not be empty");
        }
    }
}
