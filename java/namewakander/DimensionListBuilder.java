package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

import java.util.Collections;
import java.util.List;

import static namewakander.NameWakander.ext;

public class DimensionListBuilder extends ObjectListBuilder {
    private final List<IdNameObj<Integer>> dimensionIdList = Lists.newArrayList();

    @Override
    void create() {
        String str;
        WorldProvider provider;
        for (DimensionType type : DimensionType.values()) {
            for (int i : DimensionManager.getDimensions(type)) {
                provider = DimensionManager.createProviderFor(i);
                if (provider != null) {
                    str = String.format("%s, %s", provider.getDimensionType().getName(), StringUtils.translateToLocal(provider.getDimensionType().getName()));
                    dimensionIdList.add(new IdNameObj<>(i, str));
                }
            }
        }
    }

    @Override
    void writeToFile() {
        Collections.sort(dimensionIdList);
        printList("DimensionIDs" + ext,
                dimensionIdList,
                "UniqueId, UnlocalizedName, LocalizedName",
                true);
    }
}
