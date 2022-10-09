package j$.time.format;

import java.util.List;
/* loaded from: classes2.dex */
final class f implements g {
    private final g[] a;
    private final boolean b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public f(List list, boolean z) {
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
            for (g gVar : this.a) {
                sb.append(gVar);
            }
            sb.append(this.b ? "]" : ")");
        }
        return sb.toString();
    }
}
