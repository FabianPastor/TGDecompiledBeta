package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.OriginalFormatBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.cenc.CencDecryptingSampleList;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.util.Path;
import com.googlecode.mp4parser.util.RangeStartMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;

public class CencDecryptingTrackImpl
  extends AbstractTrack
{
  RangeStartMap<Integer, SecretKey> indexToKey = new RangeStartMap();
  Track original;
  CencDecryptingSampleList samples;
  
  public CencDecryptingTrackImpl(CencEncryptedTrack paramCencEncryptedTrack, Map<UUID, SecretKey> paramMap)
  {
    super("dec(" + paramCencEncryptedTrack.getName() + ")");
    this.original = paramCencEncryptedTrack;
    SchemeTypeBox localSchemeTypeBox = (SchemeTypeBox)Path.getPath(paramCencEncryptedTrack.getSampleDescriptionBox(), "enc./sinf/schm");
    if ((!"cenc".equals(localSchemeTypeBox.getSchemeType())) && (!"cbc1".equals(localSchemeTypeBox.getSchemeType()))) {
      throw new RuntimeException("You can only use the CencDecryptingTrackImpl with CENC (cenc or cbc1) encrypted tracks");
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject = paramCencEncryptedTrack.getSampleGroups().entrySet().iterator();
    int k;
    int j;
    for (;;)
    {
      if (!((Iterator)localObject).hasNext())
      {
        k = -1;
        j = 0;
        if (j < paramCencEncryptedTrack.getSamples().size()) {
          break;
        }
        this.samples = new CencDecryptingSampleList(this.indexToKey, paramCencEncryptedTrack.getSamples(), paramCencEncryptedTrack.getSampleEncryptionEntries(), localSchemeTypeBox.getSchemeType());
        return;
      }
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      if ((localEntry.getKey() instanceof CencSampleEncryptionInformationGroupEntry)) {
        localArrayList.add((CencSampleEncryptionInformationGroupEntry)localEntry.getKey());
      } else {
        getSampleGroups().put((GroupEntry)localEntry.getKey(), (long[])localEntry.getValue());
      }
    }
    int i = 0;
    int m = 0;
    label274:
    if (m >= localArrayList.size())
    {
      m = k;
      if (k != i)
      {
        if (i != 0) {
          break label397;
        }
        this.indexToKey.put(Integer.valueOf(j), (SecretKey)paramMap.get(paramCencEncryptedTrack.getDefaultKeyId()));
      }
    }
    for (;;)
    {
      m = i;
      j += 1;
      k = m;
      break;
      localObject = (GroupEntry)localArrayList.get(m);
      if (Arrays.binarySearch((long[])paramCencEncryptedTrack.getSampleGroups().get(localObject), j) >= 0) {
        i = m + 1;
      }
      m += 1;
      break label274;
      label397:
      if (((CencSampleEncryptionInformationGroupEntry)localArrayList.get(i - 1)).isEncrypted())
      {
        localObject = (SecretKey)paramMap.get(((CencSampleEncryptionInformationGroupEntry)localArrayList.get(i - 1)).getKid());
        if (localObject == null) {
          throw new RuntimeException("Key " + ((CencSampleEncryptionInformationGroupEntry)localArrayList.get(i - 1)).getKid() + " was not supplied for decryption");
        }
        this.indexToKey.put(Integer.valueOf(j), localObject);
      }
      else
      {
        this.indexToKey.put(Integer.valueOf(j), null);
      }
    }
  }
  
  public CencDecryptingTrackImpl(CencEncryptedTrack paramCencEncryptedTrack, SecretKey paramSecretKey)
  {
    this(paramCencEncryptedTrack, Collections.singletonMap(paramCencEncryptedTrack.getDefaultKeyId(), paramSecretKey));
  }
  
  public void close()
    throws IOException
  {
    this.original.close();
  }
  
  public String getHandler()
  {
    return this.original.getHandler();
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    Object localObject2 = (OriginalFormatBox)Path.getPath(this.original.getSampleDescriptionBox(), "enc./sinf/frma");
    Object localObject1 = new ByteArrayOutputStream();
    for (;;)
    {
      Iterator localIterator;
      try
      {
        this.original.getSampleDescriptionBox().getBox(Channels.newChannel((OutputStream)localObject1));
        localObject1 = (SampleDescriptionBox)new IsoFile(new MemoryDataSourceImpl(((ByteArrayOutputStream)localObject1).toByteArray())).getBoxes().get(0);
        if ((((SampleDescriptionBox)localObject1).getSampleEntry() instanceof AudioSampleEntry))
        {
          ((AudioSampleEntry)((SampleDescriptionBox)localObject1).getSampleEntry()).setType(((OriginalFormatBox)localObject2).getDataFormat());
          localObject2 = new LinkedList();
          localIterator = ((SampleDescriptionBox)localObject1).getSampleEntry().getBoxes().iterator();
          if (localIterator.hasNext()) {
            break label207;
          }
          ((SampleDescriptionBox)localObject1).getSampleEntry().setBoxes((List)localObject2);
          return (SampleDescriptionBox)localObject1;
        }
      }
      catch (IOException localIOException)
      {
        throw new RuntimeException("Dumping stsd to memory failed");
      }
      if ((localIOException.getSampleEntry() instanceof VisualSampleEntry))
      {
        ((VisualSampleEntry)localIOException.getSampleEntry()).setType(((OriginalFormatBox)localObject2).getDataFormat());
      }
      else
      {
        throw new RuntimeException("I don't know " + localIOException.getSampleEntry().getType());
        label207:
        Box localBox = (Box)localIterator.next();
        if (!localBox.getType().equals("sinf")) {
          ((List)localObject2).add(localBox);
        }
      }
    }
  }
  
  public long[] getSampleDurations()
  {
    return this.original.getSampleDurations();
  }
  
  public List<Sample> getSamples()
  {
    return this.samples;
  }
  
  public long[] getSyncSamples()
  {
    return this.original.getSyncSamples();
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.original.getTrackMetaData();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/CencDecryptingTrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */