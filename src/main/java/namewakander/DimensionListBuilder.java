package namewakander;

import com.google.common.collect.Lists;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

import java.util.Collections;
import java.util.List;

import static namewakander.ConfigUtils.COMMON;

public class DimensionListBuilder extends ObjectListBuilder<DimensionType> {

  private final List<IdNameObj<Integer>> dimensionIdList = Lists.newArrayList();

  @Override
  void create() {
    DimensionType.getAll().forEach(this::addName);
  }

  @Override
  void writeToFile() {
    Collections.sort(dimensionIdList);
    printList("DimensionIDs" + COMMON.ext,
        dimensionIdList,
        "UniqueId, UnlocalizedName, LocalizedName(if exist)",
        true);
  }

  @Override
  void addName(DimensionType dimensionType) {
    int i = DimensionManager.getRegistry().getId(dimensionType);
    dimensionIdList.add(new IdNameObj<>(i, dimensionType.toString()));
  }
}
