package j$.util.stream;

import j$.util.N;
import j$.util.Spliterator;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import java.util.Comparator;
import java.util.stream.StreamSpliterators;

final class X6 extends Z6 implements Spliterator, Consumer {
    Object e;

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

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

    X6(Spliterator spliterator, long skip, long limit) {
        super(spliterator, skip, limit);
    }

    X6(Spliterator spliterator, X6 parent) {
        super(spliterator, parent);
    }

    public final void accept(Object t) {
        this.e = t;
    }

    public boolean a(Consumer consumer) {
        consumer.getClass();
        while (o() != Y6.NO_MORE && this.a.a(this)) {
            if (k(1) == 1) {
                consumer.accept(this.e);
                this.e = null;
                return true;
            }
        }
        return false;
    }

    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        StreamSpliterators.ArrayBuffer.OfRef<T> sb = null;
        while (true) {
            Y6 o = o();
            Y6 permitStatus = o;
            if (o == Y6.NO_MORE) {
                return;
            }
            if (permitStatus == Y6.MAYBE_MORE) {
                if (sb == null) {
                    sb = new C6(128);
                } else {
                    sb.a();
                }
                long permitsRequested = 0;
                while (this.a.a(sb)) {
                    long j = 1 + permitsRequested;
                    permitsRequested = j;
                    if (j >= 128) {
                        break;
                    }
                }
                if (permitsRequested != 0) {
                    sb.c(consumer, k(permitsRequested));
                } else {
                    return;
                }
            } else {
                this.a.forEachRemaining(consumer);
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public Spliterator m(Spliterator spliterator) {
        return new X6(spliterator, this);
    }
}
