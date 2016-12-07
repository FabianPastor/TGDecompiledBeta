package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.EnclosingStaticPart;
import org.aspectj.lang.JoinPoint.StaticPart;
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

public final class Factory {
    private static Object[] NO_ARGS = new Object[0];
    static Class class$java$lang$ClassNotFoundException;
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
            return Class.forName(s, false, loader);
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

    static Class class$(String x0) {
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

    public StaticPart makeSJP(String kind, String modifiers, String methodName, String declaringType, String paramTypes, String paramNames, String exceptionTypes, String returnType, int l) {
        Signature sig = makeMethodSig(modifiers, methodName, declaringType, paramTypes, paramNames, exceptionTypes, returnType);
        int i = this.count;
        this.count = i + 1;
        return new StaticPartImpl(i, kind, sig, makeSourceLoc(l, -1));
    }

    public StaticPart makeSJP(String kind, String modifiers, String methodName, String declaringType, String paramTypes, String paramNames, String returnType, int l) {
        Signature sig = makeMethodSig(modifiers, methodName, declaringType, paramTypes, paramNames, "", returnType);
        int i = this.count;
        this.count = i + 1;
        return new StaticPartImpl(i, kind, sig, makeSourceLoc(l, -1));
    }

    public StaticPart makeSJP(String kind, Signature sig, SourceLocation loc) {
        int i = this.count;
        this.count = i + 1;
        return new StaticPartImpl(i, kind, sig, loc);
    }

    public StaticPart makeSJP(String kind, Signature sig, int l, int c) {
        int i = this.count;
        this.count = i + 1;
        return new StaticPartImpl(i, kind, sig, makeSourceLoc(l, c));
    }

    public StaticPart makeSJP(String kind, Signature sig, int l) {
        int i = this.count;
        this.count = i + 1;
        return new StaticPartImpl(i, kind, sig, makeSourceLoc(l, -1));
    }

    public EnclosingStaticPart makeESJP(String kind, Signature sig, SourceLocation loc) {
        int i = this.count;
        this.count = i + 1;
        return new EnclosingStaticPartImpl(i, kind, sig, loc);
    }

    public EnclosingStaticPart makeESJP(String kind, Signature sig, int l, int c) {
        int i = this.count;
        this.count = i + 1;
        return new EnclosingStaticPartImpl(i, kind, sig, makeSourceLoc(l, c));
    }

    public EnclosingStaticPart makeESJP(String kind, Signature sig, int l) {
        int i = this.count;
        this.count = i + 1;
        return new EnclosingStaticPartImpl(i, kind, sig, makeSourceLoc(l, -1));
    }

    public static StaticPart makeEncSJP(Member member) {
        Signature sig;
        String kind;
        if (member instanceof Method) {
            Method method = (Method) member;
            sig = new MethodSignatureImpl(method.getModifiers(), method.getName(), method.getDeclaringClass(), method.getParameterTypes(), new String[method.getParameterTypes().length], method.getExceptionTypes(), method.getReturnType());
            kind = JoinPoint.METHOD_EXECUTION;
        } else if (member instanceof Constructor) {
            Constructor cons = (Constructor) member;
            sig = new ConstructorSignatureImpl(cons.getModifiers(), cons.getDeclaringClass(), cons.getParameterTypes(), new String[cons.getParameterTypes().length], cons.getExceptionTypes());
            kind = JoinPoint.CONSTRUCTOR_EXECUTION;
        } else {
            throw new IllegalArgumentException("member must be either a method or constructor");
        }
        return new EnclosingStaticPartImpl(-1, kind, sig, null);
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object _this, Object target) {
        return new JoinPointImpl(staticPart, _this, target, NO_ARGS);
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object _this, Object target, Object arg0) {
        return new JoinPointImpl(staticPart, _this, target, new Object[]{arg0});
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object _this, Object target, Object arg0, Object arg1) {
        return new JoinPointImpl(staticPart, _this, target, new Object[]{arg0, arg1});
    }

    public static JoinPoint makeJP(StaticPart staticPart, Object _this, Object target, Object[] args) {
        return new JoinPointImpl(staticPart, _this, target, args);
    }

    public MethodSignature makeMethodSig(String stringRep) {
        MethodSignatureImpl ret = new MethodSignatureImpl(stringRep);
        ret.setLookupClassLoader(this.lookupClassLoader);
        return ret;
    }

    public MethodSignature makeMethodSig(String modifiers, String methodName, String declaringType, String paramTypes, String paramNames, String exceptionTypes, String returnType) {
        int i;
        int modifiersAsInt = Integer.parseInt(modifiers, 16);
        Class declaringTypeClass = makeClass(declaringType, this.lookupClassLoader);
        StringTokenizer st = new StringTokenizer(paramTypes, ":");
        int numParams = st.countTokens();
        Class[] paramTypeClasses = new Class[numParams];
        for (i = 0; i < numParams; i++) {
            paramTypeClasses[i] = makeClass(st.nextToken(), this.lookupClassLoader);
        }
        st = new StringTokenizer(paramNames, ":");
        numParams = st.countTokens();
        String[] paramNamesArray = new String[numParams];
        for (i = 0; i < numParams; i++) {
            paramNamesArray[i] = st.nextToken();
        }
        st = new StringTokenizer(exceptionTypes, ":");
        numParams = st.countTokens();
        Class[] exceptionTypeClasses = new Class[numParams];
        for (i = 0; i < numParams; i++) {
            exceptionTypeClasses[i] = makeClass(st.nextToken(), this.lookupClassLoader);
        }
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
        int i;
        int modifiersAsInt = Integer.parseInt(modifiers, 16);
        Class declaringTypeClass = makeClass(declaringType, this.lookupClassLoader);
        StringTokenizer st = new StringTokenizer(paramTypes, ":");
        int numParams = st.countTokens();
        Class[] paramTypeClasses = new Class[numParams];
        for (i = 0; i < numParams; i++) {
            paramTypeClasses[i] = makeClass(st.nextToken(), this.lookupClassLoader);
        }
        st = new StringTokenizer(paramNames, ":");
        numParams = st.countTokens();
        String[] paramNamesArray = new String[numParams];
        for (i = 0; i < numParams; i++) {
            paramNamesArray[i] = st.nextToken();
        }
        st = new StringTokenizer(exceptionTypes, ":");
        numParams = st.countTokens();
        Class[] exceptionTypeClasses = new Class[numParams];
        for (i = 0; i < numParams; i++) {
            exceptionTypeClasses[i] = makeClass(st.nextToken(), this.lookupClassLoader);
        }
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
        int i;
        int modifiersAsInt = Integer.parseInt(modifiers, 16);
        Class declaringTypeClass = makeClass(declaringType, this.lookupClassLoader);
        StringTokenizer st = new StringTokenizer(paramTypes, ":");
        int numParams = st.countTokens();
        Class[] paramTypeClasses = new Class[numParams];
        for (i = 0; i < numParams; i++) {
            paramTypeClasses[i] = makeClass(st.nextToken(), this.lookupClassLoader);
        }
        st = new StringTokenizer(paramNames, ":");
        numParams = st.countTokens();
        String[] paramNamesArray = new String[numParams];
        for (i = 0; i < numParams; i++) {
            paramNamesArray[i] = st.nextToken();
        }
        st = new StringTokenizer(exceptionTypes, ":");
        numParams = st.countTokens();
        Class[] exceptionTypeClasses = new Class[numParams];
        for (i = 0; i < numParams; i++) {
            exceptionTypeClasses[i] = makeClass(st.nextToken(), this.lookupClassLoader);
        }
        AdviceSignatureImpl ret = new AdviceSignatureImpl(modifiersAsInt, name, declaringTypeClass, paramTypeClasses, paramNamesArray, exceptionTypeClasses, makeClass(returnType, this.lookupClassLoader));
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
