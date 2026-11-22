package dev.emimysticalx.recipe;

import com.blakebr0.mysticalagriculture.api.crafting.IInfusionRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emimysticalx.MysticalEmiPlugin;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Infusion Altar: the first ingredient goes on the altar, all others on the
 * surrounding Infusion Pedestals (their order does not matter in-game).
 */
public class InfusionEmiRecipe extends BasicEmiRecipe {
    private final EmiIngredient altarInput;
    private final List<EmiIngredient> pedestalInputs;
    private final EmiStack output;

    public InfusionEmiRecipe(IInfusionRecipe recipe, RegistryAccess access) {
        super(MysticalEmiPlugin.INFUSION, recipe.getId(), AltarLayout.WIDTH, AltarLayout.HEIGHT);

        var ingredients = recipe.getIngredients();
        this.altarInput = EmiIngredient.of(ingredients.get(0));
        this.pedestalInputs = new ArrayList<>();
        for (int i = 1; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                this.pedestalInputs.add(EmiIngredient.of(ingredient));
            }
        }
        this.output = EmiStack.of(recipe.getResultItem(access));

        this.inputs.add(this.altarInput);
        this.inputs.addAll(this.pedestalInputs);
        this.outputs.add(this.output);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        AltarLayout.addCenter(widgets, this.altarInput, "emimysticalx.hint.infusion_altar");

        var positions = AltarLayout.pedestalPositions(this.pedestalInputs.size());
        for (int i = 0; i < this.pedestalInputs.size() && i < positions.length; i++) {
            AltarLayout.addAround(widgets, positions[i], this.pedestalInputs.get(i), "emimysticalx.hint.infusion_pedestal");
        }

        AltarLayout.addArrowAndOutput(widgets, this.output, this);
        AltarLayout.addCaption(widgets, Component.translatable("emimysticalx.caption.pedestals", this.pedestalInputs.size()));
    }
}
