package NameWakander;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

@Mod(modid="NameWakander", name="NameWakander", version="@VERSION@",dependencies="required-after:FML", canBeDeactivated = true, useMetadata = true)
public class NameWakander
{
    @Mod.Instance("NameWakander")
    public static NameWakander instance;

    public static boolean csvFormat;
    public static String directory;
    public static String charset;
    public static int checkDuplicateLimit;

    private static final String crlf = System.getProperty("line.separator");
    public static LinkedHashMap<String, Integer> blockanditemNames = new LinkedHashMap<String, Integer>();
    public static Multimap<String, String> oreBasedNames = HashMultimap.create();
    public static Map<Integer, String> enchantmentIdMap = Maps.newHashMap();
    public static Map<Integer, String> potionIdMap = Maps.newHashMap();
    public static Map<Integer, String> biomeMap = Maps.newHashMap();
    public static Map<Integer, String> dimensionMap = Maps.newHashMap();
    public static List<String> entityNameList = Lists.newArrayList();
    public static List<String> achievementNameList = Lists.newArrayList();
    //1.8になってから
//    public static Map<Integer, String> villagerProfessionMap = Maps.newHashMap();
    private static long start,end;
    public static String ext;
    private static Minecraft minecraft = Minecraft.getMinecraft();

    public static Logger logger = Logger.getLogger("NameWakander");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        csvFormat = config.get(Configuration.CATEGORY_GENERAL, "csvFormat", false, "csv形式で出力する。").getBoolean(false);
        directory = config.get(Configuration.CATEGORY_GENERAL, "directory", "NameWakander", "ファイル出力フォルダ。.minecraft以下に作成される。").getString();
        charset = config.get(Configuration.CATEGORY_GENERAL, "charset", "UTF-8", "出力ファイルの文字コード。通常は変更する必要はない。").getString();
        checkDuplicateLimit = config.get(Configuration.CATEGORY_GENERAL, "checkDuplicateLimit", 1000, "メタデータの翻訳前文字列の重複がこれ以上になったら、処理を次のアイテムに飛ばす。").getInt();
        config.save();
    }
    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        ext = csvFormat ? ".csv" : ".txt";
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Thread thread = new Thread(){
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
                printMultiMapList("OreNames" + ext, oreBasedNames, true);
                printMetaList("BlockAndItemWithMetaNames" + ext, blockanditemNames, true);
                printIdMap("EnchantmentIDs" + ext, enchantmentIdMap, 0, Enchantment.enchantmentsList.length, true);
                printIdMap("PotionIDs" + ext, potionIdMap, 0, Potion.potionTypes.length, true);
                printIdMap("BiomeIDs" + ext, biomeMap, 0, BiomeGenBase.getBiomeGenArray().length, true);
                printIdMapIgnored("DimensionIDs" + ext, dimensionMap, true);
                printNameList("EntityNames" + ext, entityNameList, "UniqueName, UnlocalizedName, LocalizedName", true);
                printNameList("AchievementNames" + ext, achievementNameList, "UnlocalizedName, LocalizedName, ParentAchievementLocalizedName", true);
            }
        };
        thread.start();
    }

    private static boolean addItemStackName(ItemStack stack) {
        String stackUnique;
        String str;
        stackUnique = getUniqueStrings(stack.getItem());
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

    public static void addItemsNameCreative() {
        List<ItemStack> itemsList = new ArrayList<ItemStack>();
        for (CreativeTabs tabs : CreativeTabs.creativeTabArray) {
            try {
                tabs.displayAllReleventItems(itemsList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (ItemStack itemStack : itemsList) {
            if (itemStack != null) {
                addItemStackName(itemStack);
            }
        }
    }

    public static void addOreNames() {
        String[] oreNames = OreDictionary.getOreNames();
        for (String oreName : oreNames) {
            addItemStackNameFromOreName(oreName);
        }
    }

    public static void addEnchantmentList() {
        for (Enchantment enchantment : Enchantment.enchantmentsList) {
            if (enchantment != null) {
                addEnchantmentName(enchantment);
            }
        }
    }

    private static void addEnchantmentName(Enchantment enchantment) {
        String str = String.format("%s, %s", enchantment.getName(), StatCollector.translateToLocal(enchantment.getName()));
        enchantmentIdMap.put(enchantment.effectId, str);
    }

    public static void addPotionList() {
        for (Potion potion : Potion.potionTypes) {
            if (potion != null) {
                addPotionName(potion);
            }
        }
    }

    private static void addPotionName(Potion potion) {
        String str = String.format("%s, %s", potion.getName(), StatCollector.translateToLocal(potion.getName()));
        potionIdMap.put(potion.id, str);
    }

    public static void addBiomeList() {
        for (BiomeGenBase biomeGenBase : BiomeGenBase.getBiomeGenArray()) {
            if (biomeGenBase != null) {
                addBiomeName(biomeGenBase);
            }
        }
    }

    private static void addBiomeName(BiomeGenBase biomeGenBase) {
        String str = String.format("%s, %s", biomeGenBase.biomeName, StatCollector.translateToLocal(biomeGenBase.biomeName));
        biomeMap.put(biomeGenBase.biomeID, str);
    }

    public static void addDimensionProviderName() {
        String str;
        WorldProvider provider;
        for (int i : DimensionManager.getStaticDimensionIDs()) {
            provider = DimensionManager.createProviderFor(i);
            if (provider != null) {
                str = String.format("%s, %s", provider.getDimensionName(), StatCollector.translateToLocal(provider.getDimensionName()));
                dimensionMap.put(i, str);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void addEntityNameFromEntityList() {
        String str;
        String entityName;
        Class entityClass;
        for (int id : (Set<Integer>)EntityList.IDtoClassMapping.keySet()) {
            entityClass = EntityList.getClassFromID(id);
            entityName = "entity." + EntityList.classToStringMapping.get(entityClass) + ".name";
            str = String.format("%s, %s, %s", Integer.toString(id), entityName, StatCollector.translateToLocal(entityName));
            entityNameList.add(str);
        }
    }

    @SuppressWarnings("unchecked")
    private static void addEntityNameFromEntityRegistry() {
        String str;
        String entityName;
        for (String entityModName : (Set<String>)EntityList.stringToClassMapping.keySet()) {
            entityName = "entity." + entityModName + ".name";
            str = String.format("%s, %s, %s", entityModName, entityName, StatCollector.translateToLocal(entityName));
            entityNameList.add(str);
        }
    }
//1.8になってから
//    public static void addVillagerProfessionName() {
//        for (int id : VillagerRegistry.getRegisteredVillagers()) {
//
//        }
//    }

    @SuppressWarnings("unchecked")
    public static void addAchievementNames() {
        String str;
        for (Achievement achievement : (List<Achievement>)AchievementList.achievementList) {
            if (achievement.parentAchievement != null) {
                str = String.format("%s, %s, %s", achievement.statId, StatCollector.translateToLocal(achievement.statId), StatCollector.translateToLocal(achievement.parentAchievement.statId));
            } else {
                str = String.format("%s, %s, %s", achievement.statId, StatCollector.translateToLocal(achievement.statId), "No Parent");
            }
            achievementNameList.add(str);
        }
    }

    private void printList(String filename, Collection col, boolean flag)
    {
        File dir = new File(minecraft.mcDataDir, directory);
        if(!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try
        {
            OutputStream stream = new FileOutputStream(file);
            BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset));
            for (Object key : col) {
                src.write((String)key);
            }
            end = System.currentTimeMillis();
            long time = end - start;
            if(flag) src.write("#output time is "+String.format("%d", time)+" ms.\n");
            src.flush();
            src.close();
            col.clear();
        }
        catch (IOException e)
        {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    public static void printMetaList(String filename, Map<String, Integer> map, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if(!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try
        {
            OutputStream stream = new FileOutputStream(file);
            BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset));
            src.write("UniqueName, UnlocalizedName, LocalizedName, Metadata" + crlf);
            for (String key : map.keySet()) {
                src.write(key);
                src.write(", " + Integer.toString(map.get(key)) + crlf);
            }
            end = System.currentTimeMillis();
            long time = end - start;
            if(flag) src.write("#output time is "+String.format("%d", time)+" ms.\n");
            src.flush();
            src.close();
            map.clear();
        }
        catch (IOException e)
        {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    public static void printMultiMapList(String filename, Multimap<String, String> map, boolean flag)
    {
        File dir = new File(minecraft.mcDataDir, directory);
        if(!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try
        {
            OutputStream stream = new FileOutputStream(file);
            BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset));
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
            if(flag) src.write("#output time is "+String.format("%d", time)+" ms.\n");
            src.flush();
            src.close();
            map.clear();
        }
        catch (IOException e)
        {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    public static void printIdMap(String filename, Map<Integer, String> map, int minRange, int maxRange, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if(!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try
        {
            OutputStream stream = new FileOutputStream(file);
            BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset));
            src.write("ID, UnlocalizedName, LocalizedName" + crlf);
            for (int i = minRange; i <= maxRange; i++) {
                if (!map.containsKey(i)) {
                    src.write(i + ", " + crlf);
                } else {
                    src.write(i + ", " + map.get(i) + crlf);
                }
            }
            end = System.currentTimeMillis();
            long time = end - start;
            if(flag) src.write("#output time is "+String.format("%d", time)+" ms.\n");
            src.flush();
            src.close();
            map.clear();
        }
        catch (IOException e)
        {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    public static void printIdMapIgnored(String filename, Map<Integer, String> map, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if(!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try
        {
            OutputStream stream = new FileOutputStream(file);
            BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset));
            src.write("ID, UnlocalizedName, LocalizedName" + crlf);
            for (int i : map.keySet()) {
                src.write(i + ", " + map.get(i) + crlf);
            }
            end = System.currentTimeMillis();
            long time = end - start;
            if(flag) src.write("#output time is "+String.format("%d", time)+" ms.\n");
            src.flush();
            src.close();
            map.clear();
        }
        catch (IOException e)
        {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    public static void printNameList(String filename, List<String> list, String description, boolean flag) {
        File dir = new File(minecraft.mcDataDir, directory);
        if(!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try
        {
            OutputStream stream = new FileOutputStream(file);
            BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset));
            src.write(description + crlf);
            for (String name : list) {
                src.write(name + crlf);
            }
            end = System.currentTimeMillis();
            long time = end - start;
            if(flag) src.write("#output time is "+String.format("%d", time)+" ms.\n");
            src.flush();
            src.close();
            list.clear();
        }
        catch (IOException e)
        {
            FMLCommonHandler.instance().raiseException(e, String.format("NameWakander: %s に書き込みできません。", file.getName()), true);
        }
    }

    public static String getUniqueStrings(Object obj) {
        GameRegistry.UniqueIdentifier uId = null;
        if (obj instanceof ItemStack) {
            obj = ((ItemStack)obj).getItem();
        }
        if (obj instanceof Block) {
            uId = GameRegistry.findUniqueIdentifierFor((Block) obj);
        }
        if (obj instanceof Item){
            uId = GameRegistry.findUniqueIdentifierFor((Item) obj);
        }
        return Optional.fromNullable(uId).or(new GameRegistry.UniqueIdentifier("none:dummy")).toString();
    }
}