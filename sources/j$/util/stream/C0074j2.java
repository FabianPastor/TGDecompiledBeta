package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.u;
import java.util.Deque;

/* renamed from: j$.util.stream.j2  reason: case insensitive filesystem */
final class CLASSNAMEj2 extends CLASSNAMEk2 {
    CLASSNAMEj2(A1 a1) {
        super(a1);
    }

    public boolean b(Consumer consumer) {
        A1 a;
        if (!h()) {
            return false;
        }
        boolean b = this.d.b(consumer);
        if (!b) {
            if (this.c != null || (a = a(this.e)) == null) {
                this.a = null;
            } else {
                u spliterator = a.spliterator();
                this.d = spliterator;
                return spliterator.b(consumer);
            }
        }
        return b;
    }

    public void forEachRemaining(Consumer consumer) {
        if (this.a != null) {
            if (this.d == null) {
                u uVar = this.c;
                if (uVar == null) {
                    Deque f = f();
                    while (true) {
                        A1 a = a(f);
                        if (a != null) {
                            a.forEach(consumer);
                        } else {
                            this.a = null;
                            return;
                        }
                    }
                } else {
                    uVar.forEachRemaining(consumer);
                }
            } else {
                do {
                } while (b(consumer));
            }
        }
    }
}
