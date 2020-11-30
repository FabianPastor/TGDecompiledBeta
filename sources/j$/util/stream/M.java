package j$.util.stream;

import j$.util.function.H;

public final /* synthetic */ class M implements H {
    public static final /* synthetic */ M a = new M();

    private /* synthetic */ M() {
    }

    public final void accept(Object obj, int i) {
        long[] jArr = (long[]) obj;
        jArr[0] = jArr[0] + 1;
        jArr[1] = jArr[1] + ((long) i);
    }
}
