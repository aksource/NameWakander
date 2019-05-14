package namewakander;

import static namewakander.ConfigUtils.Common.ext;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import namewakander.utils.StringUtils;
import net.minecraft.potion.Potion;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionListBuilder extends ObjectListBuilder {

  private final List<IdNameObj<Integer>> potionIdList = Lists.newArrayList();

  @Override
  void create() {

    for (Potion potion : ForgeRegistries.POTIONS) {
      if (potion != null) {
        addPotionName(potion);
      }
    }
  }

  @Override
  void writeToFile() {
    Collections.sort(potionIdList);
    printList("PotionIDs" + ext,
        potionIdList,
        "UniqueId, RegistryName, ModId, UnlocalizedName, LocalizedName",
        true);
  }

  private void addPotionName(Potion potion) {
    if (potion.getRegistryName() != null) {
      String str = String.format("%s, %s, %s, %s", potion.getRegistryName().toString(),
          potion.getRegistryName().getNamespace(), potion.getName(),
          StringUtils.translateToLocal(potion.getName()));
      potionIdList.add(new IdNameObj<>(Potion.getIdFromPotion(potion), str));
    }
  }
}
