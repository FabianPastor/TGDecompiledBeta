package j$.time.format;

import j$.time.o;
import j$.time.t.f;
import j$.time.t.q;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.G;
import j$.time.u.v;
import j$.time.u.w;

class B implements w {
    final /* synthetic */ f a;
    final /* synthetic */ w b;
    final /* synthetic */ q c;
    final /* synthetic */ o d;

    public /* synthetic */ int i(j$.time.u.B b2) {
        return v.a(this, b2);
    }

    B(f fVar, w wVar, q qVar, o oVar) {
        this.a = fVar;
        this.b = wVar;
        this.c = qVar;
        this.d = oVar;
    }

    public boolean h(j$.time.u.B field) {
        if (this.a == null || !field.i()) {
            return this.b.h(field);
        }
        return this.a.h(field);
    }

    public G p(j$.time.u.B field) {
        if (this.a == null || !field.i()) {
            return this.b.p(field);
        }
        return this.a.p(field);
    }

    public long f(j$.time.u.B field) {
        if (this.a == null || !field.i()) {
            return this.b.f(field);
        }
        return this.a.f(field);
    }

    public Object r(D d2) {
        if (d2 == C.a()) {
            return this.c;
        }
        if (d2 == C.n()) {
            return this.d;
        }
        if (d2 == C.l()) {
            return this.b.r(d2);
        }
        return d2.a(this);
    }
}
