package namewakander;

import javax.annotation.Nonnull;

/**
 * ソート用クラス
 * Created by A.K. on 2016/07/15.
 */
public class IdNameObj<T extends Comparable<T>> implements Comparable<IdNameObj<T>> {
    T id;
    String name;
    public IdNameObj(T id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(@Nonnull IdNameObj<T> o) {
        return this.id.compareTo(o.id);
    }
}
