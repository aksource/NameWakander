package namewakander.utils;

import net.minecraft.util.text.translation.I18n;

/**
 * Created by A.K. on 2018/10/13.
 */
public class StringUtils {
    @SuppressWarnings("Deprecated")
    public static String translateToLocal(String str) {
        return I18n.translateToLocal(str);
    }
}
