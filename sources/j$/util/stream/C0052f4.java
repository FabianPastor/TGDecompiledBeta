package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.c;
import j$.util.function.y;
import j$.util.u;
import java.util.Comparator;

/* renamed from: j$.util.stream.f4  reason: case insensitive filesystem */
abstract class CLASSNAMEf4 implements u {
    final boolean a;
    final CLASSNAMEy2 b;
    private y c;
    u d;
    CLASSNAMEm3 e;
    c f;
    long g;
    CLASSNAMEe h;
    boolean i;

    CLASSNAMEf4(CLASSNAMEy2 y2Var, y yVar, boolean z) {
        this.b = y2Var;
        this.c = yVar;
        this.d = null;
        this.a = z;
    }

    CLASSNAMEf4(CLASSNAMEy2 y2Var, u uVar, boolean z) {
        this.b = y2Var;
        this.c = null;
        this.d = uVar;
        this.a = z;
    }

    private boolean f() {
        boolean z;
        while (this.h.count() == 0) {
            if (!this.e.o()) {
                CLASSNAMEb bVar = (CLASSNAMEb) this.f;
                switch (bVar.a) {
                    case 4:
                        CLASSNAMEo4 o4Var = (CLASSNAMEo4) bVar.b;
                        z = o4Var.d.b(o4Var.e);
                        break;
                    case 5:
                        CLASSNAMEq4 q4Var = (CLASSNAMEq4) bVar.b;
                        z = q4Var.d.b(q4Var.e);
                        break;
                    case 6:
                        s4 s4Var = (s4) bVar.b;
                        z = s4Var.d.b(s4Var.e);
                        break;
                    default:
                        L4 l4 = (L4) bVar.b;
                        z = l4.d.b(l4.e);
                        break;
                }
                if (z) {
                    continue;
                }
            }
            if (this.i) {
                return false;
            }
            this.e.m();
            this.i = true;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public final boolean a() {
        CLASSNAMEe eVar = this.h;
        boolean z = false;
        if (eVar != null) {
            long j = this.g + 1;
            this.g = j;
            if (j < eVar.count()) {
                z = true;
            }
            if (z) {
                return z;
            }
            this.g = 0;
            this.h.clear();
            return f();
        } else if (this.i) {
            return false;
        } else {
            h();
            j();
            this.g = 0;
            this.e.n(this.d.getExactSizeIfKnown());
            return f();
        }
    }

    public final int characteristics() {
        h();
        int g2 = CLASSNAMEd4.g(this.b.s0()) & CLASSNAMEd4.f;
        return (g2 & 64) != 0 ? (g2 & -16449) | (this.d.characteristics() & 16448) : g2;
    }

    public final long estimateSize() {
        h();
        return this.d.estimateSize();
    }

    public Comparator getComparator() {
        if (CLASSNAMEa.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public final long getExactSizeIfKnown() {
        h();
        if (CLASSNAMEd4.SIZED.d(this.b.s0())) {
            return this.d.getExactSizeIfKnown();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public final void h() {
        if (this.d == null) {
            this.d = (u) this.c.get();
            this.c = null;
        }
    }

    public /* synthetic */ boolean hasCharacteristics(int i2) {
        return CLASSNAMEa.f(this, i2);
    }

    /* access modifiers changed from: package-private */
    public abstract void j();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEf4 l(u uVar);

    public final String toString() {
        return String.format("%s[%s]", new Object[]{getClass().getName(), this.d});
    }

    public u trySplit() {
        if (!this.a || this.i) {
            return null;
        }
        h();
        u trySplit = this.d.trySplit();
        if (trySplit == null) {
            return null;
        }
        return l(trySplit);
    }
}
