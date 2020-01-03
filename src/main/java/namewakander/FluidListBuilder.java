package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static namewakander.ConfigUtils.COMMON;

public class FluidListBuilder extends ObjectListBuilder<Fluid> {

  private final List<IdNameObj<ResourceLocation>> fluidIdList = Lists.newArrayList();

  @Override
  void create() {
    ForgeRegistries.FLUIDS.forEach(this::addName);
  }

  @Override
  void writeToFile() {
    Collections.sort(fluidIdList);
    printList("Fluids" + COMMON.ext,
        fluidIdList,
        "RegistryName, BucketItemName",
        true);
  }

  void addName(Fluid fluid) {
    if (Objects.nonNull(fluid.getRegistryName())) {
      Item bucket = fluid.getFilledBucket();
      String unlocalizedName = String.format("item.%s.%s", bucket.getRegistryName().getNamespace(), bucket.getRegistryName().getPath());
      String str = String.format("%s", StringUtils.translateToLocal(unlocalizedName));
      fluidIdList.add(new IdNameObj<>(fluid.getRegistryName(), str));
    }
  }
}
