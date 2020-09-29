package j$.util.concurrent;

import j$.o0;
import j$.util.Collection;
import j$.util.function.C;
import j$.util.function.Predicate;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/* renamed from: j$.util.concurrent.c  reason: case insensitive filesystem */
abstract class CLASSNAMEc implements Collection, Serializable, j$.util.Collection {
    final ConcurrentHashMap a;

    public abstract boolean contains(Object obj);

    public abstract Iterator iterator();

    public /* synthetic */ boolean removeIf(Predicate predicate) {
        return Collection.CC.$default$removeIf(this, predicate);
    }

    public /* synthetic */ boolean removeIf(java.util.function.Predicate predicate) {
        return removeIf(o0.c(predicate));
    }

    public /* synthetic */ Object[] toArray(C c) {
        return Collection.CC.b(this, c);
    }

    CLASSNAMEc(ConcurrentHashMap concurrentHashMap) {
        this.a = concurrentHashMap;
    }

    public final void clear() {
        this.a.clear();
    }

    public final int size() {
        return this.a.size();
    }

    public final boolean isEmpty() {
        return this.a.isEmpty();
    }

    public final Object[] toArray() {
        long sz = this.a.h();
        if (sz <= NUM) {
            int n = (int) sz;
            Object[] r = new Object[n];
            int i = 0;
            Iterator it = iterator();
            while (it.hasNext()) {
                E e = it.next();
                if (i == n) {
                    if (n < NUM) {
                        if (n >= NUM) {
                            n = NUM;
                        } else {
                            n += (n >>> 1) + 1;
                        }
                        r = Arrays.copyOf(r, n);
                    } else {
                        throw new OutOfMemoryError("Required array size too large");
                    }
                }
                r[i] = e;
                i++;
            }
            return i == n ? r : Arrays.copyOf(r, i);
        }
        throw new OutOfMemoryError("Required array size too large");
    }

    public final Object[] toArray(Object[] a2) {
        Object[] objArr;
        long sz = this.a.h();
        if (sz <= NUM) {
            int m = (int) sz;
            if (a2.length >= m) {
                objArr = a2;
            } else {
                objArr = (Object[]) Array.newInstance(a2.getClass().getComponentType(), m);
            }
            int n = objArr.length;
            int i = 0;
            Iterator it = iterator();
            while (it.hasNext()) {
                E e = it.next();
                if (i == n) {
                    if (n < NUM) {
                        if (n >= NUM) {
                            n = NUM;
                        } else {
                            n += (n >>> 1) + 1;
                        }
                        objArr = Arrays.copyOf(objArr, n);
                    } else {
                        throw new OutOfMemoryError("Required array size too large");
                    }
                }
                objArr[i] = e;
                i++;
            }
            if (a2 != objArr || i >= n) {
                return i == n ? objArr : Arrays.copyOf(objArr, i);
            }
            objArr[i] = null;
            return objArr;
        }
        throw new OutOfMemoryError("Required array size too large");
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Iterator<E> it = iterator();
        if (it.hasNext()) {
            while (true) {
                Object e = it.next();
                sb.append(e == this ? "(this Collection)" : e);
                if (!it.hasNext()) {
                    break;
                }
                sb.append(',');
                sb.append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:4:0x000c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean containsAll(java.util.Collection r4) {
        /*
            r3 = this;
            if (r4 == r3) goto L_0x001c
            java.util.Iterator r0 = r4.iterator()
        L_0x0006:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x001c
            java.lang.Object r1 = r0.next()
            if (r1 == 0) goto L_0x001a
            boolean r2 = r3.contains(r1)
            if (r2 != 0) goto L_0x0019
            goto L_0x001a
        L_0x0019:
            goto L_0x0006
        L_0x001a:
            r0 = 0
            return r0
        L_0x001c:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.CLASSNAMEc.containsAll(java.util.Collection):boolean");
    }

    public final boolean removeAll(java.util.Collection c) {
        if (c != null) {
            boolean modified = false;
            Iterator<E> it = iterator();
            while (it.hasNext()) {
                if (c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }
        throw null;
    }

    public final boolean retainAll(java.util.Collection c) {
        if (c != null) {
            boolean modified = false;
            Iterator<E> it = iterator();
            while (it.hasNext()) {
                if (!c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }
        throw null;
    }
}
