package dev.emimysticalx.recipe;

import com.blakebr0.mysticalagriculture.api.crop.Crop;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emimysticalx.EmiMysticalX;
import dev.emimysticalx.MysticalEmiPlugin;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * Crux: some seeds only grow when a specific "crux" block is placed directly
 * below the farmland they are planted on.
 */
public class CruxEmiRecipe extends BasicEmiRecipe {
    private final EmiStack seed;
    private final EmiIngredient farmland;
    private final EmiStack crux;
    private final EmiStack essence;

    private CruxEmiRecipe(Crop crop, EmiIngredient farmland) {
        super(MysticalEmiPlugin.CRUX, EmiMysticalX.syntheticId("crux", crop.getId(), null), 82, 54);
        this.seed = EmiStack.of(crop.getSeedsItem());
        this.farmland = farmland;
        this.crux = EmiStack.of(crop.getCruxBlock());
        this.essence = EmiStack.of(crop.getEssenceItem());

        this.inputs.add(this.seed);
        this.catalysts.add(this.farmland);
        this.inputs.add(this.crux);
        this.outputs.add(this.essence);
    }

    public static List<CruxEmiRecipe> createAll(List<Crop> crops) {
        var farmlandBlocks = new LinkedHashSet<Block>();
        for (var crop : crops) {
            if (crop.getTier() != null && crop.getTier().getFarmland() != null) {
                farmlandBlocks.add(crop.getTier().getFarmland());
            }
        }
        var farmland = EmiIngredient.of(farmlandBlocks.stream().filter(Objects::nonNull).map(EmiStack::of).toList());

        var result = new ArrayList<CruxEmiRecipe>();
        for (var crop : crops) {
            if (crop.getCruxBlock() != null && crop.isEnabled()) {
                result.add(new CruxEmiRecipe(crop, farmland));
            }
        }
        return result;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.seed, 0, 0).appendTooltip(AltarLayout.hint("emimysticalx.hint.crux_seed"));
        widgets.addSlot(this.farmland, 0, 18).appendTooltip(AltarLayout.hint("emimysticalx.hint.crux_farmland"));
        widgets.addSlot(this.crux, 0, 36).appendTooltip(AltarLayout.hint("emimysticalx.hint.crux_block"));
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 24, 19);
        widgets.addSlot(this.essence, 56, 14).large(true).recipeContext(this);
    }
}
