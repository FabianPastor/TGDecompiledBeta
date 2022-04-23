package j$.util.stream;

import j$.util.function.Consumer;

/* renamed from: j$.util.stream.l4  reason: case insensitive filesystem */
final class CLASSNAMEl4 extends CLASSNAMEm4 implements Consumer {
    final Object[] b;

    CLASSNAMEl4(int i) {
        this.b = new Object[i];
    }

    public void accept(Object obj) {
        Object[] objArr = this.b;
        int i = this.a;
        this.a = i + 1;
        objArr[i] = obj;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
