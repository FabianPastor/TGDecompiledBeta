package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentLocations;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate;
import org.telegram.ui.Adapters.LocationActivityAdapter;
import org.telegram.ui.Adapters.LocationActivitySearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

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
  private AvatarDrawable avatarDrawable;
  private boolean checkGpsEnabled = true;
  private boolean checkPermission = true;
  private CircleOptions circleOptions;
  private LocationActivityDelegate delegate;
  private long dialogId;
  private EmptyTextProgressView emptyView;
  private boolean firstFocus = true;
  private boolean firstWas = false;
  private GoogleMap googleMap;
  private boolean isFirstLocation = true;
  private LinearLayoutManager layoutManager;
  private RecyclerListView listView;
  private int liveLocationType;
  private ImageView locationButton;
  private MapView mapView;
  private FrameLayout mapViewClip;
  private boolean mapsInitialized;
  private ImageView markerImageView;
  private int markerTop;
  private ImageView markerXImageView;
  private ArrayList<LiveLocation> markers = new ArrayList();
  private SparseArray<LiveLocation> markersMap = new SparseArray();
  private MessageObject messageObject;
  private Location myLocation;
  private boolean onResumeCalled;
  private ActionBarMenuItem otherItem;
  private int overScrollHeight = AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(66.0F);
  private ImageView routeButton;
  private LocationActivitySearchAdapter searchAdapter;
  private RecyclerListView searchListView;
  private boolean searchWas;
  private boolean searching;
  private Runnable updateRunnable;
  private Location userLocation;
  private boolean userLocationMoved = false;
  private boolean wasResults;
  
  public LocationActivity(int paramInt)
  {
    this.liveLocationType = paramInt;
  }
  
  private LiveLocation addUserMarker(TLRPC.Message paramMessage)
  {
    Object localObject = new LatLng(paramMessage.media.geo.lat, paramMessage.media.geo._long);
    LiveLocation localLiveLocation = (LiveLocation)this.markersMap.get(paramMessage.from_id);
    if (localLiveLocation == null)
    {
      localLiveLocation = new LiveLocation();
      localLiveLocation.object = paramMessage;
      if (localLiveLocation.object.from_id != 0)
      {
        localLiveLocation.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(localLiveLocation.object.from_id));
        localLiveLocation.id = localLiveLocation.object.from_id;
      }
    }
    for (;;)
    {
      try
      {
        paramMessage = new com/google/android/gms/maps/model/MarkerOptions;
        paramMessage.<init>();
        MarkerOptions localMarkerOptions = paramMessage.position((LatLng)localObject);
        localObject = createUserBitmap(localLiveLocation);
        paramMessage = localLiveLocation;
        if (localObject != null)
        {
          localMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap((Bitmap)localObject));
          localMarkerOptions.anchor(0.5F, 0.907F);
          localLiveLocation.marker = this.googleMap.addMarker(localMarkerOptions);
          this.markers.add(localLiveLocation);
          this.markersMap.put(localLiveLocation.id, localLiveLocation);
          localObject = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
          paramMessage = localLiveLocation;
          if (localLiveLocation.id == UserConfig.getInstance(this.currentAccount).getClientUserId())
          {
            paramMessage = localLiveLocation;
            if (localObject != null)
            {
              paramMessage = localLiveLocation;
              if (localLiveLocation.object.id == ((LocationController.SharingLocationInfo)localObject).mid)
              {
                paramMessage = localLiveLocation;
                if (this.myLocation != null)
                {
                  localObject = localLiveLocation.marker;
                  paramMessage = new com/google/android/gms/maps/model/LatLng;
                  paramMessage.<init>(this.myLocation.getLatitude(), this.myLocation.getLongitude());
                  ((Marker)localObject).setPosition(paramMessage);
                  paramMessage = localLiveLocation;
                }
              }
            }
          }
        }
      }
      catch (Exception paramMessage)
      {
        int i;
        FileLog.e(paramMessage);
        paramMessage = localLiveLocation;
        continue;
      }
      return paramMessage;
      i = (int)MessageObject.getDialogId(paramMessage);
      if (i > 0)
      {
        localLiveLocation.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
        localLiveLocation.id = i;
      }
      else
      {
        localLiveLocation.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
        localLiveLocation.id = i;
        continue;
        localLiveLocation.object = paramMessage;
        localLiveLocation.marker.setPosition((LatLng)localObject);
        paramMessage = localLiveLocation;
      }
    }
  }
  
  private Bitmap createUserBitmap(LiveLocation paramLiveLocation)
  {
    localBitmap1 = null;
    Canvas localCanvas = null;
    localBitmap2 = localBitmap1;
    for (;;)
    {
      try
      {
        if (paramLiveLocation.user != null)
        {
          localBitmap2 = localBitmap1;
          if (paramLiveLocation.user.photo != null)
          {
            localBitmap2 = localBitmap1;
            localObject1 = paramLiveLocation.user.photo.photo_small;
            localBitmap2 = localBitmap1;
            localBitmap1 = Bitmap.createBitmap(AndroidUtilities.dp(62.0F), AndroidUtilities.dp(76.0F), Bitmap.Config.ARGB_8888);
            localBitmap2 = localBitmap1;
            localBitmap1.eraseColor(0);
            localBitmap2 = localBitmap1;
            localCanvas = new android/graphics/Canvas;
            localBitmap2 = localBitmap1;
            localCanvas.<init>(localBitmap1);
            localBitmap2 = localBitmap1;
            Object localObject2 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM);
            localBitmap2 = localBitmap1;
            ((Drawable)localObject2).setBounds(0, 0, AndroidUtilities.dp(62.0F), AndroidUtilities.dp(76.0F));
            localBitmap2 = localBitmap1;
            ((Drawable)localObject2).draw(localCanvas);
            localBitmap2 = localBitmap1;
            Paint localPaint = new android/graphics/Paint;
            localBitmap2 = localBitmap1;
            localPaint.<init>(1);
            localBitmap2 = localBitmap1;
            localObject2 = new android/graphics/RectF;
            localBitmap2 = localBitmap1;
            ((RectF)localObject2).<init>();
            localBitmap2 = localBitmap1;
            localCanvas.save();
            if (localObject1 == null) {
              continue;
            }
            localBitmap2 = localBitmap1;
            localObject1 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject1, true).toString());
            if (localObject1 != null)
            {
              localBitmap2 = localBitmap1;
              paramLiveLocation = new android/graphics/BitmapShader;
              localBitmap2 = localBitmap1;
              paramLiveLocation.<init>((Bitmap)localObject1, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
              localBitmap2 = localBitmap1;
              Matrix localMatrix = new android/graphics/Matrix;
              localBitmap2 = localBitmap1;
              localMatrix.<init>();
              localBitmap2 = localBitmap1;
              float f = AndroidUtilities.dp(52.0F) / ((Bitmap)localObject1).getWidth();
              localBitmap2 = localBitmap1;
              localMatrix.postTranslate(AndroidUtilities.dp(5.0F), AndroidUtilities.dp(5.0F));
              localBitmap2 = localBitmap1;
              localMatrix.postScale(f, f);
              localBitmap2 = localBitmap1;
              localPaint.setShader(paramLiveLocation);
              localBitmap2 = localBitmap1;
              paramLiveLocation.setLocalMatrix(localMatrix);
              localBitmap2 = localBitmap1;
              ((RectF)localObject2).set(AndroidUtilities.dp(5.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(57.0F), AndroidUtilities.dp(57.0F));
              localBitmap2 = localBitmap1;
              localCanvas.drawRoundRect((RectF)localObject2, AndroidUtilities.dp(26.0F), AndroidUtilities.dp(26.0F), localPaint);
            }
            localBitmap2 = localBitmap1;
            localCanvas.restore();
            localBitmap2 = localBitmap1;
          }
        }
      }
      catch (Throwable paramLiveLocation)
      {
        Object localObject1;
        FileLog.e(paramLiveLocation);
        continue;
        localBitmap2 = localBitmap1;
        if (paramLiveLocation.chat == null) {
          continue;
        }
        localBitmap2 = localBitmap1;
        ((AvatarDrawable)localObject1).setInfo(paramLiveLocation.chat);
        continue;
      }
      try
      {
        localCanvas.setBitmap(null);
        localBitmap2 = localBitmap1;
      }
      catch (Exception paramLiveLocation)
      {
        localBitmap2 = localBitmap1;
        continue;
      }
      return localBitmap2;
      localObject1 = localCanvas;
      localBitmap2 = localBitmap1;
      if (paramLiveLocation.chat != null)
      {
        localObject1 = localCanvas;
        localBitmap2 = localBitmap1;
        if (paramLiveLocation.chat.photo != null)
        {
          localBitmap2 = localBitmap1;
          localObject1 = paramLiveLocation.chat.photo.photo_small;
          continue;
          localBitmap2 = localBitmap1;
          localObject1 = new org/telegram/ui/Components/AvatarDrawable;
          localBitmap2 = localBitmap1;
          ((AvatarDrawable)localObject1).<init>();
          localBitmap2 = localBitmap1;
          if (paramLiveLocation.user == null) {
            continue;
          }
          localBitmap2 = localBitmap1;
          ((AvatarDrawable)localObject1).setInfo(paramLiveLocation.user);
          localBitmap2 = localBitmap1;
          localCanvas.translate(AndroidUtilities.dp(5.0F), AndroidUtilities.dp(5.0F));
          localBitmap2 = localBitmap1;
          ((AvatarDrawable)localObject1).setBounds(0, 0, AndroidUtilities.dp(52.2F), AndroidUtilities.dp(52.2F));
          localBitmap2 = localBitmap1;
          ((AvatarDrawable)localObject1).draw(localCanvas);
        }
      }
    }
  }
  
  private void fetchRecentLocations(ArrayList<TLRPC.Message> paramArrayList)
  {
    Object localObject = null;
    if (this.firstFocus) {
      localObject = new LatLngBounds.Builder();
    }
    int i = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
    for (int j = 0; j < paramArrayList.size(); j++)
    {
      TLRPC.Message localMessage = (TLRPC.Message)paramArrayList.get(j);
      if (localMessage.date + localMessage.media.period > i)
      {
        if (localObject != null) {
          ((LatLngBounds.Builder)localObject).include(new LatLng(localMessage.media.geo.lat, localMessage.media.geo._long));
        }
        addUserMarker(localMessage);
      }
    }
    if (localObject != null)
    {
      this.firstFocus = false;
      this.adapter.setLiveLocations(this.markers);
      if (!this.messageObject.isLiveLocation()) {}
    }
    for (;;)
    {
      try
      {
        localObject = ((LatLngBounds.Builder)localObject).build();
        j = paramArrayList.size();
        if (j <= 1) {}
      }
      catch (Exception paramArrayList)
      {
        continue;
      }
      try
      {
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds((LatLngBounds)localObject, AndroidUtilities.dp(60.0F)));
        return;
      }
      catch (Exception paramArrayList)
      {
        FileLog.e(paramArrayList);
      }
    }
  }
  
  private void fixLayoutInternal(boolean paramBoolean)
  {
    int i;
    int j;
    if (this.listView != null)
    {
      if (!this.actionBar.getOccupyStatusBar()) {
        break label40;
      }
      i = AndroidUtilities.statusBarHeight;
      i += ActionBar.getCurrentActionBarHeight();
      j = this.fragmentView.getMeasuredHeight();
      if (j != 0) {
        break label45;
      }
    }
    for (;;)
    {
      return;
      label40:
      i = 0;
      break;
      label45:
      this.overScrollHeight = (j - AndroidUtilities.dp(66.0F) - i);
      Object localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).topMargin = i;
      this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      localObject = (FrameLayout.LayoutParams)this.mapViewClip.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).topMargin = i;
      ((FrameLayout.LayoutParams)localObject).height = this.overScrollHeight;
      this.mapViewClip.setLayoutParams((ViewGroup.LayoutParams)localObject);
      if (this.searchListView != null)
      {
        localObject = (FrameLayout.LayoutParams)this.searchListView.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).topMargin = i;
        this.searchListView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
      this.adapter.setOverScrollHeight(this.overScrollHeight);
      localObject = (FrameLayout.LayoutParams)this.mapView.getLayoutParams();
      if (localObject != null)
      {
        ((FrameLayout.LayoutParams)localObject).height = (this.overScrollHeight + AndroidUtilities.dp(10.0F));
        if (this.googleMap != null) {
          this.googleMap.setPadding(AndroidUtilities.dp(70.0F), 0, AndroidUtilities.dp(70.0F), AndroidUtilities.dp(10.0F));
        }
        this.mapView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
      this.adapter.notifyDataSetChanged();
      if (paramBoolean)
      {
        localObject = this.layoutManager;
        if ((this.liveLocationType == 1) || (this.liveLocationType == 2)) {}
        for (i = 66;; i = 0)
        {
          ((LinearLayoutManager)localObject).scrollToPositionWithOffset(0, -AndroidUtilities.dp(i + 32));
          updateClipView(this.layoutManager.findFirstVisibleItemPosition());
          this.listView.post(new Runnable()
          {
            public void run()
            {
              LinearLayoutManager localLinearLayoutManager = LocationActivity.this.layoutManager;
              if ((LocationActivity.this.liveLocationType == 1) || (LocationActivity.this.liveLocationType == 2)) {}
              for (int i = 66;; i = 0)
              {
                localLinearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp(i + 32));
                LocationActivity.this.updateClipView(LocationActivity.this.layoutManager.findFirstVisibleItemPosition());
                return;
              }
            }
          });
          break;
        }
      }
      updateClipView(this.layoutManager.findFirstVisibleItemPosition());
    }
  }
  
  private Location getLastLocation()
  {
    LocationManager localLocationManager = (LocationManager)ApplicationLoader.applicationContext.getSystemService("location");
    List localList = localLocationManager.getProviders(true);
    Location localLocation = null;
    for (int i = localList.size() - 1;; i--) {
      if (i >= 0)
      {
        localLocation = localLocationManager.getLastKnownLocation((String)localList.get(i));
        if (localLocation == null) {}
      }
      else
      {
        return localLocation;
      }
    }
  }
  
  private int getMessageId(TLRPC.Message paramMessage)
  {
    if (paramMessage.from_id != 0) {}
    for (int i = paramMessage.from_id;; i = (int)MessageObject.getDialogId(paramMessage)) {
      return i;
    }
  }
  
  private boolean getRecentLocations()
  {
    boolean bool = false;
    ArrayList localArrayList = (ArrayList)LocationController.getInstance(this.currentAccount).locationsCache.get(this.messageObject.getDialogId());
    Object localObject;
    if ((localArrayList != null) && (localArrayList.isEmpty()))
    {
      fetchRecentLocations(localArrayList);
      int i = (int)this.dialogId;
      if (i >= 0) {
        break label92;
      }
      localObject = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
      if ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).megagroup)) {
        break label92;
      }
    }
    for (;;)
    {
      return bool;
      localArrayList = null;
      break;
      label92:
      localObject = new TLRPC.TL_messages_getRecentLocations();
      final long l = this.messageObject.getDialogId();
      ((TLRPC.TL_messages_getRecentLocations)localObject).peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int)l);
      ((TLRPC.TL_messages_getRecentLocations)localObject).limit = 100;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (LocationActivity.this.googleMap == null) {}
                for (;;)
                {
                  return;
                  TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)paramAnonymousTLObject;
                  int j;
                  for (int i = 0; i < localmessages_Messages.messages.size(); i = j + 1)
                  {
                    j = i;
                    if (!(((TLRPC.Message)localmessages_Messages.messages.get(i)).media instanceof TLRPC.TL_messageMediaGeoLive))
                    {
                      localmessages_Messages.messages.remove(i);
                      j = i - 1;
                    }
                  }
                  MessagesStorage.getInstance(LocationActivity.this.currentAccount).putUsersAndChats(localmessages_Messages.users, localmessages_Messages.chats, true, true);
                  MessagesController.getInstance(LocationActivity.this.currentAccount).putUsers(localmessages_Messages.users, false);
                  MessagesController.getInstance(LocationActivity.this.currentAccount).putChats(localmessages_Messages.chats, false);
                  LocationController.getInstance(LocationActivity.this.currentAccount).locationsCache.put(LocationActivity.20.this.val$dialog_id, localmessages_Messages.messages);
                  NotificationCenter.getInstance(LocationActivity.this.currentAccount).postNotificationName(NotificationCenter.liveLocationsCacheChanged, new Object[] { Long.valueOf(LocationActivity.20.this.val$dialog_id) });
                  LocationActivity.this.fetchRecentLocations(localmessages_Messages.messages);
                }
              }
            });
          }
        }
      });
      if (localArrayList != null) {
        bool = true;
      }
    }
  }
  
  private void onMapInit()
  {
    if (this.googleMap == null) {}
    for (;;)
    {
      return;
      Object localObject1;
      if (this.messageObject != null) {
        if (this.messageObject.isLiveLocation())
        {
          localObject1 = addUserMarker(this.messageObject.messageOwner);
          if (!getRecentLocations()) {
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation)localObject1).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0F));
          }
        }
      }
      try
      {
        for (;;)
        {
          this.googleMap.setMyLocationEnabled(true);
          this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
          this.googleMap.getUiSettings().setZoomControlsEnabled(false);
          this.googleMap.getUiSettings().setCompassEnabled(false);
          this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener()
          {
            public void onMyLocationChange(Location paramAnonymousLocation)
            {
              LocationActivity.this.positionMarker(paramAnonymousLocation);
              LocationController.getInstance(LocationActivity.this.currentAccount).setGoogleMapLocation(paramAnonymousLocation, LocationActivity.this.isFirstLocation);
              LocationActivity.access$3502(LocationActivity.this, false);
            }
          });
          localObject1 = getLastLocation();
          this.myLocation = ((Location)localObject1);
          positionMarker((Location)localObject1);
          if ((!this.checkGpsEnabled) || (getParentActivity() == null)) {
            break;
          }
          this.checkGpsEnabled = false;
          if (!getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
            break;
          }
          Object localObject3;
          Object localObject4;
          try
          {
            if (((LocationManager)ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
              break;
            }
            localObject1 = new org/telegram/ui/ActionBar/AlertDialog$Builder;
            ((AlertDialog.Builder)localObject1).<init>(getParentActivity());
            ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", NUM));
            ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("GpsDisabledAlert", NUM));
            localObject3 = LocaleController.getString("ConnectingToProxyEnable", NUM);
            localObject4 = new org/telegram/ui/LocationActivity$17;
            ((17)localObject4).<init>(this);
            ((AlertDialog.Builder)localObject1).setPositiveButton((CharSequence)localObject3, (DialogInterface.OnClickListener)localObject4);
            ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(((AlertDialog.Builder)localObject1).create());
          }
          catch (Exception localException1)
          {
            FileLog.e(localException1);
          }
          break;
          Object localObject2 = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
          try
          {
            localObject4 = this.googleMap;
            localObject3 = new com/google/android/gms/maps/model/MarkerOptions;
            ((MarkerOptions)localObject3).<init>();
            ((GoogleMap)localObject4).addMarker(((MarkerOptions)localObject3).position((LatLng)localObject2).icon(BitmapDescriptorFactory.fromResource(NUM)));
            localObject2 = CameraUpdateFactory.newLatLngZoom((LatLng)localObject2, this.googleMap.getMaxZoomLevel() - 4.0F);
            this.googleMap.moveCamera((CameraUpdate)localObject2);
            this.firstFocus = false;
            getRecentLocations();
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              FileLog.e(localException3);
            }
          }
          this.userLocation = new Location("network");
          this.userLocation.setLatitude(20.659322D);
          this.userLocation.setLongitude(-11.40625D);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
        }
      }
    }
  }
  
  private void positionMarker(Location paramLocation)
  {
    if (paramLocation == null) {}
    for (;;)
    {
      return;
      this.myLocation = new Location(paramLocation);
      Object localObject = (LiveLocation)this.markersMap.get(UserConfig.getInstance(this.currentAccount).getClientUserId());
      LocationController.SharingLocationInfo localSharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
      if ((localObject != null) && (localSharingLocationInfo != null) && (((LiveLocation)localObject).object.id == localSharingLocationInfo.mid)) {
        ((LiveLocation)localObject).marker.setPosition(new LatLng(paramLocation.getLatitude(), paramLocation.getLongitude()));
      }
      if ((this.messageObject == null) && (this.googleMap != null))
      {
        localObject = new LatLng(paramLocation.getLatitude(), paramLocation.getLongitude());
        if (this.adapter != null)
        {
          if (this.adapter.isPulledUp()) {
            this.adapter.searchGooglePlacesWithQuery(null, this.myLocation);
          }
          this.adapter.setGpsLocation(this.myLocation);
        }
        if (!this.userLocationMoved)
        {
          this.userLocation = new Location(paramLocation);
          if (this.firstWas)
          {
            paramLocation = CameraUpdateFactory.newLatLng((LatLng)localObject);
            this.googleMap.animateCamera(paramLocation);
          }
          else
          {
            this.firstWas = true;
            paramLocation = CameraUpdateFactory.newLatLngZoom((LatLng)localObject, this.googleMap.getMaxZoomLevel() - 4.0F);
            this.googleMap.moveCamera(paramLocation);
          }
        }
      }
      else
      {
        this.adapter.setGpsLocation(this.myLocation);
      }
    }
  }
  
  private void showPermissionAlert(boolean paramBoolean)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", NUM));
    if (paramBoolean) {
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
    }
    for (;;)
    {
      localBuilder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener()
      {
        @TargetApi(9)
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (LocationActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            try
            {
              paramAnonymousDialogInterface = new android/content/Intent;
              paramAnonymousDialogInterface.<init>("android.settings.APPLICATION_DETAILS_SETTINGS");
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              paramAnonymousDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
              LocationActivity.this.getParentActivity().startActivity(paramAnonymousDialogInterface);
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              FileLog.e(paramAnonymousDialogInterface);
            }
          }
        }
      });
      localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
      showDialog(localBuilder.create());
      break;
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocation", NUM));
    }
  }
  
  private void updateClipView(int paramInt)
  {
    if (paramInt == -1) {}
    int i;
    int j;
    Object localObject;
    label46:
    do
    {
      do
      {
        return;
        i = 0;
        j = 0;
        localObject = this.listView.getChildAt(0);
      } while (localObject == null);
      if (paramInt == 0)
      {
        paramInt = ((View)localObject).getTop();
        j = this.overScrollHeight;
        if (paramInt >= 0) {
          break;
        }
        i = paramInt;
        i = j + i;
        j = paramInt;
      }
    } while ((FrameLayout.LayoutParams)this.mapViewClip.getLayoutParams() == null);
    if (i <= 0) {
      if (this.mapView.getVisibility() == 0)
      {
        this.mapView.setVisibility(4);
        this.mapViewClip.setVisibility(4);
      }
    }
    for (;;)
    {
      this.mapViewClip.setTranslationY(Math.min(0, j));
      this.mapView.setTranslationY(Math.max(0, -j / 2));
      if (this.markerImageView != null)
      {
        localObject = this.markerImageView;
        paramInt = -j - AndroidUtilities.dp(42.0F) + i / 2;
        this.markerTop = paramInt;
        ((ImageView)localObject).setTranslationY(paramInt);
        this.markerXImageView.setTranslationY(-j - AndroidUtilities.dp(7.0F) + i / 2);
      }
      if (this.routeButton != null) {
        this.routeButton.setTranslationY(j);
      }
      localObject = (FrameLayout.LayoutParams)this.mapView.getLayoutParams();
      if ((localObject == null) || (((FrameLayout.LayoutParams)localObject).height == this.overScrollHeight + AndroidUtilities.dp(10.0F))) {
        break;
      }
      ((FrameLayout.LayoutParams)localObject).height = (this.overScrollHeight + AndroidUtilities.dp(10.0F));
      if (this.googleMap != null) {
        this.googleMap.setPadding(AndroidUtilities.dp(70.0F), 0, AndroidUtilities.dp(70.0F), AndroidUtilities.dp(10.0F));
      }
      this.mapView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      break;
      i = 0;
      break label46;
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
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    if (AndroidUtilities.isTablet()) {
      this.actionBar.setOccupyStatusBar(false);
    }
    this.actionBar.setAddToContainer(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          LocationActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if (paramAnonymousInt == 2)
          {
            if (LocationActivity.this.googleMap != null) {
              LocationActivity.this.googleMap.setMapType(1);
            }
          }
          else if (paramAnonymousInt == 3)
          {
            if (LocationActivity.this.googleMap != null) {
              LocationActivity.this.googleMap.setMapType(2);
            }
          }
          else if (paramAnonymousInt == 4)
          {
            if (LocationActivity.this.googleMap != null) {
              LocationActivity.this.googleMap.setMapType(4);
            }
          }
          else if (paramAnonymousInt == 1) {
            try
            {
              double d1 = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
              double d2 = LocationActivity.this.messageObject.messageOwner.media.geo._long;
              Activity localActivity = LocationActivity.this.getParentActivity();
              Intent localIntent = new android/content/Intent;
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localIntent.<init>("android.intent.action.VIEW", Uri.parse("geo:" + d1 + "," + d2 + "?q=" + d1 + "," + d2));
              localActivity.startActivity(localIntent);
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
            }
          }
        }
      }
    });
    Object localObject1 = this.actionBar.createMenu();
    FrameLayout localFrameLayout;
    Object localObject2;
    label1154:
    int i;
    label1193:
    float f1;
    label1206:
    int j;
    label1215:
    float f2;
    if (this.messageObject != null) {
      if (this.messageObject.isLiveLocation())
      {
        this.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", NUM));
        this.otherItem = ((ActionBarMenu)localObject1).addItem(0, NUM);
        this.otherItem.addSubItem(2, LocaleController.getString("Map", NUM));
        this.otherItem.addSubItem(3, LocaleController.getString("Satellite", NUM));
        this.otherItem.addSubItem(4, LocaleController.getString("Hybrid", NUM));
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
        localFrameLayout = (FrameLayout)this.fragmentView;
        this.locationButton = new ImageView(paramContext);
        localObject2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
        localObject1 = localObject2;
        if (Build.VERSION.SDK_INT < 21)
        {
          localObject1 = paramContext.getResources().getDrawable(NUM).mutate();
          ((Drawable)localObject1).setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
          localObject1 = new CombinedDrawable((Drawable)localObject1, (Drawable)localObject2, 0, 0);
          ((CombinedDrawable)localObject1).setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
        }
        this.locationButton.setBackgroundDrawable((Drawable)localObject1);
        this.locationButton.setImageResource(NUM);
        this.locationButton.setScaleType(ImageView.ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
        if (Build.VERSION.SDK_INT >= 21)
        {
          localObject1 = new StateListAnimator();
          localObject2 = ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
          ((StateListAnimator)localObject1).addState(new int[] { 16842919 }, (Animator)localObject2);
          localObject2 = ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
          ((StateListAnimator)localObject1).addState(new int[0], (Animator)localObject2);
          this.locationButton.setStateListAnimator((StateListAnimator)localObject1);
          this.locationButton.setOutlineProvider(new ViewOutlineProvider()
          {
            @SuppressLint({"NewApi"})
            public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
            {
              paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
            }
          });
        }
        if (this.messageObject != null)
        {
          this.userLocation = new Location("network");
          this.userLocation.setLatitude(this.messageObject.messageOwner.media.geo.lat);
          this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
        }
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
        this.listView = new RecyclerListView(paramContext);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        localObject1 = this.listView;
        localObject2 = new LocationActivityAdapter(paramContext, this.liveLocationType, this.dialogId);
        this.adapter = ((LocationActivityAdapter)localObject2);
        ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
        this.listView.setVerticalScrollBarEnabled(false);
        localObject1 = this.listView;
        localObject2 = new LinearLayoutManager(paramContext, 1, false)
        {
          public boolean supportsPredictiveItemAnimations()
          {
            return false;
          }
        };
        this.layoutManager = ((LinearLayoutManager)localObject2);
        ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
        localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            if (LocationActivity.this.adapter.getItemCount() == 0) {}
            for (;;)
            {
              return;
              paramAnonymousInt1 = LocationActivity.this.layoutManager.findFirstVisibleItemPosition();
              if (paramAnonymousInt1 != -1)
              {
                LocationActivity.this.updateClipView(paramAnonymousInt1);
                if ((paramAnonymousInt2 > 0) && (!LocationActivity.this.adapter.isPulledUp()))
                {
                  LocationActivity.this.adapter.setPulledUp();
                  if (LocationActivity.this.myLocation != null) {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        LocationActivity.this.adapter.searchGooglePlacesWithQuery(null, LocationActivity.this.myLocation);
                      }
                    });
                  }
                }
              }
            }
          }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
        {
          public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
          {
            if ((paramAnonymousInt == 1) && (LocationActivity.this.messageObject != null) && (!LocationActivity.this.messageObject.isLiveLocation())) {
              if (LocationActivity.this.googleMap != null) {
                LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.this.messageObject.messageOwner.media.geo.lat, LocationActivity.this.messageObject.messageOwner.media.geo._long), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0F));
              }
            }
            for (;;)
            {
              return;
              if ((paramAnonymousInt == 1) && (LocationActivity.this.liveLocationType != 2))
              {
                if ((LocationActivity.this.delegate != null) && (LocationActivity.this.userLocation != null))
                {
                  paramAnonymousView = new TLRPC.TL_messageMediaGeo();
                  paramAnonymousView.geo = new TLRPC.TL_geoPoint();
                  paramAnonymousView.geo.lat = LocationActivity.this.userLocation.getLatitude();
                  paramAnonymousView.geo._long = LocationActivity.this.userLocation.getLongitude();
                  LocationActivity.this.delegate.didSelectLocation(paramAnonymousView, LocationActivity.this.liveLocationType);
                }
                LocationActivity.this.finishFragment();
              }
              else if (((paramAnonymousInt == 2) && (LocationActivity.this.liveLocationType == 1)) || ((paramAnonymousInt == 1) && (LocationActivity.this.liveLocationType == 2)) || ((paramAnonymousInt == 3) && (LocationActivity.this.liveLocationType == 3)))
              {
                if (LocationController.getInstance(LocationActivity.this.currentAccount).isSharingLocation(LocationActivity.this.dialogId))
                {
                  LocationController.getInstance(LocationActivity.this.currentAccount).removeSharingLocation(LocationActivity.this.dialogId);
                  LocationActivity.this.finishFragment();
                }
                else if ((LocationActivity.this.delegate != null) && (LocationActivity.this.getParentActivity() != null) && (LocationActivity.this.myLocation != null))
                {
                  paramAnonymousView = null;
                  if ((int)LocationActivity.this.dialogId > 0) {
                    paramAnonymousView = MessagesController.getInstance(LocationActivity.this.currentAccount).getUser(Integer.valueOf((int)LocationActivity.this.dialogId));
                  }
                  LocationActivity.this.showDialog(AlertsCreator.createLocationUpdateDialog(LocationActivity.this.getParentActivity(), paramAnonymousView, new MessagesStorage.IntCallback()
                  {
                    public void run(int paramAnonymous2Int)
                    {
                      TLRPC.TL_messageMediaGeoLive localTL_messageMediaGeoLive = new TLRPC.TL_messageMediaGeoLive();
                      localTL_messageMediaGeoLive.geo = new TLRPC.TL_geoPoint();
                      localTL_messageMediaGeoLive.geo.lat = LocationActivity.this.myLocation.getLatitude();
                      localTL_messageMediaGeoLive.geo._long = LocationActivity.this.myLocation.getLongitude();
                      localTL_messageMediaGeoLive.period = paramAnonymous2Int;
                      LocationActivity.this.delegate.didSelectLocation(localTL_messageMediaGeoLive, LocationActivity.this.liveLocationType);
                      LocationActivity.this.finishFragment();
                    }
                  }));
                }
              }
              else
              {
                paramAnonymousView = LocationActivity.this.adapter.getItem(paramAnonymousInt);
                if ((paramAnonymousView instanceof TLRPC.TL_messageMediaVenue))
                {
                  if ((paramAnonymousView != null) && (LocationActivity.this.delegate != null)) {
                    LocationActivity.this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue)paramAnonymousView, LocationActivity.this.liveLocationType);
                  }
                  LocationActivity.this.finishFragment();
                }
                else if ((paramAnonymousView instanceof LocationActivity.LiveLocation))
                {
                  LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LocationActivity.LiveLocation)paramAnonymousView).marker.getPosition(), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0F));
                }
              }
            }
          }
        });
        this.adapter.setDelegate(new BaseLocationAdapter.BaseLocationAdapterDelegate()
        {
          public void didLoadedSearchResult(ArrayList<TLRPC.TL_messageMediaVenue> paramAnonymousArrayList)
          {
            if ((!LocationActivity.this.wasResults) && (!paramAnonymousArrayList.isEmpty())) {
              LocationActivity.access$2202(LocationActivity.this, true);
            }
          }
        });
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        localFrameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        this.mapView = new MapView(paramContext)
        {
          public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
          {
            if (LocationActivity.this.messageObject == null)
            {
              if (paramAnonymousMotionEvent.getAction() != 0) {
                break label314;
              }
              if (LocationActivity.this.animatorSet != null) {
                LocationActivity.this.animatorSet.cancel();
              }
              LocationActivity.access$2302(LocationActivity.this, new AnimatorSet());
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
                  LocationActivity.access$2702(LocationActivity.this, true);
                }
                if ((LocationActivity.this.googleMap != null) && (LocationActivity.this.userLocation != null))
                {
                  LocationActivity.this.userLocation.setLatitude(LocationActivity.this.googleMap.getCameraPosition().target.latitude);
                  LocationActivity.this.userLocation.setLongitude(LocationActivity.this.googleMap.getCameraPosition().target.longitude);
                }
                LocationActivity.this.adapter.setCustomLocation(LocationActivity.this.userLocation);
              }
              return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
              label314:
              if (paramAnonymousMotionEvent.getAction() == 1)
              {
                if (LocationActivity.this.animatorSet != null) {
                  LocationActivity.this.animatorSet.cancel();
                }
                LocationActivity.access$2302(LocationActivity.this, new AnimatorSet());
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
                    LocationActivity.10.this.val$map.onCreate(null);
                    MapsInitializer.initialize(LocationActivity.this.getParentActivity());
                    MapView localMapView = LocationActivity.this.mapView;
                    OnMapReadyCallback local1 = new org/telegram/ui/LocationActivity$10$1$1;
                    local1.<init>(this);
                    localMapView.getMapAsync(local1);
                    LocationActivity.access$3102(LocationActivity.this, true);
                    if (LocationActivity.this.onResumeCalled) {
                      LocationActivity.this.mapView.onResume();
                    }
                    return;
                  }
                  catch (Exception localException)
                  {
                    for (;;)
                    {
                      FileLog.e(localException);
                    }
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
        localObject1 = new View(paramContext);
        ((View)localObject1).setBackgroundResource(NUM);
        this.mapViewClip.addView((View)localObject1, LayoutHelper.createFrame(-1, 3, 83));
        if (this.messageObject != null) {
          break label1442;
        }
        this.markerImageView = new ImageView(paramContext);
        this.markerImageView.setImageResource(NUM);
        this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(24, 42, 49));
        this.markerXImageView = new ImageView(paramContext);
        this.markerXImageView.setAlpha(0.0F);
        this.markerXImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_markerX"), PorterDuff.Mode.MULTIPLY));
        this.markerXImageView.setImageResource(NUM);
        this.mapViewClip.addView(this.markerXImageView, LayoutHelper.createFrame(14, 14, 49));
        this.emptyView = new EmptyTextProgressView(paramContext);
        this.emptyView.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setVisibility(8);
        localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
        this.searchListView = new RecyclerListView(paramContext);
        this.searchListView.setVisibility(8);
        this.searchListView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
        localObject1 = this.searchListView;
        paramContext = new LocationActivitySearchAdapter(paramContext);
        this.searchAdapter = paramContext;
        ((RecyclerListView)localObject1).setAdapter(paramContext);
        localFrameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
          {
            if ((paramAnonymousInt == 1) && (LocationActivity.this.searching) && (LocationActivity.this.searchWas)) {
              AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
            }
          }
        });
        this.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
        {
          public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
          {
            paramAnonymousView = LocationActivity.this.searchAdapter.getItem(paramAnonymousInt);
            if ((paramAnonymousView != null) && (LocationActivity.this.delegate != null)) {
              LocationActivity.this.delegate.didSelectLocation(paramAnonymousView, LocationActivity.this.liveLocationType);
            }
            LocationActivity.this.finishFragment();
          }
        });
        if ((this.messageObject == null) || (this.messageObject.isLiveLocation())) {
          break label1929;
        }
        paramContext = this.mapViewClip;
        localObject1 = this.locationButton;
        if (Build.VERSION.SDK_INT < 21) {
          break label1894;
        }
        i = 56;
        if (Build.VERSION.SDK_INT < 21) {
          break label1901;
        }
        f1 = 56.0F;
        if (!LocaleController.isRTL) {
          break label1909;
        }
        j = 3;
        if (!LocaleController.isRTL) {
          break label1915;
        }
        f2 = 14.0F;
        label1226:
        if (!LocaleController.isRTL) {
          break label1921;
        }
      }
    }
    label1442:
    label1763:
    label1776:
    label1785:
    label1796:
    label1866:
    label1874:
    label1880:
    label1886:
    label1894:
    label1901:
    label1909:
    label1915:
    label1921:
    for (float f3 = 0.0F;; f3 = 14.0F)
    {
      paramContext.addView((View)localObject1, LayoutHelper.createFrame(i, f1, j | 0x50, f2, 0.0F, f3, 43.0F));
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
          for (;;)
          {
            return;
            if (LocationActivity.this.messageObject != null)
            {
              if ((LocationActivity.this.myLocation != null) && (LocationActivity.this.googleMap != null)) {
                LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude()), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0F));
              }
            }
            else if ((LocationActivity.this.myLocation != null) && (LocationActivity.this.googleMap != null))
            {
              paramAnonymousView = new AnimatorSet();
              paramAnonymousView.setDuration(200L);
              paramAnonymousView.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[] { 0.0F }));
              paramAnonymousView.start();
              LocationActivity.this.adapter.setCustomLocation(null);
              LocationActivity.access$2702(LocationActivity.this, false);
              LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude())));
            }
          }
        }
      });
      if (this.messageObject == null) {
        this.locationButton.setAlpha(0.0F);
      }
      localFrameLayout.addView(this.actionBar);
      return this.fragmentView;
      if ((this.messageObject.messageOwner.media.title != null) && (this.messageObject.messageOwner.media.title.length() > 0)) {
        this.actionBar.setTitle(LocaleController.getString("SharedPlace", NUM));
      }
      for (;;)
      {
        ((ActionBarMenu)localObject1).addItem(1, NUM);
        break;
        this.actionBar.setTitle(LocaleController.getString("ChatLocation", NUM));
      }
      this.actionBar.setTitle(LocaleController.getString("ShareLocation", NUM));
      ((ActionBarMenu)localObject1).addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
      {
        public void onSearchCollapse()
        {
          LocationActivity.access$202(LocationActivity.this, false);
          LocationActivity.access$802(LocationActivity.this, false);
          LocationActivity.this.otherItem.setVisibility(0);
          LocationActivity.this.searchListView.setEmptyView(null);
          LocationActivity.this.listView.setVisibility(0);
          LocationActivity.this.mapViewClip.setVisibility(0);
          LocationActivity.this.searchListView.setVisibility(8);
          LocationActivity.this.emptyView.setVisibility(8);
          LocationActivity.this.searchAdapter.searchDelayed(null, null);
        }
        
        public void onSearchExpand()
        {
          LocationActivity.access$202(LocationActivity.this, true);
          LocationActivity.this.otherItem.setVisibility(8);
          LocationActivity.this.listView.setVisibility(8);
          LocationActivity.this.mapViewClip.setVisibility(8);
          LocationActivity.this.searchListView.setVisibility(0);
          LocationActivity.this.searchListView.setEmptyView(LocationActivity.this.emptyView);
        }
        
        public void onTextChanged(EditText paramAnonymousEditText)
        {
          if (LocationActivity.this.searchAdapter == null) {}
          for (;;)
          {
            return;
            paramAnonymousEditText = paramAnonymousEditText.getText().toString();
            if (paramAnonymousEditText.length() != 0) {
              LocationActivity.access$802(LocationActivity.this, true);
            }
            LocationActivity.this.searchAdapter.searchDelayed(paramAnonymousEditText, LocationActivity.this.userLocation);
          }
        }
      }).getSearchField().setHint(LocaleController.getString("Search", NUM));
      break;
      if (this.messageObject.isLiveLocation()) {
        break label1154;
      }
      this.routeButton = new ImageView(paramContext);
      localObject2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
      localObject1 = localObject2;
      if (Build.VERSION.SDK_INT < 21)
      {
        paramContext = paramContext.getResources().getDrawable(NUM).mutate();
        paramContext.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        localObject1 = new CombinedDrawable(paramContext, (Drawable)localObject2, 0, 0);
        ((CombinedDrawable)localObject1).setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
      }
      this.routeButton.setBackgroundDrawable((Drawable)localObject1);
      this.routeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
      this.routeButton.setImageResource(NUM);
      this.routeButton.setScaleType(ImageView.ScaleType.CENTER);
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramContext = new StateListAnimator();
        localObject1 = ObjectAnimator.ofFloat(this.routeButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        paramContext.addState(new int[] { 16842919 }, (Animator)localObject1);
        localObject1 = ObjectAnimator.ofFloat(this.routeButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        paramContext.addState(new int[0], (Animator)localObject1);
        this.routeButton.setStateListAnimator(paramContext);
        this.routeButton.setOutlineProvider(new ViewOutlineProvider()
        {
          @SuppressLint({"NewApi"})
          public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
          {
            paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
          }
        });
      }
      paramContext = this.routeButton;
      if (Build.VERSION.SDK_INT >= 21)
      {
        i = 56;
        if (Build.VERSION.SDK_INT < 21) {
          break label1866;
        }
        f1 = 56.0F;
        if (!LocaleController.isRTL) {
          break label1874;
        }
        j = 3;
        if (!LocaleController.isRTL) {
          break label1880;
        }
        f2 = 14.0F;
        if (!LocaleController.isRTL) {
          break label1886;
        }
      }
      for (f3 = 0.0F;; f3 = 14.0F)
      {
        localFrameLayout.addView(paramContext, LayoutHelper.createFrame(i, f1, j | 0x50, f2, 0.0F, f3, 37.0F));
        this.routeButton.setOnClickListener(new View.OnClickListener()
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
            for (;;)
            {
              return;
              if (LocationActivity.this.myLocation != null) {
                try
                {
                  paramAnonymousView = new android/content/Intent;
                  paramAnonymousView.<init>("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[] { Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo.lat), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo._long) })));
                  LocationActivity.this.getParentActivity().startActivity(paramAnonymousView);
                }
                catch (Exception paramAnonymousView)
                {
                  FileLog.e(paramAnonymousView);
                }
              }
            }
          }
        });
        this.adapter.setMessageObject(this.messageObject);
        break;
        i = 60;
        break label1763;
        f1 = 60.0F;
        break label1776;
        j = 5;
        break label1785;
        f2 = 0.0F;
        break label1796;
      }
      i = 60;
      break label1193;
      f1 = 60.0F;
      break label1206;
      j = 5;
      break label1215;
      f2 = 0.0F;
      break label1226;
    }
    label1929:
    localObject1 = this.mapViewClip;
    paramContext = this.locationButton;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = 56;
      label1951:
      if (Build.VERSION.SDK_INT < 21) {
        break label2028;
      }
      f1 = 56.0F;
      label1964:
      if (!LocaleController.isRTL) {
        break label2036;
      }
      j = 3;
      label1973:
      if (!LocaleController.isRTL) {
        break label2042;
      }
      f2 = 14.0F;
      label1984:
      if (!LocaleController.isRTL) {
        break label2048;
      }
    }
    label2028:
    label2036:
    label2042:
    label2048:
    for (f3 = 0.0F;; f3 = 14.0F)
    {
      ((FrameLayout)localObject1).addView(paramContext, LayoutHelper.createFrame(i, f1, j | 0x50, f2, 0.0F, f3, 14.0F));
      break;
      i = 60;
      break label1951;
      f1 = 60.0F;
      break label1964;
      j = 5;
      break label1973;
      f2 = 0.0F;
      break label1984;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.closeChats) {
      removeSelfFromStack();
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.locationPermissionGranted)
      {
        if (this.googleMap != null) {
          try
          {
            this.googleMap.setMyLocationEnabled(true);
          }
          catch (Exception paramVarArgs)
          {
            FileLog.e(paramVarArgs);
          }
        }
      }
      else
      {
        Object localObject;
        if (paramInt1 == NotificationCenter.didReceivedNewMessages)
        {
          if ((((Long)paramVarArgs[0]).longValue() == this.dialogId) && (this.messageObject != null))
          {
            paramVarArgs = (ArrayList)paramVarArgs[1];
            paramInt2 = 0;
            for (paramInt1 = 0; paramInt1 < paramVarArgs.size(); paramInt1++)
            {
              localObject = (MessageObject)paramVarArgs.get(paramInt1);
              if (((MessageObject)localObject).isLiveLocation())
              {
                addUserMarker(((MessageObject)localObject).messageOwner);
                paramInt2 = 1;
              }
            }
            if ((paramInt2 != 0) && (this.adapter != null)) {
              this.adapter.setLiveLocations(this.markers);
            }
          }
        }
        else if ((paramInt1 != NotificationCenter.messagesDeleted) && (paramInt1 == NotificationCenter.replaceMessagesObjects))
        {
          long l = ((Long)paramVarArgs[0]).longValue();
          if ((l == this.dialogId) && (this.messageObject != null))
          {
            paramInt2 = 0;
            paramVarArgs = (ArrayList)paramVarArgs[1];
            paramInt1 = 0;
            if (paramInt1 < paramVarArgs.size())
            {
              MessageObject localMessageObject = (MessageObject)paramVarArgs.get(paramInt1);
              if (!localMessageObject.isLiveLocation()) {}
              for (;;)
              {
                paramInt1++;
                break;
                LiveLocation localLiveLocation = (LiveLocation)this.markersMap.get(getMessageId(localMessageObject.messageOwner));
                if (localLiveLocation != null)
                {
                  localObject = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(l);
                  if ((localObject == null) || (((LocationController.SharingLocationInfo)localObject).mid != localMessageObject.getId())) {
                    localLiveLocation.marker.setPosition(new LatLng(localMessageObject.messageOwner.media.geo.lat, localMessageObject.messageOwner.media.geo._long));
                  }
                  paramInt2 = 1;
                }
              }
            }
            if ((paramInt2 != 0) && (this.adapter != null)) {
              this.adapter.updateLiveLocations();
            }
          }
        }
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local21 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor() {}
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon"), new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground"), new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground"), new ThemeDescription(this.routeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon"), new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground"), new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground"), new ThemeDescription(this.markerXImageView, 0, null, null, null, null, "location_markerX"), new ThemeDescription(this.listView, 0, new Class[] { GraySectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GraySectionCell.class }, null, null, null, "graySection"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable }, local21, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundPink"), new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress"), new ThemeDescription(null, 0, null, null, null, null, "location_placeLocationBackground"), new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { SendLocationCell.class }, new String[] { "imageView" }, null, null, null, "location_sendLocationIcon"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[] { SendLocationCell.class }, new String[] { "imageView" }, null, null, null, "location_sendLocationBackground"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[] { SendLocationCell.class }, new String[] { "imageView" }, null, null, null, "location_sendLiveLocationBackground"), new ThemeDescription(this.listView, 0, new Class[] { SendLocationCell.class }, new String[] { "titleTextView" }, null, null, null, "windowBackgroundWhiteBlueText7"), new ThemeDescription(this.listView, 0, new Class[] { SendLocationCell.class }, new String[] { "accurateTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { LocationCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { LocationCell.class }, new String[] { "addressTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { LocationCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.searchListView, 0, new Class[] { LocationCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.searchListView, 0, new Class[] { LocationCell.class }, new String[] { "addressTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationLoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { LocationLoadingCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationPoweredCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationPoweredCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LocationPoweredCell.class }, new String[] { "textView2" }, null, null, null, "windowBackgroundWhiteGrayText3") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.swipeBackEnabled = false;
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
    if ((this.messageObject != null) && (this.messageObject.isLiveLocation()))
    {
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedNewMessages);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.replaceMessagesObjects);
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedNewMessages);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.replaceMessagesObjects);
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
      if (this.updateRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
        this.updateRunnable = null;
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
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
        FileLog.e(localException);
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
        FileLog.e(localThrowable);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
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
        updateClipView(this.layoutManager.findFirstVisibleItemPosition());
        return;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        continue;
        if (this.fragmentView != null) {
          ((FrameLayout)this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
        }
      }
    }
  }
  
  public void setDelegate(LocationActivityDelegate paramLocationActivityDelegate)
  {
    this.delegate = paramLocationActivityDelegate;
  }
  
  public void setDialogId(long paramLong)
  {
    this.dialogId = paramLong;
  }
  
  public void setMessageObject(MessageObject paramMessageObject)
  {
    this.messageObject = paramMessageObject;
    this.dialogId = this.messageObject.getDialogId();
  }
  
  public class LiveLocation
  {
    public TLRPC.Chat chat;
    public int id;
    public Marker marker;
    public TLRPC.Message object;
    public TLRPC.User user;
    
    public LiveLocation() {}
  }
  
  public static abstract interface LocationActivityDelegate
  {
    public abstract void didSelectLocation(TLRPC.MessageMedia paramMessageMedia, int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/LocationActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */