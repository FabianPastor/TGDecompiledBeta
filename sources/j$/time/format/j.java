package j$.time.format;

import j$.time.temporal.k;

class j implements g {
    final k a;
    final int b;
    final int c;
    /* access modifiers changed from: private */
    public final s d;
    final int e;

    j(k kVar, int i, int i2, s sVar) {
        this.a = kVar;
        this.b = i;
        this.c = i2;
        this.d = sVar;
        this.e = 0;
    }

    protected j(k kVar, int i, int i2, s sVar, int i3) {
        this.a = kVar;
        this.b = i;
        this.c = i2;
        this.d = sVar;
        this.e = i3;
    }

    /* access modifiers changed from: package-private */
    public j b() {
        return this.e == -1 ? this : new j(this.a, this.b, this.c, this.d, -1);
    }

    /* access modifiers changed from: package-private */
    public j c(int i) {
        return new j(this.a, this.b, this.c, this.d, this.e + i);
    }

    public String toString() {
        StringBuilder sb;
        Object obj;
        int i = this.b;
        if (i == 1 && this.c == 19 && this.d == s.NORMAL) {
            sb = new StringBuilder();
            sb.append("Value(");
            obj = this.a;
        } else if (i == this.c && this.d == s.NOT_NEGATIVE) {
            sb = new StringBuilder();
            sb.append("Value(");
            sb.append(this.a);
            sb.append(",");
            sb.append(this.b);
            sb.append(")");
            return sb.toString();
        } else {
            sb = new StringBuilder();
            sb.append("Value(");
            sb.append(this.a);
            sb.append(",");
            sb.append(this.b);
            sb.append(",");
            sb.append(this.c);
            sb.append(",");
            obj = this.d;
        }
        sb.append(obj);
        sb.append(")");
        return sb.toString();
    }
}
