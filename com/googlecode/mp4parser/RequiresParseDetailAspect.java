package com.googlecode.mp4parser;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.NoAspectBoundException;

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
      for (;;)
      {
        ajc$initFailureCause = localThrowable;
      }
    }
  }
  
  public static RequiresParseDetailAspect aspectOf()
  {
    if (ajc$perSingletonInstance == null) {
      throw new NoAspectBoundException("com.googlecode.mp4parser.RequiresParseDetailAspect", ajc$initFailureCause);
    }
    return ajc$perSingletonInstance;
  }
  
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