package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.y;
import java.util.Deque;

/* renamed from: j$.util.stream.k2  reason: case insensitive filesystem */
final class CLASSNAMEk2 extends CLASSNAMEl2 {
    CLASSNAMEk2(B1 b1) {
        super(b1);
    }

    public boolean b(Consumer consumer) {
        B1 a;
        if (!h()) {
            return false;
        }
        boolean b = this.d.b(consumer);
        if (!b) {
            if (this.c != null || (a = a(this.e)) == null) {
                this.a = null;
            } else {
                y spliterator = a.spliterator();
                this.d = spliterator;
                return spliterator.b(consumer);
            }
        }
        return b;
    }

    public void forEachRemaining(Consumer consumer) {
        if (this.a != null) {
            if (this.d == null) {
                y yVar = this.c;
                if (yVar == null) {
                    Deque f = f();
                    while (true) {
                        B1 a = a(f);
                        if (a != null) {
                            a.forEach(consumer);
                        } else {
                            this.a = null;
                            return;
                        }
                    }
                } else {
                    yVar.forEachRemaining(consumer);
                }
            } else {
                do {
                } while (b(consumer));
            }
        }
    }
}
