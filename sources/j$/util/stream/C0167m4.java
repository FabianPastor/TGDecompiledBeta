package j$.util.stream;

/* renamed from: j$.util.stream.m4  reason: case insensitive filesystem */
class CLASSNAMEm4 extends CLASSNAMEo4 {
    private final Object c;

    private CLASSNAMEm4(CLASSNAMEs3 node, Object array, int offset) {
        super(node, offset);
        this.c = array;
    }

    private CLASSNAMEm4(CLASSNAMEm4 parent, CLASSNAMEs3 node, int offset) {
        super(parent, node, offset);
        this.c = parent.c;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: c */
    public CLASSNAMEm4 b(int childIndex, int offset) {
        return new CLASSNAMEm4(this, ((CLASSNAMEs3) this.a).d(childIndex), offset);
    }

    /* access modifiers changed from: package-private */
    public void a() {
        ((CLASSNAMEs3) this.a).f(this.c, this.b);
    }
}
