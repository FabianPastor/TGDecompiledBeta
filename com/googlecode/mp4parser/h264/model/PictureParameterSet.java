package com.googlecode.mp4parser.h264.model;

import com.googlecode.mp4parser.h264.read.CAVLCReader;
import com.googlecode.mp4parser.h264.write.CAVLCWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class PictureParameterSet
  extends BitstreamElement
{
  public boolean bottom_field_pic_order_in_frame_present_flag;
  public int[] bottom_right;
  public int chroma_qp_index_offset;
  public boolean constrained_intra_pred_flag;
  public boolean deblocking_filter_control_present_flag;
  public boolean entropy_coding_mode_flag;
  public PPSExt extended;
  public int num_ref_idx_l0_active_minus1;
  public int num_ref_idx_l1_active_minus1;
  public int num_slice_groups_minus1;
  public int pic_init_qp_minus26;
  public int pic_init_qs_minus26;
  public int pic_parameter_set_id;
  public boolean redundant_pic_cnt_present_flag;
  public int[] run_length_minus1;
  public int seq_parameter_set_id;
  public boolean slice_group_change_direction_flag;
  public int slice_group_change_rate_minus1;
  public int[] slice_group_id;
  public int slice_group_map_type;
  public int[] top_left;
  public int weighted_bipred_idc;
  public boolean weighted_pred_flag;
  
  public static PictureParameterSet read(InputStream paramInputStream)
    throws IOException
  {
    paramInputStream = new CAVLCReader(paramInputStream);
    PictureParameterSet localPictureParameterSet = new PictureParameterSet();
    localPictureParameterSet.pic_parameter_set_id = paramInputStream.readUE("PPS: pic_parameter_set_id");
    localPictureParameterSet.seq_parameter_set_id = paramInputStream.readUE("PPS: seq_parameter_set_id");
    localPictureParameterSet.entropy_coding_mode_flag = paramInputStream.readBool("PPS: entropy_coding_mode_flag");
    localPictureParameterSet.bottom_field_pic_order_in_frame_present_flag = paramInputStream.readBool("PPS: pic_order_present_flag");
    localPictureParameterSet.num_slice_groups_minus1 = paramInputStream.readUE("PPS: num_slice_groups_minus1");
    int i;
    if (localPictureParameterSet.num_slice_groups_minus1 > 0)
    {
      localPictureParameterSet.slice_group_map_type = paramInputStream.readUE("PPS: slice_group_map_type");
      localPictureParameterSet.top_left = new int[localPictureParameterSet.num_slice_groups_minus1 + 1];
      localPictureParameterSet.bottom_right = new int[localPictureParameterSet.num_slice_groups_minus1 + 1];
      localPictureParameterSet.run_length_minus1 = new int[localPictureParameterSet.num_slice_groups_minus1 + 1];
      if (localPictureParameterSet.slice_group_map_type != 0) {
        break label373;
      }
      i = 0;
      if (i <= localPictureParameterSet.num_slice_groups_minus1) {}
    }
    else
    {
      label153:
      localPictureParameterSet.num_ref_idx_l0_active_minus1 = paramInputStream.readUE("PPS: num_ref_idx_l0_active_minus1");
      localPictureParameterSet.num_ref_idx_l1_active_minus1 = paramInputStream.readUE("PPS: num_ref_idx_l1_active_minus1");
      localPictureParameterSet.weighted_pred_flag = paramInputStream.readBool("PPS: weighted_pred_flag");
      localPictureParameterSet.weighted_bipred_idc = ((int)paramInputStream.readNBit(2, "PPS: weighted_bipred_idc"));
      localPictureParameterSet.pic_init_qp_minus26 = paramInputStream.readSE("PPS: pic_init_qp_minus26");
      localPictureParameterSet.pic_init_qs_minus26 = paramInputStream.readSE("PPS: pic_init_qs_minus26");
      localPictureParameterSet.chroma_qp_index_offset = paramInputStream.readSE("PPS: chroma_qp_index_offset");
      localPictureParameterSet.deblocking_filter_control_present_flag = paramInputStream.readBool("PPS: deblocking_filter_control_present_flag");
      localPictureParameterSet.constrained_intra_pred_flag = paramInputStream.readBool("PPS: constrained_intra_pred_flag");
      localPictureParameterSet.redundant_pic_cnt_present_flag = paramInputStream.readBool("PPS: redundant_pic_cnt_present_flag");
      if (paramInputStream.moreRBSPData())
      {
        localPictureParameterSet.extended = new PPSExt();
        localPictureParameterSet.extended.transform_8x8_mode_flag = paramInputStream.readBool("PPS: transform_8x8_mode_flag");
        if (paramInputStream.readBool("PPS: pic_scaling_matrix_present_flag"))
        {
          i = 0;
          if (!localPictureParameterSet.extended.transform_8x8_mode_flag) {
            break label684;
          }
        }
      }
    }
    label373:
    label659:
    label684:
    for (int j = 1;; j = 0)
    {
      if (i >= j * 2 + 6)
      {
        localPictureParameterSet.extended.second_chroma_qp_index_offset = paramInputStream.readSE("PPS: second_chroma_qp_index_offset");
        paramInputStream.readTrailingBits();
        return localPictureParameterSet;
        localPictureParameterSet.run_length_minus1[i] = paramInputStream.readUE("PPS: run_length_minus1");
        i += 1;
        break;
        if (localPictureParameterSet.slice_group_map_type == 2)
        {
          i = 0;
          while (i < localPictureParameterSet.num_slice_groups_minus1)
          {
            localPictureParameterSet.top_left[i] = paramInputStream.readUE("PPS: top_left");
            localPictureParameterSet.bottom_right[i] = paramInputStream.readUE("PPS: bottom_right");
            i += 1;
          }
          break label153;
        }
        if ((localPictureParameterSet.slice_group_map_type == 3) || (localPictureParameterSet.slice_group_map_type == 4) || (localPictureParameterSet.slice_group_map_type == 5))
        {
          localPictureParameterSet.slice_group_change_direction_flag = paramInputStream.readBool("PPS: slice_group_change_direction_flag");
          localPictureParameterSet.slice_group_change_rate_minus1 = paramInputStream.readUE("PPS: slice_group_change_rate_minus1");
          break label153;
        }
        if (localPictureParameterSet.slice_group_map_type != 6) {
          break label153;
        }
        if (localPictureParameterSet.num_slice_groups_minus1 + 1 > 4) {
          i = 3;
        }
        for (;;)
        {
          int k = paramInputStream.readUE("PPS: pic_size_in_map_units_minus1");
          localPictureParameterSet.slice_group_id = new int[k + 1];
          j = 0;
          while (j <= k)
          {
            localPictureParameterSet.slice_group_id[j] = paramInputStream.readU(i, "PPS: slice_group_id [" + j + "]f");
            j += 1;
          }
          break;
          if (localPictureParameterSet.num_slice_groups_minus1 + 1 > 2) {
            i = 2;
          } else {
            i = 1;
          }
        }
      }
      if (paramInputStream.readBool("PPS: pic_scaling_list_present_flag"))
      {
        localPictureParameterSet.extended.scalindMatrix.ScalingList4x4 = new ScalingList[8];
        localPictureParameterSet.extended.scalindMatrix.ScalingList8x8 = new ScalingList[8];
        if (i >= 6) {
          break label659;
        }
        localPictureParameterSet.extended.scalindMatrix.ScalingList4x4[i] = ScalingList.read(paramInputStream, 16);
      }
      for (;;)
      {
        i += 1;
        break;
        localPictureParameterSet.extended.scalindMatrix.ScalingList8x8[(i - 6)] = ScalingList.read(paramInputStream, 64);
      }
    }
  }
  
  public static PictureParameterSet read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(new ByteArrayInputStream(paramArrayOfByte));
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (PictureParameterSet)paramObject;
      if (!Arrays.equals(this.bottom_right, ((PictureParameterSet)paramObject).bottom_right)) {
        return false;
      }
      if (this.chroma_qp_index_offset != ((PictureParameterSet)paramObject).chroma_qp_index_offset) {
        return false;
      }
      if (this.constrained_intra_pred_flag != ((PictureParameterSet)paramObject).constrained_intra_pred_flag) {
        return false;
      }
      if (this.deblocking_filter_control_present_flag != ((PictureParameterSet)paramObject).deblocking_filter_control_present_flag) {
        return false;
      }
      if (this.entropy_coding_mode_flag != ((PictureParameterSet)paramObject).entropy_coding_mode_flag) {
        return false;
      }
      if (this.extended == null)
      {
        if (((PictureParameterSet)paramObject).extended != null) {
          return false;
        }
      }
      else if (!this.extended.equals(((PictureParameterSet)paramObject).extended)) {
        return false;
      }
      if (this.num_ref_idx_l0_active_minus1 != ((PictureParameterSet)paramObject).num_ref_idx_l0_active_minus1) {
        return false;
      }
      if (this.num_ref_idx_l1_active_minus1 != ((PictureParameterSet)paramObject).num_ref_idx_l1_active_minus1) {
        return false;
      }
      if (this.num_slice_groups_minus1 != ((PictureParameterSet)paramObject).num_slice_groups_minus1) {
        return false;
      }
      if (this.pic_init_qp_minus26 != ((PictureParameterSet)paramObject).pic_init_qp_minus26) {
        return false;
      }
      if (this.pic_init_qs_minus26 != ((PictureParameterSet)paramObject).pic_init_qs_minus26) {
        return false;
      }
      if (this.bottom_field_pic_order_in_frame_present_flag != ((PictureParameterSet)paramObject).bottom_field_pic_order_in_frame_present_flag) {
        return false;
      }
      if (this.pic_parameter_set_id != ((PictureParameterSet)paramObject).pic_parameter_set_id) {
        return false;
      }
      if (this.redundant_pic_cnt_present_flag != ((PictureParameterSet)paramObject).redundant_pic_cnt_present_flag) {
        return false;
      }
      if (!Arrays.equals(this.run_length_minus1, ((PictureParameterSet)paramObject).run_length_minus1)) {
        return false;
      }
      if (this.seq_parameter_set_id != ((PictureParameterSet)paramObject).seq_parameter_set_id) {
        return false;
      }
      if (this.slice_group_change_direction_flag != ((PictureParameterSet)paramObject).slice_group_change_direction_flag) {
        return false;
      }
      if (this.slice_group_change_rate_minus1 != ((PictureParameterSet)paramObject).slice_group_change_rate_minus1) {
        return false;
      }
      if (!Arrays.equals(this.slice_group_id, ((PictureParameterSet)paramObject).slice_group_id)) {
        return false;
      }
      if (this.slice_group_map_type != ((PictureParameterSet)paramObject).slice_group_map_type) {
        return false;
      }
      if (!Arrays.equals(this.top_left, ((PictureParameterSet)paramObject).top_left)) {
        return false;
      }
      if (this.weighted_bipred_idc != ((PictureParameterSet)paramObject).weighted_bipred_idc) {
        return false;
      }
    } while (this.weighted_pred_flag == ((PictureParameterSet)paramObject).weighted_pred_flag);
    return false;
  }
  
  public int hashCode()
  {
    int i3 = 1231;
    int i4 = Arrays.hashCode(this.bottom_right);
    int i5 = this.chroma_qp_index_offset;
    int i;
    int j;
    label42:
    int k;
    label53:
    int m;
    label63:
    int i6;
    int i7;
    int i8;
    int i9;
    int i10;
    int n;
    label105:
    int i11;
    int i1;
    label123:
    int i12;
    int i13;
    int i2;
    label150:
    int i14;
    int i15;
    int i16;
    int i17;
    int i18;
    if (this.constrained_intra_pred_flag)
    {
      i = 1231;
      if (!this.deblocking_filter_control_present_flag) {
        break label335;
      }
      j = 1231;
      if (!this.entropy_coding_mode_flag) {
        break label342;
      }
      k = 1231;
      if (this.extended != null) {
        break label349;
      }
      m = 0;
      i6 = this.num_ref_idx_l0_active_minus1;
      i7 = this.num_ref_idx_l1_active_minus1;
      i8 = this.num_slice_groups_minus1;
      i9 = this.pic_init_qp_minus26;
      i10 = this.pic_init_qs_minus26;
      if (!this.bottom_field_pic_order_in_frame_present_flag) {
        break label361;
      }
      n = 1231;
      i11 = this.pic_parameter_set_id;
      if (!this.redundant_pic_cnt_present_flag) {
        break label369;
      }
      i1 = 1231;
      i12 = Arrays.hashCode(this.run_length_minus1);
      i13 = this.seq_parameter_set_id;
      if (!this.slice_group_change_direction_flag) {
        break label377;
      }
      i2 = 1231;
      i14 = this.slice_group_change_rate_minus1;
      i15 = Arrays.hashCode(this.slice_group_id);
      i16 = this.slice_group_map_type;
      i17 = Arrays.hashCode(this.top_left);
      i18 = this.weighted_bipred_idc;
      if (!this.weighted_pred_flag) {
        break label385;
      }
    }
    for (;;)
    {
      return ((((((((((((((((((((((i4 + 31) * 31 + i5) * 31 + i) * 31 + j) * 31 + k) * 31 + m) * 31 + i6) * 31 + i7) * 31 + i8) * 31 + i9) * 31 + i10) * 31 + n) * 31 + i11) * 31 + i1) * 31 + i12) * 31 + i13) * 31 + i2) * 31 + i14) * 31 + i15) * 31 + i16) * 31 + i17) * 31 + i18) * 31 + i3;
      i = 1237;
      break;
      label335:
      j = 1237;
      break label42;
      label342:
      k = 1237;
      break label53;
      label349:
      m = this.extended.hashCode();
      break label63;
      label361:
      n = 1237;
      break label105;
      label369:
      i1 = 1237;
      break label123;
      label377:
      i2 = 1237;
      break label150;
      label385:
      i3 = 1237;
    }
  }
  
  public String toString()
  {
    return "PictureParameterSet{\n       entropy_coding_mode_flag=" + this.entropy_coding_mode_flag + ",\n       num_ref_idx_l0_active_minus1=" + this.num_ref_idx_l0_active_minus1 + ",\n       num_ref_idx_l1_active_minus1=" + this.num_ref_idx_l1_active_minus1 + ",\n       slice_group_change_rate_minus1=" + this.slice_group_change_rate_minus1 + ",\n       pic_parameter_set_id=" + this.pic_parameter_set_id + ",\n       seq_parameter_set_id=" + this.seq_parameter_set_id + ",\n       pic_order_present_flag=" + this.bottom_field_pic_order_in_frame_present_flag + ",\n       num_slice_groups_minus1=" + this.num_slice_groups_minus1 + ",\n       slice_group_map_type=" + this.slice_group_map_type + ",\n       weighted_pred_flag=" + this.weighted_pred_flag + ",\n       weighted_bipred_idc=" + this.weighted_bipred_idc + ",\n       pic_init_qp_minus26=" + this.pic_init_qp_minus26 + ",\n       pic_init_qs_minus26=" + this.pic_init_qs_minus26 + ",\n       chroma_qp_index_offset=" + this.chroma_qp_index_offset + ",\n       deblocking_filter_control_present_flag=" + this.deblocking_filter_control_present_flag + ",\n       constrained_intra_pred_flag=" + this.constrained_intra_pred_flag + ",\n       redundant_pic_cnt_present_flag=" + this.redundant_pic_cnt_present_flag + ",\n       top_left=" + this.top_left + ",\n       bottom_right=" + this.bottom_right + ",\n       run_length_minus1=" + this.run_length_minus1 + ",\n       slice_group_change_direction_flag=" + this.slice_group_change_direction_flag + ",\n       slice_group_id=" + this.slice_group_id + ",\n       extended=" + this.extended + '}';
  }
  
  public void write(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream = new CAVLCWriter(paramOutputStream);
    paramOutputStream.writeUE(this.pic_parameter_set_id, "PPS: pic_parameter_set_id");
    paramOutputStream.writeUE(this.seq_parameter_set_id, "PPS: seq_parameter_set_id");
    paramOutputStream.writeBool(this.entropy_coding_mode_flag, "PPS: entropy_coding_mode_flag");
    paramOutputStream.writeBool(this.bottom_field_pic_order_in_frame_present_flag, "PPS: pic_order_present_flag");
    paramOutputStream.writeUE(this.num_slice_groups_minus1, "PPS: num_slice_groups_minus1");
    int[] arrayOfInt1;
    int[] arrayOfInt2;
    int[] arrayOfInt3;
    int i;
    label108:
    boolean bool;
    if (this.num_slice_groups_minus1 > 0)
    {
      paramOutputStream.writeUE(this.slice_group_map_type, "PPS: slice_group_map_type");
      arrayOfInt1 = new int[1];
      arrayOfInt2 = new int[1];
      arrayOfInt3 = new int[1];
      if (this.slice_group_map_type != 0) {
        break label323;
      }
      i = 0;
      if (i <= this.num_slice_groups_minus1) {}
    }
    else
    {
      paramOutputStream.writeUE(this.num_ref_idx_l0_active_minus1, "PPS: num_ref_idx_l0_active_minus1");
      paramOutputStream.writeUE(this.num_ref_idx_l1_active_minus1, "PPS: num_ref_idx_l1_active_minus1");
      paramOutputStream.writeBool(this.weighted_pred_flag, "PPS: weighted_pred_flag");
      paramOutputStream.writeNBit(this.weighted_bipred_idc, 2, "PPS: weighted_bipred_idc");
      paramOutputStream.writeSE(this.pic_init_qp_minus26, "PPS: pic_init_qp_minus26");
      paramOutputStream.writeSE(this.pic_init_qs_minus26, "PPS: pic_init_qs_minus26");
      paramOutputStream.writeSE(this.chroma_qp_index_offset, "PPS: chroma_qp_index_offset");
      paramOutputStream.writeBool(this.deblocking_filter_control_present_flag, "PPS: deblocking_filter_control_present_flag");
      paramOutputStream.writeBool(this.constrained_intra_pred_flag, "PPS: constrained_intra_pred_flag");
      paramOutputStream.writeBool(this.redundant_pic_cnt_present_flag, "PPS: redundant_pic_cnt_present_flag");
      if (this.extended != null)
      {
        paramOutputStream.writeBool(this.extended.transform_8x8_mode_flag, "PPS: transform_8x8_mode_flag");
        if (this.extended.scalindMatrix == null) {
          break label499;
        }
        bool = true;
        label243:
        paramOutputStream.writeBool(bool, "PPS: scalindMatrix");
        if (this.extended.scalindMatrix != null)
        {
          i = 0;
          if (!this.extended.transform_8x8_mode_flag) {
            break label658;
          }
        }
      }
    }
    label323:
    label499:
    label658:
    for (int j = 1;; j = 0)
    {
      if (i >= j * 2 + 6)
      {
        paramOutputStream.writeSE(this.extended.second_chroma_qp_index_offset, "PPS: ");
        paramOutputStream.writeTrailingBits();
        return;
        paramOutputStream.writeUE(arrayOfInt3[i], "PPS: ");
        i += 1;
        break;
        if (this.slice_group_map_type == 2)
        {
          i = 0;
          while (i < this.num_slice_groups_minus1)
          {
            paramOutputStream.writeUE(arrayOfInt1[i], "PPS: ");
            paramOutputStream.writeUE(arrayOfInt2[i], "PPS: ");
            i += 1;
          }
          break label108;
        }
        if ((this.slice_group_map_type == 3) || (this.slice_group_map_type == 4) || (this.slice_group_map_type == 5))
        {
          paramOutputStream.writeBool(this.slice_group_change_direction_flag, "PPS: slice_group_change_direction_flag");
          paramOutputStream.writeUE(this.slice_group_change_rate_minus1, "PPS: slice_group_change_rate_minus1");
          break label108;
        }
        if (this.slice_group_map_type != 6) {
          break label108;
        }
        if (this.num_slice_groups_minus1 + 1 > 4) {
          i = 3;
        }
        for (;;)
        {
          paramOutputStream.writeUE(this.slice_group_id.length, "PPS: ");
          j = 0;
          while (j <= this.slice_group_id.length)
          {
            paramOutputStream.writeU(this.slice_group_id[j], i);
            j += 1;
          }
          break;
          if (this.num_slice_groups_minus1 + 1 > 2) {
            i = 2;
          } else {
            i = 1;
          }
        }
        bool = false;
        break label243;
      }
      if (i < 6)
      {
        if (this.extended.scalindMatrix.ScalingList4x4[i] != null) {}
        for (bool = true;; bool = false)
        {
          paramOutputStream.writeBool(bool, "PPS: ");
          if (this.extended.scalindMatrix.ScalingList4x4[i] != null) {
            this.extended.scalindMatrix.ScalingList4x4[i].write(paramOutputStream);
          }
          i += 1;
          break;
        }
      }
      if (this.extended.scalindMatrix.ScalingList8x8[(i - 6)] != null) {}
      for (bool = true;; bool = false)
      {
        paramOutputStream.writeBool(bool, "PPS: ");
        if (this.extended.scalindMatrix.ScalingList8x8[(i - 6)] == null) {
          break;
        }
        this.extended.scalindMatrix.ScalingList8x8[(i - 6)].write(paramOutputStream);
        break;
      }
    }
  }
  
  public static class PPSExt
  {
    public boolean[] pic_scaling_list_present_flag;
    public ScalingMatrix scalindMatrix = new ScalingMatrix();
    public int second_chroma_qp_index_offset;
    public boolean transform_8x8_mode_flag;
    
    public String toString()
    {
      return "PPSExt{transform_8x8_mode_flag=" + this.transform_8x8_mode_flag + ", scalindMatrix=" + this.scalindMatrix + ", second_chroma_qp_index_offset=" + this.second_chroma_qp_index_offset + ", pic_scaling_list_present_flag=" + this.pic_scaling_list_present_flag + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/model/PictureParameterSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */