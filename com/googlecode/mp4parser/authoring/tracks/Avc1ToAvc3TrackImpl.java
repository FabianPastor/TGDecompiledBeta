package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriterVariable;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.WrappingTrack;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Avc1ToAvc3TrackImpl
  extends WrappingTrack
{
  AvcConfigurationBox avcC;
  List<Sample> samples;
  SampleDescriptionBox stsd;
  
  public Avc1ToAvc3TrackImpl(Track paramTrack)
    throws IOException
  {
    super(paramTrack);
    if (!"avc1".equals(paramTrack.getSampleDescriptionBox().getSampleEntry().getType())) {
      throw new RuntimeException("Only avc1 tracks can be converted to avc3 tracks");
    }
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    paramTrack.getSampleDescriptionBox().getBox(Channels.newChannel(localByteArrayOutputStream));
    this.stsd = ((SampleDescriptionBox)Path.getPath(new IsoFile(new MemoryDataSourceImpl(localByteArrayOutputStream.toByteArray())), "stsd"));
    ((VisualSampleEntry)this.stsd.getSampleEntry()).setType("avc3");
    this.avcC = ((AvcConfigurationBox)Path.getPath(this.stsd, "avc./avcC"));
    this.samples = new ReplaceSyncSamplesList(paramTrack.getSamples());
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.stsd;
  }
  
  public List<Sample> getSamples()
  {
    return this.samples;
  }
  
  private class ReplaceSyncSamplesList
    extends AbstractList<Sample>
  {
    List<Sample> parentSamples;
    
    public ReplaceSyncSamplesList()
    {
      List localList;
      this.parentSamples = localList;
    }
    
    public Sample get(int paramInt)
    {
      if (Arrays.binarySearch(Avc1ToAvc3TrackImpl.this.getSyncSamples(), paramInt + 1) >= 0)
      {
        final int i = Avc1ToAvc3TrackImpl.this.avcC.getLengthSizeMinusOne() + 1;
        new Sample()
        {
          public ByteBuffer asByteBuffer()
          {
            int i = 0;
            Object localObject1 = Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets().iterator();
            label49:
            label77:
            Object localObject2;
            if (!((Iterator)localObject1).hasNext())
            {
              localObject1 = Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts().iterator();
              if (((Iterator)localObject1).hasNext()) {
                break label233;
              }
              localObject1 = Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets().iterator();
              if (((Iterator)localObject1).hasNext()) {
                break label256;
              }
              localObject1 = ByteBuffer.allocate(CastUtils.l2i(this.val$orignalSample.getSize()) + i);
              localObject2 = Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets().iterator();
              label123:
              if (((Iterator)localObject2).hasNext()) {
                break label279;
              }
              localObject2 = Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts().iterator();
              label151:
              if (((Iterator)localObject2).hasNext()) {
                break label312;
              }
              localObject2 = Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets().iterator();
            }
            for (;;)
            {
              if (!((Iterator)localObject2).hasNext())
              {
                ((ByteBuffer)localObject1).put(this.val$orignalSample.asByteBuffer());
                return (ByteBuffer)((ByteBuffer)localObject1).rewind();
                localObject2 = (byte[])((Iterator)localObject1).next();
                i += i + localObject2.length;
                break;
                label233:
                localObject2 = (byte[])((Iterator)localObject1).next();
                i += i + localObject2.length;
                break label49;
                label256:
                localObject2 = (byte[])((Iterator)localObject1).next();
                i += i + localObject2.length;
                break label77;
                label279:
                arrayOfByte = (byte[])((Iterator)localObject2).next();
                IsoTypeWriterVariable.write(arrayOfByte.length, (ByteBuffer)localObject1, i);
                ((ByteBuffer)localObject1).put(arrayOfByte);
                break label123;
                label312:
                arrayOfByte = (byte[])((Iterator)localObject2).next();
                IsoTypeWriterVariable.write(arrayOfByte.length, (ByteBuffer)localObject1, i);
                ((ByteBuffer)localObject1).put(arrayOfByte);
                break label151;
              }
              byte[] arrayOfByte = (byte[])((Iterator)localObject2).next();
              IsoTypeWriterVariable.write(arrayOfByte.length, (ByteBuffer)localObject1, i);
              ((ByteBuffer)localObject1).put(arrayOfByte);
            }
          }
          
          public long getSize()
          {
            int i = 0;
            Iterator localIterator = Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets().iterator();
            if (!localIterator.hasNext())
            {
              localIterator = Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts().iterator();
              label49:
              if (localIterator.hasNext()) {
                break label122;
              }
              localIterator = Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets().iterator();
            }
            for (;;)
            {
              if (!localIterator.hasNext())
              {
                return this.val$orignalSample.getSize() + i;
                arrayOfByte = (byte[])localIterator.next();
                i += i + arrayOfByte.length;
                break;
                label122:
                arrayOfByte = (byte[])localIterator.next();
                i += i + arrayOfByte.length;
                break label49;
              }
              byte[] arrayOfByte = (byte[])localIterator.next();
              i += i + arrayOfByte.length;
            }
          }
          
          public void writeTo(WritableByteChannel paramAnonymousWritableByteChannel)
            throws IOException
          {
            Iterator localIterator = Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSets().iterator();
            if (!localIterator.hasNext())
            {
              localIterator = Avc1ToAvc3TrackImpl.this.avcC.getSequenceParameterSetExts().iterator();
              label47:
              if (localIterator.hasNext()) {
                break label156;
              }
              localIterator = Avc1ToAvc3TrackImpl.this.avcC.getPictureParameterSets().iterator();
            }
            for (;;)
            {
              if (!localIterator.hasNext())
              {
                this.val$orignalSample.writeTo(paramAnonymousWritableByteChannel);
                return;
                arrayOfByte = (byte[])localIterator.next();
                IsoTypeWriterVariable.write(arrayOfByte.length, (ByteBuffer)this.val$buf.rewind(), i);
                paramAnonymousWritableByteChannel.write((ByteBuffer)this.val$buf.rewind());
                paramAnonymousWritableByteChannel.write(ByteBuffer.wrap(arrayOfByte));
                break;
                label156:
                arrayOfByte = (byte[])localIterator.next();
                IsoTypeWriterVariable.write(arrayOfByte.length, (ByteBuffer)this.val$buf.rewind(), i);
                paramAnonymousWritableByteChannel.write((ByteBuffer)this.val$buf.rewind());
                paramAnonymousWritableByteChannel.write(ByteBuffer.wrap(arrayOfByte));
                break label47;
              }
              byte[] arrayOfByte = (byte[])localIterator.next();
              IsoTypeWriterVariable.write(arrayOfByte.length, (ByteBuffer)this.val$buf.rewind(), i);
              paramAnonymousWritableByteChannel.write((ByteBuffer)this.val$buf.rewind());
              paramAnonymousWritableByteChannel.write(ByteBuffer.wrap(arrayOfByte));
            }
          }
        };
      }
      return (Sample)this.parentSamples.get(paramInt);
    }
    
    public int size()
    {
      return this.parentSamples.size();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/Avc1ToAvc3TrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */