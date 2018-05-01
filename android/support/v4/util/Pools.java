package android.support.v4.util;

public final class Pools
{
  public static abstract interface Pool<T>
  {
    public abstract T acquire();
    
    public abstract boolean release(T paramT);
  }
  
  public static class SimplePool<T>
    implements Pools.Pool<T>
  {
    private final Object[] mPool;
    private int mPoolSize;
    
    public SimplePool(int paramInt)
    {
      if (paramInt <= 0) {
        throw new IllegalArgumentException("The max pool size must be > 0");
      }
      this.mPool = new Object[paramInt];
    }
    
    private boolean isInPool(T paramT)
    {
      int i = 0;
      if (i < this.mPoolSize) {
        if (this.mPool[i] != paramT) {}
      }
      for (boolean bool = true;; bool = false)
      {
        return bool;
        i++;
        break;
      }
    }
    
    public T acquire()
    {
      Object localObject;
      if (this.mPoolSize > 0)
      {
        int i = this.mPoolSize - 1;
        localObject = this.mPool[i];
        this.mPool[i] = null;
        this.mPoolSize -= 1;
      }
      for (;;)
      {
        return (T)localObject;
        localObject = null;
      }
    }
    
    public boolean release(T paramT)
    {
      if (isInPool(paramT)) {
        throw new IllegalStateException("Already in the pool!");
      }
      if (this.mPoolSize < this.mPool.length)
      {
        this.mPool[this.mPoolSize] = paramT;
        this.mPoolSize += 1;
      }
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/util/Pools.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */