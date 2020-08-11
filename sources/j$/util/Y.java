package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;
import java.util.NoSuchElementException;

class Y implements J, J {
    boolean a = false;
    long b;
    final /* synthetic */ U c;

    public /* synthetic */ void d(J j) {
        I.b(this, j);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        I.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
        ((Y) this).d((J) obj);
    }

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    public /* synthetic */ void remove() {
        Iterator.CC.a(this);
        throw null;
    }

    Y(U u) {
        this.c = u;
    }

    public void accept(long t) {
        this.a = true;
        this.b = t;
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.i(this);
        }
        return this.a;
    }

    public long nextLong() {
        if (this.a || hasNext()) {
            this.a = false;
            return this.b;
        }
        throw new NoSuchElementException();
    }
}
