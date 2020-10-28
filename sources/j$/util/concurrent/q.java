package j$.util.concurrent;

class q {
    m[] a;
    m b = null;
    p c;
    p d;
    int e;
    int f;
    int g;
    final int h;

    q(m[] mVarArr, int i, int i2, int i3) {
        this.a = mVarArr;
        this.h = i;
        this.e = i2;
        this.f = i2;
        this.g = i3;
    }

    /* access modifiers changed from: package-private */
    public final m a() {
        m mVar;
        m[] mVarArr;
        int length;
        int i;
        p pVar;
        m mVar2 = this.b;
        if (mVar2 != null) {
            mVar2 = mVar2.d;
        }
        while (mVar == null) {
            if (this.f >= this.g || (mVarArr = this.a) == null || (length = mVarArr.length) <= (i = this.e) || i < 0) {
                this.b = null;
                return null;
            }
            m n = ConcurrentHashMap.n(mVarArr, i);
            if (n == null || n.a >= 0) {
                mVar = n;
            } else if (n instanceof h) {
                this.a = ((h) n).e;
                p pVar2 = this.d;
                if (pVar2 != null) {
                    this.d = pVar2.d;
                } else {
                    pVar2 = new p();
                }
                pVar2.c = mVarArr;
                pVar2.a = length;
                pVar2.b = i;
                pVar2.d = this.c;
                this.c = pVar2;
                mVar = null;
            } else {
                mVar = n instanceof r ? ((r) n).f : null;
            }
            if (this.c != null) {
                while (true) {
                    pVar = this.c;
                    if (pVar == null) {
                        break;
                    }
                    int i2 = this.e;
                    int i3 = pVar.a;
                    int i4 = i2 + i3;
                    this.e = i4;
                    if (i4 < length) {
                        break;
                    }
                    this.e = pVar.b;
                    this.a = pVar.c;
                    pVar.c = null;
                    p pVar3 = pVar.d;
                    pVar.d = this.d;
                    this.c = pVar3;
                    this.d = pVar;
                    length = i3;
                }
                if (pVar == null) {
                    int i5 = this.e + this.h;
                    this.e = i5;
                    if (i5 >= length) {
                        int i6 = this.f + 1;
                        this.f = i6;
                        this.e = i6;
                    }
                }
            } else {
                int i7 = i + this.h;
                this.e = i7;
                if (i7 >= length) {
                    int i8 = this.f + 1;
                    this.f = i8;
                    this.e = i8;
                }
            }
        }
        this.b = mVar;
        return mVar;
    }
}
