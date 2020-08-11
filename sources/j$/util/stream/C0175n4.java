package j$.util.stream;

/* renamed from: j$.util.stream.n4  reason: case insensitive filesystem */
final class CLASSNAMEn4 extends CLASSNAMEo4 {
    private final Object[] c;

    private CLASSNAMEn4(CLASSNAMEt3 node, Object[] array, int offset) {
        super(node, offset);
        this.c = array;
    }

    private CLASSNAMEn4(CLASSNAMEn4 parent, CLASSNAMEt3 node, int offset) {
        super(parent, node, offset);
        this.c = parent.c;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: c */
    public CLASSNAMEn4 b(int childIndex, int offset) {
        return new CLASSNAMEn4(this, this.a.d(childIndex), offset);
    }

    /* access modifiers changed from: package-private */
    public void a() {
        this.a.m(this.c, this.b);
    }
}
