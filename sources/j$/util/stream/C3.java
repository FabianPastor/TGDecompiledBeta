package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.CLASSNAMEo;
import j$.util.function.K;
import java.util.concurrent.CountedCompleter;

class C3 extends CLASSNAMEk1 {
    protected final CLASSNAMEq4 h;
    protected final K i;
    protected final CLASSNAMEo j;

    C3(CLASSNAMEq4 helper, Spliterator spliterator, K k, CLASSNAMEo oVar) {
        super(helper, spliterator);
        this.h = helper;
        this.i = k;
        this.j = oVar;
    }

    C3(C3 parent, Spliterator spliterator) {
        super((CLASSNAMEk1) parent, spliterator);
        this.h = parent.h;
        this.i = parent.i;
        this.j = parent.j;
    }

    /* access modifiers changed from: protected */
    /* renamed from: l */
    public C3 h(Spliterator spliterator) {
        return new C3(this, spliterator);
    }

    /* access modifiers changed from: protected */
    /* renamed from: k */
    public CLASSNAMEt3 a() {
        T_BUILDER builder = (CLASSNAMEk3) this.i.a(this.h.p0(this.b));
        this.h.t0(builder, this.b);
        return ((CLASSNAMEk3) builder).b();
    }

    public void onCompletion(CountedCompleter caller) {
        if (!e()) {
            i((CLASSNAMEt3) this.j.a((CLASSNAMEt3) ((C3) this.d).b(), (CLASSNAMEt3) ((C3) this.e).b()));
        }
        super.onCompletion(caller);
    }
}
