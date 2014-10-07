package NameWakander;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
    //	public static boolean nullItemID;
//	public static boolean getMetadata;
    public static String directory;
    public static String charset;
    //	public static boolean outputLanguageFile;
//	public static boolean outputEntityIDs;
//	public static boolean outputMetadataNull;
//	public static boolean outputVanillaLanguage;
//	public static String outputMetadataDetailNames;
//	public static boolean nullItemMetadata;
//	public static boolean outputErrorLogs;
    public static int checkDuplicateLimit;

    private static final String crlf = System.getProperty("line.separator");
    //	private final int blockListSize = 4096;
//	private final int itemListSize = 32000;
    private static LinkedHashSet<String> itemNames = new LinkedHashSet<String>();
    private static LinkedHashSet<String> blockNames = new LinkedHashSet<String>();
    public static LinkedHashMap<String, Integer> blockanditemNames = new LinkedHashMap<String, Integer>();
    public static Multimap<String, String> oreBasedNames = HashMultimap.create();
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
    public void postInit(FMLPostInitializationEvent event)
    {
//        this.addBlockUniqueStrings();
//        this.addItemUniqueStrings();
//		this.printList("blockNames" + ext, this.blockNames, true);
//		this.printList("itemNames" + ext, itemNames, true);

/*        addItemsNameCreative();
        addOreNames();

        printMultiMapList("OreNames" + ext, oreBasedNames, true);
        printMetaList("BlockAndItemWithMetaNames" + ext, blockanditemNames, true);*/
        Thread thread = new Thread(){
            @Override
            public void run() {
                addItemsNameCreative();
                addOreNames();
                printMultiMapList("OreNames" + ext, oreBasedNames, true);
                printMetaList("BlockAndItemWithMetaNames" + ext, blockanditemNames, true);
            }
        };
        thread.start();
    }
    private void addBlockUniqueStrings()
    {
        blockNames.add("UniqueName, UnlocalizedName, LocalizedName" + crlf);
        for (Object block : GameData.getBlockRegistry()) {
            addBlockName((Block)block);
        }
    }
    private void addItemUniqueStrings()
    {
        itemNames.add("UniqueName, UnlocalizedName, LocalizedName" + crlf);
        for (Object item : GameData.getItemRegistry()) {
            addItemName((Item)item);
        }
    }
    private void addBlockName(Block block)
    {
        if (block == null) return;
        String str;
        String blockUnique = getUniqueStrings(block);
        String blockUnlocalized = block.getUnlocalizedName() + ".name";
        String blockLocalized = block.getLocalizedName();
        if (!blockUnlocalized.equals(blockLocalized)) {
            str = String.format("%s, %s, %s" + crlf, blockUnique, blockUnlocalized, blockLocalized);
            blockNames.add(str);
        }
    }
    private static void addItemName(Item item)
    {
        if (item == null) return;
        String itemUnique = getUniqueStrings(item);
        String str;
        String itemUnlocalized;
        String itemLocalized;
        if(!(item instanceof ItemBlock)) {
            itemUnlocalized = item.getUnlocalizedName() + ".name";
            itemLocalized = item.getItemStackDisplayName(new ItemStack(item));
            if (!itemLocalized.equals(itemUnlocalized)) {
                str= String.format("%s, %s, %s" + crlf, itemUnique, itemUnlocalized, itemLocalized);
                itemNames.add(str);
            }
        }
        if(item.getHasSubtypes()) {
            int counter = 0;
            short meta = (item instanceof ItemBlock)?16:Short.MAX_VALUE;
            ItemStack stack;
            for(int i = 0;i < meta; i++){
                stack = new ItemStack(item, 1, i);
                try {
                    if(stack.getUnlocalizedName() == null || stack.getUnlocalizedName().equals("")) break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                if(!addItemStackName(stack)){
                    counter++;
                }
                if(counter > checkDuplicateLimit){
                    break;
                }
            }
        }
    }
    private static boolean addItemStackName(ItemStack stack)
    {
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

    private static String getItemStackName(ItemStack stack)
    {
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

    private void printList(String filename, Collection col, boolean flag)
    {
        File dir = new File(minecraft.mcDataDir, directory);
        if(!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
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
    public static void printMetaList(String filename, Map<String, Integer> map, boolean flag)
    {
        File dir = new File(minecraft.mcDataDir, directory);
        if(!dir.exists()) dir.mkdir();
        File file = new File(dir, filename);
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

    public static String getUniqueStrings(Object obj)
    {
        if(obj instanceof Block) {
            return GameData.getBlockRegistry().getNameForObject(obj);
        } else if (obj instanceof Item){
            return GameData.getItemRegistry().getNameForObject(obj);
        } else return "";
    }
}