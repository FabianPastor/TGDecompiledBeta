package org.telegram.messenger.exoplayer.chunk;

import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer.util.Assertions;

public abstract class Chunk
  implements Loader.Loadable
{
  public static final int NO_PARENT_ID = -1;
  public static final int TRIGGER_ADAPTIVE = 3;
  public static final int TRIGGER_CUSTOM_BASE = 10000;
  public static final int TRIGGER_INITIAL = 1;
  public static final int TRIGGER_MANUAL = 2;
  public static final int TRIGGER_TRICK_PLAY = 4;
  public static final int TRIGGER_UNSPECIFIED = 0;
  public static final int TYPE_CUSTOM_BASE = 10000;
  public static final int TYPE_DRM = 3;
  public static final int TYPE_MANIFEST = 4;
  public static final int TYPE_MEDIA = 1;
  public static final int TYPE_MEDIA_INITIALIZATION = 2;
  public static final int TYPE_UNSPECIFIED = 0;
  protected final DataSource dataSource;
  public final DataSpec dataSpec;
  public final Format format;
  public final int parentId;
  public final int trigger;
  public final int type;
  
  public Chunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3)
  {
    this.dataSource = ((DataSource)Assertions.checkNotNull(paramDataSource));
    this.dataSpec = ((DataSpec)Assertions.checkNotNull(paramDataSpec));
    this.type = paramInt1;
    this.trigger = paramInt2;
    this.format = paramFormat;
    this.parentId = paramInt3;
  }
  
  public abstract long bytesLoaded();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/chunk/Chunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */