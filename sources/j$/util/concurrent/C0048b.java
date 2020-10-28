package j$.util.concurrent;

/* renamed from: j$.util.concurrent.b  reason: case insensitive filesystem */
class CLASSNAMEb extends q {
    final ConcurrentHashMap i;
    m j;

    CLASSNAMEb(m[] mVarArr, int i2, int i3, int i4, ConcurrentHashMap concurrentHashMap) {
        super(mVarArr, i2, i3, i4);
        this.i = concurrentHashMap;
        a();
    }

    public final boolean hasMoreElements() {
        return this.b != null;
    }

    public final boolean hasNext() {
        return this.b != null;
    }

    public final void remove() {
        m mVar = this.j;
        if (mVar != null) {
            this.j = null;
            this.i.i(mVar.b, (Object) null, (Object) null);
            return;
        }
        throw new IllegalStateException();
    }
}
