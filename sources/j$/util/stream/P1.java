package j$.util.stream;

import j$.util.C;
import j$.util.function.A;
import j$.util.function.B;

final class P1 extends S1 implements D5 {
    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    P1() {
    }

    public /* bridge */ /* synthetic */ void t(Integer num) {
        super.accept((Object) num);
    }

    public void accept(int value) {
        accept((Object) Integer.valueOf(value));
    }

    /* renamed from: a */
    public C get() {
        if (this.a) {
            return C.d(((Integer) this.b).intValue());
        }
        return null;
    }
}
