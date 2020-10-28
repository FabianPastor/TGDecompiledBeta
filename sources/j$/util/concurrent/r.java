package j$.util.concurrent;

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
        try {
            Unsafe c = x.c();
            h = c;
            i = c.objectFieldOffset(r.class.getDeclaredField("lockState"));
        } catch (Exception e2) {
            throw new Error(e2);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002f, code lost:
        r6 = j$.util.concurrent.ConcurrentHashMap.c(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0035, code lost:
        r8 = j$.util.concurrent.ConcurrentHashMap.d(r6, r3, r7);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    r(j$.util.concurrent.s r10) {
        /*
            r9 = this;
            r0 = -2
            r1 = 0
            r9.<init>(r0, r1, r1, r1)
            r9.f = r10
            r0 = r1
        L_0x0008:
            if (r10 == 0) goto L_0x005c
            j$.util.concurrent.m r2 = r10.d
            j$.util.concurrent.s r2 = (j$.util.concurrent.s) r2
            r10.g = r1
            r10.f = r1
            if (r0 != 0) goto L_0x001b
            r10.e = r1
            r0 = 0
            r10.i = r0
        L_0x0019:
            r0 = r10
            goto L_0x0058
        L_0x001b:
            java.lang.Object r3 = r10.b
            int r4 = r10.a
            r5 = r0
            r6 = r1
        L_0x0021:
            java.lang.Object r7 = r5.b
            int r8 = r5.a
            if (r8 <= r4) goto L_0x0029
            r7 = -1
            goto L_0x0041
        L_0x0029:
            if (r8 >= r4) goto L_0x002d
            r7 = 1
            goto L_0x0041
        L_0x002d:
            if (r6 != 0) goto L_0x0035
            java.lang.Class r6 = j$.util.concurrent.ConcurrentHashMap.c(r3)
            if (r6 == 0) goto L_0x003b
        L_0x0035:
            int r8 = j$.util.concurrent.ConcurrentHashMap.d(r6, r3, r7)
            if (r8 != 0) goto L_0x0040
        L_0x003b:
            int r7 = j(r3, r7)
            goto L_0x0041
        L_0x0040:
            r7 = r8
        L_0x0041:
            if (r7 > 0) goto L_0x0046
            j$.util.concurrent.s r8 = r5.f
            goto L_0x0048
        L_0x0046:
            j$.util.concurrent.s r8 = r5.g
        L_0x0048:
            if (r8 != 0) goto L_0x005a
            r10.e = r5
            if (r7 > 0) goto L_0x0051
            r5.f = r10
            goto L_0x0053
        L_0x0051:
            r5.g = r10
        L_0x0053:
            j$.util.concurrent.s r10 = c(r0, r10)
            goto L_0x0019
        L_0x0058:
            r10 = r2
            goto L_0x0008
        L_0x005a:
            r5 = r8
            goto L_0x0021
        L_0x005c:
            r9.e = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.r.<init>(j$.util.concurrent.s):void");
    }

    static s b(s sVar, s sVar2) {
        s sVar3;
        while (sVar2 != null && sVar2 != sVar) {
            s sVar4 = sVar2.e;
            if (sVar4 == null) {
                sVar2.i = false;
                return sVar2;
            } else if (sVar2.i) {
                sVar2.i = false;
                return sVar;
            } else {
                s sVar5 = sVar4.f;
                s sVar6 = null;
                if (sVar5 == sVar2) {
                    sVar3 = sVar4.g;
                    if (sVar3 != null && sVar3.i) {
                        sVar3.i = false;
                        sVar4.i = true;
                        sVar = h(sVar, sVar4);
                        sVar4 = sVar2.e;
                        sVar3 = sVar4 == null ? null : sVar4.g;
                    }
                    if (sVar3 != null) {
                        s sVar7 = sVar3.f;
                        s sVar8 = sVar3.g;
                        if ((sVar8 != null && sVar8.i) || (sVar7 != null && sVar7.i)) {
                            if (sVar8 == null || !sVar8.i) {
                                if (sVar7 != null) {
                                    sVar7.i = false;
                                }
                                sVar3.i = true;
                                sVar = i(sVar, sVar3);
                                sVar4 = sVar2.e;
                                if (sVar4 != null) {
                                    sVar6 = sVar4.g;
                                }
                            } else {
                                sVar6 = sVar3;
                            }
                            if (sVar6 != null) {
                                sVar6.i = sVar4 == null ? false : sVar4.i;
                                s sVar9 = sVar6.g;
                                if (sVar9 != null) {
                                    sVar9.i = false;
                                }
                            }
                            if (sVar4 != null) {
                                sVar4.i = false;
                                sVar = h(sVar, sVar4);
                            }
                            sVar2 = sVar;
                            sVar = sVar2;
                        }
                        sVar3.i = true;
                    }
                } else {
                    if (sVar5 != null && sVar5.i) {
                        sVar5.i = false;
                        sVar4.i = true;
                        sVar = i(sVar, sVar4);
                        sVar4 = sVar2.e;
                        sVar5 = sVar4 == null ? null : sVar4.f;
                    }
                    if (sVar3 != null) {
                        s sVar10 = sVar3.f;
                        s sVar11 = sVar3.g;
                        if ((sVar10 != null && sVar10.i) || (sVar11 != null && sVar11.i)) {
                            if (sVar10 == null || !sVar10.i) {
                                if (sVar11 != null) {
                                    sVar11.i = false;
                                }
                                sVar3.i = true;
                                sVar = h(sVar, sVar3);
                                sVar4 = sVar2.e;
                                if (sVar4 != null) {
                                    sVar6 = sVar4.f;
                                }
                            } else {
                                sVar6 = sVar3;
                            }
                            if (sVar6 != null) {
                                sVar6.i = sVar4 == null ? false : sVar4.i;
                                s sVar12 = sVar6.f;
                                if (sVar12 != null) {
                                    sVar12.i = false;
                                }
                            }
                            if (sVar4 != null) {
                                sVar4.i = false;
                                sVar = i(sVar, sVar4);
                            }
                            sVar2 = sVar;
                            sVar = sVar2;
                        }
                        sVar3.i = true;
                    }
                }
                sVar2 = sVar4;
            }
        }
        return sVar;
    }

    static s c(s sVar, s sVar2) {
        s sVar3;
        sVar2.i = true;
        while (true) {
            s sVar4 = sVar2.e;
            if (sVar4 == null) {
                sVar2.i = false;
                return sVar2;
            } else if (!sVar4.i || (sVar3 = sVar4.e) == null) {
                return sVar;
            } else {
                s sVar5 = sVar3.f;
                if (sVar4 == sVar5) {
                    sVar5 = sVar3.g;
                    if (sVar5 == null || !sVar5.i) {
                        if (sVar2 == sVar4.g) {
                            sVar = h(sVar, sVar4);
                            s sVar6 = sVar4.e;
                            sVar3 = sVar6 == null ? null : sVar6.e;
                            s sVar7 = sVar4;
                            sVar4 = sVar6;
                            sVar2 = sVar7;
                        }
                        if (sVar4 != null) {
                            sVar4.i = false;
                            if (sVar3 != null) {
                                sVar3.i = true;
                                sVar = i(sVar, sVar3);
                            }
                        }
                    }
                } else if (sVar5 == null || !sVar5.i) {
                    if (sVar2 == sVar4.f) {
                        sVar = i(sVar, sVar4);
                        s sVar8 = sVar4.e;
                        sVar3 = sVar8 == null ? null : sVar8.e;
                        s sVar9 = sVar4;
                        sVar4 = sVar8;
                        sVar2 = sVar9;
                    }
                    if (sVar4 != null) {
                        sVar4.i = false;
                        if (sVar3 != null) {
                            sVar3.i = true;
                            sVar = h(sVar, sVar3);
                        }
                    }
                }
                sVar5.i = false;
                sVar4.i = false;
                sVar3.i = true;
                sVar2 = sVar3;
            }
        }
        return sVar;
    }

    private final void d() {
        boolean z = false;
        while (true) {
            int i2 = this.lockState;
            if ((i2 & -3) == 0) {
                if (h.compareAndSwapInt(this, i, i2, 1)) {
                    break;
                }
            } else if ((i2 & 2) == 0) {
                if (h.compareAndSwapInt(this, i, i2, i2 | 2)) {
                    z = true;
                    this.g = Thread.currentThread();
                }
            } else if (z) {
                LockSupport.park(this);
            }
        }
        if (z) {
            this.g = null;
        }
    }

    private final void e() {
        if (!h.compareAndSwapInt(this, i, 0, 1)) {
            d();
        }
    }

    static s h(s sVar, s sVar2) {
        s sVar3;
        if (!(sVar2 == null || (sVar3 = sVar2.g) == null)) {
            s sVar4 = sVar3.f;
            sVar2.g = sVar4;
            if (sVar4 != null) {
                sVar4.e = sVar2;
            }
            s sVar5 = sVar2.e;
            sVar3.e = sVar5;
            if (sVar5 == null) {
                sVar3.i = false;
                sVar = sVar3;
            } else if (sVar5.f == sVar2) {
                sVar5.f = sVar3;
            } else {
                sVar5.g = sVar3;
            }
            sVar3.f = sVar2;
            sVar2.e = sVar3;
        }
        return sVar;
    }

    static s i(s sVar, s sVar2) {
        s sVar3;
        if (!(sVar2 == null || (sVar3 = sVar2.f) == null)) {
            s sVar4 = sVar3.g;
            sVar2.f = sVar4;
            if (sVar4 != null) {
                sVar4.e = sVar2;
            }
            s sVar5 = sVar2.e;
            sVar3.e = sVar5;
            if (sVar5 == null) {
                sVar3.i = false;
                sVar = sVar3;
            } else if (sVar5.g == sVar2) {
                sVar5.g = sVar3;
            } else {
                sVar5.f = sVar3;
            }
            sVar3.g = sVar2;
            sVar2.e = sVar3;
        }
        return sVar;
    }

    static int j(Object obj, Object obj2) {
        int compareTo;
        return (obj == null || obj2 == null || (compareTo = obj.getClass().getName().compareTo(obj2.getClass().getName())) == 0) ? System.identityHashCode(obj) <= System.identityHashCode(obj2) ? -1 : 1 : compareTo;
    }

    /* access modifiers changed from: package-private */
    public final m a(int i2, Object obj) {
        Thread thread;
        Thread thread2;
        Object obj2;
        m mVar = this.f;
        while (true) {
            s sVar = null;
            if (mVar == null) {
                return null;
            }
            int i3 = this.lockState;
            if ((i3 & 3) == 0) {
                Unsafe unsafe = h;
                long j = i;
                if (unsafe.compareAndSwapInt(this, j, i3, i3 + 4)) {
                    try {
                        s sVar2 = this.e;
                        if (sVar2 != null) {
                            sVar = sVar2.b(i2, obj, (Class) null);
                        }
                        if (x.a(unsafe, this, j, -4) == 6 && (thread2 = this.g) != null) {
                            LockSupport.unpark(thread2);
                        }
                        return sVar;
                    } catch (Throwable th) {
                        if (x.a(h, this, i, -4) == 6 && (thread = this.g) != null) {
                            LockSupport.unpark(thread);
                        }
                        throw th;
                    }
                }
            } else if (mVar.a != i2 || ((obj2 = mVar.b) != obj && (obj2 == null || !obj.equals(obj2)))) {
                mVar = mVar.d;
            }
        }
        return mVar;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0060, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00a3, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final j$.util.concurrent.s f(int r16, java.lang.Object r17, java.lang.Object r18) {
        /*
            r15 = this;
            r1 = r15
            r0 = r16
            r4 = r17
            j$.util.concurrent.s r2 = r1.e
            r8 = 0
            r9 = 0
            r10 = r2
            r2 = r9
            r3 = 0
        L_0x000c:
            if (r10 != 0) goto L_0x0022
            j$.util.concurrent.s r8 = new j$.util.concurrent.s
            r6 = 0
            r7 = 0
            r2 = r8
            r3 = r16
            r4 = r17
            r5 = r18
            r2.<init>(r3, r4, r5, r6, r7)
            r1.e = r8
            r1.f = r8
            goto L_0x00a3
        L_0x0022:
            int r5 = r10.a
            r11 = 1
            if (r5 <= r0) goto L_0x002a
            r5 = -1
            r12 = -1
            goto L_0x0069
        L_0x002a:
            if (r5 >= r0) goto L_0x002e
            r12 = 1
            goto L_0x0069
        L_0x002e:
            java.lang.Object r5 = r10.b
            if (r5 == r4) goto L_0x00ab
            if (r5 == 0) goto L_0x003c
            boolean r6 = r4.equals(r5)
            if (r6 == 0) goto L_0x003c
            goto L_0x00ab
        L_0x003c:
            if (r2 != 0) goto L_0x0044
            java.lang.Class r2 = j$.util.concurrent.ConcurrentHashMap.c(r17)
            if (r2 == 0) goto L_0x004a
        L_0x0044:
            int r6 = j$.util.concurrent.ConcurrentHashMap.d(r2, r4, r5)
            if (r6 != 0) goto L_0x0068
        L_0x004a:
            if (r3 != 0) goto L_0x0062
            j$.util.concurrent.s r3 = r10.f
            if (r3 == 0) goto L_0x0056
            j$.util.concurrent.s r3 = r3.b(r0, r4, r2)
            if (r3 != 0) goto L_0x0060
        L_0x0056:
            j$.util.concurrent.s r3 = r10.g
            if (r3 == 0) goto L_0x0061
            j$.util.concurrent.s r3 = r3.b(r0, r4, r2)
            if (r3 == 0) goto L_0x0061
        L_0x0060:
            return r3
        L_0x0061:
            r3 = 1
        L_0x0062:
            int r5 = j(r4, r5)
            r12 = r5
            goto L_0x0069
        L_0x0068:
            r12 = r6
        L_0x0069:
            if (r12 > 0) goto L_0x006e
            j$.util.concurrent.s r5 = r10.f
            goto L_0x0070
        L_0x006e:
            j$.util.concurrent.s r5 = r10.g
        L_0x0070:
            if (r5 != 0) goto L_0x00a8
            j$.util.concurrent.s r13 = r1.f
            j$.util.concurrent.s r14 = new j$.util.concurrent.s
            r2 = r14
            r3 = r16
            r4 = r17
            r5 = r18
            r6 = r13
            r7 = r10
            r2.<init>(r3, r4, r5, r6, r7)
            r1.f = r14
            if (r13 == 0) goto L_0x0088
            r13.h = r14
        L_0x0088:
            if (r12 > 0) goto L_0x008d
            r10.f = r14
            goto L_0x008f
        L_0x008d:
            r10.g = r14
        L_0x008f:
            boolean r0 = r10.i
            if (r0 != 0) goto L_0x0096
            r14.i = r11
            goto L_0x00a3
        L_0x0096:
            r15.e()
            j$.util.concurrent.s r0 = r1.e     // Catch:{ all -> 0x00a4 }
            j$.util.concurrent.s r0 = c(r0, r14)     // Catch:{ all -> 0x00a4 }
            r1.e = r0     // Catch:{ all -> 0x00a4 }
            r1.lockState = r8
        L_0x00a3:
            return r9
        L_0x00a4:
            r0 = move-exception
            r1.lockState = r8
            throw r0
        L_0x00a8:
            r10 = r5
            goto L_0x000c
        L_0x00ab:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.r.f(int, java.lang.Object, java.lang.Object):j$.util.concurrent.s");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x008e A[Catch:{ all -> 0x00c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00a9 A[Catch:{ all -> 0x00c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00aa A[Catch:{ all -> 0x00c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x00ba A[Catch:{ all -> 0x00c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00bd A[Catch:{ all -> 0x00c8 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean g(j$.util.concurrent.s r11) {
        /*
            r10 = this;
            j$.util.concurrent.m r0 = r11.d
            j$.util.concurrent.s r0 = (j$.util.concurrent.s) r0
            j$.util.concurrent.s r1 = r11.h
            if (r1 != 0) goto L_0x000b
            r10.f = r0
            goto L_0x000d
        L_0x000b:
            r1.d = r0
        L_0x000d:
            if (r0 == 0) goto L_0x0011
            r0.h = r1
        L_0x0011:
            j$.util.concurrent.s r0 = r10.f
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x001a
            r10.e = r2
            return r1
        L_0x001a:
            j$.util.concurrent.s r0 = r10.e
            if (r0 == 0) goto L_0x00cc
            j$.util.concurrent.s r3 = r0.g
            if (r3 == 0) goto L_0x00cc
            j$.util.concurrent.s r3 = r0.f
            if (r3 == 0) goto L_0x00cc
            j$.util.concurrent.s r3 = r3.f
            if (r3 != 0) goto L_0x002c
            goto L_0x00cc
        L_0x002c:
            r10.e()
            r1 = 0
            j$.util.concurrent.s r3 = r11.f     // Catch:{ all -> 0x00c8 }
            j$.util.concurrent.s r4 = r11.g     // Catch:{ all -> 0x00c8 }
            if (r3 == 0) goto L_0x0084
            if (r4 == 0) goto L_0x0084
            r5 = r4
        L_0x0039:
            j$.util.concurrent.s r6 = r5.f     // Catch:{ all -> 0x00c8 }
            if (r6 == 0) goto L_0x003f
            r5 = r6
            goto L_0x0039
        L_0x003f:
            boolean r6 = r5.i     // Catch:{ all -> 0x00c8 }
            boolean r7 = r11.i     // Catch:{ all -> 0x00c8 }
            r5.i = r7     // Catch:{ all -> 0x00c8 }
            r11.i = r6     // Catch:{ all -> 0x00c8 }
            j$.util.concurrent.s r6 = r5.g     // Catch:{ all -> 0x00c8 }
            j$.util.concurrent.s r7 = r11.e     // Catch:{ all -> 0x00c8 }
            if (r5 != r4) goto L_0x0052
            r11.e = r5     // Catch:{ all -> 0x00c8 }
            r5.g = r11     // Catch:{ all -> 0x00c8 }
            goto L_0x0065
        L_0x0052:
            j$.util.concurrent.s r8 = r5.e     // Catch:{ all -> 0x00c8 }
            r11.e = r8     // Catch:{ all -> 0x00c8 }
            if (r8 == 0) goto L_0x0061
            j$.util.concurrent.s r9 = r8.f     // Catch:{ all -> 0x00c8 }
            if (r5 != r9) goto L_0x005f
            r8.f = r11     // Catch:{ all -> 0x00c8 }
            goto L_0x0061
        L_0x005f:
            r8.g = r11     // Catch:{ all -> 0x00c8 }
        L_0x0061:
            r5.g = r4     // Catch:{ all -> 0x00c8 }
            r4.e = r5     // Catch:{ all -> 0x00c8 }
        L_0x0065:
            r11.f = r2     // Catch:{ all -> 0x00c8 }
            r11.g = r6     // Catch:{ all -> 0x00c8 }
            if (r6 == 0) goto L_0x006d
            r6.e = r11     // Catch:{ all -> 0x00c8 }
        L_0x006d:
            r5.f = r3     // Catch:{ all -> 0x00c8 }
            r3.e = r5     // Catch:{ all -> 0x00c8 }
            r5.e = r7     // Catch:{ all -> 0x00c8 }
            if (r7 != 0) goto L_0x0077
            r0 = r5
            goto L_0x0080
        L_0x0077:
            j$.util.concurrent.s r3 = r7.f     // Catch:{ all -> 0x00c8 }
            if (r11 != r3) goto L_0x007e
            r7.f = r5     // Catch:{ all -> 0x00c8 }
            goto L_0x0080
        L_0x007e:
            r7.g = r5     // Catch:{ all -> 0x00c8 }
        L_0x0080:
            if (r6 == 0) goto L_0x008b
            r3 = r6
            goto L_0x008c
        L_0x0084:
            if (r3 == 0) goto L_0x0087
            goto L_0x008c
        L_0x0087:
            if (r4 == 0) goto L_0x008b
            r3 = r4
            goto L_0x008c
        L_0x008b:
            r3 = r11
        L_0x008c:
            if (r3 == r11) goto L_0x00a5
            j$.util.concurrent.s r4 = r11.e     // Catch:{ all -> 0x00c8 }
            r3.e = r4     // Catch:{ all -> 0x00c8 }
            if (r4 != 0) goto L_0x0096
            r0 = r3
            goto L_0x009f
        L_0x0096:
            j$.util.concurrent.s r5 = r4.f     // Catch:{ all -> 0x00c8 }
            if (r11 != r5) goto L_0x009d
            r4.f = r3     // Catch:{ all -> 0x00c8 }
            goto L_0x009f
        L_0x009d:
            r4.g = r3     // Catch:{ all -> 0x00c8 }
        L_0x009f:
            r11.e = r2     // Catch:{ all -> 0x00c8 }
            r11.g = r2     // Catch:{ all -> 0x00c8 }
            r11.f = r2     // Catch:{ all -> 0x00c8 }
        L_0x00a5:
            boolean r4 = r11.i     // Catch:{ all -> 0x00c8 }
            if (r4 == 0) goto L_0x00aa
            goto L_0x00ae
        L_0x00aa:
            j$.util.concurrent.s r0 = b(r0, r3)     // Catch:{ all -> 0x00c8 }
        L_0x00ae:
            r10.e = r0     // Catch:{ all -> 0x00c8 }
            if (r11 != r3) goto L_0x00c5
            j$.util.concurrent.s r0 = r11.e     // Catch:{ all -> 0x00c8 }
            if (r0 == 0) goto L_0x00c5
            j$.util.concurrent.s r3 = r0.f     // Catch:{ all -> 0x00c8 }
            if (r11 != r3) goto L_0x00bd
            r0.f = r2     // Catch:{ all -> 0x00c8 }
            goto L_0x00c3
        L_0x00bd:
            j$.util.concurrent.s r3 = r0.g     // Catch:{ all -> 0x00c8 }
            if (r11 != r3) goto L_0x00c3
            r0.g = r2     // Catch:{ all -> 0x00c8 }
        L_0x00c3:
            r11.e = r2     // Catch:{ all -> 0x00c8 }
        L_0x00c5:
            r10.lockState = r1
            return r1
        L_0x00c8:
            r11 = move-exception
            r10.lockState = r1
            throw r11
        L_0x00cc:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.r.g(j$.util.concurrent.s):boolean");
    }
}
