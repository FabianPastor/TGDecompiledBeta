package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Predicate;
import j$.util.function.V;

final class N1 implements f7 {
    private final CLASSNAMEv6 a;
    final boolean b;
    final Object c;
    final Predicate d;
    final V e;

    N1(boolean mustFindFirst, CLASSNAMEv6 shape, Object emptyValue, Predicate predicate, V v) {
        this.b = mustFindFirst;
        this.a = shape;
        this.c = emptyValue;
        this.d = predicate;
        this.e = v;
    }

    public int a() {
        return CLASSNAMEu6.z | (this.b ? 0 : CLASSNAMEu6.w);
    }

    public Object d(CLASSNAMEq4 helper, Spliterator spliterator) {
        O result = ((g7) helper.t0((g7) this.e.get(), spliterator)).get();
        return result != null ? result : this.c;
    }

    public Object c(CLASSNAMEq4 helper, Spliterator spliterator) {
        return new T1(this, helper, spliterator).invoke();
    }
}
