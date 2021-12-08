package org.aspectj.internal.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.internal.lang.annotation.ajcDeclareEoW;
import org.aspectj.internal.lang.annotation.ajcDeclareParents;
import org.aspectj.internal.lang.annotation.ajcDeclarePrecedence;
import org.aspectj.internal.lang.annotation.ajcDeclareSoft;
import org.aspectj.internal.lang.annotation.ajcITD;
import org.aspectj.internal.lang.annotation.ajcPrivileged;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.DeclareWarning;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;
import org.aspectj.lang.reflect.InterTypeFieldDeclaration;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;
import org.aspectj.lang.reflect.NoSuchAdviceException;
import org.aspectj.lang.reflect.NoSuchPointcutException;
import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.Pointcut;

public class AjTypeImpl<T> implements AjType<T> {
    private static final String ajcMagic = "ajc$";
    private Advice[] advice = null;
    private Class<T> clazz;
    private Advice[] declaredAdvice = null;
    private InterTypeConstructorDeclaration[] declaredITDCons = null;
    private InterTypeFieldDeclaration[] declaredITDFields = null;
    private InterTypeMethodDeclaration[] declaredITDMethods = null;
    private Pointcut[] declaredPointcuts = null;
    private InterTypeConstructorDeclaration[] itdCons = null;
    private InterTypeFieldDeclaration[] itdFields = null;
    private InterTypeMethodDeclaration[] itdMethods = null;
    private Pointcut[] pointcuts = null;

    public AjTypeImpl(Class<T> fromClass) {
        this.clazz = fromClass;
    }

    public String getName() {
        return this.clazz.getName();
    }

    public Package getPackage() {
        return this.clazz.getPackage();
    }

    public AjType<?>[] getInterfaces() {
        return toAjTypeArray(this.clazz.getInterfaces());
    }

    public int getModifiers() {
        return this.clazz.getModifiers();
    }

    public Class<T> getJavaClass() {
        return this.clazz;
    }

    public AjType<? super T> getSupertype() {
        Class<? super T> superclass = this.clazz.getSuperclass();
        if (superclass == null) {
            return null;
        }
        return new AjTypeImpl(superclass);
    }

    public Type getGenericSupertype() {
        return this.clazz.getGenericSuperclass();
    }

    public Method getEnclosingMethod() {
        return this.clazz.getEnclosingMethod();
    }

    public Constructor getEnclosingConstructor() {
        return this.clazz.getEnclosingConstructor();
    }

    public AjType<?> getEnclosingType() {
        Class<?> enc = this.clazz.getEnclosingClass();
        if (enc != null) {
            return new AjTypeImpl(enc);
        }
        return null;
    }

    public AjType<?> getDeclaringType() {
        Class dec = this.clazz.getDeclaringClass();
        if (dec != null) {
            return new AjTypeImpl(dec);
        }
        return null;
    }

