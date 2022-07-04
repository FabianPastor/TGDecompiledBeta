package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.PointcutBasedPerClause;
import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutBasedPerClauseImpl extends PerClauseImpl implements PointcutBasedPerClause {
    private final PointcutExpression pointcutExpression;

    public PointcutBasedPerClauseImpl(PerClauseKind kind, String pointcutExpression2) {
        super(kind);
        this.pointcutExpression = new PointcutExpressionImpl(pointcutExpression2);
    }

    public PointcutExpression getPointcutExpression() {
        return this.pointcutExpression;
    }

    /* renamed from: org.aspectj.internal.lang.reflect.PointcutBasedPerClauseImpl$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$aspectj$lang$reflect$PerClauseKind;

        static {
            int[] iArr = new int[PerClauseKind.values().length];
            $SwitchMap$org$aspectj$lang$reflect$PerClauseKind = iArr;
            try {
                iArr[PerClauseKind.PERCFLOW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERCFLOWBELOW.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERTARGET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$PerClauseKind[PerClauseKind.PERTHIS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        switch (AnonymousClass1.$SwitchMap$org$aspectj$lang$reflect$PerClauseKind[getKind().ordinal()]) {
            case 1:
                sb.append("percflow(");
                break;
            case 2:
                sb.append("percflowbelow(");
                break;
            case 3:
                sb.append("pertarget(");
                break;
            case 4:
                sb.append("perthis(");
                break;
        }
        sb.append(this.pointcutExpression.asString());
        sb.append(")");
        return sb.toString();
    }
}
