package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.MemberSignature;

abstract class MemberSignatureImpl extends SignatureImpl implements MemberSignature {
    MemberSignatureImpl(int i, String str, Class cls) {
        super(i, str, cls);
    }
}
