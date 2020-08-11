package j$.util.stream;

import j$.util.CLASSNAMEy;
import java.util.Map;

/* renamed from: j$.util.stream.s6  reason: case insensitive filesystem */
class CLASSNAMEs6 {
    final Map a;

    CLASSNAMEs6(Map map) {
        this.a = map;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEs6 c(CLASSNAMEt6 t, Integer i) {
        this.a.put(t, i);
        return this;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEs6 d(CLASSNAMEt6 t) {
        c(t, 1);
        return this;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEs6 b(CLASSNAMEt6 t) {
        c(t, 2);
        return this;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEs6 e(CLASSNAMEt6 t) {
        c(t, 3);
        return this;
    }

    /* access modifiers changed from: package-private */
    public Map a() {
        for (CLASSNAMEt6 t : CLASSNAMEt6.values()) {
            CLASSNAMEy.g(this.a, t, 0);
        }
        return this.a;
    }
}
