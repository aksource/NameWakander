package namewakander;

import static namewakander.ConfigUtils.Common.ext;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import namewakander.utils.StringUtils;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityNameListBuilder extends ObjectListBuilder {

  private final List<String> entityNameList = Lists.newArrayList();

  @Override
  void create() {
    String str;
    String entityName;
    for (EntityType entityType : ForgeRegistries.ENTITIES) {
      if (Objects.nonNull(entityType.getRegistryName())) {
        String rl = entityType.getRegistryName().toString();
        entityName = "entity." + rl;
        str = String.format("%s, %s, %s", rl, entityName, StringUtils.translateToLocal(entityName));
        entityNameList.add(str);
      }
    }
  }

  @Override
  void writeToFile() {
    printNameList("EntityNames" + ext, entityNameList,
        "RegistryName, UnlocalizedName, LocalizedName(if exist)", true);
  }
}
