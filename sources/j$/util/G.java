package j$.util;

import j$.CLASSNAMEz;
import j$.util.Iterator;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import java.util.Iterator;
import java.util.NoSuchElementException;

class G implements Iterator, Consumer, Iterator {
    boolean a = false;
    Object b;
    final /* synthetic */ Spliterator c;

    G(Spliterator spliterator) {
        this.c = spliterator;
    }

    public void accept(Object obj) {
        this.a = true;
        this.b = obj;
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, CLASSNAMEz.b(consumer));
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.b(this);
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

    public /* synthetic */ void remove() {
        Iterator.CC.a(this);
        throw null;
    }
}
