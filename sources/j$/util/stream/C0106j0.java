package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.j0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEj0 extends AbstractCLASSNAMEd {
    private final CLASSNAMEd0 j;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEj0(CLASSNAMEd0 CLASSNAMEd0, AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        super(abstractCLASSNAMEy2, uVar);
        this.j = CLASSNAMEd0;
    }

    CLASSNAMEj0(CLASSNAMEj0 CLASSNAMEj0, j$.util.u uVar) {
        super(CLASSNAMEj0, uVar);
        this.j = CLASSNAMEj0.j;
    }

    private void m(Object obj) {
        boolean z;
        CLASSNAMEj0 CLASSNAMEj0 = this;
        while (true) {
            if (CLASSNAMEj0 != null) {
                AbstractCLASSNAMEf c = CLASSNAMEj0.c();
                if (c != null && c.d != CLASSNAMEj0) {
                    z = false;
                    break;
                }
                CLASSNAMEj0 = c;
            } else {
                z = true;
                break;
            }
        }
        if (z) {
            l(obj);
        } else {
            j();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public Object a() {
        AbstractCLASSNAMEy2 abstractCLASSNAMEy2 = this.a;
        O4 o4 = (O4) this.j.e.get();
        abstractCLASSNAMEy2.u0(o4, this.b);
        Object obj = o4.get();
        if (!this.j.b) {
            if (obj != null) {
                l(obj);
            }
            return null;
        } else if (obj == null) {
            return null;
        } else {
            m(obj);
            return obj;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public AbstractCLASSNAMEf f(j$.util.u uVar) {
        return new CLASSNAMEj0(this, uVar);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEd
    protected Object k() {
        return this.j.c;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (this.j.b) {
            CLASSNAMEj0 CLASSNAMEj0 = (CLASSNAMEj0) this.d;
            CLASSNAMEj0 CLASSNAMEj02 = null;
            while (true) {
                if (CLASSNAMEj0 != CLASSNAMEj02) {
                    Object b = CLASSNAMEj0.b();
                    if (b != null && this.j.d.test(b)) {
                        g(b);
                        m(b);
                        break;
                    }
                    CLASSNAMEj02 = CLASSNAMEj0;
                    CLASSNAMEj0 = (CLASSNAMEj0) this.e;
                } else {
                    break;
                }
            }
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
