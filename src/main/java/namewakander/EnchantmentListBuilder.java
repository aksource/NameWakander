package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static namewakander.ConfigUtils.COMMON;

public class EnchantmentListBuilder extends ObjectListBuilder<Enchantment> {

  private final List<IdNameObj<ResourceLocation>> enchantmentIdList = Lists.newArrayList();

  @Override
  void create() {
    ForgeRegistries.ENCHANTMENTS.forEach(this::addName);
  }

  @Override
  void writeToFile() {

    Collections.sort(enchantmentIdList);
    printList("EnchantmentIDs" + COMMON.ext,
        enchantmentIdList,
        "UniqueId, RegistryName, ModId, UnlocalizedName, LocalizedName",
        true);
  }

  void addName(Enchantment enchantment) {
    if (Objects.nonNull(enchantment.getRegistryName())) {
      String str = String.format("%s, %s, %s, %s", enchantment.getRegistryName().toString(),
          enchantment.getRegistryName().getNamespace(), enchantment.getName(),
          StringUtils.translateToLocal(enchantment.getName()));
      enchantmentIdList.add(new IdNameObj<>(ForgeRegistries.ENCHANTMENTS.getKey(enchantment), str));
    }
  }
}
