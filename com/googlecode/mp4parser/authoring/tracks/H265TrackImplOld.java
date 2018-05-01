package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoTypeReader;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord.Array;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class H265TrackImplOld
{
  public static final int AUD_NUT = 35;
  private static final int BLA_N_LP = 18;
  private static final int BLA_W_LP = 16;
  private static final int BLA_W_RADL = 17;
  private static final long BUFFER = 1048576L;
  private static final int CRA_NUT = 21;
  private static final int IDR_N_LP = 20;
  private static final int IDR_W_RADL = 19;
  public static final int PPS_NUT = 34;
  public static final int PREFIX_SEI_NUT = 39;
  private static final int RADL_N = 6;
  private static final int RADL_R = 7;
  private static final int RASL_N = 8;
  private static final int RASL_R = 9;
  public static final int RSV_NVCL41 = 41;
  public static final int RSV_NVCL42 = 42;
  public static final int RSV_NVCL43 = 43;
  public static final int RSV_NVCL44 = 44;
  public static final int SPS_NUT = 33;
  private static final int STSA_N = 4;
  private static final int STSA_R = 5;
  private static final int TRAIL_N = 0;
  private static final int TRAIL_R = 1;
  private static final int TSA_N = 2;
  private static final int TSA_R = 3;
  public static final int UNSPEC48 = 48;
  public static final int UNSPEC49 = 49;
  public static final int UNSPEC50 = 50;
  public static final int UNSPEC51 = 51;
  public static final int UNSPEC52 = 52;
  public static final int UNSPEC53 = 53;
  public static final int UNSPEC54 = 54;
  public static final int UNSPEC55 = 55;
  public static final int VPS_NUT = 32;
  LinkedHashMap<Long, ByteBuffer> pictureParamterSets = new LinkedHashMap();
  List<Sample> samples = new ArrayList();
  LinkedHashMap<Long, ByteBuffer> sequenceParamterSets = new LinkedHashMap();
  List<Long> syncSamples = new ArrayList();
  LinkedHashMap<Long, ByteBuffer> videoParamterSets = new LinkedHashMap();
  
  public H265TrackImplOld(DataSource paramDataSource)
    throws IOException
  {
    paramDataSource = new LookAhead(paramDataSource);
    long l1 = 1L;
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    ByteBuffer localByteBuffer1 = findNextNal(paramDataSource);
    if (localByteBuffer1 == null)
    {
      System.err.println("");
      paramDataSource = new HevcDecoderConfigurationRecord();
      paramDataSource.setArrays(getArrays());
      paramDataSource.setAvgFrameRate(0);
      return;
    }
    Object localObject = getNalUnitHeader(localByteBuffer1);
    label164:
    int j;
    long l2;
    switch (((NalUnitHeader)localObject).nalUnitType)
    {
    default: 
      j = i;
      if (((NalUnitHeader)localObject).nalUnitType < 32) {
        j = ((NalUnitHeader)localObject).nalUnitType;
      }
      l2 = l1;
      if (isFirstOfAU(((NalUnitHeader)localObject).nalUnitType, localByteBuffer1, localArrayList))
      {
        l2 = l1;
        if (!localArrayList.isEmpty())
        {
          System.err.println("##########################");
          localObject = localArrayList.iterator();
        }
      }
      break;
    }
    for (;;)
    {
      if (!((Iterator)localObject).hasNext())
      {
        System.err.println("                          ##########################");
        this.samples.add(createSample(localArrayList));
        localArrayList.clear();
        l2 = l1 + 1L;
        localArrayList.add(localByteBuffer1);
        i = j;
        l1 = l2;
        if (j < 16) {
          break;
        }
        i = j;
        l1 = l2;
        if (j > 21) {
          break;
        }
        this.syncSamples.add(Long.valueOf(l2));
        i = j;
        l1 = l2;
        break;
        this.videoParamterSets.put(Long.valueOf(l1), localByteBuffer1);
        break label164;
        this.sequenceParamterSets.put(Long.valueOf(l1), localByteBuffer1);
        break label164;
        this.pictureParamterSets.put(Long.valueOf(l1), localByteBuffer1);
        break label164;
      }
      ByteBuffer localByteBuffer2 = (ByteBuffer)((Iterator)localObject).next();
      NalUnitHeader localNalUnitHeader = getNalUnitHeader(localByteBuffer2);
      System.err.println(String.format("type: %3d - layer: %3d - tempId: %3d - size: %3d", new Object[] { Integer.valueOf(localNalUnitHeader.nalUnitType), Integer.valueOf(localNalUnitHeader.nuhLayerId), Integer.valueOf(localNalUnitHeader.nuhTemporalIdPlusOne), Integer.valueOf(localByteBuffer2.limit()) }));
    }
  }
  
  private ByteBuffer findNextNal(LookAhead paramLookAhead)
    throws IOException
  {
    try
    {
      if (paramLookAhead.nextThreeEquals001()) {
        paramLookAhead.discardNext3AndMarkStart();
      }
      for (;;)
      {
        if (paramLookAhead.nextThreeEquals000or001orEof())
        {
          return paramLookAhead.getNal();
          paramLookAhead.discardByte();
          break;
        }
        paramLookAhead.discardByte();
      }
      return null;
    }
    catch (EOFException paramLookAhead) {}
  }
  
  private List<HevcDecoderConfigurationRecord.Array> getArrays()
  {
    HevcDecoderConfigurationRecord.Array localArray = new HevcDecoderConfigurationRecord.Array();
    localArray.array_completeness = true;
    localArray.nal_unit_type = 32;
    localArray.nalUnits = new ArrayList();
    Object localObject1 = this.videoParamterSets.values().iterator();
    Object localObject2;
    label95:
    Object localObject3;
    if (!((Iterator)localObject1).hasNext())
    {
      localObject1 = new HevcDecoderConfigurationRecord.Array();
      ((HevcDecoderConfigurationRecord.Array)localObject1).array_completeness = true;
      ((HevcDecoderConfigurationRecord.Array)localObject1).nal_unit_type = 33;
      ((HevcDecoderConfigurationRecord.Array)localObject1).nalUnits = new ArrayList();
      localObject2 = this.sequenceParamterSets.values().iterator();
      if (((Iterator)localObject2).hasNext()) {
        break label224;
      }
      localObject2 = new HevcDecoderConfigurationRecord.Array();
      ((HevcDecoderConfigurationRecord.Array)localObject2).array_completeness = true;
      ((HevcDecoderConfigurationRecord.Array)localObject2).nal_unit_type = 33;
      ((HevcDecoderConfigurationRecord.Array)localObject2).nalUnits = new ArrayList();
      localObject3 = this.pictureParamterSets.values().iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject3).hasNext())
      {
        return Arrays.asList(new HevcDecoderConfigurationRecord.Array[] { localArray, localObject1, localObject2 });
        localObject2 = (ByteBuffer)((Iterator)localObject1).next();
        localObject3 = new byte[((ByteBuffer)localObject2).limit()];
        ((ByteBuffer)localObject2).position(0);
        ((ByteBuffer)localObject2).get((byte[])localObject3);
        localArray.nalUnits.add(localObject3);
        break;
        label224:
        localObject3 = (ByteBuffer)((Iterator)localObject2).next();
        localObject4 = new byte[((ByteBuffer)localObject3).limit()];
        ((ByteBuffer)localObject3).position(0);
        ((ByteBuffer)localObject3).get((byte[])localObject4);
        ((HevcDecoderConfigurationRecord.Array)localObject1).nalUnits.add(localObject4);
        break label95;
      }
      Object localObject4 = (ByteBuffer)((Iterator)localObject3).next();
      byte[] arrayOfByte = new byte[((ByteBuffer)localObject4).limit()];
      ((ByteBuffer)localObject4).position(0);
      ((ByteBuffer)localObject4).get(arrayOfByte);
      ((HevcDecoderConfigurationRecord.Array)localObject2).nalUnits.add(arrayOfByte);
    }
  }
  
  private void hrd_parameters(boolean paramBoolean, int paramInt, CAVLCReader paramCAVLCReader)
    throws IOException
  {
    boolean bool1 = false;
    int j = 0;
    boolean bool3 = false;
    boolean bool2 = bool3;
    if (paramBoolean)
    {
      paramBoolean = paramCAVLCReader.readBool("nal_hrd_parameters_present_flag");
      boolean bool4 = paramCAVLCReader.readBool("vcl_hrd_parameters_present_flag");
      if (!paramBoolean)
      {
        bool1 = paramBoolean;
        bool2 = bool3;
        j = bool4;
        if (!bool4) {}
      }
      else
      {
        bool2 = paramCAVLCReader.readBool("sub_pic_hrd_params_present_flag");
        if (bool2)
        {
          paramCAVLCReader.readU(8, "tick_divisor_minus2");
          paramCAVLCReader.readU(5, "du_cpb_removal_delay_increment_length_minus1");
          paramCAVLCReader.readBool("sub_pic_cpb_params_in_pic_timing_sei_flag");
          paramCAVLCReader.readU(5, "dpb_output_delay_du_length_minus1");
        }
        paramCAVLCReader.readU(4, "bit_rate_scale");
        paramCAVLCReader.readU(4, "cpb_size_scale");
        if (bool2) {
          paramCAVLCReader.readU(4, "cpb_size_du_scale");
        }
        paramCAVLCReader.readU(5, "initial_cpb_removal_delay_length_minus1");
        paramCAVLCReader.readU(5, "au_cpb_removal_delay_length_minus1");
        paramCAVLCReader.readU(5, "dpb_output_delay_length_minus1");
        j = bool4;
        bool1 = paramBoolean;
      }
    }
    boolean[] arrayOfBoolean1 = new boolean[paramInt];
    boolean[] arrayOfBoolean2 = new boolean[paramInt];
    boolean[] arrayOfBoolean3 = new boolean[paramInt];
    int[] arrayOfInt1 = new int[paramInt];
    int[] arrayOfInt2 = new int[paramInt];
    int i = 0;
    if (i > paramInt) {
      return;
    }
    arrayOfBoolean1[i] = paramCAVLCReader.readBool("fixed_pic_rate_general_flag[" + i + "]");
    if (arrayOfBoolean1[i] == 0) {
      arrayOfBoolean2[i] = paramCAVLCReader.readBool("fixed_pic_rate_within_cvs_flag[" + i + "]");
    }
    if (arrayOfBoolean2[i] != 0) {
      arrayOfInt2[i] = paramCAVLCReader.readUE("elemental_duration_in_tc_minus1[" + i + "]");
    }
    for (;;)
    {
      if (arrayOfBoolean3[i] == 0) {
        arrayOfInt1[i] = paramCAVLCReader.readUE("cpb_cnt_minus1[" + i + "]");
      }
      if (bool1) {
        sub_layer_hrd_parameters(i, arrayOfInt1[i], bool2, paramCAVLCReader);
      }
      if (j != 0) {
        sub_layer_hrd_parameters(i, arrayOfInt1[i], bool2, paramCAVLCReader);
      }
      i += 1;
      break;
      arrayOfBoolean3[i] = paramCAVLCReader.readBool("low_delay_hrd_flag[" + i + "]");
    }
  }
  
  public static void main(String[] paramArrayOfString)
    throws IOException
  {
    new H265TrackImplOld(new FileDataSourceImpl("c:\\content\\test-UHD-HEVC_01_FMV_Med_track1.hvc"));
  }
  
  protected Sample createSample(List<ByteBuffer> paramList)
  {
    byte[] arrayOfByte = new byte[paramList.size() * 4];
    Object localObject = ByteBuffer.wrap(arrayOfByte);
    Iterator localIterator = paramList.iterator();
    int i;
    if (!localIterator.hasNext())
    {
      localObject = new ByteBuffer[paramList.size() * 2];
      i = 0;
    }
    for (;;)
    {
      if (i >= paramList.size())
      {
        return new SampleImpl((ByteBuffer[])localObject);
        ((ByteBuffer)localObject).putInt(((ByteBuffer)localIterator.next()).remaining());
        break;
      }
      localObject[(i * 2)] = ByteBuffer.wrap(arrayOfByte, i * 4, 4);
      localObject[(i * 2 + 1)] = ((ByteBuffer)paramList.get(i));
      i += 1;
    }
  }
  
  public int getFrameRate(ByteBuffer paramByteBuffer)
    throws IOException
  {
    paramByteBuffer = new CAVLCReader(Channels.newInputStream(new ByteBufferByteChannel((ByteBuffer)paramByteBuffer.position(0))));
    paramByteBuffer.readU(4, "vps_parameter_set_id");
    paramByteBuffer.readU(2, "vps_reserved_three_2bits");
    paramByteBuffer.readU(6, "vps_max_layers_minus1");
    int i = paramByteBuffer.readU(3, "vps_max_sub_layers_minus1");
    paramByteBuffer.readBool("vps_temporal_id_nesting_flag");
    paramByteBuffer.readU(16, "vps_reserved_0xffff_16bits");
    profile_tier_level(i, paramByteBuffer);
    boolean bool = paramByteBuffer.readBool("vps_sub_layer_ordering_info_present_flag");
    int j;
    Object localObject1;
    label115:
    Object localObject2;
    label127:
    int[] arrayOfInt;
    label139:
    int m;
    int k;
    if (bool)
    {
      j = 0;
      localObject1 = new int[j];
      if (!bool) {
        break label305;
      }
      j = 0;
      localObject2 = new int[j];
      if (!bool) {
        break label310;
      }
      j = 0;
      arrayOfInt = new int[j];
      if (!bool) {
        break label315;
      }
      j = 0;
      if (j <= i) {
        break label320;
      }
      m = paramByteBuffer.readU(6, "vps_max_layer_id");
      int n = paramByteBuffer.readUE("vps_num_layer_sets_minus1");
      localObject1 = (boolean[][])Array.newInstance(Boolean.TYPE, new int[] { n, m });
      j = 1;
      if (j <= n) {
        break label420;
      }
      if (paramByteBuffer.readBool("vps_timing_info_present_flag"))
      {
        long l = paramByteBuffer.readU(32, "vps_num_units_in_tick");
        l = paramByteBuffer.readU(32, "vps_time_scale");
        if (paramByteBuffer.readBool("vps_poc_proportional_to_timing_flag")) {
          paramByteBuffer.readUE("vps_num_ticks_poc_diff_one_minus1");
        }
        k = paramByteBuffer.readUE("vps_num_hrd_parameters");
        localObject1 = new int[k];
        localObject2 = new boolean[k];
        j = 0;
        if (j < k) {
          break label491;
        }
      }
      if (!paramByteBuffer.readBool("vps_extension_flag")) {}
    }
    for (;;)
    {
      if (!paramByteBuffer.moreRBSPData())
      {
        paramByteBuffer.readTrailingBits();
        return 0;
        j = i;
        break;
        label305:
        j = i;
        break label115;
        label310:
        j = i;
        break label127;
        label315:
        j = i;
        break label139;
        label320:
        localObject1[j] = paramByteBuffer.readUE("vps_max_dec_pic_buffering_minus1[" + j + "]");
        localObject2[j] = paramByteBuffer.readUE("vps_max_dec_pic_buffering_minus1[" + j + "]");
        arrayOfInt[j] = paramByteBuffer.readUE("vps_max_dec_pic_buffering_minus1[" + j + "]");
        j += 1;
        break label139;
        label420:
        k = 0;
        for (;;)
        {
          if (k > m)
          {
            j += 1;
            break;
          }
          localObject1[j][k] = paramByteBuffer.readBool("layer_id_included_flag[" + j + "][" + k + "]");
          k += 1;
        }
        label491:
        localObject1[j] = paramByteBuffer.readUE("hrd_layer_set_idx[" + j + "]");
        if (j > 0) {
          localObject2[j] = paramByteBuffer.readBool("cprms_present_flag[" + j + "]");
        }
        for (;;)
        {
          hrd_parameters(localObject2[j], i, paramByteBuffer);
          j += 1;
          break;
          localObject2[0] = 1;
        }
      }
      paramByteBuffer.readBool("vps_extension_data_flag");
    }
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
  
  boolean isFirstOfAU(int paramInt, ByteBuffer paramByteBuffer, List<ByteBuffer> paramList)
  {
    if (paramList.isEmpty()) {
      return true;
    }
    int i;
    if (getNalUnitHeader((ByteBuffer)paramList.get(paramList.size() - 1)).nalUnitType <= 31)
    {
      i = 1;
      label43:
      switch (paramInt)
      {
      }
    }
    do
    {
      switch (paramInt)
      {
      case 10: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      default: 
        return false;
        i = 0;
        break label43;
      }
    } while (i == 0);
    return true;
    paramList = new byte[50];
    paramByteBuffer.position(0);
    paramByteBuffer.get(paramList);
    paramByteBuffer.position(2);
    paramInt = IsoTypeReader.readUInt8(paramByteBuffer);
    return (i != 0) && ((paramInt & 0x80) > 0);
  }
  
  public void profile_tier_level(int paramInt, CAVLCReader paramCAVLCReader)
    throws IOException
  {
    paramCAVLCReader.readU(2, "general_profile_space ");
    paramCAVLCReader.readBool("general_tier_flag");
    paramCAVLCReader.readU(5, "general_profile_idc");
    boolean[] arrayOfBoolean1 = new boolean[32];
    int i = 0;
    boolean[] arrayOfBoolean2;
    if (i >= 32)
    {
      paramCAVLCReader.readBool("general_progressive_source_flag");
      paramCAVLCReader.readBool("general_interlaced_source_flag");
      paramCAVLCReader.readBool("general_non_packed_constraint_flag");
      paramCAVLCReader.readBool("general_frame_only_constraint_flag");
      long l = paramCAVLCReader.readU(44, "general_reserved_zero_44bits");
      paramCAVLCReader.readU(8, "general_level_idc");
      arrayOfBoolean1 = new boolean[paramInt];
      arrayOfBoolean2 = new boolean[paramInt];
      i = 0;
      label106:
      if (i < paramInt) {
        break label232;
      }
      if (paramInt > 0) {
        i = paramInt;
      }
    }
    int[] arrayOfInt1;
    boolean[] arrayOfBoolean3;
    int[] arrayOfInt2;
    boolean[][] arrayOfBoolean;
    boolean[] arrayOfBoolean4;
    boolean[] arrayOfBoolean5;
    boolean[] arrayOfBoolean6;
    boolean[] arrayOfBoolean7;
    int[] arrayOfInt3;
    for (;;)
    {
      if (i >= 8)
      {
        arrayOfInt1 = new int[paramInt];
        arrayOfBoolean3 = new boolean[paramInt];
        arrayOfInt2 = new int[paramInt];
        arrayOfBoolean = (boolean[][])Array.newInstance(Boolean.TYPE, new int[] { paramInt, 32 });
        arrayOfBoolean4 = new boolean[paramInt];
        arrayOfBoolean5 = new boolean[paramInt];
        arrayOfBoolean6 = new boolean[paramInt];
        arrayOfBoolean7 = new boolean[paramInt];
        arrayOfInt3 = new int[paramInt];
        i = 0;
        if (i < paramInt) {
          break label317;
        }
        return;
        arrayOfBoolean1[i] = paramCAVLCReader.readBool("general_profile_compatibility_flag[" + i + "]");
        i += 1;
        break;
        label232:
        arrayOfBoolean1[i] = paramCAVLCReader.readBool("sub_layer_profile_present_flag[" + i + "]");
        arrayOfBoolean2[i] = paramCAVLCReader.readBool("sub_layer_level_present_flag[" + i + "]");
        i += 1;
        break label106;
      }
      paramCAVLCReader.readU(2, "reserved_zero_2bits");
      i += 1;
    }
    label317:
    int j;
    if (arrayOfBoolean1[i] != 0)
    {
      arrayOfInt1[i] = paramCAVLCReader.readU(2, "sub_layer_profile_space[" + i + "]");
      arrayOfBoolean3[i] = paramCAVLCReader.readBool("sub_layer_tier_flag[" + i + "]");
      arrayOfInt2[i] = paramCAVLCReader.readU(5, "sub_layer_profile_idc[" + i + "]");
      j = 0;
    }
    for (;;)
    {
      if (j >= 32)
      {
        arrayOfBoolean4[i] = paramCAVLCReader.readBool("sub_layer_progressive_source_flag[" + i + "]");
        arrayOfBoolean5[i] = paramCAVLCReader.readBool("sub_layer_interlaced_source_flag[" + i + "]");
        arrayOfBoolean6[i] = paramCAVLCReader.readBool("sub_layer_non_packed_constraint_flag[" + i + "]");
        arrayOfBoolean7[i] = paramCAVLCReader.readBool("sub_layer_frame_only_constraint_flag[" + i + "]");
        paramCAVLCReader.readNBit(44, "reserved");
        if (arrayOfBoolean2[i] != 0) {
          arrayOfInt3[i] = paramCAVLCReader.readU(8, "sub_layer_level_idc");
        }
        i += 1;
        break;
      }
      arrayOfBoolean[i][j] = paramCAVLCReader.readBool("sub_layer_profile_compatibility_flag[" + i + "][" + j + "]");
      j += 1;
    }
  }
  
  void sub_layer_hrd_parameters(int paramInt1, int paramInt2, boolean paramBoolean, CAVLCReader paramCAVLCReader)
    throws IOException
  {
    int[] arrayOfInt1 = new int[paramInt2];
    int[] arrayOfInt2 = new int[paramInt2];
    int[] arrayOfInt3 = new int[paramInt2];
    int[] arrayOfInt4 = new int[paramInt2];
    boolean[] arrayOfBoolean = new boolean[paramInt2];
    paramInt1 = 0;
    for (;;)
    {
      if (paramInt1 > paramInt2) {
        return;
      }
      arrayOfInt1[paramInt1] = paramCAVLCReader.readUE("bit_rate_value_minus1[" + paramInt1 + "]");
      arrayOfInt2[paramInt1] = paramCAVLCReader.readUE("cpb_size_value_minus1[" + paramInt1 + "]");
      if (paramBoolean)
      {
        arrayOfInt3[paramInt1] = paramCAVLCReader.readUE("cpb_size_du_value_minus1[" + paramInt1 + "]");
        arrayOfInt4[paramInt1] = paramCAVLCReader.readUE("bit_rate_du_value_minus1[" + paramInt1 + "]");
      }
      arrayOfBoolean[paramInt1] = paramCAVLCReader.readBool("cbr_flag[" + paramInt1 + "]");
      paramInt1 += 1;
    }
  }
  
  class LookAhead
  {
    ByteBuffer buffer;
    long bufferStartPos = 0L;
    DataSource dataSource;
    int inBufferPos = 0;
    long start;
    
    LookAhead(DataSource paramDataSource)
      throws IOException
    {
      this.dataSource = paramDataSource;
      fillBuffer();
    }
    
    void discardByte()
    {
      this.inBufferPos += 1;
    }
    
    void discardNext3AndMarkStart()
    {
      this.inBufferPos += 3;
      this.start = (this.bufferStartPos + this.inBufferPos);
    }
    
    public void fillBuffer()
      throws IOException
    {
      this.buffer = this.dataSource.map(this.bufferStartPos, Math.min(this.dataSource.size() - this.bufferStartPos, 1048576L));
    }
    
    public ByteBuffer getNal()
    {
      if (this.start >= this.bufferStartPos)
      {
        this.buffer.position((int)(this.start - this.bufferStartPos));
        ByteBuffer localByteBuffer = this.buffer.slice();
        localByteBuffer.limit((int)(this.inBufferPos - (this.start - this.bufferStartPos)));
        return (ByteBuffer)localByteBuffer;
      }
      throw new RuntimeException("damn! NAL exceeds buffer");
    }
    
    boolean nextThreeEquals000or001orEof()
      throws IOException
    {
      if (this.buffer.limit() - this.inBufferPos >= 3) {
        if ((this.buffer.get(this.inBufferPos) != 0) || (this.buffer.get(this.inBufferPos + 1) != 0) || ((this.buffer.get(this.inBufferPos + 2) != 0) && (this.buffer.get(this.inBufferPos + 2) != 1))) {}
      }
      do
      {
        return true;
        return false;
        if (this.bufferStartPos + this.inBufferPos + 3L <= this.dataSource.size()) {
          break;
        }
      } while (this.bufferStartPos + this.inBufferPos == this.dataSource.size());
      return false;
      this.bufferStartPos = this.start;
      this.inBufferPos = 0;
      fillBuffer();
      return nextThreeEquals000or001orEof();
    }
    
    boolean nextThreeEquals001()
      throws IOException
    {
      if (this.buffer.limit() - this.inBufferPos >= 3) {
        return (this.buffer.get(this.inBufferPos) == 0) && (this.buffer.get(this.inBufferPos + 1) == 0) && (this.buffer.get(this.inBufferPos + 2) == 1);
      }
      if (this.bufferStartPos + this.inBufferPos == this.dataSource.size()) {
        throw new EOFException();
      }
      throw new RuntimeException("buffer repositioning require");
    }
  }
  
  public static class NalUnitHeader
  {
    int forbiddenZeroFlag;
    int nalUnitType;
    int nuhLayerId;
    int nuhTemporalIdPlusOne;
  }
  
  public static enum PARSE_STATE
  {
    AUD_SEI_SLICE,  SEI_SLICE,  SLICE_OES_EOB;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/H265TrackImplOld.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */