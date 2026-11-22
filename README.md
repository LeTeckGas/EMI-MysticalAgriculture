# EMI for Mystical Agriculture

Adds **native EMI support** for [Mystical Agriculture](https://www.curseforge.com/minecraft/mc-mods/mystical-agriculture).
Mystical Agriculture only ships a JEI plugin, so if you play with EMI *without* JEI, none of its
special recipes show up. This mod fixes that.

## Features

All Mystical Agriculture recipe types are shown in EMI:

- **Infusion Altar** – the altar item in the center, pedestal items arranged around it,
  plus a caption showing how many pedestals you need
- **Awakening Altar** – altar, 4 Awakening Pedestals and 4 Essence Vessels with the exact essence amounts
- **Enchanter** – one recipe per enchantment level, so ingredient amounts always match the result
- **Seed Reprocessor**, **Soul Extractor** (incl. souls gained) and **Soulium Spawner** (incl. spawn chances)
- **Cruxes** – which block has to go under the farmland for a seed to grow
- **Info pages** for Cognizant Dust (and Withering Soul / Dragon Scale if Mystical Agradditions is installed)

Hover over a slot to see where an item belongs (altar, pedestal, essence vessel, below the farmland, …).

Recipes are read from the game itself, so seeds and recipes from **Mystical Agradditions**,
**Mystical Customization**, KubeJS/CraftTweaker scripts and datapacks are included automatically.

## Requirements

| Mod | Version |
|---|---|
| Minecraft Forge | 1.20.1 (47.x) |
| [EMI](https://www.curseforge.com/minecraft/mc-mods/emi) | 1.1.x |
| [Mystical Agriculture](https://www.curseforge.com/minecraft/mc-mods/mystical-agriculture) | 7.0.x |
| [Cucumber Library](https://www.curseforge.com/minecraft/mc-mods/cucumber) | (required by Mystical Agriculture) |

**Client-side only.** You don't need to install it on the server.

## FAQ

**I also have JEI installed. Will I see recipes twice?**
No. This mod uses the same category IDs as Mystical Agriculture's JEI plugin, so EMI shows only
the native version.

**Does it work with Mystical Agradditions / Mystical Customization?**
Yes. All their recipes go through Mystical Agriculture's recipe types and show up automatically.

## Credits

- Mystical Agriculture & Cucumber by **BlakeBr0** (MIT)
- EMI by **Emi** (MIT)
- This mod is not affiliated with or endorsed by either of them.

## License

MIT
