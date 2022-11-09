package j$.wrappers;

import j$.util.InterfaceCLASSNAMEn;
import j$.util.function.Consumer;
import java.util.PrimitiveIterator;
/* renamed from: j$.wrappers.a  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEa implements InterfaceCLASSNAMEn {
    final /* synthetic */ PrimitiveIterator.OfDouble a;

    private /* synthetic */ CLASSNAMEa(PrimitiveIterator.OfDouble ofDouble) {
        this.a = ofDouble;
    }

    public static /* synthetic */ InterfaceCLASSNAMEn a(PrimitiveIterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof CLASSNAMEb ? ((CLASSNAMEb) ofDouble).a : new CLASSNAMEa(ofDouble);
    }

    @Override // j$.util.InterfaceCLASSNAMEn
    public /* synthetic */ void e(j$.util.function.f fVar) {
        this.a.forEachRemaining(B.a(fVar));
    }

    @Override // j$.util.InterfaceCLASSNAMEn, j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEx.a(consumer));
    }

    @Override // j$.util.p
    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining((PrimitiveIterator.OfDouble) obj);
    }

    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    @Override // j$.util.InterfaceCLASSNAMEn, java.util.Iterator, j$.util.Iterator
    /* renamed from: next */
    public /* synthetic */ Double mo311next() {
        return this.a.next();
    }

    @Override // j$.util.InterfaceCLASSNAMEn, java.util.Iterator, j$.util.Iterator
    /* renamed from: next  reason: collision with other method in class */
    public /* synthetic */ Object mo311next() {
        return this.a.next();
    }

    @Override // j$.util.InterfaceCLASSNAMEn
    public /* synthetic */ double nextDouble() {
        return this.a.nextDouble();
    }

    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
