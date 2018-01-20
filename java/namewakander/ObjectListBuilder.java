package namewakander;

import com.google.common.collect.Multimap;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static namewakander.NameWakander.*;

public abstract class ObjectListBuilder {
    private long start, end;
    private String description;
    abstract void create();

    abstract void writeToFile();

    void printMultiMapList(String filename, Multimap<String, String> map, boolean flag) {
        Consumer<BufferedWriter> consumer = (src) -> {
            try {
                src.write("OreName" + CR_LF);
                src.write("  UniqueName, UnlocalizedName, LocalizedName, Metadata" + CR_LF);
                List<String> sortedKeyList = new ArrayList<>();
                sortedKeyList.addAll(map.keySet());
                Collections.sort(sortedKeyList);
                for (String key : sortedKeyList) {
                    src.write(key + CR_LF);
                    for (String names : map.get(key)) {
                        src.write("  " + names + CR_LF);
                    }
                }
            } catch (IOException e) {
                FMLCommonHandler.instance().raiseException(e, String.format("namewakander: %s に書き込みできません。", filename + ext), true);
            }
        };
        print(filename, flag, consumer);
        map.clear();
    }

    void printList(String filename, List<? extends IdNameObj<? extends Comparable<?>>> list, String description, boolean flag) {
        Consumer<BufferedWriter> consumer = (src) -> {
            try {
                src.write(description + CR_LF);
                try {
                    list.forEach(idNameObj -> {
                        try {
                            src.write(idNameObj.id + ", " + idNameObj.name + CR_LF);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (RuntimeException e) {
                    Throwable th = e.getCause();
                    if (th instanceof IOException) {
                        throw (IOException) th;
                    }
                }
            } catch (IOException e) {
                FMLCommonHandler.instance().raiseException(e, String.format("namewakander: %s に書き込みできません。", filename + ext), true);
            }
        };
        print(filename, flag, consumer);
        list.clear();
    }

    void printMetaList(String filename, Map<String, Integer> map, boolean flag) {
        Consumer<BufferedWriter> consumer = (src) -> {
            try {
                src.write("UniqueName, UnlocalizedName, LocalizedName, Metadata" + CR_LF);
                for (String key : map.keySet()) {
                    src.write(key);
                    src.write(", " + Integer.toString(map.get(key)) + CR_LF);
                }
            } catch (IOException e) {
                FMLCommonHandler.instance().raiseException(e, String.format("namewakander: %s に書き込みできません。", filename + ext), true);
            }
        };
        print(filename, flag, consumer);
        map.clear();
    }

    void printNameList(String filename, List<String> list, String description, boolean flag) {
        Consumer<BufferedWriter> consumer = (src) -> {
            try {
                src.write(description + CR_LF);
                for (String name : list) {
                    src.write(name + CR_LF);
                }
            } catch (IOException e) {
                FMLCommonHandler.instance().raiseException(e, String.format("namewakander: %s に書き込みできません。", filename + ext), true);
            }
        };
        print(filename, flag, consumer);
        list.clear();
    }

    private void print(String filename, boolean flag, Consumer<BufferedWriter> consumer) {
        File dir = new File(minecraft.mcDataDir, directory);
        if (!dir.exists() && !dir.mkdir()) return;
        File file = new File(dir, filename);
        start = System.currentTimeMillis();
        try (
                OutputStream stream = new FileOutputStream(file);
                BufferedWriter src = new BufferedWriter(new OutputStreamWriter(stream, charset))) {
            consumer.accept(src);
            end = System.currentTimeMillis();
            long time = end - start;
            if (flag) src.write("#output time is " + String.format("%d", time) + " ms.\n");
            src.flush();
            src.close();
        } catch (IOException e) {
            FMLCommonHandler.instance().raiseException(e, String.format("namewakander: %s に書き込みできません。", file.getName()), true);
        }
    }
}
