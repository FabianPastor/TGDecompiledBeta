package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;

final class o6 extends p6 implements Consumer {
    final Object[] b;

    o6(int i) {
        this.b = new Object[i];
    }

    public void accept(Object obj) {
        Object[] objArr = this.b;
        int i = this.a;
        this.a = i + 1;
        objArr[i] = obj;
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
