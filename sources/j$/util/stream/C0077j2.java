package j$.util.stream;

import j$.util.x;
import j$.util.y;
import java.util.Deque;

/* renamed from: j$.util.stream.j2  reason: case insensitive filesystem */
abstract class CLASSNAMEj2 extends CLASSNAMEl2 implements x {
    CLASSNAMEj2(A1 a1) {
        super(a1);
    }

    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        if (this.a != null) {
            if (this.d == null) {
                y yVar = this.c;
                if (yVar == null) {
                    Deque f = f();
                    while (true) {
                        A1 a1 = (A1) a(f);
                        if (a1 != null) {
                            a1.g(obj);
                        } else {
                            this.a = null;
                            return;
                        }
                    }
                } else {
                    ((x) yVar).forEachRemaining(obj);
                }
            } else {
                do {
                } while (k(obj));
            }
        }
    }

    /* renamed from: tryAdvance */
    public boolean k(Object obj) {
        A1 a1;
        if (!h()) {
            return false;
        }
        boolean tryAdvance = ((x) this.d).tryAdvance(obj);
        if (!tryAdvance) {
            if (this.c != null || (a1 = (A1) a(this.e)) == null) {
                this.a = null;
            } else {
                x spliterator = a1.spliterator();
                this.d = spliterator;
                return spliterator.tryAdvance(obj);
            }
        }
        return tryAdvance;
    }
}
