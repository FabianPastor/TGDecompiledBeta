package org.aspectj.internal.lang.reflect;

import java.lang.annotation.Annotation;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareAnnotation;
import org.aspectj.lang.reflect.DeclareAnnotation.Kind;
import org.aspectj.lang.reflect.SignaturePattern;
import org.aspectj.lang.reflect.TypePattern;

public class DeclareAnnotationImpl
  implements DeclareAnnotation
{
  private String annText;
  private AjType<?> declaringType;
  private DeclareAnnotation.Kind kind;
  private SignaturePattern signaturePattern;
  private Annotation theAnnotation;
  private TypePattern typePattern;
  
  public DeclareAnnotationImpl(AjType<?> paramAjType, String paramString1, String paramString2, Annotation paramAnnotation, String paramString3)
  {
    this.declaringType = paramAjType;
    if (paramString1.equals("at_type"))
    {
      this.kind = DeclareAnnotation.Kind.Type;
      if (this.kind != DeclareAnnotation.Kind.Type) {
        break label144;
      }
      this.typePattern = new TypePatternImpl(paramString2);
    }
    for (;;)
    {
      this.theAnnotation = paramAnnotation;
      this.annText = paramString3;
      return;
      if (paramString1.equals("at_field"))
      {
        this.kind = DeclareAnnotation.Kind.Field;
        break;
      }
      if (paramString1.equals("at_method"))
      {
        this.kind = DeclareAnnotation.Kind.Method;
        break;
      }
      if (paramString1.equals("at_constructor"))
      {
        this.kind = DeclareAnnotation.Kind.Constructor;
        break;
      }
      throw new IllegalStateException("Unknown declare annotation kind: " + paramString1);
      label144:
      this.signaturePattern = new SignaturePatternImpl(paramString2);
    }
  }
  
  public Annotation getAnnotation()
  {
    return this.theAnnotation;
  }
  
  public String getAnnotationAsText()
  {
    return this.annText;
  }
  
  public AjType<?> getDeclaringType()
  {
    return this.declaringType;
  }
  
  public DeclareAnnotation.Kind getKind()
  {
    return this.kind;
  }
  
  public SignaturePattern getSignaturePattern()
  {
    return this.signaturePattern;
  }
  
  public TypePattern getTypePattern()
  {
    return this.typePattern;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("declare @");
    switch (getKind())
    {
    }
    for (;;)
    {
      localStringBuffer.append(" : ");
      localStringBuffer.append(getAnnotationAsText());
      return localStringBuffer.toString();
      localStringBuffer.append("type : ");
      localStringBuffer.append(getTypePattern().asString());
      continue;
      localStringBuffer.append("method : ");
      localStringBuffer.append(getSignaturePattern().asString());
      continue;
      localStringBuffer.append("field : ");
      localStringBuffer.append(getSignaturePattern().asString());
      continue;
      localStringBuffer.append("constructor : ");
      localStringBuffer.append(getSignaturePattern().asString());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/aspectj/internal/lang/reflect/DeclareAnnotationImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */