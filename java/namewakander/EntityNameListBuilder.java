package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import static namewakander.NameWakander.ext;

public class EntityNameListBuilder extends ObjectListBuilder {
    private final List<String> entityNameList = Lists.newArrayList();

    @Override
    void create() {
        String str;
        String entityName;
        for (ResourceLocation resourceLocation : EntityList.getEntityNameList()) {
            entityName = "entity." + EntityList.getTranslationName(resourceLocation) + ".name";
            str = String.format("%s, %s, %s", resourceLocation.toString(), entityName, StringUtils.translateToLocal(entityName));
            entityNameList.add(str);
        }
    }

    @Override
    void writeToFile() {
        printNameList("EntityNames" + ext, entityNameList, "UniqueName, UnlocalizedName, LocalizedName", true);
    }
}
