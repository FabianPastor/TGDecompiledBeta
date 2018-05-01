package org.telegram.messenger.exoplayer2.upstream;

public abstract interface TransferListener<S>
{
  public abstract void onBytesTransferred(S paramS, int paramInt);
  
  public abstract void onTransferEnd(S paramS);
  
  public abstract void onTransferStart(S paramS, DataSpec paramDataSpec);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/TransferListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */