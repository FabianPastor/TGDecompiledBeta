package j$.util;

import j$.util.Iterator;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import java.util.NoSuchElementException;

class Z implements F, CLASSNAMEt {
    boolean a = false;
    double b;
    final /* synthetic */ P c;

    public /* synthetic */ void e(CLASSNAMEt tVar) {
        E.b(this, tVar);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        E.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(Object obj) {
        ((Z) this).e((CLASSNAMEt) obj);
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    public /* synthetic */ void remove() {
        Iterator.CC.a(this);
        throw null;
    }

    Z(P p) {
        this.c = p;
    }

    public void accept(double t) {
        this.a = true;
        this.b = t;
    }

    public boolean hasNext() {
        if (!this.a) {
            this.c.j(this);
        }
        return this.a;
    }

    public double nextDouble() {
        if (this.a || hasNext()) {
            this.a = false;
            return this.b;
        }
        throw new NoSuchElementException();
    }
}
