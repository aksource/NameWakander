package NameWakander;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

@Mod(modid = NameWakander.MOD_ID,
        name = NameWakander.MOD_NAME,
        version = NameWakander.MOD_VERSION,
        dependencies = NameWakander.MOD_DEPENDENCIES,
        useMetadata = true,
        acceptedMinecraftVersions = NameWakander.MOD_MC_VERSION)
public class NameWakander {
    public static final String MOD_ID = "NameWakander";
    public static final String MOD_NAME = "NameWakander";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String MOD_DEPENDENCIES = "required-after:Forge@[12.17.0,)";
    public static final String MOD_MC_VERSION = "[1.9,1.10.99]";

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
    @Deprecated
    private static Map<Integer, String> enchantmentIdMap = Maps.newHashMap();
    @Deprecated
    private static Map<Integer, String> potionIdMap = Maps.newHashMap();
    @Deprecated
    private static Map<Integer, String> biomeMap = Maps.newHashMap();
    @Deprecated
    private static Map<Integer, String> dimensionMap = Maps.newHashMap();
    @Deprecated
    private static Map<String, String> fluidMap = Maps.newHashMap();
    private static List<String> entityNameList = Lists.newArrayList();
    private static List<String> achievementNameList = Lists.newArrayList();
    private static List<String> blockstatesList = Lists.newArrayList();
    //1.8になってから
//    public static Map<Integer, String> villagerProfessionMap = Maps.newHashMap();
    private static long start, end;
    private static String ext;
    private static Minecraft minecraft = Minecraft.getMinecraft();

