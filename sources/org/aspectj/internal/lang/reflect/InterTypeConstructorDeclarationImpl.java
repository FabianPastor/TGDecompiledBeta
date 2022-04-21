package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;

public class InterTypeConstructorDeclarationImpl extends InterTypeDeclarationImpl implements InterTypeConstructorDeclaration {
    private Method baseMethod;

    public InterTypeConstructorDeclarationImpl(AjType<?> decType, String target, int mods, Method baseMethod2) {
        super(decType, target, mods);
        this.baseMethod = baseMethod2;
    }

    public AjType<?>[] getParameterTypes() {
        Class<?>[] baseTypes = this.baseMethod.getParameterTypes();
        AjType<?>[] ret = new AjType[(baseTypes.length - 1)];
        for (int i = 1; i < baseTypes.length; i++) {
            ret[i - 1] = AjTypeSystem.getAjType(baseTypes[i]);
        }
        return ret;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: org.aspectj.lang.reflect.AjType[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.reflect.Type[] getGenericParameterTypes() {
        /*
            r5 = this;
            java.lang.reflect.Method r0 = r5.baseMethod
            java.lang.reflect.Type[] r0 = r0.getGenericParameterTypes()
            int r1 = r0.length
            int r1 = r1 + -1
            org.aspectj.lang.reflect.AjType[] r1 = new org.aspectj.lang.reflect.AjType[r1]
            r2 = 1
        L_0x000c:
            int r3 = r0.length
            if (r2 >= r3) goto L_0x002b
            r3 = r0[r2]
            boolean r3 = r3 instanceof java.lang.Class
            if (r3 == 0) goto L_0x0022
            int r3 = r2 + -1
            r4 = r0[r2]
            java.lang.Class r4 = (java.lang.Class) r4
            org.aspectj.lang.reflect.AjType r4 = org.aspectj.lang.reflect.AjTypeSystem.getAjType(r4)
            r1[r3] = r4
            goto L_0x0028
        L_0x0022:
            int r3 = r2 + -1
            r4 = r0[r2]
            r1[r3] = r4
        L_0x0028:
            int r2 = r2 + 1
            goto L_0x000c
        L_0x002b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.aspectj.internal.lang.reflect.InterTypeConstructorDeclarationImpl.getGenericParameterTypes():java.lang.reflect.Type[]");
    }

    public AjType<?>[] getExceptionTypes() {
        Class<?>[] baseTypes = this.baseMethod.getExceptionTypes();
        AjType<?>[] ret = new AjType[baseTypes.length];
        for (int i = 0; i < baseTypes.length; i++) {
            ret[i] = AjTypeSystem.getAjType(baseTypes[i]);
        }
        return ret;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(Modifier.toString(getModifiers()));
        sb.append(" ");
        sb.append(this.targetTypeName);
        sb.append(".new");
        sb.append("(");
        AjType<?>[] pTypes = getParameterTypes();
        for (int i = 0; i < pTypes.length - 1; i++) {
            sb.append(pTypes[i].toString());
            sb.append(", ");
        }
        if (pTypes.length > 0) {
            sb.append(pTypes[pTypes.length - 1].toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
