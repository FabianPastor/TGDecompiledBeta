package com.googlecode.mp4parser.authoring.tracks.h265;

import com.googlecode.mp4parser.h264.read.CAVLCReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

public class SequenceParameterSetRbsp
{
  public SequenceParameterSetRbsp(InputStream paramInputStream)
    throws IOException
  {
    paramInputStream = new CAVLCReader(paramInputStream);
    int i = (int)paramInputStream.readNBit(4, "sps_video_parameter_set_id");
    int j = (int)paramInputStream.readNBit(3, "sps_max_sub_layers_minus1");
    paramInputStream.readBool("sps_temporal_id_nesting_flag");
    profile_tier_level(j, paramInputStream);
    paramInputStream.readUE("sps_seq_parameter_set_id");
    if (paramInputStream.readUE("chroma_format_idc") == 3)
    {
      paramInputStream.read1Bit();
      paramInputStream.readUE("pic_width_in_luma_samples");
      paramInputStream.readUE("pic_width_in_luma_samples");
      if (paramInputStream.readBool("conformance_window_flag"))
      {
        paramInputStream.readUE("conf_win_left_offset");
        paramInputStream.readUE("conf_win_right_offset");
        paramInputStream.readUE("conf_win_top_offset");
        paramInputStream.readUE("conf_win_bottom_offset");
      }
    }
    paramInputStream.readUE("bit_depth_luma_minus8");
    paramInputStream.readUE("bit_depth_chroma_minus8");
    paramInputStream.readUE("log2_max_pic_order_cnt_lsb_minus4");
    boolean bool = paramInputStream.readBool("sps_sub_layer_ordering_info_present_flag");
    int[] arrayOfInt1;
    int[] arrayOfInt2;
    int[] arrayOfInt3;
    if (bool)
    {
      i = 0;
      i = j - i + 1;
      arrayOfInt1 = new int[i];
      arrayOfInt2 = new int[i];
      arrayOfInt3 = new int[i];
      if (!bool) {
        break label305;
      }
      i = 0;
    }
    for (;;)
    {
      if (i > j)
      {
        paramInputStream.readUE("log2_min_luma_coding_block_size_minus3");
        paramInputStream.readUE("log2_diff_max_min_luma_coding_block_size");
        paramInputStream.readUE("log2_min_transform_block_size_minus2");
        paramInputStream.readUE("log2_diff_max_min_transform_block_size");
        paramInputStream.readUE("max_transform_hierarchy_depth_inter");
        paramInputStream.readUE("max_transform_hierarchy_depth_intra");
        if ((paramInputStream.readBool("scaling_list_enabled_flag")) && (paramInputStream.readBool("sps_scaling_list_data_present_flag"))) {
          scaling_list_data(paramInputStream);
        }
        paramInputStream.readBool("amp_enabled_flag");
        paramInputStream.readBool("sample_adaptive_offset_enabled_flag");
        if (paramInputStream.readBool("pcm_enabled_flag"))
        {
          i = (int)paramInputStream.readNBit(4, "pcm_sample_bit_depth_luma_minus1");
          i = (int)paramInputStream.readNBit(4, "pcm_sample_bit_depth_chroma_minus1");
          paramInputStream.readUE("log2_min_pcm_luma_coding_block_size_minus3");
        }
        return;
        i = j;
        break;
        label305:
        i = j;
        continue;
      }
      arrayOfInt1[i] = paramInputStream.readUE("sps_max_dec_pic_buffering_minus1[" + i + "]");
      arrayOfInt2[i] = paramInputStream.readUE("sps_max_num_reorder_pics[" + i + "]");
      arrayOfInt3[i] = paramInputStream.readUE("sps_max_latency_increase_plus1[" + i + "]");
      i += 1;
    }
  }
  
