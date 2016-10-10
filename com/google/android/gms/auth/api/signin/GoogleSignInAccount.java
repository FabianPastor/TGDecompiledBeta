package com.google.android.gms.auth.api.signin;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzh;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSignInAccount
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<GoogleSignInAccount> CREATOR = new zza();
  public static zze gT = zzh.zzaxj();
  private static Comparator<Scope> hc = new Comparator()
  {
    public int zza(Scope paramAnonymousScope1, Scope paramAnonymousScope2)
    {
      return paramAnonymousScope1.zzaqg().compareTo(paramAnonymousScope2.zzaqg());
    }
  };
  List<Scope> fK;
  private String gU;
  private String gV;
  private Uri gW;
  private String gX;
  private long gY;
  private String gZ;
  private String gs;
  private String ha;
  private String hb;
  final int versionCode;
  private String zzbks;
  
  GoogleSignInAccount(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4, Uri paramUri, String paramString5, long paramLong, String paramString6, List<Scope> paramList, String paramString7, String paramString8)
  {
    this.versionCode = paramInt;
    this.zzbks = paramString1;
    this.gs = paramString2;
    this.gU = paramString3;
    this.gV = paramString4;
    this.gW = paramUri;
    this.gX = paramString5;
    this.gY = paramLong;
    this.gZ = paramString6;
    this.fK = paramList;
    this.ha = paramString7;
    this.hb = paramString8;
  }
  
  public static GoogleSignInAccount zza(@Nullable String paramString1, @Nullable String paramString2, @Nullable String paramString3, @Nullable String paramString4, @Nullable String paramString5, @Nullable String paramString6, @Nullable Uri paramUri, @Nullable Long paramLong, @NonNull String paramString7, @NonNull Set<Scope> paramSet)
  {
    Long localLong = paramLong;
    if (paramLong == null) {
      localLong = Long.valueOf(gT.currentTimeMillis() / 1000L);
    }
    return new GoogleSignInAccount(3, paramString1, paramString2, paramString3, paramString4, paramUri, null, localLong.longValue(), zzac.zzhz(paramString7), new ArrayList((Collection)zzac.zzy(paramSet)), paramString5, paramString6);
  }
  
  private JSONObject zzahi()
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      if (getId() != null) {
        localJSONObject.put("id", getId());
      }
      if (getIdToken() != null) {
        localJSONObject.put("tokenId", getIdToken());
      }
      if (getEmail() != null) {
        localJSONObject.put("email", getEmail());
      }
      if (getDisplayName() != null) {
        localJSONObject.put("displayName", getDisplayName());
      }
      if (getGivenName() != null) {
        localJSONObject.put("givenName", getGivenName());
      }
      if (getFamilyName() != null) {
        localJSONObject.put("familyName", getFamilyName());
      }
      if (getPhotoUrl() != null) {
        localJSONObject.put("photoUrl", getPhotoUrl().toString());
      }
      if (getServerAuthCode() != null) {
        localJSONObject.put("serverAuthCode", getServerAuthCode());
      }
      localJSONObject.put("expirationTime", this.gY);
      localJSONObject.put("obfuscatedIdentifier", zzahf());
      JSONArray localJSONArray = new JSONArray();
      Collections.sort(this.fK, hc);
      Iterator localIterator = this.fK.iterator();
      while (localIterator.hasNext()) {
        localJSONArray.put(((Scope)localIterator.next()).zzaqg());
      }
      localJSONException.put("grantedScopes", localJSONArray);
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
    return localJSONException;
  }
  
  @Nullable
  public static GoogleSignInAccount zzfw(@Nullable String paramString)
    throws JSONException
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    JSONObject localJSONObject = new JSONObject(paramString);
    paramString = localJSONObject.optString("photoUrl", null);
    if (!TextUtils.isEmpty(paramString)) {}
    for (paramString = Uri.parse(paramString);; paramString = null)
    {
      long l = Long.parseLong(localJSONObject.getString("expirationTime"));
      HashSet localHashSet = new HashSet();
      JSONArray localJSONArray = localJSONObject.getJSONArray("grantedScopes");
      int j = localJSONArray.length();
      int i = 0;
      while (i < j)
      {
        localHashSet.add(new Scope(localJSONArray.getString(i)));
        i += 1;
      }
      return zza(localJSONObject.optString("id"), localJSONObject.optString("tokenId", null), localJSONObject.optString("email", null), localJSONObject.optString("displayName", null), localJSONObject.optString("givenName", null), localJSONObject.optString("familyName", null), paramString, Long.valueOf(l), localJSONObject.getString("obfuscatedIdentifier"), localHashSet).zzfx(localJSONObject.optString("serverAuthCode", null));
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof GoogleSignInAccount)) {
      return false;
    }
    return ((GoogleSignInAccount)paramObject).zzahg().equals(zzahg());
  }
  
  @Nullable
  public String getDisplayName()
  {
    return this.gV;
  }
  
  @Nullable
  public String getEmail()
  {
    return this.gU;
  }
  
  @Nullable
  public String getFamilyName()
  {
    return this.hb;
  }
  
  @Nullable
  public String getGivenName()
  {
    return this.ha;
  }
  
  @NonNull
  public Set<Scope> getGrantedScopes()
  {
    return new HashSet(this.fK);
  }
  
  @Nullable
  public String getId()
  {
    return this.zzbks;
  }
  
  @Nullable
  public String getIdToken()
  {
    return this.gs;
  }
  
  @Nullable
  public Uri getPhotoUrl()
  {
    return this.gW;
  }
  
  @Nullable
  public String getServerAuthCode()
  {
    return this.gX;
  }
  
  public int hashCode()
  {
    return zzahg().hashCode();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zza.zza(this, paramParcel, paramInt);
  }
  
  public boolean zza()
  {
    return gT.currentTimeMillis() / 1000L >= this.gY - 300L;
  }
  
  public long zzahe()
  {
    return this.gY;
  }
  
  @NonNull
  public String zzahf()
  {
    return this.gZ;
  }
  
  public String zzahg()
  {
    return zzahi().toString();
  }
  
  public String zzahh()
  {
    JSONObject localJSONObject = zzahi();
    localJSONObject.remove("serverAuthCode");
    return localJSONObject.toString();
  }
  
  public GoogleSignInAccount zzfx(String paramString)
  {
    this.gX = paramString;
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/GoogleSignInAccount.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */