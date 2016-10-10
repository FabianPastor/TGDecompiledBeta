package com.googlecode.mp4parser.authoring.tracks.h265;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack.LookAhead;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord.Array;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class H265TrackImpl
  extends AbstractH26XTrack
  implements NalUnitTypes
{
  ArrayList<ByteBuffer> pps = new ArrayList();
  ArrayList<Sample> samples = new ArrayList();
  ArrayList<ByteBuffer> sps = new ArrayList();
  SampleDescriptionBox stsd;
  ArrayList<ByteBuffer> vps = new ArrayList();
  
  public H265TrackImpl(DataSource paramDataSource)
    throws IOException
  {
    super(paramDataSource);
    ArrayList localArrayList = new ArrayList();
    paramDataSource = new AbstractH26XTrack.LookAhead(paramDataSource);
    boolean[] arrayOfBoolean1 = new boolean[1];
    boolean[] arrayOfBoolean2 = new boolean[1];
    arrayOfBoolean2[0] = true;
    ByteBuffer localByteBuffer = findNextNal(paramDataSource);
    if (localByteBuffer == null)
    {
      this.stsd = createSampleDescriptionBox();
      this.decodingTimes = new long[this.samples.size()];
      getTrackMetaData().setTimescale(25L);
      Arrays.fill(this.decodingTimes, 1L);
      return;
    }
    NalUnitHeader localNalUnitHeader = getNalUnitHeader(localByteBuffer);
    if (arrayOfBoolean1[0] != 0)
    {
      if (!isVcl(localNalUnitHeader)) {
        break label365;
      }
      if ((localByteBuffer.get(2) & 0xFFFFFF80) != 0) {
        wrapUp(localArrayList, arrayOfBoolean1, arrayOfBoolean2);
      }
    }
    switch (localNalUnitHeader.nalUnitType)
    {
    case 35: 
    case 36: 
    case 37: 
    case 38: 
    default: 
      label228:
      switch (localNalUnitHeader.nalUnitType)
      {
      default: 
        System.err.println("Adding " + localNalUnitHeader.nalUnitType);
        localArrayList.add(localByteBuffer);
      }
      if (isVcl(localNalUnitHeader)) {
        switch (localNalUnitHeader.nalUnitType)
        {
        default: 
          arrayOfBoolean2[0] = false;
        }
      }
      break;
    }
    for (;;)
    {
      arrayOfBoolean1[0] |= isVcl(localNalUnitHeader);
      break;
      label365:
      switch (localNalUnitHeader.nalUnitType)
      {
      case 38: 
      case 40: 
      case 45: 
      case 46: 
      case 47: 
      default: 
        break;
      case 32: 
      case 33: 
      case 34: 
      case 35: 
      case 36: 
      case 37: 
      case 39: 
      case 41: 
      case 42: 
      case 43: 
      case 44: 
      case 48: 
      case 49: 
      case 50: 
      case 51: 
      case 52: 
      case 53: 
      case 54: 
      case 55: 
        wrapUp(localArrayList, arrayOfBoolean1, arrayOfBoolean2);
        break;
        localByteBuffer.position(2);
        this.pps.add(localByteBuffer.slice());
        System.err.println("Stored PPS");
        break label228;
        localByteBuffer.position(2);
        this.vps.add(localByteBuffer.slice());
        System.err.println("Stored VPS");
        break label228;
        localByteBuffer.position(2);
        this.sps.add(localByteBuffer.slice());
        localByteBuffer.position(1);
        new SequenceParameterSetRbsp(Channels.newInputStream(new ByteBufferByteChannel(localByteBuffer.slice())));
        System.err.println("Stored SPS");
        break label228;
        new SEIMessage(new BitReaderBuffer(localByteBuffer.slice()));
        break label228;
        arrayOfBoolean2[0] &= 0x1;
      }
    }
  }
  
  private SampleDescriptionBox createSampleDescriptionBox()
  {
    this.stsd = new SampleDescriptionBox();
    VisualSampleEntry localVisualSampleEntry = new VisualSampleEntry("hvc1");
    localVisualSampleEntry.setDataReferenceIndex(1);
    localVisualSampleEntry.setDepth(24);
    localVisualSampleEntry.setFrameCount(1);
    localVisualSampleEntry.setHorizresolution(72.0D);
    localVisualSampleEntry.setVertresolution(72.0D);
    localVisualSampleEntry.setWidth(640);
    localVisualSampleEntry.setHeight(480);
    localVisualSampleEntry.setCompressorname("HEVC Coding");
    HevcConfigurationBox localHevcConfigurationBox = new HevcConfigurationBox();
    HevcDecoderConfigurationRecord.Array localArray = new HevcDecoderConfigurationRecord.Array();
    localArray.array_completeness = true;
    localArray.nal_unit_type = 33;
    localArray.nalUnits = new ArrayList();
    Object localObject1 = this.sps.iterator();
    Object localObject2;
    label171:
    Object localObject3;
    if (!((Iterator)localObject1).hasNext())
    {
      localObject1 = new HevcDecoderConfigurationRecord.Array();
      ((HevcDecoderConfigurationRecord.Array)localObject1).array_completeness = true;
      ((HevcDecoderConfigurationRecord.Array)localObject1).nal_unit_type = 34;
      ((HevcDecoderConfigurationRecord.Array)localObject1).nalUnits = new ArrayList();
      localObject2 = this.pps.iterator();
      if (((Iterator)localObject2).hasNext()) {
        break label313;
      }
      localObject2 = new HevcDecoderConfigurationRecord.Array();
      ((HevcDecoderConfigurationRecord.Array)localObject2).array_completeness = true;
      ((HevcDecoderConfigurationRecord.Array)localObject2).nal_unit_type = 34;
      ((HevcDecoderConfigurationRecord.Array)localObject2).nalUnits = new ArrayList();
      localObject3 = this.vps.iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject3).hasNext())
      {
        localHevcConfigurationBox.getArrays().addAll(Arrays.asList(new HevcDecoderConfigurationRecord.Array[] { localArray, localObject2, localObject1 }));
        localVisualSampleEntry.addBox(localHevcConfigurationBox);
        this.stsd.addBox(localVisualSampleEntry);
        return this.stsd;
        localObject2 = (ByteBuffer)((Iterator)localObject1).next();
        localArray.nalUnits.add(toArray((ByteBuffer)localObject2));
        break;
        label313:
        localObject3 = (ByteBuffer)((Iterator)localObject2).next();
        ((HevcDecoderConfigurationRecord.Array)localObject1).nalUnits.add(toArray((ByteBuffer)localObject3));
        break label171;
      }
      ByteBuffer localByteBuffer = (ByteBuffer)((Iterator)localObject3).next();
      ((HevcDecoderConfigurationRecord.Array)localObject2).nalUnits.add(toArray(localByteBuffer));
    }
  }
  
  public static void main(String[] paramArrayOfString)
    throws IOException
  {
    paramArrayOfString = new H265TrackImpl(new FileDataSourceImpl("c:\\content\\test-UHD-HEVC_01_FMV_Med_track1.hvc"));
    Movie localMovie = new Movie();
    localMovie.addTrack(paramArrayOfString);
    new DefaultMp4Builder().build(localMovie).writeContainer(new FileOutputStream("output.mp4").getChannel());
  }
  
  public String getHandler()
  {
    return "vide";
  }
  
  public NalUnitHeader getNalUnitHeader(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.position(0);
    int i = IsoTypeReader.readUInt16(paramByteBuffer);
    paramByteBuffer = new NalUnitHeader();
    paramByteBuffer.forbiddenZeroFlag = ((0x8000 & i) >> 15);
    paramByteBuffer.nalUnitType = ((i & 0x7E00) >> 9);
    paramByteBuffer.nuhLayerId = ((i & 0x1F8) >> 3);
    paramByteBuffer.nuhTemporalIdPlusOne = (i & 0x7);
    return paramByteBuffer;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return null;
  }
  
  public List<Sample> getSamples()
  {
    return this.samples;
  }
  
  boolean isVcl(NalUnitHeader paramNalUnitHeader)
  {
    return (paramNalUnitHeader.nalUnitType >= 0) && (paramNalUnitHeader.nalUnitType <= 31);
  }
  
  public void wrapUp(List<ByteBuffer> paramList, boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
  {
    this.samples.add(createSampleObject(paramList));
    System.err.print("Create AU from " + paramList.size() + " NALs");
    if (paramArrayOfBoolean2[0] != 0) {
      System.err.println("  IDR");
    }
    for (;;)
    {
      paramArrayOfBoolean1[0] = false;
      paramArrayOfBoolean2[0] = true;
      paramList.clear();
      return;
      System.err.println();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/h265/H265TrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */