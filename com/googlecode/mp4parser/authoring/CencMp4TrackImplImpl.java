package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ChunkOffsetBox;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack;
import com.googlecode.mp4parser.util.Path;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat.Pair;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CencMp4TrackImplImpl
  extends Mp4TrackImpl
  implements CencEncryptedTrack
{
  private UUID defaultKeyId;
  private List<CencSampleAuxiliaryDataFormat> sampleEncryptionEntries;
  
  static
  {
    if (!CencMp4TrackImplImpl.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public CencMp4TrackImplImpl(String paramString, TrackBox paramTrackBox, IsoFile... paramVarArgs)
    throws IOException
  {
    super(paramString, paramTrackBox, paramVarArgs);
    paramString = (SchemeTypeBox)Path.getPath(paramTrackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schm[0]");
    assert ((paramString != null) && ((paramString.getSchemeType().equals("cenc")) || (paramString.getSchemeType().equals("cbc1")))) : "Track must be CENC (cenc or cbc1) encrypted";
    this.sampleEncryptionEntries = new ArrayList();
    long l3 = paramTrackBox.getTrackHeaderBox().getTrackId();
    Object localObject1;
    if (paramTrackBox.getParent().getBoxes(MovieExtendsBox.class).size() > 0)
    {
      localObject1 = ((Box)paramTrackBox.getParent()).getParent().getBoxes(MovieFragmentBox.class).iterator();
      break label161;
      label126:
      if (((Iterator)localObject1).hasNext()) {}
    }
    Object localObject2;
    label161:
    Object localObject3;
    long l1;
    int i;
    label380:
    long l2;
    int k;
    for (;;)
    {
      return;
      paramVarArgs = (MovieFragmentBox)((Iterator)localObject1).next();
      localObject2 = paramVarArgs.getBoxes(TrackFragmentBox.class).iterator();
      if (!((Iterator)localObject2).hasNext()) {
        break label126;
      }
      Object localObject4 = (TrackFragmentBox)((Iterator)localObject2).next();
      if (((TrackFragmentBox)localObject4).getTrackFragmentHeaderBox().getTrackId() != l3) {
        break;
      }
      localObject3 = (TrackEncryptionBox)Path.getPath(paramTrackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/tenc[0]");
      this.defaultKeyId = ((TrackEncryptionBox)localObject3).getDefault_KID();
      if (((TrackFragmentBox)localObject4).getTrackFragmentHeaderBox().hasBaseDataOffset()) {
        paramString = ((Box)paramTrackBox.getParent()).getParent();
      }
      Object localObject5;
      for (l1 = ((TrackFragmentBox)localObject4).getTrackFragmentHeaderBox().getBaseDataOffset();; l1 = 0L)
      {
        localObject5 = new FindSaioSaizPair((Container)localObject4).invoke();
        localObject6 = ((FindSaioSaizPair)localObject5).getSaio();
        localObject5 = ((FindSaioSaizPair)localObject5).getSaiz();
        if (($assertionsDisabled) || (localObject6 != null)) {
          break;
        }
        throw new AssertionError();
        paramString = paramVarArgs;
      }
      Object localObject6 = ((SampleAuxiliaryInformationOffsetsBox)localObject6).getOffsets();
      assert (localObject6.length == ((TrackFragmentBox)localObject4).getBoxes(TrackRunBox.class).size());
      assert (localObject5 != null);
      localObject4 = ((TrackFragmentBox)localObject4).getBoxes(TrackRunBox.class);
      i = 0;
      j = 0;
      int m;
      label424:
      ByteBuffer localByteBuffer;
      if (j < localObject6.length)
      {
        m = ((TrackRunBox)((List)localObject4).get(j)).getEntries().size();
        long l4 = localObject6[j];
        l2 = 0L;
        k = i;
        if (k < i + m) {
          break label479;
        }
        localByteBuffer = paramString.getByteBuffer(l1 + l4, l2);
        k = i;
      }
      for (;;)
      {
        if (k >= i + m)
        {
          i += m;
          j += 1;
          break label380;
          break;
          label479:
          l2 += ((SampleAuxiliaryInformationSizesBox)localObject5).getSize(k);
          k += 1;
          break label424;
        }
        int n = ((SampleAuxiliaryInformationSizesBox)localObject5).getSize(k);
        this.sampleEncryptionEntries.add(parseCencAuxDataFormat(((TrackEncryptionBox)localObject3).getDefaultIvSize(), localByteBuffer, n));
        k += 1;
      }
      localObject1 = (TrackEncryptionBox)Path.getPath(paramTrackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/tenc[0]");
      this.defaultKeyId = ((TrackEncryptionBox)localObject1).getDefault_KID();
      paramVarArgs = (ChunkOffsetBox)Path.getPath(paramTrackBox, "mdia[0]/minf[0]/stbl[0]/stco[0]");
      paramString = paramVarArgs;
      if (paramVarArgs == null) {
        paramString = (ChunkOffsetBox)Path.getPath(paramTrackBox, "mdia[0]/minf[0]/stbl[0]/co64[0]");
      }
      paramString = paramTrackBox.getSampleTableBox().getSampleToChunkBox().blowup(paramString.getChunkOffsets().length);
      localObject2 = new FindSaioSaizPair((Container)Path.getPath(paramTrackBox, "mdia[0]/minf[0]/stbl[0]")).invoke();
      paramVarArgs = ((FindSaioSaizPair)localObject2).saio;
      localObject2 = ((FindSaioSaizPair)localObject2).saiz;
      paramTrackBox = ((MovieBox)paramTrackBox.getParent()).getParent();
      if (paramVarArgs.getOffsets().length != 1) {
        break label795;
      }
      l1 = paramVarArgs.getOffsets()[0];
      i = 0;
      if (((SampleAuxiliaryInformationSizesBox)localObject2).getDefaultSampleInfoSize() <= 0) {
        break label756;
      }
      k = 0 + ((SampleAuxiliaryInformationSizesBox)localObject2).getSampleCount() * ((SampleAuxiliaryInformationSizesBox)localObject2).getDefaultSampleInfoSize();
      paramString = paramTrackBox.getByteBuffer(l1, k);
      i = 0;
      while (i < ((SampleAuxiliaryInformationSizesBox)localObject2).getSampleCount())
      {
        this.sampleEncryptionEntries.add(parseCencAuxDataFormat(((TrackEncryptionBox)localObject1).getDefaultIvSize(), paramString, ((SampleAuxiliaryInformationSizesBox)localObject2).getSize(i)));
        i += 1;
      }
    }
    label756:
    int j = 0;
    for (;;)
    {
      k = i;
      if (j >= ((SampleAuxiliaryInformationSizesBox)localObject2).getSampleCount()) {
        break;
      }
      i += localObject2.getSampleInfoSizes()[j];
      j += 1;
    }
    label795:
    if (paramVarArgs.getOffsets().length == paramString.length)
    {
      j = 0;
      i = 0;
      label811:
      if (i < paramString.length)
      {
        l3 = paramVarArgs.getOffsets()[i];
        l1 = 0L;
        if (((SampleAuxiliaryInformationSizesBox)localObject2).getDefaultSampleInfoSize() <= 0) {
          break label899;
        }
        l2 = 0L + ((SampleAuxiliaryInformationSizesBox)localObject2).getSampleCount() * paramString[i];
        localObject3 = paramTrackBox.getByteBuffer(l3, l2);
        k = 0;
      }
      for (;;)
      {
        if (k >= paramString[i])
        {
          j = (int)(j + paramString[i]);
          i += 1;
          break label811;
          break;
          label899:
          k = 0;
          for (;;)
          {
            l2 = l1;
            if (k >= paramString[i]) {
              break;
            }
            l1 += ((SampleAuxiliaryInformationSizesBox)localObject2).getSize(j + k);
            k += 1;
          }
        }
        l1 = ((SampleAuxiliaryInformationSizesBox)localObject2).getSize(j + k);
        this.sampleEncryptionEntries.add(parseCencAuxDataFormat(((TrackEncryptionBox)localObject1).getDefaultIvSize(), (ByteBuffer)localObject3, l1));
        k += 1;
      }
    }
    throw new RuntimeException("Number of saio offsets must be either 1 or number of chunks");
  }
  
  private CencSampleAuxiliaryDataFormat parseCencAuxDataFormat(int paramInt, ByteBuffer paramByteBuffer, long paramLong)
  {
    CencSampleAuxiliaryDataFormat localCencSampleAuxiliaryDataFormat = new CencSampleAuxiliaryDataFormat();
    if (paramLong > 0L)
    {
      localCencSampleAuxiliaryDataFormat.iv = new byte[paramInt];
      paramByteBuffer.get(localCencSampleAuxiliaryDataFormat.iv);
      if (paramLong > paramInt)
      {
        localCencSampleAuxiliaryDataFormat.pairs = new CencSampleAuxiliaryDataFormat.Pair[IsoTypeReader.readUInt16(paramByteBuffer)];
        paramInt = 0;
      }
    }
    for (;;)
    {
      if (paramInt >= localCencSampleAuxiliaryDataFormat.pairs.length) {
        return localCencSampleAuxiliaryDataFormat;
      }
      localCencSampleAuxiliaryDataFormat.pairs[paramInt] = localCencSampleAuxiliaryDataFormat.createPair(IsoTypeReader.readUInt16(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer));
      paramInt += 1;
    }
  }
  
  public UUID getDefaultKeyId()
  {
    return this.defaultKeyId;
  }
  
  public String getName()
  {
    return "enc(" + super.getName() + ")";
  }
  
  public List<CencSampleAuxiliaryDataFormat> getSampleEncryptionEntries()
  {
    return this.sampleEncryptionEntries;
  }
  
  public boolean hasSubSampleEncryption()
  {
    return false;
  }
  
  public String toString()
  {
    return "CencMp4TrackImpl{handler='" + getHandler() + '\'' + '}';
  }
  
  private class FindSaioSaizPair
  {
    private Container container;
    private SampleAuxiliaryInformationOffsetsBox saio;
    private SampleAuxiliaryInformationSizesBox saiz;
    
    static
    {
      if (!CencMp4TrackImplImpl.class.desiredAssertionStatus()) {}
      for (boolean bool = true;; bool = false)
      {
        $assertionsDisabled = bool;
        return;
      }
    }
    
    public FindSaioSaizPair(Container paramContainer)
    {
      this.container = paramContainer;
    }
    
    public SampleAuxiliaryInformationOffsetsBox getSaio()
    {
      return this.saio;
    }
    
    public SampleAuxiliaryInformationSizesBox getSaiz()
    {
      return this.saiz;
    }
    
    public FindSaioSaizPair invoke()
    {
      List localList1 = this.container.getBoxes(SampleAuxiliaryInformationSizesBox.class);
      List localList2 = this.container.getBoxes(SampleAuxiliaryInformationOffsetsBox.class);
      assert (localList1.size() == localList2.size());
      this.saiz = null;
      this.saio = null;
      int i = 0;
      if (i >= localList1.size()) {
        return this;
      }
      if (((this.saiz == null) && (((SampleAuxiliaryInformationSizesBox)localList1.get(i)).getAuxInfoType() == null)) || ("cenc".equals(((SampleAuxiliaryInformationSizesBox)localList1.get(i)).getAuxInfoType())))
      {
        this.saiz = ((SampleAuxiliaryInformationSizesBox)localList1.get(i));
        label135:
        if (((this.saio != null) || (((SampleAuxiliaryInformationOffsetsBox)localList2.get(i)).getAuxInfoType() != null)) && (!"cenc".equals(((SampleAuxiliaryInformationOffsetsBox)localList2.get(i)).getAuxInfoType()))) {
          break label265;
        }
      }
      for (this.saio = ((SampleAuxiliaryInformationOffsetsBox)localList2.get(i));; this.saio = ((SampleAuxiliaryInformationOffsetsBox)localList2.get(i)))
      {
        i += 1;
        break;
        if ((this.saiz != null) && (this.saiz.getAuxInfoType() == null) && ("cenc".equals(((SampleAuxiliaryInformationSizesBox)localList1.get(i)).getAuxInfoType())))
        {
          this.saiz = ((SampleAuxiliaryInformationSizesBox)localList1.get(i));
          break label135;
        }
        throw new RuntimeException("Are there two cenc labeled saiz?");
        label265:
        if ((this.saio == null) || (this.saio.getAuxInfoType() != null) || (!"cenc".equals(((SampleAuxiliaryInformationOffsetsBox)localList2.get(i)).getAuxInfoType()))) {
          break label320;
        }
      }
      label320:
      throw new RuntimeException("Are there two cenc labeled saio?");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/CencMp4TrackImplImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */