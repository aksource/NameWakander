package namewakander;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;

import javax.annotation.Nonnull;

import static namewakander.ConfigUtils.COMMON;
import static namewakander.NameWakander.getResourceLocationString;

public class TagNameBuilder extends ObjectListBuilder<Tag<Item>> {

  private final Multimap<String, String> tagBasedNames = HashMultimap.create();

  @Override
  void create() {
    TagCollection<Item> tagCollection = ItemTags.getCollection();
    tagCollection.getTagMap().values().forEach(this::addName);
  }

  @Override
  void writeToFile() {
    printMultiMapList("TagNames" + COMMON.ext, tagBasedNames, true);
  }


  void addName(@Nonnull Tag<Item> tag) {
    tag.getAllElements().forEach(
        item -> tagBasedNames.put(tag.getId().toString(), getItemStackName(new ItemStack(item))));
  }

  private String getItemStackName(ItemStack stack) {
    String stackUnique;
    String str;
    stackUnique = getResourceLocationString(stack.getItem());
    try {
      String itemStackUnlocalized = stack.getTranslationKey();
      String itemStackLocalized = stack.getDisplayName().getFormattedText();
      str = String
          .format("%s, %s, %s"/* + CR_LF*/, stackUnique, itemStackUnlocalized, itemStackLocalized);
      return str;
    } catch (Exception e) {
      e.printStackTrace();
      NameWakander.logger
          .warning(String.format("[namewakander]%s has an illegal name", stackUnique));
      return "";
    }
  }
}
