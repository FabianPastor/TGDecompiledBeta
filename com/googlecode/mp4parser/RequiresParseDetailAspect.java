package com.googlecode.mp4parser;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class RequiresParseDetailAspect
{
  static
  {
    try
    {
      ajc$postClinit();
      return;
    }
    catch (Throwable localThrowable)
    {
      ajc$initFailureCause = localThrowable;
    }
  }
  
  public static RequiresParseDetailAspect aspectOf()
  {
    if (ajc$perSingletonInstance == null) {
      throw new NoAspectBoundException("com.googlecode.mp4parser.RequiresParseDetailAspect", ajc$initFailureCause);
    }
    return ajc$perSingletonInstance;
  }
  
  public static boolean hasAspect()
  {
    return ajc$perSingletonInstance != null;
  }
  
  @Before("this(com.googlecode.mp4parser.AbstractBox) && ((execution(public * * (..)) && !( execution(* parseDetails()) || execution(* getNumOfBytesToFirstChild()) || execution(* getType()) || execution(* isParsed()) || execution(* getHeader(*)) || execution(* parse()) || execution(* getBox(*)) || execution(* getSize()) || execution(* getOffset()) || execution(* parseDetails()) || execution(* _parseDetails(*)) || execution(* parse(*,*,*,*)) || execution(* getIsoFile()) || execution(* getParent()) || execution(* setParent(*)) || execution(* getUserType()) || execution(* setUserType(*))) && !@annotation(com.googlecode.mp4parser.annotations.DoNotParseDetail)) || @annotation(com.googlecode.mp4parser.annotations.ParseDetail))")
  public void before(JoinPoint paramJoinPoint)
  {
    if ((paramJoinPoint.getTarget() instanceof AbstractBox))
    {
      if (!((AbstractBox)paramJoinPoint.getTarget()).isParsed()) {
        ((AbstractBox)paramJoinPoint.getTarget()).parseDetails();
      }
      return;
    }
    throw new RuntimeException("Only methods in subclasses of " + AbstractBox.class.getName() + " can  be annotated with ParseDetail");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/RequiresParseDetailAspect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */