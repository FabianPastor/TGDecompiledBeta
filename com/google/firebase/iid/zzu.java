package com.google.firebase.iid;

public final class zzu
  extends Exception
{
  private final int errorCode;
  
  public zzu(int paramInt, String paramString)
  {
    super(paramString);
    this.errorCode = paramInt;
  }
  
  public final int getErrorCode()
  {
    return this.errorCode;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */