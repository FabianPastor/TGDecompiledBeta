package j$.util.stream;

import j$.util.function.I;

public final /* synthetic */ class P implements I {
    public static final /* synthetic */ P a = new P();

    private /* synthetic */ P() {
    }

    public final void accept(Object obj, long j) {
        long[] jArr = (long[]) obj;
        jArr[0] = jArr[0] + 1;
        jArr[1] = jArr[1] + j;
    }
}
