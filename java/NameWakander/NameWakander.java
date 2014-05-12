package NameWakander;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

@Mod(modid="NameWakander", name="NameWakander", version="172V5",dependencies="required-after:FML", canBeDeactivated = true, useMetadata = true)
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
	
	private final String crlf = System.getProperty("line.separator");
//	private final int blockListSize = 4096;
//	private final int itemListSize = 32000;
	private LinkedHashSet<String> itemNames = new LinkedHashSet<String>();
	private LinkedHashSet<String> blockNames = new LinkedHashSet<String>();
	private LinkedHashMap<String, Integer> blockanditemNames = new LinkedHashMap<String, Integer>();
	private long start,end;
	private String ext;
	private Minecraft minecraft = Minecraft.getMinecraft();

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
        this.addItemsNameCreative();
//		this.printList("blockNames" + ext, this.blockNames, true);
//		this.printList("itemNames" + ext, itemNames, true);
		this.printMetaList("BlockAndItemWithMetaNames" + ext, blockanditemNames, true);
	}
	private void addBlockUniqueStrings()
	{
		this.blockNames.add("UniqueName, UnlocalizedName, LocalizedName" + crlf);
        for (Object block : GameData.getBlockRegistry()) {
            addBlockName((Block)block);
        }
	}
	private void addItemUniqueStrings()
	{
		this.itemNames.add("UniqueName, UnlocalizedName, LocalizedName" + crlf);
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
            this.blockNames.add(str);
        }
    }
	private void addItemName(Item item)
	{
        if (item == null) return;
		String itemUnique = this.getUniqueStrings(item);
		String str;
        String itemUnlocalized;
        String itemLocalized;
		if(!(item instanceof ItemBlock)) {
            itemUnlocalized = item.getUnlocalizedName() + ".name";
            itemLocalized = item.getItemStackDisplayName(new ItemStack(item));
            if (!itemLocalized.equals(itemUnlocalized)) {
                str= String.format("%s, %s, %s" + crlf, itemUnique, itemUnlocalized, itemLocalized);
                this.itemNames.add(str);
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
				if(counter > this.checkDuplicateLimit){
					break;
				}
			}
		}
	}
	private boolean addItemStackName(ItemStack stack)
	{
		String stackUnique;
		String str;
		stackUnique = getUniqueStrings(stack.getItem());
        try {
            String itemStackUnlocalized = stack.getUnlocalizedName() + ".name";
            String itemStackLocalized = stack.getDisplayName();
//            if (!itemStackUnlocalized.equals(itemStackLocalized) && !itemStackLocalized.contains(itemStackUnlocalized)) {
                str = String.format("%s, %s, %s"/* + crlf*/, stackUnique, itemStackUnlocalized, itemStackLocalized);
//                if(this.blockanditemNames.containsKey(str))
//                    return false;
//                else{
                    this.blockanditemNames.put(str, stack.getItemDamage());
                    return true;
//                }
//            } else return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(String.format("[NameWakander]%s has an illegal name", stackUnique));
            return false;
        }
	}

    private void addItemsNameCreative() {
        List<ItemStack> itemsList = new ArrayList<ItemStack>();
        for (CreativeTabs tabs : CreativeTabs.creativeTabArray) {
            tabs.displayAllReleventItems(itemsList);
        }

        for (ItemStack itemStack : itemsList) {
            addItemStackName(itemStack);
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
	private void printMetaList(String filename, Map<String, Integer> map, boolean flag)
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
	public static String getUniqueStrings(Object obj)
	{
		if(obj instanceof Block) {
			return GameData.getBlockRegistry().getNameForObject(obj);
        } else if (obj instanceof Item){
			return GameData.getItemRegistry().getNameForObject(obj);
		} else return "";
	}
}