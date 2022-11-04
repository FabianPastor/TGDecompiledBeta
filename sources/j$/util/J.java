package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Collection;
import java.util.Comparator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class J implements u {
    private final Collection a;
    private java.util.Iterator b = null;
    private final int c;
    private long d;
    private int e;

    public J(Collection collection, int i) {
        this.a = collection;
        this.c = (i & 4096) == 0 ? i | 64 | 16384 : i;
    }

    @Override // j$.util.u
    public boolean b(Consumer consumer) {
        consumer.getClass();
        if (this.b == null) {
            this.b = this.a.iterator();
            this.d = this.a.size();
        }
        if (this.b.hasNext()) {
            consumer.accept(this.b.next());
            return true;
        }
        return false;
    }

    @Override // j$.util.u
    public int characteristics() {
        return this.c;
    }

    @Override // j$.util.u
    public long estimateSize() {
        if (this.b == null) {
            this.b = this.a.iterator();
            long size = this.a.size();
            this.d = size;
            return size;
        }
        return this.d;
    }

    @Override // j$.util.u
    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        java.util.Iterator it = this.b;
        if (it == null) {
            it = this.a.iterator();
            this.b = it;
            this.d = this.a.size();
        }
        if (it instanceof Iterator) {
            ((Iterator) it).forEachRemaining(consumer);
        } else {
            Iterator.CC.$default$forEachRemaining(it, consumer);
        }
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        if (AbstractCLASSNAMEa.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractCLASSNAMEa.e(this);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractCLASSNAMEa.f(this, i);
    }

    @Override // j$.util.u
    /* renamed from: trySplit */
    public u mo326trySplit() {
        long j;
        java.util.Iterator it = this.b;
        if (it == null) {
            it = this.a.iterator();
            this.b = it;
            j = this.a.size();
            this.d = j;
        } else {
            j = this.d;
        }
        if (j <= 1 || !it.hasNext()) {
            return null;
        }
        int i = this.e + 1024;
        if (i > j) {
            i = (int) j;
        }
        if (i > 33554432) {
            i = 33554432;
        }
        Object[] objArr = new Object[i];
        int i2 = 0;
        do {
            objArr[i2] = it.next();
            i2++;
            if (i2 >= i) {
                break;
            }
        } while (it.hasNext());
        this.e = i2;
        long j2 = this.d;
        if (j2 != Long.MAX_VALUE) {
            this.d = j2 - i2;
        }
        return new B(objArr, 0, i2, this.c);
    }
}
