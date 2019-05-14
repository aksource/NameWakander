package namewakander;

import static namewakander.ConfigUtils.Common.ext;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DimensionListBuilder extends ObjectListBuilder {

  private final List<IdNameObj<Integer>> dimensionIdList = Lists.newArrayList();

  @Override
  void create() {
    for (DimensionType type : DimensionType.func_212681_b()) {
      int i = DimensionManager.getRegistry().getId(type);
      dimensionIdList.add(new IdNameObj<>(i, type.toString()));
    }
  }

  @Override
  void writeToFile() {
    Collections.sort(dimensionIdList);
    printList("DimensionIDs" + ext,
        dimensionIdList,
        "UniqueId, UnlocalizedName, LocalizedName(if exist)",
        true);
  }
}
