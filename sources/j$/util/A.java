package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;
import java.util.NoSuchElementException;

class A implements CLASSNAMEn, f, Iterator {
    boolean a = false;
    double b;
    final /* synthetic */ t c;

    A(t tVar) {
        this.c = tVar;
    }

    public void accept(double d) {
        this.a = true;
        this.b = d;
    }

    /* renamed from: e */
    public void forEachRemaining(f fVar) {
        fVar.getClass();
        while (hasNext()) {
            fVar.accept(nextDouble());
        }
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer instanceof f) {
            forEachRemaining((f) consumer);
            return;
        }
        consumer.getClass();
        if (!N.a) {
            forEachRemaining(new CLASSNAMEm(consumer));
        } else {
            N.a(A.class, "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
            throw null;
        }
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.k(this);
        }
        return this.a;
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }

    public Double next() {
        if (!N.a) {
            return Double.valueOf(nextDouble());
        }
        N.a(A.class, "{0} calling PrimitiveIterator.OfDouble.nextLong()");
        throw null;
    }

    public double nextDouble() {
        if (this.a || hasNext()) {
            this.a = false;
            return this.b;
        }
        throw new NoSuchElementException();
    }

    public /* synthetic */ void remove() {
        Iterator.CC.a(this);
        throw null;
    }
}
