package j$.util.concurrent;
/* loaded from: classes2.dex */
class e extends ThreadLocal {
    @Override // java.lang.ThreadLocal
    protected Object initialValue() {
        return new i(null);
    }
}
