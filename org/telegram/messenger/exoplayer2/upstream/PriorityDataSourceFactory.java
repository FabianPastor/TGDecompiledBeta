package org.telegram.messenger.exoplayer2.upstream;

import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;

public final class PriorityDataSourceFactory
  implements DataSource.Factory
{
  private final int priority;
  private final PriorityTaskManager priorityTaskManager;
  private final DataSource.Factory upstreamFactory;
  
  public PriorityDataSourceFactory(DataSource.Factory paramFactory, PriorityTaskManager paramPriorityTaskManager, int paramInt)
  {
    this.upstreamFactory = paramFactory;
    this.priorityTaskManager = paramPriorityTaskManager;
    this.priority = paramInt;
  }
  
  public PriorityDataSource createDataSource()
  {
    return new PriorityDataSource(this.upstreamFactory.createDataSource(), this.priorityTaskManager, this.priority);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/PriorityDataSourceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */