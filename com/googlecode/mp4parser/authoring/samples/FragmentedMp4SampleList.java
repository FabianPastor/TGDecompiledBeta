package com.googlecode.mp4parser.authoring.samples;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox.Entry;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FragmentedMp4SampleList
  extends AbstractList<Sample>
{
  private List<TrackFragmentBox> allTrafs;
  private int[] firstSamples;
  IsoFile[] fragments;
  private SoftReference<Sample>[] sampleCache;
  private int size_ = -1;
  Container topLevel;
  TrackBox trackBox = null;
  TrackExtendsBox trex = null;
  private Map<TrackRunBox, SoftReference<ByteBuffer>> trunDataCache = new HashMap();
  
  public FragmentedMp4SampleList(long paramLong, Container paramContainer, IsoFile... paramVarArgs)
  {
    this.topLevel = paramContainer;
    this.fragments = paramVarArgs;
    Iterator localIterator = Path.getPaths(paramContainer, "moov[0]/trak").iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        if (this.trackBox != null) {
          break;
        }
        throw new RuntimeException("This MP4 does not contain track " + paramLong);
      }
      paramVarArgs = (TrackBox)localIterator.next();
      if (paramVarArgs.getTrackHeaderBox().getTrackId() == paramLong) {
        this.trackBox = paramVarArgs;
      }
    }
    paramContainer = Path.getPaths(paramContainer, "moov[0]/mvex[0]/trex").iterator();
    for (;;)
    {
      if (!paramContainer.hasNext())
      {
        this.sampleCache = new SoftReference[size()];
        initAllFragments();
        return;
      }
      paramVarArgs = (TrackExtendsBox)paramContainer.next();
      if (paramVarArgs.getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
        this.trex = paramVarArgs;
      }
    }
  }
  
  private int getTrafSize(TrackFragmentBox paramTrackFragmentBox)
  {
    paramTrackFragmentBox = paramTrackFragmentBox.getBoxes();
    int i = 0;
    int j = 0;
    for (;;)
    {
      if (j >= paramTrackFragmentBox.size()) {
        return i;
      }
      Box localBox = (Box)paramTrackFragmentBox.get(j);
      int k = i;
      if ((localBox instanceof TrackRunBox)) {
        k = i + CastUtils.l2i(((TrackRunBox)localBox).getSampleCount());
      }
      j++;
      i = k;
    }
  }
  
  private List<TrackFragmentBox> initAllFragments()
  {
    if (this.allTrafs != null)
    {
      localObject1 = this.allTrafs;
      return (List<TrackFragmentBox>)localObject1;
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject2 = this.topLevel.getBoxes(MovieFragmentBox.class).iterator();
    int j;
    TrackFragmentBox localTrackFragmentBox;
    for (;;)
    {
      if (!((Iterator)localObject2).hasNext())
      {
        int i;
        if (this.fragments != null)
        {
          localObject2 = this.fragments;
          i = localObject2.length;
          j = 0;
          if (j < i) {}
        }
        else
        {
          this.allTrafs = localArrayList;
          i = 1;
          this.firstSamples = new int[this.allTrafs.size()];
          for (j = 0;; j++)
          {
            localObject1 = localArrayList;
            if (j >= this.allTrafs.size()) {
              break;
            }
            this.firstSamples[j] = i;
            i += getTrafSize((TrackFragmentBox)this.allTrafs.get(j));
          }
        }
      }
      else
      {
        localObject1 = ((MovieFragmentBox)((Iterator)localObject2).next()).getBoxes(TrackFragmentBox.class).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localTrackFragmentBox = (TrackFragmentBox)((Iterator)localObject1).next();
          if (localTrackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
            localArrayList.add(localTrackFragmentBox);
          }
        }
      }
    }
    Object localObject1 = localObject2[j].getBoxes(MovieFragmentBox.class).iterator();
    for (;;)
    {
      if (!((Iterator)localObject1).hasNext())
      {
        j++;
        break;
      }
      Iterator localIterator = ((MovieFragmentBox)((Iterator)localObject1).next()).getBoxes(TrackFragmentBox.class).iterator();
      while (localIterator.hasNext())
      {
        localTrackFragmentBox = (TrackFragmentBox)localIterator.next();
        if (localTrackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
          localArrayList.add(localTrackFragmentBox);
        }
      }
    }
  }
  
  public Sample get(int paramInt)
  {
    Object localObject1;
    if (this.sampleCache[paramInt] != null)
    {
      localObject1 = (Sample)this.sampleCache[paramInt].get();
      if (localObject1 != null) {
        return (Sample)localObject1;
      }
    }
    int i = paramInt + 1;
    final int j = this.firstSamples.length - 1;
    int k;
    Object localObject2;
    Object localObject3;
    if (i - this.firstSamples[j] >= 0)
    {
      localObject1 = (TrackFragmentBox)this.allTrafs.get(j);
      k = i - this.firstSamples[j];
      i = 0;
      localObject2 = (MovieFragmentBox)((TrackFragmentBox)localObject1).getParent();
      localObject3 = ((TrackFragmentBox)localObject1).getBoxes().iterator();
    }
    Object localObject4;
    for (;;)
    {
      if (!((Iterator)localObject3).hasNext())
      {
        throw new RuntimeException("Couldn't find sample in the traf I was looking");
        j--;
        break;
      }
      localObject4 = (Box)((Iterator)localObject3).next();
      if ((localObject4 instanceof TrackRunBox))
      {
        localObject4 = (TrackRunBox)localObject4;
        if (((TrackRunBox)localObject4).getEntries().size() >= k - i) {
          break label187;
        }
        i += ((TrackRunBox)localObject4).getEntries().size();
      }
    }
    label187:
    List localList = ((TrackRunBox)localObject4).getEntries();
    TrackFragmentHeaderBox localTrackFragmentHeaderBox = ((TrackFragmentBox)localObject1).getTrackFragmentHeaderBox();
    boolean bool1 = ((TrackRunBox)localObject4).isSampleSizePresent();
    boolean bool2 = localTrackFragmentHeaderBox.hasDefaultSampleSize();
    final long l1 = 0L;
    label234:
    label261:
    long l2;
    label294:
    long l3;
    if (!bool1)
    {
      if (bool2) {
        l1 = localTrackFragmentHeaderBox.getDefaultSampleSize();
      }
    }
    else
    {
      localObject1 = (SoftReference)this.trunDataCache.get(localObject4);
      if (localObject1 == null) {
        break label484;
      }
      localObject1 = (ByteBuffer)((SoftReference)localObject1).get();
      localObject3 = localObject1;
      if (localObject1 == null)
      {
        l2 = 0L;
        if (!localTrackFragmentHeaderBox.hasBaseDataOffset()) {
          break label489;
        }
        l2 = 0L + localTrackFragmentHeaderBox.getBaseDataOffset();
        localObject1 = ((MovieFragmentBox)localObject2).getParent();
        l3 = l2;
        if (((TrackRunBox)localObject4).isDataOffsetPresent()) {
          l3 = l2 + ((TrackRunBox)localObject4).getDataOffset();
        }
        j = 0;
        localObject3 = localList.iterator();
        label329:
        if (((Iterator)localObject3).hasNext()) {
          break label495;
        }
        l2 = j;
      }
    }
    label484:
    label489:
    label495:
    label549:
    label594:
    for (;;)
    {
      int m;
      try
      {
        localObject3 = ((Container)localObject1).getByteBuffer(l3, l2);
        localObject1 = this.trunDataCache;
        localObject2 = new java/lang/ref/SoftReference;
        ((SoftReference)localObject2).<init>(localObject3);
        ((Map)localObject1).put(localObject4, localObject2);
        j = 0;
        m = 0;
        if (m < k - i) {
          break label549;
        }
        if (!bool1) {
          break label594;
        }
        l1 = ((TrackRunBox.Entry)localList.get(k - i)).getSampleSize();
        localObject1 = new Sample() {};
        this.sampleCache[paramInt] = new SoftReference(localObject1);
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
      if (this.trex == null) {
        throw new RuntimeException("File doesn't contain trex box but track fragments aren't fully self contained. Cannot determine sample size.");
      }
      l1 = this.trex.getDefaultSampleSize();
      break label234;
      localObject1 = null;
      break label261;
      localObject1 = localObject2;
      break label294;
      localObject2 = (TrackRunBox.Entry)((Iterator)localObject3).next();
      if (bool1)
      {
        j = (int)(j + ((TrackRunBox.Entry)localObject2).getSampleSize());
        break label329;
      }
      j = (int)(j + l1);
      break label329;
      if (bool1) {}
      for (j = (int)(j + ((TrackRunBox.Entry)localList.get(m)).getSampleSize());; j = (int)(j + l1))
      {
        m++;
        break;
      }
    }
  }
  
  public int size()
  {
    if (this.size_ != -1)
    {
      i = this.size_;
      return i;
    }
    int i = 0;
    Iterator localIterator1 = this.topLevel.getBoxes(MovieFragmentBox.class).iterator();
    Object localObject1;
    int j;
    if (!localIterator1.hasNext())
    {
      localObject1 = this.fragments;
      j = localObject1.length;
    }
    Object localObject2;
    Iterator localIterator2;
    for (int k = 0;; k++)
    {
      if (k >= j)
      {
        this.size_ = i;
        break;
        localObject2 = ((MovieFragmentBox)localIterator1.next()).getBoxes(TrackFragmentBox.class).iterator();
        k = i;
        for (;;)
        {
          i = k;
          if (!((Iterator)localObject2).hasNext()) {
            break;
          }
          localObject1 = (TrackFragmentBox)((Iterator)localObject2).next();
          if (((TrackFragmentBox)localObject1).getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
            k = (int)(k + ((TrackRunBox)((TrackFragmentBox)localObject1).getBoxes(TrackRunBox.class).get(0)).getSampleCount());
          }
        }
      }
      localIterator2 = localObject1[k].getBoxes(MovieFragmentBox.class).iterator();
      if (localIterator2.hasNext()) {
        break label199;
      }
    }
    label199:
    localIterator1 = ((MovieFragmentBox)localIterator2.next()).getBoxes(TrackFragmentBox.class).iterator();
    int m = i;
    for (;;)
    {
      i = m;
      if (!localIterator1.hasNext()) {
        break;
      }
      localObject2 = (TrackFragmentBox)localIterator1.next();
      if (((TrackFragmentBox)localObject2).getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
        m = (int)(m + ((TrackRunBox)((TrackFragmentBox)localObject2).getBoxes(TrackRunBox.class).get(0)).getSampleCount());
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/samples/FragmentedMp4SampleList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */