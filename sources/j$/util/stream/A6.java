package j$.util.stream;

import j$.util.function.I;
import j$.util.function.J;

final class A6 extends B6 implements J {
    final long[] c;

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    A6(int size) {
        this.c = new long[size];
    }

    public void accept(long t) {
        long[] jArr = this.c;
        int i = this.b;
        this.b = i + 1;
        jArr[i] = t;
    }

    /* renamed from: d */
    public void c(J action, long fence) {
        for (int i = 0; ((long) i) < fence; i++) {
            action.accept(this.c[i]);
        }
    }
}
