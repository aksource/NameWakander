package namewakander;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig.Loading;
import org.apache.logging.log4j.LogManager;

/**
 * Created by A.K. on 2019/05/10.
 */
public class ConfigUtils {

  public static final Common COMMON;

  static final ForgeConfigSpec configSpec;

  static {
    Builder builder = new ForgeConfigSpec.Builder();
    COMMON = new Common(builder);
    configSpec = builder.build();
  }

  @SuppressWarnings("unused")
  @SubscribeEvent
  public static void configLoading(final Loading event) {
    LogManager.getLogger().debug(NameWakander.MOD_ID, "Loaded NameWakander config file {}",
        event.getConfig().getFileName());
    COMMON.ext = COMMON.csvFormatConfigValue.get() ? ".csv" : ".txt";;
    COMMON.directory = COMMON.directoryConfigValue.get();
    COMMON.charset = COMMON.charsetConfigValue.get();
  }

  public static class Common {

    static String directory;
    static String charset;
    static String ext;
    private BooleanValue csvFormatConfigValue;
    private ConfigValue<String> directoryConfigValue;
    private ConfigValue<String> charsetConfigValue;

    Common(Builder builder) {
      builder.comment("Common settings")
          .push(NameWakander.MOD_ID);
      directoryConfigValue = builder
          .comment("ファイル出力フォルダ。.minecraft以下に作成される。")
          .define("directory", "NameWakander");
      csvFormatConfigValue = builder.comment("csv形式で出力する。")
          .define("csvFormat", false);
      charsetConfigValue = builder
          .comment("出力ファイルの文字コード。通常は変更する必要はない。")
          .define("charset", "UTF-8");
      builder.pop();
    }
  }
}
