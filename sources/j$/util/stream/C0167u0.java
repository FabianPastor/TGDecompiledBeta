package j$.util.stream;
/* renamed from: j$.util.stream.u0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEu0 implements j$.util.function.v {
    public static final /* synthetic */ CLASSNAMEu0 a = new CLASSNAMEu0();

    private /* synthetic */ CLASSNAMEu0() {
    }

    @Override // j$.util.function.v
    public final void accept(Object obj, int i) {
        long[] jArr = (long[]) obj;
        jArr[0] = jArr[0] + 1;
        jArr[1] = jArr[1] + i;
    }
}
