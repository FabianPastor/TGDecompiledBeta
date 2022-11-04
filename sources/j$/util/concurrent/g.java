package j$.util.concurrent;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.u;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class g implements u.a {
    long a;
    final long b;
    final int c;
    final int d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public g(long j, long j2, int i, int i2) {
        this.a = j;
        this.b = j2;
        this.c = i;
        this.d = i2;
    }

    @Override // j$.util.u.a, j$.util.w, j$.util.u
    /* renamed from: a */
    public g mo326trySplit() {
        long j = this.a;
        long j2 = (this.b + j) >>> 1;
        if (j2 <= j) {
            return null;
        }
        this.a = j2;
        return new g(j, j2, this.c, this.d);
    }

    @Override // j$.util.u.a, j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.k(this, consumer);
    }

    @Override // j$.util.w
    /* renamed from: c */
    public void forEachRemaining(l lVar) {
        lVar.getClass();
        long j = this.a;
        long j2 = this.b;
        if (j < j2) {
            this.a = j2;
            int i = this.c;
            int i2 = this.d;
            i b = i.b();
            do {
                lVar.accept(b.e(i, i2));
                j++;
            } while (j < j2);
        }
    }

    @Override // j$.util.u
    public int characteristics() {
        return 17728;
    }

    @Override // j$.util.u
    public long estimateSize() {
        return this.b - this.a;
    }

    @Override // j$.util.u.a, j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.c(this, consumer);
    }

    @Override // j$.util.w
    /* renamed from: g */
    public boolean tryAdvance(l lVar) {
        lVar.getClass();
        long j = this.a;
        if (j < this.b) {
            lVar.accept(i.b().e(this.c, this.d));
            this.a = j + 1;
            return true;
        }
        return false;
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
}
