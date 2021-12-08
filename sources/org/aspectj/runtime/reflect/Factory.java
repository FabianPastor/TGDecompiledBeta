package org.aspectj.runtime.reflect;

import java.util.Hashtable;
import java.util.StringTokenizer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.CatchClauseSignature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.InitializerSignature;
import org.aspectj.lang.reflect.LockSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.lang.reflect.UnlockSignature;
import org.aspectj.runtime.reflect.JoinPointImpl;

public final class Factory {
    private static Object[] NO_ARGS = new Object[0];
    static /* synthetic */ Class class$java$lang$ClassNotFoundException;
    static Hashtable prims;
    int count = 0;
    String filename;
    Class lexicalClass;
    ClassLoader lookupClassLoader;

    static {
        Hashtable hashtable = new Hashtable();
        prims = hashtable;
        hashtable.put("void", Void.TYPE);
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
        if (s.equals("*")) {
            return null;
        }
        Class ret = (Class) prims.get(s);
        if (ret != null) {
            return ret;
        }
        if (loader != null) {
            return Class.forName(s, false, loader);
        }
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException e) {
            Class cls = class$java$lang$ClassNotFoundException;
            if (cls != null) {
                return cls;
            }
            Class class$ = class$("java.lang.ClassNotFoundException");
            class$java$lang$ClassNotFoundException = class$;
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

    public Factory(String filename2, Class lexicalClass2) {
        this.filename = filename2;
        this.lexicalClass = lexicalClass2;
        this.lookupClassLoader = lexicalClass2.getClassLoader();
    }

    public JoinPoint.StaticPart makeSJP(String kind, String modifiers, String methodName, String declaringType, String paramTypes, String paramNames, String exceptionTypes, String returnType, int l) {
        Signature sig = makeMethodSig(modifiers, methodName, declaringType, paramTypes, paramNames, exceptionTypes, returnType);
        int i = this.count;
        this.count = i + 1;
        String str = kind;
        return new JoinPointImpl.StaticPartImpl(i, kind, sig, makeSourceLoc(l, -1));
    }

    public JoinPoint.StaticPart makeSJP(String kind, String modifiers, String methodName, String declaringType, String paramTypes, String paramNames, String returnType, int l) {
        Signature sig = makeMethodSig(modifiers, methodName, declaringType, paramTypes, paramNames, "", returnType);
        int i = this.count;
        this.count = i + 1;
        String str = kind;
        return new JoinPointImpl.StaticPartImpl(i, kind, sig, makeSourceLoc(l, -1));
    }

    public JoinPoint.StaticPart makeSJP(String kind, Signature sig, SourceLocation loc) {
        int i = this.count;
        this.count = i + 1;
        return new JoinPointImpl.StaticPartImpl(i, kind, sig, loc);
    }

    public JoinPoint.StaticPart makeSJP(String kind, Signature sig, int l, int c) {
        int i = this.count;
        this.count = i + 1;
        return new JoinPointImpl.StaticPartImpl(i, kind, sig, makeSourceLoc(l, c));
    }

    public JoinPoint.StaticPart makeSJP(String kind, Signature sig, int l) {
        int i = this.count;
        this.count = i + 1;
        return new JoinPointImpl.StaticPartImpl(i, kind, sig, makeSourceLoc(l, -1));
    }

    public JoinPoint.EnclosingStaticPart makeESJP(String kind, Signature sig, SourceLocation loc) {
        int i = this.count;
        this.count = i + 1;
        return new JoinPointImpl.EnclosingStaticPartImpl(i, kind, sig, loc);
    }

    public JoinPoint.EnclosingStaticPart makeESJP(String kind, Signature sig, int l, int c) {
        int i = this.count;
        this.count = i + 1;
        return new JoinPointImpl.EnclosingStaticPartImpl(i, kind, sig, makeSourceLoc(l, c));
    }

    public JoinPoint.EnclosingStaticPart makeESJP(String kind, Signature sig, int l) {
        int i = this.count;
        this.count = i + 1;
        return new JoinPointImpl.EnclosingStaticPartImpl(i, kind, sig, makeSourceLoc(l, -1));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.aspectj.runtime.reflect.ConstructorSignatureImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.aspectj.runtime.reflect.ConstructorSignatureImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.aspectj.runtime.reflect.MethodSignatureImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.aspectj.runtime.reflect.ConstructorSignatureImpl} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.aspectj.lang.JoinPoint.StaticPart makeEncSJP(java.lang.reflect.Member r12) {
        /*
            r0 = 0
            r1 = 0
            boolean r2 = r12 instanceof java.lang.reflect.Method
            if (r2 == 0) goto L_0x0033
            r2 = r12
            java.lang.reflect.Method r2 = (java.lang.reflect.Method) r2
            org.aspectj.runtime.reflect.MethodSignatureImpl r11 = new org.aspectj.runtime.reflect.MethodSignatureImpl
            int r4 = r2.getModifiers()
            java.lang.String r5 = r2.getName()
            java.lang.Class r6 = r2.getDeclaringClass()
            java.lang.Class[] r7 = r2.getParameterTypes()
            java.lang.Class[] r3 = r2.getParameterTypes()
            int r3 = r3.length
            java.lang.String[] r8 = new java.lang.String[r3]
            java.lang.Class[] r9 = r2.getExceptionTypes()
            java.lang.Class r10 = r2.getReturnType()
            r3 = r11
            r3.<init>(r4, r5, r6, r7, r8, r9, r10)
            r0 = r11
            java.lang.String r1 = "method-execution"
            goto L_0x005b
        L_0x0033:
            boolean r2 = r12 instanceof java.lang.reflect.Constructor
            if (r2 == 0) goto L_0x0063
            r2 = r12
            java.lang.reflect.Constructor r2 = (java.lang.reflect.Constructor) r2
            org.aspectj.runtime.reflect.ConstructorSignatureImpl r9 = new org.aspectj.runtime.reflect.ConstructorSignatureImpl
            int r4 = r2.getModifiers()
            java.lang.Class r5 = r2.getDeclaringClass()
            java.lang.Class[] r6 = r2.getParameterTypes()
            java.lang.Class[] r3 = r2.getParameterTypes()
            int r3 = r3.length
            java.lang.String[] r7 = new java.lang.String[r3]
            java.lang.Class[] r8 = r2.getExceptionTypes()
            r3 = r9
            r3.<init>(r4, r5, r6, r7, r8)
            r0 = r9
            java.lang.String r1 = "constructor-execution"
        L_0x005b:
            org.aspectj.runtime.reflect.JoinPointImpl$EnclosingStaticPartImpl r2 = new org.aspectj.runtime.reflect.JoinPointImpl$EnclosingStaticPartImpl
            r3 = -1
            r4 = 0
            r2.<init>(r3, r1, r0, r4)
            return r2
        L_0x0063:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "member must be either a method or constructor"
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.aspectj.runtime.reflect.Factory.makeEncSJP(java.lang.reflect.Member):org.aspectj.lang.JoinPoint$StaticPart");
    }

    public static JoinPoint makeJP(JoinPoint.StaticPart staticPart, Object _this, Object target) {
        return new JoinPointImpl(staticPart, _this, target, NO_ARGS);
    }

    public static JoinPoint makeJP(JoinPoint.StaticPart staticPart, Object _this, Object target, Object arg0) {
        return new JoinPointImpl(staticPart, _this, target, new Object[]{arg0});
    }

    public static JoinPoint makeJP(JoinPoint.StaticPart staticPart, Object _this, Object target, Object arg0, Object arg1) {
        return new JoinPointImpl(staticPart, _this, target, new Object[]{arg0, arg1});
    }

    public static JoinPoint makeJP(JoinPoint.StaticPart staticPart, Object _this, Object target, Object[] args) {
        return new JoinPointImpl(staticPart, _this, target, args);
    }

    public MethodSignature makeMethodSig(String stringRep) {
        MethodSignatureImpl ret = new MethodSignatureImpl(stringRep);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public MethodSignature makeMethodSig(String modifiers, String methodName, String declaringType, String paramTypes, String paramNames, String exceptionTypes, String returnType) {
        int modifiersAsInt = Integer.parseInt(modifiers, 16);
        Class declaringTypeClass = makeClass(declaringType, this.lookupClassLoader);
        StringTokenizer st = new StringTokenizer(paramTypes, ":");
        int numParams = st.countTokens();
        Class[] paramTypeClasses = new Class[numParams];
        for (int i = 0; i < numParams; i++) {
            paramTypeClasses[i] = makeClass(st.nextToken(), this.lookupClassLoader);
        }
        StringTokenizer st2 = new StringTokenizer(paramNames, ":");
        int numParams2 = st2.countTokens();
        String[] paramNamesArray = new String[numParams2];
        for (int i2 = 0; i2 < numParams2; i2++) {
            paramNamesArray[i2] = st2.nextToken();
        }
        StringTokenizer st3 = new StringTokenizer(exceptionTypes, ":");
        int numParams3 = st3.countTokens();
        Class[] exceptionTypeClasses = new Class[numParams3];
        for (int i3 = 0; i3 < numParams3; i3++) {
            exceptionTypeClasses[i3] = makeClass(st3.nextToken(), this.lookupClassLoader);
        }
        int i4 = numParams3;
        String[] strArr = paramNamesArray;
        return new MethodSignatureImpl(modifiersAsInt, methodName, declaringTypeClass, paramTypeClasses, paramNamesArray, exceptionTypeClasses, makeClass(returnType, this.lookupClassLoader));
    }

    public MethodSignature makeMethodSig(int modifiers, String name, Class declaringType, Class[] parameterTypes, String[] parameterNames, Class[] exceptionTypes, Class returnType) {
        MethodSignatureImpl ret = new MethodSignatureImpl(modifiers, name, declaringType, parameterTypes, parameterNames, exceptionTypes, returnType);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public ConstructorSignature makeConstructorSig(String stringRep) {
        ConstructorSignatureImpl ret = new ConstructorSignatureImpl(stringRep);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public ConstructorSignature makeConstructorSig(String modifiers, String declaringType, String paramTypes, String paramNames, String exceptionTypes) {
        int modifiersAsInt = Integer.parseInt(modifiers, 16);
        Class declaringTypeClass = makeClass(declaringType, this.lookupClassLoader);
        StringTokenizer st = new StringTokenizer(paramTypes, ":");
        int numParams = st.countTokens();
        Class[] paramTypeClasses = new Class[numParams];
        for (int i = 0; i < numParams; i++) {
            paramTypeClasses[i] = makeClass(st.nextToken(), this.lookupClassLoader);
        }
        StringTokenizer st2 = new StringTokenizer(paramNames, ":");
        int numParams2 = st2.countTokens();
        String[] paramNamesArray = new String[numParams2];
        for (int i2 = 0; i2 < numParams2; i2++) {
            paramNamesArray[i2] = st2.nextToken();
        }
        StringTokenizer st3 = new StringTokenizer(exceptionTypes, ":");
        int numParams3 = st3.countTokens();
        Class[] exceptionTypeClasses = new Class[numParams3];
        for (int i3 = 0; i3 < numParams3; i3++) {
            exceptionTypeClasses[i3] = makeClass(st3.nextToken(), this.lookupClassLoader);
        }
        int i4 = numParams3;
        ConstructorSignatureImpl ret = new ConstructorSignatureImpl(modifiersAsInt, declaringTypeClass, paramTypeClasses, paramNamesArray, exceptionTypeClasses);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public ConstructorSignature makeConstructorSig(int modifiers, Class declaringType, Class[] parameterTypes, String[] parameterNames, Class[] exceptionTypes) {
        ConstructorSignatureImpl ret = new ConstructorSignatureImpl(modifiers, declaringType, parameterTypes, parameterNames, exceptionTypes);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public FieldSignature makeFieldSig(String stringRep) {
        FieldSignatureImpl ret = new FieldSignatureImpl(stringRep);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public FieldSignature makeFieldSig(String modifiers, String name, String declaringType, String fieldType) {
        FieldSignatureImpl ret = new FieldSignatureImpl(Integer.parseInt(modifiers, 16), name, makeClass(declaringType, this.lookupClassLoader), makeClass(fieldType, this.lookupClassLoader));
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public FieldSignature makeFieldSig(int modifiers, String name, Class declaringType, Class fieldType) {
        FieldSignatureImpl ret = new FieldSignatureImpl(modifiers, name, declaringType, fieldType);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public AdviceSignature makeAdviceSig(String stringRep) {
        AdviceSignatureImpl ret = new AdviceSignatureImpl(stringRep);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public AdviceSignature makeAdviceSig(String modifiers, String name, String declaringType, String paramTypes, String paramNames, String exceptionTypes, String returnType) {
        int modifiersAsInt = Integer.parseInt(modifiers, 16);
        Class declaringTypeClass = makeClass(declaringType, this.lookupClassLoader);
        StringTokenizer st = new StringTokenizer(paramTypes, ":");
        int numParams = st.countTokens();
        Class[] paramTypeClasses = new Class[numParams];
        for (int i = 0; i < numParams; i++) {
            paramTypeClasses[i] = makeClass(st.nextToken(), this.lookupClassLoader);
        }
        StringTokenizer st2 = new StringTokenizer(paramNames, ":");
        int numParams2 = st2.countTokens();
        String[] paramNamesArray = new String[numParams2];
        for (int i2 = 0; i2 < numParams2; i2++) {
            paramNamesArray[i2] = st2.nextToken();
        }
        StringTokenizer st3 = new StringTokenizer(exceptionTypes, ":");
        int numParams3 = st3.countTokens();
        Class[] exceptionTypeClasses = new Class[numParams3];
        for (int i3 = 0; i3 < numParams3; i3++) {
            exceptionTypeClasses[i3] = makeClass(st3.nextToken(), this.lookupClassLoader);
        }
        Class[] exceptionTypeClasses2 = exceptionTypeClasses;
        int i4 = numParams3;
        String[] strArr = paramNamesArray;
        AdviceSignatureImpl ret = new AdviceSignatureImpl(modifiersAsInt, name, declaringTypeClass, paramTypeClasses, paramNamesArray, exceptionTypeClasses2, makeClass(returnType, this.lookupClassLoader));
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public AdviceSignature makeAdviceSig(int modifiers, String name, Class declaringType, Class[] parameterTypes, String[] parameterNames, Class[] exceptionTypes, Class returnType) {
        AdviceSignatureImpl ret = new AdviceSignatureImpl(modifiers, name, declaringType, parameterTypes, parameterNames, exceptionTypes, returnType);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public InitializerSignature makeInitializerSig(String stringRep) {
        InitializerSignatureImpl ret = new InitializerSignatureImpl(stringRep);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public InitializerSignature makeInitializerSig(String modifiers, String declaringType) {
        InitializerSignatureImpl ret = new InitializerSignatureImpl(Integer.parseInt(modifiers, 16), makeClass(declaringType, this.lookupClassLoader));
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public InitializerSignature makeInitializerSig(int modifiers, Class declaringType) {
        InitializerSignatureImpl ret = new InitializerSignatureImpl(modifiers, declaringType);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public CatchClauseSignature makeCatchClauseSig(String stringRep) {
        CatchClauseSignatureImpl ret = new CatchClauseSignatureImpl(stringRep);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public CatchClauseSignature makeCatchClauseSig(String declaringType, String parameterType, String parameterName) {
        CatchClauseSignatureImpl ret = new CatchClauseSignatureImpl(makeClass(declaringType, this.lookupClassLoader), makeClass(new StringTokenizer(parameterType, ":").nextToken(), this.lookupClassLoader), new StringTokenizer(parameterName, ":").nextToken());
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public CatchClauseSignature makeCatchClauseSig(Class declaringType, Class parameterType, String parameterName) {
        CatchClauseSignatureImpl ret = new CatchClauseSignatureImpl(declaringType, parameterType, parameterName);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public LockSignature makeLockSig(String stringRep) {
        LockSignatureImpl ret = new LockSignatureImpl(stringRep);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public LockSignature makeLockSig() {
        LockSignatureImpl ret = new LockSignatureImpl(makeClass("Ljava/lang/Object;", this.lookupClassLoader));
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public LockSignature makeLockSig(Class declaringType) {
        LockSignatureImpl ret = new LockSignatureImpl(declaringType);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public UnlockSignature makeUnlockSig(String stringRep) {
        UnlockSignatureImpl ret = new UnlockSignatureImpl(stringRep);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public UnlockSignature makeUnlockSig() {
        UnlockSignatureImpl ret = new UnlockSignatureImpl(makeClass("Ljava/lang/Object;", this.lookupClassLoader));
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public UnlockSignature makeUnlockSig(Class declaringType) {
        UnlockSignatureImpl ret = new UnlockSignatureImpl(declaringType);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public SourceLocation makeSourceLoc(int line, int col) {
        return new SourceLocationImpl(this.lexicalClass, this.filename, line);
    }
}
