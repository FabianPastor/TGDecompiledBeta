package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.internal.zzn;
import com.google.android.gms.auth.api.signin.internal.zzo;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzbo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSignInOptions
  extends zza
  implements Api.ApiOptions.Optional, ReflectedParcelable
{
  public static final Parcelable.Creator<GoogleSignInOptions> CREATOR = new zzd();
  public static final GoogleSignInOptions DEFAULT_GAMES_SIGN_IN;
  public static final GoogleSignInOptions DEFAULT_SIGN_IN;
  private static Scope SCOPE_GAMES;
  private static Comparator<Scope> zzalU = new zzc();
  public static final Scope zzalV = new Scope("profile");
  public static final Scope zzalW = new Scope("email");
  public static final Scope zzalX = new Scope("openid");
  private int versionCode;
  private Account zzajb;
  private final ArrayList<Scope> zzalY;
  private final boolean zzalZ;
  private boolean zzalh;
  private String zzali;
  private final boolean zzama;
  private String zzamb;
  private ArrayList<zzn> zzamc;
  private Map<Integer, zzn> zzamd;
  
  static
  {
    SCOPE_GAMES = new Scope("https://www.googleapis.com/auth/games");
    DEFAULT_SIGN_IN = new Builder().requestId().requestProfile().build();
    DEFAULT_GAMES_SIGN_IN = new Builder().requestScopes(SCOPE_GAMES, new Scope[0]).build();
  }
  
  GoogleSignInOptions(int paramInt, ArrayList<Scope> paramArrayList, Account paramAccount, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString1, String paramString2, ArrayList<zzn> paramArrayList1)
  {
    this(paramInt, paramArrayList, paramAccount, paramBoolean1, paramBoolean2, paramBoolean3, paramString1, paramString2, zzw(paramArrayList1));
  }
  
  private GoogleSignInOptions(int paramInt, ArrayList<Scope> paramArrayList, Account paramAccount, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString1, String paramString2, Map<Integer, zzn> paramMap)
  {
    this.versionCode = paramInt;
    this.zzalY = paramArrayList;
    this.zzajb = paramAccount;
    this.zzalh = paramBoolean1;
    this.zzalZ = paramBoolean2;
    this.zzama = paramBoolean3;
    this.zzali = paramString1;
    this.zzamb = paramString2;
    this.zzamc = new ArrayList(paramMap.values());
    this.zzamd = paramMap;
  }
  
  @Nullable
  public static GoogleSignInOptions zzbQ(@Nullable String paramString)
    throws JSONException
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    JSONObject localJSONObject = new JSONObject(paramString);
    HashSet localHashSet = new HashSet();
    paramString = localJSONObject.getJSONArray("scopes");
    int j = paramString.length();
    int i = 0;
    while (i < j)
    {
      localHashSet.add(new Scope(paramString.getString(i)));
      i += 1;
    }
    paramString = localJSONObject.optString("accountName", null);
    if (!TextUtils.isEmpty(paramString)) {}
    for (paramString = new Account(paramString, "com.google");; paramString = null) {
      return new GoogleSignInOptions(3, new ArrayList(localHashSet), paramString, localJSONObject.getBoolean("idTokenRequested"), localJSONObject.getBoolean("serverAuthRequested"), localJSONObject.getBoolean("forceCodeForRefreshToken"), localJSONObject.optString("serverClientId", null), localJSONObject.optString("hostedDomain", null), new HashMap());
    }
  }
  
  private final JSONObject zzmz()
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      JSONArray localJSONArray = new JSONArray();
      Collections.sort(this.zzalY, zzalU);
      ArrayList localArrayList = (ArrayList)this.zzalY;
      int j = localArrayList.size();
      int i = 0;
      while (i < j)
      {
        Object localObject = localArrayList.get(i);
        i += 1;
        localJSONArray.put(((Scope)localObject).zzpp());
      }
      localJSONException.put("scopes", localJSONArray);
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
    if (this.zzajb != null) {
      localJSONException.put("accountName", this.zzajb.name);
    }
    localJSONException.put("idTokenRequested", this.zzalh);
    localJSONException.put("forceCodeForRefreshToken", this.zzama);
    localJSONException.put("serverAuthRequested", this.zzalZ);
    if (!TextUtils.isEmpty(this.zzali)) {
      localJSONException.put("serverClientId", this.zzali);
    }
    if (!TextUtils.isEmpty(this.zzamb)) {
      localJSONException.put("hostedDomain", this.zzamb);
    }
    return localJSONException;
  }
  
  private static Map<Integer, zzn> zzw(@Nullable List<zzn> paramList)
  {
    HashMap localHashMap = new HashMap();
    if (paramList == null) {
      return localHashMap;
    }
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      zzn localzzn = (zzn)paramList.next();
      localHashMap.put(Integer.valueOf(localzzn.getType()), localzzn);
    }
    return localHashMap;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {}
    for (;;)
    {
      return false;
      try
      {
        paramObject = (GoogleSignInOptions)paramObject;
        if ((this.zzamc.size() > 0) || (((GoogleSignInOptions)paramObject).zzamc.size() > 0) || (this.zzalY.size() != ((GoogleSignInOptions)paramObject).zzmA().size()) || (!this.zzalY.containsAll(((GoogleSignInOptions)paramObject).zzmA()))) {
          continue;
        }
        if (this.zzajb == null)
        {
          if (((GoogleSignInOptions)paramObject).zzajb != null) {
            continue;
          }
          label76:
          if (!TextUtils.isEmpty(this.zzali)) {
            break label148;
          }
          if (!TextUtils.isEmpty(((GoogleSignInOptions)paramObject).zzali)) {
            continue;
          }
        }
        while ((this.zzama == ((GoogleSignInOptions)paramObject).zzama) && (this.zzalh == ((GoogleSignInOptions)paramObject).zzalh) && (this.zzalZ == ((GoogleSignInOptions)paramObject).zzalZ))
        {
          return true;
          if (!this.zzajb.equals(((GoogleSignInOptions)paramObject).zzajb)) {
            break;
          }
          break label76;
          label148:
          boolean bool = this.zzali.equals(((GoogleSignInOptions)paramObject).zzali);
          if (!bool) {
            break;
          }
        }
        return false;
      }
      catch (ClassCastException paramObject) {}
    }
  }
  
  public final Account getAccount()
  {
    return this.zzajb;
  }
  
  public Scope[] getScopeArray()
  {
    return (Scope[])this.zzalY.toArray(new Scope[this.zzalY.size()]);
  }
  
  public final String getServerClientId()
  {
    return this.zzali;
  }
  
  public int hashCode()
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = (ArrayList)this.zzalY;
    int j = localArrayList2.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = localArrayList2.get(i);
      i += 1;
      localArrayList1.add(((Scope)localObject).zzpp());
    }
    Collections.sort(localArrayList1);
    return new zzo().zzo(localArrayList1).zzo(this.zzajb).zzo(this.zzali).zzP(this.zzama).zzP(this.zzalh).zzP(this.zzalZ).zzmJ();
  }
  
  public final boolean isIdTokenRequested()
  {
    return this.zzalh;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = com.google.android.gms.common.internal.safeparcel.zzd.zze(paramParcel);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 1, this.versionCode);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 2, zzmA(), false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 3, this.zzajb, paramInt, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 4, this.zzalh);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 5, this.zzalZ);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 6, this.zzama);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 7, this.zzali, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zza(paramParcel, 8, this.zzamb, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zzc(paramParcel, 9, this.zzamc, false);
    com.google.android.gms.common.internal.safeparcel.zzd.zzI(paramParcel, i);
  }
  
  public final ArrayList<Scope> zzmA()
  {
    return new ArrayList(this.zzalY);
  }
  
  public final boolean zzmB()
  {
    return this.zzalZ;
  }
  
  public final String zzmC()
  {
    return zzmz().toString();
  }
  
  public static final class Builder
  {
    private Account zzajb;
    private boolean zzalZ;
    private boolean zzalh;
    private String zzali;
    private boolean zzama;
    private String zzamb;
    private Set<Scope> zzame = new HashSet();
    private Map<Integer, zzn> zzamf = new HashMap();
    
    public Builder() {}
    
    public Builder(@NonNull GoogleSignInOptions paramGoogleSignInOptions)
    {
      zzbo.zzu(paramGoogleSignInOptions);
      this.zzame = new HashSet(GoogleSignInOptions.zza(paramGoogleSignInOptions));
      this.zzalZ = GoogleSignInOptions.zzb(paramGoogleSignInOptions);
      this.zzama = GoogleSignInOptions.zzc(paramGoogleSignInOptions);
      this.zzalh = GoogleSignInOptions.zzd(paramGoogleSignInOptions);
      this.zzali = GoogleSignInOptions.zze(paramGoogleSignInOptions);
      this.zzajb = GoogleSignInOptions.zzf(paramGoogleSignInOptions);
      this.zzamb = GoogleSignInOptions.zzg(paramGoogleSignInOptions);
      this.zzamf = GoogleSignInOptions.zzx(GoogleSignInOptions.zzh(paramGoogleSignInOptions));
    }
    
    private final String zzbR(String paramString)
    {
      zzbo.zzcF(paramString);
      if ((this.zzali == null) || (this.zzali.equals(paramString))) {}
      for (boolean bool = true;; bool = false)
      {
        zzbo.zzb(bool, "two different server client ids provided");
        return paramString;
      }
    }
    
    public final Builder addExtension(GoogleSignInOptionsExtension paramGoogleSignInOptionsExtension)
    {
      if (this.zzamf.containsKey(Integer.valueOf(1))) {
        throw new IllegalStateException("Only one extension per type may be added");
      }
      this.zzamf.put(Integer.valueOf(1), new zzn(paramGoogleSignInOptionsExtension));
      return this;
    }
    
    public final GoogleSignInOptions build()
    {
      if ((this.zzalh) && ((this.zzajb == null) || (!this.zzame.isEmpty()))) {
        requestId();
      }
      return new GoogleSignInOptions(3, new ArrayList(this.zzame), this.zzajb, this.zzalh, this.zzalZ, this.zzama, this.zzali, this.zzamb, this.zzamf, null);
    }
    
    public final Builder requestEmail()
    {
      this.zzame.add(GoogleSignInOptions.zzalW);
      return this;
    }
    
    public final Builder requestId()
    {
      this.zzame.add(GoogleSignInOptions.zzalX);
      return this;
    }
    
    public final Builder requestIdToken(String paramString)
    {
      this.zzalh = true;
      this.zzali = zzbR(paramString);
      return this;
    }
    
    public final Builder requestProfile()
    {
      this.zzame.add(GoogleSignInOptions.zzalV);
      return this;
    }
    
    public final Builder requestScopes(Scope paramScope, Scope... paramVarArgs)
    {
      this.zzame.add(paramScope);
      this.zzame.addAll(Arrays.asList(paramVarArgs));
      return this;
    }
    
    public final Builder requestServerAuthCode(String paramString)
    {
      return requestServerAuthCode(paramString, false);
    }
    
    public final Builder requestServerAuthCode(String paramString, boolean paramBoolean)
    {
      this.zzalZ = true;
      this.zzali = zzbR(paramString);
      this.zzama = paramBoolean;
      return this;
    }
    
    public final Builder setAccountName(String paramString)
    {
      this.zzajb = new Account(zzbo.zzcF(paramString), "com.google");
      return this;
    }
    
    public final Builder setHostedDomain(String paramString)
    {
      this.zzamb = zzbo.zzcF(paramString);
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/GoogleSignInOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */