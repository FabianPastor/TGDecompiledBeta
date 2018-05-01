package org.aspectj.runtime.reflect;

import java.util.Hashtable;
import java.util.StringTokenizer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;

public final class Factory {
    private static Object[] NO_ARGS = new Object[0];
    static /* synthetic */ Class class$java$lang$ClassNotFoundException;
    static Hashtable prims = new Hashtable();
    int count = null;
    String filename;
    Class lexicalClass;
    ClassLoader lookupClassLoader;

    static {
        prims.put("void", Void.TYPE);
        prims.put("boolean", Boolean.TYPE);
        prims.put("byte", Byte.TYPE);
        prims.put("char", Character.TYPE);
        prims.put("short", Short.TYPE);
        prims.put("int", Integer.TYPE);
        prims.put("long", Long.TYPE);
        prims.put("float", Float.TYPE);
        prims.put("double", Double.TYPE);
    }

    static java.lang.Class makeClass(java.lang.String r1, java.lang.ClassLoader r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = "*";
        r0 = r1.equals(r0);
        if (r0 == 0) goto L_0x000a;
    L_0x0008:
        r1 = 0;
        return r1;
    L_0x000a:
        r0 = prims;
        r0 = r0.get(r1);
        r0 = (java.lang.Class) r0;
        if (r0 == 0) goto L_0x0015;
    L_0x0014:
        return r0;
    L_0x0015:
        if (r2 != 0) goto L_0x001c;
    L_0x0017:
        r1 = java.lang.Class.forName(r1);	 Catch:{ ClassNotFoundException -> 0x0022 }
        return r1;	 Catch:{ ClassNotFoundException -> 0x0022 }
    L_0x001c:
        r0 = 0;	 Catch:{ ClassNotFoundException -> 0x0022 }
        r1 = java.lang.Class.forName(r1, r0, r2);	 Catch:{ ClassNotFoundException -> 0x0022 }
        return r1;
    L_0x0022:
        r1 = class$java$lang$ClassNotFoundException;
        if (r1 != 0) goto L_0x002f;
    L_0x0026:
        r1 = "java.lang.ClassNotFoundException";
        r1 = class$(r1);
        class$java$lang$ClassNotFoundException = r1;
        goto L_0x0031;
    L_0x002f:
        r1 = class$java$lang$ClassNotFoundException;
    L_0x0031:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.aspectj.runtime.reflect.Factory.makeClass(java.lang.String, java.lang.ClassLoader):java.lang.Class");
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (String str2) {
            throw new NoClassDefFoundError(str2.getMessage());
        }
    }

    public Factory(String str, Class cls) {
        this.filename = str;
        this.lexicalClass = cls;
        this.lookupClassLoader = cls.getClassLoader();
    }

    public StaticPart makeSJP(String str, Signature signature, int i) {
        int i2 = this.count;
        this.count = i2 + 1;
        return new StaticPartImpl(i2, str, signature, makeSourceLoc(i, -1));
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object obj, Object obj2) {
        return new JoinPointImpl(staticPart, obj, obj2, NO_ARGS);
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object obj, Object obj2, Object obj3) {
        return new JoinPointImpl(staticPart, obj, obj2, new Object[]{obj3});
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object obj, Object obj2, Object[] objArr) {
        return new JoinPointImpl(staticPart, obj, obj2, objArr);
    }

    public MethodSignature makeMethodSig(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        int parseInt = Integer.parseInt(str, 16);
        Class makeClass = makeClass(str3, this.lookupClassLoader);
        StringTokenizer stringTokenizer = new StringTokenizer(str4, ":");
        int countTokens = stringTokenizer.countTokens();
        Class[] clsArr = new Class[countTokens];
        int i = 0;
        for (int i2 = 0; i2 < countTokens; i2++) {
            clsArr[i2] = makeClass(stringTokenizer.nextToken(), r0.lookupClassLoader);
        }
        stringTokenizer = new StringTokenizer(str5, ":");
        countTokens = stringTokenizer.countTokens();
        String[] strArr = new String[countTokens];
        for (int i3 = 0; i3 < countTokens; i3++) {
            strArr[i3] = stringTokenizer.nextToken();
        }
        stringTokenizer = new StringTokenizer(str6, ":");
        countTokens = stringTokenizer.countTokens();
        Class[] clsArr2 = new Class[countTokens];
        while (i < countTokens) {
            clsArr2[i] = makeClass(stringTokenizer.nextToken(), r0.lookupClassLoader);
            i++;
        }
        return new MethodSignatureImpl(parseInt, str2, makeClass, clsArr, strArr, clsArr2, makeClass(str7, r0.lookupClassLoader));
    }

    public SourceLocation makeSourceLoc(int i, int i2) {
        return new SourceLocationImpl(this.lexicalClass, this.filename, i);
    }
}
