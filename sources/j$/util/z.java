package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.NoSuchElementException;
/* loaded from: classes2.dex */
class z implements r, j$.util.function.q, Iterator {
    boolean a = false;
    long b;
    final /* synthetic */ v c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public z(v vVar) {
        this.c = vVar;
    }

    @Override // j$.util.function.q
    public void accept(long j) {
        this.a = true;
        this.b = j;
    }

    @Override // j$.util.p
    /* renamed from: d */
    public void forEachRemaining(j$.util.function.q qVar) {
        qVar.getClass();
        while (hasNext()) {
            qVar.accept(nextLong());
        }
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }

    @Override // j$.util.r, j$.util.Iterator
    public void forEachRemaining(Consumer consumer) {
        if (consumer instanceof j$.util.function.q) {
            forEachRemaining((j$.util.function.q) consumer);
            return;
        }
        consumer.getClass();
        if (!N.a) {
            forEachRemaining(new q(consumer));
        } else {
            N.a(z.class, "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
            throw null;
        }
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public boolean hasNext() {
        if (!this.a) {
            this.c.i(this);
        }
        return this.a;
    }

    @Override // j$.util.r, java.util.Iterator
    /* renamed from: next */
    public Long mo315next() {
        if (!N.a) {
            return Long.valueOf(nextLong());
        }
        N.a(z.class, "{0} calling PrimitiveIterator.OfLong.nextLong()");
        throw null;
    }

    @Override // j$.util.r
    public long nextLong() {
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
