package dev.emimysticalx.recipe;

import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Shared layout for the Infusion and Awakening altars: a large center slot (the altar),
 * up to eight slots arranged around it (pedestals / essence vessels, laid out like the
 * blocks are placed in the world, seen from above) and the result on the right.
 */
final class AltarLayout {
    static final int WIDTH = 150;
    static final int HEIGHT = 82;

    private static final int CENTER_X = 28;
    private static final int CENTER_Y = 28;

    private AltarLayout() { }

    /** Positions around the altar (top-left of an 18x18 slot). Matches MA's JEI layout. */
    enum Pos {
        NORTH_WEST(6, 6),
        NORTH(32, 0),
        NORTH_EAST(58, 6),
        EAST(64, 32),
        SOUTH_EAST(58, 58),
        SOUTH(32, 64),
        SOUTH_WEST(6, 58),
        WEST(0, 32);

        final int x;
        final int y;

        Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /** Same arrangement MA's own JEI plugin uses for 1..8 pedestals. */
    static Pos[] pedestalPositions(int count) {
        return switch (count) {
            case 1 -> new Pos[] { Pos.NORTH };
            case 2 -> new Pos[] { Pos.NORTH, Pos.SOUTH };
            case 3 -> new Pos[] { Pos.WEST, Pos.NORTH, Pos.EAST };
            case 4 -> new Pos[] { Pos.NORTH, Pos.EAST, Pos.SOUTH, Pos.WEST };
            case 5 -> new Pos[] { Pos.NORTH_WEST, Pos.NORTH, Pos.NORTH_EAST, Pos.SOUTH_EAST, Pos.SOUTH_WEST };
            case 6 -> new Pos[] { Pos.NORTH_WEST, Pos.NORTH, Pos.NORTH_EAST, Pos.SOUTH_EAST, Pos.SOUTH, Pos.SOUTH_WEST };
            case 7 -> new Pos[] { Pos.WEST, Pos.NORTH_WEST, Pos.NORTH, Pos.NORTH_EAST, Pos.EAST, Pos.SOUTH_EAST, Pos.SOUTH_WEST };
            default -> new Pos[] { Pos.NORTH_WEST, Pos.NORTH, Pos.NORTH_EAST, Pos.EAST, Pos.SOUTH_EAST, Pos.SOUTH, Pos.SOUTH_WEST, Pos.WEST };
        };
    }

    static SlotWidget addCenter(WidgetHolder widgets, EmiIngredient ingredient, String hintKey) {
        return widgets.addSlot(ingredient, CENTER_X, CENTER_Y).large(true).appendTooltip(hint(hintKey));
    }

    static SlotWidget addAround(WidgetHolder widgets, Pos pos, EmiIngredient ingredient, String hintKey) {
        return widgets.addSlot(ingredient, pos.x, pos.y).appendTooltip(hint(hintKey));
    }

    static void addArrowAndOutput(WidgetHolder widgets, EmiStack output, dev.emi.emi.api.recipe.EmiRecipe recipe) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 89, 32);
        widgets.addSlot(output, 120, 28).large(true).recipeContext(recipe);
    }

    /** Small caption under the arrow, e.g. "4 Pedestals", so the required setup is visible at a glance. */
    static void addCaption(WidgetHolder widgets, Component text) {
        widgets.addText(text, 84, 70, 0x404040, false);
    }

    static Component hint(String key) {
        return Component.translatable(key).withStyle(ChatFormatting.GRAY);
    }

    static Component hint(Component text) {
        return text.copy().withStyle(ChatFormatting.GRAY);
    }
}
