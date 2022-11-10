package j$.util.stream;

import j$.util.u;
/* loaded from: classes2.dex */
abstract class D4 {
    final long a;
    final long b;
    j$.util.u c;
    long d;
    long e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public D4(j$.util.u uVar, long j, long j2, long j3, long j4) {
        this.c = uVar;
        this.a = j;
        this.b = j2;
        this.d = j3;
        this.e = j4;
    }

    protected abstract j$.util.u a(j$.util.u uVar, long j, long j2, long j3, long j4);

    public int characteristics() {
        return this.c.characteristics();
    }

    public long estimateSize() {
        long j = this.a;
        long j2 = this.e;
        if (j < j2) {
            return j2 - Math.max(j, this.d);
        }
        return 0L;
    }

    public /* bridge */ /* synthetic */ j$.util.t trySplit() {
        return (j$.util.t) m248trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ u.a m247trySplit() {
        return (u.a) m248trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public j$.util.u m248trySplit() {
        long j = this.a;
        long j2 = this.e;
        if (j < j2 && this.d < j2) {
            while (true) {
                j$.util.u moNUMtrySplit = this.c.moNUMtrySplit();
                if (moNUMtrySplit == null) {
                    return null;
                }
                long estimateSize = moNUMtrySplit.estimateSize() + this.d;
                long min = Math.min(estimateSize, this.b);
                long j3 = this.a;
                if (j3 >= min) {
                    this.d = min;
                } else {
                    long j4 = this.b;
                    if (min < j4) {
                        long j5 = this.d;
                        if (j5 < j3 || estimateSize > j4) {
                            this.d = min;
                            return a(moNUMtrySplit, j3, j4, j5, min);
                        }
                        this.d = min;
                        return moNUMtrySplit;
                    }
                    this.c = moNUMtrySplit;
                    this.e = min;
                }
            }
        } else {
            return null;
        }
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ j$.util.v m249trySplit() {
        return (j$.util.v) m248trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ j$.util.w m250trySplit() {
        return (j$.util.w) m248trySplit();
    }
}
