package j$.util;

import java.util.Comparator;

public final /* synthetic */ class N {
    public static long b(Spliterator _this) {
        if ((_this.characteristics() & 64) == 0) {
            return -1;
        }
        return _this.estimateSize();
    }

    public static boolean c(Spliterator _this, int characteristics) {
        return (_this.characteristics() & characteristics) == characteristics;
    }

    public static Comparator a(Spliterator _this) {
        throw new IllegalStateException();
    }
}
