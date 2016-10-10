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
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
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
    paramVarArgs = Path.getPaths(paramContainer, "moov[0]/trak").iterator();
    for (;;)
    {
      if (!paramVarArgs.hasNext())
      {
        if (this.trackBox != null) {
          break;
        }
        throw new RuntimeException("This MP4 does not contain track " + paramLong);
      }
      TrackBox localTrackBox = (TrackBox)paramVarArgs.next();
      if (localTrackBox.getTrackHeaderBox().getTrackId() == paramLong) {
        this.trackBox = localTrackBox;
      }
    }
    paramContainer = Path.getPaths(paramContainer, "moov[0]/mvex[0]/trex").iterator();
    for (;;)
    {
      if (!paramContainer.hasNext())
      {
        this.sampleCache = ((SoftReference[])Array.newInstance(SoftReference.class, size()));
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
    int j = 0;
    int i = 0;
    for (;;)
    {
      if (i >= paramTrackFragmentBox.size()) {
        return j;
      }
      Box localBox = (Box)paramTrackFragmentBox.get(i);
      int k = j;
      if ((localBox instanceof TrackRunBox)) {
        k = j + CastUtils.l2i(((TrackRunBox)localBox).getSampleCount());
      }
      i += 1;
      j = k;
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
    Object localObject1 = this.topLevel.getBoxes(MovieFragmentBox.class).iterator();
    int i;
    Object localObject2;
    for (;;)
    {
      if (!((Iterator)localObject1).hasNext())
      {
        int j;
        if (this.fragments != null)
        {
          localObject1 = this.fragments;
          j = localObject1.length;
          i = 0;
          if (i < j) {}
        }
        else
        {
          this.allTrafs = localArrayList;
          j = 1;
          this.firstSamples = new int[this.allTrafs.size()];
          i = 0;
          for (;;)
          {
            localObject1 = localArrayList;
            if (i >= this.allTrafs.size()) {
              break;
            }
            this.firstSamples[i] = j;
            j += getTrafSize((TrackFragmentBox)this.allTrafs.get(i));
            i += 1;
          }
        }
      }
      else
      {
        localIterator = ((MovieFragmentBox)((Iterator)localObject1).next()).getBoxes(TrackFragmentBox.class).iterator();
        while (localIterator.hasNext())
        {
          localObject2 = (TrackFragmentBox)localIterator.next();
          if (((TrackFragmentBox)localObject2).getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
            localArrayList.add(localObject2);
          }
        }
      }
    }
    Iterator localIterator = localObject1[i].getBoxes(MovieFragmentBox.class).iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        i += 1;
        break;
      }
      localObject2 = ((MovieFragmentBox)localIterator.next()).getBoxes(TrackFragmentBox.class).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        TrackFragmentBox localTrackFragmentBox = (TrackFragmentBox)((Iterator)localObject2).next();
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
    int j = paramInt + 1;
    final int i = this.firstSamples.length - 1;
    int m;
    Object localObject3;
    Object localObject2;
    if (j - this.firstSamples[i] >= 0)
    {
      localObject1 = (TrackFragmentBox)this.allTrafs.get(i);
      m = j - this.firstSamples[i];
      j = 0;
      localObject3 = (MovieFragmentBox)((TrackFragmentBox)localObject1).getParent();
      localObject2 = ((TrackFragmentBox)localObject1).getBoxes().iterator();
    }
    Object localObject4;
    for (;;)
    {
      if (!((Iterator)localObject2).hasNext())
      {
        throw new RuntimeException("Couldn't find sample in the traf I was looking");
        i -= 1;
        break;
      }
      localObject4 = (Box)((Iterator)localObject2).next();
      if ((localObject4 instanceof TrackRunBox))
      {
        localObject4 = (TrackRunBox)localObject4;
        if (((TrackRunBox)localObject4).getEntries().size() >= m - j) {
          break label190;
        }
        j += ((TrackRunBox)localObject4).getEntries().size();
      }
    }
    label190:
    List localList = ((TrackRunBox)localObject4).getEntries();
    TrackFragmentHeaderBox localTrackFragmentHeaderBox = ((TrackFragmentBox)localObject1).getTrackFragmentHeaderBox();
    boolean bool1 = ((TrackRunBox)localObject4).isSampleSizePresent();
    boolean bool2 = localTrackFragmentHeaderBox.hasDefaultSampleSize();
    final long l1 = 0L;
    label269:
    long l2;
    label305:
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
        break label487;
      }
      localObject1 = (ByteBuffer)((SoftReference)localObject1).get();
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        l2 = 0L;
        if (!localTrackFragmentHeaderBox.hasBaseDataOffset()) {
          break label493;
        }
        l2 = 0L + localTrackFragmentHeaderBox.getBaseDataOffset();
        localObject1 = ((MovieFragmentBox)localObject3).getParent();
        l3 = l2;
        if (((TrackRunBox)localObject4).isDataOffsetPresent()) {
          l3 = l2 + ((TrackRunBox)localObject4).getDataOffset();
        }
        i = 0;
        localObject2 = localList.iterator();
        label339:
        if (((Iterator)localObject2).hasNext()) {
          break label500;
        }
        l2 = i;
      }
    }
    label487:
    label493:
    label500:
    label552:
    label596:
    for (;;)
    {
      int k;
      try
      {
        localObject2 = ((Container)localObject1).getByteBuffer(l3, l2);
        this.trunDataCache.put(localObject4, new SoftReference(localObject2));
        i = 0;
        k = 0;
        if (k < m - j) {
          break label552;
        }
        if (!bool1) {
          break label596;
        }
        l1 = ((TrackRunBox.Entry)localList.get(m - j)).getSampleSize();
        localObject1 = new Sample()
        {
          public ByteBuffer asByteBuffer()
          {
            return (ByteBuffer)((ByteBuffer)i.position(this.val$finalOffset)).slice().limit(CastUtils.l2i(l1));
          }
          
          public long getSize()
          {
            return l1;
          }
          
          public void writeTo(WritableByteChannel paramAnonymousWritableByteChannel)
            throws IOException
          {
            paramAnonymousWritableByteChannel.write(asByteBuffer());
          }
        };
        this.sampleCache[paramInt] = new SoftReference(localObject1);
        return (Sample)localObject1;
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException(localIOException);
      }
      if (this.trex == null) {
        throw new RuntimeException("File doesn't contain trex box but track fragments aren't fully self contained. Cannot determine sample size.");
      }
      l1 = this.trex.getDefaultSampleSize();
      break;
      localObject1 = null;
      break label269;
      localObject1 = localObject3;
      break label305;
      localObject3 = (TrackRunBox.Entry)((Iterator)localObject2).next();
      if (bool1)
      {
        i = (int)(i + ((TrackRunBox.Entry)localObject3).getSampleSize());
        break label339;
      }
      i = (int)(i + l1);
      break label339;
      if (bool1) {}
      for (i = (int)(i + ((TrackRunBox.Entry)localList.get(k)).getSampleSize());; i = (int)(i + l1))
      {
        k += 1;
        break;
      }
    }
  }
  
  public int size()
  {
    if (this.size_ != -1) {
      return this.size_;
    }
    int i = 0;
    Object localObject1 = this.topLevel.getBoxes(MovieFragmentBox.class).iterator();
    int m;
    int j;
    if (!((Iterator)localObject1).hasNext())
    {
      localObject1 = this.fragments;
      m = localObject1.length;
      j = 0;
    }
    Iterator localIterator;
    for (;;)
    {
      if (j >= m)
      {
        this.size_ = i;
        return i;
        localIterator = ((MovieFragmentBox)((Iterator)localObject1).next()).getBoxes(TrackFragmentBox.class).iterator();
        j = i;
        for (;;)
        {
          i = j;
          if (!localIterator.hasNext()) {
            break;
          }
          localObject2 = (TrackFragmentBox)localIterator.next();
          if (((TrackFragmentBox)localObject2).getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
            j = (int)(j + ((TrackRunBox)((TrackFragmentBox)localObject2).getBoxes(TrackRunBox.class).get(0)).getSampleCount());
          }
        }
      }
      localIterator = localObject1[j].getBoxes(MovieFragmentBox.class).iterator();
      if (localIterator.hasNext()) {
        break label199;
      }
      j += 1;
    }
    label199:
    Object localObject2 = ((MovieFragmentBox)localIterator.next()).getBoxes(TrackFragmentBox.class).iterator();
    int k = i;
    for (;;)
    {
      i = k;
      if (!((Iterator)localObject2).hasNext()) {
        break;
      }
      TrackFragmentBox localTrackFragmentBox = (TrackFragmentBox)((Iterator)localObject2).next();
      if (localTrackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
        k = (int)(k + ((TrackRunBox)localTrackFragmentBox.getBoxes(TrackRunBox.class).get(0)).getSampleCount());
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/samples/FragmentedMp4SampleList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */