package namewakander;

import com.google.common.collect.Lists;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static namewakander.NameWakander.ext;

public class FluidListBuilder extends ObjectListBuilder {
    private List<IdNameObj<String>> fluidIdList = Lists.newArrayList();

    @Override
    void create() {
        String str;
        Map<String, Fluid> fluids = FluidRegistry.getRegisteredFluids();
        for (String fluidName : fluids.keySet()) {
            Fluid fluid = fluids.get(fluidName);
            FluidStack fluidStack = FluidRegistry.getFluidStack(fluidName, Fluid.BUCKET_VOLUME);
            str = String.format("%s, %s", fluidName, fluid.getLocalizedName(fluidStack));
            fluidIdList.add(new IdNameObj<>(fluidName, str));
        }
    }

    @Override
    void writeToFile() {
        Collections.sort(fluidIdList);
        printList("FluidIDs" + ext,
                fluidIdList,
                "UniqueId, UnlocalizedName, LocalizedName",
                true);
    }
}
