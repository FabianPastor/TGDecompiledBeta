package j$.wrappers;

import j$.util.function.Consumer;
import j$.util.p;
import java.util.PrimitiveIterator;
/* renamed from: j$.wrappers.c  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEc implements p.a {
    final /* synthetic */ PrimitiveIterator.OfInt a;

    private /* synthetic */ CLASSNAMEc(PrimitiveIterator.OfInt ofInt) {
        this.a = ofInt;
    }

    public static /* synthetic */ p.a a(PrimitiveIterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof CLASSNAMEd ? ((CLASSNAMEd) ofInt).a : new CLASSNAMEc(ofInt);
    }

    @Override // j$.util.p.a
    public /* synthetic */ void c(j$.util.function.l lVar) {
        this.a.forEachRemaining(S.a(lVar));
    }

    @Override // j$.util.p.a, j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEx.a(consumer));
    }

    @Override // j$.util.p
    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining((PrimitiveIterator.OfInt) obj);
    }

    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    @Override // j$.util.p.a, java.util.Iterator
    /* renamed from: next */
    public /* synthetic */ Integer mo313next() {
        return this.a.next();
    }

    @Override // j$.util.p.a, java.util.Iterator
    /* renamed from: next  reason: collision with other method in class */
    public /* synthetic */ Object mo313next() {
        return this.a.next();
    }

    @Override // j$.util.p.a
    public /* synthetic */ int nextInt() {
        return this.a.nextInt();
    }

    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
