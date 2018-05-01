package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

@KeepName
public class CommonWalletObject
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<CommonWalletObject> CREATOR = new zzb();
  String name;
  int state;
  String zzce;
  String zzcg;
  String zzcj;
  String zzck;
  String zzcl;
  String zzcm;
  String zzcn;
  ArrayList<WalletObjectMessage> zzco;
  TimeInterval zzcp;
  ArrayList<LatLng> zzcq;
  String zzcr;
  String zzcs;
  ArrayList<LabelValueRow> zzct;
  boolean zzcu;
  ArrayList<UriData> zzcv;
  ArrayList<TextModuleData> zzcw;
  ArrayList<UriData> zzcx;
  
  CommonWalletObject()
  {
    this.zzco = ArrayUtils.newArrayList();
    this.zzcq = ArrayUtils.newArrayList();
    this.zzct = ArrayUtils.newArrayList();
    this.zzcv = ArrayUtils.newArrayList();
    this.zzcw = ArrayUtils.newArrayList();
    this.zzcx = ArrayUtils.newArrayList();
  }
  
  CommonWalletObject(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, int paramInt, ArrayList<WalletObjectMessage> paramArrayList, TimeInterval paramTimeInterval, ArrayList<LatLng> paramArrayList1, String paramString9, String paramString10, ArrayList<LabelValueRow> paramArrayList2, boolean paramBoolean, ArrayList<UriData> paramArrayList3, ArrayList<TextModuleData> paramArrayList4, ArrayList<UriData> paramArrayList5)
  {
    this.zzce = paramString1;
    this.zzcn = paramString2;
    this.name = paramString3;
    this.zzcg = paramString4;
    this.zzcj = paramString5;
    this.zzck = paramString6;
    this.zzcl = paramString7;
    this.zzcm = paramString8;
    this.state = paramInt;
    this.zzco = paramArrayList;
    this.zzcp = paramTimeInterval;
    this.zzcq = paramArrayList1;
    this.zzcr = paramString9;
    this.zzcs = paramString10;
    this.zzct = paramArrayList2;
    this.zzcu = paramBoolean;
    this.zzcv = paramArrayList3;
    this.zzcw = paramArrayList4;
    this.zzcx = paramArrayList5;
  }
  
  public static zza zze()
  {
    return new zza(new CommonWalletObject(), null);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzce, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzcn, false);
    SafeParcelWriter.writeString(paramParcel, 4, this.name, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzcg, false);
    SafeParcelWriter.writeString(paramParcel, 6, this.zzcj, false);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzck, false);
    SafeParcelWriter.writeString(paramParcel, 8, this.zzcl, false);
    SafeParcelWriter.writeString(paramParcel, 9, this.zzcm, false);
    SafeParcelWriter.writeInt(paramParcel, 10, this.state);
    SafeParcelWriter.writeTypedList(paramParcel, 11, this.zzco, false);
    SafeParcelWriter.writeParcelable(paramParcel, 12, this.zzcp, paramInt, false);
    SafeParcelWriter.writeTypedList(paramParcel, 13, this.zzcq, false);
    SafeParcelWriter.writeString(paramParcel, 14, this.zzcr, false);
    SafeParcelWriter.writeString(paramParcel, 15, this.zzcs, false);
    SafeParcelWriter.writeTypedList(paramParcel, 16, this.zzct, false);
    SafeParcelWriter.writeBoolean(paramParcel, 17, this.zzcu);
    SafeParcelWriter.writeTypedList(paramParcel, 18, this.zzcv, false);
    SafeParcelWriter.writeTypedList(paramParcel, 19, this.zzcw, false);
    SafeParcelWriter.writeTypedList(paramParcel, 20, this.zzcx, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
  
  public final class zza
  {
    private zza() {}
    
    public final zza zza(String paramString)
    {
      CommonWalletObject.this.zzce = paramString;
      return this;
    }
    
    public final CommonWalletObject zzf()
    {
      return CommonWalletObject.this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/wobs/CommonWalletObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */