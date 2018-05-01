package org.aspectj.runtime.reflect;

import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import org.aspectj.lang.Signature;

abstract class SignatureImpl implements Signature {
    static Class[] EMPTY_CLASS_ARRAY = new Class[0];
    static String[] EMPTY_STRING_ARRAY = new String[0];
    private static boolean useCache = true;
    Class declaringType;
    String declaringTypeName;
    ClassLoader lookupClassLoader = null;
    int modifiers = -1;
    String name;
    Cache stringCache;
    private String stringRep;

    private interface Cache {
        String get(int i);

        void set(int i, String str);
    }

    private static final class CacheImpl implements Cache {
        private SoftReference toStringCacheRef;

        public CacheImpl() {
            makeCache();
        }

        public String get(int i) {
            String[] array = array();
            if (array == null) {
                return 0;
            }
            return array[i];
        }

        public void set(int i, String str) {
            String[] array = array();
            if (array == null) {
                array = makeCache();
            }
            array[i] = str;
        }

        private String[] array() {
            return (String[]) this.toStringCacheRef.get();
        }

        private String[] makeCache() {
            Object obj = new String[3];
            this.toStringCacheRef = new SoftReference(obj);
            return obj;
        }
    }

    protected abstract String createToString(StringMaker stringMaker);

    SignatureImpl(int i, String str, Class cls) {
        this.modifiers = i;
        this.name = str;
        this.declaringType = cls;
    }

    java.lang.String toString(org.aspectj.runtime.reflect.StringMaker r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = useCache;
        if (r0 == 0) goto L_0x001d;
    L_0x0004:
        r0 = r2.stringCache;
        if (r0 != 0) goto L_0x0014;
    L_0x0008:
        r0 = new org.aspectj.runtime.reflect.SignatureImpl$CacheImpl;	 Catch:{ Throwable -> 0x0010 }
        r0.<init>();	 Catch:{ Throwable -> 0x0010 }
        r2.stringCache = r0;	 Catch:{ Throwable -> 0x0010 }
        goto L_0x001d;
    L_0x0010:
        r0 = 0;
        useCache = r0;
        goto L_0x001d;
    L_0x0014:
        r0 = r2.stringCache;
        r1 = r3.cacheOffset;
        r0 = r0.get(r1);
        goto L_0x001e;
    L_0x001d:
        r0 = 0;
    L_0x001e:
        if (r0 != 0) goto L_0x0024;
    L_0x0020:
        r0 = r2.createToString(r3);
    L_0x0024:
        r1 = useCache;
        if (r1 == 0) goto L_0x002f;
    L_0x0028:
        r1 = r2.stringCache;
        r3 = r3.cacheOffset;
        r1.set(r3, r0);
    L_0x002f:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.aspectj.runtime.reflect.SignatureImpl.toString(org.aspectj.runtime.reflect.StringMaker):java.lang.String");
    }

    public final String toString() {
        return toString(StringMaker.middleStringMaker);
    }

    public int getModifiers() {
        if (this.modifiers == -1) {
            this.modifiers = extractInt(0);
        }
        return this.modifiers;
    }

    public String getName() {
        if (this.name == null) {
            this.name = extractString(1);
        }
        return this.name;
    }

    public Class getDeclaringType() {
        if (this.declaringType == null) {
            this.declaringType = extractType(2);
        }
        return this.declaringType;
    }

    public String getDeclaringTypeName() {
        if (this.declaringTypeName == null) {
            this.declaringTypeName = getDeclaringType().getName();
        }
        return this.declaringTypeName;
    }

    private ClassLoader getLookupClassLoader() {
        if (this.lookupClassLoader == null) {
            this.lookupClassLoader = getClass().getClassLoader();
        }
        return this.lookupClassLoader;
    }

    String extractString(int i) {
        int indexOf = this.stringRep.indexOf(45);
        int i2 = 0;
        while (true) {
            int i3 = i - 1;
            if (i <= 0) {
                break;
            }
            i2 = indexOf + 1;
            indexOf = this.stringRep.indexOf(45, i2);
            i = i3;
        }
        if (indexOf == -1) {
            indexOf = this.stringRep.length();
        }
        return this.stringRep.substring(i2, indexOf);
    }

    int extractInt(int i) {
        return Integer.parseInt(extractString(i), 16);
    }

    Class extractType(int i) {
        return Factory.makeClass(extractString(i), getLookupClassLoader());
    }

    Class[] extractTypes(int i) {
        StringTokenizer stringTokenizer = new StringTokenizer(extractString(i), ":");
        i = stringTokenizer.countTokens();
        Class[] clsArr = new Class[i];
        for (int i2 = 0; i2 < i; i2++) {
            clsArr[i2] = Factory.makeClass(stringTokenizer.nextToken(), getLookupClassLoader());
        }
        return clsArr;
    }
}
