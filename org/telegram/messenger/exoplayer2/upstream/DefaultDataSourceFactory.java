package org.telegram.messenger.exoplayer2.upstream;

import android.content.Context;

public final class DefaultDataSourceFactory
  implements DataSource.Factory
{
  private final DataSource.Factory baseDataSourceFactory;
  private final Context context;
  private final TransferListener<? super DataSource> listener;
  
  public DefaultDataSourceFactory(Context paramContext, String paramString)
  {
    this(paramContext, paramString, null);
  }
  
  public DefaultDataSourceFactory(Context paramContext, String paramString, TransferListener<? super DataSource> paramTransferListener)
  {
    this(paramContext, paramTransferListener, new DefaultHttpDataSourceFactory(paramString, paramTransferListener));
  }
  
  public DefaultDataSourceFactory(Context paramContext, TransferListener<? super DataSource> paramTransferListener, DataSource.Factory paramFactory)
  {
    this.context = paramContext.getApplicationContext();
    this.listener = paramTransferListener;
    this.baseDataSourceFactory = paramFactory;
  }
  
  public DefaultDataSource createDataSource()
  {
    return new DefaultDataSource(this.context, this.listener, this.baseDataSourceFactory.createDataSource());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/DefaultDataSourceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */