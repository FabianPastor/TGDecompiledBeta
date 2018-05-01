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
    if (!localIterator.hasNext()) {}
    for (Object localObject = null;; localObject = (ChunkOffsetBox)localObject)
    {
      return (ChunkOffsetBox)localObject;
      localObject = (Box)localIterator.next();
      if (!(localObject instanceof ChunkOffsetBox)) {
        break;
      }
    }
  }
  
  public CompositionTimeToSample getCompositionTimeToSample()
  {
    Iterator localIterator = getBoxes().iterator();
    if (!localIterator.hasNext()) {}
    for (Object localObject = null;; localObject = (CompositionTimeToSample)localObject)
    {
      return (CompositionTimeToSample)localObject;
      localObject = (Box)localIterator.next();
      if (!(localObject instanceof CompositionTimeToSample)) {
        break;
      }
    }
  }
  
  public SampleDependencyTypeBox getSampleDependencyTypeBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (SampleDependencyTypeBox)localBox)
    {
      return (SampleDependencyTypeBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof SampleDependencyTypeBox)) {
        break;
      }
    }
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (SampleDescriptionBox)localBox)
    {
      return (SampleDescriptionBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof SampleDescriptionBox)) {
        break;
      }
    }
  }
  
  public SampleSizeBox getSampleSizeBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (SampleSizeBox)localBox)
    {
      return (SampleSizeBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof SampleSizeBox)) {
        break;
      }
    }
  }
  
  public SampleToChunkBox getSampleToChunkBox()
  {
    Object localObject;
    if (this.sampleToChunkBox != null) {
      localObject = this.sampleToChunkBox;
    }
    for (;;)
    {
      return (SampleToChunkBox)localObject;
      localObject = getBoxes().iterator();
      Box localBox;
      do
      {
        if (!((Iterator)localObject).hasNext())
        {
          localObject = null;
          break;
        }
        localBox = (Box)((Iterator)localObject).next();
      } while (!(localBox instanceof SampleToChunkBox));
      this.sampleToChunkBox = ((SampleToChunkBox)localBox);
      localObject = this.sampleToChunkBox;
    }
  }
  
  public SyncSampleBox getSyncSampleBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (SyncSampleBox)localBox)
    {
      return (SyncSampleBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof SyncSampleBox)) {
        break;
      }
    }
  }
  
  public TimeToSampleBox getTimeToSampleBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (TimeToSampleBox)localBox)
    {
      return (TimeToSampleBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof TimeToSampleBox)) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/SampleTableBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */