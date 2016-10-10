package com.google.android.gms.auth.api.signin;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.internal.zze;
import com.google.android.gms.common.api.Api.ApiOptions.Optional;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleSignInOptions
  extends AbstractSafeParcelable
  implements Api.ApiOptions.Optional, ReflectedParcelable
{
  public static final Parcelable.Creator<GoogleSignInOptions> CREATOR = new zzb();
  public static final GoogleSignInOptions DEFAULT_SIGN_IN;
  private static Comparator<Scope> hc = new Comparator()
  {
    public int zza(Scope paramAnonymousScope1, Scope paramAnonymousScope2)
    {
      return paramAnonymousScope1.zzaqg().compareTo(paramAnonymousScope2.zzaqg());
    }
  };
  public static final Scope hd = new Scope("profile");
  public static final Scope he = new Scope("email");
  public static final Scope hf = new Scope("openid");
  private Account ec;
  private final ArrayList<Scope> hg;
  private boolean hh;
  private final boolean hi;
  private final boolean hj;
  private String hk;
  private String hl;
  final int versionCode;
  
  static
  {
    DEFAULT_SIGN_IN = new Builder().requestId().requestProfile().build();
  }
  
  GoogleSignInOptions(int paramInt, ArrayList<Scope> paramArrayList, Account paramAccount, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString1, String paramString2)
  {
    this.versionCode = paramInt;
    this.hg = paramArrayList;
    this.ec = paramAccount;
    this.hh = paramBoolean1;
    this.hi = paramBoolean2;
    this.hj = paramBoolean3;
    this.hk = paramString1;
    this.hl = paramString2;
  }
  
  private GoogleSignInOptions(Set<Scope> paramSet, Account paramAccount, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString1, String paramString2)
  {
    this(2, new ArrayList(paramSet), paramAccount, paramBoolean1, paramBoolean2, paramBoolean3, paramString1, paramString2);
  }
  
  private JSONObject zzahi()
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      JSONArray localJSONArray = new JSONArray();
      Collections.sort(this.hg, hc);
      Iterator localIterator = this.hg.iterator();
      while (localIterator.hasNext()) {
        localJSONArray.put(((Scope)localIterator.next()).zzaqg());
      }
      localJSONException.put("scopes", localJSONArray);
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
    if (this.ec != null) {
      localJSONException.put("accountName", this.ec.name);
    }
    localJSONException.put("idTokenRequested", this.hh);
    localJSONException.put("forceCodeForRefreshToken", this.hj);
    localJSONException.put("serverAuthRequested", this.hi);
    if (!TextUtils.isEmpty(this.hk)) {
      localJSONException.put("serverClientId", this.hk);
    }
    if (!TextUtils.isEmpty(this.hl)) {
      localJSONException.put("hostedDomain", this.hl);
    }
    return localJSONException;
  }
  
  @Nullable
  public static GoogleSignInOptions zzfy(@Nullable String paramString)
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
      return new GoogleSignInOptions(localHashSet, paramString, localJSONObject.getBoolean("idTokenRequested"), localJSONObject.getBoolean("serverAuthRequested"), localJSONObject.getBoolean("forceCodeForRefreshToken"), localJSONObject.optString("serverClientId", null), localJSONObject.optString("hostedDomain", null));
    }
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
        if ((this.hg.size() != ((GoogleSignInOptions)paramObject).zzahj().size()) || (!this.hg.containsAll(((GoogleSignInOptions)paramObject).zzahj()))) {
          continue;
        }
        if (this.ec == null)
        {
          if (((GoogleSignInOptions)paramObject).getAccount() != null) {
            continue;
          }
          label56:
          if (!TextUtils.isEmpty(this.hk)) {
            break label128;
          }
          if (!TextUtils.isEmpty(((GoogleSignInOptions)paramObject).zzahn())) {
            continue;
          }
        }
        while ((this.hj == ((GoogleSignInOptions)paramObject).zzahm()) && (this.hh == ((GoogleSignInOptions)paramObject).zzahk()) && (this.hi == ((GoogleSignInOptions)paramObject).zzahl()))
        {
          return true;
          if (!this.ec.equals(((GoogleSignInOptions)paramObject).getAccount())) {
            break;
          }
          break label56;
          label128:
          boolean bool = this.hk.equals(((GoogleSignInOptions)paramObject).zzahn());
          if (!bool) {
            break;
          }
        }
        return false;
      }
      catch (ClassCastException paramObject) {}
    }
  }
  
  public Account getAccount()
  {
    return this.ec;
  }
  
  public Scope[] getScopeArray()
  {
    return (Scope[])this.hg.toArray(new Scope[this.hg.size()]);
  }
  
  public int hashCode()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.hg.iterator();
    while (localIterator.hasNext()) {
      localArrayList.add(((Scope)localIterator.next()).zzaqg());
    }
    Collections.sort(localArrayList);
    return new zze().zzq(localArrayList).zzq(this.ec).zzq(this.hk).zzbd(this.hj).zzbd(this.hh).zzbd(this.hi).zzahv();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzb.zza(this, paramParcel, paramInt);
  }
  
  public String zzahg()
  {
    return zzahi().toString();
  }
  
  public ArrayList<Scope> zzahj()
  {
    return new ArrayList(this.hg);
  }
  
  public boolean zzahk()
  {
    return this.hh;
  }
  
  public boolean zzahl()
  {
    return this.hi;
  }
  
  public boolean zzahm()
  {
    return this.hj;
  }
  
  public String zzahn()
  {
    return this.hk;
  }
  
  public String zzaho()
  {
    return this.hl;
  }
  
  public static final class Builder
  {
    private Account ec;
    private boolean hh;
    private boolean hi;
    private boolean hj;
    private String hk;
    private String hl;
    private Set<Scope> hm = new HashSet();
    
    public Builder() {}
    
    public Builder(@NonNull GoogleSignInOptions paramGoogleSignInOptions)
    {
      zzac.zzy(paramGoogleSignInOptions);
      this.hm = new HashSet(GoogleSignInOptions.zzb(paramGoogleSignInOptions));
      this.hi = GoogleSignInOptions.zzc(paramGoogleSignInOptions);
      this.hj = GoogleSignInOptions.zzd(paramGoogleSignInOptions);
      this.hh = GoogleSignInOptions.zze(paramGoogleSignInOptions);
      this.hk = GoogleSignInOptions.zzf(paramGoogleSignInOptions);
      this.ec = GoogleSignInOptions.zzg(paramGoogleSignInOptions);
      this.hl = GoogleSignInOptions.zzh(paramGoogleSignInOptions);
    }
    
    private String zzfz(String paramString)
    {
      zzac.zzhz(paramString);
      if ((this.hk == null) || (this.hk.equals(paramString))) {}
      for (boolean bool = true;; bool = false)
      {
        zzac.zzb(bool, "two different server client ids provided");
        return paramString;
      }
    }
    
    public GoogleSignInOptions build()
    {
      if ((this.hh) && ((this.ec == null) || (!this.hm.isEmpty()))) {
        requestId();
      }
      return new GoogleSignInOptions(this.hm, this.ec, this.hh, this.hi, this.hj, this.hk, this.hl, null);
    }
    
    public Builder requestEmail()
    {
      this.hm.add(GoogleSignInOptions.he);
      return this;
    }
    
    public Builder requestId()
    {
      this.hm.add(GoogleSignInOptions.hf);
      return this;
    }
    
    public Builder requestIdToken(String paramString)
    {
      this.hh = true;
      this.hk = zzfz(paramString);
      return this;
    }
    
    public Builder requestProfile()
    {
      this.hm.add(GoogleSignInOptions.hd);
      return this;
    }
    
    public Builder requestScopes(Scope paramScope, Scope... paramVarArgs)
    {
      this.hm.add(paramScope);
      this.hm.addAll(Arrays.asList(paramVarArgs));
      return this;
    }
    
    public Builder requestServerAuthCode(String paramString)
    {
      return requestServerAuthCode(paramString, false);
    }
    
    public Builder requestServerAuthCode(String paramString, boolean paramBoolean)
    {
      this.hi = true;
      this.hk = zzfz(paramString);
      this.hj = paramBoolean;
      return this;
    }
    
    public Builder setAccountName(String paramString)
    {
      this.ec = new Account(zzac.zzhz(paramString), "com.google");
      return this;
    }
    
    public Builder setHostedDomain(String paramString)
    {
      this.hl = zzac.zzhz(paramString);
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/auth/api/signin/GoogleSignInOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */