package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.PointcutExpression;

public class DeclareSoftImpl implements DeclareSoft {
    private AjType<?> declaringType;
    private AjType<?> exceptionType;
    private String missingTypeName;
    private PointcutExpression pointcut;

    public DeclareSoftImpl(AjType<?> declaringType2, String pcut, String exceptionTypeName) {
        this.declaringType = declaringType2;
        this.pointcut = new PointcutExpressionImpl(pcut);
        try {
            this.exceptionType = AjTypeSystem.getAjType(Class.forName(exceptionTypeName, false, declaringType2.getJavaClass().getClassLoader()));
        } catch (ClassNotFoundException e) {
            this.missingTypeName = exceptionTypeName;
        }
    }

    public AjType getDeclaringType() {
        return this.declaringType;
    }

    public AjType getSoftenedExceptionType() throws ClassNotFoundException {
        if (this.missingTypeName == null) {
            return this.exceptionType;
        }
        throw new ClassNotFoundException(this.missingTypeName);
    }

    public PointcutExpression getPointcutExpression() {
        return this.pointcut;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("declare soft : ");
        String str = this.missingTypeName;
        if (str != null) {
            sb.append(this.exceptionType.getName());
        } else {
            sb.append(str);
        }
        sb.append(" : ");
        sb.append(getPointcutExpression().asString());
        return sb.toString();
    }
}
