package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.util.function.j;
import j$.util.function.k;
import java.util.NoSuchElementException;

class A implements CLASSNAMEp, k, Iterator {
    boolean a = false;
    int b;
    final /* synthetic */ v c;

    A(v vVar) {
        this.c = vVar;
    }

    public void accept(int i) {
        this.a = true;
        this.b = i;
    }

    /* renamed from: c */
    public void forEachRemaining(k kVar) {
        kVar.getClass();
        while (hasNext()) {
            kVar.accept(nextInt());
        }
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer instanceof k) {
            forEachRemaining((k) consumer);
            return;
        }
        consumer.getClass();
        if (!P.a) {
            forEachRemaining(new CLASSNAMEo(consumer));
        } else {
            P.a(A.class, "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
            throw null;
        }
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.g(this);
        }
        return this.a;
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }

    public Integer next() {
        if (!P.a) {
            return Integer.valueOf(nextInt());
        }
        P.a(A.class, "{0} calling PrimitiveIterator.OfInt.nextInt()");
        throw null;
    }

    public int nextInt() {
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
