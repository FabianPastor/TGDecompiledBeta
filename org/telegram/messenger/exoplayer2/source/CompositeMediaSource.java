package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class CompositeMediaSource<T>
  implements MediaSource
{
  private final HashMap<T, MediaSource> childSources = new HashMap();
  private ExoPlayer player;
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    Iterator localIterator = this.childSources.values().iterator();
    while (localIterator.hasNext()) {
      ((MediaSource)localIterator.next()).maybeThrowSourceInfoRefreshError();
    }
  }
  
  protected abstract void onChildSourceInfoRefreshed(T paramT, MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject);
  
  protected void prepareChildSource(final T paramT, final MediaSource paramMediaSource)
  {
    if (!this.childSources.containsKey(paramT)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.childSources.put(paramT, paramMediaSource);
      paramMediaSource.prepareSource(this.player, false, new MediaSource.Listener()
      {
        public void onSourceInfoRefreshed(MediaSource paramAnonymousMediaSource, Timeline paramAnonymousTimeline, Object paramAnonymousObject)
        {
          CompositeMediaSource.this.onChildSourceInfoRefreshed(paramT, paramMediaSource, paramAnonymousTimeline, paramAnonymousObject);
        }
      });
      return;
    }
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    this.player = paramExoPlayer;
  }
  
  protected void releaseChildSource(T paramT)
  {
    ((MediaSource)this.childSources.remove(paramT)).releaseSource();
  }
  
  public void releaseSource()
  {
    Iterator localIterator = this.childSources.values().iterator();
    while (localIterator.hasNext()) {
      ((MediaSource)localIterator.next()).releaseSource();
    }
    this.childSources.clear();
    this.player = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/CompositeMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */