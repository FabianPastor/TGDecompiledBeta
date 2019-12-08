package org.aspectj.runtime.reflect;

import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;

class JoinPointImpl implements ProceedingJoinPoint {
    StaticPart staticPart;
    Object target;

    static class StaticPartImpl implements StaticPart {
        String kind;
        Signature signature;

        public StaticPartImpl(int i, String str, Signature signature, SourceLocation sourceLocation) {
            this.kind = str;
            this.signature = signature;
        }

        public String getKind() {
            return this.kind;
        }

        public Signature getSignature() {
            return this.signature;
        }

        /* Access modifiers changed, original: 0000 */
        public String toString(StringMaker stringMaker) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(stringMaker.makeKindName(getKind()));
            stringBuffer.append("(");
            stringBuffer.append(((SignatureImpl) getSignature()).toString(stringMaker));
            stringBuffer.append(")");
            return stringBuffer.toString();
        }

        public final String toString() {
            return toString(StringMaker.middleStringMaker);
        }
    }

    public JoinPointImpl(StaticPart staticPart, Object obj, Object obj2, Object[] objArr) {
        this.staticPart = staticPart;
        this.target = obj2;
    }

    public Object getTarget() {
        return this.target;
    }

    public final String toString() {
        return this.staticPart.toString();
    }
}
