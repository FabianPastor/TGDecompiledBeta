package org.telegram.messenger.secretmedia;

import android.content.Context;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;

public final class ExtendedDefaultDataSourceFactory
  implements DataSource.Factory
{
  private final DataSource.Factory baseDataSourceFactory;
  private final Context context;
  private final TransferListener<? super DataSource> listener;
  
  public ExtendedDefaultDataSourceFactory(Context paramContext, String paramString)
  {
    this(paramContext, paramString, null);
  }
  
  public ExtendedDefaultDataSourceFactory(Context paramContext, String paramString, TransferListener<? super DataSource> paramTransferListener)
  {
    this(paramContext, paramTransferListener, new DefaultHttpDataSourceFactory(paramString, paramTransferListener));
  }
  
  public ExtendedDefaultDataSourceFactory(Context paramContext, TransferListener<? super DataSource> paramTransferListener, DataSource.Factory paramFactory)
  {
    this.context = paramContext.getApplicationContext();
    this.listener = paramTransferListener;
    this.baseDataSourceFactory = paramFactory;
  }
  
  public ExtendedDefaultDataSource createDataSource()
  {
    return new ExtendedDefaultDataSource(this.context, this.listener, this.baseDataSourceFactory.createDataSource());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/secretmedia/ExtendedDefaultDataSourceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */