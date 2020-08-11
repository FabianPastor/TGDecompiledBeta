package j$.util.stream;

import j$.util.N;
import j$.util.Spliterator;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;

abstract class Z3 implements Spliterator {
    CLASSNAMEt3 a;
    int b;
    Spliterator c;
    Spliterator d;
    Deque e;

    public /* synthetic */ Comparator getComparator() {
        N.a(this);
        throw null;
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    Z3(CLASSNAMEt3 curNode) {
        this.a = curNode;
    }

    /* access modifiers changed from: protected */
    public final Deque g() {
        Deque<N> stack = new ArrayDeque<>(8);
        int i = this.a.w();
        while (true) {
            i--;
            if (i < this.b) {
                return stack;
            }
            stack.addFirst(this.a.d(i));
        }
    }

    /* access modifiers changed from: protected */
    public final CLASSNAMEt3 b(Deque stack) {
        while (true) {
            N n = (CLASSNAMEt3) stack.pollFirst();
            N n2 = n;
            if (n == null) {
                return null;
            }
            if (n2.w() != 0) {
                for (int i = n2.w() - 1; i >= 0; i--) {
                    stack.addFirst(n2.d(i));
                }
            } else if (n2.count() > 0) {
                return n2;
            }
        }
    }

    /* access modifiers changed from: protected */
    public final boolean h() {
        if (this.a == null) {
            return false;
        }
        if (this.d != null) {
            return true;
        }
        N leaf = this.c;
        if (leaf == null) {
            Deque g = g();
            this.e = g;
            N leaf2 = b(g);
            if (leaf2 != null) {
                this.d = leaf2.spliterator();
                return true;
            }
            this.a = null;
            return false;
        }
        this.d = leaf;
        return true;
    }

    public final Spliterator trySplit() {
        CLASSNAMEt3 t3Var = this.a;
        if (t3Var == null || this.d != null) {
            return null;
        }
        Spliterator spliterator = this.c;
        if (spliterator != null) {
            return spliterator.trySplit();
        }
        if (this.b < t3Var.w() - 1) {
            CLASSNAMEt3 t3Var2 = this.a;
            int i = this.b;
            this.b = i + 1;
            return t3Var2.d(i).spliterator();
        }
        CLASSNAMEt3 d2 = this.a.d(this.b);
        this.a = d2;
        if (d2.w() == 0) {
            Spliterator spliterator2 = this.a.spliterator();
            this.c = spliterator2;
            return spliterator2.trySplit();
        }
        this.b = 0;
        CLASSNAMEt3 t3Var3 = this.a;
        this.b = 1;
        return t3Var3.d(0).spliterator();
    }

    public final long estimateSize() {
        if (this.a == null) {
            return 0;
        }
        Spliterator spliterator = this.c;
        if (spliterator != null) {
            return spliterator.estimateSize();
        }
        long size = 0;
        for (int i = this.b; i < this.a.w(); i++) {
            size += this.a.d(i).count();
        }
        return size;
    }

    public final int characteristics() {
        return 64;
    }
}
