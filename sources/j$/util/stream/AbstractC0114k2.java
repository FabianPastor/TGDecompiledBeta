package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.u;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
/* renamed from: j$.util.stream.k2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
abstract class AbstractCLASSNAMEk2 implements j$.util.u {
    A1 a;
    int b;
    j$.util.u c;
    j$.util.u d;
    Deque e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEk2(A1 a1) {
        this.a = a1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final A1 a(Deque deque) {
        while (true) {
            A1 a1 = (A1) deque.pollFirst();
            if (a1 != null) {
                if (a1.p() != 0) {
                    for (int p = a1.p() - 1; p >= 0; p--) {
                        deque.addFirst(a1.moNUMb(p));
                    }
                } else if (a1.count() > 0) {
                    return a1;
                }
            } else {
                return null;
            }
        }
    }

    @Override // j$.util.u
    public final int characteristics() {
        return 64;
    }

    @Override // j$.util.u
    public final long estimateSize() {
        long j = 0;
        if (this.a == null) {
            return 0L;
        }
        j$.util.u uVar = this.c;
        if (uVar != null) {
            return uVar.estimateSize();
        }
        for (int i = this.b; i < this.a.p(); i++) {
            j += this.a.moNUMb(i).count();
        }
        return j;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Deque f() {
        ArrayDeque arrayDeque = new ArrayDeque(8);
        int p = this.a.p();
        while (true) {
            p--;
            if (p >= this.b) {
                arrayDeque.addFirst(this.a.moNUMb(p));
            } else {
                return arrayDeque;
            }
        }
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractCLASSNAMEa.e(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean h() {
        if (this.a == null) {
            return false;
        }
        if (this.d != null) {
            return true;
        }
        j$.util.u uVar = this.c;
        if (uVar == null) {
            Deque f = f();
            this.e = f;
            A1 a = a(f);
            if (a == null) {
                this.a = null;
                return false;
            }
            uVar = a.moNUMspliterator();
        }
        this.d = uVar;
        return true;
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractCLASSNAMEa.f(this, i);
    }

    @Override // j$.util.u
    /* renamed from: trySplit */
    public /* bridge */ /* synthetic */ j$.util.t moNUMtrySplit() {
        return (j$.util.t) moNUMtrySplit();
    }

    @Override // j$.util.u
    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ u.a moNUMtrySplit() {
        return (u.a) moNUMtrySplit();
    }

    @Override // j$.util.u
    /* renamed from: trySplit */
    public final j$.util.u moNUMtrySplit() {
        A1 a1 = this.a;
        if (a1 == null || this.d != null) {
            return null;
        }
        j$.util.u uVar = this.c;
        if (uVar != null) {
            return uVar.moNUMtrySplit();
        }
        if (this.b < a1.p() - 1) {
            A1 a12 = this.a;
            int i = this.b;
            this.b = i + 1;
            return a12.moNUMb(i).moNUMspliterator();
        }
        A1 moNUMb = this.a.moNUMb(this.b);
        this.a = moNUMb;
        if (moNUMb.p() == 0) {
            j$.util.u moNUMspliterator = this.a.moNUMspliterator();
            this.c = moNUMspliterator;
            return moNUMspliterator.moNUMtrySplit();
        }
        this.b = 0;
        A1 a13 = this.a;
        this.b = 1;
        return a13.moNUMb(0).moNUMspliterator();
    }

    @Override // j$.util.u
    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ j$.util.v moNUMtrySplit() {
        return (j$.util.v) moNUMtrySplit();
    }

    @Override // j$.util.u
    /* renamed from: trySplit */
    public /* bridge */ /* synthetic */ j$.util.w moNUMtrySplit() {
        return (j$.util.w) moNUMtrySplit();
    }
}
