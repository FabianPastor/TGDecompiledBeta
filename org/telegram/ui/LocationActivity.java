package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate;
import org.telegram.ui.Adapters.LocationActivityAdapter;
import org.telegram.ui.Adapters.LocationActivitySearchAdapter;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;

public class LocationActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int map_list_menu_hybrid = 4;
  private static final int map_list_menu_map = 2;
  private static final int map_list_menu_satellite = 3;
  private static final int share = 1;
  private LocationActivityAdapter adapter;
  private AnimatorSet animatorSet;
  private BackupImageView avatarImageView;
  private boolean checkPermission = true;
  private CircleOptions circleOptions;
  private LocationActivityDelegate delegate;
  private TextView distanceTextView;
  private LinearLayout emptyTextLayout;
  private boolean firstWas = false;
  private GoogleMap googleMap;
  private ListView listView;
  private ImageView locationButton;
  private MapView mapView;
  private FrameLayout mapViewClip;
  private boolean mapsInitialized;
  private ImageView markerImageView;
  private int markerTop;
  private ImageView markerXImageView;
  private MessageObject messageObject;
  private Location myLocation;
  private TextView nameTextView;
  private boolean onResumeCalled;
  private int overScrollHeight = AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(66.0F);
  private LocationActivitySearchAdapter searchAdapter;
  private ListView searchListView;
  private boolean searchWas;
  private boolean searching;
  private Location userLocation;
  private boolean userLocationMoved = false;
  private boolean wasResults;
  
  private void fixLayoutInternal(boolean paramBoolean)
  {
    if (this.listView != null) {
      if (!this.actionBar.getOccupyStatusBar()) {
        break label40;
      }
    }
    int j;
    label40:
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      i += ActionBar.getCurrentActionBarHeight();
      j = this.fragmentView.getMeasuredHeight();
      if (j != 0) {
        break;
      }
      return;
    }
    this.overScrollHeight = (j - AndroidUtilities.dp(66.0F) - i);
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    localLayoutParams.topMargin = i;
    this.listView.setLayoutParams(localLayoutParams);
    localLayoutParams = (FrameLayout.LayoutParams)this.mapViewClip.getLayoutParams();
    localLayoutParams.topMargin = i;
    localLayoutParams.height = this.overScrollHeight;
    this.mapViewClip.setLayoutParams(localLayoutParams);
    localLayoutParams = (FrameLayout.LayoutParams)this.searchListView.getLayoutParams();
    localLayoutParams.topMargin = i;
    this.searchListView.setLayoutParams(localLayoutParams);
    this.adapter.setOverScrollHeight(this.overScrollHeight);
    localLayoutParams = (FrameLayout.LayoutParams)this.mapView.getLayoutParams();
    if (localLayoutParams != null)
    {
      localLayoutParams.height = (this.overScrollHeight + AndroidUtilities.dp(10.0F));
      if (this.googleMap != null) {
        this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0F));
      }
      this.mapView.setLayoutParams(localLayoutParams);
    }
    this.adapter.notifyDataSetChanged();
    if (paramBoolean)
    {
      this.listView.setSelectionFromTop(0, -(int)(AndroidUtilities.dp(56.0F) * 2.5F + AndroidUtilities.dp(102.0F)));
      updateClipView(this.listView.getFirstVisiblePosition());
      this.listView.post(new Runnable()
      {
        public void run()
        {
          LocationActivity.this.listView.setSelectionFromTop(0, -(int)(AndroidUtilities.dp(56.0F) * 2.5F + AndroidUtilities.dp(102.0F)));
          LocationActivity.this.updateClipView(LocationActivity.this.listView.getFirstVisiblePosition());
        }
      });
      return;
    }
    updateClipView(this.listView.getFirstVisiblePosition());
  }
  
  private Location getLastLocation()
  {
    LocationManager localLocationManager = (LocationManager)ApplicationLoader.applicationContext.getSystemService("location");
    List localList = localLocationManager.getProviders(true);
    Location localLocation = null;
    int i = localList.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        localLocation = localLocationManager.getLastKnownLocation((String)localList.get(i));
        if (localLocation == null) {}
      }
      else
      {
        return localLocation;
      }
      i -= 1;
    }
  }
  
  /* Error */
  private void onMapInit()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 153	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 159	org/telegram/ui/LocationActivity:messageObject	Lorg/telegram/messenger/MessageObject;
    //   12: ifnull +160 -> 172
    //   15: new 384	com/google/android/gms/maps/model/LatLng
    //   18: dup
    //   19: aload_0
    //   20: getfield 266	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   23: invokevirtual 390	android/location/Location:getLatitude	()D
    //   26: aload_0
    //   27: getfield 266	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   30: invokevirtual 393	android/location/Location:getLongitude	()D
    //   33: invokespecial 396	com/google/android/gms/maps/model/LatLng:<init>	(DD)V
    //   36: astore_1
    //   37: aload_0
    //   38: getfield 153	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   41: new 398	com/google/android/gms/maps/model/MarkerOptions
    //   44: dup
    //   45: invokespecial 399	com/google/android/gms/maps/model/MarkerOptions:<init>	()V
    //   48: aload_1
    //   49: invokevirtual 403	com/google/android/gms/maps/model/MarkerOptions:position	(Lcom/google/android/gms/maps/model/LatLng;)Lcom/google/android/gms/maps/model/MarkerOptions;
    //   52: ldc_w 404
    //   55: invokestatic 410	com/google/android/gms/maps/model/BitmapDescriptorFactory:fromResource	(I)Lcom/google/android/gms/maps/model/BitmapDescriptor;
    //   58: invokevirtual 414	com/google/android/gms/maps/model/MarkerOptions:icon	(Lcom/google/android/gms/maps/model/BitmapDescriptor;)Lcom/google/android/gms/maps/model/MarkerOptions;
    //   61: invokevirtual 418	com/google/android/gms/maps/GoogleMap:addMarker	(Lcom/google/android/gms/maps/model/MarkerOptions;)Lcom/google/android/gms/maps/model/Marker;
    //   64: pop
    //   65: aload_1
    //   66: aload_0
    //   67: getfield 153	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   70: invokevirtual 422	com/google/android/gms/maps/GoogleMap:getMaxZoomLevel	()F
    //   73: ldc_w 423
    //   76: fsub
    //   77: invokestatic 429	com/google/android/gms/maps/CameraUpdateFactory:newLatLngZoom	(Lcom/google/android/gms/maps/model/LatLng;F)Lcom/google/android/gms/maps/CameraUpdate;
    //   80: astore_1
    //   81: aload_0
    //   82: getfield 153	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   85: aload_1
    //   86: invokevirtual 433	com/google/android/gms/maps/GoogleMap:moveCamera	(Lcom/google/android/gms/maps/CameraUpdate;)V
    //   89: aload_0
    //   90: getfield 153	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   93: iconst_1
    //   94: invokevirtual 436	com/google/android/gms/maps/GoogleMap:setMyLocationEnabled	(Z)V
    //   97: aload_0
    //   98: getfield 153	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   101: invokevirtual 440	com/google/android/gms/maps/GoogleMap:getUiSettings	()Lcom/google/android/gms/maps/UiSettings;
    //   104: iconst_0
    //   105: invokevirtual 445	com/google/android/gms/maps/UiSettings:setMyLocationButtonEnabled	(Z)V
    //   108: aload_0
    //   109: getfield 153	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   112: invokevirtual 440	com/google/android/gms/maps/GoogleMap:getUiSettings	()Lcom/google/android/gms/maps/UiSettings;
    //   115: iconst_0
    //   116: invokevirtual 448	com/google/android/gms/maps/UiSettings:setZoomControlsEnabled	(Z)V
    //   119: aload_0
    //   120: getfield 153	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   123: invokevirtual 440	com/google/android/gms/maps/GoogleMap:getUiSettings	()Lcom/google/android/gms/maps/UiSettings;
    //   126: iconst_0
    //   127: invokevirtual 451	com/google/android/gms/maps/UiSettings:setCompassEnabled	(Z)V
    //   130: aload_0
    //   131: getfield 153	org/telegram/ui/LocationActivity:googleMap	Lcom/google/android/gms/maps/GoogleMap;
    //   134: new 32	org/telegram/ui/LocationActivity$19
    //   137: dup
    //   138: aload_0
    //   139: invokespecial 452	org/telegram/ui/LocationActivity$19:<init>	(Lorg/telegram/ui/LocationActivity;)V
    //   142: invokevirtual 456	com/google/android/gms/maps/GoogleMap:setOnMyLocationChangeListener	(Lcom/google/android/gms/maps/GoogleMap$OnMyLocationChangeListener;)V
    //   145: aload_0
    //   146: invokespecial 458	org/telegram/ui/LocationActivity:getLastLocation	()Landroid/location/Location;
    //   149: astore_1
    //   150: aload_0
    //   151: aload_1
    //   152: putfield 190	org/telegram/ui/LocationActivity:myLocation	Landroid/location/Location;
    //   155: aload_0
    //   156: aload_1
    //   157: invokespecial 240	org/telegram/ui/LocationActivity:positionMarker	(Landroid/location/Location;)V
    //   160: return
    //   161: astore_2
    //   162: ldc_w 460
    //   165: aload_2
    //   166: invokestatic 466	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   169: goto -104 -> 65
    //   172: aload_0
    //   173: new 386	android/location/Location
    //   176: dup
    //   177: ldc_w 468
    //   180: invokespecial 471	android/location/Location:<init>	(Ljava/lang/String;)V
    //   183: putfield 266	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   186: aload_0
    //   187: getfield 266	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   190: ldc2_w 472
    //   193: invokevirtual 477	android/location/Location:setLatitude	(D)V
    //   196: aload_0
    //   197: getfield 266	org/telegram/ui/LocationActivity:userLocation	Landroid/location/Location;
    //   200: ldc2_w 478
    //   203: invokevirtual 482	android/location/Location:setLongitude	(D)V
    //   206: goto -117 -> 89
    //   209: astore_1
    //   210: ldc_w 460
    //   213: aload_1
    //   214: invokestatic 466	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   217: goto -120 -> 97
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	220	0	this	LocationActivity
    //   36	121	1	localObject	Object
    //   209	5	1	localException1	Exception
    //   161	5	2	localException2	Exception
    // Exception table:
    //   from	to	target	type
    //   37	65	161	java/lang/Exception
    //   89	97	209	java/lang/Exception
  }
  
  private void positionMarker(Location paramLocation)
  {
    if (paramLocation == null) {}
    LatLng localLatLng;
    do
    {
      do
      {
        do
        {
          return;
          this.myLocation = new Location(paramLocation);
          if (this.messageObject == null) {
            break;
          }
        } while ((this.userLocation == null) || (this.distanceTextView == null));
        float f = paramLocation.distanceTo(this.userLocation);
        if (f < 1000.0F)
        {
          this.distanceTextView.setText(String.format("%d %s", new Object[] { Integer.valueOf((int)f), LocaleController.getString("MetersAway", 2131165880) }));
          return;
        }
        this.distanceTextView.setText(String.format("%.2f %s", new Object[] { Float.valueOf(f / 1000.0F), LocaleController.getString("KMetersAway", 2131165778) }));
        return;
      } while (this.googleMap == null);
      localLatLng = new LatLng(paramLocation.getLatitude(), paramLocation.getLongitude());
      if (this.adapter != null)
      {
        this.adapter.searchGooglePlacesWithQuery(null, this.myLocation);
        this.adapter.setGpsLocation(this.myLocation);
      }
    } while (this.userLocationMoved);
    this.userLocation = new Location(paramLocation);
    if (this.firstWas)
    {
      paramLocation = CameraUpdateFactory.newLatLng(localLatLng);
      this.googleMap.animateCamera(paramLocation);
      return;
    }
    this.firstWas = true;
    paramLocation = CameraUpdateFactory.newLatLngZoom(localLatLng, this.googleMap.getMaxZoomLevel() - 4.0F);
    this.googleMap.moveCamera(paramLocation);
  }
  
  private void showPermissionAlert(boolean paramBoolean)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
    if (paramBoolean) {
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocationPosition", 2131166100));
    }
    for (;;)
    {
      localBuilder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", 2131166101), new DialogInterface.OnClickListener()
      {
        @TargetApi(9)
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (LocationActivity.this.getParentActivity() == null) {
            return;
          }
          try
          {
            paramAnonymousDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            paramAnonymousDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            LocationActivity.this.getParentActivity().startActivity(paramAnonymousDialogInterface);
            return;
          }
          catch (Exception paramAnonymousDialogInterface)
          {
            FileLog.e("tmessages", paramAnonymousDialogInterface);
          }
        }
      });
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
      showDialog(localBuilder.create());
      return;
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocation", 2131166099));
    }
  }
  
  private void updateClipView(int paramInt)
  {
    int i = 0;
    int j = 0;
    Object localObject = this.listView.getChildAt(0);
    if (localObject != null)
    {
      if (paramInt == 0)
      {
        paramInt = ((View)localObject).getTop();
        j = this.overScrollHeight;
        if (paramInt >= 0) {
          break label256;
        }
        i = paramInt;
        i = j + i;
        j = paramInt;
      }
      if ((FrameLayout.LayoutParams)this.mapViewClip.getLayoutParams() != null)
      {
        if (i > 0) {
          break label261;
        }
        if (this.mapView.getVisibility() == 0)
        {
          this.mapView.setVisibility(4);
          this.mapViewClip.setVisibility(4);
        }
      }
    }
    for (;;)
    {
      this.mapViewClip.setTranslationY(Math.min(0, j));
      this.mapView.setTranslationY(Math.max(0, -j / 2));
      localObject = this.markerImageView;
      paramInt = -j - AndroidUtilities.dp(42.0F) + i / 2;
      this.markerTop = paramInt;
      ((ImageView)localObject).setTranslationY(paramInt);
      this.markerXImageView.setTranslationY(-j - AndroidUtilities.dp(7.0F) + i / 2);
      localObject = (FrameLayout.LayoutParams)this.mapView.getLayoutParams();
      if ((localObject != null) && (((FrameLayout.LayoutParams)localObject).height != this.overScrollHeight + AndroidUtilities.dp(10.0F)))
      {
        ((FrameLayout.LayoutParams)localObject).height = (this.overScrollHeight + AndroidUtilities.dp(10.0F));
        if (this.googleMap != null) {
          this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0F));
        }
        this.mapView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
      return;
      label256:
      i = 0;
      break;
      label261:
      if (this.mapView.getVisibility() == 4)
      {
        this.mapView.setVisibility(0);
        this.mapViewClip.setVisibility(0);
      }
    }
  }
  
  private void updateSearchInterface()
  {
    if (this.adapter != null) {
      this.adapter.notifyDataSetChanged();
    }
  }
  
  private void updateUserData()
  {
    int i;
    String str;
    Object localObject;
    TLRPC.User localUser;
    TLRPC.Chat localChat;
    AvatarDrawable localAvatarDrawable;
    if ((this.messageObject != null) && (this.avatarImageView != null))
    {
      i = this.messageObject.messageOwner.from_id;
      if (this.messageObject.isForwarded())
      {
        if (this.messageObject.messageOwner.fwd_from.channel_id == 0) {
          break label164;
        }
        i = -this.messageObject.messageOwner.fwd_from.channel_id;
      }
      str = "";
      localObject = null;
      localUser = null;
      localChat = null;
      localAvatarDrawable = null;
      if (i <= 0) {
        break label181;
      }
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(i));
      if (localUser != null)
      {
        localObject = localChat;
        if (localUser.photo != null) {
          localObject = localUser.photo.photo_small;
        }
        localAvatarDrawable = new AvatarDrawable(localUser);
        str = UserObject.getUserName(localUser);
      }
    }
    for (;;)
    {
      if (localAvatarDrawable == null) {
        break label240;
      }
      this.avatarImageView.setImage((TLObject)localObject, null, localAvatarDrawable);
      this.nameTextView.setText(str);
      return;
      label164:
      i = this.messageObject.messageOwner.fwd_from.from_id;
      break;
      label181:
      localChat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
      if (localChat != null)
      {
        localObject = localUser;
        if (localChat.photo != null) {
          localObject = localChat.photo.photo_small;
        }
        localAvatarDrawable = new AvatarDrawable(localChat);
        str = localChat.title;
      }
    }
    label240:
    this.avatarImageView.setImageDrawable(null);
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    if (AndroidUtilities.isTablet()) {
      this.actionBar.setOccupyStatusBar(false);
    }
    Object localObject1 = this.actionBar;
    boolean bool;
    label196:
    label206:
    int i;
    label616:
    float f1;
    if (this.messageObject != null)
    {
      bool = true;
      ((ActionBar)localObject1).setAddToContainer(bool);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            LocationActivity.this.finishFragment();
          }
          do
          {
            do
            {
              do
              {
                do
                {
                  return;
                  if (paramAnonymousInt != 2) {
                    break;
                  }
                } while (LocationActivity.this.googleMap == null);
                LocationActivity.this.googleMap.setMapType(1);
                return;
                if (paramAnonymousInt != 3) {
                  break;
                }
              } while (LocationActivity.this.googleMap == null);
              LocationActivity.this.googleMap.setMapType(2);
              return;
              if (paramAnonymousInt != 4) {
                break;
              }
            } while (LocationActivity.this.googleMap == null);
            LocationActivity.this.googleMap.setMapType(4);
            return;
          } while (paramAnonymousInt != 1);
          try
          {
            double d1 = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
            double d2 = LocationActivity.this.messageObject.messageOwner.media.geo._long;
            LocationActivity.this.getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + d1 + "," + d2 + "?q=" + d1 + "," + d2)));
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      });
      localObject1 = this.actionBar.createMenu();
      if (this.messageObject == null) {
        break label1346;
      }
      if ((this.messageObject.messageOwner.media.title == null) || (this.messageObject.messageOwner.media.title.length() <= 0)) {
        break label1327;
      }
      this.actionBar.setTitle(this.messageObject.messageOwner.media.title);
      if ((this.messageObject.messageOwner.media.address != null) && (this.messageObject.messageOwner.media.address.length() > 0)) {
        this.actionBar.setSubtitle(this.messageObject.messageOwner.media.address);
      }
      ((ActionBarMenu)localObject1).addItem(1, 2130837979);
      localObject1 = ((ActionBarMenu)localObject1).addItem(0, 2130837708);
      ((ActionBarMenuItem)localObject1).addSubItem(2, LocaleController.getString("Map", 2131165848), 0);
      ((ActionBarMenuItem)localObject1).addSubItem(3, LocaleController.getString("Satellite", 2131166197), 0);
      ((ActionBarMenuItem)localObject1).addSubItem(4, LocaleController.getString("Hybrid", 2131165744), 0);
      this.fragmentView = new FrameLayout(paramContext)
      {
        private boolean first = true;
        
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          if (paramAnonymousBoolean)
          {
            LocationActivity.this.fixLayoutInternal(this.first);
            this.first = false;
          }
        }
      };
      localObject1 = (FrameLayout)this.fragmentView;
      this.locationButton = new ImageView(paramContext);
      this.locationButton.setBackgroundResource(2130837685);
      this.locationButton.setImageResource(2130837852);
      this.locationButton.setScaleType(ImageView.ScaleType.CENTER);
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject2 = new StateListAnimator();
        localObject3 = ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        ((StateListAnimator)localObject2).addState(new int[] { 16842919 }, (Animator)localObject3);
        localObject3 = ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        ((StateListAnimator)localObject2).addState(new int[0], (Animator)localObject3);
        this.locationButton.setStateListAnimator((StateListAnimator)localObject2);
        this.locationButton.setOutlineProvider(new ViewOutlineProvider()
        {
          @SuppressLint({"NewApi"})
          public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
          {
            paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
          }
        });
      }
      if (this.messageObject == null) {
        break label1510;
      }
      this.mapView = new MapView(paramContext);
      ((FrameLayout)localObject1).setBackgroundDrawable(new MapPlaceholderDrawable());
      new Thread(new Runnable()
      {
        public void run()
        {
          try
          {
            this.val$map.onCreate(null);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if ((LocationActivity.this.mapView != null) && (LocationActivity.this.getParentActivity() != null)) {}
                try
                {
                  LocationActivity.5.this.val$map.onCreate(null);
                  MapsInitializer.initialize(LocationActivity.this.getParentActivity());
                  LocationActivity.this.mapView.getMapAsync(new OnMapReadyCallback()
                  {
                    public void onMapReady(GoogleMap paramAnonymous3GoogleMap)
                    {
                      LocationActivity.access$002(LocationActivity.this, paramAnonymous3GoogleMap);
                      LocationActivity.this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0F));
                      LocationActivity.this.onMapInit();
                    }
                  });
                  LocationActivity.access$1302(LocationActivity.this, true);
                  if (LocationActivity.this.onResumeCalled) {
                    LocationActivity.this.mapView.onResume();
                  }
                  return;
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                }
              }
            });
            return;
          }
          catch (Exception localException)
          {
            for (;;) {}
          }
        }
      }).start();
      localObject2 = new FrameLayout(paramContext);
      ((FrameLayout)localObject2).setBackgroundResource(2130837805);
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, 60, 83));
      ((FrameLayout)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (LocationActivity.this.userLocation != null)
          {
            paramAnonymousView = new LatLng(LocationActivity.this.userLocation.getLatitude(), LocationActivity.this.userLocation.getLongitude());
            if (LocationActivity.this.googleMap != null)
            {
              paramAnonymousView = CameraUpdateFactory.newLatLngZoom(paramAnonymousView, LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0F);
              LocationActivity.this.googleMap.animateCamera(paramAnonymousView);
            }
          }
        }
      });
      this.avatarImageView = new BackupImageView(paramContext);
      this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0F));
      localObject3 = this.avatarImageView;
      if (!LocaleController.isRTL) {
        break label1404;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label1410;
      }
      f1 = 0.0F;
      label624:
      if (!LocaleController.isRTL) {
        break label1417;
      }
      f2 = 12.0F;
      label634:
      ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(40, 40.0F, i | 0x30, f1, 12.0F, f2, 0.0F));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.nameTextView.setSingleLine(true);
      localObject3 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label1422;
      }
      i = 5;
      label747:
      ((TextView)localObject3).setGravity(i);
      localObject3 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label1428;
      }
      i = 5;
      label769:
      if (!LocaleController.isRTL) {
        break label1434;
      }
      f1 = 12.0F;
      label779:
      if (!LocaleController.isRTL) {
        break label1441;
      }
      f2 = 72.0F;
      label789:
      ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 10.0F, f2, 0.0F));
      this.distanceTextView = new TextView(paramContext);
      this.distanceTextView.setTextSize(1, 14.0F);
      this.distanceTextView.setTextColor(-13660983);
      this.distanceTextView.setMaxLines(1);
      this.distanceTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.distanceTextView.setSingleLine(true);
      localObject3 = this.distanceTextView;
      if (!LocaleController.isRTL) {
        break label1448;
      }
      i = 5;
      label889:
      ((TextView)localObject3).setGravity(i);
      localObject3 = this.distanceTextView;
      if (!LocaleController.isRTL) {
        break label1454;
      }
      i = 5;
      label911:
      if (!LocaleController.isRTL) {
        break label1460;
      }
      f1 = 12.0F;
      label921:
      if (!LocaleController.isRTL) {
        break label1467;
      }
      f2 = 72.0F;
      label931:
      ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 33.0F, f2, 0.0F));
      this.userLocation = new Location("network");
      this.userLocation.setLatitude(this.messageObject.messageOwner.media.geo.lat);
      this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
      paramContext = new ImageView(paramContext);
      paramContext.setBackgroundResource(2130837684);
      paramContext.setImageResource(2130837853);
      paramContext.setScaleType(ImageView.ScaleType.CENTER);
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject2 = new StateListAnimator();
        localObject3 = ObjectAnimator.ofFloat(paramContext, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        ((StateListAnimator)localObject2).addState(new int[] { 16842919 }, (Animator)localObject3);
        localObject3 = ObjectAnimator.ofFloat(paramContext, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        ((StateListAnimator)localObject2).addState(new int[0], (Animator)localObject3);
        paramContext.setStateListAnimator((StateListAnimator)localObject2);
        paramContext.setOutlineProvider(new ViewOutlineProvider()
        {
          @SuppressLint({"NewApi"})
          public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
          {
            paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
          }
        });
      }
      if (!LocaleController.isRTL) {
        break label1474;
      }
      i = 3;
      label1189:
      if (!LocaleController.isRTL) {
        break label1480;
      }
      f1 = 14.0F;
      label1199:
      if (!LocaleController.isRTL) {
        break label1485;
      }
      f2 = 0.0F;
      label1207:
      ((FrameLayout)localObject1).addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x50, f1, 0.0F, f2, 28.0F));
      paramContext.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (Build.VERSION.SDK_INT >= 23)
          {
            paramAnonymousView = LocationActivity.this.getParentActivity();
            if ((paramAnonymousView != null) && (paramAnonymousView.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0)) {
              LocationActivity.this.showPermissionAlert(true);
            }
          }
          while (LocationActivity.this.myLocation == null) {
            return;
          }
          try
          {
            paramAnonymousView = new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[] { Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo.lat), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo._long) })));
            LocationActivity.this.getParentActivity().startActivity(paramAnonymousView);
            return;
          }
          catch (Exception paramAnonymousView)
          {
            FileLog.e("tmessages", paramAnonymousView);
          }
        }
      });
      paramContext = this.locationButton;
      if (!LocaleController.isRTL) {
        break label1492;
      }
      i = 3;
      label1258:
      if (!LocaleController.isRTL) {
        break label1498;
      }
      f1 = 14.0F;
      label1268:
      if (!LocaleController.isRTL) {
        break label1503;
      }
    }
    label1327:
    label1346:
    label1404:
    label1410:
    label1417:
    label1422:
    label1428:
    label1434:
    label1441:
    label1448:
    label1454:
    label1460:
    label1467:
    label1474:
    label1480:
    label1485:
    label1492:
    label1498:
    label1503:
    for (float f2 = 0.0F;; f2 = 14.0F)
    {
      ((FrameLayout)localObject1).addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x50, f1, 0.0F, f2, 100.0F));
      this.locationButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (Build.VERSION.SDK_INT >= 23)
          {
            paramAnonymousView = LocationActivity.this.getParentActivity();
            if ((paramAnonymousView != null) && (paramAnonymousView.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0)) {
              LocationActivity.this.showPermissionAlert(true);
            }
          }
          while ((LocationActivity.this.myLocation == null) || (LocationActivity.this.googleMap == null)) {
            return;
          }
          LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude()), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0F));
        }
      });
      return this.fragmentView;
      bool = false;
      break;
      this.actionBar.setTitle(LocaleController.getString("ChatLocation", 2131165495));
      break label196;
      this.actionBar.setTitle(LocaleController.getString("ShareLocation", 2131166276));
      ((ActionBarMenu)localObject1).addItem(0, 2130837711).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
      {
        public void onSearchCollapse()
        {
          LocationActivity.access$202(LocationActivity.this, false);
          LocationActivity.access$702(LocationActivity.this, false);
          LocationActivity.this.searchListView.setEmptyView(null);
          LocationActivity.this.listView.setVisibility(0);
          LocationActivity.this.mapViewClip.setVisibility(0);
          LocationActivity.this.searchListView.setVisibility(8);
          LocationActivity.this.emptyTextLayout.setVisibility(8);
          LocationActivity.this.searchAdapter.searchDelayed(null, null);
        }
        
        public void onSearchExpand()
        {
          LocationActivity.access$202(LocationActivity.this, true);
          LocationActivity.this.listView.setVisibility(8);
          LocationActivity.this.mapViewClip.setVisibility(8);
          LocationActivity.this.searchListView.setVisibility(0);
          LocationActivity.this.searchListView.setEmptyView(LocationActivity.this.emptyTextLayout);
        }
        
        public void onTextChanged(EditText paramAnonymousEditText)
        {
          if (LocationActivity.this.searchAdapter == null) {
            return;
          }
          paramAnonymousEditText = paramAnonymousEditText.getText().toString();
          if (paramAnonymousEditText.length() != 0) {
            LocationActivity.access$702(LocationActivity.this, true);
          }
          LocationActivity.this.searchAdapter.searchDelayed(paramAnonymousEditText, LocationActivity.this.userLocation);
        }
      }).getSearchField().setHint(LocaleController.getString("Search", 2131166206));
      break label206;
      i = 3;
      break label616;
      f1 = 12.0F;
      break label624;
      f2 = 0.0F;
      break label634;
      i = 3;
      break label747;
      i = 3;
      break label769;
      f1 = 72.0F;
      break label779;
      f2 = 12.0F;
      break label789;
      i = 3;
      break label889;
      i = 3;
      break label911;
      f1 = 72.0F;
      break label921;
      f2 = 12.0F;
      break label931;
      i = 5;
      break label1189;
      f1 = 0.0F;
      break label1199;
      f2 = 14.0F;
      break label1207;
      i = 5;
      break label1258;
      f1 = 0.0F;
      break label1268;
    }
    label1510:
    this.searchWas = false;
    this.searching = false;
    this.mapViewClip = new FrameLayout(paramContext);
    this.mapViewClip.setBackgroundDrawable(new MapPlaceholderDrawable());
    if (this.adapter != null) {
      this.adapter.destroy();
    }
    if (this.searchAdapter != null) {
      this.searchAdapter.destroy();
    }
    this.listView = new ListView(paramContext);
    Object localObject2 = this.listView;
    Object localObject3 = new LocationActivityAdapter(paramContext);
    this.adapter = ((LocationActivityAdapter)localObject3);
    ((ListView)localObject2).setAdapter((ListAdapter)localObject3);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setDividerHeight(0);
    this.listView.setDivider(null);
    ((FrameLayout)localObject1).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
    this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
    {
      public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        if (paramAnonymousInt3 == 0) {
          return;
        }
        LocationActivity.this.updateClipView(paramAnonymousInt1);
      }
      
      public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt) {}
    });
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (paramAnonymousInt == 1)
        {
          if ((LocationActivity.this.delegate != null) && (LocationActivity.this.userLocation != null))
          {
            paramAnonymousAdapterView = new TLRPC.TL_messageMediaGeo();
            paramAnonymousAdapterView.geo = new TLRPC.TL_geoPoint();
            paramAnonymousAdapterView.geo.lat = LocationActivity.this.userLocation.getLatitude();
            paramAnonymousAdapterView.geo._long = LocationActivity.this.userLocation.getLongitude();
            LocationActivity.this.delegate.didSelectLocation(paramAnonymousAdapterView);
          }
          LocationActivity.this.finishFragment();
          return;
        }
        paramAnonymousAdapterView = LocationActivity.this.adapter.getItem(paramAnonymousInt);
        if ((paramAnonymousAdapterView != null) && (LocationActivity.this.delegate != null)) {
          LocationActivity.this.delegate.didSelectLocation(paramAnonymousAdapterView);
        }
        LocationActivity.this.finishFragment();
      }
    });
    this.adapter.setDelegate(new BaseLocationAdapter.BaseLocationAdapterDelegate()
    {
      public void didLoadedSearchResult(ArrayList<TLRPC.TL_messageMediaVenue> paramAnonymousArrayList)
      {
        if ((!LocationActivity.this.wasResults) && (!paramAnonymousArrayList.isEmpty())) {
          LocationActivity.access$2002(LocationActivity.this, true);
        }
      }
    });
    this.adapter.setOverScrollHeight(this.overScrollHeight);
    ((FrameLayout)localObject1).addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
    this.mapView = new MapView(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if (paramAnonymousMotionEvent.getAction() == 0)
        {
          if (LocationActivity.this.animatorSet != null) {
            LocationActivity.this.animatorSet.cancel();
          }
          LocationActivity.access$2102(LocationActivity.this, new AnimatorSet());
          LocationActivity.this.animatorSet.setDuration(200L);
          LocationActivity.this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", new float[] { LocationActivity.this.markerTop + -AndroidUtilities.dp(10.0F) }), ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", new float[] { 1.0F }) });
          LocationActivity.this.animatorSet.start();
        }
        for (;;)
        {
          if (paramAnonymousMotionEvent.getAction() == 2)
          {
            if (!LocationActivity.this.userLocationMoved)
            {
              AnimatorSet localAnimatorSet = new AnimatorSet();
              localAnimatorSet.setDuration(200L);
              localAnimatorSet.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[] { 1.0F }));
              localAnimatorSet.start();
              LocationActivity.access$2502(LocationActivity.this, true);
            }
            if ((LocationActivity.this.googleMap != null) && (LocationActivity.this.userLocation != null))
            {
              LocationActivity.this.userLocation.setLatitude(LocationActivity.this.googleMap.getCameraPosition().target.latitude);
              LocationActivity.this.userLocation.setLongitude(LocationActivity.this.googleMap.getCameraPosition().target.longitude);
            }
            LocationActivity.this.adapter.setCustomLocation(LocationActivity.this.userLocation);
          }
          return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
          if (paramAnonymousMotionEvent.getAction() == 1)
          {
            if (LocationActivity.this.animatorSet != null) {
              LocationActivity.this.animatorSet.cancel();
            }
            LocationActivity.access$2102(LocationActivity.this, new AnimatorSet());
            LocationActivity.this.animatorSet.setDuration(200L);
            LocationActivity.this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", new float[] { LocationActivity.this.markerTop }), ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", new float[] { 0.0F }) });
            LocationActivity.this.animatorSet.start();
          }
        }
      }
    };
    new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          this.val$map.onCreate(null);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if ((LocationActivity.this.mapView != null) && (LocationActivity.this.getParentActivity() != null)) {}
              try
              {
                LocationActivity.14.this.val$map.onCreate(null);
                MapsInitializer.initialize(LocationActivity.this.getParentActivity());
                LocationActivity.this.mapView.getMapAsync(new OnMapReadyCallback()
                {
                  public void onMapReady(GoogleMap paramAnonymous3GoogleMap)
                  {
                    LocationActivity.access$002(LocationActivity.this, paramAnonymous3GoogleMap);
                    LocationActivity.this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0F));
                    LocationActivity.this.onMapInit();
                  }
                });
                LocationActivity.access$1302(LocationActivity.this, true);
                if (LocationActivity.this.onResumeCalled) {
                  LocationActivity.this.mapView.onResume();
                }
                return;
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
              }
            }
          });
          return;
        }
        catch (Exception localException)
        {
          for (;;) {}
        }
      }
    }).start();
    localObject2 = new View(paramContext);
    ((View)localObject2).setBackgroundResource(2130837694);
    this.mapViewClip.addView((View)localObject2, LayoutHelper.createFrame(-1, 3, 83));
    this.markerImageView = new ImageView(paramContext);
    this.markerImageView.setImageResource(2130837811);
    this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(24, 42, 49));
    this.markerXImageView = new ImageView(paramContext);
    this.markerXImageView.setAlpha(0.0F);
    this.markerXImageView.setImageResource(2130837930);
    this.mapViewClip.addView(this.markerXImageView, LayoutHelper.createFrame(14, 14, 49));
    localObject2 = this.mapViewClip;
    localObject3 = this.locationButton;
    label1912:
    label1924:
    int j;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = 56;
      if (Build.VERSION.SDK_INT < 21) {
        break label2288;
      }
      f1 = 56.0F;
      if (!LocaleController.isRTL) {
        break label2295;
      }
      j = 3;
      label1933:
      if (!LocaleController.isRTL) {
        break label2301;
      }
      f2 = 14.0F;
      label1943:
      if (!LocaleController.isRTL) {
        break label2306;
      }
    }
    label2288:
    label2295:
    label2301:
    label2306:
    for (float f3 = 0.0F;; f3 = 14.0F)
    {
      ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(i, f1, j | 0x50, f2, 0.0F, f3, 14.0F));
      this.locationButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (Build.VERSION.SDK_INT >= 23)
          {
            paramAnonymousView = LocationActivity.this.getParentActivity();
            if ((paramAnonymousView != null) && (paramAnonymousView.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0)) {
              LocationActivity.this.showPermissionAlert(false);
            }
          }
          while ((LocationActivity.this.myLocation == null) || (LocationActivity.this.googleMap == null)) {
            return;
          }
          paramAnonymousView = new AnimatorSet();
          paramAnonymousView.setDuration(200L);
          paramAnonymousView.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[] { 0.0F }));
          paramAnonymousView.start();
          LocationActivity.this.adapter.setCustomLocation(null);
          LocationActivity.access$2502(LocationActivity.this, false);
          LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude())));
        }
      });
      this.locationButton.setAlpha(0.0F);
      this.emptyTextLayout = new LinearLayout(paramContext);
      this.emptyTextLayout.setVisibility(8);
      this.emptyTextLayout.setOrientation(1);
      ((FrameLayout)localObject1).addView(this.emptyTextLayout, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 100.0F, 0.0F, 0.0F));
      this.emptyTextLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      localObject2 = new TextView(paramContext);
      ((TextView)localObject2).setTextColor(-8355712);
      ((TextView)localObject2).setTextSize(1, 20.0F);
      ((TextView)localObject2).setGravity(17);
      ((TextView)localObject2).setText(LocaleController.getString("NoResult", 2131165949));
      this.emptyTextLayout.addView((View)localObject2, LayoutHelper.createLinear(-1, -1, 0.5F));
      localObject2 = new FrameLayout(paramContext);
      this.emptyTextLayout.addView((View)localObject2, LayoutHelper.createLinear(-1, -1, 0.5F));
      this.searchListView = new ListView(paramContext);
      this.searchListView.setVisibility(8);
      this.searchListView.setDividerHeight(0);
      this.searchListView.setDivider(null);
      localObject2 = this.searchListView;
      paramContext = new LocationActivitySearchAdapter(paramContext);
      this.searchAdapter = paramContext;
      ((ListView)localObject2).setAdapter(paramContext);
      ((FrameLayout)localObject1).addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
      this.searchListView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == 1) && (LocationActivity.this.searching) && (LocationActivity.this.searchWas)) {
            AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
          }
        }
      });
      this.searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          paramAnonymousAdapterView = LocationActivity.this.searchAdapter.getItem(paramAnonymousInt);
          if ((paramAnonymousAdapterView != null) && (LocationActivity.this.delegate != null)) {
            LocationActivity.this.delegate.didSelectLocation(paramAnonymousAdapterView);
          }
          LocationActivity.this.finishFragment();
        }
      });
      ((FrameLayout)localObject1).addView(this.actionBar);
      break;
      i = 60;
      break label1912;
      f1 = 60.0F;
      break label1924;
      j = 5;
      break label1933;
      f2 = 0.0F;
      break label1943;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0)) {
        updateUserData();
      }
    }
    do
    {
      return;
      if (paramInt == NotificationCenter.closeChats)
      {
        removeSelfFromStack();
        return;
      }
    } while ((paramInt != NotificationCenter.locationPermissionGranted) || (this.googleMap == null));
    try
    {
      this.googleMap.setMyLocationEnabled(true);
      return;
    }
    catch (Exception paramVarArgs)
    {
      FileLog.e("tmessages", paramVarArgs);
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.swipeBackEnabled = false;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
    if (this.messageObject != null) {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    try
    {
      if (this.mapView != null) {
        this.mapView.onDestroy();
      }
      if (this.adapter != null) {
        this.adapter.destroy();
      }
      if (this.searchAdapter != null) {
        this.searchAdapter.destroy();
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    if ((this.mapView != null) && (this.mapsInitialized)) {
      this.mapView.onLowMemory();
    }
  }
  
  public void onPause()
  {
    super.onPause();
    if ((this.mapView != null) && (this.mapsInitialized)) {}
    try
    {
      this.mapView.onPause();
      this.onResumeCalled = false;
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    if ((this.mapView != null) && (this.mapsInitialized)) {}
    try
    {
      this.mapView.onResume();
      this.onResumeCalled = true;
      if (this.googleMap == null) {}
    }
    catch (Throwable localThrowable)
    {
      try
      {
        this.googleMap.setMyLocationEnabled(true);
        updateUserData();
        fixLayoutInternal(true);
        if ((this.checkPermission) && (Build.VERSION.SDK_INT >= 23))
        {
          Activity localActivity = getParentActivity();
          if (localActivity != null)
          {
            this.checkPermission = false;
            if (localActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
              localActivity.requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" }, 2);
            }
          }
        }
        return;
        localThrowable = localThrowable;
        FileLog.e("tmessages", localThrowable);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {}
    try
    {
      if ((this.mapView.getParent() instanceof ViewGroup)) {
        ((ViewGroup)this.mapView.getParent()).removeView(this.mapView);
      }
      if (this.mapViewClip != null)
      {
        this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0F), 51));
        updateClipView(this.listView.getFirstVisiblePosition());
        return;
      }
    }
    catch (Exception localException)
    {
      do
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      } while (this.fragmentView == null);
      ((FrameLayout)this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
    }
  }
  
  public void setDelegate(LocationActivityDelegate paramLocationActivityDelegate)
  {
    this.delegate = paramLocationActivityDelegate;
  }
  
  public void setMessageObject(MessageObject paramMessageObject)
  {
    this.messageObject = paramMessageObject;
  }
  
  public static abstract interface LocationActivityDelegate
  {
    public abstract void didSelectLocation(TLRPC.MessageMedia paramMessageMedia);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/LocationActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */