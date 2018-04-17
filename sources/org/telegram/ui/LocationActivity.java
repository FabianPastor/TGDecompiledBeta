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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentLocations;
import org.telegram.tgnet.TLRPC.User;
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
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class LocationActivity extends BaseFragment implements NotificationCenterDelegate {
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
    private int overScrollHeight = ((AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f));
    private ImageView routeButton;
    private LocationActivitySearchAdapter searchAdapter;
    private RecyclerListView searchListView;
    private boolean searchWas;
    private boolean searching;
    private Runnable updateRunnable;
    private Location userLocation;
    private boolean userLocationMoved = false;
    private boolean wasResults;

    /* renamed from: org.telegram.ui.LocationActivity$4 */
    class C14604 extends ViewOutlineProvider {
        C14604() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    public class LiveLocation {
        public Chat chat;
        public int id;
        public Marker marker;
        public Message object;
        public User user;
    }

    public interface LocationActivityDelegate {
        void didSelectLocation(MessageMedia messageMedia, int i);
    }

    /* renamed from: org.telegram.ui.LocationActivity$1 */
    class C21691 extends ActionBarMenuOnItemClick {
        C21691() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                LocationActivity.this.finishFragment();
            } else if (id == 2) {
                if (LocationActivity.this.googleMap != null) {
                    LocationActivity.this.googleMap.setMapType(1);
                }
            } else if (id == 3) {
                if (LocationActivity.this.googleMap != null) {
                    LocationActivity.this.googleMap.setMapType(2);
                }
            } else if (id == 4) {
                if (LocationActivity.this.googleMap != null) {
                    LocationActivity.this.googleMap.setMapType(4);
                }
            } else if (id == 1) {
                try {
                    double lat = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                    double lon = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                    Activity parentActivity = LocationActivity.this.getParentActivity();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("geo:");
                    stringBuilder.append(lat);
                    stringBuilder.append(",");
                    stringBuilder.append(lon);
                    stringBuilder.append("?q=");
                    stringBuilder.append(lat);
                    stringBuilder.append(",");
                    stringBuilder.append(lon);
                    parentActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$2 */
    class C21702 extends ActionBarMenuItemSearchListener {
        C21702() {
        }

        public void onSearchExpand() {
            LocationActivity.this.searching = true;
            LocationActivity.this.otherItem.setVisibility(8);
            LocationActivity.this.listView.setVisibility(8);
            LocationActivity.this.mapViewClip.setVisibility(8);
            LocationActivity.this.searchListView.setVisibility(0);
            LocationActivity.this.searchListView.setEmptyView(LocationActivity.this.emptyView);
        }

        public void onSearchCollapse() {
            LocationActivity.this.searching = false;
            LocationActivity.this.searchWas = false;
            LocationActivity.this.otherItem.setVisibility(0);
            LocationActivity.this.searchListView.setEmptyView(null);
            LocationActivity.this.listView.setVisibility(0);
            LocationActivity.this.mapViewClip.setVisibility(0);
            LocationActivity.this.searchListView.setVisibility(8);
            LocationActivity.this.emptyView.setVisibility(8);
            LocationActivity.this.searchAdapter.searchDelayed(null, null);
        }

        public void onTextChanged(EditText editText) {
            if (LocationActivity.this.searchAdapter != null) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    LocationActivity.this.searchWas = true;
                }
                LocationActivity.this.searchAdapter.searchDelayed(text, LocationActivity.this.userLocation);
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$6 */
    class C21716 extends OnScrollListener {

        /* renamed from: org.telegram.ui.LocationActivity$6$1 */
        class C14611 implements Runnable {
            C14611() {
            }

            public void run() {
                LocationActivity.this.adapter.searchGooglePlacesWithQuery(null, LocationActivity.this.myLocation);
            }
        }

        C21716() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (LocationActivity.this.adapter.getItemCount() != 0) {
                int position = LocationActivity.this.layoutManager.findFirstVisibleItemPosition();
                if (position != -1) {
                    LocationActivity.this.updateClipView(position);
                    if (dy > 0 && !LocationActivity.this.adapter.isPulledUp()) {
                        LocationActivity.this.adapter.setPulledUp();
                        if (LocationActivity.this.myLocation != null) {
                            AndroidUtilities.runOnUIThread(new C14611());
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$7 */
    class C21737 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.LocationActivity$7$1 */
        class C21721 implements IntCallback {
            C21721() {
            }

            public void run(int param) {
                TL_messageMediaGeoLive location = new TL_messageMediaGeoLive();
                location.geo = new TL_geoPoint();
                location.geo.lat = LocationActivity.this.myLocation.getLatitude();
                location.geo._long = LocationActivity.this.myLocation.getLongitude();
                location.period = param;
                LocationActivity.this.delegate.didSelectLocation(location, LocationActivity.this.liveLocationType);
                LocationActivity.this.finishFragment();
            }
        }

        C21737() {
        }

        public void onItemClick(View view, int position) {
            if (position != 1 || LocationActivity.this.messageObject == null || LocationActivity.this.messageObject.isLiveLocation()) {
                if (position == 1 && LocationActivity.this.liveLocationType != 2) {
                    if (!(LocationActivity.this.delegate == null || LocationActivity.this.userLocation == null)) {
                        TL_messageMediaGeo location = new TL_messageMediaGeo();
                        location.geo = new TL_geoPoint();
                        location.geo.lat = LocationActivity.this.userLocation.getLatitude();
                        location.geo._long = LocationActivity.this.userLocation.getLongitude();
                        LocationActivity.this.delegate.didSelectLocation(location, LocationActivity.this.liveLocationType);
                    }
                    LocationActivity.this.finishFragment();
                } else if ((position != 2 || LocationActivity.this.liveLocationType != 1) && ((position != 1 || LocationActivity.this.liveLocationType != 2) && (position != 3 || LocationActivity.this.liveLocationType != 3))) {
                    Object object = LocationActivity.this.adapter.getItem(position);
                    if (object instanceof TL_messageMediaVenue) {
                        if (!(object == null || LocationActivity.this.delegate == null)) {
                            LocationActivity.this.delegate.didSelectLocation((TL_messageMediaVenue) object, LocationActivity.this.liveLocationType);
                        }
                        LocationActivity.this.finishFragment();
                    } else if (object instanceof LiveLocation) {
                        LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation) object).marker.getPosition(), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else if (LocationController.getInstance(LocationActivity.this.currentAccount).isSharingLocation(LocationActivity.this.dialogId)) {
                    LocationController.getInstance(LocationActivity.this.currentAccount).removeSharingLocation(LocationActivity.this.dialogId);
                    LocationActivity.this.finishFragment();
                } else {
                    if (LocationActivity.this.delegate != null) {
                        if (LocationActivity.this.getParentActivity() != null) {
                            if (LocationActivity.this.myLocation != null) {
                                User user = null;
                                if (((int) LocationActivity.this.dialogId) > 0) {
                                    user = MessagesController.getInstance(LocationActivity.this.currentAccount).getUser(Integer.valueOf((int) LocationActivity.this.dialogId));
                                }
                                LocationActivity.this.showDialog(AlertsCreator.createLocationUpdateDialog(LocationActivity.this.getParentActivity(), user, new C21721()));
                            }
                        }
                    }
                }
            } else if (LocationActivity.this.googleMap != null) {
                LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.this.messageObject.messageOwner.media.geo.lat, LocationActivity.this.messageObject.messageOwner.media.geo._long), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$8 */
    class C21748 implements BaseLocationAdapterDelegate {
        C21748() {
        }

        public void didLoadedSearchResult(ArrayList<TL_messageMediaVenue> places) {
            if (!LocationActivity.this.wasResults && !places.isEmpty()) {
                LocationActivity.this.wasResults = true;
            }
        }
    }

    public LocationActivity(int liveLocation) {
        this.liveLocationType = liveLocation;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.swipeBackEnabled = false;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        if (this.messageObject != null && this.messageObject.isLiveLocation()) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedNewMessages);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.replaceMessagesObjects);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.replaceMessagesObjects);
        try {
            if (this.mapView != null) {
                this.mapView.onDestroy();
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        if (this.adapter != null) {
            this.adapter.destroy();
        }
        if (this.searchAdapter != null) {
            this.searchAdapter.destroy();
        }
        if (this.updateRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            this.updateRunnable = null;
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            r0.actionBar.setOccupyStatusBar(false);
        }
        r0.actionBar.setAddToContainer(false);
        r0.actionBar.setActionBarMenuOnItemClick(new C21691());
        ActionBarMenu menu = r0.actionBar.createMenu();
        if (r0.messageObject == null) {
            r0.actionBar.setTitle(LocaleController.getString("ShareLocation", R.string.ShareLocation));
            menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C21702()).getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        } else if (r0.messageObject.isLiveLocation()) {
            r0.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation));
        } else {
            if (r0.messageObject.messageOwner.media.title == null || r0.messageObject.messageOwner.media.title.length() <= 0) {
                r0.actionBar.setTitle(LocaleController.getString("ChatLocation", R.string.ChatLocation));
            } else {
                r0.actionBar.setTitle(LocaleController.getString("SharedPlace", R.string.SharedPlace));
            }
            menu.addItem(1, (int) R.drawable.share);
        }
        r0.otherItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        r0.otherItem.addSubItem(2, LocaleController.getString("Map", R.string.Map));
        r0.otherItem.addSubItem(3, LocaleController.getString("Satellite", R.string.Satellite));
        r0.otherItem.addSubItem(4, LocaleController.getString("Hybrid", R.string.Hybrid));
        r0.fragmentView = new FrameLayout(context2) {
            private boolean first = true;

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (changed) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                }
            }
        };
        FrameLayout frameLayout = r0.fragmentView;
        r0.locationButton = new ImageView(context2);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        r0.locationButton.setBackgroundDrawable(drawable);
        r0.locationButton.setImageResource(R.drawable.myloc_on);
        r0.locationButton.setScaleType(ScaleType.CENTER);
        r0.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.locationButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(r0.locationButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.locationButton.setStateListAnimator(animator);
            r0.locationButton.setOutlineProvider(new C14604());
        }
        if (r0.messageObject != null) {
            r0.userLocation = new Location("network");
            r0.userLocation.setLatitude(r0.messageObject.messageOwner.media.geo.lat);
            r0.userLocation.setLongitude(r0.messageObject.messageOwner.media.geo._long);
        }
        r0.searchWas = false;
        r0.searching = false;
        r0.mapViewClip = new FrameLayout(context2);
        r0.mapViewClip.setBackgroundDrawable(new MapPlaceholderDrawable());
        if (r0.adapter != null) {
            r0.adapter.destroy();
        }
        if (r0.searchAdapter != null) {
            r0.searchAdapter.destroy();
        }
        r0.listView = new RecyclerListView(context2);
        r0.listView.setItemAnimator(null);
        r0.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = r0.listView;
        Adapter locationActivityAdapter = new LocationActivityAdapter(context2, r0.liveLocationType, r0.dialogId);
        r0.adapter = locationActivityAdapter;
        recyclerListView.setAdapter(locationActivityAdapter);
        r0.listView.setVerticalScrollBarEnabled(false);
        recyclerListView = r0.listView;
        LayoutManager c23365 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        r0.layoutManager = c23365;
        recyclerListView.setLayoutManager(c23365);
        frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
        r0.listView.setOnScrollListener(new C21716());
        r0.listView.setOnItemClickListener(new C21737());
        r0.adapter.setDelegate(new C21748());
        r0.adapter.setOverScrollHeight(r0.overScrollHeight);
        frameLayout.addView(r0.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        r0.mapView = new MapView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (LocationActivity.this.messageObject == null) {
                    AnimatorSet access$2300;
                    Animator[] animatorArr;
                    if (ev.getAction() == 0) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$2300 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", new float[]{(float) (LocationActivity.this.markerTop + (-AndroidUtilities.dp(10.0f)))});
                        animatorArr[1] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", new float[]{1.0f});
                        access$2300.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    } else if (ev.getAction() == 1) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$2300 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", new float[]{(float) LocationActivity.this.markerTop});
                        animatorArr[1] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", new float[]{0.0f});
                        access$2300.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    }
                    if (ev.getAction() == 2) {
                        if (!LocationActivity.this.userLocationMoved) {
                            access$2300 = new AnimatorSet();
                            access$2300.setDuration(200);
                            access$2300.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[]{1.0f}));
                            access$2300.start();
                            LocationActivity.this.userLocationMoved = true;
                        }
                        if (!(LocationActivity.this.googleMap == null || LocationActivity.this.userLocation == null)) {
                            LocationActivity.this.userLocation.setLatitude(LocationActivity.this.googleMap.getCameraPosition().target.latitude);
                            LocationActivity.this.userLocation.setLongitude(LocationActivity.this.googleMap.getCameraPosition().target.longitude);
                        }
                        LocationActivity.this.adapter.setCustomLocation(LocationActivity.this.userLocation);
                    }
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        final MapView map = r0.mapView;
        new Thread(new Runnable() {

            /* renamed from: org.telegram.ui.LocationActivity$10$1 */
            class C14571 implements Runnable {

                /* renamed from: org.telegram.ui.LocationActivity$10$1$1 */
                class C21681 implements OnMapReadyCallback {
                    C21681() {
                    }

                    public void onMapReady(GoogleMap map) {
                        LocationActivity.this.googleMap = map;
                        LocationActivity.this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                        LocationActivity.this.onMapInit();
                    }
                }

                C14571() {
                }

                public void run() {
                    if (LocationActivity.this.mapView != null && LocationActivity.this.getParentActivity() != null) {
                        try {
                            map.onCreate(null);
                            MapsInitializer.initialize(LocationActivity.this.getParentActivity());
                            LocationActivity.this.mapView.getMapAsync(new C21681());
                            LocationActivity.this.mapsInitialized = true;
                            if (LocationActivity.this.onResumeCalled) {
                                LocationActivity.this.mapView.onResume();
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            }

            public void run() {
                try {
                    map.onCreate(null);
                } catch (Exception e) {
                }
                AndroidUtilities.runOnUIThread(new C14571());
            }
        }).start();
        View shadow = new View(context2);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        r0.mapViewClip.addView(shadow, LayoutHelper.createFrame(-1, 3, 83));
        if (r0.messageObject == null) {
            r0.markerImageView = new ImageView(context2);
            r0.markerImageView.setImageResource(R.drawable.map_pin);
            r0.mapViewClip.addView(r0.markerImageView, LayoutHelper.createFrame(24, 42, 49));
            r0.markerXImageView = new ImageView(context2);
            r0.markerXImageView.setAlpha(0.0f);
            r0.markerXImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_markerX), Mode.MULTIPLY));
            r0.markerXImageView.setImageResource(R.drawable.place_x);
            r0.mapViewClip.addView(r0.markerXImageView, LayoutHelper.createFrame(14, 14, 49));
            r0.emptyView = new EmptyTextProgressView(context2);
            r0.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
            r0.emptyView.setShowAtCenter(true);
            r0.emptyView.setVisibility(8);
            frameLayout.addView(r0.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            r0.searchListView = new RecyclerListView(context2);
            r0.searchListView.setVisibility(8);
            r0.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            RecyclerListView recyclerListView2 = r0.searchListView;
            Adapter locationActivitySearchAdapter = new LocationActivitySearchAdapter(context2);
            r0.searchAdapter = locationActivitySearchAdapter;
            recyclerListView2.setAdapter(locationActivitySearchAdapter);
            frameLayout.addView(r0.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            r0.searchListView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            r0.searchListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    TL_messageMediaVenue object = LocationActivity.this.searchAdapter.getItem(position);
                    if (!(object == null || LocationActivity.this.delegate == null)) {
                        LocationActivity.this.delegate.didSelectLocation(object, LocationActivity.this.liveLocationType);
                    }
                    LocationActivity.this.finishFragment();
                }
            });
        } else if (!r0.messageObject.isLiveLocation()) {
            r0.routeButton = new ImageView(context2);
            Drawable drawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
            if (VERSION.SDK_INT < 21) {
                drawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                shadowDrawable = new CombinedDrawable(drawable, drawable2, 0, 0);
                shadowDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                drawable2 = shadowDrawable;
            }
            r0.routeButton.setBackgroundDrawable(drawable2);
            r0.routeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
            r0.routeButton.setImageResource(R.drawable.navigate);
            r0.routeButton.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator animator2 = new StateListAnimator();
                animator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.routeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator2.addState(new int[0], ObjectAnimator.ofFloat(r0.routeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                r0.routeButton.setStateListAnimator(animator2);
                r0.routeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            frameLayout.addView(r0.routeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 37.0f));
            r0.routeButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (VERSION.SDK_INT >= 23) {
                        Activity activity = LocationActivity.this.getParentActivity();
                        if (!(activity == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                            LocationActivity.this.showPermissionAlert(true);
                            return;
                        }
                    }
                    if (LocationActivity.this.myLocation != null) {
                        try {
                            LocationActivity.this.getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[]{Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo.lat), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo._long)}))));
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            });
            r0.adapter.setMessageObject(r0.messageObject);
        }
        if (r0.messageObject == null || r0.messageObject.isLiveLocation()) {
            r0.mapViewClip.addView(r0.locationButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        } else {
            r0.mapViewClip.addView(r0.locationButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 43.0f));
        }
        r0.locationButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VERSION.SDK_INT >= 23) {
                    Activity activity = LocationActivity.this.getParentActivity();
                    if (!(activity == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                        LocationActivity.this.showPermissionAlert(false);
                        return;
                    }
                }
                if (LocationActivity.this.messageObject != null) {
                    if (!(LocationActivity.this.myLocation == null || LocationActivity.this.googleMap == null)) {
                        LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude()), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else if (!(LocationActivity.this.myLocation == null || LocationActivity.this.googleMap == null)) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(200);
                    animatorSet.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[]{0.0f}));
                    animatorSet.start();
                    LocationActivity.this.adapter.setCustomLocation(null);
                    LocationActivity.this.userLocationMoved = false;
                    LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude())));
                }
            }
        });
        if (r0.messageObject == null) {
            r0.locationButton.setAlpha(0.0f);
        }
        frameLayout.addView(r0.actionBar);
        return r0.fragmentView;
    }

    private Bitmap createUserBitmap(LiveLocation liveLocation) {
        Bitmap result;
        Throwable e;
        LiveLocation liveLocation2 = liveLocation;
        Bitmap result2 = null;
        FileLocation photo = null;
        try {
            if (liveLocation2.user != null && liveLocation2.user.photo != null) {
                photo = liveLocation2.user.photo.photo_small;
            } else if (!(liveLocation2.chat == null || liveLocation2.chat.photo == null)) {
                photo = liveLocation2.chat.photo.photo_small;
            }
            result2 = Bitmap.createBitmap(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f), Config.ARGB_8888);
            try {
                result2.eraseColor(0);
                Canvas canvas = new Canvas(result2);
                Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.livepin);
                drawable.setBounds(0, 0, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f));
                drawable.draw(canvas);
                Paint roundPaint = new Paint(1);
                RectF bitmapRect = new RectF();
                canvas.save();
                if (photo != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                    if (bitmap != null) {
                        BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
                        Matrix matrix = new Matrix();
                        float scale = ((float) AndroidUtilities.dp(52.0f)) / ((float) bitmap.getWidth());
                        matrix.postTranslate((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f));
                        matrix.postScale(scale, scale);
                        roundPaint.setShader(shader);
                        shader.setLocalMatrix(matrix);
                        result = result2;
                        try {
                            bitmapRect.set((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(57.0f), (float) AndroidUtilities.dp(57.0f));
                            canvas.drawRoundRect(bitmapRect, (float) AndroidUtilities.dp(26.0f), (float) AndroidUtilities.dp(26.0f), roundPaint);
                        } catch (Throwable th) {
                            e = th;
                            result2 = result;
                            FileLog.m3e(e);
                            return result2;
                        }
                    }
                    result = result2;
                } else {
                    result = result2;
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    if (liveLocation2.user != null) {
                        avatarDrawable.setInfo(liveLocation2.user);
                    } else if (liveLocation2.chat != null) {
                        avatarDrawable.setInfo(liveLocation2.chat);
                    }
                    canvas.translate((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f));
                    avatarDrawable.setBounds(0, 0, AndroidUtilities.dp(52.2f), AndroidUtilities.dp(52.2f));
                    avatarDrawable.draw(canvas);
                }
                canvas.restore();
                try {
                    canvas.setBitmap(null);
                } catch (Exception e2) {
                }
                return result;
            } catch (Throwable th2) {
                result = result2;
                e = th2;
                FileLog.m3e(e);
                return result2;
            }
        } catch (Throwable th22) {
            e = th22;
            FileLog.m3e(e);
            return result2;
        }
    }

    private int getMessageId(Message message) {
        if (message.from_id != 0) {
            return message.from_id;
        }
        return (int) MessageObject.getDialogId(message);
    }

    private LiveLocation addUserMarker(Message message) {
        LatLng latLng = new LatLng(message.media.geo.lat, message.media.geo._long);
        LiveLocation liveLocation = (LiveLocation) this.markersMap.get(message.from_id);
        LiveLocation liveLocation2 = liveLocation;
        if (liveLocation == null) {
            liveLocation2 = new LiveLocation();
            liveLocation2.object = message;
            if (liveLocation2.object.from_id != 0) {
                liveLocation2.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(liveLocation2.object.from_id));
                liveLocation2.id = liveLocation2.object.from_id;
            } else {
                int did = (int) MessageObject.getDialogId(message);
                if (did > 0) {
                    liveLocation2.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(did));
                    liveLocation2.id = did;
                } else {
                    liveLocation2.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-did));
                    liveLocation2.id = did;
                }
            }
            try {
                MarkerOptions options = new MarkerOptions().position(latLng);
                Bitmap bitmap = createUserBitmap(liveLocation2);
                if (bitmap != null) {
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    options.anchor(0.5f, 0.907f);
                    liveLocation2.marker = this.googleMap.addMarker(options);
                    this.markers.add(liveLocation2);
                    this.markersMap.put(liveLocation2.id, liveLocation2);
                    SharingLocationInfo myInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
                    if (liveLocation2.id == UserConfig.getInstance(this.currentAccount).getClientUserId() && myInfo != null && liveLocation2.object.id == myInfo.mid && this.myLocation != null) {
                        liveLocation2.marker.setPosition(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
                    }
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        } else {
            liveLocation2.object = message;
            liveLocation2.marker.setPosition(latLng);
        }
        return liveLocation2;
    }

    private void onMapInit() {
        if (this.googleMap != null) {
            if (this.messageObject == null) {
                this.userLocation = new Location("network");
                this.userLocation.setLatitude(20.659322d);
                this.userLocation.setLongitude(-11.40625d);
            } else if (this.messageObject.isLiveLocation()) {
                LiveLocation liveLocation = addUserMarker(this.messageObject.messageOwner);
                if (!getRecentLocations()) {
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(liveLocation.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            } else {
                LatLng latLng = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                try {
                    this.googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
                this.firstFocus = false;
                getRecentLocations();
            }
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
                public void onMyLocationChange(Location location) {
                    LocationActivity.this.positionMarker(location);
                    LocationController.getInstance(LocationActivity.this.currentAccount).setGoogleMapLocation(location, LocationActivity.this.isFirstLocation);
                    LocationActivity.this.isFirstLocation = false;
                }
            });
            Location lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
            if (this.checkGpsEnabled && getParentActivity() != null) {
                this.checkGpsEnabled = false;
                if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                    try {
                        if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                            Builder builder = new Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlert", R.string.GpsDisabledAlert));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (LocationActivity.this.getParentActivity() != null) {
                                        try {
                                            LocationActivity.this.getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            showDialog(builder.create());
                        }
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                }
            }
        }
    }

    private void showPermissionAlert(boolean byButton) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (byButton) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", R.string.PermissionNoLocationPosition));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", R.string.PermissionNoLocation));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                @TargetApi(9)
                public void onClick(DialogInterface dialog, int which) {
                    if (LocationActivity.this.getParentActivity() != null) {
                        try {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("package:");
                            stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                            intent.setData(Uri.parse(stringBuilder.toString()));
                            LocationActivity.this.getParentActivity().startActivity(intent);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            try {
                if (this.mapView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mapView.getParent()).removeView(this.mapView);
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (this.mapViewClip != null) {
                this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                updateClipView(this.layoutManager.findFirstVisibleItemPosition());
            } else if (this.fragmentView != null) {
                ((FrameLayout) this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
            }
        }
    }

    private void updateClipView(int firstVisibleItem) {
        if (firstVisibleItem != -1) {
            int height = 0;
            int top = 0;
            View child = this.listView.getChildAt(0);
            if (child != null) {
                if (firstVisibleItem == 0) {
                    top = child.getTop();
                    height = this.overScrollHeight + (top < 0 ? top : 0);
                }
                if (((LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
                    if (height <= 0) {
                        if (this.mapView.getVisibility() == 0) {
                            this.mapView.setVisibility(4);
                            this.mapViewClip.setVisibility(4);
                        }
                    } else if (this.mapView.getVisibility() == 4) {
                        this.mapView.setVisibility(0);
                        this.mapViewClip.setVisibility(0);
                    }
                    this.mapViewClip.setTranslationY((float) Math.min(0, top));
                    this.mapView.setTranslationY((float) Math.max(0, (-top) / 2));
                    if (this.markerImageView != null) {
                        ImageView imageView = this.markerImageView;
                        int dp = ((-top) - AndroidUtilities.dp(42.0f)) + (height / 2);
                        this.markerTop = dp;
                        imageView.setTranslationY((float) dp);
                        this.markerXImageView.setTranslationY((float) (((-top) - AndroidUtilities.dp(7.0f)) + (height / 2)));
                    }
                    if (this.routeButton != null) {
                        this.routeButton.setTranslationY((float) top);
                    }
                    LayoutParams layoutParams = (LayoutParams) this.mapView.getLayoutParams();
                    if (!(layoutParams == null || layoutParams.height == this.overScrollHeight + AndroidUtilities.dp(10.0f))) {
                        layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                        if (this.googleMap != null) {
                            this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                        }
                        this.mapView.setLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    private void fixLayoutInternal(boolean resume) {
        if (this.listView != null) {
            int height = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int viewHeight = this.fragmentView.getMeasuredHeight();
            if (viewHeight != 0) {
                this.overScrollHeight = (viewHeight - AndroidUtilities.dp(66.0f)) - height;
                LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
                layoutParams.topMargin = height;
                this.listView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.mapViewClip.getLayoutParams();
                layoutParams.topMargin = height;
                layoutParams.height = this.overScrollHeight;
                this.mapViewClip.setLayoutParams(layoutParams);
                if (this.searchListView != null) {
                    layoutParams = (LayoutParams) this.searchListView.getLayoutParams();
                    layoutParams.topMargin = height;
                    this.searchListView.setLayoutParams(layoutParams);
                }
                this.adapter.setOverScrollHeight(this.overScrollHeight);
                layoutParams = (LayoutParams) this.mapView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    if (this.googleMap != null) {
                        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams);
                }
                this.adapter.notifyDataSetChanged();
                if (resume) {
                    int i;
                    LinearLayoutManager linearLayoutManager = this.layoutManager;
                    if (this.liveLocationType != 1) {
                        if (this.liveLocationType != 2) {
                            i = 0;
                            linearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + i)));
                            updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                            this.listView.post(new Runnable() {
                                public void run() {
                                    int i;
                                    LinearLayoutManager access$1300 = LocationActivity.this.layoutManager;
                                    if (LocationActivity.this.liveLocationType != 1) {
                                        if (LocationActivity.this.liveLocationType != 2) {
                                            i = 0;
                                            access$1300.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + i)));
                                            LocationActivity.this.updateClipView(LocationActivity.this.layoutManager.findFirstVisibleItemPosition());
                                        }
                                    }
                                    i = 66;
                                    access$1300.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + i)));
                                    LocationActivity.this.updateClipView(LocationActivity.this.layoutManager.findFirstVisibleItemPosition());
                                }
                            });
                        }
                    }
                    i = 66;
                    linearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + i)));
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                    this.listView.post(/* anonymous class already generated */);
                } else {
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                }
            }
        }
    }

    private Location getLastLocation() {
        LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List<String> providers = lm.getProviders(true);
        Location l = null;
        int i = providers.size() - 1;
        while (true) {
            int i2 = i;
            if (i2 < 0) {
                break;
            }
            l = lm.getLastKnownLocation((String) providers.get(i2));
            if (l != null) {
                break;
            }
            i = i2 - 1;
        }
        return l;
    }

    private void positionMarker(Location location) {
        if (location != null) {
            this.myLocation = new Location(location);
            LiveLocation liveLocation = (LiveLocation) this.markersMap.get(UserConfig.getInstance(this.currentAccount).getClientUserId());
            SharingLocationInfo myInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
            if (!(liveLocation == null || myInfo == null || liveLocation.object.id != myInfo.mid)) {
                liveLocation.marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            if (this.messageObject != null || this.googleMap == null) {
                this.adapter.setGpsLocation(this.myLocation);
            } else {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (this.adapter != null) {
                    if (this.adapter.isPulledUp()) {
                        this.adapter.searchGooglePlacesWithQuery(null, this.myLocation);
                    }
                    this.adapter.setGpsLocation(this.myLocation);
                }
                if (!this.userLocationMoved) {
                    this.userLocation = new Location(location);
                    if (this.firstWas) {
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    } else {
                        this.firstWas = true;
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                }
            }
        }
    }

    public void setMessageObject(MessageObject message) {
        this.messageObject = message;
        this.dialogId = this.messageObject.getDialogId();
    }

    public void setDialogId(long did) {
        this.dialogId = did;
    }

    private void fetchRecentLocations(ArrayList<Message> messages) {
        LatLngBounds.Builder builder = null;
        if (this.firstFocus) {
            builder = new LatLngBounds.Builder();
        }
        int date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        for (int a = 0; a < messages.size(); a++) {
            Message message = (Message) messages.get(a);
            if (message.date + message.media.period > date) {
                if (builder != null) {
                    builder.include(new LatLng(message.media.geo.lat, message.media.geo._long));
                }
                addUserMarker(message);
            }
        }
        if (builder != null) {
            this.firstFocus = false;
            this.adapter.setLiveLocations(this.markers);
            if (this.messageObject.isLiveLocation()) {
                try {
                    LatLngBounds bounds = builder.build();
                    if (messages.size() > 1) {
                        try {
                            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, AndroidUtilities.dp(60.0f)));
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                } catch (Exception e2) {
                }
            }
        }
    }

    private boolean getRecentLocations() {
        ArrayList<Message> messages = (ArrayList) LocationController.getInstance(this.currentAccount).locationsCache.get(this.messageObject.getDialogId());
        if (messages == null || !messages.isEmpty()) {
            messages = null;
        } else {
            fetchRecentLocations(messages);
        }
        int lower_id = (int) this.dialogId;
        boolean z = false;
        if (lower_id < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TL_messages_getRecentLocations req = new TL_messages_getRecentLocations();
        final long dialog_id = this.messageObject.getDialogId();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) dialog_id);
        req.limit = 100;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, TL_error error) {
                if (response != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (LocationActivity.this.googleMap != null) {
                                messages_Messages res = response;
                                int a = 0;
                                while (a < res.messages.size()) {
                                    if (!(((Message) res.messages.get(a)).media instanceof TL_messageMediaGeoLive)) {
                                        res.messages.remove(a);
                                        a--;
                                    }
                                    a++;
                                }
                                MessagesStorage.getInstance(LocationActivity.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                                MessagesController.getInstance(LocationActivity.this.currentAccount).putUsers(res.users, false);
                                MessagesController.getInstance(LocationActivity.this.currentAccount).putChats(res.chats, false);
                                LocationController.getInstance(LocationActivity.this.currentAccount).locationsCache.put(dialog_id, res.messages);
                                NotificationCenter.getInstance(LocationActivity.this.currentAccount).postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(dialog_id));
                                LocationActivity.this.fetchRecentLocations(res.messages);
                            }
                        }
                    });
                }
            }
        });
        if (messages != null) {
            z = true;
        }
        return z;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        LocationActivity locationActivity = this;
        int i = id;
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i != NotificationCenter.locationPermissionGranted) {
            int a = 0;
            MessageObject messageObject;
            if (i == NotificationCenter.didReceivedNewMessages) {
                if (((Long) args[0]).longValue() == locationActivity.dialogId) {
                    if (locationActivity.messageObject != null) {
                        ArrayList<MessageObject> arr = args[1];
                        boolean added = false;
                        while (a < arr.size()) {
                            messageObject = (MessageObject) arr.get(a);
                            if (messageObject.isLiveLocation()) {
                                addUserMarker(messageObject.messageOwner);
                                added = true;
                            }
                            a++;
                        }
                        if (added && locationActivity.adapter != null) {
                            locationActivity.adapter.setLiveLocations(locationActivity.markers);
                        }
                    }
                }
            } else if (i != NotificationCenter.messagesDeleted) {
                if (i == NotificationCenter.replaceMessagesObjects) {
                    long did = ((Long) args[0]).longValue();
                    if (did == locationActivity.dialogId) {
                        if (locationActivity.messageObject != null) {
                            boolean updated = false;
                            ArrayList<MessageObject> messageObjects = args[1];
                            while (a < messageObjects.size()) {
                                messageObject = (MessageObject) messageObjects.get(a);
                                if (messageObject.isLiveLocation()) {
                                    LiveLocation liveLocation = (LiveLocation) locationActivity.markersMap.get(getMessageId(messageObject.messageOwner));
                                    if (liveLocation != null) {
                                        SharingLocationInfo myInfo = LocationController.getInstance(locationActivity.currentAccount).getSharingLocationInfo(did);
                                        if (myInfo == null || myInfo.mid != messageObject.getId()) {
                                            liveLocation.marker.setPosition(new LatLng(messageObject.messageOwner.media.geo.lat, messageObject.messageOwner.media.geo._long));
                                        }
                                        updated = true;
                                    }
                                }
                                a++;
                                i = id;
                            }
                            if (updated && locationActivity.adapter != null) {
                                locationActivity.adapter.updateLiveLocations();
                            }
                        }
                    }
                }
            }
        } else if (locationActivity.googleMap != null) {
            try {
                locationActivity.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mapView != null && this.mapsInitialized) {
            try {
                this.mapView.onPause();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        this.onResumeCalled = false;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.mapView != null && this.mapsInitialized) {
            try {
                this.mapView.onResume();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        this.onResumeCalled = true;
        if (this.googleMap != null) {
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
        fixLayoutInternal(true);
        if (this.checkPermission && VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                if (activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                }
            }
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        if (this.mapView != null && this.mapsInitialized) {
            this.mapView.onLowMemory();
        }
    }

    public void setDelegate(LocationActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private void updateSearchInterface() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor() {
            }
        };
        r9 = new ThemeDescription[50];
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r9[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r9[14] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon);
        r9[15] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground);
        r9[16] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground);
        r9[17] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon);
        r9[18] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground);
        r9[19] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground);
        r9[20] = new ThemeDescription(this.markerXImageView, 0, null, null, null, null, Theme.key_location_markerX);
        r9[21] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        r9[23] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, сellDelegate, Theme.key_avatar_text);
        r9[24] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        r9[25] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange);
        r9[26] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        r9[27] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        r9[28] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        r9[29] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        r9[30] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        r9[31] = new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress");
        r9[32] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_location_placeLocationBackground);
        r9[33] = new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress");
        r9[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLocationIcon);
        r9[35] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLocationBackground);
        r9[36] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLiveLocationBackground);
        r9[37] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText7);
        r9[38] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[39] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[40] = new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[41] = new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[42] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[43] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[44] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[45] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r9[46] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[47] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[48] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[49] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        return r9;
    }
}
