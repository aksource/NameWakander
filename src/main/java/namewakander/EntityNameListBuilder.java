package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

import static namewakander.ConfigUtils.COMMON;

public class EntityNameListBuilder extends ObjectListBuilder<EntityType> {

  private final List<String> entityNameList = Lists.newArrayList();

  @Override
  void create() {
    ForgeRegistries.ENTITIES.forEach(this::addName);
  }

  @Override
  void writeToFile() {
    printNameList("EntityNames" + COMMON.ext, entityNameList,
        "RegistryName, UnlocalizedName, LocalizedName(if exist)", true);
  }

  @Override
  void addName(EntityType entityType) {
    String str;
    String entityName;
    if (Objects.nonNull(entityType.getRegistryName())) {
      String rl = entityType.getRegistryName().toString();
      entityName = "entity." + rl;
      str = String.format("%s, %s, %s", rl, entityName, StringUtils.translateToLocal(entityName));
      entityNameList.add(str);
    }
  }
}
