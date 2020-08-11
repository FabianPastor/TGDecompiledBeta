package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;

final class U4 extends CLASSNAMEk1 {
    private final T4 h;

    U4(T4 op, CLASSNAMEq4 helper, Spliterator spliterator) {
        super(helper, spliterator);
        this.h = op;
    }

    U4(U4 parent, Spliterator spliterator) {
        super((CLASSNAMEk1) parent, spliterator);
        this.h = parent.h;
    }

    /* access modifiers changed from: protected */
    /* renamed from: l */
    public U4 h(Spliterator spliterator) {
        return new U4(this, spliterator);
    }

    /* access modifiers changed from: protected */
    /* renamed from: k */
    public R4 a() {
        CLASSNAMEq4 q4Var = this.a;
        R4 b = this.h.b();
        q4Var.t0(b, this.b);
        return b;
    }

    public void onCompletion(CountedCompleter caller) {
        if (!e()) {
            S leftResult = (R4) ((U4) this.d).b();
            leftResult.l((R4) ((U4) this.e).b());
            i(leftResult);
        }
        super.onCompletion(caller);
    }
}
