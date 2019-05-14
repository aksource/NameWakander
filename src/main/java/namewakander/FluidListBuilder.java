package namewakander;

import static namewakander.ConfigUtils.Common.ext;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;

public class FluidListBuilder extends ObjectListBuilder {

  private final List<IdNameObj<String>> fluidIdList = Lists.newArrayList();

  @Override
  void create() {
    String str;
//    Map<String, Fluid> fluids = FluidRegistry.getRegisteredFluids();
//    for (String fluidName : fluids.keySet()) {
//      Fluid fluid = fluids.get(fluidName);
//      FluidStack fluidStack = FluidRegistry.getFluidStack(fluidName, Fluid.BUCKET_VOLUME);
//      str = String.format("%s, %s", fluidName, fluid.getLocalizedName(fluidStack));
//      fluidIdList.add(new IdNameObj<>(fluidName, str));
//    }
  }

  @Override
  void writeToFile() {
    Collections.sort(fluidIdList);
    printList("FluidIDs" + ext,
        fluidIdList,
        "UniqueId, UnlocalizedName, LocalizedName(if exist)",
        true);
  }
}
