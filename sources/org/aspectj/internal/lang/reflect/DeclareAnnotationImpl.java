package org.aspectj.internal.lang.reflect;

import java.lang.annotation.Annotation;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.SignaturePattern;
import org.aspectj.lang.reflect.TypePattern;

public class DeclareAnnotationImpl implements DeclareAnnotation {
    private String annText;
    private AjType<?> declaringType;
    private DeclareAnnotation.Kind kind;
    private SignaturePattern signaturePattern;
    private Annotation theAnnotation;
    private TypePattern typePattern;

    public DeclareAnnotationImpl(AjType<?> declaring, String kindString, String pattern, Annotation ann, String annText2) {
        this.declaringType = declaring;
        if (kindString.equals("at_type")) {
            this.kind = DeclareAnnotation.Kind.Type;
        } else if (kindString.equals("at_field")) {
            this.kind = DeclareAnnotation.Kind.Field;
        } else if (kindString.equals("at_method")) {
            this.kind = DeclareAnnotation.Kind.Method;
        } else if (kindString.equals("at_constructor")) {
            this.kind = DeclareAnnotation.Kind.Constructor;
        } else {
            throw new IllegalStateException("Unknown declare annotation kind: " + kindString);
        }
        if (this.kind == DeclareAnnotation.Kind.Type) {
            this.typePattern = new TypePatternImpl(pattern);
        } else {
            this.signaturePattern = new SignaturePatternImpl(pattern);
        }
        this.theAnnotation = ann;
        this.annText = annText2;
    }

    public AjType<?> getDeclaringType() {
        return this.declaringType;
    }

    public DeclareAnnotation.Kind getKind() {
        return this.kind;
    }

    public SignaturePattern getSignaturePattern() {
        return this.signaturePattern;
    }

    public TypePattern getTypePattern() {
        return this.typePattern;
    }

    public Annotation getAnnotation() {
        return this.theAnnotation;
    }

    public String getAnnotationAsText() {
        return this.annText;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("declare @");
        switch (AnonymousClass1.$SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[getKind().ordinal()]) {
            case 1:
                sb.append("type : ");
                sb.append(getTypePattern().asString());
                break;
            case 2:
                sb.append("method : ");
                sb.append(getSignaturePattern().asString());
                break;
            case 3:
                sb.append("field : ");
                sb.append(getSignaturePattern().asString());
                break;
            case 4:
                sb.append("constructor : ");
                sb.append(getSignaturePattern().asString());
                break;
        }
        sb.append(" : ");
        sb.append(getAnnotationAsText());
        return sb.toString();
    }

    /* renamed from: org.aspectj.internal.lang.reflect.DeclareAnnotationImpl$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind;

        static {
            int[] iArr = new int[DeclareAnnotation.Kind.values().length];
            $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind = iArr;
            try {
                iArr[DeclareAnnotation.Kind.Type.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[DeclareAnnotation.Kind.Method.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[DeclareAnnotation.Kind.Field.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$aspectj$lang$reflect$DeclareAnnotation$Kind[DeclareAnnotation.Kind.Constructor.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }
}
