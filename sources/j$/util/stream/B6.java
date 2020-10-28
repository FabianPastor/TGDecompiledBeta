package j$.util.stream;

import j$.util.CLASSNAMEw;
import j$.util.D;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.g;
import j$.util.function.u;

final class B6 extends D6 implements D, u {
    int e;

    B6(D d, long j, long j2) {
        super(d, j, j2);
    }

    B6(D d, B6 b6) {
        super(d, b6);
    }

    public void accept(int i) {
        this.e = i;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.e(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.b(this, consumer);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }

    /* access modifiers changed from: protected */
    public Spliterator r(Spliterator spliterator) {
        return new B6((D) spliterator, this);
    }

    /* access modifiers changed from: protected */
    public void t(Object obj) {
        ((u) obj).accept(this.e);
    }

    /* access modifiers changed from: protected */
    public n6 u(int i) {
        return new CLASSNAMEl6(i);
    }
}
