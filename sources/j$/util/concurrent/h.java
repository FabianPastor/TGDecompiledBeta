package j$.util.concurrent;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.v;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class h implements v {
    long a;
    final long b;
    final long c;
    final long d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public h(long j, long j2, long j3, long j4) {
        this.a = j;
        this.b = j2;
        this.c = j3;
        this.d = j4;
    }

    @Override // j$.util.v, j$.util.w, j$.util.u
    /* renamed from: a */
    public h moNUMtrySplit() {
        long j = this.a;
        long j2 = (this.b + j) >>> 1;
        if (j2 <= j) {
            return null;
        }
        this.a = j2;
        return new h(j, j2, this.c, this.d);
    }

    @Override // j$.util.v, j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.l(this, consumer);
    }

    @Override // j$.util.u
    public int characteristics() {
        return 17728;
    }

    @Override // j$.util.w
    /* renamed from: d */
    public void forEachRemaining(q qVar) {
        qVar.getClass();
        long j = this.a;
        long j2 = this.b;
        if (j < j2) {
            this.a = j2;
            long j3 = this.c;
            long j4 = this.d;
            i b = i.b();
            do {
                qVar.accept(b.f(j3, j4));
                j++;
            } while (j < j2);
        }
    }

    @Override // j$.util.u
    public long estimateSize() {
        return this.b - this.a;
    }

    @Override // j$.util.v, j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.d(this, consumer);
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractCLASSNAMEa.e(this);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractCLASSNAMEa.f(this, i);
    }

    @Override // j$.util.w
    /* renamed from: i */
    public boolean tryAdvance(q qVar) {
        qVar.getClass();
        long j = this.a;
        if (j < this.b) {
            qVar.accept(i.b().f(this.c, this.d));
            this.a = j + 1;
            return true;
        }
        return false;
    }
}
