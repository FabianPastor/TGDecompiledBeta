package j$.util.stream;

import j$.util.function.A;
import j$.util.function.B;

/* renamed from: j$.util.stream.z6  reason: case insensitive filesystem */
final class CLASSNAMEz6 extends B6 implements B {
    final int[] c;

    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    CLASSNAMEz6(int size) {
        this.c = new int[size];
    }

    public void accept(int t) {
        int[] iArr = this.c;
        int i = this.b;
        this.b = i + 1;
        iArr[i] = t;
    }

    /* renamed from: d */
    public void c(B action, long fence) {
        for (int i = 0; ((long) i) < fence; i++) {
            action.accept(this.c[i]);
        }
    }
}
