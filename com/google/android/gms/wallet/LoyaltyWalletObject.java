package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.wobs.LabelValueRow;
import com.google.android.gms.wallet.wobs.LoyaltyPoints;
import com.google.android.gms.wallet.wobs.TextModuleData;
import com.google.android.gms.wallet.wobs.TimeInterval;
import com.google.android.gms.wallet.wobs.UriData;
import com.google.android.gms.wallet.wobs.WalletObjectMessage;
import java.util.ArrayList;

public final class LoyaltyWalletObject
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<LoyaltyWalletObject> CREATOR = new zzv();
  int state;
  String zzce;
  String zzcf;
  String zzcg;
  String zzch;
  String zzci;
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
  LoyaltyPoints zzcy;
  
  LoyaltyWalletObject()
  {
    this.zzco = ArrayUtils.newArrayList();
    this.zzcq = ArrayUtils.newArrayList();
    this.zzct = ArrayUtils.newArrayList();
    this.zzcv = ArrayUtils.newArrayList();
    this.zzcw = ArrayUtils.newArrayList();
    this.zzcx = ArrayUtils.newArrayList();
  }
  
  LoyaltyWalletObject(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, String paramString10, int paramInt, ArrayList<WalletObjectMessage> paramArrayList, TimeInterval paramTimeInterval, ArrayList<LatLng> paramArrayList1, String paramString11, String paramString12, ArrayList<LabelValueRow> paramArrayList2, boolean paramBoolean, ArrayList<UriData> paramArrayList3, ArrayList<TextModuleData> paramArrayList4, ArrayList<UriData> paramArrayList5, LoyaltyPoints paramLoyaltyPoints)
  {
    this.zzce = paramString1;
    this.zzcf = paramString2;
    this.zzcg = paramString3;
    this.zzch = paramString4;
    this.zzci = paramString5;
    this.zzcj = paramString6;
    this.zzck = paramString7;
    this.zzcl = paramString8;
    this.zzcm = paramString9;
    this.zzcn = paramString10;
    this.state = paramInt;
    this.zzco = paramArrayList;
    this.zzcp = paramTimeInterval;
    this.zzcq = paramArrayList1;
    this.zzcr = paramString11;
    this.zzcs = paramString12;
    this.zzct = paramArrayList2;
    this.zzcu = paramBoolean;
    this.zzcv = paramArrayList3;
    this.zzcw = paramArrayList4;
    this.zzcx = paramArrayList5;
    this.zzcy = paramLoyaltyPoints;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.zzce, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzcf, false);
    SafeParcelWriter.writeString(paramParcel, 4, this.zzcg, false);
    SafeParcelWriter.writeString(paramParcel, 5, this.zzch, false);
    SafeParcelWriter.writeString(paramParcel, 6, this.zzci, false);
    SafeParcelWriter.writeString(paramParcel, 7, this.zzcj, false);
    SafeParcelWriter.writeString(paramParcel, 8, this.zzck, false);
    SafeParcelWriter.writeString(paramParcel, 9, this.zzcl, false);
    SafeParcelWriter.writeString(paramParcel, 10, this.zzcm, false);
    SafeParcelWriter.writeString(paramParcel, 11, this.zzcn, false);
    SafeParcelWriter.writeInt(paramParcel, 12, this.state);
    SafeParcelWriter.writeTypedList(paramParcel, 13, this.zzco, false);
    SafeParcelWriter.writeParcelable(paramParcel, 14, this.zzcp, paramInt, false);
    SafeParcelWriter.writeTypedList(paramParcel, 15, this.zzcq, false);
    SafeParcelWriter.writeString(paramParcel, 16, this.zzcr, false);
    SafeParcelWriter.writeString(paramParcel, 17, this.zzcs, false);
    SafeParcelWriter.writeTypedList(paramParcel, 18, this.zzct, false);
    SafeParcelWriter.writeBoolean(paramParcel, 19, this.zzcu);
    SafeParcelWriter.writeTypedList(paramParcel, 20, this.zzcv, false);
    SafeParcelWriter.writeTypedList(paramParcel, 21, this.zzcw, false);
    SafeParcelWriter.writeTypedList(paramParcel, 22, this.zzcx, false);
    SafeParcelWriter.writeParcelable(paramParcel, 23, this.zzcy, paramInt, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/LoyaltyWalletObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */