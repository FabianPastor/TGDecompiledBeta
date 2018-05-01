package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.DefaultClock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSignInAccount
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<GoogleSignInAccount> CREATOR = new GoogleSignInAccountCreator();
  public static Clock sClock = DefaultClock.getInstance();
  private final int versionCode;
  private String zze;
  private String zzf;
  private String zzg;
  private String zzh;
  private Uri zzi;
  private String zzj;
  private long zzk;
  private String zzl;
  private List<Scope> zzm;
  private String zzn;
  private String zzo;
  private Set<Scope> zzp = new HashSet();
  
  GoogleSignInAccount(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, Uri paramUri, String paramString5, long paramLong, String paramString6, List<Scope> paramList, String paramString7, String paramString8)
  {
    this.versionCode = paramInt;
    this.zze = paramString1;
    this.zzf = paramString2;
    this.zzg = paramString3;
    this.zzh = paramString4;
    this.zzi = paramUri;
    this.zzj = paramString5;
    this.zzk = paramLong;
    this.zzl = paramString6;
    this.zzm = paramList;
    this.zzn = paramString7;
    this.zzo = paramString8;
  }
  
  public static GoogleSignInAccount create(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, Uri paramUri, Long paramLong, String paramString7, Set<Scope> paramSet)
  {
    Long localLong = paramLong;
    if (paramLong == null) {
      localLong = Long.valueOf(sClock.currentTimeMillis() / 1000L);
    }
    return new GoogleSignInAccount(3, paramString1, paramString2, paramString3, paramString4, paramUri, null, localLong.longValue(), Preconditions.checkNotEmpty(paramString7), new ArrayList((Collection)Preconditions.checkNotNull(paramSet)), paramString5, paramString6);
  }
  
  public static GoogleSignInAccount fromJsonString(String paramString)
    throws JSONException
  {
    JSONObject localJSONObject = null;
    if (TextUtils.isEmpty(paramString))
    {
      paramString = localJSONObject;
      return paramString;
    }
    localJSONObject = new JSONObject(paramString);
    paramString = localJSONObject.optString("photoUrl", null);
    if (!TextUtils.isEmpty(paramString)) {}
    for (paramString = Uri.parse(paramString);; paramString = null)
    {
      long l = Long.parseLong(localJSONObject.getString("expirationTime"));
      HashSet localHashSet = new HashSet();
      JSONArray localJSONArray = localJSONObject.getJSONArray("grantedScopes");
      int i = localJSONArray.length();
      for (int j = 0; j < i; j++) {
        localHashSet.add(new Scope(localJSONArray.getString(j)));
      }
      paramString = create(localJSONObject.optString("id"), localJSONObject.optString("tokenId", null), localJSONObject.optString("email", null), localJSONObject.optString("displayName", null), localJSONObject.optString("givenName", null), localJSONObject.optString("familyName", null), paramString, Long.valueOf(l), localJSONObject.getString("obfuscatedIdentifier"), localHashSet).setServerAuthCode(localJSONObject.optString("serverAuthCode", null));
      break;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof GoogleSignInAccount))
      {
        bool = false;
      }
      else
      {
        paramObject = (GoogleSignInAccount)paramObject;
        if ((!((GoogleSignInAccount)paramObject).getObfuscatedIdentifier().equals(getObfuscatedIdentifier())) || (!((GoogleSignInAccount)paramObject).getRequestedScopes().equals(getRequestedScopes()))) {
          bool = false;
        }
      }
    }
  }
  
  public Account getAccount()
  {
    if (this.zzg == null) {}
    for (Account localAccount = null;; localAccount = new Account(this.zzg, "com.google")) {
      return localAccount;
    }
  }
  
  public String getDisplayName()
  {
    return this.zzh;
  }
  
  public String getEmail()
  {
    return this.zzg;
  }
  
  public long getExpirationTimeSecs()
  {
    return this.zzk;
  }
  
  public String getFamilyName()
  {
    return this.zzo;
  }
  
  public String getGivenName()
  {
    return this.zzn;
  }
  
  public String getId()
  {
    return this.zze;
  }
  
  public String getIdToken()
  {
    return this.zzf;
  }
  
  public String getObfuscatedIdentifier()
  {
    return this.zzl;
  }
  
  public Uri getPhotoUrl()
  {
    return this.zzi;
  }
  
  public Set<Scope> getRequestedScopes()
  {
    HashSet localHashSet = new HashSet(this.zzm);
    localHashSet.addAll(this.zzp);
    return localHashSet;
  }
  
  public String getServerAuthCode()
  {
    return this.zzj;
  }
  
  public int hashCode()
  {
    return (getObfuscatedIdentifier().hashCode() + 527) * 31 + getRequestedScopes().hashCode();
  }
  
  public GoogleSignInAccount setServerAuthCode(String paramString)
  {
    this.zzj = paramString;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeInt(paramParcel, 1, this.versionCode);
    SafeParcelWriter.writeString(paramParcel, 2, getId(), false);
    SafeParcelWriter.writeString(paramParcel, 3, getIdToken(), false);
    SafeParcelWriter.writeString(paramParcel, 4, getEmail(), false);
    SafeParcelWriter.writeString(paramParcel, 5, getDisplayName(), false);
    SafeParcelWriter.writeParcelable(paramParcel, 6, getPhotoUrl(), paramInt, false);
    SafeParcelWriter.writeString(paramParcel, 7, getServerAuthCode(), false);
    SafeParcelWriter.writeLong(paramParcel, 8, getExpirationTimeSecs());
    SafeParcelWriter.writeString(paramParcel, 9, getObfuscatedIdentifier(), false);
    SafeParcelWriter.writeTypedList(paramParcel, 10, this.zzm, false);
    SafeParcelWriter.writeString(paramParcel, 11, getGivenName(), false);
    SafeParcelWriter.writeString(paramParcel, 12, getFamilyName(), false);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/GoogleSignInAccount.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */