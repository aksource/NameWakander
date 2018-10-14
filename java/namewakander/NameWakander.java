package namewakander;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Mod(modid = NameWakander.MOD_ID,
        name = NameWakander.MOD_NAME,
        version = NameWakander.MOD_VERSION,
        dependencies = NameWakander.MOD_DEPENDENCIES,
        useMetadata = true,
        acceptedMinecraftVersions = NameWakander.MOD_MC_VERSION)
@SuppressWarnings("unused")
public class NameWakander {
    public static final String MOD_ID = "name-wakander";
    public static final String MOD_NAME = "NameWakander";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String MOD_DEPENDENCIES = "required-after:forge@[14.21.1,)";
    public static final String MOD_MC_VERSION = "[1.12,1.12.99]";
    static final String CR_LF = System.getProperty("line.separator");
    static String directory;
    static String charset;
    static String ext;
    static Minecraft minecraft = Minecraft.getMinecraft();
    static Logger logger = Logger.getLogger("name-wakander");
    private static boolean csvFormat;

    public static String getResourceLocationString(Object obj) {
        ResourceLocation registryName = new ResourceLocation("air");
        if (obj instanceof ItemStack) {
            obj = ((ItemStack) obj).getItem();
        }
        if (obj instanceof IForgeRegistryEntry) {
            Optional<ResourceLocation> rl = Optional.ofNullable(((IForgeRegistryEntry) obj).getRegistryName());
            if (rl.isPresent()) registryName = rl.get();
        }
        return registryName.toString();
    }

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
        List<ObjectListBuilder> list = new ArrayList<>();
        list.add(new ItemBlockListBuilder());
        list.add(new OreNameBuilder());
        list.add(new PotionListBuilder());
        list.add(new FluidListBuilder());
        list.add(new EnchantmentListBuilder());
        list.add(new DimensionListBuilder());
        list.add(new BiomeListBuilder());
        list.add(new EntityNameListBuilder());
        list.add(new VillagerProfessionListBuilder());
        list.add(new AdvancementListBuilder());
        Executor executor = Executors.newCachedThreadPool();
        list.forEach(builder -> executor.execute(builder::run));
    }
}