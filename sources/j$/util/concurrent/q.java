package j$.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;

class q {
    m[] a;
    m b = null;
    p c;
    p d;
    int e;
    int f;
    int g;
    final int h;

    q(m[] tab, int size, int index, int limit) {
        this.a = tab;
        this.h = size;
        this.e = index;
        this.f = index;
        this.g = limit;
    }

    /* access modifiers changed from: package-private */
    public final m b() {
        s sVar;
        m mVar = this.b;
        m mVar2 = mVar;
        if (mVar != null) {
            mVar2 = mVar2.d;
        }
        while (sVar == null) {
            if (this.f < this.g) {
                ConcurrentHashMap.Node<K, V>[] nodeArr = this.a;
                ConcurrentHashMap.Node<K, V>[] t = nodeArr;
                if (nodeArr != null) {
                    int length = t.length;
                    int n = length;
                    int i = this.e;
                    int i2 = i;
                    if (length > i && i2 >= 0) {
                        m o = ConcurrentHashMap.o(t, i2);
                        sVar = o;
                        if (o != null && sVar.a < 0) {
                            if (sVar instanceof h) {
                                this.a = ((h) sVar).e;
                                sVar = null;
                                g(t, i2, n);
                            } else {
                                sVar = sVar instanceof r ? ((r) sVar).f : null;
                            }
                        }
                        if (this.c != null) {
                            h(n);
                        } else {
                            int i3 = this.h + i2;
                            this.e = i3;
                            if (i3 >= n) {
                                int i4 = this.f + 1;
                                this.f = i4;
                                this.e = i4;
                            }
                        }
                    }
                }
            }
            this.b = null;
            return null;
        }
        this.b = sVar;
        return sVar;
    }

    private void g(m[] t, int i, int n) {
        ConcurrentHashMap.TableStack<K, V> s = this.d;
        if (s != null) {
            this.d = s.d;
        } else {
            s = new p();
        }
        s.c = t;
        s.a = n;
        s.b = i;
        s.d = this.c;
        this.c = s;
    }

    private void h(int n) {
        ConcurrentHashMap.TableStack<K, V> s;
        while (true) {
            ConcurrentHashMap.TableStack<K, V> tableStack = this.c;
            s = tableStack;
            if (tableStack == null) {
                break;
            }
            int i = this.e;
            int i2 = s.a;
            int len = i2;
            int i3 = i + i2;
            this.e = i3;
            if (i3 < n) {
                break;
            }
            n = len;
            this.e = s.b;
            this.a = s.c;
            s.c = null;
            ConcurrentHashMap.TableStack<K, V> next = s.d;
            s.d = this.d;
            this.c = next;
            this.d = s;
        }
        if (s == null) {
            int i4 = this.e + this.h;
            this.e = i4;
            if (i4 >= n) {
                int i5 = this.f + 1;
                this.f = i5;
                this.e = i5;
            }
        }
    }
}
