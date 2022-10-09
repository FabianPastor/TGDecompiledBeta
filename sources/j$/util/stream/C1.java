package j$.util.stream;
/* loaded from: classes2.dex */
abstract class C1 implements A1 {
    protected final A1 a;
    protected final A1 b;
    private final long c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C1(A1 a1, A1 a12) {
        this.a = a1;
        this.b = a12;
        this.c = a1.count() + a12.count();
    }

    @Override // j$.util.stream.A1
    /* renamed from: b */
    public A1 mo288b(int i) {
        if (i == 0) {
            return this.a;
        }
        if (i != 1) {
            throw new IndexOutOfBoundsException();
        }
        return this.b;
    }

    @Override // j$.util.stream.A1
    /* renamed from: b  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ InterfaceCLASSNAMEz1 mo288b(int i) {
        return (InterfaceCLASSNAMEz1) mo288b(i);
    }

    @Override // j$.util.stream.A1
    public long count() {
        return this.c;
    }

    @Override // j$.util.stream.A1
    public int p() {
        return 2;
    }
}
