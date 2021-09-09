package j$.util.stream;

import j$.util.function.u;

/* renamed from: j$.util.stream.v  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv implements u {
    public static final /* synthetic */ CLASSNAMEv a = new CLASSNAMEv();

    private /* synthetic */ CLASSNAMEv() {
    }

    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        dArr[2] = dArr[2] + 1.0d;
        CLASSNAMEl.b(dArr, d);
        dArr[3] = dArr[3] + d;
    }
}
