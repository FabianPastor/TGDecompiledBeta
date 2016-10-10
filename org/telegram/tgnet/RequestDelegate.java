package org.telegram.tgnet;

public abstract interface RequestDelegate
{
  public abstract void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/RequestDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */