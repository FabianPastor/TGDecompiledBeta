package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiConsumer;

/* renamed from: j$.util.stream.x0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEx0 implements BiConsumer {
    public static final /* synthetic */ CLASSNAMEx0 a = new CLASSNAMEx0();

    private /* synthetic */ CLASSNAMEx0() {
    }

    public final void accept(Object obj, Object obj2) {
        long[] jArr = (long[]) obj;
        long[] jArr2 = (long[]) obj2;
        jArr[0] = jArr[0] + jArr2[0];
        jArr[1] = jArr[1] + jArr2[1];
    }

    public BiConsumer c(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new a((BiConsumer) this, biConsumer);
    }
}
