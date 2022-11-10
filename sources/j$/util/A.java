package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.NoSuchElementException;
/* loaded from: classes2.dex */
class A implements InterfaceCLASSNAMEn, j$.util.function.f, Iterator {
    boolean a = false;
    double b;
    final /* synthetic */ t c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public A(t tVar) {
        this.c = tVar;
    }

    @Override // j$.util.function.f
    public void accept(double d) {
        this.a = true;
        this.b = d;
    }

    @Override // j$.util.p
    /* renamed from: e */
    public void forEachRemaining(j$.util.function.f fVar) {
        fVar.getClass();
        while (hasNext()) {
            fVar.accept(nextDouble());
        }
    }

    @Override // j$.util.InterfaceCLASSNAMEn, j$.util.Iterator
    public void forEachRemaining(Consumer consumer) {
        if (consumer instanceof j$.util.function.f) {
            forEachRemaining((j$.util.function.f) consumer);
            return;
        }
        consumer.getClass();
        if (!N.a) {
            forEachRemaining(new CLASSNAMEm(consumer));
        } else {
            N.a(A.class, "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
            throw null;
        }
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public boolean hasNext() {
        if (!this.a) {
            this.c.k(this);
        }
        return this.a;
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }

    @Override // j$.util.InterfaceCLASSNAMEn, java.util.Iterator, j$.util.Iterator
    /* renamed from: next */
    public Double moNUMnext() {
        if (!N.a) {
            return Double.valueOf(nextDouble());
        }
        N.a(A.class, "{0} calling PrimitiveIterator.OfDouble.nextLong()");
        throw null;
    }

    @Override // j$.util.InterfaceCLASSNAMEn
    public double nextDouble() {
        if (this.a || hasNext()) {
            this.a = false;
            return this.b;
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public /* synthetic */ void remove() {
        Iterator.CC.a(this);
        throw null;
    }
}
