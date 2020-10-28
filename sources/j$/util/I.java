package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.util.function.h;
import j$.util.function.y;
import java.util.NoSuchElementException;

class I implements z, y {
    boolean a = false;
    long b;
    final /* synthetic */ E c;

    I(E e) {
        this.c = e;
    }

    public void accept(long j) {
        this.a = true;
        this.b = j;
    }

    /* renamed from: d */
    public void forEachRemaining(y yVar) {
        yVar.getClass();
        while (hasNext()) {
            yVar.accept(nextLong());
        }
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer instanceof y) {
            forEachRemaining((y) consumer);
            return;
        }
        consumer.getClass();
        if (!W.a) {
            forEachRemaining(new CLASSNAMEg(consumer));
        } else {
            W.a(I.class, "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
            throw null;
        }
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.j(this);
        }
        return this.a;
    }

    public Long next() {
        if (!W.a) {
            return Long.valueOf(nextLong());
        }
        W.a(I.class, "{0} calling PrimitiveIterator.OfLong.nextLong()");
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
