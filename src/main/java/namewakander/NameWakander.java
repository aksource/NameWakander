package namewakander;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod(NameWakander.MOD_ID)
public class NameWakander {

  static final String MOD_ID = "name-wakander";
  static final String MOD_NAME = "NameWakander";
  static final String CR_LF = System.getProperty("line.separator");
  static Minecraft minecraft = Minecraft.getInstance();
  static Logger logger = Logger.getLogger("name-wakander");

  public NameWakander() {
    final IEventBus modEventBus =
        FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(this::doClientStuff);
    ModLoadingContext.get().registerConfig(Type.COMMON, ConfigUtils.configSpec);
    modEventBus.register(ConfigUtils.class);
    MinecraftForge.EVENT_BUS.register(this);
  }

  public static String getResourceLocationString(Object obj) {
    ResourceLocation registryName = new ResourceLocation("air");
    if (obj instanceof ItemStack) {
      obj = ((ItemStack) obj).getItem();
    }
    if (obj instanceof IForgeRegistryEntry) {
      Optional<ResourceLocation> rl = Optional
          .ofNullable(((IForgeRegistryEntry) obj).getRegistryName());
      if (rl.isPresent()) {
        registryName = rl.get();
      }
    }
    return registryName.toString();
  }

  @SuppressWarnings("unused")
  public void doClientStuff(final FMLClientSetupEvent event) {
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public void serverStarted(final FMLServerStartedEvent event) {
    List<ObjectListBuilder> list = new ArrayList<>();
    list.add(new ItemBlockListBuilder());
    list.add(new TagNameBuilder());
    list.add(new PotionListBuilder());
    // TODO 液体登録系がもうちょっとしっかりしたら復活させる
//    list.add(new FluidListBuilder());
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