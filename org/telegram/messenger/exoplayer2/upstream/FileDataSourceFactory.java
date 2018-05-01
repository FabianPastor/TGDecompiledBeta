package org.telegram.messenger.exoplayer2.upstream;

public final class FileDataSourceFactory
  implements DataSource.Factory
{
  private final TransferListener<? super FileDataSource> listener;
  
  public FileDataSourceFactory()
  {
    this(null);
  }
  
  public FileDataSourceFactory(TransferListener<? super FileDataSource> paramTransferListener)
  {
    this.listener = paramTransferListener;
  }
  
  public DataSource createDataSource()
  {
    return new FileDataSource(this.listener);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/FileDataSourceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */