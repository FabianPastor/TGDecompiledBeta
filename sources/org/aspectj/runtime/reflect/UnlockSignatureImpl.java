package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.UnlockSignature;

class UnlockSignatureImpl extends SignatureImpl implements UnlockSignature {
    private Class parameterType;

    UnlockSignatureImpl(Class c) {
        super(8, "unlock", c);
        this.parameterType = c;
    }

    UnlockSignatureImpl(String stringRep) {
        super(stringRep);
    }

    /* access modifiers changed from: protected */
    public String createToString(StringMaker sm) {
        if (this.parameterType == null) {
            this.parameterType = extractType(3);
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("unlock(");
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
