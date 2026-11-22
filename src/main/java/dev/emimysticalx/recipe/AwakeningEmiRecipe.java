package dev.emimysticalx.recipe;

import com.blakebr0.mysticalagriculture.api.crafting.IAwakeningRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emimysticalx.MysticalEmiPlugin;
import net.minecraft.core.RegistryAccess;

/**
 * Awakening Altar: altar in the middle, 4 Awakening Pedestals on the straight sides
 * and 4 Essence Vessels on the diagonals (holding the listed amount of essence).
 *
 * MA stores the 9 inputs as: [altar, NW, N, NE, E, SE, S, SW, W]; odd indices are the
 * essence vessels whose contents come from {@link IAwakeningRecipe#getEssences()}.
 */
public class AwakeningEmiRecipe extends BasicEmiRecipe {
    private static final AltarLayout.Pos[] RING = {
            AltarLayout.Pos.NORTH_WEST, AltarLayout.Pos.NORTH, AltarLayout.Pos.NORTH_EAST, AltarLayout.Pos.EAST,
            AltarLayout.Pos.SOUTH_EAST, AltarLayout.Pos.SOUTH, AltarLayout.Pos.SOUTH_WEST, AltarLayout.Pos.WEST
    };

    private final EmiIngredient[] slots = new EmiIngredient[9];
    private final EmiStack output;

    public AwakeningEmiRecipe(IAwakeningRecipe recipe, RegistryAccess access) {
        super(MysticalEmiPlugin.AWAKENING, recipe.getId(), AltarLayout.WIDTH, AltarLayout.HEIGHT);

        var ingredients = recipe.getIngredients();
        var essences = recipe.getEssences();

        for (int i = 0; i < this.slots.length; i++) {
            if (i % 2 == 1) {
                int essenceIndex = i / 2;
                this.slots[i] = essenceIndex < essences.size() ? EmiStack.of(essences.get(essenceIndex)) : EmiStack.EMPTY;
            } else {
                this.slots[i] = i < ingredients.size() ? EmiIngredient.of(ingredients.get(i)) : EmiStack.EMPTY;
            }

            if (!this.slots[i].isEmpty()) {
                this.inputs.add(this.slots[i]);
            }
        }

        this.output = EmiStack.of(recipe.getResultItem(access));
        this.outputs.add(this.output);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        AltarLayout.addCenter(widgets, this.slots[0], "emimysticalx.hint.awakening_altar");

        for (int i = 1; i < this.slots.length; i++) {
            var hint = i % 2 == 1 ? "emimysticalx.hint.essence_vessel" : "emimysticalx.hint.awakening_pedestal";
            AltarLayout.addAround(widgets, RING[i - 1], this.slots[i], hint);
        }

        AltarLayout.addArrowAndOutput(widgets, this.output, this);
    }
}
