package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.CLASSNAMEc;
import j$.util.function.z;
import j$.util.y;
import java.util.Comparator;

/* renamed from: j$.util.stream.g4  reason: case insensitive filesystem */
abstract class CLASSNAMEg4 implements y {
    final boolean a;
    final CLASSNAMEz2 b;
    private z c;
    y d;
    CLASSNAMEn3 e;
    CLASSNAMEc f;
    long g;
    CLASSNAMEe h;
    boolean i;

    CLASSNAMEg4(CLASSNAMEz2 z2Var, z zVar, boolean z) {
        this.b = z2Var;
        this.c = zVar;
        this.d = null;
        this.a = z;
    }

    CLASSNAMEg4(CLASSNAMEz2 z2Var, y yVar, boolean z) {
        this.b = z2Var;
        this.c = null;
        this.d = yVar;
        this.a = z;
    }

    private boolean f() {
        boolean z;
        while (this.h.count() == 0) {
            if (!this.e.o()) {
                CLASSNAMEb bVar = (CLASSNAMEb) this.f;
                switch (bVar.a) {
                    case 4:
                        CLASSNAMEp4 p4Var = (CLASSNAMEp4) bVar.b;
                        z = p4Var.d.b(p4Var.e);
                        break;
                    case 5:
                        CLASSNAMEr4 r4Var = (CLASSNAMEr4) bVar.b;
                        z = r4Var.d.b(r4Var.e);
                        break;
                    case 6:
                        t4 t4Var = (t4) bVar.b;
                        z = t4Var.d.b(t4Var.e);
                        break;
                    default:
                        M4 m4 = (M4) bVar.b;
                        z = m4.d.b(m4.e);
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
        int g2 = CLASSNAMEe4.g(this.b.s0()) & CLASSNAMEe4.f;
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
        if (CLASSNAMEe4.SIZED.d(this.b.s0())) {
            return this.d.getExactSizeIfKnown();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public final void h() {
        if (this.d == null) {
            this.d = (y) this.c.get();
            this.c = null;
        }
    }

    public /* synthetic */ boolean hasCharacteristics(int i2) {
        return CLASSNAMEa.f(this, i2);
    }

    /* access modifiers changed from: package-private */
    public abstract void j();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEg4 l(y yVar);

    public final String toString() {
        return String.format("%s[%s]", new Object[]{getClass().getName(), this.d});
    }

    public y trySplit() {
        if (!this.a || this.i) {
            return null;
        }
        h();
        y trySplit = this.d.trySplit();
        if (trySplit == null) {
            return null;
        }
        return l(trySplit);
    }
}
