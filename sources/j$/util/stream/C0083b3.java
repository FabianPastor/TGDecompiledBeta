package j$.util.stream;

import j$.util.Spliterator;

/* renamed from: j$.util.stream.b3  reason: case insensitive filesystem */
final class CLASSNAMEb3 extends CLASSNAMEi1 {
    private final CLASSNAMEa3 j;

    CLASSNAMEb3(CLASSNAMEa3 a3Var, CLASSNAMEi4 i4Var, Spliterator spliterator) {
        super(i4Var, spliterator);
        this.j = a3Var;
    }

    CLASSNAMEb3(CLASSNAMEb3 b3Var, Spliterator spliterator) {
        super((CLASSNAMEi1) b3Var, spliterator);
        this.j = b3Var.j;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEi4 i4Var = this.a;
        Y2 y2 = (Y2) this.j.c.get();
        i4Var.t0(y2, this.b);
        boolean z = y2.b;
        if (z != this.j.b.b) {
            return null;
        }
        l(Boolean.valueOf(z));
        return null;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk1 f(Spliterator spliterator) {
        return new CLASSNAMEb3(this, spliterator);
    }

    /* access modifiers changed from: protected */
    public Object k() {
        return Boolean.valueOf(!this.j.b.b);
    }
}
