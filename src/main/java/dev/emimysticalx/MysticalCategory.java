package dev.emimysticalx;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A recipe category that uses the same id as Mystical Agriculture's own JEI category
 * (e.g. {@code mysticalagriculture:infusion}). That way, if JEI is installed as well,
 * EMI's JEI bridge skips MA's JEI category instead of showing it twice.
 * The title re-uses MA's existing (already translated) JEI category names.
 */
public class MysticalCategory extends EmiRecipeCategory {
    private final String translationKey;

    public MysticalCategory(String path, EmiStack icon) {
        super(new ResourceLocation("mysticalagriculture", path), icon);
        this.translationKey = "jei.category.mysticalagriculture." + path;
    }

    @Override
    public Component getName() {
        return Component.translatable(this.translationKey);
    }
}
