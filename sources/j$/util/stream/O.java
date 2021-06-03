package j$.util.stream;

import j$.util.function.I;

public final /* synthetic */ class O implements I {
    public static final /* synthetic */ O a = new O();

    private /* synthetic */ O() {
    }

    public final void accept(Object obj, long j) {
        long[] jArr = (long[]) obj;
        jArr[0] = jArr[0] + 1;
        jArr[1] = jArr[1] + j;
    }
}
