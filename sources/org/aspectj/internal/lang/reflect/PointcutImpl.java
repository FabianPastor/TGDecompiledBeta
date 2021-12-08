package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.Pointcut;
import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutImpl implements Pointcut {
    private final Method baseMethod;
    private final AjType declaringType;
    private final String name;
    private String[] parameterNames = new String[0];
    private final PointcutExpression pc;

    protected PointcutImpl(String name2, String pc2, Method method, AjType declaringType2, String pNames) {
        this.name = name2;
        this.pc = new PointcutExpressionImpl(pc2);
        this.baseMethod = method;
        this.declaringType = declaringType2;
        this.parameterNames = splitOnComma(pNames);
    }

    public PointcutExpression getPointcutExpression() {
        return this.pc;
    }

    public String getName() {
        return this.name;
    }

    public int getModifiers() {
        return this.baseMethod.getModifiers();
    }

    public AjType<?>[] getParameterTypes() {
        Class<?>[] baseParamTypes = this.baseMethod.getParameterTypes();
        AjType<?>[] ajParamTypes = new AjType[baseParamTypes.length];
        for (int i = 0; i < ajParamTypes.length; i++) {
            ajParamTypes[i] = AjTypeSystem.getAjType(baseParamTypes[i]);
        }
        return ajParamTypes;
    }

    public AjType getDeclaringType() {
        return this.declaringType;
    }

    public String[] getParameterNames() {
        return this.parameterNames;
    }

    private String[] splitOnComma(String s) {
        StringTokenizer strTok = new StringTokenizer(s, ",");
        String[] ret = new String[strTok.countTokens()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = strTok.nextToken().trim();
        }
        return ret;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getName());
        sb.append("(");
        AjType<?>[] ptypes = getParameterTypes();
        for (int i = 0; i < ptypes.length; i++) {
            sb.append(ptypes[i].getName());
            String[] strArr = this.parameterNames;
            if (!(strArr == null || strArr[i] == null)) {
                sb.append(" ");
                sb.append(this.parameterNames[i]);
            }
            if (i + 1 < ptypes.length) {
                sb.append(",");
            }
        }
        sb.append(") : ");
        sb.append(getPointcutExpression().asString());
        return sb.toString();
    }
}
