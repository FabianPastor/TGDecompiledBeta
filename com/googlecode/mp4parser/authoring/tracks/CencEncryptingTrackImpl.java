package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.OriginalFormatBox;
import com.coremedia.iso.boxes.ProtectionSchemeInformationBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SchemeInformationBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.cenc.CencEncryptingSampleList;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;

public class CencEncryptingTrackImpl
  implements CencEncryptedTrack
{
  List<CencSampleAuxiliaryDataFormat> cencSampleAuxiliaryData;
  UUID defaultKeyId;
  boolean dummyIvs = false;
  private final String encryptionAlgo;
  RangeStartMap<Integer, SecretKey> indexToKey;
  Map<UUID, SecretKey> keys = new HashMap();
  Map<GroupEntry, long[]> sampleGroups;
  List<Sample> samples;
  Track source;
  SampleDescriptionBox stsd = null;
  boolean subSampleEncryption = false;
  
  public CencEncryptingTrackImpl(Track paramTrack, UUID paramUUID, Map<UUID, SecretKey> paramMap, Map<CencSampleEncryptionInformationGroupEntry, long[]> paramMap1, String paramString, boolean paramBoolean)
  {
    this(paramTrack, paramUUID, paramMap, paramMap1, paramString, paramBoolean, false);
  }
  
  public CencEncryptingTrackImpl(Track paramTrack, UUID paramUUID, Map<UUID, SecretKey> paramMap, Map<CencSampleEncryptionInformationGroupEntry, long[]> paramMap1, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.source = paramTrack;
    this.keys = paramMap;
    this.defaultKeyId = paramUUID;
    this.dummyIvs = paramBoolean1;
    this.encryptionAlgo = paramString;
    this.sampleGroups = new HashMap();
    paramString = paramTrack.getSampleGroups().entrySet().iterator();
    label115:
    Object localObject;
    ArrayList localArrayList;
    int j;
    int i;
    if (!paramString.hasNext())
    {
      if (paramMap1 != null)
      {
        paramString = paramMap1.entrySet().iterator();
        if (paramString.hasNext()) {
          break label389;
        }
      }
      this.sampleGroups = new HashMap(this.sampleGroups)
      {
        public long[] put(GroupEntry paramAnonymousGroupEntry, long[] paramAnonymousArrayOfLong)
        {
          if ((paramAnonymousGroupEntry instanceof CencSampleEncryptionInformationGroupEntry)) {
            throw new RuntimeException("Please supply CencSampleEncryptionInformationGroupEntries in the constructor");
          }
          return (long[])super.put(paramAnonymousGroupEntry, paramAnonymousArrayOfLong);
        }
      };
      this.samples = paramTrack.getSamples();
      this.cencSampleAuxiliaryData = new ArrayList();
      localObject = new BigInteger("1");
      paramString = new byte[8];
      if (!paramBoolean1) {
        new SecureRandom().nextBytes(paramString);
      }
      paramString = new BigInteger(1, paramString);
      localArrayList = new ArrayList();
      if (paramMap1 != null) {
        localArrayList.addAll(paramMap1.keySet());
      }
      this.indexToKey = new RangeStartMap();
      k = -1;
      j = 0;
      if (j < paramTrack.getSamples().size()) {
        break label434;
      }
      paramTrack = paramTrack.getSampleDescriptionBox().getSampleEntry().getBoxes();
      i = -1;
      paramTrack = paramTrack.iterator();
    }
    for (;;)
    {
      if (!paramTrack.hasNext())
      {
        j = 0;
        paramTrack = paramString;
        if (j < this.samples.size()) {
          break label756;
        }
        System.err.println("");
        return;
        localObject = (Map.Entry)paramString.next();
        if ((((Map.Entry)localObject).getKey() instanceof CencSampleEncryptionInformationGroupEntry)) {
          break;
        }
        this.sampleGroups.put((GroupEntry)((Map.Entry)localObject).getKey(), (long[])((Map.Entry)localObject).getValue());
        break;
        label389:
        localObject = (Map.Entry)paramString.next();
        this.sampleGroups.put((GroupEntry)((Map.Entry)localObject).getKey(), (long[])((Map.Entry)localObject).getValue());
        break label115;
        label434:
        i = 0;
        m = 0;
        label440:
        if (m >= localArrayList.size())
        {
          m = k;
          if (k != i)
          {
            if (i != 0) {
              break label560;
            }
            this.indexToKey.put(Integer.valueOf(j), (SecretKey)paramMap.get(paramUUID));
          }
        }
        for (;;)
        {
          m = i;
          j += 1;
          k = m;
          break;
          paramMap1 = (GroupEntry)localArrayList.get(m);
          if (Arrays.binarySearch((long[])getSampleGroups().get(paramMap1), j) >= 0) {
            i = m + 1;
          }
          m += 1;
          break label440;
          label560:
          if (((CencSampleEncryptionInformationGroupEntry)localArrayList.get(i - 1)).getKid() != null)
          {
            paramMap1 = (SecretKey)paramMap.get(((CencSampleEncryptionInformationGroupEntry)localArrayList.get(i - 1)).getKid());
            if (paramMap1 == null) {
              throw new RuntimeException("Key " + ((CencSampleEncryptionInformationGroupEntry)localArrayList.get(i - 1)).getKid() + " was not supplied for decryption");
            }
            this.indexToKey.put(Integer.valueOf(j), paramMap1);
          }
          else
          {
            this.indexToKey.put(Integer.valueOf(j), null);
          }
        }
      }
      paramUUID = (Box)paramTrack.next();
      if ((paramUUID instanceof AvcConfigurationBox))
      {
        paramMap = (AvcConfigurationBox)paramUUID;
        this.subSampleEncryption = true;
        i = paramMap.getLengthSizeMinusOne() + 1;
      }
      if ((paramUUID instanceof HevcConfigurationBox))
      {
        paramUUID = (HevcConfigurationBox)paramUUID;
        this.subSampleEncryption = true;
        i = paramUUID.getLengthSizeMinusOne() + 1;
      }
    }
    label756:
    paramMap1 = (Sample)this.samples.get(j);
    paramMap = new CencSampleAuxiliaryDataFormat();
    this.cencSampleAuxiliaryData.add(paramMap);
    paramUUID = paramTrack;
    if (this.indexToKey.get(Integer.valueOf(j)) != null)
    {
      paramUUID = paramTrack.toByteArray();
      paramString = new byte[8];
      if (paramUUID.length - 8 <= 0) {
        break label938;
      }
      k = paramUUID.length - 8;
      label834:
      if (8 - paramUUID.length >= 0) {
        break label944;
      }
      m = 0;
      label845:
      if (paramUUID.length <= 8) {
        break label954;
      }
      n = 8;
      label856:
      System.arraycopy(paramUUID, k, paramString, m, n);
      paramMap.iv = paramString;
      paramUUID = (ByteBuffer)paramMap1.asByteBuffer().rewind();
      if (this.subSampleEncryption) {
        if (!paramBoolean2) {
          break label961;
        }
      }
    }
    for (paramMap.pairs = new CencSampleAuxiliaryDataFormat.Pair[] { paramMap.createPair(paramUUID.remaining(), 0L) };; paramMap.pairs = ((CencSampleAuxiliaryDataFormat.Pair[])paramMap1.toArray(new CencSampleAuxiliaryDataFormat.Pair[paramMap1.size()])))
    {
      paramUUID = paramTrack.add((BigInteger)localObject);
      j += 1;
      paramTrack = paramUUID;
      break;
      label938:
      k = 0;
      break label834;
      label944:
      m = 8 - paramUUID.length;
      break label845;
      label954:
      n = paramUUID.length;
      break label856;
      label961:
      paramMap1 = new ArrayList(5);
      if (paramUUID.remaining() > 0) {
        break label1005;
      }
    }
    label1005:
    int n = CastUtils.l2i(IsoTypeReaderVariable.read(paramUUID, i));
    int m = n + i;
    if (m >= 112) {}
    for (int k = m % 16 + 96;; k = m)
    {
      paramMap1.add(paramMap.createPair(k, m - k));
      paramUUID.position(paramUUID.position() + n);
      break;
    }
  }
  
  public CencEncryptingTrackImpl(Track paramTrack, UUID paramUUID, SecretKey paramSecretKey, boolean paramBoolean)
  {
    this(paramTrack, paramUUID, Collections.singletonMap(paramUUID, paramSecretKey), null, "cenc", paramBoolean);
  }
  
  public void close()
    throws IOException
  {
    this.source.close();
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return this.source.getCompositionTimeEntries();
  }
  
  public UUID getDefaultKeyId()
  {
    return this.defaultKeyId;
  }
  
  public long getDuration()
  {
    return this.source.getDuration();
  }
  
  public List<Edit> getEdits()
  {
    return this.source.getEdits();
  }
  
  public String getHandler()
  {
    return this.source.getHandler();
  }
  
  public String getName()
  {
    return "enc(" + this.source.getName() + ")";
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return this.source.getSampleDependencies();
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    for (;;)
    {
      try
      {
        Object localObject1;
        if (this.stsd == null) {
          localObject1 = new ByteArrayOutputStream();
        }
        try
        {
          this.source.getSampleDescriptionBox().getBox(Channels.newChannel((OutputStream)localObject1));
          this.stsd = ((SampleDescriptionBox)new IsoFile(new MemoryDataSourceImpl(((ByteArrayOutputStream)localObject1).toByteArray())).getBoxes().get(0));
          localObject1 = new OriginalFormatBox();
          ((OriginalFormatBox)localObject1).setDataFormat(this.stsd.getSampleEntry().getType());
          if ((this.stsd.getSampleEntry() instanceof AudioSampleEntry))
          {
            ((AudioSampleEntry)this.stsd.getSampleEntry()).setType("enca");
            ProtectionSchemeInformationBox localProtectionSchemeInformationBox = new ProtectionSchemeInformationBox();
            localProtectionSchemeInformationBox.addBox((Box)localObject1);
            localObject1 = new SchemeTypeBox();
            ((SchemeTypeBox)localObject1).setSchemeType(this.encryptionAlgo);
            ((SchemeTypeBox)localObject1).setSchemeVersion(65536);
            localProtectionSchemeInformationBox.addBox((Box)localObject1);
            SchemeInformationBox localSchemeInformationBox = new SchemeInformationBox();
            TrackEncryptionBox localTrackEncryptionBox = new TrackEncryptionBox();
            if (this.defaultKeyId != null) {
              break label354;
            }
            i = 0;
            localTrackEncryptionBox.setDefaultIvSize(i);
            if (this.defaultKeyId != null) {
              break label360;
            }
            i = 0;
            localTrackEncryptionBox.setDefaultAlgorithmId(i);
            if (this.defaultKeyId != null) {
              break label346;
            }
            localObject1 = new UUID(0L, 0L);
            localTrackEncryptionBox.setDefault_KID((UUID)localObject1);
            localSchemeInformationBox.addBox(localTrackEncryptionBox);
            localProtectionSchemeInformationBox.addBox(localSchemeInformationBox);
            this.stsd.getSampleEntry().addBox(localProtectionSchemeInformationBox);
            localObject1 = this.stsd;
            return (SampleDescriptionBox)localObject1;
          }
        }
        catch (IOException localIOException)
        {
          throw new RuntimeException("Dumping stsd to memory failed");
        }
        if (!(this.stsd.getSampleEntry() instanceof VisualSampleEntry)) {
          break label312;
        }
      }
      finally {}
      ((VisualSampleEntry)this.stsd.getSampleEntry()).setType("encv");
      continue;
      label312:
      throw new RuntimeException("I don't know how to cenc " + this.stsd.getSampleEntry().getType());
      label346:
      UUID localUUID = this.defaultKeyId;
      continue;
      label354:
      int i = 8;
      continue;
      label360:
      i = 1;
    }
  }
  
  public long[] getSampleDurations()
  {
    return this.source.getSampleDurations();
  }
  
  public List<CencSampleAuxiliaryDataFormat> getSampleEncryptionEntries()
  {
    return this.cencSampleAuxiliaryData;
  }
  
  public Map<GroupEntry, long[]> getSampleGroups()
  {
    return this.sampleGroups;
  }
  
  public List<Sample> getSamples()
  {
    return new CencEncryptingSampleList(this.indexToKey, this.source.getSamples(), this.cencSampleAuxiliaryData, this.encryptionAlgo);
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return this.source.getSubsampleInformationBox();
  }
  
  public long[] getSyncSamples()
  {
    return this.source.getSyncSamples();
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.source.getTrackMetaData();
  }
  
  public boolean hasSubSampleEncryption()
  {
    return this.subSampleEncryption;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/CencEncryptingTrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */