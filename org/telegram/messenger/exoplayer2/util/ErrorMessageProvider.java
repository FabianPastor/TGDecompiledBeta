package org.telegram.messenger.exoplayer2.util;

import android.util.Pair;

public abstract interface ErrorMessageProvider<T extends Exception>
{
  public abstract Pair<Integer, String> getErrorMessage(T paramT);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/ErrorMessageProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */