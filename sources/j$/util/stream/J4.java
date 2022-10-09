package j$.util.stream;

import j$.util.u;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes2.dex */
abstract class J4 {
    protected final j$.util.u a;
    protected final boolean b;
    private final long c;
    private final AtomicLong d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public J4(j$.util.u uVar, long j, long j2) {
        this.a = uVar;
        long j3 = 0;
        int i = (j2 > 0L ? 1 : (j2 == 0L ? 0 : -1));
        this.b = i < 0;
        this.c = i >= 0 ? j2 : j3;
        this.d = new AtomicLong(i >= 0 ? j + j2 : j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public J4(j$.util.u uVar, J4 j4) {
        this.a = uVar;
        this.b = j4.b;
        this.d = j4.d;
        this.c = j4.c;
    }

    public final int characteristics() {
        return this.a.characteristics() & (-16465);
    }

    public final long estimateSize() {
        return this.a.estimateSize();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final long p(long j) {
        long j2;
        long min;
        do {
            j2 = this.d.get();
            if (j2 != 0) {
                min = Math.min(j2, j);
                if (min <= 0) {
                    break;
                }
            } else if (!this.b) {
                return 0L;
            } else {
                return j;
            }
        } while (!this.d.compareAndSet(j2, j2 - min));
        if (this.b) {
            return Math.max(j - min, 0L);
        }
        long j3 = this.c;
        return j2 > j3 ? Math.max(min - (j2 - j3), 0L) : min;
    }

    protected abstract j$.util.u q(j$.util.u uVar);

    /* JADX INFO: Access modifiers changed from: protected */
    public final int r() {
        if (this.d.get() > 0) {
            return 2;
        }
        return this.b ? 3 : 1;
    }

    public /* bridge */ /* synthetic */ j$.util.t trySplit() {
        return (j$.util.t) m248trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ u.a m247trySplit() {
        return (u.a) m248trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public final j$.util.u m248trySplit() {
        j$.util.u mo322trySplit;
        if (this.d.get() == 0 || (mo322trySplit = this.a.mo322trySplit()) == null) {
            return null;
        }
        return q(mo322trySplit);
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
