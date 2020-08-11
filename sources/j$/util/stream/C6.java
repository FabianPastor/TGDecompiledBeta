package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

final class C6 extends D6 implements Consumer {
    final Object[] b;

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    C6(int size) {
        this.b = new Object[size];
    }

    public void accept(Object t) {
        Object[] objArr = this.b;
        int i = this.a;
        this.a = i + 1;
        objArr[i] = t;
    }

    public void c(Consumer consumer, long fence) {
        for (int i = 0; ((long) i) < fence; i++) {
            consumer.accept(this.b[i]);
        }
    }
}
