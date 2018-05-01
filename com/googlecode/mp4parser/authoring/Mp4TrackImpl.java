package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.EditListBox.Entry;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.SubSampleInformationBox.SubSampleEntry;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.SampleFlags;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox.Entry;
import com.coremedia.iso.boxes.mdat.SampleList;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox.Entry;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Mp4TrackImpl
  extends AbstractTrack
{
  private List<CompositionTimeToSample.Entry> compositionTimeEntries;
  private long[] decodingTimes;
  IsoFile[] fragments;
  private String handler;
  private List<SampleDependencyTypeBox.Entry> sampleDependencies;
  private SampleDescriptionBox sampleDescriptionBox;
  private List<Sample> samples;
  private SubSampleInformationBox subSampleInformationBox = null;
  private long[] syncSamples = new long[0];
  TrackBox trackBox;
  private TrackMetaData trackMetaData = new TrackMetaData();
  
  public Mp4TrackImpl(String paramString, TrackBox paramTrackBox, IsoFile... paramVarArgs)
  {
    super(paramString);
    long l3 = paramTrackBox.getTrackHeaderBox().getTrackId();
    this.samples = new SampleList(paramTrackBox, paramVarArgs);
    paramString = paramTrackBox.getMediaBox().getMediaInformationBox().getSampleTableBox();
    this.handler = paramTrackBox.getMediaBox().getHandlerBox().getHandlerType();
    Object localObject1 = new ArrayList();
    this.compositionTimeEntries = new ArrayList();
    this.sampleDependencies = new ArrayList();
    ((List)localObject1).addAll(paramString.getTimeToSampleBox().getEntries());
    if (paramString.getCompositionTimeToSample() != null) {
      this.compositionTimeEntries.addAll(paramString.getCompositionTimeToSample().getEntries());
    }
    if (paramString.getSampleDependencyTypeBox() != null) {
      this.sampleDependencies.addAll(paramString.getSampleDependencyTypeBox().getEntries());
    }
    if (paramString.getSyncSampleBox() != null) {
      this.syncSamples = paramString.getSyncSampleBox().getSampleNumber();
    }
    this.subSampleInformationBox = ((SubSampleInformationBox)Path.getPath(paramString, "subs"));
    Object localObject2 = new ArrayList();
    ((List)localObject2).addAll(((Box)paramTrackBox.getParent()).getParent().getBoxes(MovieFragmentBox.class));
    int j = paramVarArgs.length;
    int i = 0;
    if (i >= j)
    {
      this.sampleDescriptionBox = paramString.getSampleDescriptionBox();
      paramVarArgs = paramTrackBox.getParent().getBoxes(MovieExtendsBox.class);
      if (paramVarArgs.size() <= 0) {
        break label1501;
      }
      paramVarArgs = paramVarArgs.iterator();
      break label543;
      label286:
      if (paramVarArgs.hasNext()) {
        break label521;
      }
      new ArrayList();
      new ArrayList();
      paramString = ((List)localObject2).iterator();
      label319:
      if (paramString.hasNext()) {
        break label1415;
      }
      label328:
      this.decodingTimes = TimeToSampleBox.blowupTimeToSamples((List)localObject1);
      paramString = paramTrackBox.getMediaBox().getMediaHeaderBox();
      paramVarArgs = paramTrackBox.getTrackHeaderBox();
      this.trackMetaData.setTrackId(paramVarArgs.getTrackId());
      this.trackMetaData.setCreationTime(paramString.getCreationTime());
      this.trackMetaData.setLanguage(paramString.getLanguage());
      this.trackMetaData.setModificationTime(paramString.getModificationTime());
      this.trackMetaData.setTimescale(paramString.getTimescale());
      this.trackMetaData.setHeight(paramVarArgs.getHeight());
      this.trackMetaData.setWidth(paramVarArgs.getWidth());
      this.trackMetaData.setLayer(paramVarArgs.getLayer());
      this.trackMetaData.setMatrix(paramVarArgs.getMatrix());
      paramVarArgs = (EditListBox)Path.getPath(paramTrackBox, "edts/elst");
      paramTrackBox = (MovieHeaderBox)Path.getPath(paramTrackBox, "../mvhd");
      if (paramVarArgs != null) {
        paramVarArgs = paramVarArgs.getEntries().iterator();
      }
    }
    for (;;)
    {
      if (!paramVarArgs.hasNext())
      {
        return;
        ((List)localObject2).addAll(paramVarArgs[i].getBoxes(MovieFragmentBox.class));
        i += 1;
        break;
        label521:
        Iterator localIterator1 = ((MovieExtendsBox)paramVarArgs.next()).getBoxes(TrackExtendsBox.class).iterator();
        label543:
        TrackExtendsBox localTrackExtendsBox;
        Object localObject3;
        long l2;
        Iterator localIterator2;
        for (;;)
        {
          if (!localIterator1.hasNext()) {
            break label286;
          }
          localTrackExtendsBox = (TrackExtendsBox)localIterator1.next();
          if (localTrackExtendsBox.getTrackId() != l3) {
            break;
          }
          if (Path.getPaths(((Box)paramTrackBox.getParent()).getParent(), "/moof/traf/subs").size() > 0) {
            this.subSampleInformationBox = new SubSampleInformationBox();
          }
          localObject3 = new LinkedList();
          l2 = 1L;
          localIterator2 = ((List)localObject2).iterator();
          if (localIterator2.hasNext()) {
            break label734;
          }
          paramString = this.syncSamples;
          this.syncSamples = new long[this.syncSamples.length + ((List)localObject3).size()];
          System.arraycopy(paramString, 0, this.syncSamples, 0, paramString.length);
          localObject3 = ((List)localObject3).iterator();
          i = paramString.length;
          while (((Iterator)localObject3).hasNext())
          {
            paramString = (Long)((Iterator)localObject3).next();
            this.syncSamples[i] = paramString.longValue();
            i += 1;
          }
        }
        label734:
        Iterator localIterator3 = ((MovieFragmentBox)localIterator2.next()).getBoxes(TrackFragmentBox.class).iterator();
        long l1 = l2;
        do
        {
          l2 = l1;
          if (!localIterator3.hasNext()) {
            break;
          }
          paramString = (TrackFragmentBox)localIterator3.next();
        } while (paramString.getTrackFragmentHeaderBox().getTrackId() != l3);
        Object localObject4 = (SubSampleInformationBox)Path.getPath(paramString, "subs");
        Object localObject5;
        Object localObject6;
        if (localObject4 != null)
        {
          l2 = l1 - 0 - 1L;
          localObject4 = ((SubSampleInformationBox)localObject4).getEntries().iterator();
          if (((Iterator)localObject4).hasNext()) {}
        }
        else
        {
          localObject4 = paramString.getBoxes(TrackRunBox.class).iterator();
          l2 = l1;
          Iterator localIterator4;
          label922:
          do
          {
            l1 = l2;
            if (!((Iterator)localObject4).hasNext()) {
              break;
            }
            localObject5 = (TrackRunBox)((Iterator)localObject4).next();
            localObject6 = ((TrackFragmentBox)((TrackRunBox)localObject5).getParent()).getTrackFragmentHeaderBox();
            i = 1;
            localIterator4 = ((TrackRunBox)localObject5).getEntries().iterator();
            l1 = l2;
            l2 = l1;
          } while (!localIterator4.hasNext());
          paramString = (TrackRunBox.Entry)localIterator4.next();
          if (!((TrackRunBox)localObject5).isSampleDurationPresent()) {
            break label1271;
          }
          if ((((List)localObject1).size() != 0) && (((TimeToSampleBox.Entry)((List)localObject1).get(((List)localObject1).size() - 1)).getDelta() == paramString.getSampleDuration())) {
            break label1235;
          }
          ((List)localObject1).add(new TimeToSampleBox.Entry(1L, paramString.getSampleDuration()));
          label1015:
          if (((TrackRunBox)localObject5).isSampleCompositionTimeOffsetPresent())
          {
            if ((this.compositionTimeEntries.size() != 0) && (((CompositionTimeToSample.Entry)this.compositionTimeEntries.get(this.compositionTimeEntries.size() - 1)).getOffset() == paramString.getSampleCompositionTimeOffset())) {
              break label1327;
            }
            this.compositionTimeEntries.add(new CompositionTimeToSample.Entry(1, CastUtils.l2i(paramString.getSampleCompositionTimeOffset())));
          }
          label1095:
          if (!((TrackRunBox)localObject5).isSampleFlagsPresent()) {
            break label1367;
          }
          paramString = paramString.getSampleFlags();
        }
        for (;;)
        {
          if ((paramString != null) && (!paramString.isSampleIsDifferenceSample())) {
            ((List)localObject3).add(Long.valueOf(l1));
          }
          l1 += 1L;
          i = 0;
          break label922;
          localObject5 = (SubSampleInformationBox.SubSampleEntry)((Iterator)localObject4).next();
          localObject6 = new SubSampleInformationBox.SubSampleEntry();
          ((SubSampleInformationBox.SubSampleEntry)localObject6).getSubsampleEntries().addAll(((SubSampleInformationBox.SubSampleEntry)localObject5).getSubsampleEntries());
          if (l2 != 0L)
          {
            ((SubSampleInformationBox.SubSampleEntry)localObject6).setSampleDelta(((SubSampleInformationBox.SubSampleEntry)localObject5).getSampleDelta() + l2);
            l2 = 0L;
          }
          for (;;)
          {
            this.subSampleInformationBox.getEntries().add(localObject6);
            break;
            ((SubSampleInformationBox.SubSampleEntry)localObject6).setSampleDelta(((SubSampleInformationBox.SubSampleEntry)localObject5).getSampleDelta());
          }
          label1235:
          Object localObject7 = (TimeToSampleBox.Entry)((List)localObject1).get(((List)localObject1).size() - 1);
          ((TimeToSampleBox.Entry)localObject7).setCount(((TimeToSampleBox.Entry)localObject7).getCount() + 1L);
          break label1015;
          label1271:
          if (((TrackFragmentHeaderBox)localObject6).hasDefaultSampleDuration())
          {
            ((List)localObject1).add(new TimeToSampleBox.Entry(1L, ((TrackFragmentHeaderBox)localObject6).getDefaultSampleDuration()));
            break label1015;
          }
          ((List)localObject1).add(new TimeToSampleBox.Entry(1L, localTrackExtendsBox.getDefaultSampleDuration()));
          break label1015;
          label1327:
          localObject7 = (CompositionTimeToSample.Entry)this.compositionTimeEntries.get(this.compositionTimeEntries.size() - 1);
          ((CompositionTimeToSample.Entry)localObject7).setCount(((CompositionTimeToSample.Entry)localObject7).getCount() + 1);
          break label1095;
          label1367:
          if ((i != 0) && (((TrackRunBox)localObject5).isFirstSampleFlagsPresent())) {
            paramString = ((TrackRunBox)localObject5).getFirstSampleFlags();
          } else if (((TrackFragmentHeaderBox)localObject6).hasDefaultSampleFlags()) {
            paramString = ((TrackFragmentHeaderBox)localObject6).getDefaultSampleFlags();
          } else {
            paramString = localTrackExtendsBox.getDefaultSampleFlags();
          }
        }
        label1415:
        paramVarArgs = ((MovieFragmentBox)paramString.next()).getBoxes(TrackFragmentBox.class).iterator();
        while (paramVarArgs.hasNext())
        {
          localObject2 = (TrackFragmentBox)paramVarArgs.next();
          if (((TrackFragmentBox)localObject2).getTrackFragmentHeaderBox().getTrackId() == l3) {
            this.sampleGroups = getSampleGroups(Path.getPaths((Container)localObject2, "sgpd"), Path.getPaths((Container)localObject2, "sbgp"), this.sampleGroups);
          }
        }
        break label319;
        label1501:
        this.sampleGroups = getSampleGroups(paramString.getBoxes(SampleGroupDescriptionBox.class), paramString.getBoxes(SampleToGroupBox.class), this.sampleGroups);
        break label328;
      }
      localObject1 = (EditListBox.Entry)paramVarArgs.next();
      this.edits.add(new Edit(((EditListBox.Entry)localObject1).getMediaTime(), paramString.getTimescale(), ((EditListBox.Entry)localObject1).getMediaRate(), ((EditListBox.Entry)localObject1).getSegmentDuration() / paramTrackBox.getTimescale()));
    }
  }
  
  private Map<GroupEntry, long[]> getSampleGroups(List<SampleGroupDescriptionBox> paramList, List<SampleToGroupBox> paramList1, Map<GroupEntry, long[]> paramMap)
  {
    Iterator localIterator1 = paramList.iterator();
    if (!localIterator1.hasNext()) {
      return paramMap;
    }
    SampleGroupDescriptionBox localSampleGroupDescriptionBox = (SampleGroupDescriptionBox)localIterator1.next();
    int j = 0;
    Iterator localIterator2 = paramList1.iterator();
    int i;
    Iterator localIterator3;
    do
    {
      do
      {
        if (!localIterator2.hasNext())
        {
          if (j != 0) {
            break;
          }
          throw new RuntimeException("Could not find SampleToGroupBox for " + ((GroupEntry)localSampleGroupDescriptionBox.getGroupEntries().get(0)).getType() + ".");
        }
        paramList = (SampleToGroupBox)localIterator2.next();
      } while (!paramList.getGroupingType().equals(((GroupEntry)localSampleGroupDescriptionBox.getGroupEntries().get(0)).getType()));
      int k = 1;
      i = 0;
      localIterator3 = paramList.getEntries().iterator();
      j = k;
    } while (!localIterator3.hasNext());
    SampleToGroupBox.Entry localEntry = (SampleToGroupBox.Entry)localIterator3.next();
    GroupEntry localGroupEntry;
    long[] arrayOfLong;
    if (localEntry.getGroupDescriptionIndex() > 0)
    {
      localGroupEntry = (GroupEntry)localSampleGroupDescriptionBox.getGroupEntries().get(localEntry.getGroupDescriptionIndex() - 1);
      arrayOfLong = (long[])paramMap.get(localGroupEntry);
      paramList = arrayOfLong;
      if (arrayOfLong == null) {
        paramList = new long[0];
      }
      arrayOfLong = new long[CastUtils.l2i(localEntry.getSampleCount()) + paramList.length];
      System.arraycopy(paramList, 0, arrayOfLong, 0, paramList.length);
      j = 0;
    }
    for (;;)
    {
      if (j >= localEntry.getSampleCount())
      {
        paramMap.put(localGroupEntry, arrayOfLong);
        i = (int)(i + localEntry.getSampleCount());
        break;
      }
      arrayOfLong[(paramList.length + j)] = (i + j);
      j += 1;
    }
  }
  
  public void close()
    throws IOException
  {
    Object localObject = this.trackBox.getParent();
    if ((localObject instanceof BasicContainer)) {
      ((BasicContainer)localObject).close();
    }
    localObject = this.fragments;
    int j = localObject.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      localObject[i].close();
      i += 1;
    }
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return this.compositionTimeEntries;
  }
  
  public String getHandler()
  {
    return this.handler;
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return this.sampleDependencies;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.sampleDescriptionBox;
  }
  
  public long[] getSampleDurations()
  {
    try
    {
      long[] arrayOfLong = this.decodingTimes;
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
    return this.samples;
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return this.subSampleInformationBox;
  }
  
  public long[] getSyncSamples()
  {
    if (this.syncSamples.length == this.samples.size()) {
      return null;
    }
    return this.syncSamples;
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.trackMetaData;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/Mp4TrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */