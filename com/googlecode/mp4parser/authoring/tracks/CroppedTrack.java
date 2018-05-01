package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class CroppedTrack
  extends AbstractTrack
{
  private int fromSample;
  Track origTrack;
  private int toSample;
  
  static
  {
    if (!CroppedTrack.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public CroppedTrack(Track paramTrack, long paramLong1, long paramLong2)
  {
    super("crop(" + paramTrack.getName() + ")");
    this.origTrack = paramTrack;
    assert (paramLong1 <= 2147483647L);
    assert (paramLong2 <= 2147483647L);
    this.fromSample = ((int)paramLong1);
    this.toSample = ((int)paramLong2);
  }
  
  static List<CompositionTimeToSample.Entry> getCompositionTimeEntries(List<CompositionTimeToSample.Entry> paramList, long paramLong1, long paramLong2)
  {
    if ((paramList != null) && (!paramList.isEmpty()))
    {
      long l = 0L;
      ListIterator localListIterator = paramList.listIterator();
      ArrayList localArrayList = new ArrayList();
      for (;;)
      {
        paramList = (CompositionTimeToSample.Entry)localListIterator.next();
        if (paramList.getCount() + l > paramLong1)
        {
          if (paramList.getCount() + l < paramLong2) {
            break;
          }
          localArrayList.add(new CompositionTimeToSample.Entry((int)(paramLong2 - paramLong1), paramList.getOffset()));
          return localArrayList;
        }
        l += paramList.getCount();
      }
      localArrayList.add(new CompositionTimeToSample.Entry((int)(paramList.getCount() + l - paramLong1), paramList.getOffset()));
      for (paramLong1 = l + paramList.getCount();; paramLong1 += paramList.getCount())
      {
        if (localListIterator.hasNext())
        {
          paramList = (CompositionTimeToSample.Entry)localListIterator.next();
          if (paramList.getCount() + paramLong1 < paramLong2) {}
        }
        else
        {
          localArrayList.add(new CompositionTimeToSample.Entry((int)(paramLong2 - paramLong1), paramList.getOffset()));
          return localArrayList;
        }
        localArrayList.add(paramList);
      }
    }
    return null;
  }
  
  static List<TimeToSampleBox.Entry> getDecodingTimeEntries(List<TimeToSampleBox.Entry> paramList, long paramLong1, long paramLong2)
  {
    if ((paramList != null) && (!paramList.isEmpty()))
    {
      long l = 0L;
      ListIterator localListIterator = paramList.listIterator();
      LinkedList localLinkedList = new LinkedList();
      for (;;)
      {
        paramList = (TimeToSampleBox.Entry)localListIterator.next();
        if (paramList.getCount() + l > paramLong1)
        {
          if (paramList.getCount() + l < paramLong2) {
            break;
          }
          localLinkedList.add(new TimeToSampleBox.Entry(paramLong2 - paramLong1, paramList.getDelta()));
          return localLinkedList;
        }
        l += paramList.getCount();
      }
      localLinkedList.add(new TimeToSampleBox.Entry(paramList.getCount() + l - paramLong1, paramList.getDelta()));
      for (paramLong1 = l + paramList.getCount();; paramLong1 += paramList.getCount())
      {
        if (localListIterator.hasNext())
        {
          paramList = (TimeToSampleBox.Entry)localListIterator.next();
          if (paramList.getCount() + paramLong1 < paramLong2) {}
        }
        else
        {
          localLinkedList.add(new TimeToSampleBox.Entry(paramLong2 - paramLong1, paramList.getDelta()));
          return localLinkedList;
        }
        localLinkedList.add(paramList);
      }
    }
    return null;
  }
  
  public void close()
    throws IOException
  {
    this.origTrack.close();
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return getCompositionTimeEntries(this.origTrack.getCompositionTimeEntries(), this.fromSample, this.toSample);
  }
  
  public String getHandler()
  {
    return this.origTrack.getHandler();
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    if ((this.origTrack.getSampleDependencies() != null) && (!this.origTrack.getSampleDependencies().isEmpty())) {
      return this.origTrack.getSampleDependencies().subList(this.fromSample, this.toSample);
    }
    return null;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.origTrack.getSampleDescriptionBox();
  }
  
  public long[] getSampleDurations()
  {
    try
    {
      long[] arrayOfLong = new long[this.toSample - this.fromSample];
      System.arraycopy(this.origTrack.getSampleDurations(), this.fromSample, arrayOfLong, 0, arrayOfLong.length);
      return arrayOfLong;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public List<Sample> getSamples()
  {
    return this.origTrack.getSamples().subList(this.fromSample, this.toSample);
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return this.origTrack.getSubsampleInformationBox();
  }
  
  /* Error */
  public long[] getSyncSamples()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 49	com/googlecode/mp4parser/authoring/tracks/CroppedTrack:origTrack	Lcom/googlecode/mp4parser/authoring/Track;
    //   6: invokeinterface 165 1 0
    //   11: ifnull +130 -> 141
    //   14: aload_0
    //   15: getfield 49	com/googlecode/mp4parser/authoring/tracks/CroppedTrack:origTrack	Lcom/googlecode/mp4parser/authoring/Track;
    //   18: invokeinterface 165 1 0
    //   23: astore 4
    //   25: iconst_0
    //   26: istore_1
    //   27: aload 4
    //   29: arraylength
    //   30: istore_3
    //   31: iload_3
    //   32: istore_2
    //   33: iload_1
    //   34: aload 4
    //   36: arraylength
    //   37: if_icmpge +18 -> 55
    //   40: aload 4
    //   42: iload_1
    //   43: laload
    //   44: aload_0
    //   45: getfield 57	com/googlecode/mp4parser/authoring/tracks/CroppedTrack:fromSample	I
    //   48: i2l
    //   49: lcmp
    //   50: iflt +56 -> 106
    //   53: iload_3
    //   54: istore_2
    //   55: iload_2
    //   56: ifle +18 -> 74
    //   59: aload_0
    //   60: getfield 59	com/googlecode/mp4parser/authoring/tracks/CroppedTrack:toSample	I
    //   63: i2l
    //   64: aload 4
    //   66: iload_2
    //   67: iconst_1
    //   68: isub
    //   69: laload
    //   70: lcmp
    //   71: iflt +42 -> 113
    //   74: aload_0
    //   75: getfield 49	com/googlecode/mp4parser/authoring/tracks/CroppedTrack:origTrack	Lcom/googlecode/mp4parser/authoring/Track;
    //   78: invokeinterface 165 1 0
    //   83: iload_1
    //   84: iload_2
    //   85: invokestatic 171	java/util/Arrays:copyOfRange	([JII)[J
    //   88: astore 4
    //   90: iconst_0
    //   91: istore_1
    //   92: aload 4
    //   94: arraylength
    //   95: istore_2
    //   96: iload_1
    //   97: iload_2
    //   98: if_icmplt +22 -> 120
    //   101: aload_0
    //   102: monitorexit
    //   103: aload 4
    //   105: areturn
    //   106: iload_1
    //   107: iconst_1
    //   108: iadd
    //   109: istore_1
    //   110: goto -79 -> 31
    //   113: iload_2
    //   114: iconst_1
    //   115: isub
    //   116: istore_2
    //   117: goto -62 -> 55
    //   120: aload 4
    //   122: iload_1
    //   123: aload 4
    //   125: iload_1
    //   126: laload
    //   127: aload_0
    //   128: getfield 57	com/googlecode/mp4parser/authoring/tracks/CroppedTrack:fromSample	I
    //   131: i2l
    //   132: lsub
    //   133: lastore
    //   134: iload_1
    //   135: iconst_1
    //   136: iadd
    //   137: istore_1
    //   138: goto -46 -> 92
    //   141: aconst_null
    //   142: astore 4
    //   144: goto -43 -> 101
    //   147: astore 4
    //   149: aload_0
    //   150: monitorexit
    //   151: aload 4
    //   153: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	154	0	this	CroppedTrack
    //   26	112	1	i	int
    //   32	85	2	j	int
    //   30	24	3	k	int
    //   23	120	4	arrayOfLong	long[]
    //   147	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	25	147	finally
    //   27	31	147	finally
    //   33	53	147	finally
    //   59	74	147	finally
    //   74	90	147	finally
    //   92	96	147	finally
    //   120	134	147	finally
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.origTrack.getTrackMetaData();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/CroppedTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */