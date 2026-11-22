package dev.emimysticalx;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;

/**
 * Mod entry point. All the actual work happens in {@link MysticalEmiPlugin},
 * which EMI discovers through the {@code @EmiEntrypoint} annotation.
 */
@Mod(EmiMysticalX.MOD_ID)
public final class EmiMysticalX {
    public static final String MOD_ID = "emimysticalx";

    public EmiMysticalX() { }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /** Builds a synthetic EMI recipe id (EMI convention: path starts with '/'). */
    public static ResourceLocation syntheticId(String kind, ResourceLocation source, String suffix) {
        var path = "/" + kind + "/" + source.getNamespace() + "/" + source.getPath();
        if (suffix != null && !suffix.isEmpty()) {
            path += "/" + suffix;
        }
        return new ResourceLocation(MOD_ID, path);
    }
}
