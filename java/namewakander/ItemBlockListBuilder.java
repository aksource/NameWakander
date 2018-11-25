package namewakander;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.LinkedHashMap;
import java.util.List;

import static namewakander.NameWakander.ext;
import static namewakander.NameWakander.getResourceLocationString;

public class ItemBlockListBuilder extends ObjectListBuilder {
    private final LinkedHashMap<String, Integer> blockAndItemNames = new LinkedHashMap<>();
    private final List<String> blockStatesList = Lists.newArrayList();

    @Override
    void create() {

        NonNullList<ItemStack> itemsList = NonNullList.create();
        for (Item item : Item.REGISTRY) {
            for (CreativeTabs tabs : CreativeTabs.CREATIVE_TAB_ARRAY) {
                item.getSubItems(tabs, itemsList);
            }
        }

        for (ItemStack itemStack : itemsList) {
            if (itemStack != null) {
                addItemStackName(itemStack);
            }
        }
    }

    @Override
    void writeToFile() {
        printMetaList("BlockAndItemWithMetaNames" + ext, blockAndItemNames, true);
        printNameList("BlockStateList" + ext, blockStatesList, "LocalizedName, BlockState", true);
    }

    private void addItemStackName(ItemStack stack) {
        String stackUnique;
        String str;
        stackUnique = getResourceLocationString(stack.getItem());
        if (stack.getItem() instanceof ItemBlock) {
            addBlockState(stack);
        }
        try {
            String itemStackUnlocalized = stack.getTranslationKey() + ".name";
            String itemStackLocalized = stack.getDisplayName();
            str = String.format("%s, %s, %s"/* + CR_LF*/, stackUnique, itemStackUnlocalized, itemStackLocalized);
            blockAndItemNames.put(str, stack.getItemDamage());
        } catch (Exception e) {
            e.printStackTrace();
            NameWakander.logger.warning(String.format("[namewakander]%s has an illegal name", stackUnique));
        }
    }

    /**
     * BlockStateリスト生成
     *
     * @param itemStack ItemBlockのItemStack
     */
    @SuppressWarnings("Deprecated")
    private void addBlockState(ItemStack itemStack) {
        String str;
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block != Blocks.AIR) {
            try {
                IBlockState state = block.getStateFromMeta(itemStack.getItemDamage());
                str = String.format("%s, %s", itemStack.getDisplayName(), state.toString());
                if (!blockStatesList.contains(str)) {
                    blockStatesList.add(str);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
