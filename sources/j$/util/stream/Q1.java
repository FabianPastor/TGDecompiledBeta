package j$.util.stream;

import j$.util.D;
import j$.util.function.I;
import j$.util.function.J;

final class Q1 extends S1 implements F5 {
    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    Q1() {
    }

    public /* bridge */ /* synthetic */ void n(Long l) {
        super.accept((Object) l);
    }

    public void accept(long value) {
        accept((Object) Long.valueOf(value));
    }

    /* renamed from: a */
    public D get() {
        if (this.a) {
            return D.d(((Long) this.b).longValue());
        }
        return null;
    }
}
