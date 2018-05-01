package org.telegram.messenger.exoplayer2.source.hls;

import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;

public final class DefaultHlsDataSourceFactory
  implements HlsDataSourceFactory
{
  private final DataSource.Factory dataSourceFactory;
  
  public DefaultHlsDataSourceFactory(DataSource.Factory paramFactory)
  {
    this.dataSourceFactory = paramFactory;
  }
  
  public DataSource createDataSource(int paramInt)
  {
    return this.dataSourceFactory.createDataSource();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/DefaultHlsDataSourceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */