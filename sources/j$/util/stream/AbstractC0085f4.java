package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import java.util.Comparator;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.f4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public abstract class AbstractCLASSNAMEf4 implements j$.util.u {
    final boolean a;
    final AbstractCLASSNAMEy2 b;
    private j$.util.function.y c;
    j$.util.u d;
    InterfaceCLASSNAMEm3 e;
    j$.util.function.c f;
    long g;
    AbstractCLASSNAMEe h;
    boolean i;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEf4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z) {
        this.b = abstractCLASSNAMEy2;
        this.c = yVar;
        this.d = null;
        this.a = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEf4(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z) {
        this.b = abstractCLASSNAMEy2;
        this.c = null;
        this.d = uVar;
        this.a = z;
    }

    private boolean f() {
        boolean b;
        while (this.h.count() == 0) {
            if (!this.e.o()) {
                CLASSNAMEb CLASSNAMEb = (CLASSNAMEb) this.f;
                switch (CLASSNAMEb.a) {
                    case 4:
                        CLASSNAMEo4 CLASSNAMEo4 = (CLASSNAMEo4) CLASSNAMEb.b;
                        b = CLASSNAMEo4.d.b(CLASSNAMEo4.e);
                        break;
                    case 5:
                        CLASSNAMEq4 CLASSNAMEq4 = (CLASSNAMEq4) CLASSNAMEb.b;
                        b = CLASSNAMEq4.d.b(CLASSNAMEq4.e);
                        break;
                    case 6:
                        s4 s4Var = (s4) CLASSNAMEb.b;
                        b = s4Var.d.b(s4Var.e);
                        break;
                    default:
                        L4 l4 = (L4) CLASSNAMEb.b;
                        b = l4.d.b(l4.e);
                        break;
                }
                if (b) {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean a() {
        AbstractCLASSNAMEe abstractCLASSNAMEe = this.h;
        boolean z = false;
        if (abstractCLASSNAMEe == null) {
            if (this.i) {
                return false;
            }
            h();
            j();
            this.g = 0L;
            this.e.n(this.d.getExactSizeIfKnown());
            return f();
        }
        long j = this.g + 1;
        this.g = j;
        if (j < abstractCLASSNAMEe.count()) {
            z = true;
        }
        if (z) {
            return z;
        }
        this.g = 0L;
        this.h.clear();
        return f();
    }

    @Override // j$.util.u
    public final int characteristics() {
        h();
        int g = EnumCLASSNAMEd4.g(this.b.s0()) & EnumCLASSNAMEd4.f;
        return (g & 64) != 0 ? (g & (-16449)) | (this.d.characteristics() & 16448) : g;
    }

    @Override // j$.util.u
    public final long estimateSize() {
        h();
        return this.d.estimateSize();
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        if (AbstractCLASSNAMEa.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public final long getExactSizeIfKnown() {
        h();
        if (EnumCLASSNAMEd4.SIZED.d(this.b.s0())) {
            return this.d.getExactSizeIfKnown();
        }
        return -1L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void h() {
        if (this.d == null) {
            this.d = (j$.util.u) this.c.get();
            this.c = null;
        }
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractCLASSNAMEa.f(this, i);
    }

    abstract void j();

    abstract AbstractCLASSNAMEf4 l(j$.util.u uVar);

    public final String toString() {
        return String.format("%s[%s]", getClass().getName(), this.d);
    }

    @Override // j$.util.u
    /* renamed from: trySplit */
    public j$.util.u mo326trySplit() {
        if (!this.a || this.i) {
            return null;
        }
        h();
        j$.util.u mo326trySplit = this.d.mo326trySplit();
        if (mo326trySplit != null) {
            return l(mo326trySplit);
        }
        return null;
    }
}
