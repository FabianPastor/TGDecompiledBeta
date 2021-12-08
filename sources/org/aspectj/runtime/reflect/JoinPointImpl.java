package org.aspectj.runtime.reflect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

class JoinPointImpl implements ProceedingJoinPoint {
    Object _this;
    private AroundClosure arc;
    Object[] args;
    JoinPoint.StaticPart staticPart;
    Object target;

    static class StaticPartImpl implements JoinPoint.StaticPart {
        private int id;
        String kind;
        Signature signature;
        SourceLocation sourceLocation;

        public StaticPartImpl(int id2, String kind2, Signature signature2, SourceLocation sourceLocation2) {
            this.kind = kind2;
            this.signature = signature2;
            this.sourceLocation = sourceLocation2;
            this.id = id2;
        }

        public int getId() {
            return this.id;
        }

        public String getKind() {
            return this.kind;
        }

        public Signature getSignature() {
            return this.signature;
        }

        public SourceLocation getSourceLocation() {
            return this.sourceLocation;
        }

        /* access modifiers changed from: package-private */
        public String toString(StringMaker sm) {
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

        public final String toShortString() {
            return toString(StringMaker.shortStringMaker);
        }

        public final String toLongString() {
            return toString(StringMaker.longStringMaker);
        }
    }

    static class EnclosingStaticPartImpl extends StaticPartImpl implements JoinPoint.EnclosingStaticPart {
        public EnclosingStaticPartImpl(int count, String kind, Signature signature, SourceLocation sourceLocation) {
            super(count, kind, signature, sourceLocation);
        }
    }

    public JoinPointImpl(JoinPoint.StaticPart staticPart2, Object _this2, Object target2, Object[] args2) {
        this.staticPart = staticPart2;
        this._this = _this2;
        this.target = target2;
        this.args = args2;
    }

    public Object getThis() {
        return this._this;
    }

    public Object getTarget() {
        return this.target;
    }

    public Object[] getArgs() {
        if (this.args == null) {
            this.args = new Object[0];
        }
        Object[] objArr = this.args;
        Object[] argsCopy = new Object[objArr.length];
        System.arraycopy(objArr, 0, argsCopy, 0, objArr.length);
        return argsCopy;
    }

    public JoinPoint.StaticPart getStaticPart() {
        return this.staticPart;
    }

    public String getKind() {
        return this.staticPart.getKind();
    }

    public Signature getSignature() {
        return this.staticPart.getSignature();
    }

    public SourceLocation getSourceLocation() {
        return this.staticPart.getSourceLocation();
    }

    public final String toString() {
        return this.staticPart.toString();
    }

    public final String toShortString() {
        return this.staticPart.toShortString();
    }

    public final String toLongString() {
        return this.staticPart.toLongString();
    }

    public void set$AroundClosure(AroundClosure arc2) {
        this.arc = arc2;
    }

    public Object proceed() throws Throwable {
        AroundClosure aroundClosure = this.arc;
        if (aroundClosure == null) {
            return null;
        }
        return aroundClosure.run(aroundClosure.getState());
    }

    public Object proceed(Object[] adviceBindings) throws Throwable {
        AroundClosure aroundClosure = this.arc;
        if (aroundClosure == null) {
            return null;
        }
        int flags = aroundClosure.getFlags();
        char c = 0;
        char c2 = 1;
        if ((1048576 & flags) != 0) {
        }
        boolean thisTargetTheSame = (65536 & flags) != 0;
        boolean hasThis = (flags & 4096) != 0;
        boolean bindsThis = (flags & 256) != 0;
        boolean hasTarget = (flags & 16) != 0;
        boolean bindsTarget = (flags & 1) != 0;
        Object[] state = this.arc.getState();
        int firstArgumentIndexIntoAdviceBindings = 0;
        int firstArgumentIndexIntoState = 0 + (hasThis ? 1 : 0) + ((!hasTarget || thisTargetTheSame) ? 0 : 1);
        if (hasThis && bindsThis) {
            firstArgumentIndexIntoAdviceBindings = 1;
            state[0] = adviceBindings[0];
        }
        if (hasTarget && bindsTarget) {
            if (thisTargetTheSame) {
                firstArgumentIndexIntoAdviceBindings = (bindsThis ? 1 : 0) + 1;
                if (!bindsThis) {
                    c2 = 0;
                }
                state[0] = adviceBindings[c2];
            } else {
                firstArgumentIndexIntoAdviceBindings = (hasThis ? 1 : 0) + 1;
                char c3 = hasThis ? (char) 1 : 0;
                if (hasThis) {
                    c = 1;
                }
                state[c3] = adviceBindings[c];
            }
        }
        for (int i = firstArgumentIndexIntoAdviceBindings; i < adviceBindings.length; i++) {
            state[(i - firstArgumentIndexIntoAdviceBindings) + firstArgumentIndexIntoState] = adviceBindings[i];
        }
        return this.arc.run(state);
    }
}
