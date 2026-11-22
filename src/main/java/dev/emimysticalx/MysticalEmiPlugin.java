package dev.emimysticalx;

import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.init.ModBlocks;
import com.blakebr0.mysticalagriculture.init.ModItems;
import com.blakebr0.mysticalagriculture.init.ModRecipeTypes;
import com.mojang.logging.LogUtils;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emimysticalx.recipe.AwakeningEmiRecipe;
import dev.emimysticalx.recipe.CruxEmiRecipe;
import dev.emimysticalx.recipe.EnchanterEmiRecipe;
import dev.emimysticalx.recipe.InfusionEmiRecipe;
import dev.emimysticalx.recipe.MachineEmiRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Function;

@EmiEntrypoint
public final class MysticalEmiPlugin implements EmiPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static MysticalCategory INFUSION;
    public static MysticalCategory AWAKENING;
    public static MysticalCategory ENCHANTER;
    public static MysticalCategory REPROCESSOR;
    public static MysticalCategory SOUL_EXTRACTOR;
    public static MysticalCategory SOULIUM_SPAWNER;
    public static MysticalCategory CRUX;

    @Override
    public void register(EmiRegistry registry) {
        registerCategories(registry);
        registerWorkstations(registry);
        registerRecipes(registry);
        registerInfo(registry);

        // Soul jars with different mobs are different items for lookup purposes
        ModItems.SOUL_JAR.ifPresent(jar -> registry.setDefaultComparison(EmiStack.of(jar), Comparison.compareNbt()));
    }

    private static void registerCategories(EmiRegistry registry) {
        INFUSION = new MysticalCategory("infusion", EmiStack.of(ModBlocks.INFUSION_ALTAR.get()));
        AWAKENING = new MysticalCategory("awakening", EmiStack.of(ModBlocks.AWAKENING_ALTAR.get()));
        ENCHANTER = new MysticalCategory("enchanter", EmiStack.of(ModBlocks.ENCHANTER.get()));
        REPROCESSOR = new MysticalCategory("reprocessor", EmiStack.of(ModBlocks.REPROCESSOR.get()));
        SOUL_EXTRACTOR = new MysticalCategory("soul_extractor", EmiStack.of(ModBlocks.SOUL_EXTRACTOR.get()));
        SOULIUM_SPAWNER = new MysticalCategory("soulium_spawner", EmiStack.of(ModBlocks.SOULIUM_SPAWNER.get()));
        CRUX = new MysticalCategory("crux", EmiStack.of(ModItems.PROSPERITY_SEED_BASE.get()));

        for (var category : List.of(INFUSION, AWAKENING, ENCHANTER, REPROCESSOR, SOUL_EXTRACTOR, SOULIUM_SPAWNER, CRUX)) {
            registry.addCategory(category);
        }
    }

    private static void registerWorkstations(EmiRegistry registry) {
        addWorkstation(registry, INFUSION, ModBlocks.INFUSION_ALTAR.get());
        addWorkstation(registry, INFUSION, ModBlocks.INFUSION_PEDESTAL.get());
        addWorkstation(registry, AWAKENING, ModBlocks.AWAKENING_ALTAR.get());
        addWorkstation(registry, AWAKENING, ModBlocks.AWAKENING_PEDESTAL.get());
        addWorkstation(registry, AWAKENING, ModBlocks.ESSENCE_VESSEL.get());
        addWorkstation(registry, ENCHANTER, ModBlocks.ENCHANTER.get());
        addWorkstation(registry, REPROCESSOR, ModBlocks.REPROCESSOR.get());
        addWorkstation(registry, SOUL_EXTRACTOR, ModBlocks.SOUL_EXTRACTOR.get());
        addWorkstation(registry, SOULIUM_SPAWNER, ModBlocks.SOULIUM_SPAWNER.get());
    }

    private static void addWorkstation(EmiRegistry registry, MysticalCategory category, Block block) {
        registry.addWorkstation(category, EmiStack.of(block));
    }

    private static void registerRecipes(EmiRegistry registry) {
        var manager = registry.getRecipeManager();
        var access = registryAccess();

        addAll(registry, manager, ModRecipeTypes.INFUSION.get(), r -> List.of(new InfusionEmiRecipe(r, access)));
        addAll(registry, manager, ModRecipeTypes.AWAKENING.get(), r -> List.of(new AwakeningEmiRecipe(r, access)));
        addAll(registry, manager, ModRecipeTypes.ENCHANTER.get(), EnchanterEmiRecipe::createAllLevels);
        addAll(registry, manager, ModRecipeTypes.REPROCESSOR.get(), r -> List.of(MachineEmiRecipe.reprocessor(r, access)));
        addAll(registry, manager, ModRecipeTypes.SOUL_EXTRACTION.get(), r -> List.of(MachineEmiRecipe.soulExtraction(r, access)));
        addAll(registry, manager, ModRecipeTypes.SOULIUM_SPAWNER.get(), r -> List.of(MachineEmiRecipe.souliumSpawner(r)));

        try {
            for (var recipe : CruxEmiRecipe.createAll(MysticalAgricultureAPI.getCropRegistry().getCrops())) {
                registry.addRecipe(recipe);
            }
        } catch (Throwable t) {
            LOGGER.error("[EMI for Mystical Agriculture] Failed to create crux recipes", t);
        }
    }

    /** Converts every recipe of a type; a single broken recipe (e.g. from a datapack) never takes the rest down. */
    private static <T extends Recipe<Container>> void addAll(EmiRegistry registry, RecipeManager manager, RecipeType<T> type,
                                                             Function<T, List<? extends EmiRecipe>> factory) {
        for (var recipe : manager.getAllRecipesFor(type)) {
            try {
                for (var emiRecipe : factory.apply(recipe)) {
                    registry.addRecipe(emiRecipe);
                }
            } catch (Throwable t) {
                LOGGER.warn("[EMI for Mystical Agriculture] Skipping recipe {}: {}", recipe.getId(), t.toString());
            }
        }
    }

    private static void registerInfo(EmiRegistry registry) {
        addInfo(registry, new ResourceLocation("mysticalagriculture", "cognizant_dust"), "jei.desc.mysticalagriculture.cognizant_dust");
        addInfo(registry, new ResourceLocation("mysticalagradditions", "withering_soul"), "jei.desc.mysticalagradditions.withering_soul");
        addInfo(registry, new ResourceLocation("mysticalagradditions", "dragon_scale"), "jei.desc.mysticalagradditions.dragon_scale");
    }

    private static void addInfo(EmiRegistry registry, ResourceLocation itemId, String translationKey) {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item == null || !itemId.equals(ForgeRegistries.ITEMS.getKey(item))) return; // mod not installed

        registry.addRecipe(new EmiInfoRecipe(
                List.<EmiIngredient>of(EmiStack.of(item)),
                List.of(Component.translatable(translationKey)),
                EmiMysticalX.syntheticId("info", itemId, null)
        ));
    }

    static RegistryAccess registryAccess() {
        var level = Minecraft.getInstance().level;
        return level != null ? level.registryAccess() : RegistryAccess.EMPTY;
    }
}
