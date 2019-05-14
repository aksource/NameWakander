NameWakander
============

this lists up unique names of blocks, items, potions, enchantments, dimensions, biomes, villagers etc in Minecraft

# Output Information
## Items

- RegistryName
- Unlocalized Name
- Localized Name
- Metadata (only for MC ver 1.12.2 or under)

## Blocks

- RegistryName
- Unlocalized Name
- Localized Name
- BlockState (only for MC ver 1.12.2 or under)

## Enchantments

- UniqueId
- RegistryName
- ModId
- UnlocalizedName
- LocalizedName

## Entities

- RegistryName
- UnlocalizedName
- LocalizedName(if exist)

## Biomes

- UniqueId
- ModId
- RegistryName
- LocalizedName(if exist)

## Dimensions

- UniqueId
- UnlocalizedName
- LocalizedName(if exist)

## Potions

- UniqueId
- RegistryName
- ModId
- UnlocalizedName
- LocalizedName

## TagNames(old: OreNames)

- TagName
- Item Information
  - UniqueName
  - UnlocalizedName
  - LocalizedName
  - Metadata (only for MC ver 1.12.2 or under)
  
## VillagerTradeInformation

- ProfessionName
- CareerName
- TradeInfo

## Advancements

- RegistryName
- LocalizedName(if exist)
- Parent AdvancementLocalizedName(if exist)

## Fluids (only for MC ver 1.12.2 or under)

- UniqueId
- UnlocalizedName
- LocalizedName(if exist)

# How to use

1. Put this mod's jar in mods directory.
1. Start Minecraft.
1. Select any world.
1. Then, text files is created in NameWakander dir of minecraft dir.

# Configuration

- charset:text file encode
- directory:output directory
- csvFormat:output csv or not