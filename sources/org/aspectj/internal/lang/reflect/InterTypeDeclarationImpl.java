package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.InterTypeDeclaration;

public class InterTypeDeclarationImpl implements InterTypeDeclaration {
    private AjType<?> declaringType;
    private int modifiers;
    private AjType<?> targetType;
    protected String targetTypeName;

    public InterTypeDeclarationImpl(AjType<?> decType, String target, int mods) {
        this.declaringType = decType;
        this.targetTypeName = target;
        this.modifiers = mods;
        try {
            this.targetType = (AjType) StringToType.stringToType(target, decType.getJavaClass());
        } catch (ClassNotFoundException e) {
        }
    }

    public InterTypeDeclarationImpl(AjType<?> decType, AjType<?> targetType2, int mods) {
        this.declaringType = decType;
        this.targetType = targetType2;
        this.targetTypeName = targetType2.getName();
        this.modifiers = mods;
    }

    public AjType<?> getDeclaringType() {
        return this.declaringType;
    }

    public AjType<?> getTargetType() throws ClassNotFoundException {
        AjType<?> ajType = this.targetType;
        if (ajType != null) {
            return ajType;
        }
        throw new ClassNotFoundException(this.targetTypeName);
    }

    public int getModifiers() {
        return this.modifiers;
    }
}
