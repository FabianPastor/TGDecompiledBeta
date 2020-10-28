package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;

abstract class R3 implements Spliterator {
    CLASSNAMEl3 a;
    int b;
    Spliterator c;
    Spliterator d;
    Deque e;

    R3(CLASSNAMEl3 l3Var) {
        this.a = l3Var;
    }

    /* access modifiers changed from: protected */
    public final CLASSNAMEl3 a(Deque deque) {
        while (true) {
            CLASSNAMEl3 l3Var = (CLASSNAMEl3) deque.pollFirst();
            if (l3Var == null) {
                return null;
            }
            if (l3Var.o() != 0) {
                for (int o = l3Var.o() - 1; o >= 0; o--) {
                    deque.addFirst(l3Var.b(o));
                }
            } else if (l3Var.count() > 0) {
                return l3Var;
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
        Spliterator spliterator = this.c;
        if (spliterator != null) {
            return spliterator.estimateSize();
        }
        for (int i = this.b; i < this.a.o(); i++) {
            j += this.a.b(i).count();
        }
        return j;
    }

    /* access modifiers changed from: protected */
    public final Deque f() {
        ArrayDeque arrayDeque = new ArrayDeque(8);
        int o = this.a.o();
        while (true) {
            o--;
            if (o < this.b) {
                return arrayDeque;
            }
            arrayDeque.addFirst(this.a.b(o));
        }
    }

    /* access modifiers changed from: protected */
    public final boolean g() {
        if (this.a == null) {
            return false;
        }
        if (this.d != null) {
            return true;
        }
        Spliterator spliterator = this.c;
        if (spliterator == null) {
            Deque f = f();
            this.e = f;
            CLASSNAMEl3 a2 = a(f);
            if (a2 != null) {
                spliterator = a2.spliterator();
            } else {
                this.a = null;
                return false;
            }
        }
        this.d = spliterator;
        return true;
    }

    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }

    public final Spliterator trySplit() {
        CLASSNAMEl3 l3Var = this.a;
        if (l3Var == null || this.d != null) {
            return null;
        }
        Spliterator spliterator = this.c;
        if (spliterator != null) {
            return spliterator.trySplit();
        }
        if (this.b < l3Var.o() - 1) {
            CLASSNAMEl3 l3Var2 = this.a;
            int i = this.b;
            this.b = i + 1;
            return l3Var2.b(i).spliterator();
        }
        CLASSNAMEl3 b2 = this.a.b(this.b);
        this.a = b2;
        if (b2.o() == 0) {
            Spliterator spliterator2 = this.a.spliterator();
            this.c = spliterator2;
            return spliterator2.trySplit();
        }
        this.b = 0;
        CLASSNAMEl3 l3Var3 = this.a;
        this.b = 1;
        return l3Var3.b(0).spliterator();
    }
}
