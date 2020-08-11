package j$.util;

import j$.r;
import j$.util.Iterator;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import java.util.Iterator;
import java.util.NoSuchElementException;

class W implements Iterator, Consumer, Iterator {
    boolean a = false;
    Object b;
    final /* synthetic */ Spliterator c;

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
        forEachRemaining(r.a(consumer));
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ void remove() {
        Iterator.CC.a(this);
        throw null;
    }

    W(Spliterator spliterator) {
        this.c = spliterator;
    }

    public void accept(Object t) {
        this.a = true;
        this.b = t;
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.a(this);
        }
        return this.a;
    }

    public Object next() {
        if (this.a || hasNext()) {
            this.a = false;
            return this.b;
        }
        throw new NoSuchElementException();
    }
}
