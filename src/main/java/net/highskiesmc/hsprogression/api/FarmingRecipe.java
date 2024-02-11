package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hscore.configuration.Config;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class FarmingRecipe {
    private final static HSProgression main;
    private final static HSProgressionApi api;
    public final static NamespacedKey RECIPE_CROP_KEY;

    static {
        api = HSProgression.getApi();
        main = HSProgression.getPlugin(HSProgression.class);
        RECIPE_CROP_KEY = new NamespacedKey(main, "recipe-crop-type");
    }

    private FarmingRecipe() {

    }

    public static ItemStack getRecipe(Material crop, Config config) {
        List<Material> crops = api.getFarmingLevels().stream().map(FarmingLevel::getCrop).toList();

        if (crops.contains(crop)) {
            ItemStack recipe = new ItemStack(Material.PAPER);
            ItemMeta meta = recipe.getItemMeta();

            String current = TextUtils.toTitleCase(crop.name().replace("_", " "));
            meta.setDisplayName(TextUtils.translateColor(
                    config.get("farming.recipe.display-name", String.class, "&eFarming Recipe &a(&e{current}&a)")
                            .replace("{current}", current)
            ));

            List<String> lore = new ArrayList<String>(config.get("farming.recipe.lore", ArrayList.class,
                    new ArrayList<>()));
            lore.replaceAll(x -> TextUtils.translateColor(x.replace("{current}", current)));
            meta.setLore(lore);

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(RECIPE_CROP_KEY, PersistentDataType.STRING, crop.name());

            recipe.setItemMeta(meta);

            return recipe;
        } else {
            throw new IllegalArgumentException("Crop type " + crop.name() + " is not valid for a farming recipe");
        }
    }

    @Nullable
    public static Material getCrop(ItemStack recipe) {
        if (isFarmingRecipe(recipe)) {
            return Material.valueOf(recipe.getItemMeta().getPersistentDataContainer().get(RECIPE_CROP_KEY,
                    PersistentDataType.STRING));
        } else {
            return null;
        }
    }

    public static boolean isFarmingRecipe(ItemStack recipe) {
        ItemMeta meta = recipe.getItemMeta();

        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(RECIPE_CROP_KEY, PersistentDataType.STRING);
    }


}
