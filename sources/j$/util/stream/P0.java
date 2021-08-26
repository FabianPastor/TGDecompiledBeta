package j$.util.stream;

import j$.util.function.v;

public final /* synthetic */ class P0 implements v {
    public static final /* synthetic */ P0 a = new P0();

    private /* synthetic */ P0() {
    }

    public final void accept(Object obj, long j) {
        long[] jArr = (long[]) obj;
        jArr[0] = jArr[0] + 1;
        jArr[1] = jArr[1] + j;
    }
}
