package org.aspectj.runtime.reflect;

import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import org.aspectj.lang.Signature;

abstract class SignatureImpl implements Signature {
    static Class[] EMPTY_CLASS_ARRAY = new Class[0];
    static String[] EMPTY_STRING_ARRAY = new String[0];
    static final String INNER_SEP = ":";
    static final char SEP = '-';
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

    /* access modifiers changed from: protected */
    public abstract String createToString(StringMaker stringMaker);

    SignatureImpl(int modifiers2, String name2, Class declaringType2) {
        this.modifiers = modifiers2;
        this.name = name2;
        this.declaringType = declaringType2;
    }

    /* access modifiers changed from: package-private */
    public String toString(StringMaker sm) {
        String result = null;
        if (useCache) {
            Cache cache = this.stringCache;
            if (cache == null) {
                try {
                    this.stringCache = new CacheImpl();
                } catch (Throwable th) {
                    useCache = false;
                }
            } else {
                result = cache.get(sm.cacheOffset);
            }
        }
        if (result == null) {
            result = createToString(sm);
        }
        if (useCache) {
            this.stringCache.set(sm.cacheOffset, result);
        }
        return result;
    }

    public final String toString() {
        return toString(StringMaker.middleStringMaker);
    }

    public final String toShortString() {
        return toString(StringMaker.shortStringMaker);
    }

    public final String toLongString() {
        return toString(StringMaker.longStringMaker);
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

    /* access modifiers changed from: package-private */
    public String fullTypeName(Class type) {
        if (type == null) {
            return "ANONYMOUS";
        }
        if (!type.isArray()) {
            return type.getName().replace('$', '.');
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(fullTypeName(type.getComponentType()));
        stringBuffer.append("[]");
        return stringBuffer.toString();
    }

    /* access modifiers changed from: package-private */
    public String stripPackageName(String name2) {
        int dot = name2.lastIndexOf(46);
        if (dot == -1) {
            return name2;
        }
        return name2.substring(dot + 1);
    }

    /* access modifiers changed from: package-private */
    public String shortTypeName(Class type) {
        if (type == null) {
            return "ANONYMOUS";
        }
        if (!type.isArray()) {
            return stripPackageName(type.getName()).replace('$', '.');
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(shortTypeName(type.getComponentType()));
        stringBuffer.append("[]");
        return stringBuffer.toString();
    }

    /* access modifiers changed from: package-private */
    public void addFullTypeNames(StringBuffer buf, Class[] types) {
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(fullTypeName(types[i]));
        }
    }

    /* access modifiers changed from: package-private */
    public void addShortTypeNames(StringBuffer buf, Class[] types) {
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(shortTypeName(types[i]));
        }
    }

    /* access modifiers changed from: package-private */
    public void addTypeArray(StringBuffer buf, Class[] types) {
        addFullTypeNames(buf, types);
    }

    public void setLookupClassLoader(ClassLoader loader) {
        this.lookupClassLoader = loader;
    }

    private ClassLoader getLookupClassLoader() {
        if (this.lookupClassLoader == null) {
            this.lookupClassLoader = getClass().getClassLoader();
        }
        return this.lookupClassLoader;
    }

    public SignatureImpl(String stringRep2) {
        this.stringRep = stringRep2;
    }

    /* access modifiers changed from: package-private */
    public String extractString(int n) {
        int startIndex = 0;
        int endIndex = this.stringRep.indexOf(45);
        while (true) {
            int n2 = n - 1;
            if (n <= 0) {
                break;
            }
            startIndex = endIndex + 1;
            endIndex = this.stringRep.indexOf(45, startIndex);
            n = n2;
        }
        if (endIndex == -1) {
            endIndex = this.stringRep.length();
        }
        return this.stringRep.substring(startIndex, endIndex);
    }

    /* access modifiers changed from: package-private */
    public int extractInt(int n) {
        return Integer.parseInt(extractString(n), 16);
    }

    /* access modifiers changed from: package-private */
    public Class extractType(int n) {
        return Factory.makeClass(extractString(n), getLookupClassLoader());
    }

    /* access modifiers changed from: package-private */
    public String[] extractStrings(int n) {
        StringTokenizer st = new StringTokenizer(extractString(n), ":");
        int N = st.countTokens();
        String[] ret = new String[N];
        for (int i = 0; i < N; i++) {
            ret[i] = st.nextToken();
        }
        return ret;
    }

    /* access modifiers changed from: package-private */
    public Class[] extractTypes(int n) {
        StringTokenizer st = new StringTokenizer(extractString(n), ":");
        int N = st.countTokens();
        Class[] ret = new Class[N];
        for (int i = 0; i < N; i++) {
            ret[i] = Factory.makeClass(st.nextToken(), getLookupClassLoader());
        }
        return ret;
    }

    static void setUseCache(boolean b) {
        useCache = b;
    }

    static boolean getUseCache() {
        return useCache;
    }

    private static final class CacheImpl implements Cache {
        private SoftReference toStringCacheRef;

        public CacheImpl() {
            makeCache();
        }

        public String get(int cacheOffset) {
            String[] cachedArray = array();
            if (cachedArray == null) {
                return null;
            }
            return cachedArray[cacheOffset];
        }

        public void set(int cacheOffset, String result) {
            String[] cachedArray = array();
            if (cachedArray == null) {
                cachedArray = makeCache();
            }
            cachedArray[cacheOffset] = result;
        }

        private String[] array() {
            return (String[]) this.toStringCacheRef.get();
        }

        private String[] makeCache() {
            String[] array = new String[3];
            this.toStringCacheRef = new SoftReference(array);
            return array;
        }
    }
}
