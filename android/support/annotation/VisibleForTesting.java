package android.support.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface VisibleForTesting
{
  public static final int NONE = 5;
  public static final int PACKAGE_PRIVATE = 3;
  public static final int PRIVATE = 2;
  public static final int PROTECTED = 4;
  
  int otherwise() default 2;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/annotation/VisibleForTesting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */