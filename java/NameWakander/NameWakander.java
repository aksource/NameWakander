package NameWakander;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Mod(modid = NameWakander.MOD_ID,
        name = NameWakander.MOD_NAME,
        version = NameWakander.MOD_VERSION,
        dependencies = NameWakander.MOD_DEPENDENCIES,
        useMetadata = true,
        acceptedMinecraftVersions = NameWakander.MOD_MC_VERSION)
public class NameWakander {
    public static final String MOD_ID = "namewakander";
    public static final String MOD_NAME = "NameWakander";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String MOD_DEPENDENCIES = "required-after:forge@[13.20.0,)";
    public static final String MOD_MC_VERSION = "[1.11,1.11.99]";

    private static boolean csvFormat;
    private static String directory;
    private static String charset;
    private static final String crlf = System.getProperty("line.separator");
    private static LinkedHashMap<String, Integer> blockanditemNames = new LinkedHashMap<String, Integer>();
    private static Multimap<String, String> oreBasedNames = HashMultimap.create();
    private static List<IdNameObj<Integer>> enchantmentIdList = Lists.newArrayList();
    private static List<IdNameObj<Integer>> potionIdList = Lists.newArrayList();
    private static List<IdNameObj<Integer>> biomeIdList = Lists.newArrayList();
    private static List<IdNameObj<Integer>> dimensionIdList = Lists.newArrayList();
    private static List<IdNameObj<String>> fluidIdList = Lists.newArrayList();
    private static List<String> entityNameList = Lists.newArrayList();
    private static List<String> achievementNameList = Lists.newArrayList();
    private static List<String> blockstatesList = Lists.newArrayList();
    private static List<String> villagerProfessionList = Lists.newArrayList();
    private static long start, end;
    private static String ext;
    private static Minecraft minecraft = Minecraft.getMinecraft();

