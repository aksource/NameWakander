package namewakander;

import static namewakander.ConfigUtils.Common.ext;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class AdvancementListBuilder extends ObjectListBuilder {

  private final List<String> advancementNameList = Lists.newArrayList();

  @Override
  void create() {
    MinecraftServer ms = Minecraft.getInstance().getIntegratedServer();
    if (Objects.nonNull(ms)) {
      AdvancementManager advancementManager = ms.getAdvancementManager();
      String str;
      for (Advancement advancement : advancementManager.getAllAdvancements()) {
        DisplayInfo displayInfo = advancement.getDisplay();
        String title =
            Objects.nonNull(displayInfo) ? displayInfo.getTitle().getFormattedText() : "";
        if (advancement.getParent() != null) {
          String parentTitle =
              Objects.nonNull(advancement.getParent().getDisplay()) ? advancement.getParent()
                  .getDisplay().getTitle().getFormattedText() : "";
          str = String.format("%s, %s, %s", advancement.getId(), title, parentTitle);
        } else {
          str = String.format("%s, %s, %s", advancement.getId(), title, "No Parent");
        }
        advancementNameList.add(str);
      }
    }
  }

  @Override
  void writeToFile() {
    printNameList("AdvancementNames" + ext, advancementNameList,
        "RegistryName, LocalizedName(if exist), ParentAdvancementLocalizedName(if exist)", true);
  }
}
