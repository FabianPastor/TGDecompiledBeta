package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Deque;

final class Q3 extends R3 {
    Q3(CLASSNAMEl3 l3Var) {
        super(l3Var);
    }

    public boolean b(Consumer consumer) {
        CLASSNAMEl3 a;
        if (!g()) {
            return false;
        }
        boolean b = this.d.b(consumer);
        if (!b) {
            if (this.c != null || (a = a(this.e)) == null) {
                this.a = null;
            } else {
                Spliterator spliterator = a.spliterator();
                this.d = spliterator;
                return spliterator.b(consumer);
            }
        }
        return b;
    }

    public void forEachRemaining(Consumer consumer) {
        if (this.a != null) {
            if (this.d == null) {
                Spliterator spliterator = this.c;
                if (spliterator == null) {
                    Deque f = f();
                    while (true) {
                        CLASSNAMEl3 a = a(f);
                        if (a != null) {
                            a.forEach(consumer);
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
                } while (b(consumer));
            }
        }
    }
}
