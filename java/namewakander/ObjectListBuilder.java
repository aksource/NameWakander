package namewakander;

import com.google.common.collect.Multimap;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static namewakander.NameWakander.*;

public abstract class ObjectListBuilder {
    void run() {
        create();
        writeToFile();
    }

    abstract void create();

    abstract void writeToFile();

    void printMultiMapList(String filename, Multimap<String, String> map, boolean flag) {
        ThrowableConsumer<BufferedWriter> consumer = (src) -> {
            src.write("OreName" + CR_LF);
            src.write("  UniqueName, UnlocalizedName, LocalizedName, Metadata" + CR_LF);
            List<String> sortedKeyList = new ArrayList<>(map.keySet());
            Collections.sort(sortedKeyList);
            for (String key : sortedKeyList) {
                src.write(key + CR_LF);
                for (String names : map.get(key)) {
                    src.write("  " + names + CR_LF);
                }
            }
        };
        print(filename, flag, tryWithException(consumer, (error, x) -> raiseException(error, filename + ext)));
        map.clear();
    }

    void printList(String filename, List<? extends IdNameObj<? extends Comparable<?>>> list, String description, boolean flag) {
        ThrowableConsumer<BufferedWriter> consumer = (src) -> {
            src.write(description + CR_LF);
            for (IdNameObj idNameObj : list) {
                src.write(idNameObj.id + ", " + idNameObj.name + CR_LF);
            }
        };
        print(filename, flag, tryWithException(consumer, (error, x) -> raiseException(error, filename + ext)));
        list.clear();
    }

    void printMetaList(String filename, Map<String, Integer> map, boolean flag) {
        ThrowableConsumer<BufferedWriter> consumer = (src) -> {
            src.write("UniqueName, UnlocalizedName, LocalizedName, Metadata" + CR_LF);
            for (String key : map.keySet()) {
                src.write(key);
                src.write(", " + Integer.toString(map.get(key)) + CR_LF);
            }
        };
        print(filename, flag, tryWithException(consumer, (error, x) -> raiseException(error, filename + ext)));
        map.clear();
    }

    void printNameList(String filename, List<String> list, String description, boolean flag) {
        ThrowableConsumer<BufferedWriter> consumer = (src) -> {
            src.write(description + CR_LF);
            for (String name : list) {
                src.write(name + CR_LF);
            }
        };
        print(filename, flag, tryWithException(consumer, (error, x) -> raiseException(error, filename + ext)));
        list.clear();
    }

    private void print(String filename, boolean flag, Consumer<BufferedWriter> consumer) {
        long start, end;
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
        } catch (IOException e) {
            raiseException(e, file.getName());
        }
    }

    private void raiseException(Exception e, String fileName) {
        FMLCommonHandler.instance().raiseException(e, String.format(NameWakander.MOD_NAME + ": %s に書き込みできません。", fileName), true);
    }

    public <T> Consumer<T> tryWithException(ThrowableConsumer<T> onTry, BiConsumer<Exception, T> onCatch) {
        return t -> {
            try {
                onTry.accept(t);
            } catch (Exception e) {
                onCatch.accept(e, t);
            }
        };
    }

    interface ThrowableConsumer<T> {
        void accept(T t) throws Exception;
    }
}
