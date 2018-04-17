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
    int count = 0;
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

    static Class makeClass(String s, ClassLoader loader) {
        Class class$;
        if (s.equals("*")) {
            return null;
        }
        Class ret = (Class) prims.get(s);
        if (ret != null) {
            return ret;
        }
        if (loader != null) {
            return Class.forName(s, null, loader);
        }
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException e) {
            if (class$java$lang$ClassNotFoundException == null) {
                class$ = class$("java.lang.ClassNotFoundException");
                class$java$lang$ClassNotFoundException = class$;
            } else {
                class$ = class$java$lang$ClassNotFoundException;
            }
            return class$;
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public Factory(String filename, Class lexicalClass) {
        this.filename = filename;
        this.lexicalClass = lexicalClass;
        this.lookupClassLoader = lexicalClass.getClassLoader();
    }

    public StaticPart makeSJP(String kind, Signature sig, int l) {
        int i = this.count;
        this.count = i + 1;
        return new StaticPartImpl(i, kind, sig, makeSourceLoc(l, -1));
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object _this, Object target) {
        return new JoinPointImpl(staticPart, _this, target, NO_ARGS);
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object _this, Object target, Object arg0) {
        return new JoinPointImpl(staticPart, _this, target, new Object[]{arg0});
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object _this, Object target, Object[] args) {
        return new JoinPointImpl(staticPart, _this, target, args);
    }

    public MethodSignature makeMethodSig(String modifiers, String methodName, String declaringType, String paramTypes, String paramNames, String exceptionTypes, String returnType) {
        int i;
        int modifiersAsInt = Integer.parseInt(modifiers, 16);
        Class declaringTypeClass = makeClass(declaringType, this.lookupClassLoader);
        StringTokenizer st = new StringTokenizer(paramTypes, ":");
        int numParams = st.countTokens();
        Class[] paramTypeClasses = new Class[numParams];
        int i2 = 0;
        for (i = 0; i < numParams; i++) {
            paramTypeClasses[i] = makeClass(st.nextToken(), r0.lookupClassLoader);
        }
        st = new StringTokenizer(paramNames, ":");
        numParams = st.countTokens();
        String[] paramNamesArray = new String[numParams];
        for (i = 0; i < numParams; i++) {
            paramNamesArray[i] = st.nextToken();
        }
        StringTokenizer st2 = new StringTokenizer(exceptionTypes, ":");
        int numParams2 = st2.countTokens();
        Class[] exceptionTypeClasses = new Class[numParams2];
        while (true) {
            int i3 = i2;
            if (i3 < numParams2) {
                exceptionTypeClasses[i3] = makeClass(st2.nextToken(), r0.lookupClassLoader);
                i2 = i3 + 1;
            } else {
                return new MethodSignatureImpl(modifiersAsInt, methodName, declaringTypeClass, paramTypeClasses, paramNamesArray, exceptionTypeClasses, makeClass(returnType, r0.lookupClassLoader));
            }
        }
    }

    public SourceLocation makeSourceLoc(int line, int col) {
        return new SourceLocationImpl(this.lexicalClass, this.filename, line);
    }
}
