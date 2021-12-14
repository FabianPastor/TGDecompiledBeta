package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.y;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;

/* renamed from: j$.util.stream.l2  reason: case insensitive filesystem */
abstract class CLASSNAMEl2 implements y {
    B1 a;
    int b;
    y c;
    y d;
    Deque e;

    CLASSNAMEl2(B1 b1) {
        this.a = b1;
    }

    /* access modifiers changed from: protected */
    public final B1 a(Deque deque) {
        while (true) {
            B1 b1 = (B1) deque.pollFirst();
            if (b1 == null) {
                return null;
            }
            if (b1.p() != 0) {
                for (int p = b1.p() - 1; p >= 0; p--) {
                    deque.addFirst(b1.b(p));
                }
            } else if (b1.count() > 0) {
                return b1;
            }
        }
    }

    public final int characteristics() {
        return 64;
    }

    public final long estimateSize() {
        long j = 0;
        if (this.a == null) {
            return 0;
        }
        y yVar = this.c;
        if (yVar != null) {
            return yVar.estimateSize();
        }
        for (int i = this.b; i < this.a.p(); i++) {
            j += this.a.b(i).count();
        }
        return j;
    }

    /* access modifiers changed from: protected */
    public final Deque f() {
        ArrayDeque arrayDeque = new ArrayDeque(8);
        int p = this.a.p();
        while (true) {
            p--;
            if (p < this.b) {
                return arrayDeque;
            }
            arrayDeque.addFirst(this.a.b(p));
        }
    }

    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEa.e(this);
    }

    /* access modifiers changed from: protected */
    public final boolean h() {
        if (this.a == null) {
            return false;
        }
        if (this.d != null) {
            return true;
        }
        y yVar = this.c;
        if (yVar == null) {
            Deque f = f();
            this.e = f;
            B1 a2 = a(f);
            if (a2 != null) {
                yVar = a2.spliterator();
            } else {
                this.a = null;
                return false;
            }
        }
        this.d = yVar;
        return true;
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }

    public final y trySplit() {
        B1 b1 = this.a;
        if (b1 == null || this.d != null) {
            return null;
        }
        y yVar = this.c;
        if (yVar != null) {
            return yVar.trySplit();
        }
        if (this.b < b1.p() - 1) {
            B1 b12 = this.a;
            int i = this.b;
            this.b = i + 1;
            return b12.b(i).spliterator();
        }
        B1 b2 = this.a.b(this.b);
        this.a = b2;
        if (b2.p() == 0) {
            y spliterator = this.a.spliterator();
            this.c = spliterator;
            return spliterator.trySplit();
        }
        this.b = 0;
        B1 b13 = this.a;
        this.b = 1;
        return b13.b(0).spliterator();
    }
}
