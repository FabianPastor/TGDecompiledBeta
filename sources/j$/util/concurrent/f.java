package j$.util.concurrent;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.t;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class f implements t {
    long a;
    final long b;
    final double c;
    final double d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public f(long j, long j2, double d, double d2) {
        this.a = j;
        this.b = j2;
        this.c = d;
        this.d = d2;
    }

    @Override // j$.util.t, j$.util.w, j$.util.u
    /* renamed from: a */
    public f mo322trySplit() {
        long j = this.a;
        long j2 = (this.b + j) >>> 1;
        if (j2 <= j) {
            return null;
        }
        this.a = j2;
        return new f(j, j2, this.c, this.d);
    }

    @Override // j$.util.t, j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.j(this, consumer);
    }

    @Override // j$.util.u
    public int characteristics() {
        return 17728;
    }

    @Override // j$.util.w
    /* renamed from: e */
    public void forEachRemaining(j$.util.function.f fVar) {
        fVar.getClass();
        long j = this.a;
        long j2 = this.b;
        if (j < j2) {
            this.a = j2;
            double d = this.c;
            double d2 = this.d;
            i b = i.b();
            do {
                fVar.accept(b.d(d, d2));
                j++;
            } while (j < j2);
        }
    }

    @Override // j$.util.u
    public long estimateSize() {
        return this.b - this.a;
    }

    @Override // j$.util.t, j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.b(this, consumer);
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
    /* renamed from: k */
    public boolean tryAdvance(j$.util.function.f fVar) {
        fVar.getClass();
        long j = this.a;
        if (j < this.b) {
            fVar.accept(i.b().d(this.c, this.d));
            this.a = j + 1;
            return true;
        }
        return false;
    }
}