    public PerClause getPerClause() {
        if (!isAspect()) {
            return null;
        }
        String perClause = ((Aspect) this.clazz.getAnnotation(Aspect.class)).value();
        if (perClause.equals("")) {
            if (getSupertype().isAspect()) {
                return getSupertype().getPerClause();
            }
            return new PerClauseImpl(PerClauseKind.SINGLETON);
        } else if (perClause.startsWith("perthis(")) {
            return new PointcutBasedPerClauseImpl(PerClauseKind.PERTHIS, perClause.substring("perthis(".length(), perClause.length() - 1));
        } else {
            if (perClause.startsWith("pertarget(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERTARGET, perClause.substring("pertarget(".length(), perClause.length() - 1));
            }
            if (perClause.startsWith("percflow(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOW, perClause.substring("percflow(".length(), perClause.length() - 1));
            }
            if (perClause.startsWith("percflowbelow(")) {
                return new PointcutBasedPerClauseImpl(PerClauseKind.PERCFLOWBELOW, perClause.substring("percflowbelow(".length(), perClause.length() - 1));
            }
            if (perClause.startsWith("pertypewithin")) {
                return new TypePatternBasedPerClauseImpl(PerClauseKind.PERTYPEWITHIN, perClause.substring("pertypewithin(".length(), perClause.length() - 1));
            }
            throw new IllegalStateException("Per-clause not recognized: " + perClause);
        }
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return this.clazz.isAnnotationPresent(annotationType);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return this.clazz.getAnnotation(annotationType);
    }

    public Annotation[] getAnnotations() {
        return this.clazz.getAnnotations();
    }

    public Annotation[] getDeclaredAnnotations() {
        return this.clazz.getDeclaredAnnotations();
    }

    public AjType<?>[] getAjTypes() {
        return toAjTypeArray(this.clazz.getClasses());
    }

    public AjType<?>[] getDeclaredAjTypes() {
        return toAjTypeArray(this.clazz.getDeclaredClasses());
    }

    public Constructor getConstructor(AjType<?>... parameterTypes) throws NoSuchMethodException {
        return this.clazz.getConstructor(toClassArray(parameterTypes));
    }

    public Constructor[] getConstructors() {
        return this.clazz.getConstructors();
    }

    public Constructor getDeclaredConstructor(AjType<?>... parameterTypes) throws NoSuchMethodException {
        return this.clazz.getDeclaredConstructor(toClassArray(parameterTypes));
    }

    public Constructor[] getDeclaredConstructors() {
        return this.clazz.getDeclaredConstructors();
    }

    public Field getDeclaredField(String name) throws NoSuchFieldException {
        Field f = this.clazz.getDeclaredField(name);
        if (!f.getName().startsWith("ajc$")) {
            return f;
        }
        throw new NoSuchFieldException(name);
    }

    public Field[] getDeclaredFields() {
        Field[] fields = this.clazz.getDeclaredFields();
        List<Field> filteredFields = new ArrayList<>();
        for (Field field : fields) {
            if (!field.getName().startsWith("ajc$") && !field.isAnnotationPresent(DeclareWarning.class) && !field.isAnnotationPresent(DeclareError.class)) {
                filteredFields.add(field);
            }
        }
        Field[] ret = new Field[filteredFields.size()];
        filteredFields.toArray(ret);
        return ret;
    }

    public Field getField(String name) throws NoSuchFieldException {
        Field f = this.clazz.getField(name);
        if (!f.getName().startsWith("ajc$")) {
            return f;
        }
        throw new NoSuchFieldException(name);
    }

    public Field[] getFields() {
        Field[] fields = this.clazz.getFields();
        List<Field> filteredFields = new ArrayList<>();
        for (Field field : fields) {
            if (!field.getName().startsWith("ajc$") && !field.isAnnotationPresent(DeclareWarning.class) && !field.isAnnotationPresent(DeclareError.class)) {
                filteredFields.add(field);
            }
        }
        Field[] ret = new Field[filteredFields.size()];
        filteredFields.toArray(ret);
        return ret;
    }

    public Method getDeclaredMethod(String name, AjType<?>... parameterTypes) throws NoSuchMethodException {
        Method m = this.clazz.getDeclaredMethod(name, toClassArray(parameterTypes));
        if (isReallyAMethod(m)) {
            return m;
        }
        throw new NoSuchMethodException(name);
    }

    public Method getMethod(String name, AjType<?>... parameterTypes) throws NoSuchMethodException {
        Method m = this.clazz.getMethod(name, toClassArray(parameterTypes));
        if (isReallyAMethod(m)) {
            return m;
        }
        throw new NoSuchMethodException(name);
    }

    public Method[] getDeclaredMethods() {
        Method[] methods = this.clazz.getDeclaredMethods();
        List<Method> filteredMethods = new ArrayList<>();
        for (Method method : methods) {
            if (isReallyAMethod(method)) {
                filteredMethods.add(method);
            }
        }
        Method[] ret = new Method[filteredMethods.size()];
        filteredMethods.toArray(ret);
        return ret;
    }

    public Method[] getMethods() {
        Method[] methods = this.clazz.getMethods();
        List<Method> filteredMethods = new ArrayList<>();
        for (Method method : methods) {
            if (isReallyAMethod(method)) {
                filteredMethods.add(method);
            }
        }
        Method[] ret = new Method[filteredMethods.size()];
        filteredMethods.toArray(ret);
        return ret;
    }

    private boolean isReallyAMethod(Method method) {
        if (method.getName().startsWith("ajc$")) {
            return false;
        }
        if (method.getAnnotations().length == 0) {
            return true;
        }
        if (!method.isAnnotationPresent(org.aspectj.lang.annotation.Pointcut.class) && !method.isAnnotationPresent(Before.class) && !method.isAnnotationPresent(After.class) && !method.isAnnotationPresent(AfterReturning.class) && !method.isAnnotationPresent(AfterThrowing.class) && !method.isAnnotationPresent(Around.class)) {
            return true;
        }
        return false;
    }

    public Pointcut getDeclaredPointcut(String name) throws NoSuchPointcutException {
        for (Pointcut pc : getDeclaredPointcuts()) {
            if (pc.getName().equals(name)) {
                return pc;
            }
        }
        throw new NoSuchPointcutException(name);
    }

    public Pointcut getPointcut(String name) throws NoSuchPointcutException {
        for (Pointcut pc : getPointcuts()) {
            if (pc.getName().equals(name)) {
                return pc;
            }
        }
        throw new NoSuchPointcutException(name);
    }

    public Pointcut[] getDeclaredPointcuts() {
        Pointcut[] pointcutArr = this.declaredPointcuts;
        if (pointcutArr != null) {
            return pointcutArr;
        }
        List<Pointcut> pointcuts2 = new ArrayList<>();
        for (Method method : this.clazz.getDeclaredMethods()) {
            Pointcut pc = asPointcut(method);
            if (pc != null) {
                pointcuts2.add(pc);
            }
        }
        Pointcut[] ret = new Pointcut[pointcuts2.size()];
        pointcuts2.toArray(ret);
        this.declaredPointcuts = ret;
        return ret;
    }

    public Pointcut[] getPointcuts() {
        Pointcut[] pointcutArr = this.pointcuts;
        if (pointcutArr != null) {
            return pointcutArr;
        }
        List<Pointcut> pcuts = new ArrayList<>();
        for (Method method : this.clazz.getMethods()) {
            Pointcut pc = asPointcut(method);
            if (pc != null) {
                pcuts.add(pc);
            }
        }
        Pointcut[] ret = new Pointcut[pcuts.size()];
        pcuts.toArray(ret);
        this.pointcuts = ret;
        return ret;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0016, code lost:
        r1 = r1.substring(r1.indexOf("$$") + 2, r1.length());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.aspectj.lang.reflect.Pointcut asPointcut(java.lang.reflect.Method r11) {
        /*
            r10 = this;
            java.lang.Class<org.aspectj.lang.annotation.Pointcut> r0 = org.aspectj.lang.annotation.Pointcut.class
            java.lang.annotation.Annotation r0 = r11.getAnnotation(r0)
            org.aspectj.lang.annotation.Pointcut r0 = (org.aspectj.lang.annotation.Pointcut) r0
            if (r0 == 0) goto L_0x004d
            java.lang.String r1 = r11.getName()
            java.lang.String r2 = "ajc$"
            boolean r2 = r1.startsWith(r2)
            if (r2 == 0) goto L_0x0034
            java.lang.String r2 = "$$"
            int r2 = r1.indexOf(r2)
            int r3 = r2 + 2
            int r4 = r1.length()
            java.lang.String r1 = r1.substring(r3, r4)
            java.lang.String r3 = "$"
            int r3 = r1.indexOf(r3)
            r4 = -1
            if (r3 == r4) goto L_0x0034
            r4 = 0
            java.lang.String r1 = r1.substring(r4, r3)
        L_0x0034:
            org.aspectj.internal.lang.reflect.PointcutImpl r2 = new org.aspectj.internal.lang.reflect.PointcutImpl
            java.lang.String r6 = r0.value()
            java.lang.Class r3 = r11.getDeclaringClass()
            org.aspectj.lang.reflect.AjType r8 = org.aspectj.lang.reflect.AjTypeSystem.getAjType(r3)
            java.lang.String r9 = r0.argNames()
            r4 = r2
            r5 = r1
            r7 = r11
            r4.<init>(r5, r6, r7, r8, r9)
            return r2
        L_0x004d:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.aspectj.internal.lang.reflect.AjTypeImpl.asPointcut(java.lang.reflect.Method):org.aspectj.lang.reflect.Pointcut");
    }

    public Advice[] getDeclaredAdvice(AdviceKind... ofType) {
        Set<AdviceKind> types;
        if (ofType.length == 0) {
            types = EnumSet.allOf(AdviceKind.class);
        } else {
            types = EnumSet.noneOf(AdviceKind.class);
            types.addAll(Arrays.asList(ofType));
        }
        return getDeclaredAdvice((Set) types);
    }

    public Advice[] getAdvice(AdviceKind... ofType) {
        Set<AdviceKind> types;
        if (ofType.length == 0) {
            types = EnumSet.allOf(AdviceKind.class);
        } else {
            types = EnumSet.noneOf(AdviceKind.class);
            types.addAll(Arrays.asList(ofType));
        }
        return getAdvice((Set) types);
    }

    private Advice[] getDeclaredAdvice(Set ofAdviceTypes) {
        if (this.declaredAdvice == null) {
            initDeclaredAdvice();
        }
        List<Advice> adviceList = new ArrayList<>();
        for (Advice a : this.declaredAdvice) {
            if (ofAdviceTypes.contains(a.getKind())) {
                adviceList.add(a);
            }
        }
        Advice[] ret = new Advice[adviceList.size()];
        adviceList.toArray(ret);
        return ret;
    }

    private void initDeclaredAdvice() {
        Method[] methods = this.clazz.getDeclaredMethods();
        List<Advice> adviceList = new ArrayList<>();
        for (Method method : methods) {
            Advice advice2 = asAdvice(method);
            if (advice2 != null) {
                adviceList.add(advice2);
            }
        }
        Advice[] adviceArr = new Advice[adviceList.size()];
        this.declaredAdvice = adviceArr;
        adviceList.toArray(adviceArr);
    }

    private Advice[] getAdvice(Set ofAdviceTypes) {
        if (this.advice == null) {
            initAdvice();
        }
        List<Advice> adviceList = new ArrayList<>();
        for (Advice a : this.advice) {
            if (ofAdviceTypes.contains(a.getKind())) {
                adviceList.add(a);
            }
        }
        Advice[] ret = new Advice[adviceList.size()];
        adviceList.toArray(ret);
        return ret;
    }

    private void initAdvice() {
        Method[] methods = this.clazz.getMethods();
        List<Advice> adviceList = new ArrayList<>();
        for (Method method : methods) {
            Advice advice2 = asAdvice(method);
            if (advice2 != null) {
                adviceList.add(advice2);
            }
        }
        Advice[] adviceArr = new Advice[adviceList.size()];
        this.advice = adviceArr;
        adviceList.toArray(adviceArr);
    }

    public Advice getAdvice(String name) throws NoSuchAdviceException {
        if (!name.equals("")) {
            if (this.advice == null) {
                initAdvice();
            }
            for (Advice a : this.advice) {
                if (a.getName().equals(name)) {
                    return a;
                }
            }
            throw new NoSuchAdviceException(name);
        }
        throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
    }

    public Advice getDeclaredAdvice(String name) throws NoSuchAdviceException {
        if (!name.equals("")) {
            if (this.declaredAdvice == null) {
                initDeclaredAdvice();
            }
            for (Advice a : this.declaredAdvice) {
                if (a.getName().equals(name)) {
                    return a;
                }
            }
            throw new NoSuchAdviceException(name);
        }
        throw new IllegalArgumentException("use getAdvice(AdviceType...) instead for un-named advice");
    }

    private Advice asAdvice(Method method) {
        if (method.getAnnotations().length == 0) {
            return null;
        }
        Before beforeAnn = (Before) method.getAnnotation(Before.class);
        if (beforeAnn != null) {
            return new AdviceImpl(method, beforeAnn.value(), AdviceKind.BEFORE);
        }
        After afterAnn = (After) method.getAnnotation(After.class);
        if (afterAnn != null) {
            return new AdviceImpl(method, afterAnn.value(), AdviceKind.AFTER);
        }
        AfterReturning afterReturningAnn = (AfterReturning) method.getAnnotation(AfterReturning.class);
        if (afterReturningAnn != null) {
            String pcExpr = afterReturningAnn.pointcut();
            if (pcExpr.equals("")) {
                pcExpr = afterReturningAnn.value();
            }
            return new AdviceImpl(method, pcExpr, AdviceKind.AFTER_RETURNING, afterReturningAnn.returning());
        }
        AfterThrowing afterThrowingAnn = (AfterThrowing) method.getAnnotation(AfterThrowing.class);
        if (afterThrowingAnn != null) {
            String pcExpr2 = afterThrowingAnn.pointcut();
            if (pcExpr2 == null) {
                pcExpr2 = afterThrowingAnn.value();
            }
            return new AdviceImpl(method, pcExpr2, AdviceKind.AFTER_THROWING, afterThrowingAnn.throwing());
        }
        Around aroundAnn = (Around) method.getAnnotation(Around.class);
        if (aroundAnn != null) {
            return new AdviceImpl(method, aroundAnn.value(), AdviceKind.AROUND);
        }
        return null;
    }

    public InterTypeMethodDeclaration getDeclaredITDMethod(String name, AjType<?> target, AjType<?>... parameterTypes) throws NoSuchMethodException {
        for (InterTypeMethodDeclaration itdm : getDeclaredITDMethods()) {
            try {
                if (itdm.getName().equals(name)) {
                    if (itdm.getTargetType().equals(target)) {
                        AjType<?>[] ptypes = itdm.getParameterTypes();
                        if (ptypes.length == parameterTypes.length) {
                            int i = 0;
                            while (i < ptypes.length) {
                                if (ptypes[i].equals(parameterTypes[i])) {
                                    i++;
                                }
                            }
                            return itdm;
                        }
                        continue;
                    } else {
                        continue;
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException(name);
    }

    public InterTypeMethodDeclaration[] getDeclaredITDMethods() {
        if (this.declaredITDMethods == null) {
            List<InterTypeMethodDeclaration> itdms = new ArrayList<>();
            for (Method m : this.clazz.getDeclaredMethods()) {
                if (m.getName().contains("ajc$interMethodDispatch1$") && m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    itdms.add(new InterTypeMethodDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), m));
                }
            }
            addAnnotationStyleITDMethods(itdms, false);
            InterTypeMethodDeclaration[] interTypeMethodDeclarationArr = new InterTypeMethodDeclaration[itdms.size()];
            this.declaredITDMethods = interTypeMethodDeclarationArr;
            itdms.toArray(interTypeMethodDeclarationArr);
        }
        return this.declaredITDMethods;
    }

    public InterTypeMethodDeclaration getITDMethod(String name, AjType<?> target, AjType<?>... parameterTypes) throws NoSuchMethodException {
        for (InterTypeMethodDeclaration itdm : getITDMethods()) {
            try {
                if (itdm.getName().equals(name)) {
                    if (itdm.getTargetType().equals(target)) {
                        AjType<?>[] ptypes = itdm.getParameterTypes();
                        if (ptypes.length == parameterTypes.length) {
                            int i = 0;
                            while (i < ptypes.length) {
                                if (ptypes[i].equals(parameterTypes[i])) {
                                    i++;
                                }
                            }
                            return itdm;
                        }
                        continue;
                    } else {
                        continue;
                    }
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException(name);
    }

    public InterTypeMethodDeclaration[] getITDMethods() {
        if (this.itdMethods == null) {
            List<InterTypeMethodDeclaration> itdms = new ArrayList<>();
            for (Method m : this.clazz.getDeclaredMethods()) {
                if (m.getName().contains("ajc$interMethod$") && m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    if (Modifier.isPublic(ann.modifiers())) {
                        itdms.add(new InterTypeMethodDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), m));
                    }
                }
            }
            addAnnotationStyleITDMethods(itdms, true);
            InterTypeMethodDeclaration[] interTypeMethodDeclarationArr = new InterTypeMethodDeclaration[itdms.size()];
            this.itdMethods = interTypeMethodDeclarationArr;
            itdms.toArray(interTypeMethodDeclarationArr);
        }
        return this.itdMethods;
    }

    private void addAnnotationStyleITDMethods(List<InterTypeMethodDeclaration> toList, boolean publicOnly) {
        if (isAspect()) {
            for (Field f : this.clazz.getDeclaredFields()) {
                if (f.getType().isInterface() && f.isAnnotationPresent(DeclareParents.class)) {
                    Class cls = DeclareParents.class;
                    if (((DeclareParents) f.getAnnotation(cls)).defaultImpl() != cls) {
                        for (Method itdM : f.getType().getDeclaredMethods()) {
                            if (Modifier.isPublic(itdM.getModifiers()) || !publicOnly) {
                                toList.add(new InterTypeMethodDeclarationImpl(this, AjTypeSystem.getAjType(f.getType()), itdM, 1));
                            }
                        }
                    }
                }
            }
        }
    }

    private void addAnnotationStyleITDFields(List<InterTypeFieldDeclaration> list, boolean publicOnly) {
    }

    public InterTypeConstructorDeclaration getDeclaredITDConstructor(AjType<?> target, AjType<?>... parameterTypes) throws NoSuchMethodException {
        for (InterTypeConstructorDeclaration itdc : getDeclaredITDConstructors()) {
            try {
                if (itdc.getTargetType().equals(target)) {
                    AjType<?>[] ptypes = itdc.getParameterTypes();
                    if (ptypes.length == parameterTypes.length) {
                        int i = 0;
                        while (i < ptypes.length) {
                            if (ptypes[i].equals(parameterTypes[i])) {
                                i++;
                            }
                        }
                        return itdc;
                    }
                    continue;
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException();
    }

    public InterTypeConstructorDeclaration[] getDeclaredITDConstructors() {
        if (this.declaredITDCons == null) {
            List<InterTypeConstructorDeclaration> itdcs = new ArrayList<>();
            for (Method m : this.clazz.getDeclaredMethods()) {
                if (m.getName().contains("ajc$postInterConstructor") && m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    itdcs.add(new InterTypeConstructorDeclarationImpl(this, ann.targetType(), ann.modifiers(), m));
                }
            }
            InterTypeConstructorDeclaration[] interTypeConstructorDeclarationArr = new InterTypeConstructorDeclaration[itdcs.size()];
            this.declaredITDCons = interTypeConstructorDeclarationArr;
            itdcs.toArray(interTypeConstructorDeclarationArr);
        }
        return this.declaredITDCons;
    }

    public InterTypeConstructorDeclaration getITDConstructor(AjType<?> target, AjType<?>... parameterTypes) throws NoSuchMethodException {
        for (InterTypeConstructorDeclaration itdc : getITDConstructors()) {
            try {
                if (itdc.getTargetType().equals(target)) {
                    AjType<?>[] ptypes = itdc.getParameterTypes();
                    if (ptypes.length == parameterTypes.length) {
                        int i = 0;
                        while (i < ptypes.length) {
                            if (ptypes[i].equals(parameterTypes[i])) {
                                i++;
                            }
                        }
                        return itdc;
                    }
                    continue;
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        throw new NoSuchMethodException();
    }

    public InterTypeConstructorDeclaration[] getITDConstructors() {
        if (this.itdCons == null) {
            List<InterTypeConstructorDeclaration> itdcs = new ArrayList<>();
            for (Method m : this.clazz.getMethods()) {
                if (m.getName().contains("ajc$postInterConstructor") && m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    if (Modifier.isPublic(ann.modifiers())) {
                        itdcs.add(new InterTypeConstructorDeclarationImpl(this, ann.targetType(), ann.modifiers(), m));
                    }
                }
            }
            InterTypeConstructorDeclaration[] interTypeConstructorDeclarationArr = new InterTypeConstructorDeclaration[itdcs.size()];
            this.itdCons = interTypeConstructorDeclarationArr;
            itdcs.toArray(interTypeConstructorDeclarationArr);
        }
        return this.itdCons;
    }

    public InterTypeFieldDeclaration getDeclaredITDField(String name, AjType<?> target) throws NoSuchFieldException {
        for (InterTypeFieldDeclaration itdf : getDeclaredITDFields()) {
            if (itdf.getName().equals(name)) {
                try {
                    if (itdf.getTargetType().equals(target)) {
                        return itdf;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
        throw new NoSuchFieldException(name);
    }

    public InterTypeFieldDeclaration[] getDeclaredITDFields() {
        List<InterTypeFieldDeclaration> itdfs = new ArrayList<>();
        if (this.declaredITDFields == null) {
            for (Method m : this.clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(ajcITD.class) && m.getName().contains("ajc$interFieldInit")) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    String interFieldInitMethodName = m.getName();
                    String interFieldGetDispatchMethodName = interFieldInitMethodName.replace("FieldInit", "FieldGetDispatch");
                    try {
                        Method dispatch = this.clazz.getDeclaredMethod(interFieldGetDispatchMethodName, m.getParameterTypes());
                        String str = interFieldGetDispatchMethodName;
                        String str2 = interFieldInitMethodName;
                        try {
                            InterTypeFieldDeclarationImpl itdf = new InterTypeFieldDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), AjTypeSystem.getAjType(dispatch.getReturnType()), dispatch.getGenericReturnType());
                            itdfs.add(itdf);
                        } catch (NoSuchMethodException e) {
                        }
                    } catch (NoSuchMethodException e2) {
                        String str3 = interFieldGetDispatchMethodName;
                        String str4 = interFieldInitMethodName;
                        throw new IllegalStateException("Can't find field get dispatch method for " + m.getName());
                    }
                }
            }
            addAnnotationStyleITDFields(itdfs, false);
            InterTypeFieldDeclaration[] interTypeFieldDeclarationArr = new InterTypeFieldDeclaration[itdfs.size()];
            this.declaredITDFields = interTypeFieldDeclarationArr;
            itdfs.toArray(interTypeFieldDeclarationArr);
        }
        return this.declaredITDFields;
    }

    public InterTypeFieldDeclaration getITDField(String name, AjType<?> target) throws NoSuchFieldException {
        for (InterTypeFieldDeclaration itdf : getITDFields()) {
            if (itdf.getName().equals(name)) {
                try {
                    if (itdf.getTargetType().equals(target)) {
                        return itdf;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        }
        throw new NoSuchFieldException(name);
    }

    public InterTypeFieldDeclaration[] getITDFields() {
        List<InterTypeFieldDeclaration> itdfs = new ArrayList<>();
        if (this.itdFields == null) {
            for (Method m : this.clazz.getMethods()) {
                if (m.isAnnotationPresent(ajcITD.class)) {
                    ajcITD ann = (ajcITD) m.getAnnotation(ajcITD.class);
                    if (m.getName().contains("ajc$interFieldInit") && Modifier.isPublic(ann.modifiers())) {
                        String interFieldInitMethodName = m.getName();
                        String interFieldGetDispatchMethodName = interFieldInitMethodName.replace("FieldInit", "FieldGetDispatch");
                        try {
                            Method dispatch = m.getDeclaringClass().getDeclaredMethod(interFieldGetDispatchMethodName, m.getParameterTypes());
                            String str = interFieldGetDispatchMethodName;
                            String str2 = interFieldInitMethodName;
                            try {
                                InterTypeFieldDeclarationImpl itdf = new InterTypeFieldDeclarationImpl(this, ann.targetType(), ann.modifiers(), ann.name(), AjTypeSystem.getAjType(dispatch.getReturnType()), dispatch.getGenericReturnType());
                                itdfs.add(itdf);
                            } catch (NoSuchMethodException e) {
                            }
                        } catch (NoSuchMethodException e2) {
                            String str3 = interFieldGetDispatchMethodName;
                            String str4 = interFieldInitMethodName;
                            throw new IllegalStateException("Can't find field get dispatch method for " + m.getName());
                        }
                    }
                }
            }
            addAnnotationStyleITDFields(itdfs, true);
            InterTypeFieldDeclaration[] interTypeFieldDeclarationArr = new InterTypeFieldDeclaration[itdfs.size()];
            this.itdFields = interTypeFieldDeclarationArr;
            itdfs.toArray(interTypeFieldDeclarationArr);
        }
        return this.itdFields;
    }

    public DeclareErrorOrWarning[] getDeclareErrorOrWarnings() {
        List<DeclareErrorOrWarning> deows = new ArrayList<>();
        for (Field field : this.clazz.getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(DeclareWarning.class)) {
                    DeclareWarning dw = (DeclareWarning) field.getAnnotation(DeclareWarning.class);
                    if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                        deows.add(new DeclareErrorOrWarningImpl(dw.value(), (String) field.get((Object) null), false, this));
                    }
                } else if (field.isAnnotationPresent(DeclareError.class)) {
                    DeclareError de = (DeclareError) field.getAnnotation(DeclareError.class);
                    if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                        deows.add(new DeclareErrorOrWarningImpl(de.value(), (String) field.get((Object) null), true, this));
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
            }
        }
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclareEoW.class)) {
                ajcDeclareEoW deowAnn = (ajcDeclareEoW) method.getAnnotation(ajcDeclareEoW.class);
                deows.add(new DeclareErrorOrWarningImpl(deowAnn.pointcut(), deowAnn.message(), deowAnn.isError(), this));
            }
        }
        DeclareErrorOrWarning[] ret = new DeclareErrorOrWarning[deows.size()];
        deows.toArray(ret);
        return ret;
    }

    public org.aspectj.lang.reflect.DeclareParents[] getDeclareParents() {
        List<org.aspectj.lang.reflect.DeclareParents> decps = new ArrayList<>();
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclareParents.class)) {
                ajcDeclareParents decPAnn = (ajcDeclareParents) method.getAnnotation(ajcDeclareParents.class);
                decps.add(new DeclareParentsImpl(decPAnn.targetTypePattern(), decPAnn.parentTypes(), decPAnn.isExtends(), this));
            }
        }
        addAnnotationStyleDeclareParents(decps);
        if (getSupertype().isAspect()) {
            decps.addAll(Arrays.asList(getSupertype().getDeclareParents()));
        }
        org.aspectj.lang.reflect.DeclareParents[] ret = new org.aspectj.lang.reflect.DeclareParents[decps.size()];
        decps.toArray(ret);
        return ret;
    }

    private void addAnnotationStyleDeclareParents(List<org.aspectj.lang.reflect.DeclareParents> toList) {
        for (Field f : this.clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(DeclareParents.class) && f.getType().isInterface()) {
                toList.add(new DeclareParentsImpl(((DeclareParents) f.getAnnotation(DeclareParents.class)).value(), f.getType().getName(), false, this));
            }
        }
    }

    public DeclareSoft[] getDeclareSofts() {
        List<DeclareSoft> decs = new ArrayList<>();
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclareSoft.class)) {
                ajcDeclareSoft decSAnn = (ajcDeclareSoft) method.getAnnotation(ajcDeclareSoft.class);
                decs.add(new DeclareSoftImpl(this, decSAnn.pointcut(), decSAnn.exceptionType()));
            }
        }
        if (getSupertype().isAspect()) {
            decs.addAll(Arrays.asList(getSupertype().getDeclareSofts()));
        }
        DeclareSoft[] ret = new DeclareSoft[decs.size()];
        decs.toArray(ret);
        return ret;
    }

    public DeclareAnnotation[] getDeclareAnnotations() {
        Annotation targetAnnotation;
        List<DeclareAnnotation> decAs = new ArrayList<>();
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclareAnnotation.class)) {
                ajcDeclareAnnotation decAnn = (ajcDeclareAnnotation) method.getAnnotation(ajcDeclareAnnotation.class);
                Annotation[] arr$ = method.getAnnotations();
                int len$ = arr$.length;
                int i$ = 0;
                while (true) {
                    if (i$ >= len$) {
                        targetAnnotation = null;
                        break;
                    }
                    Annotation ann = arr$[i$];
                    if (ann.annotationType() != ajcDeclareAnnotation.class) {
                        targetAnnotation = ann;
                        break;
                    }
                    i$++;
                }
                decAs.add(new DeclareAnnotationImpl(this, decAnn.kind(), decAnn.pattern(), targetAnnotation, decAnn.annotation()));
            }
        }
        if (getSupertype().isAspect()) {
            decAs.addAll(Arrays.asList(getSupertype().getDeclareAnnotations()));
        }
        DeclareAnnotation[] ret = new DeclareAnnotation[decAs.size()];
        decAs.toArray(ret);
        return ret;
    }

    public DeclarePrecedence[] getDeclarePrecedence() {
        List<DeclarePrecedence> decps = new ArrayList<>();
        if (this.clazz.isAnnotationPresent(org.aspectj.lang.annotation.DeclarePrecedence.class)) {
            decps.add(new DeclarePrecedenceImpl(((org.aspectj.lang.annotation.DeclarePrecedence) this.clazz.getAnnotation(org.aspectj.lang.annotation.DeclarePrecedence.class)).value(), this));
        }
        for (Method method : this.clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ajcDeclarePrecedence.class)) {
                decps.add(new DeclarePrecedenceImpl(((ajcDeclarePrecedence) method.getAnnotation(ajcDeclarePrecedence.class)).value(), this));
            }
        }
        if (getSupertype().isAspect()) {
            decps.addAll(Arrays.asList(getSupertype().getDeclarePrecedence()));
        }
        DeclarePrecedence[] ret = new DeclarePrecedence[decps.size()];
        decps.toArray(ret);
        return ret;
    }

    public T[] getEnumConstants() {
        return this.clazz.getEnumConstants();
    }

    public TypeVariable<Class<T>>[] getTypeParameters() {
        return this.clazz.getTypeParameters();
    }

    public boolean isEnum() {
        return this.clazz.isEnum();
    }

    public boolean isInstance(Object o) {
        return this.clazz.isInstance(o);
    }

    public boolean isInterface() {
        return this.clazz.isInterface();
    }

    public boolean isLocalClass() {
        return this.clazz.isLocalClass() && !isAspect();
    }

    public boolean isMemberClass() {
        return this.clazz.isMemberClass() && !isAspect();
    }

    public boolean isArray() {
        return this.clazz.isArray();
    }

    public boolean isPrimitive() {
        return this.clazz.isPrimitive();
    }

    public boolean isAspect() {
        return this.clazz.getAnnotation(Aspect.class) != null;
    }

    public boolean isMemberAspect() {
        return this.clazz.isMemberClass() && isAspect();
    }

    public boolean isPrivileged() {
        return isAspect() && this.clazz.isAnnotationPresent(ajcPrivileged.class);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AjTypeImpl)) {
            return false;
        }
        return ((AjTypeImpl) obj).clazz.equals(this.clazz);
    }

    public int hashCode() {
        return this.clazz.hashCode();
    }

    private AjType<?>[] toAjTypeArray(Class<?>[] classes) {
        AjType<?>[] ajtypes = new AjType[classes.length];
        for (int i = 0; i < ajtypes.length; i++) {
            ajtypes[i] = AjTypeSystem.getAjType(classes[i]);
        }
        return ajtypes;
    }

    private Class<?>[] toClassArray(AjType<?>[] ajTypes) {
        Class<?>[] classes = new Class[ajTypes.length];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = ajTypes[i].getJavaClass();
        }
        return classes;
    }

    public String toString() {
        return getName();
    }
}
