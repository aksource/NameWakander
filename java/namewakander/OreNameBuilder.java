package namewakander;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import static namewakander.NameWakander.ext;
import static namewakander.NameWakander.getResourceLocationString;

public class OreNameBuilder extends ObjectListBuilder {
    private Multimap<String, String> oreBasedNames = HashMultimap.create();

    @Override
    void create() {

        String[] oreNames = OreDictionary.getOreNames();
        for (String oreName : oreNames) {
            addItemStackNameFromOreName(oreName);
        }
    }

    @Override
    void writeToFile() {
        printMultiMapList("OreNames" + ext, oreBasedNames, true);
    }


    private void addItemStackNameFromOreName(String oreName) {
        List<ItemStack> oreList = OreDictionary.getOres(oreName);
        if (oreList == null || oreList.isEmpty()) return;
        for (ItemStack itemStack : oreList) {
            oreBasedNames.put(oreName, getItemStackName(itemStack));
        }
    }

    private String getItemStackName(ItemStack stack) {
        String stackUnique;
        String str;
        stackUnique = getResourceLocationString(stack.getItem());
        try {
            String itemStackUnlocalized = stack.getUnlocalizedName() + ".name";
            String itemStackLocalized = stack.getDisplayName();
            str = String.format("%s, %s, %s, %d"/* + CR_LF*/, stackUnique, itemStackUnlocalized, itemStackLocalized, stack.getItemDamage());
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            NameWakander.logger.warning(String.format("[namewakander]%s has an illegal name", stackUnique));
            return "";
        }
    }
}
