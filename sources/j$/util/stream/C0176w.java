package j$.util.stream;
/* renamed from: j$.util.stream.w  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEw implements j$.util.function.u {
    public static final /* synthetic */ CLASSNAMEw a = new CLASSNAMEw();

    private /* synthetic */ CLASSNAMEw() {
    }

    @Override // j$.util.function.u
    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        AbstractCLASSNAMEl.b(dArr, d);
        dArr[2] = dArr[2] + d;
    }
}
