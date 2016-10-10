package com.googlecode.mp4parser.authoring.tracks.h265;

import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

public class VideoParameterSet
{
  ByteBuffer vps;
  int vps_parameter_set_id;
  
  public VideoParameterSet(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.vps = paramByteBuffer;
    paramByteBuffer = new CAVLCReader(Channels.newInputStream(new ByteBufferByteChannel((ByteBuffer)paramByteBuffer.position(0))));
    this.vps_parameter_set_id = paramByteBuffer.readU(4, "vps_parameter_set_id");
    paramByteBuffer.readU(2, "vps_reserved_three_2bits");
    paramByteBuffer.readU(6, "vps_max_layers_minus1");
    int k = paramByteBuffer.readU(3, "vps_max_sub_layers_minus1");
    paramByteBuffer.readBool("vps_temporal_id_nesting_flag");
    paramByteBuffer.readU(16, "vps_reserved_0xffff_16bits");
    profile_tier_level(k, paramByteBuffer);
    boolean bool = paramByteBuffer.readBool("vps_sub_layer_ordering_info_present_flag");
    int i;
    Object localObject1;
    label122:
    Object localObject2;
    label134:
    int[] arrayOfInt;
    label146:
    int m;
    int j;
    if (bool)
    {
      i = 1;
      localObject1 = new int[i];
      if (!bool) {
        break label302;
      }
      i = 1;
      localObject2 = new int[i];
      if (!bool) {
        break label310;
      }
      i = 1;
      arrayOfInt = new int[i];
      if (!bool) {
        break label318;
      }
      i = 0;
      if (i <= k) {
        break label324;
      }
      m = paramByteBuffer.readU(6, "vps_max_layer_id");
      int n = paramByteBuffer.readUE("vps_num_layer_sets_minus1");
      localObject1 = (boolean[][])Array.newInstance(Boolean.TYPE, new int[] { n, m });
      i = 1;
      if (i <= n) {
        break label418;
      }
      if (paramByteBuffer.readBool("vps_timing_info_present_flag"))
      {
        long l = paramByteBuffer.readU(32, "vps_num_units_in_tick");
        l = paramByteBuffer.readU(32, "vps_time_scale");
        if (paramByteBuffer.readBool("vps_poc_proportional_to_timing_flag")) {
          paramByteBuffer.readUE("vps_num_ticks_poc_diff_one_minus1");
        }
        j = paramByteBuffer.readUE("vps_num_hrd_parameters");
        localObject1 = new int[j];
        localObject2 = new boolean[j];
        i = 0;
        if (i < j) {
          break label480;
        }
      }
      if (!paramByteBuffer.readBool("vps_extension_flag")) {}
    }
    for (;;)
    {
      if (!paramByteBuffer.moreRBSPData())
      {
        paramByteBuffer.readTrailingBits();
        return;
        i = k + 1;
        break;
        label302:
        i = k + 1;
        break label122;
        label310:
        i = k + 1;
        break label134;
        label318:
        i = k;
        break label146;
        label324:
        localObject1[i] = paramByteBuffer.readUE("vps_max_dec_pic_buffering_minus1[" + i + "]");
        localObject2[i] = paramByteBuffer.readUE("vps_max_dec_pic_buffering_minus1[" + i + "]");
        arrayOfInt[i] = paramByteBuffer.readUE("vps_max_dec_pic_buffering_minus1[" + i + "]");
        i += 1;
        break label146;
        label418:
        j = 0;
        for (;;)
        {
          if (j > m)
          {
            i += 1;
            break;
          }
          localObject1[i][j] = paramByteBuffer.readBool("layer_id_included_flag[" + i + "][" + j + "]");
          j += 1;
        }
        label480:
        localObject1[i] = paramByteBuffer.readUE("hrd_layer_set_idx[" + i + "]");
        if (i > 0) {
          localObject2[i] = paramByteBuffer.readBool("cprms_present_flag[" + i + "]");
        }
        for (;;)
        {
          hrd_parameters(localObject2[i], k, paramByteBuffer);
          i += 1;
          break;
          localObject2[0] = 1;
        }
      }
      paramByteBuffer.readBool("vps_extension_data_flag");
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
      label97:
      if (i < paramInt) {
        break label221;
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
          break label301;
        }
        return;
        arrayOfBoolean1[i] = paramCAVLCReader.readBool("general_profile_compatibility_flag[" + i + "]");
        i += 1;
        break;
        label221:
        arrayOfBoolean1[i] = paramCAVLCReader.readBool("sub_layer_profile_present_flag[" + i + "]");
        arrayOfBoolean2[i] = paramCAVLCReader.readBool("sub_layer_level_present_flag[" + i + "]");
        i += 1;
        break label97;
      }
      paramCAVLCReader.readU(2, "reserved_zero_2bits");
      i += 1;
    }
    label301:
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
  
  public ByteBuffer toByteBuffer()
  {
    return this.vps;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/h265/VideoParameterSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */