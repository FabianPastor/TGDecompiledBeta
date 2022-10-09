package j$.util.stream;
/* renamed from: j$.util.stream.v  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEv implements j$.util.function.u {
    public static final /* synthetic */ CLASSNAMEv a = new CLASSNAMEv();

    private /* synthetic */ CLASSNAMEv() {
    }

    @Override // j$.util.function.u
    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        dArr[2] = dArr[2] + 1.0d;
        AbstractCLASSNAMEl.b(dArr, d);
        dArr[3] = dArr[3] + d;
    }
}
