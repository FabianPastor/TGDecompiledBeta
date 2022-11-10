package j$.wrappers;

import j$.util.function.Consumer;
import java.util.PrimitiveIterator;
/* renamed from: j$.wrappers.e  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEe implements j$.util.r {
    final /* synthetic */ PrimitiveIterator.OfLong a;

    private /* synthetic */ CLASSNAMEe(PrimitiveIterator.OfLong ofLong) {
        this.a = ofLong;
    }

    public static /* synthetic */ j$.util.r a(PrimitiveIterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof CLASSNAMEf ? ((CLASSNAMEf) ofLong).a : new CLASSNAMEe(ofLong);
    }

    @Override // j$.util.r
    public /* synthetic */ void d(j$.util.function.q qVar) {
        this.a.forEachRemaining(CLASSNAMEg0.a(qVar));
    }

    @Override // j$.util.r, j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEx.a(consumer));
    }

    @Override // j$.util.p
    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining((PrimitiveIterator.OfLong) obj);
    }

    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    @Override // j$.util.r, java.util.Iterator
    /* renamed from: next */
    public /* synthetic */ Long moNUMnext() {
        return this.a.next();
    }

    @Override // j$.util.r, java.util.Iterator
    /* renamed from: next  reason: collision with other method in class */
    public /* synthetic */ Object moNUMnext() {
        return this.a.next();
    }

    @Override // j$.util.r
    public /* synthetic */ long nextLong() {
        return this.a.nextLong();
    }

    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
