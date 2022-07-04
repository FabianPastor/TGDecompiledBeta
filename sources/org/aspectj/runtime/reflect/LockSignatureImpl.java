package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.LockSignature;

class LockSignatureImpl extends SignatureImpl implements LockSignature {
    private Class parameterType;

    LockSignatureImpl(Class c) {
        super(8, "lock", c);
        this.parameterType = c;
    }

    LockSignatureImpl(String stringRep) {
        super(stringRep);
    }

    /* access modifiers changed from: protected */
    public String createToString(StringMaker sm) {
        if (this.parameterType == null) {
            this.parameterType = extractType(3);
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("lock(");
        stringBuffer.append(sm.makeTypeName(this.parameterType));
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    public Class getParameterType() {
        if (this.parameterType == null) {
            this.parameterType = extractType(3);
        }
        return this.parameterType;
    }
}
