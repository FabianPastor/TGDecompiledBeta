package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.util.function.g;
import j$.util.function.u;
import java.util.NoSuchElementException;

class H implements y, u {
    boolean a = false;
    int b;
    final /* synthetic */ D c;

    H(D d) {
        this.c = d;
    }

    public void accept(int i) {
        this.a = true;
        this.b = i;
    }

    /* renamed from: c */
    public void forEachRemaining(u uVar) {
        uVar.getClass();
        while (hasNext()) {
            uVar.accept(nextInt());
        }
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer instanceof u) {
            forEachRemaining((u) consumer);
            return;
        }
        consumer.getClass();
        if (!W.a) {
            forEachRemaining(new CLASSNAMEh(consumer));
        } else {
            W.a(H.class, "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
            throw null;
        }
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.h(this);
        }
        return this.a;
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }

    public Integer next() {
        if (!W.a) {
            return Integer.valueOf(nextInt());
        }
        W.a(H.class, "{0} calling PrimitiveIterator.OfInt.nextInt()");
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
