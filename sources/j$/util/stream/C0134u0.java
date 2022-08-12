package j$.util.stream;

import j$.util.function.v;

/* renamed from: j$.util.stream.u0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEu0 implements v {
    public static final /* synthetic */ CLASSNAMEu0 a = new CLASSNAMEu0();

    private /* synthetic */ CLASSNAMEu0() {
    }

    public final void accept(Object obj, int i) {
        long[] jArr = (long[]) obj;
        jArr[0] = jArr[0] + 1;
        jArr[1] = jArr[1] + ((long) i);
    }
}
