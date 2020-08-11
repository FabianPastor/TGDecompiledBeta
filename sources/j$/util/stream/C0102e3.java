package j$.util.stream;

import j$.util.Spliterator;

/* renamed from: j$.util.stream.e3  reason: case insensitive filesystem */
final class CLASSNAMEe3 extends CLASSNAMEi1 {
    private final CLASSNAMEd3 j;

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ Object a() {
        p();
        return null;
    }

    CLASSNAMEe3(CLASSNAMEd3 op, CLASSNAMEq4 helper, Spliterator spliterator) {
        super(helper, spliterator);
        this.j = op;
    }

    CLASSNAMEe3(CLASSNAMEe3 parent, Spliterator spliterator) {
        super((CLASSNAMEi1) parent, spliterator);
        this.j = parent.j;
    }

    /* access modifiers changed from: protected */
    /* renamed from: r */
    public CLASSNAMEe3 h(Spliterator spliterator) {
        return new CLASSNAMEe3(this, spliterator);
    }

    /* access modifiers changed from: protected */
    public Boolean p() {
        CLASSNAMEq4 q4Var = this.a;
        CLASSNAMEb3 b3Var = (CLASSNAMEb3) this.j.c.get();
        q4Var.t0(b3Var, this.b);
        boolean b = b3Var.a();
        if (b != this.j.b.b) {
            return null;
        }
        n(Boolean.valueOf(b));
        return null;
    }

    /* access modifiers changed from: protected */
    /* renamed from: q */
    public Boolean m() {
        return Boolean.valueOf(!this.j.b.b);
    }
}
