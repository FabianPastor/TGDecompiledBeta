package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class SampleTableBox
  extends AbstractContainerBox
{
  public static final String TYPE = "stbl";
  private SampleToChunkBox sampleToChunkBox;
  
  public SampleTableBox()
  {
    super("stbl");
  }
  
  public ChunkOffsetBox getChunkOffsetBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof ChunkOffsetBox));
    return (ChunkOffsetBox)localBox;
  }
  
  public CompositionTimeToSample getCompositionTimeToSample()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof CompositionTimeToSample));
    return (CompositionTimeToSample)localBox;
  }
  
  public SampleDependencyTypeBox getSampleDependencyTypeBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof SampleDependencyTypeBox));
    return (SampleDependencyTypeBox)localBox;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof SampleDescriptionBox));
    return (SampleDescriptionBox)localBox;
  }
  
  public SampleSizeBox getSampleSizeBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof SampleSizeBox));
    return (SampleSizeBox)localBox;
  }
  
  public SampleToChunkBox getSampleToChunkBox()
  {
    if (this.sampleToChunkBox != null) {
      return this.sampleToChunkBox;
    }
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof SampleToChunkBox));
    this.sampleToChunkBox = ((SampleToChunkBox)localBox);
    return this.sampleToChunkBox;
  }
  
  public SyncSampleBox getSyncSampleBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof SyncSampleBox));
    return (SyncSampleBox)localBox;
  }
  
  public TimeToSampleBox getTimeToSampleBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof TimeToSampleBox));
    return (TimeToSampleBox)localBox;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/SampleTableBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */