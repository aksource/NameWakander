package namewakander;

import static namewakander.ConfigUtils.Common.ext;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import namewakander.utils.StringUtils;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeListBuilder extends ObjectListBuilder {

  private final List<IdNameObj<Integer>> biomeIdList = Lists.newArrayList();

  @Override
  void create() {
    for (Biome biome : ForgeRegistries.BIOMES) {
      if (biome != null) {
        addBiomeName(biome);
      }
    }
  }

  @Override
  void writeToFile() {
    Collections.sort(biomeIdList);
    printList("BiomeIDs" + ext,
        biomeIdList,
        "UniqueId, ModId, RegistryName, LocalizedName(if exist)",
        true);
  }

  private void addBiomeName(Biome biome) {
    if (biome.getRegistryName() != null) {
      String str = String.format("%s, %s, %s", biome.getRegistryName().getNamespace(),
          biome.getRegistryName().toString(),
          StringUtils.translateToLocal("biome." + biome.getRegistryName().toString()));
      biomeIdList.add(new IdNameObj<>(IRegistry.field_212624_m.getId(biome), str));
    }
  }
}
