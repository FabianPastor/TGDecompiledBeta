package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.V;

/* renamed from: j$.util.stream.d3  reason: case insensitive filesystem */
final class CLASSNAMEd3 implements f7 {
    private final CLASSNAMEv6 a;
    final CLASSNAMEc3 b;
    final V c;

    CLASSNAMEd3(CLASSNAMEv6 shape, CLASSNAMEc3 matchKind, V v) {
        this.a = shape;
        this.b = matchKind;
        this.c = v;
    }

    public int a() {
        return CLASSNAMEu6.z | CLASSNAMEu6.w;
    }

    /* renamed from: e */
    public Boolean d(CLASSNAMEq4 helper, Spliterator spliterator) {
        return Boolean.valueOf(((CLASSNAMEb3) helper.t0((CLASSNAMEb3) this.c.get(), spliterator)).a());
    }

    /* renamed from: b */
    public Boolean c(CLASSNAMEq4 helper, Spliterator spliterator) {
        return (Boolean) new CLASSNAMEe3(this, helper, spliterator).invoke();
    }
}
