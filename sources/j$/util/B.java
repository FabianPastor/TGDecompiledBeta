package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.util.function.o;
import j$.util.function.p;
import java.util.NoSuchElementException;

class B implements r, p, Iterator {
    boolean a = false;
    long b;
    final /* synthetic */ w c;

    B(w wVar) {
        this.c = wVar;
    }

    public void accept(long j) {
        this.a = true;
        this.b = j;
    }

    /* renamed from: d */
    public void forEachRemaining(p pVar) {
        pVar.getClass();
        while (hasNext()) {
            pVar.accept(nextLong());
        }
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer instanceof p) {
            forEachRemaining((p) consumer);
            return;
        }
        consumer.getClass();
        if (!P.a) {
            forEachRemaining(new q(consumer));
        } else {
            P.a(B.class, "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
            throw null;
        }
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.i(this);
        }
        return this.a;
    }

    public Long next() {
        if (!P.a) {
            return Long.valueOf(nextLong());
        }
        P.a(B.class, "{0} calling PrimitiveIterator.OfLong.nextLong()");
        throw null;
    }

    public long nextLong() {
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
