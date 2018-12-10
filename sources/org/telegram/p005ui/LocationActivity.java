package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
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
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.p005ui.ActionBar.ActionBarMenu;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Adapters.LocationActivityAdapter;
import org.telegram.p005ui.Adapters.LocationActivitySearchAdapter;
import org.telegram.p005ui.Cells.GraySectionCell;
import org.telegram.p005ui.Cells.LocationCell;
import org.telegram.p005ui.Cells.LocationLoadingCell;
import org.telegram.p005ui.Cells.LocationPoweredCell;
import org.telegram.p005ui.Cells.SendLocationCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.MapPlaceholderDrawable;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.tgnet.ConnectionsManager;
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

/* renamed from: org.telegram.ui.LocationActivity */
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
    private int overScrollHeight = ((AndroidUtilities.displaySize.x - CLASSNAMEActionBar.getCurrentActionBarHeight()) - AndroidUtilities.m9dp(66.0f));
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
    class CLASSNAME extends ViewOutlineProvider {
        CLASSNAME() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$9 */
    class CLASSNAME extends ViewOutlineProvider {
        CLASSNAME() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$LiveLocation */
    public class LiveLocation {
        public Chat chat;
        /* renamed from: id */
        public int f250id;
        public Marker marker;
        public Message object;
        public User user;
    }

    /* renamed from: org.telegram.ui.LocationActivity$LocationActivityDelegate */
    public interface LocationActivityDelegate {
        void didSelectLocation(MessageMedia messageMedia, int i);
    }

    /* renamed from: org.telegram.ui.LocationActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                LocationActivity.this.lambda$checkDiscard$70$PassportActivity();
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
                    LocationActivity.this.getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$2 */
    class CLASSNAME extends ActionBarMenuItemSearchListener {
        CLASSNAME() {
        }

        public void onSearchExpand() {
            LocationActivity.this.searching = true;
            LocationActivity.this.otherItem.setVisibility(8);
            LocationActivity.this.listView.setVisibility(8);
            LocationActivity.this.mapViewClip.setVisibility(8);
            LocationActivity.this.searchListView.setVisibility(0);
            LocationActivity.this.searchListView.setEmptyView(LocationActivity.this.emptyView);
            LocationActivity.this.emptyView.showTextView();
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
                LocationActivity.this.emptyView.showProgress();
                LocationActivity.this.searchAdapter.searchDelayed(text, LocationActivity.this.userLocation);
            }
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$6 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (LocationActivity.this.adapter.getItemCount() != 0) {
                int position = LocationActivity.this.layoutManager.findFirstVisibleItemPosition();
                if (position != -1) {
                    LocationActivity.this.updateClipView(position);
                    if (dy > 0 && !LocationActivity.this.adapter.isPulledUp()) {
                        LocationActivity.this.adapter.setPulledUp();
                        if (LocationActivity.this.myLocation != null) {
                            AndroidUtilities.runOnUIThread(new LocationActivity$6$$Lambda$0(this));
                        }
                    }
                }
            }
        }

        final /* synthetic */ void lambda$onScrolled$0$LocationActivity$6() {
            LocationActivity.this.adapter.searchPlacesWithQuery(null, LocationActivity.this.myLocation, true);
        }
    }

    /* renamed from: org.telegram.ui.LocationActivity$8 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
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
            if (this.googleMap != null) {
                this.googleMap.setMyLocationEnabled(false);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        try {
            if (this.mapView != null) {
                this.mapView.onDestroy();
            }
        } catch (Throwable e2) {
            FileLog.m13e(e2);
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
        Drawable shadowDrawable;
        Drawable combinedDrawable;
        StateListAnimator animator;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAddToContainer(false);
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.messageObject == null) {
            this.actionBar.setTitle(LocaleController.getString("ShareLocation", R.string.ShareLocation));
            menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new CLASSNAME()).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        } else if (this.messageObject.isLiveLocation()) {
            this.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation));
        } else {
            if (this.messageObject.messageOwner.media.title == null || this.messageObject.messageOwner.media.title.length() <= 0) {
                this.actionBar.setTitle(LocaleController.getString("ChatLocation", R.string.ChatLocation));
            } else {
                this.actionBar.setTitle(LocaleController.getString("SharedPlace", R.string.SharedPlace));
            }
            menu.addItem(1, (int) R.drawable.share);
        }
        this.otherItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        this.otherItem.addSubItem(2, LocaleController.getString("Map", R.string.Map));
        this.otherItem.addSubItem(3, LocaleController.getString("Satellite", R.string.Satellite));
        this.otherItem.addSubItem(4, LocaleController.getString("Hybrid", R.string.Hybrid));
        this.fragmentView = new FrameLayout(context) {
            private boolean first = true;

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (changed) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                }
            }
        };
        FrameLayout frameLayout = this.fragmentView;
        this.locationButton = new ImageView(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.m9dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
            drawable = combinedDrawable;
        }
        this.locationButton.setBackgroundDrawable(drawable);
        this.locationButton.setImageResource(R.drawable.myloc_on);
        this.locationButton.setScaleType(ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        if (VERSION.SDK_INT >= 21) {
            animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(2.0f), (float) AndroidUtilities.m9dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(2.0f)}).setDuration(200));
            this.locationButton.setStateListAnimator(animator);
            this.locationButton.setOutlineProvider(new CLASSNAME());
        }
        if (this.messageObject != null) {
            this.userLocation = new Location("network");
            this.userLocation.setLatitude(this.messageObject.messageOwner.media.geo.lat);
            this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
        }
        this.searchWas = false;
        this.searching = false;
        this.mapViewClip = new FrameLayout(context);
        this.mapViewClip.setBackgroundDrawable(new MapPlaceholderDrawable());
        if (this.adapter != null) {
            this.adapter.destroy();
        }
        if (this.searchAdapter != null) {
            this.searchAdapter.destroy();
        }
        this.listView = new RecyclerListView(context);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = this.listView;
        Adapter locationActivityAdapter = new LocationActivityAdapter(context, this.liveLocationType, this.dialogId);
        this.adapter = locationActivityAdapter;
        recyclerListView.setAdapter(locationActivityAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        recyclerListView = this.listView;
        LayoutManager CLASSNAME = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = CLASSNAME;
        recyclerListView.setLayoutManager(CLASSNAME);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new CLASSNAME());
        this.listView.setOnItemClickListener(new LocationActivity$$Lambda$0(this));
        this.adapter.setDelegate(this.dialogId, new LocationActivity$$Lambda$1(this));
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        this.mapView = new MapView(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (LocationActivity.this.messageObject == null) {
                    AnimatorSet access$1600;
                    Animator[] animatorArr;
                    if (ev.getAction() == 0) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$1600 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", new float[]{(float) (LocationActivity.this.markerTop + (-AndroidUtilities.m9dp(10.0f)))});
                        animatorArr[1] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", new float[]{1.0f});
                        access$1600.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    } else if (ev.getAction() == 1) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$1600 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", new float[]{(float) LocationActivity.this.markerTop});
                        animatorArr[1] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", new float[]{0.0f});
                        access$1600.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    }
                    if (ev.getAction() == 2) {
                        if (!LocationActivity.this.userLocationMoved) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.setDuration(200);
                            animatorSet.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[]{1.0f}));
                            animatorSet.start();
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
        new Thread(new LocationActivity$$Lambda$2(this, this.mapView)).start();
        View view = new View(context);
        view.setBackgroundResource(R.drawable.header_shadow_reverse);
        this.mapViewClip.addView(view, LayoutHelper.createFrame(-1, 3, 83));
        if (this.messageObject == null) {
            this.markerImageView = new ImageView(context);
            this.markerImageView.setImageResource(R.drawable.map_pin);
            this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(24, 42, 49));
            this.markerXImageView = new ImageView(context);
            this.markerXImageView.setAlpha(0.0f);
            this.markerXImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_markerX), Mode.MULTIPLY));
            this.markerXImageView.setImageResource(R.drawable.place_x);
            this.mapViewClip.addView(this.markerXImageView, LayoutHelper.createFrame(14, 14, 49));
            this.emptyView = new EmptyTextProgressView(context);
            this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
            this.emptyView.setShowAtCenter(true);
            this.emptyView.setVisibility(8);
            frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.searchListView = new RecyclerListView(context);
            this.searchListView.setVisibility(8);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
            recyclerListView = this.searchListView;
            locationActivityAdapter = new LocationActivitySearchAdapter(context);
            this.searchAdapter = locationActivityAdapter;
            recyclerListView.setAdapter(locationActivityAdapter);
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new CLASSNAME());
            this.searchListView.setOnItemClickListener(new LocationActivity$$Lambda$3(this));
        } else if (!this.messageObject.isLiveLocation()) {
            float f;
            int i;
            float f2;
            float f3;
            this.routeButton = new ImageView(context);
            drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.m9dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
            if (VERSION.SDK_INT < 21) {
                shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
                drawable = combinedDrawable;
            }
            this.routeButton.setBackgroundDrawable(drawable);
            this.routeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
            this.routeButton.setImageResource(R.drawable.navigate);
            this.routeButton.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.routeButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(2.0f), (float) AndroidUtilities.m9dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.routeButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(2.0f)}).setDuration(200));
                this.routeButton.setStateListAnimator(animator);
                this.routeButton.setOutlineProvider(new CLASSNAME());
            }
            View view2 = this.routeButton;
            int i2 = VERSION.SDK_INT >= 21 ? 56 : 60;
            if (VERSION.SDK_INT >= 21) {
                f = 56.0f;
            } else {
                f = 60.0f;
            }
            if (LocaleController.isRTL) {
                i = 3;
            } else {
                i = 5;
            }
            i |= 80;
            if (LocaleController.isRTL) {
                f2 = 14.0f;
            } else {
                f2 = 0.0f;
            }
            if (LocaleController.isRTL) {
                f3 = 0.0f;
            } else {
                f3 = 14.0f;
            }
            frameLayout.addView(view2, LayoutHelper.createFrame(i2, f, i, f2, 0.0f, f3, 37.0f));
            this.routeButton.setOnClickListener(new LocationActivity$$Lambda$4(this));
            this.adapter.setMessageObject(this.messageObject);
        }
        if (this.messageObject == null || this.messageObject.isLiveLocation()) {
            this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        } else {
            this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 43.0f));
        }
        this.locationButton.setOnClickListener(new LocationActivity$$Lambda$5(this));
        if (this.messageObject == null) {
            this.locationButton.setAlpha(0.0f);
        }
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$1$LocationActivity(View view, int position) {
        if (position != 1 || this.messageObject == null || this.messageObject.isLiveLocation()) {
            if (position == 1 && this.liveLocationType != 2) {
                if (!(this.delegate == null || this.userLocation == null)) {
                    TL_messageMediaGeo location = new TL_messageMediaGeo();
                    location.geo = new TL_geoPoint();
                    location.geo.lat = AndroidUtilities.fixLocationCoord(this.userLocation.getLatitude());
                    location.geo._long = AndroidUtilities.fixLocationCoord(this.userLocation.getLongitude());
                    this.delegate.didSelectLocation(location, this.liveLocationType);
                }
                lambda$checkDiscard$70$PassportActivity();
            } else if ((position != 2 || this.liveLocationType != 1) && ((position != 1 || this.liveLocationType != 2) && (position != 3 || this.liveLocationType != 3))) {
                Object object = this.adapter.getItem(position);
                if (object instanceof TL_messageMediaVenue) {
                    if (!(object == null || this.delegate == null)) {
                        this.delegate.didSelectLocation((TL_messageMediaVenue) object, this.liveLocationType);
                    }
                    lambda$checkDiscard$70$PassportActivity();
                } else if (object instanceof LiveLocation) {
                    this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation) object).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            } else if (LocationController.getInstance(this.currentAccount).isSharingLocation(this.dialogId)) {
                LocationController.getInstance(this.currentAccount).removeSharingLocation(this.dialogId);
                lambda$checkDiscard$70$PassportActivity();
            } else if (this.delegate != null && getParentActivity() != null && this.myLocation != null) {
                User user = null;
                if (((int) this.dialogId) > 0) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) this.dialogId));
                }
                showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new LocationActivity$$Lambda$15(this)));
            }
        } else if (this.googleMap != null) {
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.messageObject.messageOwner.media.geo.lat, this.messageObject.messageOwner.media.geo._long), this.googleMap.getMaxZoomLevel() - 4.0f));
        }
    }

    final /* synthetic */ void lambda$null$0$LocationActivity(int param) {
        TL_messageMediaGeoLive location = new TL_messageMediaGeoLive();
        location.geo = new TL_geoPoint();
        location.geo.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        location.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        location.period = param;
        this.delegate.didSelectLocation(location, this.liveLocationType);
        lambda$checkDiscard$70$PassportActivity();
    }

    final /* synthetic */ void lambda$createView$2$LocationActivity(ArrayList places) {
        if (!(this.wasResults || places.isEmpty())) {
            this.wasResults = true;
        }
        this.emptyView.showTextView();
    }

    final /* synthetic */ void lambda$createView$5$LocationActivity(MapView map) {
        try {
            map.onCreate(null);
        } catch (Exception e) {
        }
        AndroidUtilities.runOnUIThread(new LocationActivity$$Lambda$13(this, map));
    }

    final /* synthetic */ void lambda$null$4$LocationActivity(MapView map) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                map.onCreate(null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new LocationActivity$$Lambda$14(this));
                this.mapsInitialized = true;
                if (this.onResumeCalled) {
                    this.mapView.onResume();
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    final /* synthetic */ void lambda$null$3$LocationActivity(GoogleMap map1) {
        this.googleMap = map1;
        this.googleMap.setPadding(AndroidUtilities.m9dp(70.0f), 0, AndroidUtilities.m9dp(70.0f), AndroidUtilities.m9dp(10.0f));
        onMapInit();
    }

    final /* synthetic */ void lambda$createView$6$LocationActivity(View view, int position) {
        TL_messageMediaVenue object = this.searchAdapter.getItem(position);
        if (!(object == null || this.delegate == null)) {
            this.delegate.didSelectLocation(object, this.liveLocationType);
        }
        lambda$checkDiscard$70$PassportActivity();
    }

    final /* synthetic */ void lambda$createView$7$LocationActivity(View v) {
        if (VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (!(activity == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                showPermissionAlert(true);
                return;
            }
        }
        if (this.myLocation != null) {
            try {
                getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[]{Double.valueOf(this.myLocation.getLatitude()), Double.valueOf(this.myLocation.getLongitude()), Double.valueOf(this.messageObject.messageOwner.media.geo.lat), Double.valueOf(this.messageObject.messageOwner.media.geo._long)}))));
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    final /* synthetic */ void lambda$createView$8$LocationActivity(View v) {
        if (VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (!(activity == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                showPermissionAlert(false);
                return;
            }
        }
        if (this.messageObject != null) {
            if (this.myLocation != null && this.googleMap != null) {
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (this.myLocation != null && this.googleMap != null) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(200);
            animatorSet.play(ObjectAnimator.ofFloat(this.locationButton, "alpha", new float[]{0.0f}));
            animatorSet.start();
            this.adapter.setCustomLocation(null);
            this.userLocationMoved = false;
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude())));
        }
    }

    private Bitmap createUserBitmap(LiveLocation liveLocation) {
        Bitmap result = null;
        FileLocation photo = null;
        try {
            if (liveLocation.user != null && liveLocation.user.photo != null) {
                photo = liveLocation.user.photo.photo_small;
            } else if (!(liveLocation.chat == null || liveLocation.chat.photo == null)) {
                photo = liveLocation.chat.photo.photo_small;
            }
            result = Bitmap.createBitmap(AndroidUtilities.m9dp(62.0f), AndroidUtilities.m9dp(76.0f), Config.ARGB_8888);
            result.eraseColor(0);
            Canvas canvas = new Canvas(result);
            Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.livepin);
            drawable.setBounds(0, 0, AndroidUtilities.m9dp(62.0f), AndroidUtilities.m9dp(76.0f));
            drawable.draw(canvas);
            Paint roundPaint = new Paint(1);
            RectF bitmapRect = new RectF();
            canvas.save();
            if (photo != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                if (bitmap != null) {
                    Shader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
                    Matrix matrix = new Matrix();
                    float scale = ((float) AndroidUtilities.m9dp(52.0f)) / ((float) bitmap.getWidth());
                    matrix.postTranslate((float) AndroidUtilities.m9dp(5.0f), (float) AndroidUtilities.m9dp(5.0f));
                    matrix.postScale(scale, scale);
                    roundPaint.setShader(bitmapShader);
                    bitmapShader.setLocalMatrix(matrix);
                    bitmapRect.set((float) AndroidUtilities.m9dp(5.0f), (float) AndroidUtilities.m9dp(5.0f), (float) AndroidUtilities.m9dp(57.0f), (float) AndroidUtilities.m9dp(57.0f));
                    canvas.drawRoundRect(bitmapRect, (float) AndroidUtilities.m9dp(26.0f), (float) AndroidUtilities.m9dp(26.0f), roundPaint);
                }
            } else {
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                if (liveLocation.user != null) {
                    avatarDrawable.setInfo(liveLocation.user);
                } else if (liveLocation.chat != null) {
                    avatarDrawable.setInfo(liveLocation.chat);
                }
                canvas.translate((float) AndroidUtilities.m9dp(5.0f), (float) AndroidUtilities.m9dp(5.0f));
                avatarDrawable.setBounds(0, 0, AndroidUtilities.m9dp(52.2f), AndroidUtilities.m9dp(52.2f));
                avatarDrawable.draw(canvas);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {
            }
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        }
        return result;
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
                liveLocation.f250id = liveLocation.object.from_id;
            } else {
                int did = (int) MessageObject.getDialogId(message);
                if (did > 0) {
                    liveLocation.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(did));
                    liveLocation.f250id = did;
                } else {
                    liveLocation.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-did));
                    liveLocation.f250id = did;
                }
            }
            try {
                MarkerOptions options = new MarkerOptions().position(latLng);
                Bitmap bitmap = createUserBitmap(liveLocation);
                if (bitmap != null) {
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    options.anchor(0.5f, 0.907f);
                    liveLocation.marker = this.googleMap.addMarker(options);
                    this.markers.add(liveLocation);
                    this.markersMap.put(liveLocation.f250id, liveLocation);
                    SharingLocationInfo myInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
                    if (liveLocation.f250id == UserConfig.getInstance(this.currentAccount).getClientUserId() && myInfo != null && liveLocation.object.f104id == myInfo.mid && this.myLocation != null) {
                        liveLocation.marker.setPosition(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
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
                LiveLocation liveLocation = addUserMarker(this.messageObject.messageOwner);
                if (!getRecentLocations()) {
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(liveLocation.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            } else {
                LatLng latLng = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                try {
                    this.googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
                this.firstFocus = false;
                getRecentLocations();
            }
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e2) {
                FileLog.m13e(e2);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnMyLocationChangeListener(new LocationActivity$$Lambda$6(this));
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
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new LocationActivity$$Lambda$7(this));
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            showDialog(builder.create());
                        }
                    } catch (Throwable e22) {
                        FileLog.m13e(e22);
                    }
                }
            }
        }
    }

    final /* synthetic */ void lambda$onMapInit$9$LocationActivity(Location location) {
        positionMarker(location);
        LocationController.getInstance(this.currentAccount).setGoogleMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    final /* synthetic */ void lambda$onMapInit$10$LocationActivity(DialogInterface dialog, int id) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception e) {
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
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new LocationActivity$$Lambda$8(this));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$showPermissionAlert$11$LocationActivity(DialogInterface dialog, int which) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                getParentActivity().startActivity(intent);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            try {
                if (this.mapView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mapView.getParent()).removeView(this.mapView);
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            if (this.mapViewClip != null) {
                this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.m9dp(10.0f), 51));
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
                int i;
                if (firstVisibleItem == 0) {
                    int i2;
                    top = child.getTop();
                    i = this.overScrollHeight;
                    if (top < 0) {
                        i2 = top;
                    } else {
                        i2 = 0;
                    }
                    height = i + i2;
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
                        i = ((-top) - AndroidUtilities.m9dp(42.0f)) + (height / 2);
                        this.markerTop = i;
                        imageView.setTranslationY((float) i);
                        this.markerXImageView.setTranslationY((float) (((-top) - AndroidUtilities.m9dp(7.0f)) + (height / 2)));
                    }
                    if (this.routeButton != null) {
                        this.routeButton.setTranslationY((float) top);
                    }
                    LayoutParams layoutParams = (LayoutParams) this.mapView.getLayoutParams();
                    if (layoutParams != null && layoutParams.height != this.overScrollHeight + AndroidUtilities.m9dp(10.0f)) {
                        layoutParams.height = this.overScrollHeight + AndroidUtilities.m9dp(10.0f);
                        if (this.googleMap != null) {
                            this.googleMap.setPadding(AndroidUtilities.m9dp(70.0f), 0, AndroidUtilities.m9dp(70.0f), AndroidUtilities.m9dp(10.0f));
                        }
                        this.mapView.setLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    private void fixLayoutInternal(boolean resume) {
        if (this.listView != null) {
            int height = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + CLASSNAMEActionBar.getCurrentActionBarHeight();
            int viewHeight = this.fragmentView.getMeasuredHeight();
            if (viewHeight != 0) {
                this.overScrollHeight = (viewHeight - AndroidUtilities.m9dp(66.0f)) - height;
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
                    layoutParams.height = this.overScrollHeight + AndroidUtilities.m9dp(10.0f);
                    if (this.googleMap != null) {
                        this.googleMap.setPadding(AndroidUtilities.m9dp(70.0f), 0, AndroidUtilities.m9dp(70.0f), AndroidUtilities.m9dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams);
                }
                this.adapter.notifyDataSetChanged();
                if (resume) {
                    int i;
                    LinearLayoutManager linearLayoutManager = this.layoutManager;
                    if (this.liveLocationType == 1 || this.liveLocationType == 2) {
                        i = 66;
                    } else {
                        i = 0;
                    }
                    linearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.m9dp((float) (i + 32)));
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                    this.listView.post(new LocationActivity$$Lambda$9(this));
                    return;
                }
                updateClipView(this.layoutManager.findFirstVisibleItemPosition());
            }
        }
    }

    final /* synthetic */ void lambda$fixLayoutInternal$12$LocationActivity() {
        int i;
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        if (this.liveLocationType == 1 || this.liveLocationType == 2) {
            i = 66;
        } else {
            i = 0;
        }
        linearLayoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.m9dp((float) (i + 32)));
        updateClipView(this.layoutManager.findFirstVisibleItemPosition());
    }

    private Location getLastLocation() {
        LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation((String) providers.get(i));
            if (l != null) {
                break;
            }
        }
        return l;
    }

    private void positionMarker(Location location) {
        if (location != null) {
            this.myLocation = new Location(location);
            LiveLocation liveLocation = (LiveLocation) this.markersMap.get(UserConfig.getInstance(this.currentAccount).getClientUserId());
            SharingLocationInfo myInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
            if (!(liveLocation == null || myInfo == null || liveLocation.object.f104id != myInfo.mid)) {
                liveLocation.marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            if (this.messageObject != null || this.googleMap == null) {
                this.adapter.setGpsLocation(this.myLocation);
                return;
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (this.adapter != null) {
                if (this.adapter.isPulledUp()) {
                    this.adapter.searchPlacesWithQuery(null, this.myLocation, true);
                }
                this.adapter.setGpsLocation(this.myLocation);
            }
            if (!this.userLocationMoved) {
                this.userLocation = new Location(location);
                if (this.firstWas) {
                    this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    return;
                }
                this.firstWas = true;
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
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
                            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, AndroidUtilities.m9dp(60.0f)));
                        } catch (Throwable e) {
                            FileLog.m13e(e);
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
        if (lower_id < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TL_messages_getRecentLocations req = new TL_messages_getRecentLocations();
        long dialog_id = this.messageObject.getDialogId();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) dialog_id);
        req.limit = 100;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LocationActivity$$Lambda$10(this, dialog_id));
        if (messages != null) {
            return true;
        }
        return false;
    }

    final /* synthetic */ void lambda$getRecentLocations$14$LocationActivity(long dialog_id, TLObject response, TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new LocationActivity$$Lambda$12(this, response, dialog_id));
        }
    }

    final /* synthetic */ void lambda$null$13$LocationActivity(TLObject response, long dialog_id) {
        if (this.googleMap != null) {
            messages_Messages res = (messages_Messages) response;
            int a = 0;
            while (a < res.messages.size()) {
                if (!(((Message) res.messages.get(a)).media instanceof TL_messageMediaGeoLive)) {
                    res.messages.remove(a);
                    a--;
                }
                a++;
            }
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
            LocationController.getInstance(this.currentAccount).locationsCache.put(dialog_id, res.messages);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(dialog_id));
            fetchRecentLocations(res.messages);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int a;
        MessageObject messageObject;
        if (id == NotificationCenter.closeChats) {
            lambda$null$10$ProfileActivity();
        } else if (id == NotificationCenter.locationPermissionGranted) {
            if (this.googleMap != null) {
                try {
                    this.googleMap.setMyLocationEnabled(true);
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
        } else if (id == NotificationCenter.didReceivedNewMessages) {
            if (((Long) args[0]).longValue() == this.dialogId && this.messageObject != null) {
                ArrayList<MessageObject> arr = args[1];
                boolean added = false;
                for (a = 0; a < arr.size(); a++) {
                    messageObject = (MessageObject) arr.get(a);
                    if (messageObject.isLiveLocation()) {
                        addUserMarker(messageObject.messageOwner);
                        added = true;
                    }
                }
                if (added && this.adapter != null) {
                    this.adapter.setLiveLocations(this.markers);
                }
            }
        } else if (id != NotificationCenter.messagesDeleted && id == NotificationCenter.replaceMessagesObjects) {
            long did = ((Long) args[0]).longValue();
            if (did == this.dialogId && this.messageObject != null) {
                boolean updated = false;
                ArrayList<MessageObject> messageObjects = args[1];
                for (a = 0; a < messageObjects.size(); a++) {
                    messageObject = (MessageObject) messageObjects.get(a);
                    if (messageObject.isLiveLocation()) {
                        LiveLocation liveLocation = (LiveLocation) this.markersMap.get(getMessageId(messageObject.messageOwner));
                        if (liveLocation != null) {
                            SharingLocationInfo myInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(did);
                            if (myInfo == null || myInfo.mid != messageObject.getId()) {
                                liveLocation.marker.setPosition(new LatLng(messageObject.messageOwner.media.geo.lat, messageObject.messageOwner.media.geo._long));
                            }
                            updated = true;
                        }
                    }
                }
                if (updated && this.adapter != null) {
                    this.adapter.updateLiveLocations();
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mapView != null && this.mapsInitialized) {
            try {
                this.mapView.onPause();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
        this.onResumeCalled = false;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.mapView != null && this.mapsInitialized) {
            try {
                this.mapView.onResume();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
        this.onResumeCalled = true;
        if (this.googleMap != null) {
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e2) {
                FileLog.m13e(e2);
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
        ThemeDescriptionDelegate cellDelegate = LocationActivity$$Lambda$11.$instance;
        r10 = new ThemeDescription[52];
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
        r10[21] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_graySectionText);
        r10[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        r10[23] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, cellDelegate, Theme.key_avatar_text);
        r10[24] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        r10[25] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        r10[26] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        r10[27] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        r10[28] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        r10[29] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        r10[30] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        r10[31] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_location_liveLocationProgress);
        r10[32] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_location_placeLocationBackground);
        r10[33] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialog_liveLocationProgress);
        r10[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLocationIcon);
        r10[35] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLiveLocationIcon);
        r10[36] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLocationBackground);
        r10[37] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLiveLocationBackground);
        r10[38] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[39] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText2);
        r10[40] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText7);
        r10[41] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[42] = new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[43] = new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[44] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[45] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[46] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[47] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r10[48] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[49] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[50] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[51] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        return r10;
    }

    static final /* synthetic */ void lambda$getThemeDescriptions$15$LocationActivity() {
    }
}
