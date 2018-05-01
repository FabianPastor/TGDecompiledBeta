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
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
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
    class C14664 extends ViewOutlineProvider {
        C14664() {
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
    class C21751 extends ActionBarMenuOnItemClick {
        C21751() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                LocationActivity.this.finishFragment();
            } else if (i == 2) {
                if (LocationActivity.this.googleMap != 0) {
                    LocationActivity.this.googleMap.setMapType(1);
                }
            } else if (i == 3) {
                if (LocationActivity.this.googleMap != 0) {
                    LocationActivity.this.googleMap.setMapType(2);
                }
            } else if (i == 4) {
                if (LocationActivity.this.googleMap != 0) {
                    LocationActivity.this.googleMap.setMapType(4);
                }
            } else if (i == 1) {
                try {
                    double d = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                    double d2 = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                    i = LocationActivity.this.getParentActivity();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("geo:");
                    stringBuilder.append(d);
                    stringBuilder.append(",");
                    stringBuilder.append(d2);
                    stringBuilder.append("?q=");
                    stringBuilder.append(d);
                    stringBuilder.append(",");
                    stringBuilder.append(d2);
                    i.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$2 */
    class C21762 extends ActionBarMenuItemSearchListener {
        C21762() {
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
                editText = editText.getText().toString();
                if (editText.length() != 0) {
                    LocationActivity.this.searchWas = true;
                }
                LocationActivity.this.searchAdapter.searchDelayed(editText, LocationActivity.this.userLocation);
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$6 */
    class C21776 extends OnScrollListener {

        /* renamed from: org.telegram.ui.LocationActivity$6$1 */
        class C14671 implements Runnable {
            C14671() {
            }

            public void run() {
                LocationActivity.this.adapter.searchGooglePlacesWithQuery(null, LocationActivity.this.myLocation);
            }
        }

        C21776() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (LocationActivity.this.adapter.getItemCount() != null) {
                recyclerView = LocationActivity.this.layoutManager.findFirstVisibleItemPosition();
                if (recyclerView != -1) {
                    LocationActivity.this.updateClipView(recyclerView);
                    if (i2 > 0 && LocationActivity.this.adapter.isPulledUp() == null) {
                        LocationActivity.this.adapter.setPulledUp();
                        if (LocationActivity.this.myLocation != null) {
                            AndroidUtilities.runOnUIThread(new C14671());
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$7 */
    class C21797 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.LocationActivity$7$1 */
        class C21781 implements IntCallback {
            C21781() {
            }

            public void run(int i) {
                MessageMedia tL_messageMediaGeoLive = new TL_messageMediaGeoLive();
                tL_messageMediaGeoLive.geo = new TL_geoPoint();
                tL_messageMediaGeoLive.geo.lat = LocationActivity.this.myLocation.getLatitude();
                tL_messageMediaGeoLive.geo._long = LocationActivity.this.myLocation.getLongitude();
                tL_messageMediaGeoLive.period = i;
                LocationActivity.this.delegate.didSelectLocation(tL_messageMediaGeoLive, LocationActivity.this.liveLocationType);
                LocationActivity.this.finishFragment();
            }
        }

        C21797() {
        }

        public void onItemClick(View view, int i) {
            if (i != 1 || LocationActivity.this.messageObject == null || LocationActivity.this.messageObject.isLiveLocation()) {
                if (i == 1 && LocationActivity.this.liveLocationType != 2) {
                    if (!(LocationActivity.this.delegate == null || LocationActivity.this.userLocation == null)) {
                        view = new TL_messageMediaGeo();
                        view.geo = new TL_geoPoint();
                        view.geo.lat = LocationActivity.this.userLocation.getLatitude();
                        view.geo._long = LocationActivity.this.userLocation.getLongitude();
                        LocationActivity.this.delegate.didSelectLocation(view, LocationActivity.this.liveLocationType);
                    }
                    LocationActivity.this.finishFragment();
                } else if ((i != 2 || LocationActivity.this.liveLocationType != 1) && ((i != 1 || LocationActivity.this.liveLocationType != 2) && (i != 3 || LocationActivity.this.liveLocationType != 3))) {
                    i = LocationActivity.this.adapter.getItem(i);
                    if (i instanceof TL_messageMediaVenue) {
                        if (!(i == 0 || LocationActivity.this.delegate == null)) {
                            LocationActivity.this.delegate.didSelectLocation((TL_messageMediaVenue) i, LocationActivity.this.liveLocationType);
                        }
                        LocationActivity.this.finishFragment();
                    } else if (i instanceof LiveLocation) {
                        LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation) i).marker.getPosition(), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else if (LocationController.getInstance(LocationActivity.this.currentAccount).isSharingLocation(LocationActivity.this.dialogId) != null) {
                    LocationController.getInstance(LocationActivity.this.currentAccount).removeSharingLocation(LocationActivity.this.dialogId);
                    LocationActivity.this.finishFragment();
                } else {
                    if (LocationActivity.this.delegate != null) {
                        if (LocationActivity.this.getParentActivity() != null) {
                            if (LocationActivity.this.myLocation != null) {
                                view = null;
                                if (((int) LocationActivity.this.dialogId) > 0) {
                                    view = MessagesController.getInstance(LocationActivity.this.currentAccount).getUser(Integer.valueOf((int) LocationActivity.this.dialogId));
                                }
                                LocationActivity.this.showDialog(AlertsCreator.createLocationUpdateDialog(LocationActivity.this.getParentActivity(), view, new C21781()));
                            }
                        }
                    }
                }
            } else if (LocationActivity.this.googleMap != 0) {
                LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.this.messageObject.messageOwner.media.geo.lat, LocationActivity.this.messageObject.messageOwner.media.geo._long), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$8 */
    class C21808 implements BaseLocationAdapterDelegate {
        C21808() {
        }

        public void didLoadedSearchResult(ArrayList<TL_messageMediaVenue> arrayList) {
            if (!LocationActivity.this.wasResults && arrayList.isEmpty() == null) {
                LocationActivity.this.wasResults = true;
            }
        }
    }

    public LocationActivity(int i) {
        this.liveLocationType = i;
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            r0.actionBar.setOccupyStatusBar(false);
        }
        r0.actionBar.setAddToContainer(false);
        r0.actionBar.setActionBarMenuOnItemClick(new C21751());
        ActionBarMenu createMenu = r0.actionBar.createMenu();
        if (r0.messageObject == null) {
            r0.actionBar.setTitle(LocaleController.getString("ShareLocation", C0446R.string.ShareLocation));
            createMenu.addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C21762()).getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        } else if (r0.messageObject.isLiveLocation()) {
            r0.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", C0446R.string.AttachLiveLocation));
        } else {
            if (r0.messageObject.messageOwner.media.title == null || r0.messageObject.messageOwner.media.title.length() <= 0) {
                r0.actionBar.setTitle(LocaleController.getString("ChatLocation", C0446R.string.ChatLocation));
            } else {
                r0.actionBar.setTitle(LocaleController.getString("SharedPlace", C0446R.string.SharedPlace));
            }
            createMenu.addItem(1, (int) C0446R.drawable.share);
        }
        r0.otherItem = createMenu.addItem(0, (int) C0446R.drawable.ic_ab_other);
        r0.otherItem.addSubItem(2, LocaleController.getString("Map", C0446R.string.Map));
        r0.otherItem.addSubItem(3, LocaleController.getString("Satellite", C0446R.string.Satellite));
        r0.otherItem.addSubItem(4, LocaleController.getString("Hybrid", C0446R.string.Hybrid));
        r0.fragmentView = new FrameLayout(context2) {
            private boolean first = true;

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (z) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) r0.fragmentView;
        r0.locationButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(C0446R.drawable.floating_shadow_profile).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        r0.locationButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        r0.locationButton.setImageResource(C0446R.drawable.myloc_on);
        r0.locationButton.setScaleType(ScaleType.CENTER);
        r0.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.locationButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(r0.locationButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.locationButton.setStateListAnimator(stateListAnimator);
            r0.locationButton.setOutlineProvider(new C14664());
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
        LayoutManager c23425 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        r0.layoutManager = c23425;
        recyclerListView.setLayoutManager(c23425);
        frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
        r0.listView.setOnScrollListener(new C21776());
        r0.listView.setOnItemClickListener(new C21797());
        r0.adapter.setDelegate(new C21808());
        r0.adapter.setOverScrollHeight(r0.overScrollHeight);
        frameLayout.addView(r0.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        r0.mapView = new MapView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (LocationActivity.this.messageObject == null) {
                    AnimatorSet access$2300;
                    Animator[] animatorArr;
                    if (motionEvent.getAction() == 0) {
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
                    } else if (motionEvent.getAction() == 1) {
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
                    if (motionEvent.getAction() == 2) {
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
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        final MapView mapView = r0.mapView;
        new Thread(new Runnable() {

            /* renamed from: org.telegram.ui.LocationActivity$10$1 */
            class C14631 implements Runnable {

                /* renamed from: org.telegram.ui.LocationActivity$10$1$1 */
                class C21741 implements OnMapReadyCallback {
                    C21741() {
                    }

                    public void onMapReady(GoogleMap googleMap) {
                        LocationActivity.this.googleMap = googleMap;
                        LocationActivity.this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                        LocationActivity.this.onMapInit();
                    }
                }

                C14631() {
                }

                public void run() {
                    if (LocationActivity.this.mapView != null && LocationActivity.this.getParentActivity() != null) {
                        try {
                            mapView.onCreate(null);
                            MapsInitializer.initialize(LocationActivity.this.getParentActivity());
                            LocationActivity.this.mapView.getMapAsync(new C21741());
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
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                /*
                r2 = this;
                r0 = r5;	 Catch:{ Exception -> 0x0006 }
                r1 = 0;	 Catch:{ Exception -> 0x0006 }
                r0.onCreate(r1);	 Catch:{ Exception -> 0x0006 }
            L_0x0006:
                r0 = new org.telegram.ui.LocationActivity$10$1;
                r0.<init>();
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.10.run():void");
            }
        }).start();
        View view = new View(context2);
        view.setBackgroundResource(C0446R.drawable.header_shadow_reverse);
        r0.mapViewClip.addView(view, LayoutHelper.createFrame(-1, 3, 83));
        if (r0.messageObject == null) {
            r0.markerImageView = new ImageView(context2);
            r0.markerImageView.setImageResource(C0446R.drawable.map_pin);
            r0.mapViewClip.addView(r0.markerImageView, LayoutHelper.createFrame(24, 42, 49));
            r0.markerXImageView = new ImageView(context2);
            r0.markerXImageView.setAlpha(0.0f);
            r0.markerXImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_markerX), Mode.MULTIPLY));
            r0.markerXImageView.setImageResource(C0446R.drawable.place_x);
            r0.mapViewClip.addView(r0.markerXImageView, LayoutHelper.createFrame(14, 14, 49));
            r0.emptyView = new EmptyTextProgressView(context2);
            r0.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
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
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1 && LocationActivity.this.searching != null && LocationActivity.this.searchWas != null) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            r0.searchListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    view = LocationActivity.this.searchAdapter.getItem(i);
                    if (!(view == null || LocationActivity.this.delegate == 0)) {
                        LocationActivity.this.delegate.didSelectLocation(view, LocationActivity.this.liveLocationType);
                    }
                    LocationActivity.this.finishFragment();
                }
            });
        } else if (!r0.messageObject.isLiveLocation()) {
            r0.routeButton = new ImageView(context2);
            Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
            if (VERSION.SDK_INT < 21) {
                Drawable mutate2 = context.getResources().getDrawable(C0446R.drawable.floating_shadow).mutate();
                mutate2.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                Drawable combinedDrawable2 = new CombinedDrawable(mutate2, createSimpleSelectorCircleDrawable2, 0, 0);
                combinedDrawable2.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                createSimpleSelectorCircleDrawable2 = combinedDrawable2;
            }
            r0.routeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable2);
            r0.routeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
            r0.routeButton.setImageResource(C0446R.drawable.navigate);
            r0.routeButton.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator stateListAnimator2 = new StateListAnimator();
                stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.routeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator2.addState(new int[0], ObjectAnimator.ofFloat(r0.routeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                r0.routeButton.setStateListAnimator(stateListAnimator2);
                r0.routeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            frameLayout.addView(r0.routeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 37.0f));
            r0.routeButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VERSION.SDK_INT >= 23) {
                        view = LocationActivity.this.getParentActivity();
                        if (!(view == null || view.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == null)) {
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
            public void onClick(View view) {
                if (VERSION.SDK_INT >= 23) {
                    view = LocationActivity.this.getParentActivity();
                    if (!(view == null || view.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == null)) {
                        LocationActivity.this.showPermissionAlert(false);
                        return;
                    }
                }
                if (LocationActivity.this.messageObject != null) {
                    if (!(LocationActivity.this.myLocation == null || LocationActivity.this.googleMap == null)) {
                        LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude()), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else if (!(LocationActivity.this.myLocation == null || LocationActivity.this.googleMap == null)) {
                    view = new AnimatorSet();
                    view.setDuration(200);
                    view.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[]{0.0f}));
                    view.start();
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

    private android.graphics.Bitmap createUserBitmap(org.telegram.ui.LocationActivity.LiveLocation r11) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r10 = this;
        r0 = 0;
        r1 = r11.user;	 Catch:{ Throwable -> 0x0113 }
        if (r1 == 0) goto L_0x0012;	 Catch:{ Throwable -> 0x0113 }
    L_0x0005:
        r1 = r11.user;	 Catch:{ Throwable -> 0x0113 }
        r1 = r1.photo;	 Catch:{ Throwable -> 0x0113 }
        if (r1 == 0) goto L_0x0012;	 Catch:{ Throwable -> 0x0113 }
    L_0x000b:
        r1 = r11.user;	 Catch:{ Throwable -> 0x0113 }
        r1 = r1.photo;	 Catch:{ Throwable -> 0x0113 }
        r1 = r1.photo_small;	 Catch:{ Throwable -> 0x0113 }
        goto L_0x0024;	 Catch:{ Throwable -> 0x0113 }
    L_0x0012:
        r1 = r11.chat;	 Catch:{ Throwable -> 0x0113 }
        if (r1 == 0) goto L_0x0023;	 Catch:{ Throwable -> 0x0113 }
    L_0x0016:
        r1 = r11.chat;	 Catch:{ Throwable -> 0x0113 }
        r1 = r1.photo;	 Catch:{ Throwable -> 0x0113 }
        if (r1 == 0) goto L_0x0023;	 Catch:{ Throwable -> 0x0113 }
    L_0x001c:
        r1 = r11.chat;	 Catch:{ Throwable -> 0x0113 }
        r1 = r1.photo;	 Catch:{ Throwable -> 0x0113 }
        r1 = r1.photo_small;	 Catch:{ Throwable -> 0x0113 }
        goto L_0x0024;	 Catch:{ Throwable -> 0x0113 }
    L_0x0023:
        r1 = r0;	 Catch:{ Throwable -> 0x0113 }
    L_0x0024:
        r2 = NUM; // 0x42780000 float:62.0 double:5.5096253E-315;	 Catch:{ Throwable -> 0x0113 }
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Throwable -> 0x0113 }
        r4 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;	 Catch:{ Throwable -> 0x0113 }
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Throwable -> 0x0113 }
        r6 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0113 }
        r3 = android.graphics.Bitmap.createBitmap(r3, r5, r6);	 Catch:{ Throwable -> 0x0113 }
        r5 = 0;
        r3.eraseColor(r5);	 Catch:{ Throwable -> 0x0111 }
        r6 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x0111 }
        r6.<init>(r3);	 Catch:{ Throwable -> 0x0111 }
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0111 }
        r7 = r7.getResources();	 Catch:{ Throwable -> 0x0111 }
        r8 = NUM; // 0x7f07011c float:1.7945154E38 double:1.0529356433E-314;	 Catch:{ Throwable -> 0x0111 }
        r7 = r7.getDrawable(r8);	 Catch:{ Throwable -> 0x0111 }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Throwable -> 0x0111 }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Throwable -> 0x0111 }
        r7.setBounds(r5, r5, r2, r4);	 Catch:{ Throwable -> 0x0111 }
        r7.draw(r6);	 Catch:{ Throwable -> 0x0111 }
        r2 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x0111 }
        r4 = 1;	 Catch:{ Throwable -> 0x0111 }
        r2.<init>(r4);	 Catch:{ Throwable -> 0x0111 }
        r7 = new android.graphics.RectF;	 Catch:{ Throwable -> 0x0111 }
        r7.<init>();	 Catch:{ Throwable -> 0x0111 }
        r6.save();	 Catch:{ Throwable -> 0x0111 }
        r8 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;	 Catch:{ Throwable -> 0x0111 }
        if (r1 == 0) goto L_0x00d4;	 Catch:{ Throwable -> 0x0111 }
    L_0x006c:
        r11 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r4);	 Catch:{ Throwable -> 0x0111 }
        r11 = r11.toString();	 Catch:{ Throwable -> 0x0111 }
        r11 = android.graphics.BitmapFactory.decodeFile(r11);	 Catch:{ Throwable -> 0x0111 }
        if (r11 == 0) goto L_0x010a;	 Catch:{ Throwable -> 0x0111 }
    L_0x007a:
        r1 = new android.graphics.BitmapShader;	 Catch:{ Throwable -> 0x0111 }
        r4 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x0111 }
        r5 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x0111 }
        r1.<init>(r11, r4, r5);	 Catch:{ Throwable -> 0x0111 }
        r4 = new android.graphics.Matrix;	 Catch:{ Throwable -> 0x0111 }
        r4.<init>();	 Catch:{ Throwable -> 0x0111 }
        r5 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;	 Catch:{ Throwable -> 0x0111 }
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Throwable -> 0x0111 }
        r5 = (float) r5;	 Catch:{ Throwable -> 0x0111 }
        r11 = r11.getWidth();	 Catch:{ Throwable -> 0x0111 }
        r11 = (float) r11;	 Catch:{ Throwable -> 0x0111 }
        r5 = r5 / r11;	 Catch:{ Throwable -> 0x0111 }
        r11 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x0111 }
        r11 = (float) r11;	 Catch:{ Throwable -> 0x0111 }
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x0111 }
        r9 = (float) r9;	 Catch:{ Throwable -> 0x0111 }
        r4.postTranslate(r11, r9);	 Catch:{ Throwable -> 0x0111 }
        r4.postScale(r5, r5);	 Catch:{ Throwable -> 0x0111 }
        r2.setShader(r1);	 Catch:{ Throwable -> 0x0111 }
        r1.setLocalMatrix(r4);	 Catch:{ Throwable -> 0x0111 }
        r11 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x0111 }
        r11 = (float) r11;	 Catch:{ Throwable -> 0x0111 }
        r1 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x0111 }
        r1 = (float) r1;	 Catch:{ Throwable -> 0x0111 }
        r4 = NUM; // 0x42640000 float:57.0 double:5.503149485E-315;	 Catch:{ Throwable -> 0x0111 }
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Throwable -> 0x0111 }
        r5 = (float) r5;	 Catch:{ Throwable -> 0x0111 }
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Throwable -> 0x0111 }
        r4 = (float) r4;	 Catch:{ Throwable -> 0x0111 }
        r7.set(r11, r1, r5, r4);	 Catch:{ Throwable -> 0x0111 }
        r11 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;	 Catch:{ Throwable -> 0x0111 }
        r1 = org.telegram.messenger.AndroidUtilities.dp(r11);	 Catch:{ Throwable -> 0x0111 }
        r1 = (float) r1;	 Catch:{ Throwable -> 0x0111 }
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);	 Catch:{ Throwable -> 0x0111 }
        r11 = (float) r11;	 Catch:{ Throwable -> 0x0111 }
        r6.drawRoundRect(r7, r1, r11, r2);	 Catch:{ Throwable -> 0x0111 }
        goto L_0x010a;	 Catch:{ Throwable -> 0x0111 }
    L_0x00d4:
        r1 = new org.telegram.ui.Components.AvatarDrawable;	 Catch:{ Throwable -> 0x0111 }
        r1.<init>();	 Catch:{ Throwable -> 0x0111 }
        r2 = r11.user;	 Catch:{ Throwable -> 0x0111 }
        if (r2 == 0) goto L_0x00e3;	 Catch:{ Throwable -> 0x0111 }
    L_0x00dd:
        r11 = r11.user;	 Catch:{ Throwable -> 0x0111 }
        r1.setInfo(r11);	 Catch:{ Throwable -> 0x0111 }
        goto L_0x00ec;	 Catch:{ Throwable -> 0x0111 }
    L_0x00e3:
        r2 = r11.chat;	 Catch:{ Throwable -> 0x0111 }
        if (r2 == 0) goto L_0x00ec;	 Catch:{ Throwable -> 0x0111 }
    L_0x00e7:
        r11 = r11.chat;	 Catch:{ Throwable -> 0x0111 }
        r1.setInfo(r11);	 Catch:{ Throwable -> 0x0111 }
    L_0x00ec:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x0111 }
        r11 = (float) r11;	 Catch:{ Throwable -> 0x0111 }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x0111 }
        r2 = (float) r2;	 Catch:{ Throwable -> 0x0111 }
        r6.translate(r11, r2);	 Catch:{ Throwable -> 0x0111 }
        r11 = NUM; // 0x4250cccd float:52.2 double:5.4969327E-315;	 Catch:{ Throwable -> 0x0111 }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r11);	 Catch:{ Throwable -> 0x0111 }
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);	 Catch:{ Throwable -> 0x0111 }
        r1.setBounds(r5, r5, r2, r11);	 Catch:{ Throwable -> 0x0111 }
        r1.draw(r6);	 Catch:{ Throwable -> 0x0111 }
    L_0x010a:
        r6.restore();	 Catch:{ Throwable -> 0x0111 }
        r6.setBitmap(r0);	 Catch:{ Exception -> 0x0118 }
        goto L_0x0118;
    L_0x0111:
        r11 = move-exception;
        goto L_0x0115;
    L_0x0113:
        r11 = move-exception;
        r3 = r0;
    L_0x0115:
        org.telegram.messenger.FileLog.m3e(r11);
    L_0x0118:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.createUserBitmap(org.telegram.ui.LocationActivity$LiveLocation):android.graphics.Bitmap");
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
        if (liveLocation == null) {
            liveLocation = new LiveLocation();
            liveLocation.object = message;
            if (liveLocation.object.from_id != 0) {
                liveLocation.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(liveLocation.object.from_id));
                liveLocation.id = liveLocation.object.from_id;
            } else {
                message = (int) MessageObject.getDialogId(message);
                if (message > null) {
                    liveLocation.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(message));
                    liveLocation.id = message;
                } else {
                    liveLocation.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-message));
                    liveLocation.id = message;
                }
            }
            try {
                message = new MarkerOptions().position(latLng);
                Bitmap createUserBitmap = createUserBitmap(liveLocation);
                if (createUserBitmap != null) {
                    message.icon(BitmapDescriptorFactory.fromBitmap(createUserBitmap));
                    message.anchor(0.5f, 0.907f);
                    liveLocation.marker = this.googleMap.addMarker(message);
                    this.markers.add(liveLocation);
                    this.markersMap.put(liveLocation.id, liveLocation);
                    message = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
                    if (liveLocation.id == UserConfig.getInstance(this.currentAccount).getClientUserId() && message != null && liveLocation.object.id == message.mid && this.myLocation != null) {
                        liveLocation.marker.setPosition(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
                    }
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        } else {
            liveLocation.object = message;
            liveLocation.marker.setPosition(latLng);
        }
        return liveLocation;
    }

    private void onMapInit() {
        if (this.googleMap != null) {
            if (this.messageObject == null) {
                this.userLocation = new Location("network");
                this.userLocation.setLatitude(20.659322d);
                this.userLocation.setLongitude(-11.40625d);
            } else if (this.messageObject.isLiveLocation()) {
                LiveLocation addUserMarker = addUserMarker(this.messageObject.messageOwner);
                if (!getRecentLocations()) {
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            } else {
                LatLng latLng = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                try {
                    this.googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(C0446R.drawable.map_pin)));
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
                            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlert", C0446R.string.GpsDisabledAlert));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", C0446R.string.ConnectingToProxyEnable), new DialogInterface.OnClickListener() {
                                public void onClick(android.content.DialogInterface r2, int r3) {
                                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                                    /*
                                    r1 = this;
                                    r2 = org.telegram.ui.LocationActivity.this;
                                    r2 = r2.getParentActivity();
                                    if (r2 != 0) goto L_0x0009;
                                L_0x0008:
                                    return;
                                L_0x0009:
                                    r2 = org.telegram.ui.LocationActivity.this;	 Catch:{ Exception -> 0x0019 }
                                    r2 = r2.getParentActivity();	 Catch:{ Exception -> 0x0019 }
                                    r3 = new android.content.Intent;	 Catch:{ Exception -> 0x0019 }
                                    r0 = "android.settings.LOCATION_SOURCE_SETTINGS";	 Catch:{ Exception -> 0x0019 }
                                    r3.<init>(r0);	 Catch:{ Exception -> 0x0019 }
                                    r2.startActivity(r3);	 Catch:{ Exception -> 0x0019 }
                                L_0x0019:
                                    return;
                                    */
                                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.17.onClick(android.content.DialogInterface, int):void");
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                            showDialog(builder.create());
                        }
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                }
            }
        }
    }

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", C0446R.string.PermissionNoLocationPosition));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", C0446R.string.PermissionNoLocation));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", C0446R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                @TargetApi(9)
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (LocationActivity.this.getParentActivity() != null) {
                        try {
                            dialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            i = new StringBuilder();
                            i.append("package:");
                            i.append(ApplicationLoader.applicationContext.getPackageName());
                            dialogInterface.setData(Uri.parse(i.toString()));
                            LocationActivity.this.getParentActivity().startActivity(dialogInterface);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
            showDialog(builder.create());
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            try {
                if (this.mapView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mapView.getParent()).removeView(this.mapView);
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (this.mapViewClip) {
                this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                updateClipView(this.layoutManager.findFirstVisibleItemPosition());
            } else if (this.fragmentView) {
                ((FrameLayout) this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
            }
        }
    }

    private void updateClipView(int i) {
        if (i != -1) {
            View childAt = this.listView.getChildAt(0);
            if (childAt != null) {
                int i2;
                if (i == 0) {
                    i = childAt.getTop();
                    i2 = this.overScrollHeight + (i < 0 ? i : 0);
                } else {
                    i = 0;
                    i2 = i;
                }
                if (((LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
                    if (i2 <= 0) {
                        if (this.mapView.getVisibility() == 0) {
                            this.mapView.setVisibility(4);
                            this.mapViewClip.setVisibility(4);
                        }
                    } else if (this.mapView.getVisibility() == 4) {
                        this.mapView.setVisibility(0);
                        this.mapViewClip.setVisibility(0);
                    }
                    this.mapViewClip.setTranslationY((float) Math.min(0, i));
                    int i3 = -i;
                    this.mapView.setTranslationY((float) Math.max(0, i3 / 2));
                    if (this.markerImageView != null) {
                        ImageView imageView = this.markerImageView;
                        i2 /= 2;
                        int dp = (i3 - AndroidUtilities.dp(42.0f)) + i2;
                        this.markerTop = dp;
                        imageView.setTranslationY((float) dp);
                        this.markerXImageView.setTranslationY((float) ((i3 - AndroidUtilities.dp(7.0f)) + i2));
                    }
                    if (this.routeButton != null) {
                        this.routeButton.setTranslationY((float) i);
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

    private void fixLayoutInternal(boolean z) {
        if (this.listView != null) {
            int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int measuredHeight = this.fragmentView.getMeasuredHeight();
            if (measuredHeight != 0) {
                this.overScrollHeight = (measuredHeight - AndroidUtilities.dp(66.0f)) - currentActionBarHeight;
                LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.mapViewClip.getLayoutParams();
                layoutParams.topMargin = currentActionBarHeight;
                layoutParams.height = this.overScrollHeight;
                this.mapViewClip.setLayoutParams(layoutParams);
                if (this.searchListView != null) {
                    layoutParams = (LayoutParams) this.searchListView.getLayoutParams();
                    layoutParams.topMargin = currentActionBarHeight;
                    this.searchListView.setLayoutParams(layoutParams);
                }
                this.adapter.setOverScrollHeight(this.overScrollHeight);
                LayoutParams layoutParams2 = (LayoutParams) this.mapView.getLayoutParams();
                if (layoutParams2 != null) {
                    layoutParams2.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    if (this.googleMap != null) {
                        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams2);
                }
                this.adapter.notifyDataSetChanged();
                if (z) {
                    z = this.layoutManager;
                    if (this.liveLocationType != 1) {
                        if (this.liveLocationType != 2) {
                            measuredHeight = 0;
                            z.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + measuredHeight)));
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
                    measuredHeight = 66;
                    z.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) (32 + measuredHeight)));
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                    this.listView.post(/* anonymous class already generated */);
                } else {
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                }
            }
        }
    }

    private Location getLastLocation() {
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List providers = locationManager.getProviders(true);
        Location location = null;
        for (int size = providers.size() - 1; size >= 0; size--) {
            location = locationManager.getLastKnownLocation((String) providers.get(size));
            if (location != null) {
                break;
            }
        }
        return location;
    }

    private void positionMarker(Location location) {
        if (location != null) {
            this.myLocation = new Location(location);
            LiveLocation liveLocation = (LiveLocation) this.markersMap.get(UserConfig.getInstance(this.currentAccount).getClientUserId());
            SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
            if (!(liveLocation == null || sharingLocationInfo == null || liveLocation.object.id != sharingLocationInfo.mid)) {
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
                    if (this.firstWas != null) {
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    } else {
                        this.firstWas = true;
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                }
            }
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        this.messageObject = messageObject;
        this.dialogId = this.messageObject.getDialogId();
    }

    public void setDialogId(long j) {
        this.dialogId = j;
    }

    private void fetchRecentLocations(java.util.ArrayList<org.telegram.tgnet.TLRPC.Message> r11) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r10 = this;
        r0 = r10.firstFocus;
        if (r0 == 0) goto L_0x000a;
    L_0x0004:
        r0 = new com.google.android.gms.maps.model.LatLngBounds$Builder;
        r0.<init>();
        goto L_0x000b;
    L_0x000a:
        r0 = 0;
    L_0x000b:
        r1 = r10.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getCurrentTime();
        r2 = 0;
        r3 = r2;
    L_0x0017:
        r4 = r11.size();
        if (r3 >= r4) goto L_0x0048;
    L_0x001d:
        r4 = r11.get(r3);
        r4 = (org.telegram.tgnet.TLRPC.Message) r4;
        r5 = r4.date;
        r6 = r4.media;
        r6 = r6.period;
        r5 = r5 + r6;
        if (r5 <= r1) goto L_0x0045;
    L_0x002c:
        if (r0 == 0) goto L_0x0042;
    L_0x002e:
        r5 = new com.google.android.gms.maps.model.LatLng;
        r6 = r4.media;
        r6 = r6.geo;
        r6 = r6.lat;
        r8 = r4.media;
        r8 = r8.geo;
        r8 = r8._long;
        r5.<init>(r6, r8);
        r0.include(r5);
    L_0x0042:
        r10.addUserMarker(r4);
    L_0x0045:
        r3 = r3 + 1;
        goto L_0x0017;
    L_0x0048:
        if (r0 == 0) goto L_0x007a;
    L_0x004a:
        r10.firstFocus = r2;
        r1 = r10.adapter;
        r2 = r10.markers;
        r1.setLiveLocations(r2);
        r1 = r10.messageObject;
        r1 = r1.isLiveLocation();
        if (r1 == 0) goto L_0x007a;
    L_0x005b:
        r0 = r0.build();	 Catch:{ Exception -> 0x007a }
        r11 = r11.size();	 Catch:{ Exception -> 0x007a }
        r1 = 1;
        if (r11 <= r1) goto L_0x007a;
    L_0x0066:
        r11 = r10.googleMap;	 Catch:{ Exception -> 0x0076 }
        r1 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;	 Catch:{ Exception -> 0x0076 }
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);	 Catch:{ Exception -> 0x0076 }
        r0 = com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(r0, r1);	 Catch:{ Exception -> 0x0076 }
        r11.moveCamera(r0);	 Catch:{ Exception -> 0x0076 }
        goto L_0x007a;
    L_0x0076:
        r11 = move-exception;
        org.telegram.messenger.FileLog.m3e(r11);	 Catch:{ Exception -> 0x007a }
    L_0x007a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.fetchRecentLocations(java.util.ArrayList):void");
    }

    private boolean getRecentLocations() {
        ArrayList arrayList = (ArrayList) LocationController.getInstance(this.currentAccount).locationsCache.get(this.messageObject.getDialogId());
        if (arrayList == null || !arrayList.isEmpty()) {
            arrayList = null;
        } else {
            fetchRecentLocations(arrayList);
        }
        int i = (int) this.dialogId;
        boolean z = false;
        if (i < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TLObject tL_messages_getRecentLocations = new TL_messages_getRecentLocations();
        final long dialogId = this.messageObject.getDialogId();
        tL_messages_getRecentLocations.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) dialogId);
        tL_messages_getRecentLocations.limit = 100;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getRecentLocations, new RequestDelegate() {
            public void run(final TLObject tLObject, TL_error tL_error) {
                if (tLObject != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (LocationActivity.this.googleMap != null) {
                                messages_Messages messages_messages = (messages_Messages) tLObject;
                                int i = 0;
                                while (i < messages_messages.messages.size()) {
                                    if (!(((Message) messages_messages.messages.get(i)).media instanceof TL_messageMediaGeoLive)) {
                                        messages_messages.messages.remove(i);
                                        i--;
                                    }
                                    i++;
                                }
                                MessagesStorage.getInstance(LocationActivity.this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                                MessagesController.getInstance(LocationActivity.this.currentAccount).putUsers(messages_messages.users, false);
                                MessagesController.getInstance(LocationActivity.this.currentAccount).putChats(messages_messages.chats, false);
                                LocationController.getInstance(LocationActivity.this.currentAccount).locationsCache.put(dialogId, messages_messages.messages);
                                NotificationCenter.getInstance(LocationActivity.this.currentAccount).postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(dialogId));
                                LocationActivity.this.fetchRecentLocations(messages_messages.messages);
                            }
                        }
                    });
                }
            }
        });
        if (arrayList != null) {
            z = true;
        }
        return z;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i != NotificationCenter.locationPermissionGranted) {
            int i3 = 0;
            if (i == NotificationCenter.didReceivedNewMessages) {
                if (((Long) objArr[0]).longValue() == this.dialogId) {
                    if (this.messageObject != 0) {
                        ArrayList arrayList = (ArrayList) objArr[1];
                        i2 = 0;
                        while (i3 < arrayList.size()) {
                            MessageObject messageObject = (MessageObject) arrayList.get(i3);
                            if (messageObject.isLiveLocation()) {
                                addUserMarker(messageObject.messageOwner);
                                i2 = 1;
                            }
                            i3++;
                        }
                        if (!(i2 == 0 || this.adapter == 0)) {
                            this.adapter.setLiveLocations(this.markers);
                        }
                    }
                }
            } else if (i != NotificationCenter.messagesDeleted) {
                if (i == NotificationCenter.replaceMessagesObjects) {
                    i = ((Long) objArr[0]).longValue();
                    if (i == this.dialogId) {
                        if (this.messageObject != null) {
                            ArrayList arrayList2 = (ArrayList) objArr[1];
                            int i4 = 0;
                            while (i3 < arrayList2.size()) {
                                MessageObject messageObject2 = (MessageObject) arrayList2.get(i3);
                                if (messageObject2.isLiveLocation()) {
                                    LiveLocation liveLocation = (LiveLocation) this.markersMap.get(getMessageId(messageObject2.messageOwner));
                                    if (liveLocation != null) {
                                        SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(i);
                                        if (sharingLocationInfo == null || sharingLocationInfo.mid != messageObject2.getId()) {
                                            liveLocation.marker.setPosition(new LatLng(messageObject2.messageOwner.media.geo.lat, messageObject2.messageOwner.media.geo._long));
                                        }
                                        i4 = true;
                                    }
                                }
                                i3++;
                            }
                            if (!(i4 == 0 || this.adapter == 0)) {
                                this.adapter.updateLiveLocations();
                            }
                        }
                    }
                }
            }
        } else if (this.googleMap != 0) {
            try {
                this.googleMap.setMyLocationEnabled(true);
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
            } catch (Throwable th) {
                FileLog.m3e(th);
            }
        }
        this.onResumeCalled = true;
        if (this.googleMap != null) {
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        fixLayoutInternal(true);
        if (this.checkPermission && VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                if (parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
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

    public void setDelegate(LocationActivityDelegate locationActivityDelegate) {
        this.delegate = locationActivityDelegate;
    }

    private void updateSearchInterface() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        AnonymousClass21 anonymousClass21 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
            }
        };
        r10 = new ThemeDescription[50];
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r10[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r10[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r10[14] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon);
        r10[15] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground);
        r10[16] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground);
        r10[17] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon);
        r10[18] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground);
        r10[19] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground);
        r10[20] = new ThemeDescription(this.markerXImageView, 0, null, null, null, null, Theme.key_location_markerX);
        r10[21] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        AnonymousClass21 anonymousClass212 = anonymousClass21;
        r10[23] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, anonymousClass212, Theme.key_avatar_text);
        r10[24] = new ThemeDescription(null, 0, null, null, null, anonymousClass212, Theme.key_avatar_backgroundRed);
        r10[25] = new ThemeDescription(null, 0, null, null, null, anonymousClass212, Theme.key_avatar_backgroundOrange);
        r10[26] = new ThemeDescription(null, 0, null, null, null, anonymousClass212, Theme.key_avatar_backgroundViolet);
        r10[27] = new ThemeDescription(null, 0, null, null, null, anonymousClass212, Theme.key_avatar_backgroundGreen);
        r10[28] = new ThemeDescription(null, 0, null, null, null, anonymousClass212, Theme.key_avatar_backgroundCyan);
        r10[29] = new ThemeDescription(null, 0, null, null, null, anonymousClass212, Theme.key_avatar_backgroundBlue);
        r10[30] = new ThemeDescription(null, 0, null, null, null, anonymousClass212, Theme.key_avatar_backgroundPink);
        r10[31] = new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress");
        r10[32] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_location_placeLocationBackground);
        r10[33] = new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress");
        r10[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLocationIcon);
        r10[35] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLocationBackground);
        r10[36] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLiveLocationBackground);
        r10[37] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText7);
        r10[38] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[39] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[40] = new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[41] = new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[42] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[43] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[44] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[45] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r10[46] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[47] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[48] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[49] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        return r10;
    }
}
