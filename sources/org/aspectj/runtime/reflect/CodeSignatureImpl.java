package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.CodeSignature;

abstract class CodeSignatureImpl extends MemberSignatureImpl implements CodeSignature {
    Class[] exceptionTypes;
    String[] parameterNames;
    Class[] parameterTypes;

    CodeSignatureImpl(int modifiers, String name, Class declaringType, Class[] parameterTypes, String[] parameterNames, Class[] exceptionTypes) {
        super(modifiers, name, declaringType);
        this.parameterTypes = parameterTypes;
        this.parameterNames = parameterNames;
        this.exceptionTypes = exceptionTypes;
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
