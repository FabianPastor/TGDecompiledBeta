package j$.util.stream;

import j$.util.Spliterator;

abstract class T4 implements f7 {
    private final CLASSNAMEv6 a;

    public /* synthetic */ int a() {
        e7.a();
        return 0;
    }

    public abstract R4 b();

    T4(CLASSNAMEv6 shape) {
        this.a = shape;
    }

    public Object d(CLASSNAMEq4 helper, Spliterator spliterator) {
        return ((R4) helper.t0(b(), spliterator)).get();
    }

    public Object c(CLASSNAMEq4 helper, Spliterator spliterator) {
        return ((R4) new U4(this, helper, spliterator).invoke()).get();
    }
}
