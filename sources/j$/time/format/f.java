package j$.time.format;

import java.util.List;

final class f implements g {
    private final g[] a;
    private final boolean b;

    f(List list, boolean z) {
        this.a = (g[]) list.toArray(new g[list.size()]);
        this.b = z;
    }

    f(g[] gVarArr, boolean z) {
        this.a = gVarArr;
        this.b = z;
    }

    public f a(boolean z) {
        return z == this.b ? this : new f(this.a, z);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.a != null) {
            sb.append(this.b ? "[" : "(");
            for (g append : this.a) {
                sb.append(append);
            }
            sb.append(this.b ? "]" : ")");
        }
        return sb.toString();
    }
}
