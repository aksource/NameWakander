package namewakander;

import com.google.common.collect.Lists;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

import java.util.Collections;
import java.util.List;

import static namewakander.NameWakander.ext;

public class DimensionListBuilder extends ObjectListBuilder {
    private List<IdNameObj<Integer>> dimensionIdList = Lists.newArrayList();
    @Override
    @SuppressWarnings("Deprecated")
    void create() {
        String str;
        WorldProvider provider;
        for (DimensionType type : DimensionType.values()) {
            for (int i : DimensionManager.getDimensions(type)) {
                provider = DimensionManager.createProviderFor(i);
                if (provider != null) {
                    str = String.format("%s, %s", provider.getDimensionType().getName(), I18n.translateToLocal(provider.getDimensionType().getName()));
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
