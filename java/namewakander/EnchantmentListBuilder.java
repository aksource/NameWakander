package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.enchantment.Enchantment;

import java.util.Collections;
import java.util.List;

import static namewakander.NameWakander.ext;

public class EnchantmentListBuilder extends ObjectListBuilder {
    private final List<IdNameObj<Integer>> enchantmentIdList = Lists.newArrayList();

    @Override
    void create() {

        for (Enchantment enchantment : Enchantment.REGISTRY) {
            if (enchantment != null) {
                addEnchantmentName(enchantment);
            }
        }
    }

    @Override
    void writeToFile() {

        Collections.sort(enchantmentIdList);
        printList("EnchantmentIDs" + ext,
                enchantmentIdList,
                "UniqueId, ModId, UnlocalizedName, LocalizedName",
                true);
    }

    private void addEnchantmentName(Enchantment enchantment) {
        if (enchantment.getRegistryName() != null) {
            String str = String.format("%s, %s, %s", enchantment.getRegistryName().getResourceDomain(), enchantment.getName(), StringUtils.translateToLocal(enchantment.getName()));
            enchantmentIdList.add(new IdNameObj<>(Enchantment.getEnchantmentID(enchantment), str));
        }
    }
}
