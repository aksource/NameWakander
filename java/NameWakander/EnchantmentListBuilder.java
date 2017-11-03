package NameWakander;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.translation.I18n;

import java.util.Collections;
import java.util.List;

import static NameWakander.NameWakander.ext;

public class EnchantmentListBuilder extends ObjectListBuilder {
    private List<IdNameObj<Integer>> enchantmentIdList = Lists.newArrayList();

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
        printList("EnchantmentIDs" + ext, enchantmentIdList, true);
    }

    @SuppressWarnings("Deprecated")
    private void addEnchantmentName(Enchantment enchantment) {
        String str = String.format("%s, %s", enchantment.getName(), I18n.translateToLocal(enchantment.getName()));
        enchantmentIdList.add(new IdNameObj<>(Enchantment.getEnchantmentID(enchantment), str));
    }
}
