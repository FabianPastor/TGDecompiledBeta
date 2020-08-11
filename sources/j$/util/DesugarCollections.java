package j$.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class DesugarCollections {
    public static final Class a = Collections.synchronizedCollection(new ArrayList()).getClass();
    static final Class b = Collections.synchronizedList(new LinkedList()).getClass();
    private static final Field c;
    private static final Field d;
    /* access modifiers changed from: private */
    public static final Constructor e;
    /* access modifiers changed from: private */
    public static final Constructor f;

    private DesugarCollections() {
    }

    static {
        Class<Object> cls = Object.class;
        Field e2 = e(a, "mutex");
        c = e2;
        if (e2 != null) {
            e2.setAccessible(true);
        }
        Field e3 = e(a, "c");
        d = e3;
        if (e3 != null) {
            e3.setAccessible(true);
        }
        Constructor d2 = d(Collections.synchronizedSet(new HashSet()).getClass(), Set.class, cls);
        f = d2;
        if (d2 != null) {
            d2.setAccessible(true);
        }
        Constructor d3 = d(a, Collection.class, cls);
        e = d3;
        if (d3 != null) {
            d3.setAccessible(true);
        }
    }

    private static Field e(Class clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e2) {
            return null;
        }
    }

    private static Constructor d(Class clazz, Class... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e2) {
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0030, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean f(java.util.Collection r3, j$.util.function.Predicate r4) {
        /*
            java.lang.reflect.Field r0 = c
            if (r0 != 0) goto L_0x001a
            java.lang.reflect.Field r0 = d     // Catch:{ IllegalAccessException -> 0x0011 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0011 }
            java.util.Collection r0 = (java.util.Collection) r0     // Catch:{ IllegalAccessException -> 0x0011 }
            boolean r0 = j$.util.CLASSNAMEk.b(r0, r4)     // Catch:{ IllegalAccessException -> 0x0011 }
            return r0
        L_0x0011:
            r0 = move-exception
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection removeIf fall-back."
            r1.<init>(r2, r0)
            throw r1
        L_0x001a:
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0032 }
            monitor-enter(r0)     // Catch:{ IllegalAccessException -> 0x0032 }
            java.lang.reflect.Field r1 = d     // Catch:{ all -> 0x002d }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x002d }
            java.util.Collection r1 = (java.util.Collection) r1     // Catch:{ all -> 0x002d }
            boolean r1 = j$.util.CLASSNAMEk.b(r1, r4)     // Catch:{ all -> 0x002d }
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            return r1
        L_0x002d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            throw r1     // Catch:{ IllegalAccessException -> 0x0030 }
        L_0x0030:
            r0 = move-exception
            goto L_0x0033
        L_0x0032:
            r0 = move-exception
        L_0x0033:
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection removeIf."
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.DesugarCollections.f(java.util.Collection, j$.util.function.Predicate):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002f, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void c(java.lang.Iterable r3, j$.util.function.Consumer r4) {
        /*
            java.lang.reflect.Field r0 = c
            if (r0 != 0) goto L_0x0019
            java.lang.reflect.Field r0 = d     // Catch:{ IllegalAccessException -> 0x0010 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0010 }
            java.util.Collection r0 = (java.util.Collection) r0     // Catch:{ IllegalAccessException -> 0x0010 }
            j$.util.CLASSNAMEk.a(r0, r4)     // Catch:{ IllegalAccessException -> 0x0010 }
            return
        L_0x0010:
            r0 = move-exception
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection forEach fall-back."
            r1.<init>(r2, r0)
            throw r1
        L_0x0019:
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0031 }
            monitor-enter(r0)     // Catch:{ IllegalAccessException -> 0x0031 }
            java.lang.reflect.Field r1 = d     // Catch:{ all -> 0x002c }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x002c }
            java.util.Collection r1 = (java.util.Collection) r1     // Catch:{ all -> 0x002c }
            j$.util.CLASSNAMEk.a(r1, r4)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1     // Catch:{ IllegalAccessException -> 0x002f }
        L_0x002f:
            r0 = move-exception
            goto L_0x0032
        L_0x0031:
            r0 = move-exception
        L_0x0032:
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection forEach."
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.DesugarCollections.c(java.lang.Iterable, j$.util.function.Consumer):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002f, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void g(java.util.List r3, j$.util.function.UnaryOperator r4) {
        /*
            java.lang.reflect.Field r0 = c
            if (r0 != 0) goto L_0x0019
            java.lang.reflect.Field r0 = d     // Catch:{ IllegalAccessException -> 0x0010 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0010 }
            java.util.List r0 = (java.util.List) r0     // Catch:{ IllegalAccessException -> 0x0010 }
            j$.util.CLASSNAMEv.a(r0, r4)     // Catch:{ IllegalAccessException -> 0x0010 }
            return
        L_0x0010:
            r0 = move-exception
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized list replaceAll fall-back."
            r1.<init>(r2, r0)
            throw r1
        L_0x0019:
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0031 }
            monitor-enter(r0)     // Catch:{ IllegalAccessException -> 0x0031 }
            java.lang.reflect.Field r1 = d     // Catch:{ all -> 0x002c }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x002c }
            java.util.List r1 = (java.util.List) r1     // Catch:{ all -> 0x002c }
            j$.util.CLASSNAMEv.a(r1, r4)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1     // Catch:{ IllegalAccessException -> 0x002f }
        L_0x002f:
            r0 = move-exception
            goto L_0x0032
        L_0x0031:
            r0 = move-exception
        L_0x0032:
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized list replaceAll."
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.DesugarCollections.g(java.util.List, j$.util.function.UnaryOperator):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002f, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void h(java.util.List r3, java.util.Comparator r4) {
        /*
            java.lang.reflect.Field r0 = c
            if (r0 != 0) goto L_0x0019
            java.lang.reflect.Field r0 = d     // Catch:{ IllegalAccessException -> 0x0010 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0010 }
            java.util.List r0 = (java.util.List) r0     // Catch:{ IllegalAccessException -> 0x0010 }
            j$.util.CLASSNAMEv.b(r0, r4)     // Catch:{ IllegalAccessException -> 0x0010 }
            return
        L_0x0010:
            r0 = move-exception
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection sort fall-back."
            r1.<init>(r2, r0)
            throw r1
        L_0x0019:
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0031 }
            monitor-enter(r0)     // Catch:{ IllegalAccessException -> 0x0031 }
            java.lang.reflect.Field r1 = d     // Catch:{ all -> 0x002c }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x002c }
            java.util.List r1 = (java.util.List) r1     // Catch:{ all -> 0x002c }
            j$.util.CLASSNAMEv.b(r1, r4)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1     // Catch:{ IllegalAccessException -> 0x002f }
        L_0x002f:
            r0 = move-exception
            goto L_0x0032
        L_0x0031:
            r0 = move-exception
        L_0x0032:
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized list sort."
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.DesugarCollections.h(java.util.List, java.util.Comparator):void");
    }

    public static Map synchronizedMap(Map m) {
        return new CLASSNAMEo(m);
    }
}
