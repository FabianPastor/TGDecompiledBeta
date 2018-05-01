package com.coremedia.iso.boxes;

public abstract interface FullBox
  extends Box
{
  public abstract int getFlags();
  
  public abstract int getVersion();
  
  public abstract void setFlags(int paramInt);
  
  public abstract void setVersion(int paramInt);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/FullBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */