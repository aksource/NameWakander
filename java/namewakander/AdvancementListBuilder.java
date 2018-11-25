package namewakander;

import com.google.common.collect.Lists;
import namewakander.utils.StringUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.world.storage.SaveHandlerMP;

import java.io.File;
import java.util.List;

import static namewakander.NameWakander.ext;

public class AdvancementListBuilder extends ObjectListBuilder {
    private final List<String> advancementNameList = Lists.newArrayList();

    @Override
    void create() {
        AdvancementManager advancementManager = new AdvancementManager(new File(new File(new SaveHandlerMP().getWorldDirectory(), "data"), "advancements"));

        String str;
        for (Advancement advancement : advancementManager.getAdvancements()) {
            if (advancement.getParent() != null) {
                str = String.format("%s, %s, %s", advancement.getId(), StringUtils.translateToLocal(advancement.getId().toString()), StringUtils.translateToLocal(advancement.getParent().getId().toString()));
            } else {
                str = String.format("%s, %s, %s", advancement.getId(), StringUtils.translateToLocal(advancement.getId().toString()), "No Parent");
            }
            advancementNameList.add(str);
        }
    }

    @Override
    void writeToFile() {
        printNameList("AdvancementNames" + ext, advancementNameList, "RegistryName, LocalizedName(if exist), ParentAdvancementLocalizedName", true);
    }
}
