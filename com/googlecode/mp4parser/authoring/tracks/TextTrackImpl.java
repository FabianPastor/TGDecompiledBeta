package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.TextSampleEntry;
import com.coremedia.iso.boxes.sampleentry.TextSampleEntry.BoxRecord;
import com.coremedia.iso.boxes.sampleentry.TextSampleEntry.StyleRecord;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.threegpp26245.FontTableBox;
import com.googlecode.mp4parser.boxes.threegpp26245.FontTableBox.FontRecord;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TextTrackImpl
  extends AbstractTrack
{
  SampleDescriptionBox sampleDescriptionBox = new SampleDescriptionBox();
  List<Line> subs = new LinkedList();
  TrackMetaData trackMetaData = new TrackMetaData();
  
  public TextTrackImpl()
  {
    super("subtiles");
    TextSampleEntry localTextSampleEntry = new TextSampleEntry("tx3g");
    localTextSampleEntry.setDataReferenceIndex(1);
    localTextSampleEntry.setStyleRecord(new TextSampleEntry.StyleRecord());
    localTextSampleEntry.setBoxRecord(new TextSampleEntry.BoxRecord());
    this.sampleDescriptionBox.addBox(localTextSampleEntry);
    FontTableBox localFontTableBox = new FontTableBox();
    localFontTableBox.setEntries(Collections.singletonList(new FontTableBox.FontRecord(1, "Serif")));
    localTextSampleEntry.addBox(localFontTableBox);
    this.trackMetaData.setCreationTime(new Date());
    this.trackMetaData.setModificationTime(new Date());
    this.trackMetaData.setTimescale(1000L);
  }
  
  public void close()
    throws IOException
  {}
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return null;
  }
  
  public String getHandler()
  {
    return "sbtl";
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return null;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.sampleDescriptionBox;
  }
  
  public long[] getSampleDurations()
  {
    Object localObject1 = new ArrayList();
    long l = 0L;
    Object localObject2 = this.subs.iterator();
    int i;
    if (!((Iterator)localObject2).hasNext())
    {
      localObject2 = new long[((List)localObject1).size()];
      i = 0;
      localObject1 = ((List)localObject1).iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject1).hasNext())
      {
        return (long[])localObject2;
        Line localLine = (Line)((Iterator)localObject2).next();
        l = localLine.from - l;
        if (l > 0L) {
          ((List)localObject1).add(Long.valueOf(l));
        }
        while (l >= 0L)
        {
          ((List)localObject1).add(Long.valueOf(localLine.to - localLine.from));
          l = localLine.to;
          break;
        }
        throw new Error("Subtitle display times may not intersect");
      }
      localObject2[i] = ((Long)((Iterator)localObject1).next()).longValue();
      i += 1;
    }
  }
  
  public List<Sample> getSamples()
  {
    LinkedList localLinkedList = new LinkedList();
    long l = 0L;
    Iterator localIterator = this.subs.iterator();
    if (!localIterator.hasNext()) {
      return localLinkedList;
    }
    Line localLine = (Line)localIterator.next();
    l = localLine.from - l;
    if (l > 0L) {
      localLinkedList.add(new SampleImpl(ByteBuffer.wrap(new byte[2])));
    }
    do
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
      try
      {
        localDataOutputStream.writeShort(localLine.text.getBytes("UTF-8").length);
        localDataOutputStream.write(localLine.text.getBytes("UTF-8"));
        localDataOutputStream.close();
        localLinkedList.add(new SampleImpl(ByteBuffer.wrap(localByteArrayOutputStream.toByteArray())));
        l = localLine.to;
      }
      catch (IOException localIOException)
      {
        throw new Error("VM is broken. Does not support UTF-8");
      }
    } while (l >= 0L);
    throw new Error("Subtitle display times may not intersect");
  }
  
  public List<Line> getSubs()
  {
    return this.subs;
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return null;
  }
  
  public long[] getSyncSamples()
  {
    return null;
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.trackMetaData;
  }
  
  public static class Line
  {
    long from;
    String text;
    long to;
    
    public Line(long paramLong1, long paramLong2, String paramString)
    {
      this.from = paramLong1;
      this.to = paramLong2;
      this.text = paramString;
    }
    
    public long getFrom()
    {
      return this.from;
    }
    
    public String getText()
    {
      return this.text;
    }
    
    public long getTo()
    {
      return this.to;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/TextTrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */