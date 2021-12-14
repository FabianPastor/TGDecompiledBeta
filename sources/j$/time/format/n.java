package j$.time.format;

import j$.time.temporal.k;

final class n implements g {
    private final k a;
    private final t b;

    n(k kVar, t tVar, c cVar) {
        this.a = kVar;
        this.b = tVar;
    }

    public String toString() {
        StringBuilder sb;
        Object obj;
        if (this.b == t.FULL) {
            sb = new StringBuilder();
            sb.append("Text(");
            obj = this.a;
        } else {
            sb = new StringBuilder();
            sb.append("Text(");
            sb.append(this.a);
            sb.append(",");
            obj = this.b;
        }
        sb.append(obj);
        sb.append(")");
        return sb.toString();
    }
}
