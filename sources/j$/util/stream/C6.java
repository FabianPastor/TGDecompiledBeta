package j$.util.stream;

import j$.util.CLASSNAMEw;
import j$.util.E;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.h;
import j$.util.function.y;

final class C6 extends D6 implements E, y {
    long e;

    C6(E e2, long j, long j2) {
        super(e2, j, j2);
    }

    C6(E e2, C6 c6) {
        super(e2, c6);
    }

    public void accept(long j) {
        this.e = j;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.f(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.c(this, consumer);
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }

    /* access modifiers changed from: protected */
    public Spliterator r(Spliterator spliterator) {
        return new C6((E) spliterator, this);
    }

    /* access modifiers changed from: protected */
    public void t(Object obj) {
        ((y) obj).accept(this.e);
    }

    /* access modifiers changed from: protected */
    public n6 u(int i) {
        return new m6(i);
    }
}
