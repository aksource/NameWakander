package NameWakander;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

import static NameWakander.NameWakander.ext;

public class EntityNameListBuilder extends ObjectListBuilder {
    private List<String> entityNameList = Lists.newArrayList();
    @Override
    @SuppressWarnings("Deprecated")
    void create() {
        String str;
        String entityName;
        for (ResourceLocation resourceLocation : EntityList.getEntityNameList()) {
            entityName = "entity." + EntityList.getTranslationName(resourceLocation) + ".name";
            str = String.format("%s, %s, %s", resourceLocation.toString(), entityName, I18n.translateToLocal(entityName));
            entityNameList.add(str);
        }
    }

    @Override
    void writeToFile() {
        printNameList("EntityNames" + ext, entityNameList, "UniqueName, UnlocalizedName, LocalizedName", true);
    }
}
