package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.u;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;

/* renamed from: j$.util.stream.k2  reason: case insensitive filesystem */
abstract class CLASSNAMEk2 implements u {
    A1 a;
    int b;
    u c;
    u d;
    Deque e;

    CLASSNAMEk2(A1 a1) {
        this.a = a1;
    }

    /* access modifiers changed from: protected */
    public final A1 a(Deque deque) {
        while (true) {
            A1 a1 = (A1) deque.pollFirst();
            if (a1 == null) {
                return null;
            }
            if (a1.p() != 0) {
                for (int p = a1.p() - 1; p >= 0; p--) {
                    deque.addFirst(a1.b(p));
                }
            } else if (a1.count() > 0) {
                return a1;
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
        u uVar = this.c;
        if (uVar != null) {
            return uVar.estimateSize();
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
        u uVar = this.c;
        if (uVar == null) {
            Deque f = f();
            this.e = f;
            A1 a2 = a(f);
            if (a2 != null) {
                uVar = a2.spliterator();
            } else {
                this.a = null;
                return false;
            }
        }
        this.d = uVar;
        return true;
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }

    public final u trySplit() {
        A1 a1 = this.a;
        if (a1 == null || this.d != null) {
            return null;
        }
        u uVar = this.c;
        if (uVar != null) {
            return uVar.trySplit();
        }
        if (this.b < a1.p() - 1) {
            A1 a12 = this.a;
            int i = this.b;
            this.b = i + 1;
            return a12.b(i).spliterator();
        }
        A1 b2 = this.a.b(this.b);
        this.a = b2;
        if (b2.p() == 0) {
            u spliterator = this.a.spliterator();
            this.c = spliterator;
            return spliterator.trySplit();
        }
        this.b = 0;
        A1 a13 = this.a;
        this.b = 1;
        return a13.b(0).spliterator();
    }
}
