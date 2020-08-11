package j$.util;

import j$.util.function.Consumer;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

class i0 implements Spliterator {
    private final Collection a;
    private Iterator b;
    private final int c;
    private long d;
    private int e;

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    public i0(Collection collection, int characteristics) {
        int i;
        this.a = collection;
        this.b = null;
        if ((characteristics & 4096) == 0) {
            i = characteristics | 64 | 16384;
        } else {
            i = characteristics;
        }
        this.c = i;
    }

    public i0(Iterator iterator, int characteristics) {
        this.a = null;
        this.b = iterator;
        this.d = Long.MAX_VALUE;
        this.c = characteristics & -16449;
    }

    public Spliterator trySplit() {
        long s;
        Iterator<? extends T> it = this.b;
        Iterator<? extends T> i = it;
        if (it == null) {
            Iterator<? extends T> it2 = this.a.iterator();
            this.b = it2;
            i = it2;
            s = (long) this.a.size();
            this.d = s;
        } else {
            s = this.d;
        }
        if (s <= 1 || !i.hasNext()) {
            return null;
        }
        int n = this.e + 1024;
        if (((long) n) > s) {
            n = (int) s;
        }
        if (n > 33554432) {
            n = 33554432;
        }
        Object[] a2 = new Object[n];
        int j = 0;
        do {
            a2[j] = i.next();
            j++;
            if (j >= n || !i.hasNext()) {
                this.e = j;
                long j2 = this.d;
            }
            a2[j] = i.next();
            j++;
            break;
        } while (!i.hasNext());
        this.e = j;
        long j22 = this.d;
        if (j22 != Long.MAX_VALUE) {
            this.d = j22 - ((long) j);
        }
        return new a0(a2, 0, j, this.c);
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer != null) {
            Iterator<? extends T> it = this.b;
            Iterator<? extends T> i = it;
            if (it == null) {
                Iterator<? extends T> it2 = this.a.iterator();
                this.b = it2;
                i = it2;
                this.d = (long) this.a.size();
            }
            CLASSNAMEu.a(i, consumer);
            return;
        }
        throw null;
    }

    public boolean a(Consumer consumer) {
        if (consumer != null) {
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
        throw null;
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

    public int characteristics() {
        return this.c;
    }

    public Comparator getComparator() {
        if (hasCharacteristics(4)) {
            return null;
        }
        throw new IllegalStateException();
    }
}
