package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.LocationActivityAdapter;
import org.telegram.ui.Adapters.LocationActivitySearchAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationDirectionCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;
import org.telegram.ui.Components.ProximitySheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;

public class LocationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final double EARTHRADIUS = 6366198.0d;
    public static final int LOCATION_TYPE_GROUP = 4;
    public static final int LOCATION_TYPE_GROUP_VIEW = 5;
    public static final int LOCATION_TYPE_LIVE_VIEW = 6;
    public static final int LOCATION_TYPE_SEND = 0;
    public static final int LOCATION_TYPE_SEND_WITH_LIVE = 1;
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    private static final int open_in = 1;
    private static final int share_live_location = 5;
    /* access modifiers changed from: private */
    public LocationActivityAdapter adapter;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private int askWithRadius;
    private AvatarDrawable avatarDrawable;
    private Bitmap[] bitmapCache = new Bitmap[7];
    private boolean canUndo;
    /* access modifiers changed from: private */
    public TLRPC.TL_channelLocation chatLocation;
    private boolean checkBackgroundPermission = true;
    private boolean checkGpsEnabled = true;
    private boolean checkPermission = true;
    private CircleOptions circleOptions;
    private boolean currentMapStyleDark;
    /* access modifiers changed from: private */
    public LocationActivityDelegate delegate;
    private long dialogId;
    private ImageView emptyImageView;
    /* access modifiers changed from: private */
    public TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    private boolean firstFocus = true;
    private boolean firstWas;
    /* access modifiers changed from: private */
    public CameraUpdate forceUpdate;
    /* access modifiers changed from: private */
    public GoogleMap googleMap;
    private HintView hintView;
    private TLRPC.TL_channelLocation initialLocation;
    private boolean isFirstLocation = true;
    /* access modifiers changed from: private */
    public Marker lastPressedMarker;
    /* access modifiers changed from: private */
    public FrameLayout lastPressedMarkerView;
    /* access modifiers changed from: private */
    public VenueLocation lastPressedVenue;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ImageView locationButton;
    private boolean locationDenied = false;
    /* access modifiers changed from: private */
    public int locationType;
    private ActionBarMenuItem mapTypeButton;
    private MapView mapView;
    /* access modifiers changed from: private */
    public FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private Runnable markAsReadRunnable;
    /* access modifiers changed from: private */
    public View markerImageView;
    /* access modifiers changed from: private */
    public int markerTop;
    private ArrayList<LiveLocation> markers = new ArrayList<>();
    private LongSparseArray<LiveLocation> markersMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public MessageObject messageObject;
    /* access modifiers changed from: private */
    public CameraUpdate moveToBounds;
    /* access modifiers changed from: private */
    public Location myLocation;
    private boolean onResumeCalled;
    /* access modifiers changed from: private */
    public ActionBarMenuItem otherItem;
    private int overScrollHeight = ((AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f));
    /* access modifiers changed from: private */
    public MapOverlayView overlayView;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
    private ArrayList<VenueLocation> placeMarkers = new ArrayList<>();
    private double previousRadius;
    /* access modifiers changed from: private */
    public boolean proximityAnimationInProgress;
    private ImageView proximityButton;
    private Circle proximityCircle;
    private ProximitySheet proximitySheet;
    /* access modifiers changed from: private */
    public boolean scrolling;
    /* access modifiers changed from: private */
    public LocationActivitySearchAdapter searchAdapter;
    private SearchButton searchAreaButton;
    /* access modifiers changed from: private */
    public boolean searchInProgress;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public RecyclerListView searchListView;
    /* access modifiers changed from: private */
    public boolean searchWas;
    private boolean searchedForCustomLocations;
    /* access modifiers changed from: private */
    public boolean searching;
    private View shadow;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private UndoView[] undoView = new UndoView[2];
    private Runnable updateRunnable;
    /* access modifiers changed from: private */
    public Location userLocation;
    /* access modifiers changed from: private */
    public boolean userLocationMoved;
    private boolean wasResults;
    /* access modifiers changed from: private */
    public float yOffset;

    public static class LiveLocation {
        public TLRPC.Chat chat;
        public Marker directionMarker;
        public boolean hasRotation;
        public long id;
        public Marker marker;
        public TLRPC.Message object;
        public TLRPC.User user;
    }

    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2);
    }

    public static class VenueLocation {
        public Marker marker;
        public int num;
        public TLRPC.TL_messageMediaVenue venue;
    }

    static /* synthetic */ float access$3316(LocationActivity x0, float x1) {
        float f = x0.yOffset + x1;
        x0.yOffset = f;
        return f;
    }

    private static class SearchButton extends TextView {
        private float additionanTranslationY;
        private float currentTranslationY;

        public SearchButton(Context context) {
            super(context);
        }

        public float getTranslationX() {
            return this.additionanTranslationY;
        }

        public void setTranslationX(float translationX) {
            this.additionanTranslationY = translationX;
            updateTranslationY();
        }

        public void setTranslation(float value) {
            this.currentTranslationY = value;
            updateTranslationY();
        }

        private void updateTranslationY() {
            setTranslationY(this.currentTranslationY + this.additionanTranslationY);
        }
    }

    public class MapOverlayView extends FrameLayout {
        private HashMap<Marker, View> views = new HashMap<>();

        public MapOverlayView(Context context) {
            super(context);
        }

        public void addInfoView(Marker marker) {
            Marker marker2 = marker;
            VenueLocation location = (VenueLocation) marker.getTag();
            if (location != null && LocationActivity.this.lastPressedVenue != location) {
                LocationActivity.this.showSearchPlacesButton(false);
                if (LocationActivity.this.lastPressedMarker != null) {
                    removeInfoView(LocationActivity.this.lastPressedMarker);
                    Marker unused = LocationActivity.this.lastPressedMarker = null;
                }
                VenueLocation unused2 = LocationActivity.this.lastPressedVenue = location;
                Marker unused3 = LocationActivity.this.lastPressedMarker = marker2;
                Context context = getContext();
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createFrame(-2, 114.0f));
                FrameLayout unused4 = LocationActivity.this.lastPressedMarkerView = new FrameLayout(context);
                LocationActivity.this.lastPressedMarkerView.setBackgroundResource(NUM);
                LocationActivity.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
                frameLayout.addView(LocationActivity.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
                LocationActivity.this.lastPressedMarkerView.setAlpha(0.0f);
                LocationActivity.this.lastPressedMarkerView.setOnClickListener(new LocationActivity$MapOverlayView$$ExternalSyntheticLambda0(this, location));
                TextView nameTextView = new TextView(context);
                nameTextView.setTextSize(1, 16.0f);
                nameTextView.setMaxLines(1);
                nameTextView.setEllipsize(TextUtils.TruncateAt.END);
                nameTextView.setSingleLine(true);
                nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                int i = 5;
                nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
                LocationActivity.this.lastPressedMarkerView.addView(nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 10.0f, 18.0f, 0.0f));
                TextView addressTextView = new TextView(context);
                addressTextView.setTextSize(1, 14.0f);
                addressTextView.setMaxLines(1);
                addressTextView.setEllipsize(TextUtils.TruncateAt.END);
                addressTextView.setSingleLine(true);
                addressTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
                FrameLayout access$300 = LocationActivity.this.lastPressedMarkerView;
                if (!LocaleController.isRTL) {
                    i = 3;
                }
                access$300.addView(addressTextView, LayoutHelper.createFrame(-2, -2.0f, i | 48, 18.0f, 32.0f, 18.0f, 0.0f));
                nameTextView.setText(location.venue.title);
                addressTextView.setText(LocaleController.getString("TapToSendLocation", NUM));
                final FrameLayout iconLayout = new FrameLayout(context);
                iconLayout.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(36.0f), LocationCell.getColorForIndex(location.num)));
                frameLayout.addView(iconLayout, LayoutHelper.createFrame(36, 36.0f, 81, 0.0f, 0.0f, 0.0f, 4.0f));
                BackupImageView imageView = new BackupImageView(context);
                imageView.setImage("https://ss3.4sqi.net/img/categories_v2/" + location.venue.venue_type + "_64.png", (String) null, (Drawable) null);
                iconLayout.addView(imageView, LayoutHelper.createFrame(30, 30, 17));
                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    private final float[] animatorValues = {0.0f, 1.0f};
                    private boolean startedInner;

                    public void onAnimationUpdate(ValueAnimator animation) {
                        float scale;
                        float value = AndroidUtilities.lerp(this.animatorValues, animation.getAnimatedFraction());
                        if (value >= 0.7f && !this.startedInner && LocationActivity.this.lastPressedMarkerView != null) {
                            AnimatorSet animatorSet1 = new AnimatorSet();
                            animatorSet1.playTogether(new Animator[]{ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.SCALE_Y, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.ALPHA, new float[]{0.0f, 1.0f})});
                            animatorSet1.setInterpolator(new OvershootInterpolator(1.02f));
                            animatorSet1.setDuration(250);
                            animatorSet1.start();
                            this.startedInner = true;
                        }
                        if (value <= 0.5f) {
                            scale = CubicBezierInterpolator.EASE_OUT.getInterpolation(value / 0.5f) * 1.1f;
                        } else if (value <= 0.75f) {
                            scale = 1.1f - (CubicBezierInterpolator.EASE_OUT.getInterpolation((value - 0.5f) / 0.25f) * 0.2f);
                        } else {
                            scale = (CubicBezierInterpolator.EASE_OUT.getInterpolation((value - 0.75f) / 0.25f) * 0.1f) + 0.9f;
                        }
                        iconLayout.setScaleX(scale);
                        iconLayout.setScaleY(scale);
                    }
                });
                animator.setDuration(360);
                animator.start();
                this.views.put(marker2, frameLayout);
                LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, (GoogleMap.CancelableCallback) null);
            }
        }

        /* renamed from: lambda$addInfoView$1$org-telegram-ui-LocationActivity$MapOverlayView  reason: not valid java name */
        public /* synthetic */ void m2443x40891783(VenueLocation location, View v) {
            if (LocationActivity.this.parentFragment == null || !LocationActivity.this.parentFragment.isInScheduleMode()) {
                LocationActivity.this.delegate.didSelectLocation(location.venue, LocationActivity.this.locationType, true, 0);
                LocationActivity.this.finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(LocationActivity.this.getParentActivity(), LocationActivity.this.parentFragment.getDialogId(), new LocationActivity$MapOverlayView$$ExternalSyntheticLambda1(this, location));
        }

        /* renamed from: lambda$addInfoView$0$org-telegram-ui-LocationActivity$MapOverlayView  reason: not valid java name */
        public /* synthetic */ void m2442x40ff7d82(VenueLocation location, boolean notify, int scheduleDate) {
            LocationActivity.this.delegate.didSelectLocation(location.venue, LocationActivity.this.locationType, notify, scheduleDate);
            LocationActivity.this.finishFragment();
        }

        public void removeInfoView(Marker marker) {
            View view = this.views.get(marker);
            if (view != null) {
                removeView(view);
                this.views.remove(marker);
            }
        }

        public void updatePositions() {
            if (LocationActivity.this.googleMap != null) {
                Projection projection = LocationActivity.this.googleMap.getProjection();
                for (Map.Entry<Marker, View> entry : this.views.entrySet()) {
                    View view = entry.getValue();
                    Point point = projection.toScreenLocation(entry.getKey().getPosition());
                    view.setTranslationX((float) (point.x - (view.getMeasuredWidth() / 2)));
                    view.setTranslationY((float) ((point.y - view.getMeasuredHeight()) + AndroidUtilities.dp(22.0f)));
                }
            }
        }
    }

    public LocationActivity(int type) {
        this.locationType = type;
        AndroidUtilities.fixGoogleMapsBug();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionDenied);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
        MessageObject messageObject2 = this.messageObject;
        if (messageObject2 == null || !messageObject2.isLiveLocation()) {
            return true;
        }
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.replaceMessagesObjects);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionDenied);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.replaceMessagesObjects);
        try {
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                googleMap2.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            MapView mapView2 = this.mapView;
            if (mapView2 != null) {
                mapView2.onDestroy();
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        Runnable runnable = this.updateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRunnable = null;
        }
        Runnable runnable2 = this.markAsReadRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.markAsReadRunnable = null;
        }
    }

    private UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            this.mapViewClip.removeView(this.undoView[0]);
            this.mapViewClip.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public boolean isSwipeBackEnabled(MotionEvent event) {
        return false;
    }

    public View createView(Context context) {
        FrameLayout.LayoutParams layoutParams;
        ActionBarMenu menu;
        FrameLayout.LayoutParams layoutParams2;
        CombinedDrawable drawable;
        TLRPC.Chat chat;
        int i;
        Context context2 = context;
        this.searchWas = false;
        this.searching = false;
        this.searchInProgress = false;
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        if (this.chatLocation != null) {
            Location location = new Location("network");
            this.userLocation = location;
            location.setLatitude(this.chatLocation.geo_point.lat);
            this.userLocation.setLongitude(this.chatLocation.geo_point._long);
        } else if (this.messageObject != null) {
            Location location2 = new Location("network");
            this.userLocation = location2;
            location2.setLatitude(this.messageObject.messageOwner.media.geo.lat);
            this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
        }
        this.locationDenied = (Build.VERSION.SDK_INT < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) ? false : true;
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAddToContainer(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    LocationActivity.this.finishFragment();
                } else if (id == 1) {
                    try {
                        double lat = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                        double lon = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                        Activity parentActivity = LocationActivity.this.getParentActivity();
                        parentActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else if (id == 5) {
                    LocationActivity.this.openShareLiveLocation(0);
                }
            }
        });
        ActionBarMenu menu2 = this.actionBar.createMenu();
        if (this.chatLocation != null) {
            this.actionBar.setTitle(LocaleController.getString("ChatLocation", NUM));
        } else {
            MessageObject messageObject2 = this.messageObject;
            if (messageObject2 == null) {
                this.actionBar.setTitle(LocaleController.getString("ShareLocation", NUM));
                if (this.locationType != 4) {
                    this.overlayView = new MapOverlayView(context2);
                    ActionBarMenuItem actionBarMenuItemSearchListener = menu2.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                        public void onSearchExpand() {
                            boolean unused = LocationActivity.this.searching = true;
                        }

                        public void onSearchCollapse() {
                            boolean unused = LocationActivity.this.searching = false;
                            boolean unused2 = LocationActivity.this.searchWas = false;
                            LocationActivity.this.searchAdapter.searchDelayed((String) null, (Location) null);
                            LocationActivity.this.updateEmptyView();
                        }

                        public void onTextChanged(EditText editText) {
                            if (LocationActivity.this.searchAdapter != null) {
                                String text = editText.getText().toString();
                                boolean z = false;
                                if (text.length() != 0) {
                                    boolean unused = LocationActivity.this.searchWas = true;
                                    LocationActivity.this.searchItem.setShowSearchProgress(true);
                                    if (LocationActivity.this.otherItem != null) {
                                        LocationActivity.this.otherItem.setVisibility(8);
                                    }
                                    LocationActivity.this.listView.setVisibility(8);
                                    LocationActivity.this.mapViewClip.setVisibility(8);
                                    if (LocationActivity.this.searchListView.getAdapter() != LocationActivity.this.searchAdapter) {
                                        LocationActivity.this.searchListView.setAdapter(LocationActivity.this.searchAdapter);
                                    }
                                    LocationActivity.this.searchListView.setVisibility(0);
                                    LocationActivity locationActivity = LocationActivity.this;
                                    if (locationActivity.searchAdapter.getItemCount() == 0) {
                                        z = true;
                                    }
                                    boolean unused2 = locationActivity.searchInProgress = z;
                                } else {
                                    if (LocationActivity.this.otherItem != null) {
                                        LocationActivity.this.otherItem.setVisibility(0);
                                    }
                                    LocationActivity.this.listView.setVisibility(0);
                                    LocationActivity.this.mapViewClip.setVisibility(0);
                                    LocationActivity.this.searchListView.setAdapter((RecyclerView.Adapter) null);
                                    LocationActivity.this.searchListView.setVisibility(8);
                                }
                                LocationActivity.this.updateEmptyView();
                                LocationActivity.this.searchAdapter.searchDelayed(text, LocationActivity.this.userLocation);
                            }
                        }
                    });
                    this.searchItem = actionBarMenuItemSearchListener;
                    actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", NUM));
                    this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
                    EditTextBoldCursor editText = this.searchItem.getSearchField();
                    editText.setTextColor(Theme.getColor("dialogTextBlack"));
                    editText.setCursorColor(Theme.getColor("dialogTextBlack"));
                    editText.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
                }
            } else if (messageObject2.isLiveLocation()) {
                this.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", NUM));
            } else {
                if (this.messageObject.messageOwner.media.title == null || this.messageObject.messageOwner.media.title.length() <= 0) {
                    this.actionBar.setTitle(LocaleController.getString("ChatLocation", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SharedPlace", NUM));
                }
                ActionBarMenuItem addItem = menu2.addItem(0, NUM);
                this.otherItem = addItem;
                addItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("OpenInExternalApp", NUM));
                if (!getLocationController().isSharingLocation(this.dialogId)) {
                    this.otherItem.addSubItem(5, NUM, (CharSequence) LocaleController.getString("SendLiveLocationMenu", NUM));
                }
                this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            }
        }
        this.fragmentView = new FrameLayout(context2) {
            private boolean first = true;

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (changed) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                    return;
                }
                LocationActivity.this.updateClipView(true);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == LocationActivity.this.actionBar && LocationActivity.this.parentLayout != null) {
                    LocationActivity.this.parentLayout.drawHeaderShadow(canvas, LocationActivity.this.actionBar.getMeasuredHeight());
                }
                return result;
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor("dialogBackground"));
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        final Rect padding = new Rect();
        this.shadowDrawable.getPadding(padding);
        int i2 = this.locationType;
        if (i2 == 0 || i2 == 1) {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f) + padding.top);
        } else {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(6.0f) + padding.top);
        }
        layoutParams.gravity = 83;
        AnonymousClass4 r0 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (LocationActivity.this.overlayView != null) {
                    LocationActivity.this.overlayView.updatePositions();
                }
            }
        };
        this.mapViewClip = r0;
        r0.setBackgroundDrawable(new MapPlaceholderDrawable());
        FrameLayout frameLayout2 = frameLayout;
        if (this.messageObject == null && ((i = this.locationType) == 0 || i == 1)) {
            SearchButton searchButton = new SearchButton(context2);
            this.searchAreaButton = searchButton;
            searchButton.setTranslationX((float) (-AndroidUtilities.dp(80.0f)));
            Drawable drawable2 = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable2 = context.getResources().getDrawable(NUM).mutate();
                shadowDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable2, drawable2, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
                combinedDrawable.setFullsize(true);
                drawable2 = combinedDrawable;
            } else {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.searchAreaButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.searchAreaButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.searchAreaButton.setStateListAnimator(animator);
                this.searchAreaButton.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) (view.getMeasuredHeight() / 2));
                    }
                });
            }
            this.searchAreaButton.setBackgroundDrawable(drawable2);
            this.searchAreaButton.setTextColor(Theme.getColor("location_actionActiveIcon"));
            this.searchAreaButton.setTextSize(1, 14.0f);
            this.searchAreaButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.searchAreaButton.setText(LocaleController.getString("PlacesInThisArea", NUM));
            this.searchAreaButton.setGravity(17);
            this.searchAreaButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.mapViewClip.addView(this.searchAreaButton, LayoutHelper.createFrame(-2, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
            this.searchAreaButton.setOnClickListener(new LocationActivity$$ExternalSyntheticLambda33(this));
        }
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, 0, Theme.getColor("location_actionIcon"));
        this.mapTypeButton = actionBarMenuItem;
        actionBarMenuItem.setClickable(true);
        this.mapTypeButton.setSubMenuOpenSide(2);
        this.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        this.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        this.mapTypeButton.addSubItem(2, NUM, (CharSequence) LocaleController.getString("Map", NUM));
        this.mapTypeButton.addSubItem(3, NUM, (CharSequence) LocaleController.getString("Satellite", NUM));
        this.mapTypeButton.addSubItem(4, NUM, (CharSequence) LocaleController.getString("Hybrid", NUM));
        this.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        Drawable drawable3 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable3 = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(shadowDrawable3, drawable3, 0, 0);
            combinedDrawable2.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            drawable3 = combinedDrawable2;
        } else {
            StateListAnimator animator2 = new StateListAnimator();
            animator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.mapTypeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            ActionBarMenuItem actionBarMenuItem2 = this.mapTypeButton;
            Property property = View.TRANSLATION_Z;
            float[] fArr = new float[2];
            fArr[0] = (float) AndroidUtilities.dp(4.0f);
            float[] fArr2 = fArr;
            fArr2[1] = (float) AndroidUtilities.dp(2.0f);
            animator2.addState(new int[0], ObjectAnimator.ofFloat(actionBarMenuItem2, property, fArr2).setDuration(200));
            this.mapTypeButton.setStateListAnimator(animator2);
            this.mapTypeButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.mapTypeButton.setBackgroundDrawable(drawable3);
        this.mapTypeButton.setIcon(NUM);
        this.mapViewClip.addView(this.mapTypeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        this.mapTypeButton.setOnClickListener(new LocationActivity$$ExternalSyntheticLambda34(this));
        this.mapTypeButton.setDelegate(new LocationActivity$$ExternalSyntheticLambda20(this));
        this.locationButton = new ImageView(context2);
        Drawable drawable4 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable4 = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable4.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(shadowDrawable4, drawable4, 0, 0);
            combinedDrawable3.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            drawable4 = combinedDrawable3;
            menu = menu2;
        } else {
            StateListAnimator animator3 = new StateListAnimator();
            menu = menu2;
            animator3.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            ImageView imageView = this.locationButton;
            Property property2 = View.TRANSLATION_Z;
            float[] fArr3 = new float[2];
            fArr3[0] = (float) AndroidUtilities.dp(4.0f);
            float[] fArr4 = fArr3;
            fArr4[1] = (float) AndroidUtilities.dp(2.0f);
            animator3.addState(new int[0], ObjectAnimator.ofFloat(imageView, property2, fArr4).setDuration(200));
            this.locationButton.setStateListAnimator(animator3);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.locationButton.setBackgroundDrawable(drawable4);
        this.locationButton.setImageResource(NUM);
        this.locationButton.setScaleType(ImageView.ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
        this.locationButton.setTag("location_actionActiveIcon");
        this.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", NUM));
        FrameLayout.LayoutParams layoutParams1 = LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f);
        layoutParams1.bottomMargin += layoutParams.height - padding.top;
        this.mapViewClip.addView(this.locationButton, layoutParams1);
        this.locationButton.setOnClickListener(new LocationActivity$$ExternalSyntheticLambda35(this));
        this.proximityButton = new ImageView(context2);
        Drawable drawable5 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable5 = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable5.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable4 = new CombinedDrawable(shadowDrawable5, drawable5, 0, 0);
            combinedDrawable4.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            layoutParams2 = layoutParams;
            drawable = combinedDrawable4;
        } else {
            StateListAnimator animator4 = new StateListAnimator();
            layoutParams2 = layoutParams;
            animator4.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.proximityButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator4.addState(new int[0], ObjectAnimator.ofFloat(this.proximityButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.proximityButton.setStateListAnimator(animator4);
            this.proximityButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
            drawable = drawable5;
        }
        this.proximityButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.proximityButton.setBackgroundDrawable(drawable);
        this.proximityButton.setScaleType(ImageView.ScaleType.CENTER);
        this.proximityButton.setContentDescription(LocaleController.getString("AccDescrLocationNotify", NUM));
        this.mapViewClip.addView(this.proximityButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 53, 0.0f, 62.0f, 12.0f, 0.0f));
        this.proximityButton.setOnClickListener(new LocationActivity$$ExternalSyntheticLambda36(this));
        if (DialogObject.isChatDialog(this.dialogId)) {
            chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
        } else {
            chat = null;
        }
        MessageObject messageObject3 = this.messageObject;
        if (messageObject3 == null || !messageObject3.isLiveLocation() || this.messageObject.isExpiredLiveLocation(getConnectionsManager().getCurrentTime()) || (ChatObject.isChannel(chat) && !chat.megagroup)) {
            this.proximityButton.setVisibility(8);
            this.proximityButton.setImageResource(NUM);
        } else {
            LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (myInfo == null || myInfo.proximityMeters <= 0) {
                if (DialogObject.isUserDialog(this.dialogId) && this.messageObject.getFromChatId() == getUserConfig().getClientUserId()) {
                    this.proximityButton.setVisibility(4);
                    this.proximityButton.setAlpha(0.0f);
                    this.proximityButton.setScaleX(0.4f);
                    this.proximityButton.setScaleY(0.4f);
                }
                this.proximityButton.setImageResource(NUM);
            } else {
                this.proximityButton.setImageResource(NUM);
            }
        }
        HintView hintView2 = new HintView(context2, 6, true);
        this.hintView = hintView2;
        hintView2.setVisibility(4);
        this.hintView.setShowingDuration(4000);
        this.mapViewClip.addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(1);
        this.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        this.emptyView.setVisibility(8);
        FrameLayout frameLayout3 = frameLayout2;
        frameLayout3.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(LocationActivity$$ExternalSyntheticLambda37.INSTANCE);
        ImageView imageView2 = new ImageView(context2);
        this.emptyImageView = imageView2;
        imageView2.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context2);
        this.emptyTitleTextView = textView;
        textView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setText(LocaleController.getString("NoPlacesFound", NUM));
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context2);
        this.emptySubtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        String str = "location_actionIcon";
        AnonymousClass9 r14 = r0;
        TLRPC.Chat chat2 = chat;
        Drawable drawable6 = drawable;
        FrameLayout.LayoutParams layoutParams3 = layoutParams2;
        ActionBarMenu actionBarMenu = menu;
        AnonymousClass9 r02 = new LocationActivityAdapter(context, this.locationType, this.dialogId, false, (Theme.ResourcesProvider) null) {
            /* access modifiers changed from: protected */
            public void onDirectionClick() {
                Intent intent;
                Activity activity;
                if (Build.VERSION.SDK_INT >= 23 && (activity = LocationActivity.this.getParentActivity()) != null && activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    LocationActivity.this.showPermissionAlert(true);
                } else if (LocationActivity.this.myLocation != null) {
                    try {
                        if (LocationActivity.this.messageObject != null) {
                            intent = new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[]{Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo.lat), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo._long)})));
                        } else {
                            intent = new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[]{Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.chatLocation.geo_point.lat), Double.valueOf(LocationActivity.this.chatLocation.geo_point._long)})));
                        }
                        LocationActivity.this.getParentActivity().startActivity(intent);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        };
        this.adapter = r14;
        recyclerListView.setAdapter(r14);
        this.adapter.setMyLocationDenied(this.locationDenied);
        this.adapter.setUpdateRunnable(new LocationActivity$$ExternalSyntheticLambda6(this));
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        FrameLayout frameLayout4 = frameLayout3;
        frameLayout4.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean unused = LocationActivity.this.scrolling = newState != 0;
                if (!LocationActivity.this.scrolling && LocationActivity.this.forceUpdate != null) {
                    CameraUpdate unused2 = LocationActivity.this.forceUpdate = null;
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LocationActivity.this.updateClipView(false);
                if (LocationActivity.this.forceUpdate != null) {
                    LocationActivity.access$3316(LocationActivity.this, (float) dy);
                }
            }
        });
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LocationActivity$$ExternalSyntheticLambda30(this));
        this.adapter.setDelegate(this.dialogId, new LocationActivity$$ExternalSyntheticLambda24(this));
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout4.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        this.mapView = new MapView(context2) {
            public boolean onTouchEvent(MotionEvent event) {
                return super.onTouchEvent(event);
            }

            public boolean dispatchTouchEvent(MotionEvent ev) {
                MotionEvent eventToRecycle = null;
                if (LocationActivity.this.yOffset != 0.0f) {
                    MotionEvent obtain = MotionEvent.obtain(ev);
                    eventToRecycle = obtain;
                    ev = obtain;
                    eventToRecycle.offsetLocation(0.0f, (-LocationActivity.this.yOffset) / 2.0f);
                }
                boolean result = super.dispatchTouchEvent(ev);
                if (eventToRecycle != null) {
                    eventToRecycle.recycle();
                }
                return result;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (LocationActivity.this.messageObject == null && LocationActivity.this.chatLocation == null) {
                    if (ev.getAction() == 0) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        AnimatorSet unused = LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        LocationActivity.this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) (LocationActivity.this.markerTop - AndroidUtilities.dp(10.0f))})});
                        LocationActivity.this.animatorSet.start();
                    } else if (ev.getAction() == 1) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        float unused2 = LocationActivity.this.yOffset = 0.0f;
                        AnimatorSet unused3 = LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        LocationActivity.this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) LocationActivity.this.markerTop})});
                        LocationActivity.this.animatorSet.start();
                        LocationActivity.this.adapter.fetchLocationAddress();
                    }
                    if (ev.getAction() == 2) {
                        if (!LocationActivity.this.userLocationMoved) {
                            LocationActivity.this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
                            LocationActivity.this.locationButton.setTag("location_actionIcon");
                            boolean unused4 = LocationActivity.this.userLocationMoved = true;
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

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                AndroidUtilities.runOnUIThread(new LocationActivity$11$$ExternalSyntheticLambda0(this));
            }

            /* renamed from: lambda$onLayout$0$org-telegram-ui-LocationActivity$11  reason: not valid java name */
            public /* synthetic */ void m2441lambda$onLayout$0$orgtelegramuiLocationActivity$11() {
                if (LocationActivity.this.moveToBounds != null) {
                    LocationActivity.this.googleMap.moveCamera(LocationActivity.this.moveToBounds);
                    CameraUpdate unused = LocationActivity.this.moveToBounds = null;
                }
            }
        };
        new Thread(new LocationActivity$$ExternalSyntheticLambda13(this, this.mapView)).start();
        MessageObject messageObject4 = this.messageObject;
        if (messageObject4 == null && this.chatLocation == null) {
            TLRPC.Chat chat3 = chat2;
            if (!(chat3 == null || this.locationType != 4 || this.dialogId == 0)) {
                FrameLayout frameLayout1 = new FrameLayout(context2);
                frameLayout1.setBackgroundResource(NUM);
                this.mapViewClip.addView(frameLayout1, LayoutHelper.createFrame(62, 76, 49));
                BackupImageView backupImageView = new BackupImageView(context2);
                backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
                backupImageView.setForUserOrChat(chat3, new AvatarDrawable(chat3));
                frameLayout1.addView(backupImageView, LayoutHelper.createFrame(52, 52.0f, 51, 5.0f, 5.0f, 0.0f, 0.0f));
                this.markerImageView = frameLayout1;
                frameLayout1.setTag(1);
            }
            if (this.markerImageView == null) {
                ImageView imageView3 = new ImageView(context2);
                imageView3.setImageResource(NUM);
                this.mapViewClip.addView(imageView3, LayoutHelper.createFrame(28, 48, 49));
                this.markerImageView = imageView3;
            }
            RecyclerListView recyclerListView3 = new RecyclerListView(context2);
            this.searchListView = recyclerListView3;
            recyclerListView3.setVisibility(8);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            AnonymousClass12 r1 = new LocationActivitySearchAdapter(context2) {
                public void notifyDataSetChanged() {
                    if (LocationActivity.this.searchItem != null) {
                        LocationActivity.this.searchItem.setShowSearchProgress(LocationActivity.this.searchAdapter.isSearching());
                    }
                    if (LocationActivity.this.emptySubtitleTextView != null) {
                        LocationActivity.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoPlacesFoundInfo", NUM, LocationActivity.this.searchAdapter.getLastSearchString())));
                    }
                    super.notifyDataSetChanged();
                }
            };
            this.searchAdapter = r1;
            r1.setDelegate(0, new LocationActivity$$ExternalSyntheticLambda23(this));
            frameLayout4.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LocationActivity$$ExternalSyntheticLambda31(this));
        } else {
            if ((messageObject4 != null && !messageObject4.isLiveLocation()) || this.chatLocation != null) {
                TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
                if (tL_channelLocation != null) {
                    this.adapter.setChatLocation(tL_channelLocation);
                } else {
                    MessageObject messageObject5 = this.messageObject;
                    if (messageObject5 != null) {
                        this.adapter.setMessageObject(messageObject5);
                    }
                }
            }
        }
        MessageObject messageObject6 = this.messageObject;
        if (messageObject6 != null && this.locationType == 6) {
            this.adapter.setMessageObject(messageObject6);
        }
        for (int a = 0; a < 2; a++) {
            this.undoView[a] = new UndoView(context2);
            this.undoView[a].setAdditionalTranslationY((float) AndroidUtilities.dp(10.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.undoView[a].setTranslationZ((float) AndroidUtilities.dp(5.0f));
            }
            this.mapViewClip.addView(this.undoView[a], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        this.shadow = new View(context2) {
            private RectF rect = new RectF();

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                LocationActivity.this.shadowDrawable.setBounds(-padding.left, 0, getMeasuredWidth() + padding.right, getMeasuredHeight());
                LocationActivity.this.shadowDrawable.draw(canvas);
                if (LocationActivity.this.locationType == 0 || LocationActivity.this.locationType == 1) {
                    int w = AndroidUtilities.dp(36.0f);
                    int y = padding.top + AndroidUtilities.dp(10.0f);
                    this.rect.set((float) ((getMeasuredWidth() - w) / 2), (float) y, (float) ((getMeasuredWidth() + w) / 2), (float) (AndroidUtilities.dp(4.0f) + y));
                    int color = Theme.getColor("key_sheet_scrollUp");
                    int alpha = Color.alpha(color);
                    Theme.dialogs_onlineCirclePaint.setColor(color);
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
            }
        };
        if (Build.VERSION.SDK_INT >= 21) {
            this.shadow.setTranslationZ((float) AndroidUtilities.dp(6.0f));
        }
        this.mapViewClip.addView(this.shadow, layoutParams3);
        if (this.messageObject == null && this.chatLocation == null && this.initialLocation != null) {
            this.userLocationMoved = true;
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag(str);
        }
        frameLayout4.addView(this.actionBar);
        updateEmptyView();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2405lambda$createView$0$orgtelegramuiLocationActivity(View v) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery((String) null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2406lambda$createView$1$orgtelegramuiLocationActivity(View v) {
        this.mapTypeButton.toggleSubMenu();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2417lambda$createView$2$orgtelegramuiLocationActivity(int id) {
        GoogleMap googleMap2 = this.googleMap;
        if (googleMap2 != null) {
            if (id == 2) {
                googleMap2.setMapType(1);
            } else if (id == 3) {
                googleMap2.setMapType(2);
            } else if (id == 4) {
                googleMap2.setMapType(4);
            }
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2419lambda$createView$3$orgtelegramuiLocationActivity(View v) {
        GoogleMap googleMap2;
        Activity activity;
        if (Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null && activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            showPermissionAlert(false);
        } else if (checkGpsEnabled()) {
            if (this.messageObject == null && this.chatLocation == null) {
                if (!(this.myLocation == null || this.googleMap == null)) {
                    this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
                    this.locationButton.setTag("location_actionActiveIcon");
                    this.adapter.setCustomLocation((Location) null);
                    this.userLocationMoved = false;
                    showSearchPlacesButton(false);
                    this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude())));
                    if (this.searchedForCustomLocations) {
                        Location location = this.myLocation;
                        if (location != null) {
                            this.adapter.searchPlacesWithQuery((String) null, location, true, true);
                        }
                        this.searchedForCustomLocations = false;
                        showResults();
                    }
                }
            } else if (!(this.myLocation == null || (googleMap2 = this.googleMap) == null)) {
                googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
            removeInfoView();
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2422lambda$createView$6$orgtelegramuiLocationActivity(View v) {
        if (getParentActivity() != null && this.myLocation != null && checkGpsEnabled() && this.googleMap != null) {
            HintView hintView2 = this.hintView;
            if (hintView2 != null) {
                hintView2.hide();
            }
            MessagesController.getGlobalMainSettings().edit().putInt("proximityhint", 3).commit();
            LocationController.SharingLocationInfo info = getLocationController().getSharingLocationInfo(this.dialogId);
            if (this.canUndo) {
                this.undoView[0].hide(true, 1);
            }
            if (info == null || info.proximityMeters <= 0) {
                openProximityAlert();
                return;
            }
            this.proximityButton.setImageResource(NUM);
            Circle circle = this.proximityCircle;
            if (circle != null) {
                circle.remove();
                this.proximityCircle = null;
            }
            this.canUndo = true;
            getUndoView().showWithAction(0, 25, (Object) 0, (Object) null, (Runnable) new LocationActivity$$ExternalSyntheticLambda5(this), (Runnable) new LocationActivity$$ExternalSyntheticLambda14(this, info));
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2420lambda$createView$4$orgtelegramuiLocationActivity() {
        getLocationController().setProximityLocation(this.dialogId, 0, true);
        this.canUndo = false;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2421lambda$createView$5$orgtelegramuiLocationActivity(LocationController.SharingLocationInfo info) {
        this.proximityButton.setImageResource(NUM);
        createCircle(info.proximityMeters);
        this.canUndo = false;
    }

    static /* synthetic */ boolean lambda$createView$7(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2423lambda$createView$8$orgtelegramuiLocationActivity() {
        updateClipView(false);
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2411lambda$createView$14$orgtelegramuiLocationActivity(View view, int position) {
        MessageObject messageObject2;
        TLRPC.TL_messageMediaVenue venue;
        int i = this.locationType;
        if (i == 4) {
            if (position == 1 && (venue = (TLRPC.TL_messageMediaVenue) this.adapter.getItem(position)) != null) {
                if (this.dialogId == 0) {
                    this.delegate.didSelectLocation(venue, 4, true, 0);
                    finishFragment();
                    return;
                }
                AlertDialog[] progressDialog = {new AlertDialog(getParentActivity(), 3)};
                TLRPC.TL_channels_editLocation req = new TLRPC.TL_channels_editLocation();
                req.address = venue.address;
                req.channel = getMessagesController().getInputChannel(-this.dialogId);
                req.geo_point = new TLRPC.TL_inputGeoPoint();
                req.geo_point.lat = venue.geo.lat;
                req.geo_point._long = venue.geo._long;
                progressDialog[0].setOnCancelListener(new LocationActivity$$ExternalSyntheticLambda0(this, getConnectionsManager().sendRequest(req, new LocationActivity$$ExternalSyntheticLambda19(this, progressDialog, venue))));
                showDialog(progressDialog[0]);
            }
        } else if (i == 5) {
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.chatLocation.geo_point.lat, this.chatLocation.geo_point._long), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (position == 1 && (messageObject2 = this.messageObject) != null && (!messageObject2.isLiveLocation() || this.locationType == 6)) {
            GoogleMap googleMap3 = this.googleMap;
            if (googleMap3 != null) {
                googleMap3.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.messageObject.messageOwner.media.geo.lat, this.messageObject.messageOwner.media.geo._long), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (position != 1 || this.locationType == 2) {
            if ((position != 2 || this.locationType != 1) && ((position != 1 || this.locationType != 2) && (position != 3 || this.locationType != 3))) {
                Object object = this.adapter.getItem(position);
                if (object instanceof TLRPC.TL_messageMediaVenue) {
                    ChatActivity chatActivity = this.parentFragment;
                    if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                        this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, true, 0);
                        finishFragment();
                        return;
                    }
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new LocationActivity$$ExternalSyntheticLambda25(this, object));
                } else if (object instanceof LiveLocation) {
                    this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation) object).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            } else if (getLocationController().isSharingLocation(this.dialogId)) {
                getLocationController().removeSharingLocation(this.dialogId);
                finishFragment();
            } else {
                openShareLiveLocation(0);
            }
        } else if (this.delegate != null && this.userLocation != null) {
            FrameLayout frameLayout = this.lastPressedMarkerView;
            if (frameLayout != null) {
                frameLayout.callOnClick();
                return;
            }
            TLRPC.TL_messageMediaGeo location = new TLRPC.TL_messageMediaGeo();
            location.geo = new TLRPC.TL_geoPoint();
            location.geo.lat = AndroidUtilities.fixLocationCoord(this.userLocation.getLatitude());
            location.geo._long = AndroidUtilities.fixLocationCoord(this.userLocation.getLongitude());
            ChatActivity chatActivity2 = this.parentFragment;
            if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
                this.delegate.didSelectLocation(location, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new LocationActivity$$ExternalSyntheticLambda26(this, location));
        }
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2407lambda$createView$10$orgtelegramuiLocationActivity(AlertDialog[] progressDialog, TLRPC.TL_messageMediaVenue venue, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LocationActivity$$ExternalSyntheticLambda16(this, progressDialog, venue));
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2424lambda$createView$9$orgtelegramuiLocationActivity(AlertDialog[] progressDialog, TLRPC.TL_messageMediaVenue venue) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        this.delegate.didSelectLocation(venue, 4, true, 0);
        finishFragment();
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2408lambda$createView$11$orgtelegramuiLocationActivity(int requestId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(requestId, true);
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2409lambda$createView$12$orgtelegramuiLocationActivity(TLRPC.TL_messageMediaGeo location, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(location, this.locationType, notify, scheduleDate);
        finishFragment();
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2410lambda$createView$13$orgtelegramuiLocationActivity(Object object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, notify, scheduleDate);
        finishFragment();
    }

    /* renamed from: lambda$createView$17$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2414lambda$createView$17$orgtelegramuiLocationActivity(MapView map) {
        try {
            map.onCreate((Bundle) null);
        } catch (Exception e) {
        }
        AndroidUtilities.runOnUIThread(new LocationActivity$$ExternalSyntheticLambda12(this, map));
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2413lambda$createView$16$orgtelegramuiLocationActivity(MapView map) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                map.onCreate((Bundle) null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new LocationActivity$$ExternalSyntheticLambda4(this));
                this.mapsInitialized = true;
                if (this.onResumeCalled) {
                    this.mapView.onResume();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2412lambda$createView$15$orgtelegramuiLocationActivity(GoogleMap map1) {
        this.googleMap = map1;
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, NUM));
        }
        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        onMapInit();
    }

    /* renamed from: lambda$createView$18$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2415lambda$createView$18$orgtelegramuiLocationActivity(ArrayList places) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2418lambda$createView$20$orgtelegramuiLocationActivity(View view, int position) {
        TLRPC.TL_messageMediaVenue object = this.searchAdapter.getItem(position);
        if (object != null && this.delegate != null) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                this.delegate.didSelectLocation(object, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new LocationActivity$$ExternalSyntheticLambda27(this, object));
        }
    }

    /* renamed from: lambda$createView$19$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2416lambda$createView$19$orgtelegramuiLocationActivity(TLRPC.TL_messageMediaVenue object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(object, this.locationType, notify, scheduleDate);
        finishFragment();
    }

    private boolean isActiveThemeDark() {
        if (!Theme.getActiveTheme().isDark() && AndroidUtilities.computePerceivedBrightness(Theme.getColor("windowBackgroundWhite")) >= 0.721f) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        if (!this.searching) {
            this.emptyView.setVisibility(8);
        } else if (this.searchInProgress) {
            this.searchListView.setEmptyView((View) null);
            this.emptyView.setVisibility(8);
            this.searchListView.setVisibility(8);
        } else {
            this.searchListView.setEmptyView(this.emptyView);
        }
    }

    /* access modifiers changed from: private */
    public void showSearchPlacesButton(boolean show) {
        SearchButton searchButton;
        Location location;
        Location location2;
        if (show && (searchButton = this.searchAreaButton) != null && searchButton.getTag() == null && ((location = this.myLocation) == null || (location2 = this.userLocation) == null || location2.distanceTo(location) < 300.0f)) {
            show = false;
        }
        SearchButton searchButton2 = this.searchAreaButton;
        if (searchButton2 == null) {
            return;
        }
        if (show && searchButton2.getTag() != null) {
            return;
        }
        if (show || this.searchAreaButton.getTag() != null) {
            this.searchAreaButton.setTag(show ? 1 : null);
            AnimatorSet animatorSet2 = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            SearchButton searchButton3 = this.searchAreaButton;
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            fArr[0] = show ? 0.0f : (float) (-AndroidUtilities.dp(80.0f));
            animatorArr[0] = ObjectAnimator.ofFloat(searchButton3, property, fArr);
            animatorSet2.playTogether(animatorArr);
            animatorSet2.setDuration(180);
            animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet2.start();
        }
    }

    private Bitmap createUserBitmap(LiveLocation liveLocation) {
        TLRPC.FileLocation photo;
        LiveLocation liveLocation2 = liveLocation;
        Bitmap result = null;
        try {
            if (liveLocation2.user != null && liveLocation2.user.photo != null) {
                photo = liveLocation2.user.photo.photo_small;
            } else if (liveLocation2.chat == null || liveLocation2.chat.photo == null) {
                photo = null;
            } else {
                photo = liveLocation2.chat.photo.photo_small;
            }
            result = Bitmap.createBitmap(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(85.0f), Bitmap.Config.ARGB_8888);
            result.eraseColor(0);
            Canvas canvas = new Canvas(result);
            Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(NUM);
            drawable.setBounds(0, 0, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(85.0f));
            drawable.draw(canvas);
            Paint roundPaint = new Paint(1);
            RectF bitmapRect = new RectF();
            canvas.save();
            if (photo != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                if (bitmap != null) {
                    BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Matrix matrix = new Matrix();
                    float scale = ((float) AndroidUtilities.dp(50.0f)) / ((float) bitmap.getWidth());
                    matrix.postTranslate((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f));
                    matrix.postScale(scale, scale);
                    roundPaint.setShader(shader);
                    shader.setLocalMatrix(matrix);
                    bitmapRect.set((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(56.0f), (float) AndroidUtilities.dp(56.0f));
                    canvas.drawRoundRect(bitmapRect, (float) AndroidUtilities.dp(25.0f), (float) AndroidUtilities.dp(25.0f), roundPaint);
                }
            } else {
                AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                if (liveLocation2.user != null) {
                    avatarDrawable2.setInfo(liveLocation2.user);
                } else if (liveLocation2.chat != null) {
                    avatarDrawable2.setInfo(liveLocation2.chat);
                }
                canvas.translate((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f));
                avatarDrawable2.setBounds(0, 0, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f));
                avatarDrawable2.draw(canvas);
            }
            canvas.restore();
            try {
                canvas.setBitmap((Bitmap) null);
            } catch (Exception e) {
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        return result;
    }

    private long getMessageId(TLRPC.Message message) {
        if (message.from_id != null) {
            return MessageObject.getFromChatId(message);
        }
        return MessageObject.getDialogId(message);
    }

    private void openProximityAlert() {
        TLRPC.User user;
        Circle circle = this.proximityCircle;
        if (circle == null) {
            createCircle(500);
        } else {
            this.previousRadius = circle.getRadius();
        }
        if (DialogObject.isUserDialog(this.dialogId)) {
            user = getMessagesController().getUser(Long.valueOf(this.dialogId));
        } else {
            user = null;
        }
        this.proximitySheet = new ProximitySheet(getParentActivity(), user, new LocationActivity$$ExternalSyntheticLambda28(this), new LocationActivity$$ExternalSyntheticLambda29(this, user), new LocationActivity$$ExternalSyntheticLambda8(this));
        ((FrameLayout) this.fragmentView).addView(this.proximitySheet, LayoutHelper.createFrame(-1, -1.0f));
        this.proximitySheet.show();
    }

    /* renamed from: lambda$openProximityAlert$21$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ boolean m2434lambda$openProximityAlert$21$orgtelegramuiLocationActivity(boolean move, int radius) {
        Circle circle = this.proximityCircle;
        if (circle != null) {
            circle.setRadius((double) radius);
            if (move) {
                moveToBounds(radius, true, true);
            }
        }
        if (DialogObject.isChatDialog(this.dialogId)) {
            return true;
        }
        int N = this.markers.size();
        for (int a = 0; a < N; a++) {
            LiveLocation location = this.markers.get(a);
            if (location.object != null && !UserObject.isUserSelf(location.user)) {
                TLRPC.GeoPoint point = location.object.media.geo;
                Location loc = new Location("network");
                loc.setLatitude(point.lat);
                loc.setLongitude(point._long);
                if (this.myLocation.distanceTo(loc) > ((float) radius)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* renamed from: lambda$openProximityAlert$23$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ boolean m2436lambda$openProximityAlert$23$orgtelegramuiLocationActivity(TLRPC.User user, boolean move, int radius) {
        if (getLocationController().getSharingLocationInfo(this.dialogId) == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("ShareLocationAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("ShareLocationAlertText", NUM));
            builder.setPositiveButton(LocaleController.getString("ShareLocationAlertButton", NUM), new LocationActivity$$ExternalSyntheticLambda32(this, user, radius));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
            return false;
        }
        this.proximitySheet.setRadiusSet();
        this.proximityButton.setImageResource(NUM);
        getUndoView().showWithAction(0, 24, (Object) Integer.valueOf(radius), (Object) user, (Runnable) null, (Runnable) null);
        getLocationController().setProximityLocation(this.dialogId, radius, true);
        return true;
    }

    /* renamed from: lambda$openProximityAlert$22$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2435lambda$openProximityAlert$22$orgtelegramuiLocationActivity(TLRPC.User user, int radius, DialogInterface dialog, int id) {
        m2439lambda$openShareLiveLocation$26$orgtelegramuiLocationActivity(user, 900, radius);
    }

    /* renamed from: lambda$openProximityAlert$24$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2437lambda$openProximityAlert$24$orgtelegramuiLocationActivity() {
        GoogleMap googleMap2 = this.googleMap;
        if (googleMap2 != null) {
            googleMap2.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        }
        if (!this.proximitySheet.getRadiusSet()) {
            double d = this.previousRadius;
            if (d > 0.0d) {
                this.proximityCircle.setRadius(d);
            } else {
                Circle circle = this.proximityCircle;
                if (circle != null) {
                    circle.remove();
                    this.proximityCircle = null;
                }
            }
        }
        this.proximitySheet = null;
    }

    /* access modifiers changed from: private */
    public void openShareLiveLocation(int proximityRadius) {
        TLRPC.User user;
        Activity activity;
        if (this.delegate != null && getParentActivity() != null && this.myLocation != null && checkGpsEnabled()) {
            if (this.checkBackgroundPermission && Build.VERSION.SDK_INT >= 29 && (activity = getParentActivity()) != null) {
                this.askWithRadius = proximityRadius;
                this.checkBackgroundPermission = false;
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (Math.abs((System.currentTimeMillis() / 1000) - ((long) preferences.getInt("backgroundloc", 0))) > 86400 && activity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
                    preferences.edit().putInt("backgroundloc", (int) (System.currentTimeMillis() / 1000)).commit();
                    AlertsCreator.createBackgroundLocationPermissionDialog(activity, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), new LocationActivity$$ExternalSyntheticLambda9(this), (Theme.ResourcesProvider) null).show();
                    return;
                }
            }
            if (DialogObject.isUserDialog(this.dialogId)) {
                user = getMessagesController().getUser(Long.valueOf(this.dialogId));
            } else {
                user = null;
            }
            showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new LocationActivity$$ExternalSyntheticLambda17(this, user, proximityRadius), (Theme.ResourcesProvider) null));
        }
    }

    /* renamed from: lambda$openShareLiveLocation$25$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2438lambda$openShareLiveLocation$25$orgtelegramuiLocationActivity() {
        openShareLiveLocation(this.askWithRadius);
    }

    /* access modifiers changed from: private */
    /* renamed from: shareLiveLocation */
    public void m2439lambda$openShareLiveLocation$26$orgtelegramuiLocationActivity(TLRPC.User user, int period, int radius) {
        TLRPC.TL_messageMediaGeoLive location = new TLRPC.TL_messageMediaGeoLive();
        location.geo = new TLRPC.TL_geoPoint();
        location.geo.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        location.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        location.heading = LocationController.getHeading(this.myLocation);
        location.flags |= 1;
        location.period = period;
        location.proximity_notification_radius = radius;
        location.flags |= 8;
        this.delegate.didSelectLocation(location, this.locationType, true, 0);
        if (radius > 0) {
            this.proximitySheet.setRadiusSet();
            this.proximityButton.setImageResource(NUM);
            ProximitySheet proximitySheet2 = this.proximitySheet;
            if (proximitySheet2 != null) {
                proximitySheet2.dismiss();
            }
            getUndoView().showWithAction(0, 24, (Object) Integer.valueOf(radius), (Object) user, (Runnable) null, (Runnable) null);
            return;
        }
        finishFragment();
    }

    private Bitmap createPlaceBitmap(int num) {
        Bitmap[] bitmapArr = this.bitmapCache;
        if (bitmapArr[num % 7] != null) {
            return bitmapArr[num % 7];
        }
        try {
            Paint paint = new Paint(1);
            paint.setColor(-1);
            Bitmap bitmap = Bitmap.createBitmap(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawCircle((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), paint);
            paint.setColor(LocationCell.getColorForIndex(num));
            canvas.drawCircle((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(5.0f), paint);
            canvas.setBitmap((Bitmap) null);
            this.bitmapCache[num % 7] = bitmap;
            return bitmap;
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void updatePlacesMarkers(ArrayList<TLRPC.TL_messageMediaVenue> places) {
        if (places != null) {
            int N = this.placeMarkers.size();
            for (int a = 0; a < N; a++) {
                this.placeMarkers.get(a).marker.remove();
            }
            this.placeMarkers.clear();
            int N2 = places.size();
            for (int a2 = 0; a2 < N2; a2++) {
                TLRPC.TL_messageMediaVenue venue = places.get(a2);
                try {
                    MarkerOptions options = new MarkerOptions().position(new LatLng(venue.geo.lat, venue.geo._long));
                    options.icon(BitmapDescriptorFactory.fromBitmap(createPlaceBitmap(a2)));
                    options.anchor(0.5f, 0.5f);
                    options.title(venue.title);
                    options.snippet(venue.address);
                    VenueLocation venueLocation = new VenueLocation();
                    venueLocation.num = a2;
                    venueLocation.marker = this.googleMap.addMarker(options);
                    venueLocation.venue = venue;
                    venueLocation.marker.setTag(venueLocation);
                    this.placeMarkers.add(venueLocation);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    private LiveLocation addUserMarker(TLRPC.Message message) {
        LatLng latLng = new LatLng(message.media.geo.lat, message.media.geo._long);
        LiveLocation liveLocation = this.markersMap.get(MessageObject.getFromChatId(message));
        LiveLocation liveLocation2 = liveLocation;
        if (liveLocation == null) {
            liveLocation2 = new LiveLocation();
            liveLocation2.object = message;
            if (liveLocation2.object.from_id instanceof TLRPC.TL_peerUser) {
                liveLocation2.user = getMessagesController().getUser(Long.valueOf(liveLocation2.object.from_id.user_id));
                liveLocation2.id = liveLocation2.object.from_id.user_id;
            } else {
                long did = MessageObject.getDialogId(message);
                if (DialogObject.isUserDialog(did)) {
                    liveLocation2.user = getMessagesController().getUser(Long.valueOf(did));
                } else {
                    liveLocation2.chat = getMessagesController().getChat(Long.valueOf(-did));
                }
                liveLocation2.id = did;
            }
            try {
                MarkerOptions options = new MarkerOptions().position(latLng);
                Bitmap bitmap = createUserBitmap(liveLocation2);
                if (bitmap != null) {
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    options.anchor(0.5f, 0.907f);
                    liveLocation2.marker = this.googleMap.addMarker(options);
                    if (!UserObject.isUserSelf(liveLocation2.user)) {
                        MarkerOptions dirOptions = new MarkerOptions().position(latLng).flat(true);
                        dirOptions.anchor(0.5f, 0.5f);
                        liveLocation2.directionMarker = this.googleMap.addMarker(dirOptions);
                        if (message.media.heading != 0) {
                            liveLocation2.directionMarker.setRotation((float) message.media.heading);
                            liveLocation2.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(NUM));
                            liveLocation2.hasRotation = true;
                        } else {
                            liveLocation2.directionMarker.setRotation(0.0f);
                            liveLocation2.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(NUM));
                            liveLocation2.hasRotation = false;
                        }
                    }
                    this.markers.add(liveLocation2);
                    this.markersMap.put(liveLocation2.id, liveLocation2);
                    LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(this.dialogId);
                    if (liveLocation2.id == getUserConfig().getClientUserId() && myInfo != null && liveLocation2.object.id == myInfo.mid && this.myLocation != null) {
                        liveLocation2.marker.setPosition(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            liveLocation2.object = message;
            liveLocation2.marker.setPosition(latLng);
        }
        ProximitySheet proximitySheet2 = this.proximitySheet;
        if (proximitySheet2 != null) {
            proximitySheet2.updateText(true, true);
        }
        return liveLocation2;
    }

    private LiveLocation addUserMarker(TLRPC.TL_channelLocation location) {
        LatLng latLng = new LatLng(location.geo_point.lat, location.geo_point._long);
        LiveLocation liveLocation = new LiveLocation();
        if (DialogObject.isUserDialog(this.dialogId)) {
            liveLocation.user = getMessagesController().getUser(Long.valueOf(this.dialogId));
        } else {
            liveLocation.chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
        }
        liveLocation.id = this.dialogId;
        try {
            MarkerOptions options = new MarkerOptions().position(latLng);
            Bitmap bitmap = createUserBitmap(liveLocation);
            if (bitmap != null) {
                options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                options.anchor(0.5f, 0.907f);
                liveLocation.marker = this.googleMap.addMarker(options);
                if (!UserObject.isUserSelf(liveLocation.user)) {
                    MarkerOptions dirOptions = new MarkerOptions().position(latLng).flat(true);
                    dirOptions.icon(BitmapDescriptorFactory.fromResource(NUM));
                    dirOptions.anchor(0.5f, 0.5f);
                    liveLocation.directionMarker = this.googleMap.addMarker(dirOptions);
                }
                this.markers.add(liveLocation);
                this.markersMap.put(liveLocation.id, liveLocation);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return liveLocation;
    }

    private void onMapInit() {
        LocationController.SharingLocationInfo myInfo;
        if (this.googleMap != null) {
            TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
            if (tL_channelLocation != null) {
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker(tL_channelLocation).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
            } else {
                MessageObject messageObject2 = this.messageObject;
                if (messageObject2 == null) {
                    Location location = new Location("network");
                    this.userLocation = location;
                    if (this.initialLocation != null) {
                        LatLng latLng = new LatLng(this.initialLocation.geo_point.lat, this.initialLocation.geo_point._long);
                        GoogleMap googleMap2 = this.googleMap;
                        googleMap2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, googleMap2.getMaxZoomLevel() - 4.0f));
                        this.userLocation.setLatitude(this.initialLocation.geo_point.lat);
                        this.userLocation.setLongitude(this.initialLocation.geo_point._long);
                        this.adapter.setCustomLocation(this.userLocation);
                    } else {
                        location.setLatitude(20.659322d);
                        this.userLocation.setLongitude(-11.40625d);
                    }
                } else if (messageObject2.isLiveLocation()) {
                    LiveLocation liveLocation = addUserMarker(this.messageObject.messageOwner);
                    if (!getRecentLocations()) {
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(liveLocation.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else {
                    LatLng latLng2 = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                    try {
                        this.googleMap.addMarker(new MarkerOptions().position(latLng2).icon(BitmapDescriptorFactory.fromResource(NUM)));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, this.googleMap.getMaxZoomLevel() - 4.0f));
                    this.firstFocus = false;
                    getRecentLocations();
                }
            }
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2, false);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnCameraMoveStartedListener(new LocationActivity$$ExternalSyntheticLambda1(this));
            this.googleMap.setOnMyLocationChangeListener(new LocationActivity$$ExternalSyntheticLambda3(this));
            this.googleMap.setOnMarkerClickListener(new LocationActivity$$ExternalSyntheticLambda2(this));
            this.googleMap.setOnCameraMoveListener(new LocationActivity$$ExternalSyntheticLambda38(this));
            Location lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
            if (this.checkGpsEnabled && getParentActivity() != null) {
                this.checkGpsEnabled = false;
                checkGpsEnabled();
            }
            ImageView imageView = this.proximityButton;
            if (imageView != null && imageView.getVisibility() == 0 && (myInfo = getLocationController().getSharingLocationInfo(this.dialogId)) != null && myInfo.proximityMeters > 0) {
                createCircle(myInfo.proximityMeters);
            }
        }
    }

    /* renamed from: lambda$onMapInit$27$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2430lambda$onMapInit$27$orgtelegramuiLocationActivity(int reason) {
        View view;
        RecyclerView.ViewHolder holder;
        if (reason == 1) {
            showSearchPlacesButton(true);
            removeInfoView();
            if (!this.scrolling) {
                int i = this.locationType;
                if ((i == 0 || i == 1) && this.listView.getChildCount() > 0 && (view = this.listView.getChildAt(0)) != null && (holder = this.listView.findContainingViewHolder(view)) != null && holder.getAdapterPosition() == 0) {
                    int min = this.locationType == 0 ? 0 : AndroidUtilities.dp(66.0f);
                    int top = view.getTop();
                    if (top < (-min)) {
                        CameraPosition cameraPosition = this.googleMap.getCameraPosition();
                        this.forceUpdate = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom);
                        this.listView.smoothScrollBy(0, top + min);
                    }
                }
            }
        }
    }

    /* renamed from: lambda$onMapInit$28$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2431lambda$onMapInit$28$orgtelegramuiLocationActivity(Location location) {
        positionMarker(location);
        getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    /* renamed from: lambda$onMapInit$29$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ boolean m2432lambda$onMapInit$29$orgtelegramuiLocationActivity(Marker marker) {
        if (!(marker.getTag() instanceof VenueLocation)) {
            return true;
        }
        this.markerImageView.setVisibility(4);
        if (!this.userLocationMoved) {
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag("location_actionIcon");
            this.userLocationMoved = true;
        }
        this.overlayView.addInfoView(marker);
        return true;
    }

    /* renamed from: lambda$onMapInit$30$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2433lambda$onMapInit$30$orgtelegramuiLocationActivity() {
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null) {
            mapOverlayView.updatePositions();
        }
    }

    private boolean checkGpsEnabled() {
        if (!getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
            return true;
        }
        try {
            if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground"));
                builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new LocationActivity$$ExternalSyntheticLambda11(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
                return false;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return true;
    }

    /* renamed from: lambda$checkGpsEnabled$31$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2404lambda$checkGpsEnabled$31$orgtelegramuiLocationActivity(DialogInterface dialog, int id) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception e) {
            }
        }
    }

    private void createCircle(int meters) {
        if (this.googleMap != null) {
            List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(new PatternItem[]{new Gap(20.0f), new Dash(20.0f)});
            CircleOptions circleOptions2 = new CircleOptions();
            circleOptions2.center(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
            circleOptions2.radius((double) meters);
            if (isActiveThemeDark()) {
                circleOptions2.strokeColor(-NUM);
                circleOptions2.fillColor(NUM);
            } else {
                circleOptions2.strokeColor(-NUM);
                circleOptions2.fillColor(NUM);
            }
            circleOptions2.strokePattern(PATTERN_POLYGON_ALPHA);
            circleOptions2.strokeWidth(2.0f);
            this.proximityCircle = this.googleMap.addCircle(circleOptions2);
        }
    }

    private void removeInfoView() {
        if (this.lastPressedMarker != null) {
            this.markerImageView.setVisibility(0);
            this.overlayView.removeInfoView(this.lastPressedMarker);
            this.lastPressedMarker = null;
            this.lastPressedVenue = null;
            this.lastPressedMarkerView = null;
        }
    }

    /* access modifiers changed from: private */
    public void showPermissionAlert(boolean byButton) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground"));
            if (byButton) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationNavigation", NUM));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocationFriends", NUM));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new LocationActivity$$ExternalSyntheticLambda22(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$showPermissionAlert$32$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2440lambda$showPermissionAlert$32$orgtelegramuiLocationActivity(DialogInterface dialog, int which) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            try {
                if (this.mapView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mapView.getParent()).removeView(this.mapView);
                }
            } catch (Exception e) {
            }
            FrameLayout frameLayout = this.mapViewClip;
            if (frameLayout != null) {
                frameLayout.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                MapOverlayView mapOverlayView = this.overlayView;
                if (mapOverlayView != null) {
                    try {
                        if (mapOverlayView.getParent() instanceof ViewGroup) {
                            ((ViewGroup) this.overlayView.getParent()).removeView(this.overlayView);
                        }
                    } catch (Exception e2) {
                    }
                    this.mapViewClip.addView(this.overlayView, 1, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                }
                updateClipView(false);
                maybeShowProximityHint();
            } else if (this.fragmentView != null) {
                ((FrameLayout) this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000f, code lost:
        r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void maybeShowProximityHint() {
        /*
            r9 = this;
            android.widget.ImageView r0 = r9.proximityButton
            if (r0 == 0) goto L_0x006e
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x006e
            boolean r0 = r9.proximityAnimationInProgress
            if (r0 == 0) goto L_0x000f
            goto L_0x006e
        L_0x000f:
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "proximityhint"
            r2 = 0
            int r3 = r0.getInt(r1, r2)
            r4 = 3
            if (r3 >= r4) goto L_0x006d
            android.content.SharedPreferences$Editor r4 = r0.edit()
            int r3 = r3 + 1
            android.content.SharedPreferences$Editor r1 = r4.putInt(r1, r3)
            r1.commit()
            long r4 = r9.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            r4 = 1
            if (r1 == 0) goto L_0x0058
            org.telegram.messenger.MessagesController r1 = r9.getMessagesController()
            long r5 = r9.dialogId
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r5)
            org.telegram.ui.Components.HintView r5 = r9.hintView
            r6 = 2131627557(0x7f0e0e25, float:1.8882382E38)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r1)
            r7[r2] = r8
            java.lang.String r2 = "ProximityTooltioUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r7)
            r5.setOverrideText(r2)
            goto L_0x0066
        L_0x0058:
            org.telegram.ui.Components.HintView r1 = r9.hintView
            r2 = 2131627556(0x7f0e0e24, float:1.888238E38)
            java.lang.String r5 = "ProximityTooltioGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1.setOverrideText(r2)
        L_0x0066:
            org.telegram.ui.Components.HintView r1 = r9.hintView
            android.widget.ImageView r2 = r9.proximityButton
            r1.showForView(r2, r4)
        L_0x006d:
            return
        L_0x006e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.maybeShowProximityHint():void");
    }

    private void showResults() {
        if (this.adapter.getItemCount() != 0 && this.layoutManager.findFirstVisibleItemPosition() == 0) {
            int offset = AndroidUtilities.dp(258.0f) + this.listView.getChildAt(0).getTop();
            if (offset >= 0 && offset <= AndroidUtilities.dp(258.0f)) {
                this.listView.smoothScrollBy(0, offset);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateClipView(boolean fromLayout) {
        int top;
        FrameLayout.LayoutParams layoutParams;
        int height = 0;
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            top = (int) holder.itemView.getY();
            height = this.overScrollHeight + Math.min(top, 0);
        } else {
            top = -this.mapViewClip.getMeasuredHeight();
        }
        if (((FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
            if (height <= 0) {
                if (this.mapView.getVisibility() == 0) {
                    this.mapView.setVisibility(4);
                    this.mapViewClip.setVisibility(4);
                    MapOverlayView mapOverlayView = this.overlayView;
                    if (mapOverlayView != null) {
                        mapOverlayView.setVisibility(4);
                    }
                }
            } else if (this.mapView.getVisibility() == 4) {
                this.mapView.setVisibility(0);
                this.mapViewClip.setVisibility(0);
                MapOverlayView mapOverlayView2 = this.overlayView;
                if (mapOverlayView2 != null) {
                    mapOverlayView2.setVisibility(0);
                }
            }
            this.mapViewClip.setTranslationY((float) Math.min(0, top));
            this.mapView.setTranslationY((float) Math.max(0, (-top) / 2));
            MapOverlayView mapOverlayView3 = this.overlayView;
            if (mapOverlayView3 != null) {
                mapOverlayView3.setTranslationY((float) Math.max(0, (-top) / 2));
            }
            int measuredHeight = this.overScrollHeight - this.mapTypeButton.getMeasuredHeight();
            int i = this.locationType;
            float translationY = (float) Math.min(measuredHeight - AndroidUtilities.dp((float) (64 + ((i == 0 || i == 1) ? 30 : 10))), -top);
            this.mapTypeButton.setTranslationY(translationY);
            this.proximityButton.setTranslationY(translationY);
            HintView hintView2 = this.hintView;
            if (hintView2 != null) {
                hintView2.setExtraTranslationY(translationY);
            }
            SearchButton searchButton = this.searchAreaButton;
            if (searchButton != null) {
                searchButton.setTranslation(translationY);
            }
            View view = this.markerImageView;
            if (view != null) {
                int dp = ((-top) - AndroidUtilities.dp(view.getTag() == null ? 48.0f : 69.0f)) + (height / 2);
                this.markerTop = dp;
                view.setTranslationY((float) dp);
            }
            if (!fromLayout) {
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mapView.getLayoutParams();
                if (!(layoutParams2 == null || layoutParams2.height == this.overScrollHeight + AndroidUtilities.dp(10.0f))) {
                    layoutParams2.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    GoogleMap googleMap2 = this.googleMap;
                    if (googleMap2 != null) {
                        googleMap2.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams2);
                }
                MapOverlayView mapOverlayView4 = this.overlayView;
                if (mapOverlayView4 != null && (layoutParams = (FrameLayout.LayoutParams) mapOverlayView4.getLayoutParams()) != null && layoutParams.height != this.overScrollHeight + AndroidUtilities.dp(10.0f)) {
                    layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    this.overlayView.setLayoutParams(layoutParams);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal(boolean resume) {
        int top;
        FrameLayout.LayoutParams layoutParams;
        if (this.listView != null) {
            int height = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int viewHeight = this.fragmentView.getMeasuredHeight();
            if (viewHeight != 0) {
                int i = this.locationType;
                if (i == 6) {
                    this.overScrollHeight = (viewHeight - AndroidUtilities.dp(66.0f)) - height;
                } else if (i == 2) {
                    this.overScrollHeight = (viewHeight - AndroidUtilities.dp(73.0f)) - height;
                } else {
                    this.overScrollHeight = (viewHeight - AndroidUtilities.dp(66.0f)) - height;
                }
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
                layoutParams2.topMargin = height;
                this.listView.setLayoutParams(layoutParams2);
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams();
                layoutParams3.topMargin = height;
                layoutParams3.height = this.overScrollHeight;
                this.mapViewClip.setLayoutParams(layoutParams3);
                RecyclerListView recyclerListView = this.searchListView;
                if (recyclerListView != null) {
                    FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
                    layoutParams4.topMargin = height;
                    this.searchListView.setLayoutParams(layoutParams4);
                }
                this.adapter.setOverScrollHeight(this.overScrollHeight);
                FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.mapView.getLayoutParams();
                if (layoutParams5 != null) {
                    layoutParams5.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    GoogleMap googleMap2 = this.googleMap;
                    if (googleMap2 != null) {
                        googleMap2.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams5);
                }
                MapOverlayView mapOverlayView = this.overlayView;
                if (!(mapOverlayView == null || (layoutParams = (FrameLayout.LayoutParams) mapOverlayView.getLayoutParams()) == null)) {
                    layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    this.overlayView.setLayoutParams(layoutParams);
                }
                this.adapter.notifyDataSetChanged();
                if (resume) {
                    int i2 = this.locationType;
                    if (i2 == 3) {
                        top = 73;
                    } else if (i2 == 1 || i2 == 2) {
                        top = 66;
                    } else {
                        top = 0;
                    }
                    this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) top));
                    updateClipView(false);
                    this.listView.post(new LocationActivity$$ExternalSyntheticLambda10(this, top));
                    return;
                }
                updateClipView(false);
            }
        }
    }

    /* renamed from: lambda$fixLayoutInternal$33$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2425lambda$fixLayoutInternal$33$orgtelegramuiLocationActivity(int top) {
        this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) top));
        updateClipView(false);
    }

    private Location getLastLocation() {
        LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) {
                break;
            }
        }
        return l;
    }

    private void positionMarker(Location location) {
        if (location != null) {
            this.myLocation = new Location(location);
            LiveLocation liveLocation = this.markersMap.get(getUserConfig().getClientUserId());
            LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (!(liveLocation == null || myInfo == null || liveLocation.object.id != myInfo.mid)) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                liveLocation.marker.setPosition(latLng);
                if (liveLocation.directionMarker != null) {
                    liveLocation.directionMarker.setPosition(latLng);
                }
            }
            if (this.messageObject == null && this.chatLocation == null && this.googleMap != null) {
                LatLng latLng2 = new LatLng(location.getLatitude(), location.getLongitude());
                LocationActivityAdapter locationActivityAdapter = this.adapter;
                if (locationActivityAdapter != null) {
                    if (!this.searchedForCustomLocations && this.locationType != 4) {
                        locationActivityAdapter.searchPlacesWithQuery((String) null, this.myLocation, true);
                    }
                    this.adapter.setGpsLocation(this.myLocation);
                }
                if (!this.userLocationMoved) {
                    this.userLocation = new Location(location);
                    if (this.firstWas) {
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng2));
                    } else {
                        this.firstWas = true;
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                }
            } else {
                this.adapter.setGpsLocation(this.myLocation);
            }
            ProximitySheet proximitySheet2 = this.proximitySheet;
            if (proximitySheet2 != null) {
                proximitySheet2.updateText(true, true);
            }
            Circle circle = this.proximityCircle;
            if (circle != null) {
                circle.setCenter(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
            }
        }
    }

    public void setMessageObject(MessageObject message) {
        this.messageObject = message;
        this.dialogId = message.getDialogId();
    }

    public void setChatLocation(long chatId, TLRPC.TL_channelLocation location) {
        this.dialogId = -chatId;
        this.chatLocation = location;
    }

    public void setDialogId(long did) {
        this.dialogId = did;
    }

    public void setInitialLocation(TLRPC.TL_channelLocation location) {
        this.initialLocation = location;
    }

    private static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        return new LatLng(startLL.latitude + meterToLatitude(toNorth), startLL.longitude + lonDiff);
    }

    private static double meterToLongitude(double meterToEast, double latitude) {
        return Math.toDegrees(meterToEast / (Math.cos(Math.toRadians(latitude)) * 6366198.0d));
    }

    private static double meterToLatitude(double meterToNorth) {
        return Math.toDegrees(meterToNorth / 6366198.0d);
    }

    private void fetchRecentLocations(ArrayList<TLRPC.Message> messages) {
        LatLngBounds.Builder builder = null;
        if (this.firstFocus) {
            builder = new LatLngBounds.Builder();
        }
        int date = getConnectionsManager().getCurrentTime();
        for (int a = 0; a < messages.size(); a++) {
            TLRPC.Message message = messages.get(a);
            if (message.date + message.media.period > date) {
                if (builder != null) {
                    builder.include(new LatLng(message.media.geo.lat, message.media.geo._long));
                }
                addUserMarker(message);
                if (!(this.proximityButton.getVisibility() == 8 || MessageObject.getFromChatId(message) == getUserConfig().getClientUserId())) {
                    this.proximityButton.setVisibility(0);
                    this.proximityAnimationInProgress = true;
                    this.proximityButton.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(180).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            boolean unused = LocationActivity.this.proximityAnimationInProgress = false;
                            LocationActivity.this.maybeShowProximityHint();
                        }
                    }).start();
                }
            }
        }
        if (builder != null) {
            if (this.firstFocus) {
                this.listView.smoothScrollBy(0, AndroidUtilities.dp(99.0f));
            }
            this.firstFocus = false;
            this.adapter.setLiveLocations(this.markers);
            if (this.messageObject.isLiveLocation()) {
                try {
                    LatLng center = builder.build().getCenter();
                    LatLng northEast = move(center, 100.0d, 100.0d);
                    builder.include(move(center, -100.0d, -100.0d));
                    builder.include(northEast);
                    LatLngBounds bounds = builder.build();
                    if (messages.size() > 1) {
                        try {
                            CameraUpdate newLatLngBounds = CameraUpdateFactory.newLatLngBounds(bounds, AndroidUtilities.dp(113.0f));
                            this.moveToBounds = newLatLngBounds;
                            this.googleMap.moveCamera(newLatLngBounds);
                            this.moveToBounds = null;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                } catch (Exception e2) {
                }
            }
        }
    }

    private void moveToBounds(int radius, boolean self, boolean animated) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
        if (self) {
            try {
                int radius2 = Math.max(radius, 250);
                LatLng center = builder.build().getCenter();
                LatLng northEast = move(center, (double) radius2, (double) radius2);
                builder.include(move(center, (double) (-radius2), (double) (-radius2)));
                builder.include(northEast);
                LatLngBounds bounds = builder.build();
                try {
                    this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), (int) (((float) (this.proximitySheet.getCustomView().getMeasuredHeight() - AndroidUtilities.dp(40.0f))) + this.mapViewClip.getTranslationY()));
                    if (animated) {
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0), 500, (GoogleMap.CancelableCallback) null);
                    } else {
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } catch (Exception e2) {
            }
        } else {
            int date = getConnectionsManager().getCurrentTime();
            int N = this.markers.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Message message = this.markers.get(a).object;
                if (message.date + message.media.period > date) {
                    builder.include(new LatLng(message.media.geo.lat, message.media.geo._long));
                }
            }
            try {
                LatLng center2 = builder.build().getCenter();
                LatLng northEast2 = move(center2, 100.0d, 100.0d);
                builder.include(move(center2, -100.0d, -100.0d));
                builder.include(northEast2);
                LatLngBounds bounds2 = builder.build();
                try {
                    this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), this.proximitySheet.getCustomView().getMeasuredHeight() - AndroidUtilities.dp(100.0f));
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds2, 0));
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            } catch (Exception e4) {
            }
        }
    }

    private boolean getRecentLocations() {
        ArrayList<TLRPC.Message> messages = getLocationController().locationsCache.get(this.messageObject.getDialogId());
        if (messages == null || !messages.isEmpty()) {
            messages = null;
        } else {
            fetchRecentLocations(messages);
        }
        if (DialogObject.isChatDialog(this.dialogId)) {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TLRPC.TL_messages_getRecentLocations req = new TLRPC.TL_messages_getRecentLocations();
        long dialog_id = this.messageObject.getDialogId();
        req.peer = getMessagesController().getInputPeer(dialog_id);
        req.limit = 100;
        getConnectionsManager().sendRequest(req, new LocationActivity$$ExternalSyntheticLambda18(this, dialog_id));
        if (messages != null) {
            return true;
        }
        return false;
    }

    /* renamed from: lambda$getRecentLocations$36$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2428lambda$getRecentLocations$36$orgtelegramuiLocationActivity(long dialog_id, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new LocationActivity$$ExternalSyntheticLambda15(this, response, dialog_id));
        }
    }

    /* renamed from: lambda$getRecentLocations$35$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2427lambda$getRecentLocations$35$orgtelegramuiLocationActivity(TLObject response, long dialog_id) {
        if (this.googleMap != null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            int a = 0;
            while (a < res.messages.size()) {
                if (!(res.messages.get(a).media instanceof TLRPC.TL_messageMediaGeoLive)) {
                    res.messages.remove(a);
                    a--;
                }
                a++;
            }
            getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            getMessagesController().putUsers(res.users, false);
            getMessagesController().putChats(res.chats, false);
            getLocationController().locationsCache.put(dialog_id, res.messages);
            getNotificationCenter().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(dialog_id));
            fetchRecentLocations(res.messages);
            getLocationController().markLiveLoactionsAsRead(this.dialogId);
            if (this.markAsReadRunnable == null) {
                LocationActivity$$ExternalSyntheticLambda7 locationActivity$$ExternalSyntheticLambda7 = new LocationActivity$$ExternalSyntheticLambda7(this);
                this.markAsReadRunnable = locationActivity$$ExternalSyntheticLambda7;
                AndroidUtilities.runOnUIThread(locationActivity$$ExternalSyntheticLambda7, 5000);
            }
        }
    }

    /* renamed from: lambda$getRecentLocations$34$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2426lambda$getRecentLocations$34$orgtelegramuiLocationActivity() {
        Runnable runnable;
        getLocationController().markLiveLoactionsAsRead(this.dialogId);
        if (!this.isPaused && (runnable = this.markAsReadRunnable) != null) {
            AndroidUtilities.runOnUIThread(runnable, 5000);
        }
    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        LatLng latLng = latLng1;
        LatLng latLng3 = latLng2;
        double lat1 = (latLng.latitude * 3.141592653589793d) / 180.0d;
        double lat2 = (latLng3.latitude * 3.141592653589793d) / 180.0d;
        double dLon = ((latLng3.longitude * 3.141592653589793d) / 180.0d) - ((latLng.longitude * 3.141592653589793d) / 180.0d);
        return (Math.toDegrees(Math.atan2(Math.sin(dLon) * Math.cos(lat2), (Math.cos(lat1) * Math.sin(lat2)) - ((Math.sin(lat1) * Math.cos(lat2)) * Math.cos(dLon)))) + 360.0d) % 360.0d;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        LocationActivityAdapter locationActivityAdapter;
        LocationActivityAdapter locationActivityAdapter2;
        int i = id;
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.locationPermissionGranted) {
            this.locationDenied = false;
            LocationActivityAdapter locationActivityAdapter3 = this.adapter;
            if (locationActivityAdapter3 != null) {
                locationActivityAdapter3.setMyLocationDenied(false);
            }
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                try {
                    googleMap2.setMyLocationEnabled(true);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        } else if (i == NotificationCenter.locationPermissionDenied) {
            this.locationDenied = true;
            LocationActivityAdapter locationActivityAdapter4 = this.adapter;
            if (locationActivityAdapter4 != null) {
                locationActivityAdapter4.setMyLocationDenied(true);
            }
        } else if (i == NotificationCenter.liveLocationsChanged) {
            LocationActivityAdapter locationActivityAdapter5 = this.adapter;
            if (locationActivityAdapter5 != null) {
                locationActivityAdapter5.updateLiveLocationCell();
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!args[2].booleanValue() && args[0].longValue() == this.dialogId && this.messageObject != null) {
                ArrayList<MessageObject> arr = args[1];
                boolean added = false;
                for (int a = 0; a < arr.size(); a++) {
                    MessageObject messageObject2 = arr.get(a);
                    if (messageObject2.isLiveLocation()) {
                        addUserMarker(messageObject2.messageOwner);
                        added = true;
                    } else if ((messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionGeoProximityReached) && DialogObject.isUserDialog(messageObject2.getDialogId())) {
                        this.proximityButton.setImageResource(NUM);
                        Circle circle = this.proximityCircle;
                        if (circle != null) {
                            circle.remove();
                            this.proximityCircle = null;
                        }
                    }
                }
                if (added && (locationActivityAdapter2 = this.adapter) != null) {
                    locationActivityAdapter2.setLiveLocations(this.markers);
                }
            }
        } else if (i == NotificationCenter.replaceMessagesObjects) {
            long did = args[0].longValue();
            if (did == this.dialogId && this.messageObject != null) {
                boolean updated = false;
                ArrayList<MessageObject> messageObjects = args[1];
                for (int a2 = 0; a2 < messageObjects.size(); a2++) {
                    MessageObject messageObject3 = messageObjects.get(a2);
                    if (messageObject3.isLiveLocation()) {
                        LiveLocation liveLocation = this.markersMap.get(getMessageId(messageObject3.messageOwner));
                        if (liveLocation != null) {
                            LocationController.SharingLocationInfo myInfo = getLocationController().getSharingLocationInfo(did);
                            if (myInfo == null || myInfo.mid != messageObject3.getId()) {
                                liveLocation.object = messageObject3.messageOwner;
                                LatLng latLng = new LatLng(messageObject3.messageOwner.media.geo.lat, messageObject3.messageOwner.media.geo._long);
                                liveLocation.marker.setPosition(latLng);
                                if (liveLocation.directionMarker != null) {
                                    LatLng position = liveLocation.directionMarker.getPosition();
                                    liveLocation.directionMarker.setPosition(latLng);
                                    if (messageObject3.messageOwner.media.heading != 0) {
                                        liveLocation.directionMarker.setRotation((float) messageObject3.messageOwner.media.heading);
                                        if (!liveLocation.hasRotation) {
                                            liveLocation.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(NUM));
                                            liveLocation.hasRotation = true;
                                        }
                                    } else if (liveLocation.hasRotation) {
                                        liveLocation.directionMarker.setRotation(0.0f);
                                        liveLocation.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(NUM));
                                        liveLocation.hasRotation = false;
                                    }
                                }
                            }
                            updated = true;
                        }
                    }
                }
                if (updated && (locationActivityAdapter = this.adapter) != null) {
                    locationActivityAdapter.updateLiveLocations();
                    ProximitySheet proximitySheet2 = this.proximitySheet;
                    if (proximitySheet2 != null) {
                        proximitySheet2.updateText(true, true);
                    }
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        MapView mapView2 = this.mapView;
        if (mapView2 != null && this.mapsInitialized) {
            try {
                mapView2.onPause();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        this.onResumeCalled = false;
    }

    public boolean onBackPressed() {
        ProximitySheet proximitySheet2 = this.proximitySheet;
        if (proximitySheet2 == null) {
            return super.onBackPressed();
        }
        proximitySheet2.dismiss();
        return false;
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    public void onResume() {
        Activity activity;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        MapView mapView2 = this.mapView;
        if (mapView2 != null && this.mapsInitialized) {
            try {
                mapView2.onResume();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = true;
        GoogleMap googleMap2 = this.googleMap;
        if (googleMap2 != null) {
            try {
                googleMap2.setMyLocationEnabled(true);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
        fixLayoutInternal(true);
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            }
        }
        Runnable runnable = this.markAsReadRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            AndroidUtilities.runOnUIThread(this.markAsReadRunnable, 5000);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 30) {
            openShareLiveLocation(this.askWithRadius);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        MapView mapView2 = this.mapView;
        if (mapView2 != null && this.mapsInitialized) {
            mapView2.onLowMemory();
        }
    }

    public void setDelegate(LocationActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    private void updateSearchInterface() {
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new LocationActivity$$ExternalSyntheticLambda21(this);
        for (int a = 0; a < this.undoView.length; a++) {
            themeDescriptions.add(new ThemeDescription(this.undoView[a], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "BODY", "undo_background"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Wibe Big", "undo_background"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Wibe Big 3", "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Wibe Small", "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Body Main.**", "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Body Top.**", "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Line.**", "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Curve Big.**", "undo_infoColor"));
            themeDescriptions.add(new ThemeDescription((View) this.undoView[a], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Curve Small.**", "undo_infoColor"));
        }
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint"));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptions.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "actionBarDefaultSubmenuBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "actionBarDefaultSubmenuItem"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "actionBarDefaultSubmenuItemIcon"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        themeDescriptions.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        themeDescriptions.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        themeDescriptions.add(new ThemeDescription(this.shadow, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionIcon"));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon"));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "location_actionIcon"));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        themeDescriptions.add(new ThemeDescription(this.proximityButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "location_actionIcon"));
        themeDescriptions.add(new ThemeDescription(this.proximityButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        themeDescriptions.add(new ThemeDescription(this.proximityButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        themeDescriptions.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon"));
        themeDescriptions.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        themeDescriptions.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, themeDescriptionDelegate2, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate3 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundRed"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_liveLocationProgress"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_placeLocationBackground"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialog_liveLocationProgress"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationDirectionCell.class}, new String[]{"buttonTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"distanceTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$37$org-telegram-ui-LocationActivity  reason: not valid java name */
    public /* synthetic */ void m2429lambda$getThemeDescriptions$37$orgtelegramuiLocationActivity() {
        this.mapTypeButton.setIconColor(Theme.getColor("location_actionIcon"));
        this.mapTypeButton.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.shadow.invalidate();
        if (this.googleMap == null) {
            return;
        }
        if (isActiveThemeDark()) {
            if (!this.currentMapStyleDark) {
                this.currentMapStyleDark = true;
                this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, NUM));
                Circle circle = this.proximityCircle;
                if (circle != null) {
                    circle.setStrokeColor(-1);
                    this.proximityCircle.setFillColor(NUM);
                }
            }
        } else if (this.currentMapStyleDark) {
            this.currentMapStyleDark = false;
            this.googleMap.setMapStyle((MapStyleOptions) null);
            Circle circle2 = this.proximityCircle;
            if (circle2 != null) {
                circle2.setStrokeColor(-16777216);
                this.proximityCircle.setFillColor(NUM);
            }
        }
    }
}
