package j$.util.stream;

import j$.util.N;
import j$.util.V;
import java.util.Comparator;

abstract class W6 extends Z6 implements V {
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

    /* access modifiers changed from: protected */
    public abstract void w(Object obj);

    /* access modifiers changed from: protected */
    public abstract B6 x(int i);

    public /* bridge */ /* synthetic */ V trySplit() {
        return (V) super.trySplit();
    }

    W6(V s, long skip, long limit) {
        super(s, skip, limit);
    }

    W6(V s, W6 parent) {
        super(s, parent);
    }

    public boolean tryAdvance(Object action) {
        action.getClass();
        while (o() != Y6.NO_MORE && ((V) this.a).tryAdvance(this)) {
            if (k(1) == 1) {
                w(action);
                return true;
            }
        }
        return false;
    }

    public void forEachRemaining(Object action) {
        action.getClass();
        T_BUFF sb = null;
        while (true) {
            Y6 o = o();
            Y6 permitStatus = o;
            if (o == Y6.NO_MORE) {
                return;
            }
            if (permitStatus == Y6.MAYBE_MORE) {
                if (sb == null) {
                    sb = x(128);
                } else {
                    sb.a();
                }
                T_BUFF sbc = sb;
                long permitsRequested = 0;
                while (((V) this.a).tryAdvance(sbc)) {
                    long j = 1 + permitsRequested;
                    permitsRequested = j;
                    if (j >= 128) {
                        break;
                    }
                }
                if (permitsRequested != 0) {
                    sb.c(action, k(permitsRequested));
                } else {
                    return;
                }
            } else {
                ((V) this.a).forEachRemaining(action);
                return;
            }
        }
    }
}
