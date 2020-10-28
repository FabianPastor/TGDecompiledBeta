package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.q;
import java.util.NoSuchElementException;

class J implements x, q {
    boolean a = false;
    double b;
    final /* synthetic */ C c;

    J(C c2) {
        this.c = c2;
    }

    public void accept(double d) {
        this.a = true;
        this.b = d;
    }

    /* renamed from: e */
    public void forEachRemaining(q qVar) {
        qVar.getClass();
        while (hasNext()) {
            qVar.accept(nextDouble());
        }
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer instanceof q) {
            forEachRemaining((q) consumer);
            return;
        }
        consumer.getClass();
        if (!W.a) {
            forEachRemaining(new CLASSNAMEj(consumer));
        } else {
            W.a(J.class, "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
            throw null;
        }
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.o(this);
        }
        return this.a;
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }

    public Double next() {
        if (!W.a) {
            return Double.valueOf(nextDouble());
        }
        W.a(J.class, "{0} calling PrimitiveIterator.OfDouble.nextLong()");
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
