package com.google.android.gms.wallet.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.DeferredLifecycleHelper;
import com.google.android.gms.dynamic.FragmentWrapper;
import com.google.android.gms.dynamic.LifecycleDelegate;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamic.OnDelegateCreatedListener;
import com.google.android.gms.internal.wallet.zzak;
import com.google.android.gms.internal.wallet.zzl;
import com.google.android.gms.internal.wallet.zzp;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.R.string;

@TargetApi(12)
public final class WalletFragment
  extends Fragment
{
  private boolean zzfc = false;
  private WalletFragmentOptions zzfg;
  private WalletFragmentInitParams zzfh;
  private MaskedWalletRequest zzfi;
  private MaskedWallet zzfj;
  private Boolean zzfk;
  private zzb zzfp;
  private final FragmentWrapper zzfq = FragmentWrapper.wrap(this);
  private final zzc zzfr = new zzc(null);
  private zza zzfs = new zza(this);
  private final Fragment zzft = this;
  
  public static WalletFragment newInstance(WalletFragmentOptions paramWalletFragmentOptions)
  {
    WalletFragment localWalletFragment = new WalletFragment();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("extraWalletFragmentOptions", paramWalletFragmentOptions);
    localWalletFragment.zzft.setArguments(localBundle);
    return localWalletFragment;
  }
  
  public final void initialize(WalletFragmentInitParams paramWalletFragmentInitParams)
  {
    if (this.zzfp != null)
    {
      zzb.zza(this.zzfp, paramWalletFragmentInitParams);
      this.zzfh = null;
    }
    for (;;)
    {
      return;
      if (this.zzfh == null)
      {
        this.zzfh = paramWalletFragmentInitParams;
        if (this.zzfi != null) {
          Log.w("WalletFragment", "updateMaskedWalletRequest() was called before initialize()");
        }
        if (this.zzfj != null) {
          Log.w("WalletFragment", "updateMaskedWallet() was called before initialize()");
        }
      }
      else
      {
        Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once. Ignoring.");
      }
    }
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (this.zzfp != null) {
      zzb.zza(this.zzfp, paramInt1, paramInt2, paramIntent);
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
        if (this.zzfh != null) {
          Log.w("WalletFragment", "initialize(WalletFragmentInitParams) was called more than once.Ignoring.");
        }
        this.zzfh = ((WalletFragmentInitParams)localObject);
      }
      if (this.zzfi == null) {
        this.zzfi = ((MaskedWalletRequest)paramBundle.getParcelable("maskedWalletRequest"));
      }
      if (this.zzfj == null) {
        this.zzfj = ((MaskedWallet)paramBundle.getParcelable("maskedWallet"));
      }
      if (paramBundle.containsKey("walletFragmentOptions")) {
        this.zzfg = ((WalletFragmentOptions)paramBundle.getParcelable("walletFragmentOptions"));
      }
      if (paramBundle.containsKey("enabled")) {
        this.zzfk = Boolean.valueOf(paramBundle.getBoolean("enabled"));
      }
    }
    for (;;)
    {
      this.zzfc = true;
      this.zzfr.onCreate(paramBundle);
      return;
      if (this.zzft.getArguments() != null)
      {
        localObject = (WalletFragmentOptions)this.zzft.getArguments().getParcelable("extraWalletFragmentOptions");
        if (localObject != null)
        {
          ((WalletFragmentOptions)localObject).zza(this.zzft.getActivity());
          this.zzfg = ((WalletFragmentOptions)localObject);
        }
      }
    }
  }
  
  public final View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return this.zzfr.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  public final void onDestroy()
  {
    super.onDestroy();
    this.zzfc = false;
  }
  
  public final void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    super.onInflate(paramActivity, paramAttributeSet, paramBundle);
    if (this.zzfg == null) {
      this.zzfg = WalletFragmentOptions.zza(paramActivity, paramAttributeSet);
    }
    paramAttributeSet = new Bundle();
    paramAttributeSet.putParcelable("attrKeyWalletFragmentOptions", this.zzfg);
    this.zzfr.onInflate(paramActivity, paramAttributeSet, paramBundle);
  }
  
  public final void onPause()
  {
    super.onPause();
    this.zzfr.onPause();
  }
  
  public final void onResume()
  {
    super.onResume();
    this.zzfr.onResume();
    FragmentManager localFragmentManager = this.zzft.getActivity().getFragmentManager();
    Fragment localFragment = localFragmentManager.findFragmentByTag("GooglePlayServicesErrorDialog");
    if (localFragment != null)
    {
      localFragmentManager.beginTransaction().remove(localFragment).commit();
      GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.zzft.getActivity(), 12451000), this.zzft.getActivity(), -1);
    }
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.setClassLoader(WalletFragmentOptions.class.getClassLoader());
    this.zzfr.onSaveInstanceState(paramBundle);
    if (this.zzfh != null)
    {
      paramBundle.putParcelable("walletFragmentInitParams", this.zzfh);
      this.zzfh = null;
    }
    if (this.zzfi != null)
    {
      paramBundle.putParcelable("maskedWalletRequest", this.zzfi);
      this.zzfi = null;
    }
    if (this.zzfj != null)
    {
      paramBundle.putParcelable("maskedWallet", this.zzfj);
      this.zzfj = null;
    }
    if (this.zzfg != null)
    {
      paramBundle.putParcelable("walletFragmentOptions", this.zzfg);
      this.zzfg = null;
    }
    if (this.zzfk != null)
    {
      paramBundle.putBoolean("enabled", this.zzfk.booleanValue());
      this.zzfk = null;
    }
  }
  
  public final void onStart()
  {
    super.onStart();
    this.zzfr.onStart();
  }
  
  public final void onStop()
  {
    super.onStop();
    this.zzfr.onStop();
  }
  
  public static abstract interface OnStateChangedListener
  {
    public abstract void onStateChanged(WalletFragment paramWalletFragment, int paramInt1, int paramInt2, Bundle paramBundle);
  }
  
  static final class zza
    extends zzp
  {
    private WalletFragment.OnStateChangedListener zzfu;
    private final WalletFragment zzfv;
    
    zza(WalletFragment paramWalletFragment)
    {
      this.zzfv = paramWalletFragment;
    }
    
    public final void zza(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      if (this.zzfu != null) {
        this.zzfu.onStateChanged(this.zzfv, paramInt1, paramInt2, paramBundle);
      }
    }
  }
  
  private static final class zzb
    implements LifecycleDelegate
  {
    private final zzl zzfn;
    
    private zzb(zzl paramzzl)
    {
      this.zzfn = paramzzl;
    }
    
    private final void initialize(WalletFragmentInitParams paramWalletFragmentInitParams)
    {
      try
      {
        this.zzfn.initialize(paramWalletFragmentInitParams);
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
        this.zzfn.onActivityResult(paramInt1, paramInt2, paramIntent);
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
        this.zzfn.setEnabled(paramBoolean);
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
        this.zzfn.updateMaskedWallet(paramMaskedWallet);
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
        this.zzfn.updateMaskedWalletRequest(paramMaskedWalletRequest);
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
        this.zzfn.onCreate(paramBundle);
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
        paramLayoutInflater = (View)ObjectWrapper.unwrap(this.zzfn.onCreateView(ObjectWrapper.wrap(paramLayoutInflater), ObjectWrapper.wrap(paramViewGroup), paramBundle));
        return paramLayoutInflater;
      }
      catch (RemoteException paramLayoutInflater)
      {
        throw new RuntimeException(paramLayoutInflater);
      }
    }
    
    public final void onDestroy() {}
    
    public final void onInflate(Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2)
    {
      paramBundle1 = (WalletFragmentOptions)paramBundle1.getParcelable("extraWalletFragmentOptions");
      try
      {
        this.zzfn.zza(ObjectWrapper.wrap(paramActivity), paramBundle1, paramBundle2);
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
        this.zzfn.onPause();
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
        this.zzfn.onResume();
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
        this.zzfn.onSaveInstanceState(paramBundle);
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
        this.zzfn.onStart();
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
        this.zzfn.onStop();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
  }
  
  private final class zzc
    extends DeferredLifecycleHelper<WalletFragment.zzb>
    implements View.OnClickListener
  {
    private zzc() {}
    
    protected final void createDelegate(OnDelegateCreatedListener<WalletFragment.zzb> paramOnDelegateCreatedListener)
    {
      Object localObject = WalletFragment.zza(WalletFragment.this).getActivity();
      if ((WalletFragment.zzb(WalletFragment.this) == null) && (WalletFragment.zzc(WalletFragment.this)) && (localObject != null)) {}
      try
      {
        localObject = zzak.zza((Activity)localObject, WalletFragment.zzd(WalletFragment.this), WalletFragment.zze(WalletFragment.this), WalletFragment.zzf(WalletFragment.this));
        WalletFragment localWalletFragment = WalletFragment.this;
        WalletFragment.zzb localzzb = new com/google/android/gms/wallet/fragment/WalletFragment$zzb;
        localzzb.<init>((zzl)localObject, null);
        WalletFragment.zza(localWalletFragment, localzzb);
        WalletFragment.zza(WalletFragment.this, null);
        paramOnDelegateCreatedListener.onDelegateCreated(WalletFragment.zzb(WalletFragment.this));
        if (WalletFragment.zzg(WalletFragment.this) != null)
        {
          WalletFragment.zzb.zza(WalletFragment.zzb(WalletFragment.this), WalletFragment.zzg(WalletFragment.this));
          WalletFragment.zza(WalletFragment.this, null);
        }
        if (WalletFragment.zzh(WalletFragment.this) != null)
        {
          WalletFragment.zzb.zza(WalletFragment.zzb(WalletFragment.this), WalletFragment.zzh(WalletFragment.this));
          WalletFragment.zza(WalletFragment.this, null);
        }
        if (WalletFragment.zzi(WalletFragment.this) != null)
        {
          WalletFragment.zzb.zza(WalletFragment.zzb(WalletFragment.this), WalletFragment.zzi(WalletFragment.this));
          WalletFragment.zza(WalletFragment.this, null);
        }
        if (WalletFragment.zzj(WalletFragment.this) != null)
        {
          WalletFragment.zzb.zza(WalletFragment.zzb(WalletFragment.this), WalletFragment.zzj(WalletFragment.this).booleanValue());
          WalletFragment.zza(WalletFragment.this, null);
        }
        return;
      }
      catch (GooglePlayServicesNotAvailableException paramOnDelegateCreatedListener)
      {
        for (;;) {}
      }
    }
    
    protected final void handleGooglePlayUnavailable(FrameLayout paramFrameLayout)
    {
      int i = -1;
      int j = -2;
      Button localButton = new Button(WalletFragment.zza(WalletFragment.this).getActivity());
      localButton.setText(R.string.wallet_buy_button_place_holder);
      int k = j;
      int m = i;
      if (WalletFragment.zze(WalletFragment.this) != null)
      {
        WalletFragmentStyle localWalletFragmentStyle = WalletFragment.zze(WalletFragment.this).getFragmentStyle();
        k = j;
        m = i;
        if (localWalletFragmentStyle != null)
        {
          DisplayMetrics localDisplayMetrics = WalletFragment.zza(WalletFragment.this).getResources().getDisplayMetrics();
          m = localWalletFragmentStyle.zza("buyButtonWidth", localDisplayMetrics, -1);
          k = localWalletFragmentStyle.zza("buyButtonHeight", localDisplayMetrics, -2);
        }
      }
      localButton.setLayoutParams(new ViewGroup.LayoutParams(m, k));
      localButton.setOnClickListener(this);
      paramFrameLayout.addView(localButton);
    }
    
    public final void onClick(View paramView)
    {
      paramView = WalletFragment.zza(WalletFragment.this).getActivity();
      GooglePlayServicesUtil.showErrorDialogFragment(GooglePlayServicesUtil.isGooglePlayServicesAvailable(paramView, 12451000), paramView, -1);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wallet/fragment/WalletFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */