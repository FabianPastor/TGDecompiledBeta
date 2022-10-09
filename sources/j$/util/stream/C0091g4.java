package j$.util.stream;
/* renamed from: j$.util.stream.g4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEg4 extends AbstractCLASSNAMEj4 implements j$.util.function.f {
    final double[] c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEg4(int i) {
        this.c = new double[i];
    }

    @Override // j$.util.function.f
    public void accept(double d) {
        double[] dArr = this.c;
        int i = this.b;
        this.b = i + 1;
        dArr[i] = d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEj4
    public void b(Object obj, long j) {
        j$.util.function.f fVar = (j$.util.function.f) obj;
        for (int i = 0; i < j; i++) {
            fVar.accept(this.c[i]);
        }
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
