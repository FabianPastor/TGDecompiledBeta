package org.aspectj.runtime.reflect;

abstract class CodeSignatureImpl extends MemberSignatureImpl {
    Class[] exceptionTypes;
    Class[] parameterTypes;

    CodeSignatureImpl(int i, String str, Class cls, Class[] clsArr, String[] strArr, Class[] clsArr2) {
        super(i, str, cls);
        this.parameterTypes = clsArr;
        this.exceptionTypes = clsArr2;
    }

    public Class[] getParameterTypes() {
        if (this.parameterTypes == null) {
            this.parameterTypes = extractTypes(3);
        }
        return this.parameterTypes;
    }

    public Class[] getExceptionTypes() {
        if (this.exceptionTypes == null) {
            this.exceptionTypes = extractTypes(5);
        }
        return this.exceptionTypes;
    }
}
