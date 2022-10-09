package j$.time.format;
/* loaded from: classes2.dex */
class j implements g {
    final j$.time.temporal.k a;
    final int b;
    final int c;
    private final s d;
    final int e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public j(j$.time.temporal.k kVar, int i, int i2, s sVar) {
        this.a = kVar;
        this.b = i;
        this.c = i2;
        this.d = sVar;
        this.e = 0;
    }

    protected j(j$.time.temporal.k kVar, int i, int i2, s sVar, int i3) {
        this.a = kVar;
        this.b = i;
        this.c = i2;
        this.d = sVar;
        this.e = i3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public j b() {
        return this.e == -1 ? this : new j(this.a, this.b, this.c, this.d, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
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
