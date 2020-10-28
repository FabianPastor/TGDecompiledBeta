package j$.util.stream;

import j$.util.function.B;

public final /* synthetic */ class r implements B {
    public static final /* synthetic */ r a = new r();

    private /* synthetic */ r() {
    }

    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        CLASSNAMEo1.b(dArr, d);
        dArr[2] = dArr[2] + d;
    }
}
