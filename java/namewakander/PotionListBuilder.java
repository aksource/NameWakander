package namewakander;

import com.google.common.collect.Lists;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.translation.I18n;

import java.util.Collections;
import java.util.List;

import static namewakander.NameWakander.ext;

public class PotionListBuilder extends ObjectListBuilder {
    private List<IdNameObj<Integer>> potionIdList = Lists.newArrayList();

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

    @SuppressWarnings("Deprecated")
    private void addPotionName(Potion potion) {
        String str = String.format("%s, %s, %s", potion.getRegistryName().getResourceDomain(), potion.getName(), I18n.translateToLocal(potion.getName()));
        potionIdList.add(new IdNameObj<>(Potion.getIdFromPotion(potion), str));
    }
}
