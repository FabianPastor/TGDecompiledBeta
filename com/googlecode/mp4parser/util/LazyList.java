package com.googlecode.mp4parser.util;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LazyList<E>
  extends AbstractList<E>
{
  private static final Logger LOG = Logger.getLogger(LazyList.class);
  Iterator<E> elementSource;
  List<E> underlying;
  
  public LazyList(List<E> paramList, Iterator<E> paramIterator)
  {
    this.underlying = paramList;
    this.elementSource = paramIterator;
  }
  
  private void blowup()
  {
    LOG.logDebug("blowup running");
    for (;;)
    {
      if (!this.elementSource.hasNext()) {
        return;
      }
      this.underlying.add(this.elementSource.next());
    }
  }
  
  public E get(int paramInt)
  {
    if (this.underlying.size() > paramInt) {}
    for (Object localObject = this.underlying.get(paramInt);; localObject = get(paramInt))
    {
      return (E)localObject;
      if (!this.elementSource.hasNext()) {
        break;
      }
      this.underlying.add(this.elementSource.next());
    }
    throw new NoSuchElementException();
  }
  
  public Iterator<E> iterator()
  {
    new Iterator()
    {
      int pos = 0;
      
      public boolean hasNext()
      {
        if ((this.pos >= LazyList.this.underlying.size()) && (!LazyList.this.elementSource.hasNext())) {}
        for (boolean bool = false;; bool = true) {
          return bool;
        }
      }
      
      public E next()
      {
        int i;
        if (this.pos < LazyList.this.underlying.size())
        {
          localObject = LazyList.this.underlying;
          i = this.pos;
          this.pos = (i + 1);
        }
        for (Object localObject = ((List)localObject).get(i);; localObject = next())
        {
          return (E)localObject;
          LazyList.this.underlying.add(LazyList.this.elementSource.next());
        }
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public int size()
  {
    LOG.logDebug("potentially expensive size() call");
    blowup();
    return this.underlying.size();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/LazyList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */