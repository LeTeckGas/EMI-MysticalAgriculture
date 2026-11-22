package dev.emimysticalx.recipe;

import com.blakebr0.mysticalagriculture.api.crafting.IEnchanterRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emimysticalx.EmiMysticalX;
import dev.emimysticalx.MysticalEmiPlugin;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Enchanter: one EMI recipe per enchantment level, so ingredient amounts and the
 * resulting book always match (level N needs N times the base amounts).
 */
public class EnchanterEmiRecipe extends BasicEmiRecipe {
    private final EmiIngredient first;
    private final EmiIngredient second;
    private final EmiStack book = EmiStack.of(Items.BOOK);
    private final EmiStack output;

    private EnchanterEmiRecipe(IEnchanterRecipe recipe, int level) {
        super(MysticalEmiPlugin.ENCHANTER, EmiMysticalX.syntheticId("enchanter", recipe.getId(), "level_" + level), 126, 18);

        var ingredients = recipe.getIngredients();
        var counts = recipe.getIngredientCounts();

        this.first = ingredient(ingredients.size() > 0 ? ingredients.get(0) : null, counts.size() > 0 ? counts.get(0) : 1, level);
        this.second = ingredient(ingredients.size() > 1 ? ingredients.get(1) : null, counts.size() > 1 ? counts.get(1) : 1, level);
        this.output = EmiStack.of(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(recipe.getEnchantment(), level)));

        if (!this.first.isEmpty()) this.inputs.add(this.first);
        if (!this.second.isEmpty()) this.inputs.add(this.second);
        this.inputs.add(this.book);
        this.outputs.add(this.output);
    }

    private static EmiIngredient ingredient(net.minecraft.world.item.crafting.Ingredient ingredient, int count, int level) {
        if (ingredient == null || ingredient.isEmpty()) return EmiStack.EMPTY;
        return EmiIngredient.of(ingredient, (long) count * level);
    }

    public static List<EnchanterEmiRecipe> createAllLevels(IEnchanterRecipe recipe) {
        var enchantment = recipe.getEnchantment();
        var result = new ArrayList<EnchanterEmiRecipe>();
        for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
            result.add(new EnchanterEmiRecipe(recipe, level));
        }
        return result;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.first, 0, 0);
        widgets.addSlot(this.second, 18, 0);
        widgets.addTexture(EmiTexture.PLUS, 40, 3);
        widgets.addSlot(this.book, 57, 0)
                .appendTooltip(AltarLayout.hint("emimysticalx.hint.enchanter_item"));
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 80, 1);
        widgets.addSlot(this.output, 108, 0).recipeContext(this);
    }
}