  private void profile_tier_level(int paramInt, CAVLCReader paramCAVLCReader)
    throws IOException
  {
    paramCAVLCReader.readU(2, "general_profile_space");
    paramCAVLCReader.readBool("general_tier_flag");
    paramCAVLCReader.readU(5, "general_profile_idc");
    boolean[] arrayOfBoolean1 = new boolean[32];
    int i = 0;
    boolean[] arrayOfBoolean2;
    label91:
    int[] arrayOfInt1;
    if (i >= 32)
    {
      paramCAVLCReader.readBool("general_progressive_source_flag");
      paramCAVLCReader.readBool("general_interlaced_source_flag");
      paramCAVLCReader.readBool("general_non_packed_constraint_flag");
      paramCAVLCReader.readBool("general_frame_only_constraint_flag");
      paramCAVLCReader.readNBit(44, "general_reserved_zero_44bits");
      paramCAVLCReader.readByte();
      arrayOfBoolean1 = new boolean[paramInt];
      arrayOfBoolean2 = new boolean[paramInt];
      i = 0;
      if (i < paramInt) {
        break label205;
      }
      if (paramInt > 0)
      {
        arrayOfInt1 = new int[8];
        i = paramInt;
      }
    }
    boolean[] arrayOfBoolean3;
    int[] arrayOfInt2;
    boolean[][] arrayOfBoolean;
    boolean[] arrayOfBoolean4;
    boolean[] arrayOfBoolean5;
    boolean[] arrayOfBoolean6;
    boolean[] arrayOfBoolean7;
    long[] arrayOfLong;
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
        arrayOfLong = new long[paramInt];
        arrayOfInt3 = new int[paramInt];
        i = 0;
        if (i < paramInt) {
          break label307;
        }
        return;
        arrayOfBoolean1[i] = paramCAVLCReader.readBool();
        i += 1;
        break;
        label205:
        arrayOfBoolean1[i] = paramCAVLCReader.readBool("sub_layer_profile_present_flag[" + i + "]");
        arrayOfBoolean2[i] = paramCAVLCReader.readBool("sub_layer_level_present_flag[" + i + "]");
        i += 1;
        break label91;
      }
      arrayOfInt1[i] = paramCAVLCReader.readU(2, "reserved_zero_2bits[" + i + "]");
      i += 1;
    }
    label307:
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
        arrayOfLong[i] = paramCAVLCReader.readNBit(44);
        if (arrayOfBoolean2[i] != 0) {
          arrayOfInt3[i] = paramCAVLCReader.readU(8, "sub_layer_level_idc[" + i + "]");
        }
        i += 1;
        break;
      }
      arrayOfBoolean[i][j] = paramCAVLCReader.readBool("sub_layer_profile_compatibility_flag[" + i + "][" + j + "]");
      j += 1;
    }
  }
  
  private void scaling_list_data(CAVLCReader paramCAVLCReader)
    throws IOException
  {
    boolean[][] arrayOfBoolean = new boolean[4][];
    int[][] arrayOfInt1 = new int[4][];
    int[][] arrayOfInt2 = new int[2][];
    int[][][] arrayOfInt = new int[4][][];
    int j = 0;
    if (j >= 4) {
      return;
    }
    int k = 0;
    if (j == 3) {}
    for (int i = 2;; i = 6)
    {
      if (k >= i)
      {
        j += 1;
        break;
      }
      if (j == 3)
      {
        i = 2;
        label62:
        arrayOfBoolean[j] = new boolean[i];
        if (j != 3) {
          break label176;
        }
        i = 2;
        label76:
        arrayOfInt1[j] = new int[i];
        if (j != 3) {
          break label182;
        }
        i = 2;
        label90:
        arrayOfInt[j] = new int[i][];
        arrayOfBoolean[j][k] = paramCAVLCReader.readBool();
        if (arrayOfBoolean[j][k] != 0) {
          break label188;
        }
        arrayOfInt1[j][k] = paramCAVLCReader.readUE("scaling_list_pred_matrix_id_delta[" + j + "][" + k + "]");
      }
      for (;;)
      {
        k += 1;
        break;
        i = 6;
        break label62;
        label176:
        i = 6;
        break label76;
        label182:
        i = 6;
        break label90;
        label188:
        i = 8;
        int i1 = Math.min(64, 1 << (j << 1) + 4);
        if (j > 1)
        {
          arrayOfInt2[(j - 2)][k] = paramCAVLCReader.readSE("scaling_list_dc_coef_minus8[" + j + "- 2][" + k + "]");
          i = arrayOfInt2[(j - 2)][k] + 8;
        }
        arrayOfInt[j][k] = new int[i1];
        int n = 0;
        int m = i;
        i = n;
        while (i < i1)
        {
          m = (m + paramCAVLCReader.readSE("scaling_list_delta_coef ") + 256) % 256;
          arrayOfInt[j][k][i] = m;
          i += 1;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/h265/SequenceParameterSetRbsp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */