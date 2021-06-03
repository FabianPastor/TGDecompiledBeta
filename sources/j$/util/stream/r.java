package j$.util.stream;

import j$.util.function.G;

public final /* synthetic */ class r implements G {
    public static final /* synthetic */ r a = new r();

    private /* synthetic */ r() {
    }

    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        CLASSNAMEn1.b(dArr, d);
        dArr[2] = dArr[2] + d;
    }
}
