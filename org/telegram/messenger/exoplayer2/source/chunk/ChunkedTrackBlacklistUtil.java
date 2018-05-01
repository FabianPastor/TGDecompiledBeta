package org.telegram.messenger.exoplayer2.source.chunk;

import android.util.Log;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;

public final class ChunkedTrackBlacklistUtil
{
  public static final long DEFAULT_TRACK_BLACKLIST_MS = 60000L;
  private static final String TAG = "ChunkedTrackBlacklist";
  
  public static boolean maybeBlacklistTrack(TrackSelection paramTrackSelection, int paramInt, Exception paramException)
  {
    return maybeBlacklistTrack(paramTrackSelection, paramInt, paramException, 60000L);
  }
  
  public static boolean maybeBlacklistTrack(TrackSelection paramTrackSelection, int paramInt, Exception paramException, long paramLong)
  {
    boolean bool;
    int i;
    if (shouldBlacklist(paramException))
    {
      bool = paramTrackSelection.blacklist(paramInt, paramLong);
      i = ((HttpDataSource.InvalidResponseCodeException)paramException).responseCode;
      if (bool) {
        Log.w("ChunkedTrackBlacklist", "Blacklisted: duration=" + paramLong + ", responseCode=" + i + ", format=" + paramTrackSelection.getFormat(paramInt));
      }
    }
    for (;;)
    {
      return bool;
      Log.w("ChunkedTrackBlacklist", "Blacklisting failed (cannot blacklist last enabled track): responseCode=" + i + ", format=" + paramTrackSelection.getFormat(paramInt));
      continue;
      bool = false;
    }
  }
  
  public static boolean shouldBlacklist(Exception paramException)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if ((paramException instanceof HttpDataSource.InvalidResponseCodeException))
    {
      int i = ((HttpDataSource.InvalidResponseCodeException)paramException).responseCode;
      if (i != 404)
      {
        bool2 = bool1;
        if (i != 410) {}
      }
      else
      {
        bool2 = true;
      }
    }
    return bool2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/chunk/ChunkedTrackBlacklistUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */