package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Deque;
import java.util.stream.Node;

final class Y3 extends Z3 {
    Y3(CLASSNAMEt3 curNode) {
        super(curNode);
    }

    public boolean a(Consumer consumer) {
        Node<T> leaf;
        if (!h()) {
            return false;
        }
        boolean hasNext = this.d.a(consumer);
        if (!hasNext) {
            if (this.c != null || (leaf = b(this.e)) == null) {
                this.a = null;
            } else {
                Spliterator spliterator = leaf.spliterator();
                this.d = spliterator;
                return spliterator.a(consumer);
            }
        }
        return hasNext;
    }

    public void forEachRemaining(Consumer consumer) {
        if (this.a != null) {
            if (this.d == null) {
                Spliterator spliterator = this.c;
                if (spliterator == null) {
                    Deque<Node<T>> stack = g();
                    while (true) {
                        CLASSNAMEt3 b = b(stack);
                        CLASSNAMEt3 t3Var = b;
                        if (b != null) {
                            t3Var.forEach(consumer);
                        } else {
                            this.a = null;
                            return;
                        }
                    }
                } else {
                    spliterator.forEachRemaining(consumer);
                }
            } else {
                do {
                } while (a(consumer));
            }
        }
    }
}
