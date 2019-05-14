package namewakander;

import static namewakander.ConfigUtils.Common.ext;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import namewakander.utils.StringUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantmentListBuilder extends ObjectListBuilder {

  private final List<IdNameObj<Integer>> enchantmentIdList = Lists.newArrayList();

  @Override
  void create() {

    for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
      if (enchantment != null) {
        addEnchantmentName(enchantment);
      }
    }
  }

  @Override
  void writeToFile() {

    Collections.sort(enchantmentIdList);
    printList("EnchantmentIDs" + ext,
        enchantmentIdList,
        "UniqueId, RegistryName, ModId, UnlocalizedName, LocalizedName",
        true);
  }

  private void addEnchantmentName(Enchantment enchantment) {
    if (enchantment.getRegistryName() != null) {
      String str = String.format("%s, %s, %s, %s", enchantment.getRegistryName().toString(),
          enchantment.getRegistryName().getNamespace(), enchantment.getName(),
          StringUtils.translateToLocal(enchantment.getName()));
      enchantmentIdList.add(new IdNameObj<>(IRegistry.field_212628_q.getId(enchantment), str));
    }
  }
}
