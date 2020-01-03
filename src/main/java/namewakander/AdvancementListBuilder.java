package namewakander;

import com.google.common.collect.Lists;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Objects;

import static namewakander.ConfigUtils.COMMON;

public class AdvancementListBuilder extends ObjectListBuilder<Advancement> {

  private final List<String> advancementNameList = Lists.newArrayList();

  @Override
  void create() {
    MinecraftServer ms = Minecraft.getInstance().getIntegratedServer();
    if (Objects.nonNull(ms)) {
      AdvancementManager advancementManager = ms.getAdvancementManager();
      advancementManager.getAllAdvancements().forEach(this::addName);
    }
  }

  @Override
  void writeToFile() {
    printNameList("AdvancementNames" + COMMON.ext, advancementNameList,
        "RegistryName, LocalizedName(if exist), ParentAdvancementLocalizedName(if exist)", true);
  }

  @Override
  void addName(Advancement advancement) {
    String str;
    DisplayInfo displayInfo = advancement.getDisplay();
    String title =
            Objects.nonNull(displayInfo) ? displayInfo.getTitle().getFormattedText() : "";
    if (Objects.nonNull(advancement.getParent())) {
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
