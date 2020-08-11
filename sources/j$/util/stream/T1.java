package j$.util.stream;

import j$.util.Spliterator;

final class T1 extends CLASSNAMEi1 {
    private final N1 j;

    T1(N1 op, CLASSNAMEq4 helper, Spliterator spliterator) {
        super(helper, spliterator);
        this.j = op;
    }

    T1(T1 parent, Spliterator spliterator) {
        super((CLASSNAMEi1) parent, spliterator);
        this.j = parent.j;
    }

    /* access modifiers changed from: protected */
    /* renamed from: q */
    public T1 h(Spliterator spliterator) {
        return new T1(this, spliterator);
    }

    /* access modifiers changed from: protected */
    public Object m() {
        return this.j.c;
    }

    private void p(Object answer) {
        if (f()) {
            n(answer);
        } else {
            l();
        }
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEq4 q4Var = this.a;
        g7 g7Var = (g7) this.j.e.get();
        q4Var.t0(g7Var, this.b);
        O result = g7Var.get();
        if (!this.j.b) {
            if (result != null) {
                n(result);
            }
            return null;
        } else if (result == null) {
            return null;
        } else {
            p(result);
            return result;
        }
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCompletion(java.util.concurrent.CountedCompleter r5) {
        /*
            r4 = this;
            j$.util.stream.N1 r0 = r4.j
            boolean r0 = r0.b
            if (r0 == 0) goto L_0x002b
            j$.util.stream.k1 r0 = r4.d
            j$.util.stream.T1 r0 = (j$.util.stream.T1) r0
            r1 = 0
        L_0x000b:
            if (r0 == r1) goto L_0x002b
            java.lang.Object r2 = r0.b()
            if (r2 == 0) goto L_0x0024
            j$.util.stream.N1 r3 = r4.j
            j$.util.function.Predicate r3 = r3.d
            boolean r3 = r3.test(r2)
            if (r3 == 0) goto L_0x0024
            r4.i(r2)
            r4.p(r2)
            goto L_0x002b
        L_0x0024:
            r1 = r0
            j$.util.stream.k1 r2 = r4.e
            r0 = r2
            j$.util.stream.T1 r0 = (j$.util.stream.T1) r0
            goto L_0x000b
        L_0x002b:
            super.onCompletion(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.T1.onCompletion(java.util.concurrent.CountedCompleter):void");
    }
}
