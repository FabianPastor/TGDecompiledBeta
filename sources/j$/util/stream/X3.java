package j$.util.stream;

import j$.util.Spliterator;
import j$.util.V;
import java.util.Deque;

abstract class X3 extends Z3 implements V {
    public /* bridge */ /* synthetic */ V trySplit() {
        return (V) super.trySplit();
    }

    X3(CLASSNAMEs3 cur) {
        super(cur);
    }

    public boolean tryAdvance(Object consumer) {
        N leaf;
        if (!h()) {
            return false;
        }
        boolean hasNext = ((V) this.d).tryAdvance(consumer);
        if (!hasNext) {
            if (this.c != null || (leaf = (CLASSNAMEs3) b(this.e)) == null) {
                this.a = null;
            } else {
                V spliterator = leaf.spliterator();
                this.d = spliterator;
                return spliterator.tryAdvance(consumer);
            }
        }
        return hasNext;
    }

    public void forEachRemaining(Object consumer) {
        if (this.a != null) {
            if (this.d == null) {
                Spliterator spliterator = this.c;
                if (spliterator == null) {
                    Deque<N> stack = g();
                    while (true) {
                        N n = (CLASSNAMEs3) b(stack);
                        N leaf = n;
                        if (n != null) {
                            leaf.j(consumer);
                        } else {
                            this.a = null;
                            return;
                        }
                    }
                } else {
                    ((V) spliterator).forEachRemaining(consumer);
                }
            } else {
                do {
                } while (tryAdvance(consumer));
            }
        }
    }
}
