package com.google.android.gms.wallet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import com.google.android.gms.R.string;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.LifecycleDelegate;
import com.google.android.gms.dynamic.zza;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzo;
import com.google.android.gms.dynamic.zzr;
import com.google.android.gms.internal.ga;
import com.google.android.gms.internal.ge;
import com.google.android.gms.internal.gz;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public final class SupportWalletFragment
  extends Fragment
{
  private boolean mCreated = false;
  private final Fragment zzaSE = this;
  private zzb zzbPZ;
  private final zzr zzbQa = zzr.zza(this);
  private final zzc zzbQb = new zzc(null);
  private zza zzbQc = new zza(this);
  private WalletFragmentOptions zzbQd;
  private WalletFragmentInitParams zzbQe;
  private MaskedWalletRequest zzbQf;
  private MaskedWallet zzbQg;
  private Boolean zzbQh;
  
  public static SupportWalletFragment newInstance(WalletFragmentOptions paramWalletFragmentOptions)
  {
    SupportWalletFragment localSupportWalletFragment = new SupportWalletFragment();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("extraWalletFragmentOptions", paramWalletFragmentOptions);
    localSupportWalletFragment.zzaSE.setArguments(localBundle);
    return localSupportWalletFragment;
  }
  
  public final int getState()
  {
    if (this.zzbPZ != null) {
      return zzb.zza(this.zzbPZ);
    }
    return 0;
  }
  
  public final void initialize(WalletFragmentInitParams paramWalletFragmentInitParams)
  {
    if (this.zzbPZ != null)
    {
      zzb.zza(this.zzbPZ, paramWalletFragmentInitParams);
      this.zzbQe = null;
    }
    do
    {
      return;
      if (this.zzbQe != null) {
        break;
      }
      this.zzbQe = paramWalletFragmentInitParams;
      if (this.zzbQf != null) {
        Log.w("SupportWalletFragment", "updateMaskedWalletRequest() was called before initialize()");
      }
    } while (this.zzbQg == null);
    Log.w("SupportWalletFragment", "updateMaskedWallet() was called before initialize()");
    return;
    Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (this.zzbPZ != null) {
      zzb.zza(this.zzbPZ, paramInt1, paramInt2, paramIntent);
    }
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject;
    if (paramBundle != null)
    {
      paramBundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
      localObject = (WalletFragmentInitParams)paramBundle.getParcelable("walletFragmentInitParams");
      if (localObject != null)
      {
        if (this.zzbQe != null) {
          Log.w("SupportWalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
        }
        this.zzbQe = ((WalletFragmentInitParams)localObject);
      }
      if (this.zzbQf == null) {
        this.zzbQf = ((MaskedWalletRequest)paramBundle.getParcelable("maskedWalletRequest"));
      }
      if (this.zzbQg == null) {
        this.zzbQg = ((MaskedWallet)paramBundle.getParcelable("maskedWallet"));
      }
      if (paramBundle.containsKey("walletFragmentOptions")) {
        this.zzbQd = ((WalletFragmentOptions)paramBundle.getParcelable("walletFragmentOptions"));
      }
      if (paramBundle.containsKey("enabled")) {
        this.zzbQh = Boolean.valueOf(paramBundle.getBoolean("enabled"));
      }
    }
    for (;;)
    {
      this.mCreated = true;
      this.zzbQb.onCreate(paramBundle);
      return;
      if (this.zzaSE.getArguments() != null)
      {
        localObject = (WalletFragmentOptions)this.zzaSE.getArguments().getParcelable("extraWalletFragmentOptions");
        if (localObject != null)
        {
          ((WalletFragmentOptions)localObject).zzby(this.zzaSE.getActivity());
          this.zzbQd = ((WalletFragmentOptions)localObject);
        }
      }
    }
  }
  
  public final View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return this.zzbQb.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  public final void onDestroy()
  {
    super.onDestroy();
    this.mCreated = false;
  }
  
  public final void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    super.onInflate(paramActivity, paramAttributeSet, paramBundle);
    if (this.zzbQd == null) {
      this.zzbQd = WalletFragmentOptions.zza(paramActivity, paramAttributeSet);
    }
    paramAttributeSet = new Bundle();
    paramAttributeSet.putParcelable("attrKeyWalletFragmentOptions", this.zzbQd);
    this.zzbQb.onInflate(paramActivity, paramAttributeSet, paramBundle);
  }
  
  public final void onPause()
  {
    super.onPause();
    this.zzbQb.onPause();
  }
  
  public final void onResume()
  {
    super.onResume();
    this.zzbQb.onResume();
    FragmentManager localFragmentManager = this.zzaSE.getActivity().getSupportFragmentManager();
    Fragment localFragment = localFragmentManager.findFragmentByTag("GooglePlayServicesErrorDialog");
    if (localFragment != null)
    {
      localFragmentManager.beginTransaction().remove(localFragment).commit();
      GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.zzaSE.getActivity()), this.zzaSE.getActivity(), -1);
    }
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
    this.zzbQb.onSaveInstanceState(paramBundle);
    if (this.zzbQe != null)
    {
      paramBundle.putParcelable("walletFragmentInitParams", this.zzbQe);
      this.zzbQe = null;
    }
    if (this.zzbQf != null)
    {
      paramBundle.putParcelable("maskedWalletRequest", this.zzbQf);
      this.zzbQf = null;
    }
    if (this.zzbQg != null)
    {
      paramBundle.putParcelable("maskedWallet", this.zzbQg);
      this.zzbQg = null;
    }
    if (this.zzbQd != null)
    {
      paramBundle.putParcelable("walletFragmentOptions", this.zzbQd);
      this.zzbQd = null;
    }
    if (this.zzbQh != null)
    {
      paramBundle.putBoolean("enabled", this.zzbQh.booleanValue());
      this.zzbQh = null;
    }
  }
  
  public final void onStart()
  {
    super.onStart();
    this.zzbQb.onStart();
  }
  
  public final void onStop()
  {
    super.onStop();
    this.zzbQb.onStop();
  }
  
  public final void setEnabled(boolean paramBoolean)
  {
    if (this.zzbPZ != null)
    {
      zzb.zza(this.zzbPZ, paramBoolean);
      this.zzbQh = null;
      return;
    }
    this.zzbQh = Boolean.valueOf(paramBoolean);
  }
  
  public final void setOnStateChangedListener(OnStateChangedListener paramOnStateChangedListener)
  {
    this.zzbQc.zza(paramOnStateChangedListener);
  }
  
  public final void updateMaskedWallet(MaskedWallet paramMaskedWallet)
  {
    if (this.zzbPZ != null)
    {
      zzb.zza(this.zzbPZ, paramMaskedWallet);
      this.zzbQg = null;
      return;
    }
    this.zzbQg = paramMaskedWallet;
  }
  
  public final void updateMaskedWalletRequest(MaskedWalletRequest paramMaskedWalletRequest)
  {
    if (this.zzbPZ != null)
    {
      zzb.zza(this.zzbPZ, paramMaskedWalletRequest);
      this.zzbQf = null;
      return;
    }
    this.zzbQf = paramMaskedWalletRequest;
  }
  
  public static abstract interface OnStateChangedListener
  {
    public abstract void onStateChanged(SupportWalletFragment paramSupportWalletFragment, int paramInt1, int paramInt2, Bundle paramBundle);
  }
  
  static final class zza
    extends ge
  {
    private SupportWalletFragment.OnStateChangedListener zzbQi;
    private final SupportWalletFragment zzbQj;
    
    zza(SupportWalletFragment paramSupportWalletFragment)
    {
      this.zzbQj = paramSupportWalletFragment;
    }
    
    public final void zza(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      if (this.zzbQi != null) {
        this.zzbQi.onStateChanged(this.zzbQj, paramInt1, paramInt2, paramBundle);
      }
    }
    
    public final void zza(SupportWalletFragment.OnStateChangedListener paramOnStateChangedListener)
    {
      this.zzbQi = paramOnStateChangedListener;
    }
  }
  
  static final class zzb
    implements LifecycleDelegate
  {
    private final ga zzbQk;
    
    private zzb(ga paramga)
    {
      this.zzbQk = paramga;
    }
    
    private final int getState()
    {
      try
      {
        int i = this.zzbQk.getState();
        return i;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
    
    private final void initialize(WalletFragmentInitParams paramWalletFragmentInitParams)
    {
      try
      {
        this.zzbQk.initialize(paramWalletFragmentInitParams);
        return;
      }
      catch (RemoteException paramWalletFragmentInitParams)
      {
        throw new RuntimeException(paramWalletFragmentInitParams);
      }
    }
    
    private final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      try
      {
        this.zzbQk.onActivityResult(paramInt1, paramInt2, paramIntent);
        return;
      }
      catch (RemoteException paramIntent)
      {
        throw new RuntimeException(paramIntent);
      }
    }
    
    private final void setEnabled(boolean paramBoolean)
    {
      try
      {
        this.zzbQk.setEnabled(paramBoolean);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
    
    private final void updateMaskedWallet(MaskedWallet paramMaskedWallet)
    {
      try
      {
        this.zzbQk.updateMaskedWallet(paramMaskedWallet);
        return;
      }
      catch (RemoteException paramMaskedWallet)
      {
        throw new RuntimeException(paramMaskedWallet);
      }
    }
    
    private final void updateMaskedWalletRequest(MaskedWalletRequest paramMaskedWalletRequest)
    {
      try
      {
        this.zzbQk.updateMaskedWalletRequest(paramMaskedWalletRequest);
        return;
      }
      catch (RemoteException paramMaskedWalletRequest)
      {
        throw new RuntimeException(paramMaskedWalletRequest);
      }
    }
    
    public final void onCreate(Bundle paramBundle)
    {
      try
      {
        this.zzbQk.onCreate(paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeException(paramBundle);
      }
    }
    
    public final View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      try
      {
        paramLayoutInflater = (View)zzn.zzE(this.zzbQk.onCreateView(zzn.zzw(paramLayoutInflater), zzn.zzw(paramViewGroup), paramBundle));
        return paramLayoutInflater;
      }
      catch (RemoteException paramLayoutInflater)
      {
        throw new RuntimeException(paramLayoutInflater);
      }
    }
    
    public final void onDestroy() {}
    
    public final void onDestroyView() {}
    
    public final void onInflate(Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2)
    {
      paramBundle1 = (WalletFragmentOptions)paramBundle1.getParcelable("extraWalletFragmentOptions");
      try
      {
        this.zzbQk.zza(zzn.zzw(paramActivity), paramBundle1, paramBundle2);
        return;
      }
      catch (RemoteException paramActivity)
      {
        throw new RuntimeException(paramActivity);
      }
    }
    
    public final void onLowMemory() {}
    
    public final void onPause()
    {
      try
      {
        this.zzbQk.onPause();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
    
    public final void onResume()
    {
      try
      {
        this.zzbQk.onResume();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
    
    public final void onSaveInstanceState(Bundle paramBundle)
    {
      try
      {
        this.zzbQk.onSaveInstanceState(paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeException(paramBundle);
      }
    }
    
    public final void onStart()
    {
      try
      {
        this.zzbQk.onStart();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
    
    public final void onStop()
    {
      try
      {
        this.zzbQk.onStop();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
  }
  
  final class zzc
    extends zza<SupportWalletFragment.zzb>
    implements View.OnClickListener
  {
    private zzc() {}
    
    public final void onClick(View paramView)
    {
      paramView = SupportWalletFragment.zza(SupportWalletFragment.this).getActivity();
      GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(paramView), paramView, -1);
    }
    
    protected final void zza(FrameLayout paramFrameLayout)
    {
      int k = -1;
      int m = -2;
      Button localButton = new Button(SupportWalletFragment.zza(SupportWalletFragment.this).getActivity());
      localButton.setText(R.string.wallet_buy_button_place_holder);
      int j = m;
      int i = k;
      if (SupportWalletFragment.zze(SupportWalletFragment.this) != null)
      {
        WalletFragmentStyle localWalletFragmentStyle = SupportWalletFragment.zze(SupportWalletFragment.this).getFragmentStyle();
        j = m;
        i = k;
        if (localWalletFragmentStyle != null)
        {
          DisplayMetrics localDisplayMetrics = SupportWalletFragment.zza(SupportWalletFragment.this).getResources().getDisplayMetrics();
          i = localWalletFragmentStyle.zza("buyButtonWidth", localDisplayMetrics, -1);
          j = localWalletFragmentStyle.zza("buyButtonHeight", localDisplayMetrics, -2);
        }
      }
      localButton.setLayoutParams(new ViewGroup.LayoutParams(i, j));
      localButton.setOnClickListener(this);
      paramFrameLayout.addView(localButton);
    }
    
    protected final void zza(zzo<SupportWalletFragment.zzb> paramzzo)
    {
      Object localObject = SupportWalletFragment.zza(SupportWalletFragment.this).getActivity();
      if ((SupportWalletFragment.zzb(SupportWalletFragment.this) == null) && (SupportWalletFragment.zzc(SupportWalletFragment.this)) && (localObject != null)) {}
      try
      {
        localObject = gz.zza((Activity)localObject, SupportWalletFragment.zzd(SupportWalletFragment.this), SupportWalletFragment.zze(SupportWalletFragment.this), SupportWalletFragment.zzf(SupportWalletFragment.this));
        SupportWalletFragment.zza(SupportWalletFragment.this, new SupportWalletFragment.zzb((ga)localObject, null));
        SupportWalletFragment.zza(SupportWalletFragment.this, null);
        paramzzo.zza(SupportWalletFragment.zzb(SupportWalletFragment.this));
        if (SupportWalletFragment.zzg(SupportWalletFragment.this) != null)
        {
          SupportWalletFragment.zzb.zza(SupportWalletFragment.zzb(SupportWalletFragment.this), SupportWalletFragment.zzg(SupportWalletFragment.this));
          SupportWalletFragment.zza(SupportWalletFragment.this, null);
        }
        if (SupportWalletFragment.zzh(SupportWalletFragment.this) != null)
        {
          SupportWalletFragment.zzb.zza(SupportWalletFragment.zzb(SupportWalletFragment.this), SupportWalletFragment.zzh(SupportWalletFragment.this));
          SupportWalletFragment.zza(SupportWalletFragment.this, null);
        }
        if (SupportWalletFragment.zzi(SupportWalletFragment.this) != null)
        {
          SupportWalletFragment.zzb.zza(SupportWalletFragment.zzb(SupportWalletFragment.this), SupportWalletFragment.zzi(SupportWalletFragment.this));
          SupportWalletFragment.zza(SupportWalletFragment.this, null);
        }
        if (SupportWalletFragment.zzj(SupportWalletFragment.this) != null)
        {
          SupportWalletFragment.zzb.zza(SupportWalletFragment.zzb(SupportWalletFragment.this), SupportWalletFragment.zzj(SupportWalletFragment.this).booleanValue());
          SupportWalletFragment.zza(SupportWalletFragment.this, null);
        }
        return;
      }
      catch (GooglePlayServicesNotAvailableException paramzzo) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/fragment/SupportWalletFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */