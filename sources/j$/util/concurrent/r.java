package j$.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

final class r extends m {
    private static final Unsafe h;
    private static final long i;
    s e;
    volatile s f;
    volatile Thread g;
    volatile int lockState;

    static {
        Class<ConcurrentHashMap> cls = ConcurrentHashMap.class;
        try {
            Unsafe c = z.c();
            h = c;
            i = c.objectFieldOffset(r.class.getDeclaredField("lockState"));
        } catch (Exception e2) {
            throw new Error(e2);
        }
    }

    static int j(Object a, Object b) {
        if (!(a == null || b == null)) {
            int compareTo = a.getClass().getName().compareTo(b.getClass().getName());
            int d = compareTo;
            if (compareTo != 0) {
                return d;
            }
        }
        return System.identityHashCode(a) <= System.identityHashCode(b) ? -1 : 1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0036, code lost:
        if (r9 != null) goto L_0x0038;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    r(j$.util.concurrent.s r14) {
        /*
            r13 = this;
            r0 = -2
            r1 = 0
            r13.<init>(r0, r1, r1, r1)
            r13.f = r14
            r0 = 0
            r2 = r14
        L_0x0009:
            if (r2 == 0) goto L_0x0061
            j$.util.concurrent.m r3 = r2.d
            j$.util.concurrent.s r3 = (j$.util.concurrent.s) r3
            r2.g = r1
            r2.f = r1
            if (r0 != 0) goto L_0x001c
            r2.e = r1
            r4 = 0
            r2.i = r4
            r0 = r2
            goto L_0x005e
        L_0x001c:
            java.lang.Object r4 = r2.b
            int r5 = r2.a
            r6 = 0
            r7 = r0
        L_0x0022:
            java.lang.Object r8 = r7.b
            int r9 = r7.a
            r10 = r9
            if (r9 <= r5) goto L_0x002b
            r9 = -1
            goto L_0x0045
        L_0x002b:
            if (r10 >= r5) goto L_0x002f
            r9 = 1
            goto L_0x0045
        L_0x002f:
            if (r6 != 0) goto L_0x0038
            java.lang.Class r9 = j$.util.concurrent.ConcurrentHashMap.c(r4)
            r6 = r9
            if (r9 == 0) goto L_0x003f
        L_0x0038:
            int r9 = j$.util.concurrent.ConcurrentHashMap.d(r6, r4, r8)
            r11 = r9
            if (r9 != 0) goto L_0x0044
        L_0x003f:
            int r9 = j(r4, r8)
            goto L_0x0045
        L_0x0044:
            r9 = r11
        L_0x0045:
            r11 = r7
            if (r9 > 0) goto L_0x004b
            j$.util.concurrent.s r12 = r7.f
            goto L_0x004d
        L_0x004b:
            j$.util.concurrent.s r12 = r7.g
        L_0x004d:
            r7 = r12
            if (r12 != 0) goto L_0x0060
            r2.e = r11
            if (r9 > 0) goto L_0x0057
            r11.f = r2
            goto L_0x0059
        L_0x0057:
            r11.g = r2
        L_0x0059:
            j$.util.concurrent.s r0 = c(r0, r2)
        L_0x005e:
            r2 = r3
            goto L_0x0009
        L_0x0060:
            goto L_0x0022
        L_0x0061:
            r13.e = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.r.<init>(j$.util.concurrent.s):void");
    }

    private final void e() {
        if (!h.compareAndSwapInt(this, i, 0, 1)) {
            d();
        }
    }

    private final void k() {
        this.lockState = 0;
    }

    private final void d() {
        boolean waiting = false;
        while (true) {
            int i2 = this.lockState;
            int s = i2;
            if ((i2 & -3) == 0) {
                if (h.compareAndSwapInt(this, i, s, 1)) {
                    break;
                }
            } else if ((s & 2) == 0) {
                if (h.compareAndSwapInt(this, i, s, s | 2)) {
                    waiting = true;
                    this.g = Thread.currentThread();
                }
            } else if (waiting) {
                LockSupport.park(this);
            }
        }
        if (waiting) {
            this.g = null;
        }
    }

    /* access modifiers changed from: package-private */
    public final m a(int h2, Object k) {
        ConcurrentHashMap.TreeNode<K, V> p = null;
        if (k != null) {
            ConcurrentHashMap.Node<K, V> e2 = this.f;
            while (e2 != null) {
                int i2 = this.lockState;
                int s = i2;
                if ((i2 & 3) != 0) {
                    if (e2.a == h2) {
                        K k2 = e2.b;
                        K ek = k2;
                        if (k2 == k || (ek != null && k.equals(ek))) {
                            return e2;
                        }
                    }
                    e2 = e2.d;
                } else {
                    if (h.compareAndSwapInt(this, i, s, s + 4)) {
                        try {
                            s sVar = this.e;
                            s sVar2 = sVar;
                            if (sVar != null) {
                                p = sVar2.b(h2, k, (Class) null);
                            }
                            return p;
                        } finally {
                            if (z.a(h, this, i, -4) == 6) {
                                Thread thread = this.g;
                                Thread w = thread;
                                if (thread != null) {
                                    LockSupport.unpark(w);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004a, code lost:
        if (r2 != null) goto L_0x004c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final j$.util.concurrent.s f(int r17, java.lang.Object r18, java.lang.Object r19) {
        /*
            r16 = this;
            r1 = r16
            r8 = r17
            r9 = r18
            r0 = 0
            r2 = 0
            j$.util.concurrent.s r3 = r1.e
            r10 = r2
            r11 = r3
        L_0x000c:
            if (r11 != 0) goto L_0x0022
            j$.util.concurrent.s r12 = new j$.util.concurrent.s
            r6 = 0
            r7 = 0
            r2 = r12
            r3 = r17
            r4 = r18
            r5 = r19
            r2.<init>(r3, r4, r5, r6, r7)
            r1.e = r12
            r1.f = r12
            goto L_0x00bd
        L_0x0022:
            int r2 = r11.a
            r12 = r2
            if (r2 <= r8) goto L_0x002d
            r2 = -1
            r13 = r2
            r14 = r10
            r10 = r0
            goto L_0x007a
        L_0x002d:
            if (r12 >= r8) goto L_0x0034
            r2 = 1
            r13 = r2
            r14 = r10
            r10 = r0
            goto L_0x007a
        L_0x0034:
            java.lang.Object r2 = r11.b
            r3 = r2
            if (r2 == r9) goto L_0x00cb
            if (r3 == 0) goto L_0x0043
            boolean r2 = r9.equals(r3)
            if (r2 == 0) goto L_0x0043
            goto L_0x00cb
        L_0x0043:
            if (r0 != 0) goto L_0x004c
            java.lang.Class r2 = j$.util.concurrent.ConcurrentHashMap.c(r18)
            r0 = r2
            if (r2 == 0) goto L_0x0053
        L_0x004c:
            int r2 = j$.util.concurrent.ConcurrentHashMap.d(r0, r9, r3)
            r4 = r2
            if (r2 != 0) goto L_0x0077
        L_0x0053:
            if (r10 != 0) goto L_0x006f
            r10 = 1
            j$.util.concurrent.s r2 = r11.f
            r4 = r2
            if (r2 == 0) goto L_0x0062
            j$.util.concurrent.s r2 = r4.b(r8, r9, r0)
            r5 = r2
            if (r2 != 0) goto L_0x006e
        L_0x0062:
            j$.util.concurrent.s r2 = r11.g
            r4 = r2
            if (r2 == 0) goto L_0x006f
            j$.util.concurrent.s r2 = r4.b(r8, r9, r0)
            r5 = r2
            if (r2 == 0) goto L_0x006f
        L_0x006e:
            return r5
        L_0x006f:
            int r2 = j(r9, r3)
            r13 = r2
            r14 = r10
            r10 = r0
            goto L_0x007a
        L_0x0077:
            r13 = r4
            r14 = r10
            r10 = r0
        L_0x007a:
            r15 = r11
            if (r13 > 0) goto L_0x0080
            j$.util.concurrent.s r0 = r11.f
            goto L_0x0082
        L_0x0080:
            j$.util.concurrent.s r0 = r11.g
        L_0x0082:
            r11 = r0
            if (r0 != 0) goto L_0x00c5
            j$.util.concurrent.s r7 = r1.f
            j$.util.concurrent.s r0 = new j$.util.concurrent.s
            r2 = r0
            r3 = r17
            r4 = r18
            r5 = r19
            r6 = r7
            r8 = r7
            r7 = r15
            r2.<init>(r3, r4, r5, r6, r7)
            r1.f = r0
            if (r8 == 0) goto L_0x009c
            r8.h = r2
        L_0x009c:
            if (r13 > 0) goto L_0x00a1
            r15.f = r2
            goto L_0x00a3
        L_0x00a1:
            r15.g = r2
        L_0x00a3:
            boolean r0 = r15.i
            if (r0 != 0) goto L_0x00ab
            r0 = 1
            r2.i = r0
            goto L_0x00bb
        L_0x00ab:
            r16.e()
            j$.util.concurrent.s r0 = r1.e     // Catch:{ all -> 0x00c0 }
            j$.util.concurrent.s r0 = c(r0, r2)     // Catch:{ all -> 0x00c0 }
            r1.e = r0     // Catch:{ all -> 0x00c0 }
            r16.k()
        L_0x00bb:
            r0 = r10
            r10 = r14
        L_0x00bd:
            r2 = 0
            return r2
        L_0x00c0:
            r0 = move-exception
            r16.k()
            throw r0
        L_0x00c5:
            r8 = r17
            r0 = r10
            r10 = r14
            goto L_0x000c
        L_0x00cb:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.r.f(int, java.lang.Object, java.lang.Object):j$.util.concurrent.s");
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    public final boolean g(s p) {
        s sVar;
        ConcurrentHashMap.TreeNode<K, V> next = (s) p.d;
        ConcurrentHashMap.TreeNode<K, V> pred = p.h;
        if (pred == null) {
            this.f = next;
        } else {
            pred.d = next;
        }
        if (next != null) {
            next.h = pred;
        }
        if (this.f == null) {
            this.e = null;
            return true;
        }
        s sVar2 = this.e;
        s sVar3 = sVar2;
        if (!(sVar2 == null || sVar3.g == null)) {
            ConcurrentHashMap.TreeNode<K, V> treeNode = sVar3.f;
            ConcurrentHashMap.TreeNode<K, V> rl = treeNode;
            if (!(treeNode == null || rl.f == null)) {
                e();
                try {
                    s sVar4 = p.f;
                    s sVar5 = p.g;
                    if (sVar4 != null && sVar5 != null) {
                        s sVar6 = sVar5;
                        while (true) {
                            s sVar7 = sVar6.f;
                            s sVar8 = sVar7;
                            if (sVar7 == null) {
                                break;
                            }
                            sVar6 = sVar8;
                        }
                        boolean c = sVar6.i;
                        sVar6.i = p.i;
                        p.i = c;
                        s sVar9 = sVar6.g;
                        ConcurrentHashMap.TreeNode<K, V> pp = p.e;
                        if (sVar6 == sVar5) {
                            p.e = sVar6;
                            sVar6.g = p;
                        } else {
                            ConcurrentHashMap.TreeNode<K, V> sp = sVar6.e;
                            p.e = sp;
                            if (sp != null) {
                                if (sVar6 == sp.f) {
                                    sp.f = p;
                                } else {
                                    sp.g = p;
                                }
                            }
                            sVar6.g = sVar5;
                            sVar5.e = sVar6;
                        }
                        p.f = null;
                        p.g = sVar9;
                        if (sVar9 != null) {
                            sVar9.e = p;
                        }
                        sVar6.f = sVar4;
                        sVar4.e = sVar6;
                        sVar6.e = pp;
                        if (pp == null) {
                            sVar3 = sVar6;
                        } else if (p == pp.f) {
                            pp.f = sVar6;
                        } else {
                            pp.g = sVar6;
                        }
                        if (sVar9 != null) {
                            sVar = sVar9;
                        } else {
                            sVar = p;
                        }
                    } else if (sVar4 != null) {
                        sVar = sVar4;
                    } else if (sVar5 != null) {
                        sVar = sVar5;
                    } else {
                        sVar = p;
                    }
                    if (sVar != p) {
                        ConcurrentHashMap.TreeNode<K, V> pp2 = p.e;
                        sVar.e = pp2;
                        if (pp2 == null) {
                            sVar3 = sVar;
                        } else if (p == pp2.f) {
                            pp2.f = sVar;
                        } else {
                            pp2.g = sVar;
                        }
                        p.e = null;
                        p.g = null;
                        p.f = null;
                    }
                    this.e = p.i ? sVar3 : b(sVar3, sVar);
                    if (p == sVar) {
                        ConcurrentHashMap.TreeNode<K, V> treeNode2 = p.e;
                        ConcurrentHashMap.TreeNode<K, V> pp3 = treeNode2;
                        if (treeNode2 != null) {
                            if (p == pp3.f) {
                                pp3.f = null;
                            } else if (p == pp3.g) {
                                pp3.g = null;
                            }
                            p.e = null;
                        }
                    }
                    k();
                    return false;
                } catch (Throwable th) {
                    k();
                    throw th;
                }
            }
        }
        return true;
    }

    static s h(ConcurrentHashMap.TreeNode<K, V> root, s p) {
        if (p != null) {
            ConcurrentHashMap.TreeNode<K, V> treeNode = p.g;
            ConcurrentHashMap.TreeNode<K, V> r = treeNode;
            if (treeNode != null) {
                ConcurrentHashMap.TreeNode<K, V> treeNode2 = r.f;
                p.g = treeNode2;
                ConcurrentHashMap.TreeNode<K, V> rl = treeNode2;
                if (treeNode2 != null) {
                    rl.e = p;
                }
                ConcurrentHashMap.TreeNode<K, V> treeNode3 = p.e;
                r.e = treeNode3;
                ConcurrentHashMap.TreeNode<K, V> pp = treeNode3;
                if (treeNode3 == null) {
                    root = r;
                    r.i = false;
                } else if (pp.f == p) {
                    pp.f = r;
                } else {
                    pp.g = r;
                }
                r.f = p;
                p.e = r;
            }
        }
        return root;
    }

    static s i(ConcurrentHashMap.TreeNode<K, V> root, s p) {
        if (p != null) {
            ConcurrentHashMap.TreeNode<K, V> treeNode = p.f;
            ConcurrentHashMap.TreeNode<K, V> l = treeNode;
            if (treeNode != null) {
                ConcurrentHashMap.TreeNode<K, V> treeNode2 = l.g;
                p.f = treeNode2;
                ConcurrentHashMap.TreeNode<K, V> lr = treeNode2;
                if (treeNode2 != null) {
                    lr.e = p;
                }
                ConcurrentHashMap.TreeNode<K, V> treeNode3 = p.e;
                l.e = treeNode3;
                ConcurrentHashMap.TreeNode<K, V> pp = treeNode3;
                if (treeNode3 == null) {
                    root = l;
                    l.i = false;
                } else if (pp.g == p) {
                    pp.g = l;
                } else {
                    pp.f = l;
                }
                l.g = p;
                p.e = l;
            }
        }
        return root;
    }

    static s c(ConcurrentHashMap.TreeNode<K, V> root, ConcurrentHashMap.TreeNode<K, V> x) {
        x.i = true;
        while (true) {
            ConcurrentHashMap.TreeNode<K, V> treeNode = x.e;
            ConcurrentHashMap.TreeNode<K, V> xp = treeNode;
            if (treeNode != null) {
                if (!xp.i) {
                    break;
                }
                ConcurrentHashMap.TreeNode<K, V> treeNode2 = xp.e;
                ConcurrentHashMap.TreeNode<K, V> xpp = treeNode2;
                if (treeNode2 == null) {
                    break;
                }
                ConcurrentHashMap.TreeNode<K, V> treeNode3 = xpp.f;
                ConcurrentHashMap.TreeNode<K, V> xppl = treeNode3;
                ConcurrentHashMap.TreeNode<K, V> treeNode4 = null;
                if (xp == treeNode3) {
                    ConcurrentHashMap.TreeNode<K, V> treeNode5 = xpp.g;
                    ConcurrentHashMap.TreeNode<K, V> xppr = treeNode5;
                    if (treeNode5 == null || !xppr.i) {
                        if (x == xp.g) {
                            x = xp;
                            root = h(root, xp);
                            ConcurrentHashMap.TreeNode<K, V> treeNode6 = x.e;
                            xp = treeNode6;
                            if (treeNode6 != null) {
                                treeNode4 = xp.e;
                            }
                            xpp = treeNode4;
                        }
                        if (xp != null) {
                            xp.i = false;
                            if (xpp != null) {
                                xpp.i = true;
                                root = i(root, xpp);
                            }
                        }
                    } else {
                        xppr.i = false;
                        xp.i = false;
                        xpp.i = true;
                        x = xpp;
                    }
                } else if (xppl == null || !xppl.i) {
                    if (x == xp.f) {
                        x = xp;
                        root = i(root, xp);
                        ConcurrentHashMap.TreeNode<K, V> treeNode7 = x.e;
                        xp = treeNode7;
                        if (treeNode7 != null) {
                            treeNode4 = xp.e;
                        }
                        xpp = treeNode4;
                    }
                    if (xp != null) {
                        xp.i = false;
                        if (xpp != null) {
                            xpp.i = true;
                            root = h(root, xpp);
                        }
                    }
                } else {
                    xppl.i = false;
                    xp.i = false;
                    xpp.i = true;
                    x = xpp;
                }
            } else {
                x.i = false;
                return x;
            }
        }
        return root;
    }

    static s b(ConcurrentHashMap.TreeNode<K, V> root, ConcurrentHashMap.TreeNode<K, V> x) {
        while (x != null && x != root) {
            ConcurrentHashMap.TreeNode<K, V> treeNode = x.e;
            ConcurrentHashMap.TreeNode<K, V> xp = treeNode;
            if (treeNode == null) {
                x.i = false;
                return x;
            } else if (x.i) {
                x.i = false;
                return root;
            } else {
                ConcurrentHashMap.TreeNode<K, V> treeNode2 = xp.f;
                ConcurrentHashMap.TreeNode<K, V> xpl = treeNode2;
                ConcurrentHashMap.TreeNode<K, V> treeNode3 = null;
                if (treeNode2 == x) {
                    ConcurrentHashMap.TreeNode<K, V> treeNode4 = xp.g;
                    ConcurrentHashMap.TreeNode<K, V> xpr = treeNode4;
                    if (treeNode4 != null && xpr.i) {
                        xpr.i = false;
                        xp.i = true;
                        root = h(root, xp);
                        ConcurrentHashMap.TreeNode<K, V> treeNode5 = x.e;
                        xp = treeNode5;
                        xpr = treeNode5 == null ? null : xp.g;
                    }
                    if (xpr == null) {
                        x = xp;
                    } else {
                        ConcurrentHashMap.TreeNode<K, V> sl = xpr.f;
                        ConcurrentHashMap.TreeNode<K, V> sr = xpr.g;
                        if ((sr == null || !sr.i) && (sl == null || !sl.i)) {
                            xpr.i = true;
                            x = xp;
                        } else {
                            if (sr == null || !sr.i) {
                                if (sl != null) {
                                    sl.i = false;
                                }
                                xpr.i = true;
                                root = i(root, xpr);
                                ConcurrentHashMap.TreeNode<K, V> treeNode6 = x.e;
                                xp = treeNode6;
                                if (treeNode6 != null) {
                                    treeNode3 = xp.g;
                                }
                                xpr = treeNode3;
                            }
                            if (xpr != null) {
                                xpr.i = xp == null ? false : xp.i;
                                ConcurrentHashMap.TreeNode<K, V> treeNode7 = xpr.g;
                                ConcurrentHashMap.TreeNode<K, V> sr2 = treeNode7;
                                if (treeNode7 != null) {
                                    sr2.i = false;
                                }
                            }
                            if (xp != null) {
                                xp.i = false;
                                root = h(root, xp);
                            }
                            x = root;
                        }
                    }
                } else {
                    if (xpl != null && xpl.i) {
                        xpl.i = false;
                        xp.i = true;
                        root = i(root, xp);
                        ConcurrentHashMap.TreeNode<K, V> treeNode8 = x.e;
                        xp = treeNode8;
                        xpl = treeNode8 == null ? null : xp.f;
                    }
                    if (xpl == null) {
                        x = xp;
                    } else {
                        ConcurrentHashMap.TreeNode<K, V> sl2 = xpl.f;
                        ConcurrentHashMap.TreeNode<K, V> sr3 = xpl.g;
                        if ((sl2 == null || !sl2.i) && (sr3 == null || !sr3.i)) {
                            xpl.i = true;
                            x = xp;
                        } else {
                            if (sl2 == null || !sl2.i) {
                                if (sr3 != null) {
                                    sr3.i = false;
                                }
                                xpl.i = true;
                                root = h(root, xpl);
                                ConcurrentHashMap.TreeNode<K, V> treeNode9 = x.e;
                                xp = treeNode9;
                                if (treeNode9 != null) {
                                    treeNode3 = xp.f;
                                }
                                xpl = treeNode3;
                            }
                            if (xpl != null) {
                                xpl.i = xp == null ? false : xp.i;
                                ConcurrentHashMap.TreeNode<K, V> treeNode10 = xpl.f;
                                ConcurrentHashMap.TreeNode<K, V> sl3 = treeNode10;
                                if (treeNode10 != null) {
                                    sl3.i = false;
                                }
                            }
                            if (xp != null) {
                                xp.i = false;
                                root = i(root, xp);
                            }
                            x = root;
                        }
                    }
                }
            }
        }
        return root;
    }
}
