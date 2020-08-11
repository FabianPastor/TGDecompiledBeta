package j$.util;

import j$.util.Iterator;
import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.Consumer;
import java.util.NoSuchElementException;

class X implements H, B {
    boolean a = false;
    int b;
    final /* synthetic */ S c;

    public /* synthetic */ void c(B b2) {
        G.b(this, b2);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        G.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
        ((X) this).c((B) obj);
    }

    public /* synthetic */ B q(B b2) {
        return A.a(this, b2);
    }

    public /* synthetic */ void remove() {
        Iterator.CC.a(this);
        throw null;
    }

    X(S s) {
        this.c = s;
    }

    public void accept(int t) {
        this.a = true;
        this.b = t;
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.f(this);
        }
        return this.a;
    }

    public int nextInt() {
        if (this.a || hasNext()) {
            this.a = false;
            return this.b;
        }
        throw new NoSuchElementException();
    }
}
