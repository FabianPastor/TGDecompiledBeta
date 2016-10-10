package org.telegram.messenger.exoplayer.upstream;

public abstract interface TransferListener
{
  public abstract void onBytesTransferred(int paramInt);
  
  public abstract void onTransferEnd();
  
  public abstract void onTransferStart();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/TransferListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */