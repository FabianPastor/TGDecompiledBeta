package org.aspectj.runtime.reflect;

import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;

class JoinPointImpl implements ProceedingJoinPoint {
    Object _this;
    Object[] args;
    StaticPart staticPart;
    Object target;

    static class StaticPartImpl implements StaticPart {
        private int id;
        String kind;
        Signature signature;
        SourceLocation sourceLocation;

        public StaticPartImpl(int id, String kind, Signature signature, SourceLocation sourceLocation) {
            this.kind = kind;
            this.signature = signature;
            this.sourceLocation = sourceLocation;
            this.id = id;
        }

        public String getKind() {
            return this.kind;
        }

        public Signature getSignature() {
            return this.signature;
        }

        String toString(StringMaker sm) {
            StringBuffer buf = new StringBuffer();
            buf.append(sm.makeKindName(getKind()));
            buf.append("(");
            buf.append(((SignatureImpl) getSignature()).toString(sm));
            buf.append(")");
            return buf.toString();
        }

        public final String toString() {
            return toString(StringMaker.middleStringMaker);
        }
    }

    public JoinPointImpl(StaticPart staticPart, Object _this, Object target, Object[] args) {
        this.staticPart = staticPart;
        this._this = _this;
        this.target = target;
        this.args = args;
    }

    public Object getTarget() {
        return this.target;
    }

    public final String toString() {
        return this.staticPart.toString();
    }
}
