package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiConsumer;

public final /* synthetic */ class R0 implements BiConsumer {
    public static final /* synthetic */ R0 a = new R0();

    private /* synthetic */ R0() {
    }

    public final void accept(Object obj, Object obj2) {
        long[] jArr = (long[]) obj;
        long[] jArr2 = (long[]) obj2;
        jArr[0] = jArr[0] + jArr2[0];
        jArr[1] = jArr[1] + jArr2[1];
    }

    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new a((BiConsumer) this, biConsumer);
    }
}
