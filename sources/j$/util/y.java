package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.p;
import j$.util.u;
import java.util.NoSuchElementException;

class y implements p.a, l, Iterator {
    boolean a = false;
    int b;
    final /* synthetic */ u.a c;

    y(u.a aVar) {
        this.c = aVar;
    }

    public void accept(int i) {
        this.a = true;
        this.b = i;
    }

    /* renamed from: c */
    public void forEachRemaining(l lVar) {
        lVar.getClass();
        while (hasNext()) {
            lVar.accept(nextInt());
        }
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer instanceof l) {
            forEachRemaining((l) consumer);
            return;
        }
        consumer.getClass();
        if (!N.a) {
            forEachRemaining(new o(consumer));
        } else {
            N.a(y.class, "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
            throw null;
        }
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.g(this);
        }
        return this.a;
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }

    public Integer next() {
        if (!N.a) {
            return Integer.valueOf(nextInt());
        }
        N.a(y.class, "{0} calling PrimitiveIterator.OfInt.nextInt()");
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
