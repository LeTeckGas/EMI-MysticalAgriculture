package dev.emimysticalx.recipe;

import com.blakebr0.mysticalagriculture.api.crafting.IReprocessorRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.ISoulExtractionRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.ISouliumSpawnerRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emimysticalx.MysticalEmiPlugin;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simple "one input -> one output" machine recipe:
 * Seed Reprocessor, Soul Extractor and Soulium Spawner.
 */
public class MachineEmiRecipe extends BasicEmiRecipe {
    private static final DecimalFormat SOULS_FORMAT = new DecimalFormat("0.##");

    private final EmiIngredient input;
    private final EmiIngredient outputDisplay;
    private final List<Component> outputHints;

    private MachineEmiRecipe(EmiRecipeCategory category, ResourceLocation id, EmiIngredient input,
                             List<EmiStack> outputs, List<Component> outputHints) {
        super(category, id, 82, 26);
        this.input = input;
        this.outputDisplay = EmiIngredient.of(outputs);
        this.outputHints = outputHints;

        this.inputs.add(input);
        this.outputs.addAll(outputs);
    }

    public static MachineEmiRecipe reprocessor(IReprocessorRecipe recipe, RegistryAccess access) {
        return new MachineEmiRecipe(MysticalEmiPlugin.REPROCESSOR, recipe.getId(),
                EmiIngredient.of(recipe.getIngredients().get(0)),
                List.of(EmiStack.of(recipe.getResultItem(access))),
                List.of());
    }

    public static MachineEmiRecipe soulExtraction(ISoulExtractionRecipe recipe, RegistryAccess access) {
        var hints = new ArrayList<Component>();
        var type = recipe.getMobSoulType();
        if (type != null) {
            hints.add(Component.translatable("emimysticalx.hint.souls",
                    SOULS_FORMAT.format(recipe.getSouls()), type.getEntityDisplayName()));
        }

        return new MachineEmiRecipe(MysticalEmiPlugin.SOUL_EXTRACTOR, recipe.getId(),
                EmiIngredient.of(recipe.getIngredients().get(0)),
                List.of(EmiStack.of(recipe.getResultItem(access))),
                hints);
    }

    public static MachineEmiRecipe souliumSpawner(ISouliumSpawnerRecipe recipe) {
        var inputs = recipe.getIngredients().stream()
                .map(i -> EmiIngredient.of(i, recipe.getInputCount()))
                .toList();

        var entries = recipe.getEntityTypes().unwrap();
        int totalWeight = entries.stream().mapToInt(e -> e.getWeight().asInt()).sum();

        var outputs = new ArrayList<EmiStack>();
        var hints = new ArrayList<Component>();
        for (WeightedEntry.Wrapper<?> entry : entries) {
            var type = (net.minecraft.world.entity.EntityType<?>) entry.getData();
            var egg = ForgeSpawnEggItem.fromEntityType(type);
            if (egg != null) {
                outputs.add(EmiStack.of(new ItemStack(egg)));
            }
            if (entries.size() > 1 && totalWeight > 0) {
                hints.add(Component.translatable("emimysticalx.hint.spawn_chance",
                        type.getDescription(), Math.round(entry.getWeight().asInt() * 100.0 / totalWeight)));
            }
        }

        return new MachineEmiRecipe(MysticalEmiPlugin.SOULIUM_SPAWNER, recipe.getId(),
                EmiIngredient.of(inputs), outputs.stream().filter(Objects::nonNull).toList(), hints);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.input, 0, 4);
        widgets.addFillingArrow(24, 5, 5000);

        var slot = widgets.addSlot(this.outputDisplay, 56, 0).large(true).recipeContext(this);
        for (var hint : this.outputHints) {
            slot.appendTooltip(AltarLayout.hint(hint));
        }
    }
}
