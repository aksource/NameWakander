package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static namewakander.ConfigUtils.COMMON;

public class BiomeListBuilder extends ObjectListBuilder<Biome> {

  private final List<IdNameObj<Integer>> biomeIdList = Lists.newArrayList();

  @Override
  void create() {
    ForgeRegistries.BIOMES.forEach(this::addName);
  }

  @Override
  void writeToFile() {
    Collections.sort(biomeIdList);
    printList("BiomeIDs" + COMMON.ext,
        biomeIdList,
        "UniqueId, ModId, RegistryName, LocalizedName(if exist)",
        true);
  }

  void addName(Biome biome) {
    if (Objects.nonNull(biome.getRegistryName())) {
      String str = String.format("%s, %s, %s", biome.getRegistryName().getNamespace(),
          biome.getRegistryName().toString(),
          StringUtils.translateToLocal("biome." + biome.getRegistryName().toString()));
      biomeIdList.add(new IdNameObj<>(Registry.BIOME.getId(biome), str));
    }
  }
}
