package j$.util.stream;

import j$.util.function.w;

public final /* synthetic */ class O0 implements w {
    public static final /* synthetic */ O0 a = new O0();

    private /* synthetic */ O0() {
    }

    public final void accept(Object obj, long j) {
        long[] jArr = (long[]) obj;
        jArr[0] = jArr[0] + 1;
        jArr[1] = jArr[1] + j;
    }
}