    private static Logger logger = Logger.getLogger("NameWakander");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        csvFormat = config.get(Configuration.CATEGORY_GENERAL, "csvFormat", false, "csv形式で出力する。").getBoolean(false);
        directory = config.get(Configuration.CATEGORY_GENERAL, "directory", "NameWakander", "ファイル出力フォルダ。.minecraft以下に作成される。").getString();
        charset = config.get(Configuration.CATEGORY_GENERAL, "charset", "UTF-8", "出力ファイルの文字コード。通常は変更する必要はない。").getString();
        config.save();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        ext = csvFormat ? ".csv" : ".txt";
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                addItemsNameCreative();
                addOreNames();
                addEnchantmentList();
                addPotionList();
                addBiomeList();
                addDimensionProviderName();
                addEntityNameFromEntityRegistry();
                addAchievementNames();
                addFluid();
                Collections.sort(enchantmentIdList);
                Collections.sort(potionIdList);
                Collections.sort(biomeIdList);
                Collections.sort(dimensionIdList);
                Collections.sort(fluidIdList);
                printMultiMapList("OreNames" + ext, oreBasedNames, true);
                printMetaList("BlockAndItemWithMetaNames" + ext, blockanditemNames, true);
//                printIdMapIgnored("EnchantmentIDs" + ext, enchantmentIdMap, true);
                printList("EnchantmentIDs" + ext, enchantmentIdList, true);
//                printIdMapIgnored("PotionIDs" + ext, potionIdMap, true);
                printList("PotionIDs" + ext, potionIdList, true);
//                printIdMapIgnored("BiomeIDs" + ext, biomeMap, true);
                printList("BiomeIDs" + ext, biomeIdList, true);
//                printIdMapIgnored("DimensionIDs" + ext, dimensionMap, true);
                printList("DimensionIDs" + ext, dimensionIdList, true);
//                printStringMap("FluidIDs" + ext, fluidMap, "ID, RegisteredName, LocalizedName", true);
                printList("FluidIDs" + ext, fluidIdList, true);
                printNameList("EntityNames" + ext, entityNameList, "UniqueName, UnlocalizedName, LocalizedName", true);
                printNameList("AchievementNames" + ext, achievementNameList, "UnlocalizedName, LocalizedName, ParentAchievementLocalizedName", true);
                printNameList("BlockStateList" + ext, blockstatesList, "LocalizedName, BlockState", true);
            }
        };
        thread.start();
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

    private static boolean addItemStackNameFromOreName(String oreName) {
        List<ItemStack> oreList = OreDictionary.getOres(oreName);
        if (oreList == null || oreList.isEmpty()) return false;
        for (ItemStack itemStack : oreList) {
            oreBasedNames.put(oreName, getItemStackName(itemStack));
        }
        return true;
    }

    private static void addItemsNameCreative() {
        List<ItemStack> itemsList = new ArrayList<>();
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

    private static void addOreNames() {
        String[] oreNames = OreDictionary.getOreNames();
        for (String oreName : oreNames) {
            addItemStackNameFromOreName(oreName);
        }
    }

    private static void addEnchantmentList() {
        for (Enchantment enchantment : Enchantment.REGISTRY) {
            if (enchantment != null) {
                addEnchantmentName(enchantment);
            }
        }
    }

    private static void addEnchantmentName(Enchantment enchantment) {
        String str = String.format("%s, %s", enchantment.getName(), I18n.translateToLocal(enchantment.getName()));
        enchantmentIdMap.put(Enchantment.getEnchantmentID(enchantment), str);
        enchantmentIdList.add(new IdNameObj<Integer>(Enchantment.getEnchantmentID(enchantment), str));
    }

    private static void addPotionList() {
        for (Potion potion : Potion.REGISTRY) {
            if (potion != null) {
                addPotionName(potion);
            }
        }
    }

    private static void addPotionName(Potion potion) {
        String str = String.format("%s, %s", potion.getName(), I18n.translateToLocal(potion.getName()));
        potionIdMap.put(Potion.getIdFromPotion(potion), str);
        potionIdList.add(new IdNameObj<Integer>(Potion.getIdFromPotion(potion), str));
    }

    private static void addBiomeList() {
        for (Biome biome : Biome.REGISTRY) {
            if (biome != null) {
                addBiomeName(biome);
            }
        }
    }

    private static void addBiomeName(Biome biome) {
        String str = String.format("%s, %s", biome.getRegistryName().toString(), I18n.translateToLocal(biome.getRegistryName().toString()));
        biomeMap.put(Biome.getIdForBiome(biome), str);
        biomeIdList.add(new IdNameObj<Integer>(Biome.getIdForBiome(biome), str));
    }

    private static void addDimensionProviderName() {
        String str;
        WorldProvider provider;
        for (int i : DimensionManager.getStaticDimensionIDs()) {
            provider = DimensionManager.createProviderFor(i);
            if (provider != null) {
                str = String.format("%s, %s", provider.getDimensionType().getName(), I18n.translateToLocal(provider.getDimensionType().getName()));
                dimensionMap.put(i, str);
                dimensionIdList.add(new IdNameObj<Integer>(i, str));
            }
        }
    }

/*    private static void addEntityNameFromEntityList() {
        String str;
        String entityName;
        Class entityClass;
        for (int id : (Set<Integer>) EntityList.ID_TO_CLASS.keySet()) {
            entityClass = EntityList.getClassFromID(id);
            entityName = "entity." + EntityList.CLASS_TO_NAME.get(entityClass) + ".name";
            str = String.format("%s, %s, %s", Integer.toString(id), entityName, I18n.translateToLocal(entityName));
            entityNameList.add(str);
        }
    }*/

    private static void addEntityNameFromEntityRegistry() {
        String str;
        String entityName;
        for (String entityModName : EntityList.NAME_TO_CLASS.keySet()) {
            entityName = "entity." + entityModName + ".name";
            str = String.format("%s, %s, %s", entityModName, entityName, I18n.translateToLocal(entityName));
            entityNameList.add(str);
        }
    }

    //1.8から追加できそうだけど、どうも未実装な部分が多い。
    public static void addVillagerProfessionName() {
        //NO-OP
    }

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

    private static void addFluid() {
        String str;
        Map<String, Fluid> fluids = FluidRegistry.getRegisteredFluids();
        for (String fluidName : fluids.keySet()) {
            Fluid fluid = fluids.get(fluidName);
            FluidStack fluidStack = FluidRegistry.getFluidStack(fluidName, FluidContainerRegistry.BUCKET_VOLUME);
            str = String.format("%s, %s", fluidName, fluid.getLocalizedName(fluidStack));
            fluidMap.put(fluidName, str);
            fluidIdList.add(new IdNameObj<String>(fluidName, str));
        }
    }

    private static void addBlockState(ItemStack itemStack) {
        String str;
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block != null) {
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

    @Deprecated
    private static void printStringMap(String filename, Map<String, String> map, String context, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if (!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try (
                OutputStream stream = new FileOutputStream(file);
                BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset))) {
            src.write(context + crlf);
            for (String string : map.values()) {
                src.write(string + crlf);
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

    @Deprecated
    private static void printIdMap(String filename, Map<Integer, String> map, String context, int minRange, int maxRange, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if (!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try (
                OutputStream stream = new FileOutputStream(file);
                BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset))) {
            src.write(context + crlf);
            for (int i = minRange; i <= maxRange; i++) {
                if (!map.containsKey(i)) {
                    src.write(i + ", " + crlf);
                } else {
                    src.write(i + ", " + map.get(i) + crlf);
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

    @Deprecated
    private static void printIdMapIgnored(String filename, Map<Integer, String> map, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if (!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try (
                OutputStream stream = new FileOutputStream(file);
                BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset))) {
            src.write("ID, UnlocalizedName, LocalizedName" + crlf);
            for (int i : map.keySet()) {
                src.write(i + ", " + map.get(i) + crlf);
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
            registryName = ((Block) obj).getRegistryName().toString();
        }
        if (obj instanceof Item) {
            registryName = ((Item) obj).getRegistryName().toString();
        }
        return registryName;
    }
}