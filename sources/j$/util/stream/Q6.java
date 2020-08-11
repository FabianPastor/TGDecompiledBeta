package j$.util.stream;

import j$.util.N;
import j$.util.V;
import java.util.Comparator;

abstract class Q6 extends S6 implements V {
    /* access modifiers changed from: protected */
    public abstract Object g();

    public /* synthetic */ Comparator getComparator() {
        N.a(this);
        throw null;
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    public /* bridge */ /* synthetic */ V trySplit() {
        return (V) super.trySplit();
    }

    Q6(V s, long sliceOrigin, long sliceFence) {
        this(s, sliceOrigin, sliceFence, 0, Math.min(s.estimateSize(), sliceFence));
    }

    private Q6(V s, long sliceOrigin, long sliceFence, long origin, long fence) {
        super(s, sliceOrigin, sliceFence, origin, fence);
    }

    public boolean tryAdvance(Object action) {
        long j;
        action.getClass();
        if (this.a >= this.e) {
            return false;
        }
        while (true) {
            long j2 = this.a;
            j = this.d;
            if (j2 <= j) {
                break;
            }
            ((V) this.c).tryAdvance(g());
            this.d++;
        }
        if (j >= this.e) {
            return false;
        }
        this.d = j + 1;
        return ((V) this.c).tryAdvance(action);
    }

    public void forEachRemaining(Object action) {
        action.getClass();
        long j = this.a;
        long j2 = this.e;
        if (j < j2) {
            long j3 = this.d;
            if (j3 < j2) {
                if (j3 < j || j3 + ((V) this.c).estimateSize() > this.b) {
                    while (this.a > this.d) {
                        ((V) this.c).tryAdvance(g());
                        this.d++;
                    }
                    while (this.d < this.e) {
                        ((V) this.c).tryAdvance(action);
                        this.d++;
                    }
                    return;
                }
                ((V) this.c).forEachRemaining(action);
                this.d = this.e;
            }
        }
    }
}
