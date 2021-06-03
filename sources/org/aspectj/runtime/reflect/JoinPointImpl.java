package org.aspectj.runtime.reflect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;

class JoinPointImpl implements JoinPoint {
    Object _this;
    Object[] args;
    JoinPoint.StaticPart staticPart;
    Object target;

    static class StaticPartImpl implements JoinPoint.StaticPart {
        private int id;
        String kind;
        Signature signature;
        SourceLocation sourceLocation;

        public StaticPartImpl(int i, String str, Signature signature2, SourceLocation sourceLocation2) {
            this.kind = str;
            this.signature = signature2;
            this.sourceLocation = sourceLocation2;
            this.id = i;
        }

        public String getKind() {
            return this.kind;
        }

        public Signature getSignature() {
            return this.signature;
        }

        /* access modifiers changed from: package-private */
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

    public JoinPointImpl(JoinPoint.StaticPart staticPart2, Object obj, Object obj2, Object[] objArr) {
        this.staticPart = staticPart2;
        this._this = obj;
        this.target = obj2;
        this.args = objArr;
    }

    public Object getTarget() {
        return this.target;
    }

    public final String toString() {
        return this.staticPart.toString();
    }
}
