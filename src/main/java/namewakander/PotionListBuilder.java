package namewakander;

import com.google.common.collect.Lists;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static namewakander.ConfigUtils.COMMON;

public class PotionListBuilder extends ObjectListBuilder<Potion> {

  private final List<IdNameObj<ResourceLocation>> potionIdList = Lists.newArrayList();

  @Override
  void create() {
    ForgeRegistries.POTION_TYPES.forEach(this::addName);
  }

  @Override
  void writeToFile() {
    Collections.sort(potionIdList);
    printList("Potions" + COMMON.ext,
        potionIdList,
        "RegistryName, Effects",
        true);
  }

  void addName(Potion potion) {
    if (Objects.nonNull(potion.getRegistryName())) {
      StringJoiner joiner = new StringJoiner(",");
      potion.getEffects().forEach(e -> joiner.add(e.getEffectName()));
      String str = String.format("Effects:{%s}", joiner.toString());
      potionIdList.add(new IdNameObj<>(potion.getRegistryName(), str));
    }
  }
}
