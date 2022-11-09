package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Deque;
/* renamed from: j$.util.stream.j2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEj2 extends AbstractCLASSNAMEk2 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEj2(A1 a1) {
        super(a1);
    }

    @Override // j$.util.u
    public boolean b(Consumer consumer) {
        A1 a;
        if (!h()) {
            return false;
        }
        boolean b = this.d.b(consumer);
        if (!b) {
            if (this.c == null && (a = a(this.e)) != null) {
                j$.util.u mo289spliterator = a.mo289spliterator();
                this.d = mo289spliterator;
                return mo289spliterator.b(consumer);
            }
            this.a = null;
        }
        return b;
    }

    @Override // j$.util.u
    public void forEachRemaining(Consumer consumer) {
        if (this.a == null) {
            return;
        }
        if (this.d != null) {
            do {
            } while (b(consumer));
            return;
        }
        j$.util.u uVar = this.c;
        if (uVar != null) {
            uVar.forEachRemaining(consumer);
            return;
        }
        Deque f = f();
        while (true) {
            A1 a = a(f);
            if (a == null) {
                this.a = null;
                return;
            }
            a.forEach(consumer);
        }
    }
}
