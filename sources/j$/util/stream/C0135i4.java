package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.i4  reason: case insensitive filesystem */
final class CLASSNAMEi4 extends CLASSNAMEr6 implements CLASSNAMEt3, CLASSNAMEk3 {
    private boolean g = false;

    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* synthetic */ CLASSNAMEt3 c(long j, long j2, C c) {
        return CLASSNAMEg3.d(this, j, j2, c);
    }

    public /* synthetic */ CLASSNAMEt3 d(int i) {
        CLASSNAMEg3.a(this);
        throw null;
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    public /* synthetic */ int w() {
        CLASSNAMEg3.b();
        return 0;
    }

    CLASSNAMEi4() {
    }

    public Spliterator spliterator() {
        return super.spliterator();
    }

    public void forEach(Consumer consumer) {
        super.forEach(consumer);
    }

    public void s(long size) {
        this.g = true;
        clear();
        A(size);
    }

    public void accept(Object t) {
        super.accept(t);
    }

    public void r() {
        this.g = false;
    }

    public void m(Object[] array, int offset) {
        super.m(array, offset);
    }

    public Object[] x(C c) {
        return super.x(c);
    }

    public CLASSNAMEt3 b() {
        return this;
    }
}
