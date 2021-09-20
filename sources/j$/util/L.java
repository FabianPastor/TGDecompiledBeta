package j$.util;

import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

class L implements y {
    private final Collection a;
    private Iterator b = null;
    private final int c;
    private long d;
    private int e;

    public L(Collection collection, int i) {
        this.a = collection;
        this.c = (i & 4096) == 0 ? i | 64 | 16384 : i;
    }

    public boolean b(Consumer consumer) {
        consumer.getClass();
        if (this.b == null) {
            this.b = this.a.iterator();
            this.d = (long) this.a.size();
        }
        if (!this.b.hasNext()) {
            return false;
        }
        consumer.accept(this.b.next());
        return true;
    }

    public int characteristics() {
        return this.c;
    }

    public long estimateSize() {
        if (this.b != null) {
            return this.d;
        }
        this.b = this.a.iterator();
        long size = (long) this.a.size();
        this.d = size;
        return size;
    }

    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        Iterator it = this.b;
        if (it == null) {
            it = this.a.iterator();
            this.b = it;
            this.d = (long) this.a.size();
        }
        if (it instanceof Iterator) {
            ((Iterator) it).forEachRemaining(consumer);
        } else {
            Iterator.CC.$default$forEachRemaining(it, consumer);
        }
    }

    public Comparator getComparator() {
        if (CLASSNAMEa.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEa.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }

    public y trySplit() {
        long j;
        java.util.Iterator it = this.b;
        if (it == null) {
            it = this.a.iterator();
            this.b = it;
            j = (long) this.a.size();
            this.d = j;
        } else {
            j = this.d;
        }
        if (j <= 1 || !it.hasNext()) {
            return null;
        }
        int i = this.e + 1024;
        if (((long) i) > j) {
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
            if (i2 >= i || !it.hasNext()) {
                this.e = i2;
                long j2 = this.d;
            }
            objArr[i2] = it.next();
            i2++;
            break;
        } while (!it.hasNext());
        this.e = i2;
        long j22 = this.d;
        if (j22 != Long.MAX_VALUE) {
            this.d = j22 - ((long) i2);
        }
        return new D(objArr, 0, i2, this.c);
    }
}
