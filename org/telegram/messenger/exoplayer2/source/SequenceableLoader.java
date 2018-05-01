package org.telegram.messenger.exoplayer2.source;

public abstract interface SequenceableLoader
{
  public abstract boolean continueLoading(long paramLong);
  
  public abstract long getBufferedPositionUs();
  
  public abstract long getNextLoadPositionUs();
  
  public abstract void reevaluateBuffer(long paramLong);
  
  public static abstract interface Callback<T extends SequenceableLoader>
  {
    public abstract void onContinueLoadingRequested(T paramT);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/SequenceableLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */