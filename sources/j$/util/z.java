package j$.util;

import j$.util.function.Consumer;
import java.util.Iterator;
import java.util.NoSuchElementException;

class z implements Iterator, Consumer {
    boolean a = false;
    Object b;
    final /* synthetic */ y c;

    z(y yVar) {
        this.c = yVar;
    }

    public void accept(Object obj) {
        this.a = true;
        this.b = obj;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
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
}
