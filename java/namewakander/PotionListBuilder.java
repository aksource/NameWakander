package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.potion.Potion;

import java.util.Collections;
import java.util.List;

import static namewakander.NameWakander.ext;

public class PotionListBuilder extends ObjectListBuilder {
    private final List<IdNameObj<Integer>> potionIdList = Lists.newArrayList();

    @Override
    void create() {

        for (Potion potion : Potion.REGISTRY) {
            if (potion != null) {
                addPotionName(potion);
            }
        }
    }

    @Override
    void writeToFile() {
        Collections.sort(potionIdList);
        printList("PotionIDs" + ext,
                potionIdList,
                "UniqueId, ModId, UnlocalizedName, LocalizedName",
                true);
    }

    private void addPotionName(Potion potion) {
        if (potion.getRegistryName() != null) {
            String str = String.format("%s, %s, %s", potion.getRegistryName().getResourceDomain(), potion.getName(), StringUtils.translateToLocal(potion.getName()));
            potionIdList.add(new IdNameObj<>(Potion.getIdFromPotion(potion), str));
        }
    }
}
