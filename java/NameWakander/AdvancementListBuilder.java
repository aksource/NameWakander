package NameWakander;

import com.google.common.collect.Lists;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.storage.SaveHandlerMP;

import java.io.File;
import java.util.List;

import static NameWakander.NameWakander.ext;

public class AdvancementListBuilder extends ObjectListBuilder {
    private List<String> advancementNameList = Lists.newArrayList();

    @Override
    void create() {
        AdvancementManager advancementManager = new AdvancementManager(new File(new File(new SaveHandlerMP().getWorldDirectory(), "data"), "advancements"));

        String str;
        for (Advancement advancement : advancementManager.getAdvancements()) {
            if (advancement.getParent() != null) {
                str = String.format("%s, %s, %s", advancement.getId(), I18n.translateToLocal(advancement.getId().toString()), I18n.translateToLocal(advancement.getParent().getId().toString()));
            } else {
                str = String.format("%s, %s, %s", advancement.getId(), I18n.translateToLocal(advancement.getId().toString()), "No Parent");
            }
            advancementNameList.add(str);
        }
    }

    @Override
    void writeToFile() {
        printNameList("AdvancementNames" + ext, advancementNameList, "UnlocalizedName, LocalizedName, ParentAdvancementLocalizedName", true);
    }
}
