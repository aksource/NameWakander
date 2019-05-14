package namewakander;

import static namewakander.ConfigUtils.Common.ext;
import static namewakander.NameWakander.getResourceLocationString;

import com.google.common.collect.Lists;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemBlockListBuilder extends ObjectListBuilder {

  private final LinkedHashMap<String, Integer> blockAndItemNames = new LinkedHashMap<>();
  private final List<String> blockStatesList = Lists.newArrayList();

  @Override
  void create() {

    NonNullList<ItemStack> itemsList = NonNullList.create();
    for (Item item : ForgeRegistries.ITEMS) {
      itemsList.add(new ItemStack(item));
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
//    printNameList("BlockStateList" + ext, blockStatesList, "LocalizedName, BlockState", true);
  }

  private void addItemStackName(ItemStack stack) {
    String stackUnique;
    String str;
    stackUnique = getResourceLocationString(stack.getItem());
//        if (stack.getItem() instanceof ItemBlock) {
//            addBlockState(stack);
//        }
    try {
      String itemStackUnlocalized = stack.getTranslationKey();
      String itemStackLocalized = stack.getDisplayName().getFormattedText();
      str = String
          .format("%s, %s, %s"/* + CR_LF*/, stackUnique, itemStackUnlocalized, itemStackLocalized);
      blockAndItemNames.put(str, 0/*stack.getItemDamage()*/);
    } catch (Exception e) {
      e.printStackTrace();
      NameWakander.logger
          .warning(String.format("[namewakander]%s has an illegal name", stackUnique));
    }
  }

  /**
   * BlockStateリスト生成
   *
   * @param itemStack ItemBlockのItemStack
   */
//    @SuppressWarnings("Deprecated")
//    private void addBlockState(ItemStack itemStack) {
//        String str;
//        Block block = Block.getBlockFromItem(itemStack.getItem());
//        if (block != Blocks.AIR) {
//            try {
//                IBlockState state = block.getStateById(itemStack.getItemDamage());
//                str = String.format("%s, %s", itemStack.getDisplayName(), state.toString());
//                if (!blockStatesList.contains(str)) {
//                    blockStatesList.add(str);
//                }
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