    private static Logger logger = Logger.getLogger("NameWakander");

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        csvFormat = config.get(Configuration.CATEGORY_GENERAL, "csvFormat", false, "csv形式で出力する。").getBoolean(false);
        directory = config.get(Configuration.CATEGORY_GENERAL, "directory", "NameWakander", "ファイル出力フォルダ。.minecraft以下に作成される。").getString();
        charset = config.get(Configuration.CATEGORY_GENERAL, "charset", "UTF-8", "出力ファイルの文字コード。通常は変更する必要はない。").getString();
        config.save();
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        ext = csvFormat ? ".csv" : ".txt";
    }

    @SuppressWarnings("unused")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Executor executor = Executors.newCachedThreadPool();
        executor.execute(()-> {
            addItemsNameCreative();
            printMetaList("BlockAndItemWithMetaNames" + ext, blockanditemNames, true);
            printNameList("BlockStateList" + ext, blockstatesList, "LocalizedName, BlockState", true);
        });
        executor.execute(()-> {
            addVillagerProfessionName();
            printNameList("VillagerProfessionList" + ext, villagerProfessionList, "ProfessionName, CareerName, TradeInfo", true);
        });
        executor.execute(()-> {
            addOreNames();
            printMultiMapList("OreNames" + ext, oreBasedNames, true);
        });
        executor.execute(()-> {
            addEnchantmentList();
            Collections.sort(enchantmentIdList);
            printList("EnchantmentIDs" + ext, enchantmentIdList, true);
        });
        executor.execute(()-> {
            addPotionList();
            Collections.sort(potionIdList);
            printList("PotionIDs" + ext, potionIdList, true);
        });
        executor.execute(()-> {
            addBiomeList();
            Collections.sort(biomeIdList);
            printList("BiomeIDs" + ext, biomeIdList, true);
        });
        executor.execute(()-> {
            addDimensionProviderName();
            Collections.sort(dimensionIdList);
            printList("DimensionIDs" + ext, dimensionIdList, true);
        });
        executor.execute(()-> {
            addEntityNameFromEntityRegistry();
            printNameList("EntityNames" + ext, entityNameList, "UniqueName, UnlocalizedName, LocalizedName", true);
        });
        executor.execute(()-> {
            addAchievementNames();
            printNameList("AchievementNames" + ext, achievementNameList, "UnlocalizedName, LocalizedName, ParentAchievementLocalizedName", true);
        });
        executor.execute(()-> {
            addFluid();
            Collections.sort(fluidIdList);
            printList("FluidIDs" + ext, fluidIdList, true);
        });
    }

    /**
     * アイテムリスト生成
     */
    private static void addItemsNameCreative() {
        NonNullList<ItemStack> itemsList = NonNullList.create();
        for (Item item : Item.REGISTRY) {
            for (CreativeTabs tabs : CreativeTabs.CREATIVE_TAB_ARRAY) {
                item.getSubItems(item, tabs, itemsList);
            }
        }

        for (ItemStack itemStack : itemsList) {
            if (itemStack != null) {
                addItemStackName(itemStack);
            }
        }
    }

    private static boolean addItemStackName(ItemStack stack) {
        String stackUnique;
        String str;
        stackUnique = getUniqueStrings(stack.getItem());
        if (stack.getItem() instanceof ItemBlock) {
            addBlockState(stack);
        }
        try {
            String itemStackUnlocalized = stack.getUnlocalizedName() + ".name";
            String itemStackLocalized = stack.getDisplayName();
            str = String.format("%s, %s, %s"/* + crlf*/, stackUnique, itemStackUnlocalized, itemStackLocalized);
            blockanditemNames.put(str, stack.getItemDamage());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(String.format("[NameWakander]%s has an illegal name", stackUnique));
            return false;
        }
    }

    /**
     * 鉱石辞書名リスト生成
     */
    private static void addOreNames() {
        String[] oreNames = OreDictionary.getOreNames();
        for (String oreName : oreNames) {
            addItemStackNameFromOreName(oreName);
        }
    }

    private static boolean addItemStackNameFromOreName(String oreName) {
        List<ItemStack> oreList = OreDictionary.getOres(oreName);
        if (oreList == null || oreList.isEmpty()) return false;
        for (ItemStack itemStack : oreList) {
            oreBasedNames.put(oreName, getItemStackName(itemStack));
        }
        return true;
    }

    private static String getItemStackName(ItemStack stack) {
        String stackUnique;
        String str;
        stackUnique = getUniqueStrings(stack.getItem());
        try {
            String itemStackUnlocalized = stack.getUnlocalizedName() + ".name";
            String itemStackLocalized = stack.getDisplayName();
            str = String.format("%s, %s, %s, %d"/* + crlf*/, stackUnique, itemStackUnlocalized, itemStackLocalized, stack.getItemDamage());
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(String.format("[NameWakander]%s has an illegal name", stackUnique));
            return "";
        }
    }

    /**
     * エンチャントリスト生成
     */
    private static void addEnchantmentList() {
        for (Enchantment enchantment : Enchantment.REGISTRY) {
            if (enchantment != null) {
                addEnchantmentName(enchantment);
            }
        }
    }

    private static void addEnchantmentName(Enchantment enchantment) {
        String str = String.format("%s, %s", enchantment.getName(), I18n.translateToLocal(enchantment.getName()));
        enchantmentIdList.add(new IdNameObj<Integer>(Enchantment.getEnchantmentID(enchantment), str));
    }

    /**
     * ポーションリスト生成
     */
    private static void addPotionList() {
        for (Potion potion : Potion.REGISTRY) {
            if (potion != null) {
                addPotionName(potion);
            }
        }
    }

    private static void addPotionName(Potion potion) {
        String str = String.format("%s, %s", potion.getName(), I18n.translateToLocal(potion.getName()));
        potionIdList.add(new IdNameObj<Integer>(Potion.getIdFromPotion(potion), str));
    }

    /**
     * バイオームリスト生成
     */
    private static void addBiomeList() {
        for (Biome biome : Biome.REGISTRY) {
            if (biome != null) {
                addBiomeName(biome);
            }
        }
    }

    private static void addBiomeName(Biome biome) {
        String str = String.format("%s, %s", Optional.ofNullable(biome.getRegistryName()).orElse(Biomes.DEFAULT.getRegistryName()).toString(), I18n.translateToLocal(biome.getRegistryName().toString()));
        biomeIdList.add(new IdNameObj<Integer>(Biome.getIdForBiome(biome), str));
    }

    /**
     * Dimensionリスト生成
     */
    private static void addDimensionProviderName() {
        String str;
        WorldProvider provider;
        for (DimensionType type: DimensionType.values()) {
            for (int i : DimensionManager.getDimensions(type)) {
                provider = DimensionManager.createProviderFor(i);
                if (provider != null) {
                    str = String.format("%s, %s", provider.getDimensionType().getName(), I18n.translateToLocal(provider.getDimensionType().getName()));
                    dimensionIdList.add(new IdNameObj<Integer>(i, str));
                }
            }
        }
    }

    /**
     * Entityリスト生成
     */
    private static void addEntityNameFromEntityRegistry() {
        String str;
        String entityName;
        for (ResourceLocation resourceLocation : EntityList.getEntityNameList()) {
            entityName = "entity." + EntityList.getTranslationName(resourceLocation) + ".name";
            str = String.format("%s, %s, %s", resourceLocation.toString(), entityName, I18n.translateToLocal(entityName));
            entityNameList.add(str);
        }
    }

    /**
     * 村人の職業リスト生成
     */
    private static void addVillagerProfessionName() {
        for (Map.Entry<ResourceLocation, VillagerRegistry.VillagerProfession> entry : VillagerRegistry.instance().getRegistry().getEntries()) {
            ResourceLocation regName = entry.getKey();
            VillagerRegistry.VillagerProfession profession = entry.getValue();
            int indexCareer = 0;
            VillagerRegistry.VillagerCareer initCareer = profession.getCareer(0);
            do {
                VillagerRegistry.VillagerCareer career = profession.getCareer(indexCareer);
                String careerName = career.getName();
                int tradeLevel = 0;
                while (career.getTrades(tradeLevel) != null) {
                    List<EntityVillager.ITradeList> tradeLists = career.getTrades(tradeLevel++);
                    for (EntityVillager.ITradeList tradeList : tradeLists) {
                        if (tradeList instanceof EntityVillager.EmeraldForItems) {
                            EntityVillager.EmeraldForItems emeraldForItems = (EntityVillager.EmeraldForItems) tradeList;
                            String s = String.format("%s,%s,Emerald:%d,%s->%s",
                                    regName.toString(), careerName, emeraldForItems.price.getFirst(), emeraldForItems.price.getSecond(),
                                    emeraldForItems.buyingItem.getRegistryName());
                            villagerProfessionList.add(s);
                        } else if (tradeList instanceof EntityVillager.ItemAndEmeraldToItem) {
                            EntityVillager.ItemAndEmeraldToItem itemAndEmeraldToItem = (EntityVillager.ItemAndEmeraldToItem) tradeList;
                            String s = String.format("%s,%s,Item:%s,Emerald:%d,%d->Item:%s,Emerald:%d,%d",
                                    regName.toString(), careerName,
                                    itemAndEmeraldToItem.buyingItemStack.getItem().getRegistryName(),
                                    itemAndEmeraldToItem.buyingPriceInfo.getFirst(), itemAndEmeraldToItem.buyingPriceInfo.getSecond(),
                                    itemAndEmeraldToItem.sellingItemstack.getItem().getRegistryName(),
                                    itemAndEmeraldToItem.sellingPriceInfo.getFirst(), itemAndEmeraldToItem.sellingPriceInfo.getSecond());
                            villagerProfessionList.add(s);
                        } else if (tradeList instanceof EntityVillager.ListEnchantedBookForEmeralds) {
                            String s = String.format("%s,%s,Item:%s->Emerald:?",
                                    regName.toString(), careerName, Items.ENCHANTED_BOOK.getRegistryName());
                            villagerProfessionList.add(s);
                        } else if (tradeList instanceof EntityVillager.ListEnchantedItemForEmeralds) {
                            EntityVillager.ListEnchantedItemForEmeralds enchantedItemForEmeralds = (EntityVillager.ListEnchantedItemForEmeralds) tradeList;
                            String s = String.format("%s,%s,Item:%s->Emerald:%d,%d",
                                    regName.toString(), careerName,
                                    enchantedItemForEmeralds.enchantedItemStack.getItem().getRegistryName(),
                                    enchantedItemForEmeralds.priceInfo.getFirst(), enchantedItemForEmeralds.priceInfo.getSecond());
                            villagerProfessionList.add(s);
                        } else if (tradeList instanceof EntityVillager.ListItemForEmeralds) {
                            EntityVillager.ListItemForEmeralds itemForEmeralds = (EntityVillager.ListItemForEmeralds) tradeList;
                            String s = String.format("%s,%s,Emerald:%d,%d->Item:%s",
                                    regName.toString(), careerName,
                                    itemForEmeralds.priceInfo.getFirst(), itemForEmeralds.priceInfo.getSecond(),
                                    itemForEmeralds.itemToBuy.getItem().getRegistryName()
                                    );
                            villagerProfessionList.add(s);
                        }
                    }
                }
            } while (!initCareer.equals(profession.getCareer(++indexCareer)));
        }
    }

    /**
     * 実績リスト生成
     */
    private static void addAchievementNames() {
        String str;
        for (Achievement achievement : AchievementList.ACHIEVEMENTS) {
            if (achievement.parentAchievement != null) {
                str = String.format("%s, %s, %s", achievement.statId, I18n.translateToLocal(achievement.statId), I18n.translateToLocal(achievement.parentAchievement.statId));
            } else {
                str = String.format("%s, %s, %s", achievement.statId, I18n.translateToLocal(achievement.statId), "No Parent");
            }
            achievementNameList.add(str);
        }
    }

    /**
     * 液体リスト生成
     */
    private static void addFluid() {
        String str;
        Map<String, Fluid> fluids = FluidRegistry.getRegisteredFluids();
        for (String fluidName : fluids.keySet()) {
            Fluid fluid = fluids.get(fluidName);
            FluidStack fluidStack = FluidRegistry.getFluidStack(fluidName, Fluid.BUCKET_VOLUME);
            str = String.format("%s, %s", fluidName, fluid.getLocalizedName(fluidStack));
            fluidIdList.add(new IdNameObj<String>(fluidName, str));
        }
    }

    /**
     * BlockStateリスト生成
     * @param itemStack ItemBlockのItemStack
     */
    private static void addBlockState(ItemStack itemStack) {
        String str;
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block != Blocks.AIR) {
            try {
                IBlockState state = block.getStateFromMeta(itemStack.getItemDamage());
                str = String.format("%s, %s", itemStack.getDisplayName(), state.toString());
                if (!blockstatesList.contains(str)) {
                    blockstatesList.add(str);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void printList(String filename, List<? extends IdNameObj<? extends Comparable<?>>> list, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if (!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try (
                OutputStream stream = new FileOutputStream(file);
                BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset))) {
            src.write("UniqueId, UnlocalizedName, LocalizedName" + crlf);
            try {
                list.forEach(idNameObj -> {
                    try {
                        src.write(idNameObj.id + ", " + idNameObj.name + crlf);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (RuntimeException e) {
                Throwable th = e.getCause();
                if (th instanceof IOException) {
                    throw (IOException) th;
                }
            }
            end = System.currentTimeMillis();
            long time = end - start;
            if (flag) src.write("#output time is " + String.format("%d", time) + " ms.\n");
            src.flush();
            src.close();
            list.clear();
        } catch (IOException e) {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    private static void printMetaList(String filename, Map<String, Integer> map, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if (!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try (
                OutputStream stream = new FileOutputStream(file);
                BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset))) {
            src.write("UniqueName, UnlocalizedName, LocalizedName, Metadata" + crlf);
            for (String key : map.keySet()) {
                src.write(key);
                src.write(", " + Integer.toString(map.get(key)) + crlf);
            }
            end = System.currentTimeMillis();
            long time = end - start;
            if (flag) src.write("#output time is " + String.format("%d", time) + " ms.\n");
            src.flush();
            src.close();
            map.clear();
        } catch (IOException e) {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    private static void printMultiMapList(String filename, Multimap<String, String> map, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if (!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try (
                OutputStream stream = new FileOutputStream(file);
                BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset))) {
            src.write("OreName" + crlf);
            src.write("  UniqueName, UnlocalizedName, LocalizedName, Metadata" + crlf);
            List<String> sortedKeyList = new ArrayList<String>();
            sortedKeyList.addAll(map.keySet());
            Collections.sort(sortedKeyList);
            for (String key : sortedKeyList) {
                src.write(key + crlf);
                for (String names : map.get(key)) {
                    src.write("  " + names + crlf);
                }
            }
            end = System.currentTimeMillis();
            long time = end - start;
            if (flag) src.write("#output time is " + String.format("%d", time) + " ms.\n");
            src.flush();
            src.close();
            map.clear();
        } catch (IOException e) {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    private static void printNameList(String filename, List<String> list, String description, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if (!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try (
                OutputStream stream = new FileOutputStream(file);
                BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset))) {
            src.write(description + crlf);
            for (String name : list) {
                src.write(name + crlf);
            }
            end = System.currentTimeMillis();
            long time = end - start;
            if (flag) src.write("#output time is " + String.format("%d", time) + " ms.\n");
            src.flush();
            src.close();
            list.clear();
        } catch (IOException e) {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    private static String getUniqueStrings(Object obj) {
        String registryName = "none:dummy";
        if (obj instanceof ItemStack) {
            obj = ((ItemStack) obj).getItem();
        }
        if (obj instanceof Block) {
            registryName = Optional.ofNullable(((Block) obj).getRegistryName()).orElse(Blocks.AIR.getRegistryName()).toString();
        }
        if (obj instanceof Item) {
            registryName = Optional.ofNullable(((Item) obj).getRegistryName()).orElse(Items.AIR.getRegistryName()).toString();
        }
        return registryName;
    }
}