package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
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
import android.text.TextUtils.TruncateAt;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.InputGeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_channelLocation;
import org.telegram.tgnet.TLRPC.TL_channels_editLocation;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentLocations;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
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
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class LocationActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final double EARTHRADIUS = 6366198.0d;
    public static final int LOCATION_TYPE_GROUP = 4;
    public static final int LOCATION_TYPE_GROUP_VIEW = 5;
    public static final int LOCATION_TYPE_SEND = 0;
    public static final int LOCATION_TYPE_SEND_WITH_LIVE = 1;
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    private static final int open_in = 1;
    private static final int share_live_location = 5;
    private LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private Bitmap[] bitmapCache = new Bitmap[7];
    private TL_channelLocation chatLocation;
    private boolean checkGpsEnabled = true;
    private boolean checkPermission = true;
    private CircleOptions circleOptions;
    private boolean currentMapStyleDark;
    private LocationActivityDelegate delegate;
    private long dialogId;
    private ImageView emptyImageView;
    private TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    private boolean firstFocus = true;
    private boolean firstWas;
    private CameraUpdate forceUpdate;
    private GoogleMap googleMap;
    private TL_channelLocation initialLocation;
    private boolean isFirstLocation = true;
    private Marker lastPressedMarker;
    private FrameLayout lastPressedMarkerView;
    private VenueLocation lastPressedVenue;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ImageView locationButton;
    private int locationType;
    private ActionBarMenuItem mapTypeButton;
    private MapView mapView;
    private FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private Runnable markAsReadRunnable;
    private View markerImageView;
    private int markerTop;
    private ArrayList<LiveLocation> markers = new ArrayList();
    private SparseArray<LiveLocation> markersMap = new SparseArray();
    private MessageObject messageObject;
    private Location myLocation;
    private boolean onResumeCalled;
    private ActionBarMenuItem otherItem;
    private int overScrollHeight = ((AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f));
    private MapOverlayView overlayView;
    private ChatActivity parentFragment;
    private ArrayList<VenueLocation> placeMarkers = new ArrayList();
    private boolean scrolling;
    private LocationActivitySearchAdapter searchAdapter;
    private SearchButton searchAreaButton;
    private boolean searchInProgress;
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchWas;
    private boolean searchedForCustomLocations;
    private boolean searching;
    private View shadow;
    private Drawable shadowDrawable;
    private Runnable updateRunnable;
    private Location userLocation;
    private boolean userLocationMoved;
    private boolean wasResults;
    private float yOffset;

    public class LiveLocation {
        public Chat chat;
        public int id;
        public Marker marker;
        public Message object;
        public User user;
    }

    public interface LocationActivityDelegate {
        void didSelectLocation(MessageMedia messageMedia, int i, boolean z, int i2);
    }

    public class MapOverlayView extends FrameLayout {
        private HashMap<Marker, View> views = new HashMap();

        public MapOverlayView(Context context) {
            super(context);
        }

        public void addInfoView(Marker marker) {
            Marker marker2 = marker;
            VenueLocation venueLocation = (VenueLocation) marker.getTag();
            if (LocationActivity.this.lastPressedVenue != venueLocation) {
                LocationActivity.this.showSearchPlacesButton(false);
                if (LocationActivity.this.lastPressedMarker != null) {
                    removeInfoView(LocationActivity.this.lastPressedMarker);
                    LocationActivity.this.lastPressedMarker = null;
                }
                LocationActivity.this.lastPressedVenue = venueLocation;
                LocationActivity.this.lastPressedMarker = marker2;
                Context context = getContext();
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createFrame(-2, 114.0f));
                LocationActivity.this.lastPressedMarkerView = new FrameLayout(context);
                LocationActivity.this.lastPressedMarkerView.setBackgroundResource(NUM);
                LocationActivity.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), Mode.MULTIPLY));
                frameLayout.addView(LocationActivity.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
                LocationActivity.this.lastPressedMarkerView.setAlpha(0.0f);
                LocationActivity.this.lastPressedMarkerView.setOnClickListener(new -$$Lambda$LocationActivity$MapOverlayView$o5OZdnsBb0Qzhy6B5bQNdu7taGo(this, venueLocation));
                TextView textView = new TextView(context);
                textView.setTextSize(1, 16.0f);
                textView.setMaxLines(1);
                textView.setEllipsize(TruncateAt.END);
                textView.setSingleLine(true);
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                int i = 5;
                textView.setGravity(LocaleController.isRTL ? 5 : 3);
                LocationActivity.this.lastPressedMarkerView.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 10.0f, 18.0f, 0.0f));
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 14.0f);
                textView2.setMaxLines(1);
                textView2.setEllipsize(TruncateAt.END);
                textView2.setSingleLine(true);
                textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                FrameLayout access$300 = LocationActivity.this.lastPressedMarkerView;
                if (!LocaleController.isRTL) {
                    i = 3;
                }
                access$300.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, i | 48, 18.0f, 32.0f, 18.0f, 0.0f));
                textView.setText(venueLocation.venue.title);
                textView2.setText(LocaleController.getString("TapToSendLocation", NUM));
                final FrameLayout frameLayout2 = new FrameLayout(context);
                frameLayout2.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(36.0f), LocationCell.getColorForIndex(venueLocation.num)));
                frameLayout.addView(frameLayout2, LayoutHelper.createFrame(36, 36.0f, 81, 0.0f, 0.0f, 0.0f, 4.0f));
                BackupImageView backupImageView = new BackupImageView(context);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("https://ss3.4sqi.net/img/categories_v2/");
                stringBuilder.append(venueLocation.venue.venue_type);
                stringBuilder.append("_64.png");
                backupImageView.setImage(stringBuilder.toString(), null, null);
                frameLayout2.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                    private final float[] animatorValues = new float[]{0.0f, 1.0f};
                    private boolean startedInner;

                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float lerp = AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction());
                        if (!(lerp < 0.7f || this.startedInner || LocationActivity.this.lastPressedMarkerView == null)) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.SCALE_Y, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.ALPHA, new float[]{0.0f, 1.0f})});
                            animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
                            animatorSet.setDuration(250);
                            animatorSet.start();
                            this.startedInner = true;
                        }
                        if (lerp <= 0.5f) {
                            lerp = CubicBezierInterpolator.EASE_OUT.getInterpolation(lerp / 0.5f) * 1.1f;
                        } else if (lerp <= 0.75f) {
                            lerp = 1.1f - (CubicBezierInterpolator.EASE_OUT.getInterpolation((lerp - 0.5f) / 0.25f) * 0.2f);
                        } else {
                            lerp = (CubicBezierInterpolator.EASE_OUT.getInterpolation((lerp - 0.75f) / 0.25f) * 0.1f) + 0.9f;
                        }
                        frameLayout2.setScaleX(lerp);
                        frameLayout2.setScaleY(lerp);
                    }
                });
                ofFloat.setDuration(360);
                ofFloat.start();
                this.views.put(marker2, frameLayout);
                LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, null);
            }
        }

        public /* synthetic */ void lambda$addInfoView$1$LocationActivity$MapOverlayView(VenueLocation venueLocation, View view) {
            if (LocationActivity.this.parentFragment == null || !LocationActivity.this.parentFragment.isInScheduleMode()) {
                LocationActivity.this.delegate.didSelectLocation(venueLocation.venue, LocationActivity.this.locationType, true, 0);
                LocationActivity.this.finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(LocationActivity.this.getParentActivity(), LocationActivity.this.parentFragment.getDialogId(), new -$$Lambda$LocationActivity$MapOverlayView$BqnPfqylvzkhigN9LSO44zjd2EA(this, venueLocation));
        }

        public /* synthetic */ void lambda$null$0$LocationActivity$MapOverlayView(VenueLocation venueLocation, boolean z, int i) {
            LocationActivity.this.delegate.didSelectLocation(venueLocation.venue, LocationActivity.this.locationType, z, i);
            LocationActivity.this.finishFragment();
        }

        public void removeInfoView(Marker marker) {
            View view = (View) this.views.get(marker);
            if (view != null) {
                removeView(view);
                this.views.remove(marker);
            }
        }

        public void updatePositions() {
            if (LocationActivity.this.googleMap != null) {
                Projection projection = LocationActivity.this.googleMap.getProjection();
                for (Entry entry : this.views.entrySet()) {
                    Marker marker = (Marker) entry.getKey();
                    View view = (View) entry.getValue();
                    Point toScreenLocation = projection.toScreenLocation(marker.getPosition());
                    view.setTranslationX((float) (toScreenLocation.x - (view.getMeasuredWidth() / 2)));
                    view.setTranslationY((float) ((toScreenLocation.y - view.getMeasuredHeight()) + AndroidUtilities.dp(22.0f)));
                }
            }
        }
    }

    private class SearchButton extends TextView {
        private float additionanTranslationY;
        private float currentTranslationY;

        public SearchButton(Context context) {
            super(context);
        }

        public float getTranslationX() {
            return this.additionanTranslationY;
        }

        public void setTranslationX(float f) {
            this.additionanTranslationY = f;
            updateTranslationY();
        }

        public void setTranslation(float f) {
            this.currentTranslationY = f;
            updateTranslationY();
        }

        private void updateTranslationY() {
            setTranslationY(this.currentTranslationY + this.additionanTranslationY);
        }
    }

    public class VenueLocation {
        public Marker marker;
        public int num;
        public TL_messageMediaVenue venue;
    }

    public LocationActivity(int i) {
        this.locationType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.swipeBackEnabled = false;
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        MessageObject messageObject = this.messageObject;
        if (messageObject != null && messageObject.isLiveLocation()) {
            getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
            getNotificationCenter().addObserver(this, NotificationCenter.replaceMessagesObjects);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.replaceMessagesObjects);
        try {
            if (this.googleMap != null) {
                this.googleMap.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            if (this.mapView != null) {
                this.mapView.onDestroy();
            }
        } catch (Exception e2) {
            FileLog.e(e2);
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
        runnable = this.markAsReadRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.markAsReadRunnable = null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x04d8  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x04d3  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x04e3  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x04e0  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x054c  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x051f  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x05fb  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x05f6  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0606  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0603  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x08c1  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x08bb  */
    public android.view.View createView(android.content.Context r29) {
        /*
        r28 = this;
        r6 = r28;
        r7 = r29;
        r8 = 0;
        r6.searchWas = r8;
        r6.searching = r8;
        r6.searchInProgress = r8;
        r0 = r6.adapter;
        if (r0 == 0) goto L_0x0012;
    L_0x000f:
        r0.destroy();
    L_0x0012:
        r0 = r6.searchAdapter;
        if (r0 == 0) goto L_0x0019;
    L_0x0016:
        r0.destroy();
    L_0x0019:
        r0 = r6.chatLocation;
        if (r0 == 0) goto L_0x003d;
    L_0x001d:
        r0 = new android.location.Location;
        r1 = "network";
        r0.<init>(r1);
        r6.userLocation = r0;
        r0 = r6.userLocation;
        r1 = r6.chatLocation;
        r1 = r1.geo_point;
        r1 = r1.lat;
        r0.setLatitude(r1);
        r0 = r6.userLocation;
        r1 = r6.chatLocation;
        r1 = r1.geo_point;
        r1 = r1._long;
        r0.setLongitude(r1);
        goto L_0x0068;
    L_0x003d:
        r0 = r6.messageObject;
        if (r0 == 0) goto L_0x0068;
    L_0x0041:
        r0 = new android.location.Location;
        r1 = "network";
        r0.<init>(r1);
        r6.userLocation = r0;
        r0 = r6.userLocation;
        r1 = r6.messageObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.geo;
        r1 = r1.lat;
        r0.setLatitude(r1);
        r0 = r6.userLocation;
        r1 = r6.messageObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.geo;
        r1 = r1._long;
        r0.setLongitude(r1);
    L_0x0068:
        r0 = r6.actionBar;
        r1 = "dialogBackground";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setBackgroundColor(r2);
        r0 = r6.actionBar;
        r2 = "dialogTextBlack";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setTitleColor(r3);
        r0 = r6.actionBar;
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setItemsColor(r3, r8);
        r0 = r6.actionBar;
        r3 = "dialogButtonSelector";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r0.setItemsBackgroundColor(r3, r8);
        r0 = r6.actionBar;
        r3 = NUM; // 0x7var_f3 float:1.794507E38 double:1.052935623E-314;
        r0.setBackButtonImage(r3);
        r0 = r6.actionBar;
        r9 = 1;
        r0.setAllowOverlayTitle(r9);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x00ab;
    L_0x00a6:
        r0 = r6.actionBar;
        r0.setOccupyStatusBar(r8);
    L_0x00ab:
        r0 = r6.actionBar;
        r0.setAddToContainer(r8);
        r0 = r6.actionBar;
        r3 = new org.telegram.ui.LocationActivity$1;
        r3.<init>();
        r0.setActionBarMenuOnItemClick(r3);
        r0 = r6.actionBar;
        r0 = r0.createMenu();
        r3 = r6.chatLocation;
        r10 = 4;
        if (r3 == 0) goto L_0x00d5;
    L_0x00c5:
        r0 = r6.actionBar;
        r2 = NUM; // 0x7f0e02bc float:1.8876457E38 double:1.0531625025E-314;
        r3 = "ChatLocation";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setTitle(r2);
        goto L_0x01cb;
    L_0x00d5:
        r3 = r6.messageObject;
        if (r3 == 0) goto L_0x0163;
    L_0x00d9:
        r2 = r3.isLiveLocation();
        if (r2 == 0) goto L_0x00ef;
    L_0x00df:
        r0 = r6.actionBar;
        r2 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r3 = "AttachLiveLocation";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setTitle(r2);
        goto L_0x01cb;
    L_0x00ef:
        r2 = r6.messageObject;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2 = r2.title;
        if (r2 == 0) goto L_0x010e;
    L_0x00f9:
        r2 = r2.length();
        if (r2 <= 0) goto L_0x010e;
    L_0x00ff:
        r2 = r6.actionBar;
        r3 = NUM; // 0x7f0e0a71 float:1.888046E38 double:1.0531634773E-314;
        r4 = "SharedPlace";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r2.setTitle(r3);
        goto L_0x011c;
    L_0x010e:
        r2 = r6.actionBar;
        r3 = NUM; // 0x7f0e02bc float:1.8876457E38 double:1.0531625025E-314;
        r4 = "ChatLocation";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r2.setTitle(r3);
    L_0x011c:
        r2 = NUM; // 0x7var_fa float:1.7945085E38 double:1.0529356265E-314;
        r0 = r0.addItem(r8, r2);
        r6.otherItem = r0;
        r0 = r6.otherItem;
        r2 = NUM; // 0x7var_ea float:1.7945572E38 double:1.052935745E-314;
        r3 = NUM; // 0x7f0e078b float:1.8878954E38 double:1.0531631107E-314;
        r4 = "OpenInExternalApp";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r0.addSubItem(r9, r2, r3);
        r0 = r28.getLocationController();
        r2 = r6.dialogId;
        r0 = r0.isSharingLocation(r2);
        if (r0 != 0) goto L_0x0154;
    L_0x0142:
        r0 = r6.otherItem;
        r2 = 5;
        r3 = NUM; // 0x7var_a5 float:1.7945432E38 double:1.052935711E-314;
        r4 = NUM; // 0x7f0e0a18 float:1.8880279E38 double:1.0531634333E-314;
        r5 = "SendLiveLocationMenu";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r0.addSubItem(r2, r3, r4);
    L_0x0154:
        r0 = r6.otherItem;
        r2 = NUM; // 0x7f0e002c float:1.8875127E38 double:1.0531621784E-314;
        r3 = "AccDescrMoreOptions";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setContentDescription(r2);
        goto L_0x01cb;
    L_0x0163:
        r3 = r6.actionBar;
        r4 = NUM; // 0x7f0e0a5a float:1.8880412E38 double:1.053163466E-314;
        r5 = "ShareLocation";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r3.setTitle(r4);
        r3 = r6.locationType;
        if (r3 == r10) goto L_0x01cb;
    L_0x0175:
        r3 = new org.telegram.ui.LocationActivity$MapOverlayView;
        r3.<init>(r7);
        r6.overlayView = r3;
        r3 = NUM; // 0x7var_fd float:1.7945091E38 double:1.052935628E-314;
        r0 = r0.addItem(r8, r3);
        r0 = r0.setIsSearchField(r9);
        r3 = new org.telegram.ui.LocationActivity$2;
        r3.<init>();
        r0 = r0.setActionBarMenuItemSearchListener(r3);
        r6.searchItem = r0;
        r0 = r6.searchItem;
        r3 = NUM; // 0x7f0e09d9 float:1.888015E38 double:1.053163402E-314;
        r4 = "Search";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r0.setSearchFieldHint(r3);
        r0 = r6.searchItem;
        r3 = NUM; // 0x7f0e09d9 float:1.888015E38 double:1.053163402E-314;
        r4 = "Search";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r0.setContentDescription(r3);
        r0 = r6.searchItem;
        r0 = r0.getSearchField();
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setTextColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setCursorColor(r2);
        r2 = "chat_messagePanelHint";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setHintTextColor(r2);
    L_0x01cb:
        r0 = new org.telegram.ui.LocationActivity$3;
        r0.<init>(r7);
        r6.fragmentView = r0;
        r0 = r6.fragmentView;
        r11 = r0;
        r11 = (android.widget.FrameLayout) r11;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setBackgroundColor(r2);
        r0 = r29.getResources();
        r2 = NUM; // 0x7var_d float:1.7945935E38 double:1.0529358336E-314;
        r0 = r0.getDrawable(r2);
        r0 = r0.mutate();
        r6.shadowDrawable = r0;
        r0 = r6.shadowDrawable;
        r2 = new android.graphics.PorterDuffColorFilter;
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r3 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r1, r3);
        r0.setColorFilter(r2);
        r12 = new android.graphics.Rect;
        r12.<init>();
        r0 = r6.shadowDrawable;
        r0.getPadding(r12);
        r0 = r6.locationType;
        r13 = -1;
        if (r0 == 0) goto L_0x0220;
    L_0x020e:
        if (r0 != r9) goto L_0x0211;
    L_0x0210:
        goto L_0x0220;
    L_0x0211:
        r0 = new android.widget.FrameLayout$LayoutParams;
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r12.top;
        r1 = r1 + r2;
        r0.<init>(r13, r1);
        goto L_0x022e;
    L_0x0220:
        r0 = new android.widget.FrameLayout$LayoutParams;
        r1 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r12.top;
        r1 = r1 + r2;
        r0.<init>(r13, r1);
    L_0x022e:
        r14 = r0;
        r0 = 83;
        r14.gravity = r0;
        r0 = new org.telegram.ui.LocationActivity$4;
        r0.<init>(r7);
        r6.mapViewClip = r0;
        r0 = r6.mapViewClip;
        r1 = new org.telegram.ui.Components.MapPlaceholderDrawable;
        r1.<init>();
        r0.setBackgroundDrawable(r1);
        r0 = r6.messageObject;
        r1 = "location_actionPressedBackground";
        r2 = "location_actionBackground";
        r15 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r13 = 21;
        r10 = 2;
        r16 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        if (r0 != 0) goto L_0x0391;
    L_0x0253:
        r0 = r6.locationType;
        if (r0 == 0) goto L_0x0259;
    L_0x0257:
        if (r0 != r9) goto L_0x0391;
    L_0x0259:
        r0 = new org.telegram.ui.LocationActivity$SearchButton;
        r0.<init>(r7);
        r6.searchAreaButton = r0;
        r0 = r6.searchAreaButton;
        r17 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r3 = -r3;
        r3 = (float) r3;
        r0.setTranslationX(r3);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r0, r3, r4);
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 >= r13) goto L_0x02af;
    L_0x0281:
        r3 = r29.getResources();
        r4 = NUM; // 0x7var_ float:1.7945815E38 double:1.0529358044E-314;
        r3 = r3.getDrawable(r4);
        r3 = r3.mutate();
        r4 = new android.graphics.PorterDuffColorFilter;
        r13 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r5 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r4.<init>(r13, r5);
        r3.setColorFilter(r4);
        r4 = new org.telegram.ui.Components.CombinedDrawable;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r13 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r4.<init>(r3, r0, r5, r13);
        r4.setFullsize(r9);
        r0 = r4;
        r9 = r11;
        goto L_0x0319;
    L_0x02af:
        r3 = new android.animation.StateListAnimator;
        r3.<init>();
        r4 = new int[r9];
        r5 = 16842919; // 0x10100a7 float:2.3694026E-38 double:8.3215077E-317;
        r4[r8] = r5;
        r5 = r6.searchAreaButton;
        r13 = android.view.View.TRANSLATION_Z;
        r9 = new float[r10];
        r10 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r10 = (float) r10;
        r9[r8] = r10;
        r10 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r15 = (float) r15;
        r18 = 1;
        r9[r18] = r15;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r13, r9);
        r9 = r11;
        r10 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r5 = r5.setDuration(r10);
        r3.addState(r4, r5);
        r4 = new int[r8];
        r5 = r6.searchAreaButton;
        r10 = android.view.View.TRANSLATION_Z;
        r11 = 2;
        r13 = new float[r11];
        r11 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r15;
        r13[r8] = r11;
        r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r15;
        r15 = 1;
        r13[r15] = r11;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r13);
        r10 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r5 = r5.setDuration(r10);
        r3.addState(r4, r5);
        r4 = r6.searchAreaButton;
        r4.setStateListAnimator(r3);
        r3 = r6.searchAreaButton;
        r4 = new org.telegram.ui.LocationActivity$5;
        r4.<init>();
        r3.setOutlineProvider(r4);
    L_0x0319:
        r3 = r6.searchAreaButton;
        r3.setBackgroundDrawable(r0);
        r0 = r6.searchAreaButton;
        r3 = "location_actionActiveIcon";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r0.setTextColor(r3);
        r0 = r6.searchAreaButton;
        r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = 1;
        r0.setTextSize(r4, r3);
        r0 = r6.searchAreaButton;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r0.setTypeface(r3);
        r0 = r6.searchAreaButton;
        r3 = NUM; // 0x7f0e08f0 float:1.8879678E38 double:1.053163287E-314;
        r4 = "PlacesInThisArea";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r0.setText(r3);
        r0 = r6.searchAreaButton;
        r3 = 17;
        r0.setGravity(r3);
        r0 = r6.searchAreaButton;
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0.setPadding(r3, r8, r4, r8);
        r0 = r6.mapViewClip;
        r3 = r6.searchAreaButton;
        r21 = -2;
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 21;
        if (r4 < r5) goto L_0x0371;
    L_0x036e:
        r22 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        goto L_0x0375;
    L_0x0371:
        r4 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r22 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
    L_0x0375:
        r23 = 49;
        r24 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r26 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r27 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r0.addView(r3, r4);
        r0 = r6.searchAreaButton;
        r3 = new org.telegram.ui.-$$Lambda$LocationActivity$TkcWncqpxSCAWRvTBUSIB-QLlIg;
        r3.<init>(r6);
        r0.setOnClickListener(r3);
        goto L_0x0392;
    L_0x0391:
        r9 = r11;
    L_0x0392:
        r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem;
        r3 = 0;
        r10 = "location_actionIcon";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r0.<init>(r7, r3, r8, r4);
        r6.mapTypeButton = r0;
        r0 = r6.mapTypeButton;
        r3 = 1;
        r0.setClickable(r3);
        r0 = r6.mapTypeButton;
        r3 = 2;
        r0.setSubMenuOpenSide(r3);
        r0 = r6.mapTypeButton;
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0.setAdditionalXOffset(r3);
        r0 = r6.mapTypeButton;
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = -r3;
        r0.setAdditionalYOffset(r3);
        r0 = r6.mapTypeButton;
        r3 = NUM; // 0x7var_e3 float:1.7945558E38 double:1.0529357417E-314;
        r4 = NUM; // 0x7f0e0611 float:1.8878187E38 double:1.053162924E-314;
        r5 = "Map";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = 2;
        r0.addSubItem(r5, r3, r4);
        r0 = r6.mapTypeButton;
        r3 = 3;
        r4 = NUM; // 0x7var_fc float:1.7945608E38 double:1.052935754E-314;
        r5 = NUM; // 0x7f0e09c2 float:1.8880104E38 double:1.053163391E-314;
        r11 = "Satellite";
        r5 = org.telegram.messenger.LocaleController.getString(r11, r5);
        r0.addSubItem(r3, r4, r5);
        r0 = r6.mapTypeButton;
        r3 = NUM; // 0x7var_dc float:1.7945543E38 double:1.052935738E-314;
        r4 = NUM; // 0x7f0e0573 float:1.8877867E38 double:1.053162846E-314;
        r5 = "Hybrid";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = 4;
        r0.addSubItem(r5, r3, r4);
        r0 = r6.mapTypeButton;
        r3 = NUM; // 0x7f0e002c float:1.8875127E38 double:1.0531621784E-314;
        r4 = "AccDescrMoreOptions";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r0.setContentDescription(r3);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r0, r3, r4);
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 21;
        if (r3 >= r4) goto L_0x044a;
    L_0x041d:
        r3 = r29.getResources();
        r4 = NUM; // 0x7var_cf float:1.7944998E38 double:1.0529356053E-314;
        r3 = r3.getDrawable(r4);
        r3 = r3.mutate();
        r4 = new android.graphics.PorterDuffColorFilter;
        r5 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r11 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r4.<init>(r5, r11);
        r3.setColorFilter(r4);
        r4 = new org.telegram.ui.Components.CombinedDrawable;
        r4.<init>(r3, r0, r8, r8);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r4.setIconSize(r0, r3);
        r11 = r1;
        goto L_0x04bc;
    L_0x044a:
        r3 = new android.animation.StateListAnimator;
        r3.<init>();
        r4 = 1;
        r5 = new int[r4];
        r11 = 16842919; // 0x10100a7 float:2.3694026E-38 double:8.3215077E-317;
        r5[r8] = r11;
        r11 = r6.mapTypeButton;
        r13 = android.view.View.TRANSLATION_Z;
        r15 = 2;
        r4 = new float[r15];
        r21 = r0;
        r15 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r0 = (float) r0;
        r4[r8] = r0;
        r0 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r15 = (float) r15;
        r18 = 1;
        r4[r18] = r15;
        r4 = android.animation.ObjectAnimator.ofFloat(r11, r13, r4);
        r11 = r1;
        r0 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r4 = r4.setDuration(r0);
        r3.addState(r5, r4);
        r0 = new int[r8];
        r1 = r6.mapTypeButton;
        r4 = android.view.View.TRANSLATION_Z;
        r5 = 2;
        r13 = new float[r5];
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r15;
        r13[r8] = r5;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r15;
        r15 = 1;
        r13[r15] = r5;
        r1 = android.animation.ObjectAnimator.ofFloat(r1, r4, r13);
        r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r1 = r1.setDuration(r4);
        r3.addState(r0, r1);
        r0 = r6.mapTypeButton;
        r0.setStateListAnimator(r3);
        r0 = r6.mapTypeButton;
        r1 = new org.telegram.ui.LocationActivity$6;
        r1.<init>();
        r0.setOutlineProvider(r1);
        r4 = r21;
    L_0x04bc:
        r0 = r6.mapTypeButton;
        r0.setBackgroundDrawable(r4);
        r0 = r6.mapTypeButton;
        r1 = NUM; // 0x7var_ float:1.7945328E38 double:1.052935686E-314;
        r0.setIcon(r1);
        r0 = r6.mapViewClip;
        r1 = r6.mapTypeButton;
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 21;
        if (r3 < r4) goto L_0x04d8;
    L_0x04d3:
        r3 = 40;
        r21 = 40;
        goto L_0x04dc;
    L_0x04d8:
        r3 = 44;
        r21 = 44;
    L_0x04dc:
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 < r4) goto L_0x04e3;
    L_0x04e0:
        r22 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        goto L_0x04e7;
    L_0x04e3:
        r3 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r22 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
    L_0x04e7:
        r23 = 53;
        r24 = 0;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r26 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r27 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27);
        r0.addView(r1, r3);
        r0 = r6.mapTypeButton;
        r1 = new org.telegram.ui.-$$Lambda$LocationActivity$lWSdOASfoaVKdfEagNe64TmZXrI;
        r1.<init>(r6);
        r0.setDelegate(r1);
        r0 = new android.widget.ImageView;
        r0.<init>(r7);
        r6.locationButton = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r11);
        r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r0, r1, r2);
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 21;
        if (r1 >= r2) goto L_0x054c;
    L_0x051f:
        r1 = r29.getResources();
        r2 = NUM; // 0x7var_cf float:1.7944998E38 double:1.0529356053E-314;
        r1 = r1.getDrawable(r2);
        r1 = r1.mutate();
        r2 = new android.graphics.PorterDuffColorFilter;
        r3 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r4 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r3, r4);
        r1.setColorFilter(r2);
        r2 = new org.telegram.ui.Components.CombinedDrawable;
        r2.<init>(r1, r0, r8, r8);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2.setIconSize(r0, r1);
        r0 = r2;
        goto L_0x05b5;
    L_0x054c:
        r1 = new android.animation.StateListAnimator;
        r1.<init>();
        r2 = 1;
        r3 = new int[r2];
        r4 = 16842919; // 0x10100a7 float:2.3694026E-38 double:8.3215077E-317;
        r3[r8] = r4;
        r4 = r6.locationButton;
        r5 = android.view.View.TRANSLATION_Z;
        r11 = 2;
        r13 = new float[r11];
        r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r15;
        r13[r8] = r11;
        r11 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r15 = (float) r15;
        r13[r2] = r15;
        r2 = android.animation.ObjectAnimator.ofFloat(r4, r5, r13);
        r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r2 = r2.setDuration(r4);
        r1.addState(r3, r2);
        r2 = new int[r8];
        r3 = r6.locationButton;
        r4 = android.view.View.TRANSLATION_Z;
        r5 = 2;
        r5 = new float[r5];
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r5[r8] = r11;
        r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r13 = 1;
        r5[r13] = r11;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5);
        r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r3 = r3.setDuration(r4);
        r1.addState(r2, r3);
        r2 = r6.locationButton;
        r2.setStateListAnimator(r1);
        r1 = r6.locationButton;
        r2 = new org.telegram.ui.LocationActivity$7;
        r2.<init>();
        r1.setOutlineProvider(r2);
    L_0x05b5:
        r1 = r6.locationButton;
        r1.setBackgroundDrawable(r0);
        r0 = r6.locationButton;
        r1 = NUM; // 0x7var_f float:1.7945322E38 double:1.0529356843E-314;
        r0.setImageResource(r1);
        r0 = r6.locationButton;
        r1 = android.widget.ImageView.ScaleType.CENTER;
        r0.setScaleType(r1);
        r0 = r6.locationButton;
        r1 = new android.graphics.PorterDuffColorFilter;
        r2 = "location_actionActiveIcon";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r3 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r1.<init>(r2, r3);
        r0.setColorFilter(r1);
        r0 = r6.locationButton;
        r1 = "location_actionActiveIcon";
        r0.setTag(r1);
        r0 = r6.locationButton;
        r1 = NUM; // 0x7f0e0032 float:1.8875139E38 double:1.0531621813E-314;
        r2 = "AccDescrMyLocation";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setContentDescription(r1);
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 21;
        if (r0 < r1) goto L_0x05fb;
    L_0x05f6:
        r0 = 40;
        r19 = 40;
        goto L_0x05ff;
    L_0x05fb:
        r0 = 44;
        r19 = 44;
    L_0x05ff:
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r1) goto L_0x0606;
    L_0x0603:
        r20 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        goto L_0x060a;
    L_0x0606:
        r0 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r20 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
    L_0x060a:
        r21 = 85;
        r22 = 0;
        r23 = 0;
        r24 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r25 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r1 = r0.bottomMargin;
        r2 = r14.height;
        r3 = r12.top;
        r2 = r2 - r3;
        r1 = r1 + r2;
        r0.bottomMargin = r1;
        r1 = r6.mapViewClip;
        r2 = r6.locationButton;
        r1.addView(r2, r0);
        r0 = r6.locationButton;
        r1 = new org.telegram.ui.-$$Lambda$LocationActivity$_S8Uotztn_nTGrRvar_YAorbRk;
        r1.<init>(r6);
        r0.setOnClickListener(r1);
        r0 = new android.widget.LinearLayout;
        r0.<init>(r7);
        r6.emptyView = r0;
        r0 = r6.emptyView;
        r1 = 1;
        r0.setOrientation(r1);
        r0 = r6.emptyView;
        r0.setGravity(r1);
        r0 = r6.emptyView;
        r1 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0.setPadding(r8, r1, r8, r8);
        r0 = r6.emptyView;
        r1 = 8;
        r0.setVisibility(r1);
        r0 = r6.emptyView;
        r1 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r2 = -1;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1);
        r9.addView(r0, r1);
        r0 = r6.emptyView;
        r1 = org.telegram.ui.-$$Lambda$LocationActivity$imGE6wrjywjW00_01AfMqekBLBw.INSTANCE;
        r0.setOnTouchListener(r1);
        r0 = new android.widget.ImageView;
        r0.<init>(r7);
        r6.emptyImageView = r0;
        r0 = r6.emptyImageView;
        r1 = NUM; // 0x7var_ float:1.7945324E38 double:1.052935685E-314;
        r0.setImageResource(r1);
        r0 = r6.emptyImageView;
        r1 = new android.graphics.PorterDuffColorFilter;
        r2 = "dialogEmptyImage";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r3 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r1.<init>(r2, r3);
        r0.setColorFilter(r1);
        r0 = r6.emptyView;
        r1 = r6.emptyImageView;
        r2 = -2;
        r3 = -2;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3);
        r0.addView(r1, r2);
        r0 = new android.widget.TextView;
        r0.<init>(r7);
        r6.emptyTitleTextView = r0;
        r0 = r6.emptyTitleTextView;
        r1 = "dialogEmptyText";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
        r0 = r6.emptyTitleTextView;
        r1 = 17;
        r0.setGravity(r1);
        r0 = r6.emptyTitleTextView;
        r1 = "fonts/rmedium.ttf";
        r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1);
        r0.setTypeface(r1);
        r0 = r6.emptyTitleTextView;
        r1 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r2 = 1;
        r0.setTextSize(r2, r1);
        r0 = r6.emptyTitleTextView;
        r1 = NUM; // 0x7f0e06c5 float:1.8878552E38 double:1.053163013E-314;
        r2 = "NoPlacesFound";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
        r0 = r6.emptyView;
        r1 = r6.emptyTitleTextView;
        r19 = -2;
        r20 = -2;
        r21 = 17;
        r22 = 0;
        r23 = 11;
        r24 = 0;
        r25 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24, r25);
        r0.addView(r1, r2);
        r0 = new android.widget.TextView;
        r0.<init>(r7);
        r6.emptySubtitleTextView = r0;
        r0 = r6.emptySubtitleTextView;
        r1 = "dialogEmptyText";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
        r0 = r6.emptySubtitleTextView;
        r1 = 17;
        r0.setGravity(r1);
        r0 = r6.emptySubtitleTextView;
        r1 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r2 = 1;
        r0.setTextSize(r2, r1);
        r0 = r6.emptySubtitleTextView;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0.setPadding(r1, r8, r2, r8);
        r0 = r6.emptyView;
        r1 = r6.emptySubtitleTextView;
        r23 = 6;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24, r25);
        r0.addView(r1, r2);
        r0 = new org.telegram.ui.Components.RecyclerListView;
        r0.<init>(r7);
        r6.listView = r0;
        r11 = r6.listView;
        r13 = new org.telegram.ui.LocationActivity$8;
        r3 = r6.locationType;
        r4 = r6.dialogId;
        r0 = r13;
        r1 = r28;
        r2 = r29;
        r0.<init>(r2, r3, r4);
        r6.adapter = r13;
        r11.setAdapter(r13);
        r0 = r6.adapter;
        r1 = new org.telegram.ui.-$$Lambda$LocationActivity$vQq9_2ftulLlnR00VpfnDx9xUgQ;
        r1.<init>(r6);
        r0.setUpdateRunnable(r1);
        r0 = r6.listView;
        r0.setVerticalScrollBarEnabled(r8);
        r0 = r6.listView;
        r1 = new androidx.recyclerview.widget.LinearLayoutManager;
        r2 = 1;
        r1.<init>(r7, r2, r8);
        r6.layoutManager = r1;
        r0.setLayoutManager(r1);
        r0 = r6.listView;
        r1 = 51;
        r2 = -1;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r2, r1);
        r9.addView(r0, r1);
        r0 = r6.listView;
        r1 = new org.telegram.ui.LocationActivity$9;
        r1.<init>();
        r0.setOnScrollListener(r1);
        r0 = r6.listView;
        r0 = r0.getItemAnimator();
        r0 = (androidx.recyclerview.widget.DefaultItemAnimator) r0;
        r0.setDelayAnimations(r8);
        r0 = r6.listView;
        r1 = new org.telegram.ui.-$$Lambda$LocationActivity$M5nzDQeX7UhAugxqRzy_QyMzpvk;
        r1.<init>(r6);
        r0.setOnItemClickListener(r1);
        r0 = r6.adapter;
        r1 = r6.dialogId;
        r3 = new org.telegram.ui.-$$Lambda$LocationActivity$IPQvEgGc0E_OyZ6XMjHW9Pf7Ywc;
        r3.<init>(r6);
        r0.setDelegate(r1, r3);
        r0 = r6.adapter;
        r1 = r6.overScrollHeight;
        r0.setOverScrollHeight(r1);
        r0 = r6.mapViewClip;
        r1 = 51;
        r2 = -1;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r2, r1);
        r9.addView(r0, r1);
        r0 = new org.telegram.ui.LocationActivity$10;
        r0.<init>(r7);
        r6.mapView = r0;
        r0 = r6.mapView;
        r1 = new java.lang.Thread;
        r2 = new org.telegram.ui.-$$Lambda$LocationActivity$kYqmaD6kAR-ZgttPX_XR3SdS0fA;
        r2.<init>(r6, r0);
        r1.<init>(r2);
        r1.start();
        r0 = r6.messageObject;
        if (r0 != 0) goto L_0x08a9;
    L_0x07c2:
        r0 = r6.chatLocation;
        if (r0 != 0) goto L_0x08a9;
    L_0x07c6:
        r0 = r6.locationType;
        r1 = 4;
        if (r0 != r1) goto L_0x083c;
    L_0x07cb:
        r0 = r6.dialogId;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x083c;
    L_0x07d3:
        r0 = r28.getMessagesController();
        r1 = r6.dialogId;
        r2 = (int) r1;
        r1 = -r2;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        if (r0 == 0) goto L_0x083c;
    L_0x07e5:
        r1 = new android.widget.FrameLayout;
        r1.<init>(r7);
        r2 = NUM; // 0x7var_b float:1.7945314E38 double:1.0529356824E-314;
        r1.setBackgroundResource(r2);
        r2 = r6.mapViewClip;
        r3 = 62;
        r4 = 76;
        r5 = 49;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5);
        r2.addView(r1, r3);
        r2 = new org.telegram.ui.Components.BackupImageView;
        r2.<init>(r7);
        r3 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2.setRoundRadius(r3);
        r3 = org.telegram.messenger.ImageLocation.getForChat(r0, r8);
        r4 = new org.telegram.ui.Components.AvatarDrawable;
        r4.<init>(r0);
        r5 = "50_50";
        r2.setImage(r3, r5, r4, r0);
        r19 = 52;
        r20 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r21 = 51;
        r22 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r23 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r24 = 0;
        r25 = 0;
        r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r1.addView(r2, r0);
        r6.markerImageView = r1;
        r0 = r6.markerImageView;
        r1 = 1;
        r2 = java.lang.Integer.valueOf(r1);
        r0.setTag(r2);
    L_0x083c:
        r0 = r6.markerImageView;
        if (r0 != 0) goto L_0x085c;
    L_0x0840:
        r0 = new android.widget.ImageView;
        r0.<init>(r7);
        r1 = NUM; // 0x7var_e float:1.7945353E38 double:1.052935692E-314;
        r0.setImageResource(r1);
        r1 = r6.mapViewClip;
        r2 = 28;
        r3 = 48;
        r4 = 49;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4);
        r1.addView(r0, r2);
        r6.markerImageView = r0;
    L_0x085c:
        r0 = new org.telegram.ui.Components.RecyclerListView;
        r0.<init>(r7);
        r6.searchListView = r0;
        r0 = r6.searchListView;
        r1 = 8;
        r0.setVisibility(r1);
        r0 = r6.searchListView;
        r1 = new androidx.recyclerview.widget.LinearLayoutManager;
        r2 = 1;
        r1.<init>(r7, r2, r8);
        r0.setLayoutManager(r1);
        r0 = new org.telegram.ui.LocationActivity$11;
        r0.<init>(r7);
        r6.searchAdapter = r0;
        r0 = r6.searchAdapter;
        r1 = 0;
        r3 = new org.telegram.ui.-$$Lambda$LocationActivity$8aYke0hdtBzh2hbTqbL46wjKu2I;
        r3.<init>(r6);
        r0.setDelegate(r1, r3);
        r0 = r6.searchListView;
        r1 = 51;
        r2 = -1;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r2, r1);
        r9.addView(r0, r1);
        r0 = r6.searchListView;
        r1 = new org.telegram.ui.LocationActivity$12;
        r1.<init>();
        r0.setOnScrollListener(r1);
        r0 = r6.searchListView;
        r1 = new org.telegram.ui.-$$Lambda$LocationActivity$Zp7SEMyIdIb5wHzd7BCRAFKPc6Y;
        r1.<init>(r6);
        r0.setOnItemClickListener(r1);
        goto L_0x08ca;
    L_0x08a9:
        r0 = r6.messageObject;
        if (r0 == 0) goto L_0x08b3;
    L_0x08ad:
        r0 = r0.isLiveLocation();
        if (r0 == 0) goto L_0x08b7;
    L_0x08b3:
        r0 = r6.chatLocation;
        if (r0 == 0) goto L_0x08ca;
    L_0x08b7:
        r0 = r6.chatLocation;
        if (r0 == 0) goto L_0x08c1;
    L_0x08bb:
        r1 = r6.adapter;
        r1.setChatLocation(r0);
        goto L_0x08ca;
    L_0x08c1:
        r0 = r6.messageObject;
        if (r0 == 0) goto L_0x08ca;
    L_0x08c5:
        r1 = r6.adapter;
        r1.setMessageObject(r0);
    L_0x08ca:
        r0 = new org.telegram.ui.LocationActivity$13;
        r0.<init>(r7, r12);
        r6.shadow = r0;
        r0 = r6.mapViewClip;
        r1 = r6.shadow;
        r0.addView(r1, r14);
        r0 = r6.messageObject;
        if (r0 != 0) goto L_0x08fc;
    L_0x08dc:
        r0 = r6.chatLocation;
        if (r0 != 0) goto L_0x08fc;
    L_0x08e0:
        r0 = r6.initialLocation;
        if (r0 == 0) goto L_0x08fc;
    L_0x08e4:
        r0 = 1;
        r6.userLocationMoved = r0;
        r0 = r6.locationButton;
        r1 = new android.graphics.PorterDuffColorFilter;
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r3 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r1.<init>(r2, r3);
        r0.setColorFilter(r1);
        r0 = r6.locationButton;
        r0.setTag(r10);
    L_0x08fc:
        r0 = r6.actionBar;
        r9.addView(r0);
        r28.updateEmptyView();
        r0 = r6.fragmentView;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ void lambda$createView$0$LocationActivity(View view) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery(null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    public /* synthetic */ void lambda$createView$1$LocationActivity(int i) {
        GoogleMap googleMap = this.googleMap;
        if (googleMap != null) {
            if (i == 2) {
                googleMap.setMapType(1);
            } else if (i == 3) {
                googleMap.setMapType(2);
            } else if (i == 4) {
                googleMap.setMapType(4);
            }
        }
    }

    public /* synthetic */ void lambda$createView$2$LocationActivity(View view) {
        if (VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                showPermissionAlert(false);
                return;
            }
        }
        Location location;
        if (this.messageObject != null || this.chatLocation != null) {
            location = this.myLocation;
            if (location != null) {
                GoogleMap googleMap = this.googleMap;
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), this.myLocation.getLongitude()), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            }
        } else if (!(this.myLocation == null || this.googleMap == null)) {
            String str = "location_actionActiveIcon";
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.locationButton.setTag(str);
            this.adapter.setCustomLocation(null);
            this.userLocationMoved = false;
            showSearchPlacesButton(false);
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude())));
            if (this.searchedForCustomLocations) {
                location = this.myLocation;
                if (location != null) {
                    this.adapter.searchPlacesWithQuery(null, location, true, true);
                }
                this.searchedForCustomLocations = false;
                showResults();
            }
        }
        removeInfoView();
    }

    public /* synthetic */ void lambda$createView$9$LocationActivity(View view, int i) {
        int i2 = this.locationType;
        GoogleMap googleMap;
        GeoPoint geoPoint;
        if (i2 == 4) {
            if (i == 1) {
                TL_messageMediaVenue tL_messageMediaVenue = (TL_messageMediaVenue) this.adapter.getItem(i);
                if (tL_messageMediaVenue != null) {
                    if (this.dialogId == 0) {
                        this.delegate.didSelectLocation(tL_messageMediaVenue, 4, true, 0);
                        finishFragment();
                    } else {
                        AlertDialog[] alertDialogArr = new AlertDialog[]{new AlertDialog(getParentActivity(), 3)};
                        TL_channels_editLocation tL_channels_editLocation = new TL_channels_editLocation();
                        tL_channels_editLocation.address = tL_messageMediaVenue.address;
                        tL_channels_editLocation.channel = getMessagesController().getInputChannel(-((int) this.dialogId));
                        tL_channels_editLocation.geo_point = new TL_inputGeoPoint();
                        InputGeoPoint inputGeoPoint = tL_channels_editLocation.geo_point;
                        GeoPoint geoPoint2 = tL_messageMediaVenue.geo;
                        inputGeoPoint.lat = geoPoint2.lat;
                        inputGeoPoint._long = geoPoint2._long;
                        alertDialogArr[0].setOnCancelListener(new -$$Lambda$LocationActivity$4Qs8VB8sg7Y79R0AHdQQDCKXXQs(this, getConnectionsManager().sendRequest(tL_channels_editLocation, new -$$Lambda$LocationActivity$pKF2K0fO3wWfSkQU37TiB-NSoZU(this, alertDialogArr, tL_messageMediaVenue))));
                        showDialog(alertDialogArr[0]);
                    }
                }
            }
        } else if (i2 == 5) {
            googleMap = this.googleMap;
            if (googleMap != null) {
                geoPoint = this.chatLocation.geo_point;
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPoint.lat, geoPoint._long), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else {
            if (i == 1) {
                MessageObject messageObject = this.messageObject;
                if (!(messageObject == null || messageObject.isLiveLocation())) {
                    googleMap = this.googleMap;
                    if (googleMap != null) {
                        geoPoint = this.messageObject.messageOwner.media.geo;
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPoint.lat, geoPoint._long), this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                }
            }
            ChatActivity chatActivity;
            if (i != 1 || this.locationType == 2) {
                if ((i != 2 || this.locationType != 1) && ((i != 1 || this.locationType != 2) && (i != 3 || this.locationType != 3))) {
                    Object item = this.adapter.getItem(i);
                    if (item instanceof TL_messageMediaVenue) {
                        chatActivity = this.parentFragment;
                        if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                            this.delegate.didSelectLocation((TL_messageMediaVenue) item, this.locationType, true, 0);
                            finishFragment();
                        } else {
                            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new -$$Lambda$LocationActivity$8LFk62-zoz8sQd_DS2vu3SAn01w(this, item));
                        }
                    } else if (item instanceof LiveLocation) {
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation) item).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else if (getLocationController().isSharingLocation(this.dialogId)) {
                    getLocationController().removeSharingLocation(this.dialogId);
                    finishFragment();
                } else {
                    openShareLiveLocation();
                }
            } else if (!(this.delegate == null || this.userLocation == null)) {
                FrameLayout frameLayout = this.lastPressedMarkerView;
                if (frameLayout != null) {
                    frameLayout.callOnClick();
                } else {
                    TL_messageMediaGeo tL_messageMediaGeo = new TL_messageMediaGeo();
                    tL_messageMediaGeo.geo = new TL_geoPoint();
                    tL_messageMediaGeo.geo.lat = AndroidUtilities.fixLocationCoord(this.userLocation.getLatitude());
                    tL_messageMediaGeo.geo._long = AndroidUtilities.fixLocationCoord(this.userLocation.getLongitude());
                    chatActivity = this.parentFragment;
                    if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                        this.delegate.didSelectLocation(tL_messageMediaGeo, this.locationType, true, 0);
                        finishFragment();
                    } else {
                        AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new -$$Lambda$LocationActivity$FwVwaI9NXpsURHhDFK4wP9HRCiU(this, tL_messageMediaGeo));
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$5$LocationActivity(AlertDialog[] alertDialogArr, TL_messageMediaVenue tL_messageMediaVenue, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$ft2XuYls0FtkSPMyuazTNc8ZQ6A(this, alertDialogArr, tL_messageMediaVenue));
    }

    public /* synthetic */ void lambda$null$4$LocationActivity(AlertDialog[] alertDialogArr, TL_messageMediaVenue tL_messageMediaVenue) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        this.delegate.didSelectLocation(tL_messageMediaVenue, 4, true, 0);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$6$LocationActivity(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public /* synthetic */ void lambda$null$7$LocationActivity(TL_messageMediaGeo tL_messageMediaGeo, boolean z, int i) {
        this.delegate.didSelectLocation(tL_messageMediaGeo, this.locationType, z, i);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$8$LocationActivity(Object obj, boolean z, int i) {
        this.delegate.didSelectLocation((TL_messageMediaVenue) obj, this.locationType, z, i);
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$12$LocationActivity(MapView mapView) {
        try {
            mapView.onCreate(null);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$j4wbW7lBTlEHpEhyNf_bnX1w4i8(this, mapView));
    }

    public /* synthetic */ void lambda$null$11$LocationActivity(MapView mapView) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                mapView.onCreate(null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new -$$Lambda$LocationActivity$0SWiaT1qDbtEO0u6zOGsZ1uEPZM(this));
                this.mapsInitialized = true;
                if (this.onResumeCalled) {
                    this.mapView.onResume();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$null$10$LocationActivity(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, NUM));
        }
        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        onMapInit();
    }

    public /* synthetic */ void lambda$createView$13$LocationActivity(ArrayList arrayList) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    public /* synthetic */ void lambda$createView$15$LocationActivity(View view, int i) {
        TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item != null && this.delegate != null) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                this.delegate.didSelectLocation(item, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new -$$Lambda$LocationActivity$uYVE4mxsr1Ec3JIz7SEpIOVtso0(this, item));
        }
    }

    public /* synthetic */ void lambda$null$14$LocationActivity(TL_messageMediaVenue tL_messageMediaVenue, boolean z, int i) {
        this.delegate.didSelectLocation(tL_messageMediaVenue, this.locationType, z, i);
        finishFragment();
    }

    private boolean isActiveThemeDark() {
        boolean z = true;
        if (Theme.getActiveTheme().isDark()) {
            return true;
        }
        if (AndroidUtilities.computePerceivedBrightness(Theme.getColor("windowBackgroundWhite")) >= 0.721f) {
            z = false;
        }
        return z;
    }

    private void updateEmptyView() {
        if (!this.searching) {
            this.emptyView.setVisibility(8);
        } else if (this.searchInProgress) {
            this.searchListView.setEmptyView(null);
            this.emptyView.setVisibility(8);
            this.searchListView.setVisibility(8);
        } else {
            this.searchListView.setEmptyView(this.emptyView);
        }
    }

    /* JADX WARNING: Missing block: B:11:0x001d, code skipped:
            if (r2.distanceTo(r1) >= 300.0f) goto L_0x0020;
     */
    private void showSearchPlacesButton(boolean r7) {
        /*
        r6 = this;
        r0 = 0;
        if (r7 == 0) goto L_0x0020;
    L_0x0003:
        r1 = r6.searchAreaButton;
        if (r1 == 0) goto L_0x0020;
    L_0x0007:
        r1 = r1.getTag();
        if (r1 != 0) goto L_0x0020;
    L_0x000d:
        r1 = r6.myLocation;
        if (r1 == 0) goto L_0x001f;
    L_0x0011:
        r2 = r6.userLocation;
        if (r2 == 0) goto L_0x001f;
    L_0x0015:
        r1 = r2.distanceTo(r1);
        r2 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 >= 0) goto L_0x0020;
    L_0x001f:
        r7 = 0;
    L_0x0020:
        r1 = r6.searchAreaButton;
        if (r1 == 0) goto L_0x0076;
    L_0x0024:
        if (r7 == 0) goto L_0x002c;
    L_0x0026:
        r1 = r1.getTag();
        if (r1 != 0) goto L_0x0076;
    L_0x002c:
        if (r7 != 0) goto L_0x0037;
    L_0x002e:
        r1 = r6.searchAreaButton;
        r1 = r1.getTag();
        if (r1 != 0) goto L_0x0037;
    L_0x0036:
        goto L_0x0076;
    L_0x0037:
        r1 = r6.searchAreaButton;
        r2 = 1;
        if (r7 == 0) goto L_0x0041;
    L_0x003c:
        r3 = java.lang.Integer.valueOf(r2);
        goto L_0x0042;
    L_0x0041:
        r3 = 0;
    L_0x0042:
        r1.setTag(r3);
        r1 = new android.animation.AnimatorSet;
        r1.<init>();
        r3 = new android.animation.Animator[r2];
        r4 = r6.searchAreaButton;
        r5 = android.view.View.TRANSLATION_X;
        r2 = new float[r2];
        if (r7 == 0) goto L_0x0056;
    L_0x0054:
        r7 = 0;
        goto L_0x005e;
    L_0x0056:
        r7 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = -r7;
        r7 = (float) r7;
    L_0x005e:
        r2[r0] = r7;
        r7 = android.animation.ObjectAnimator.ofFloat(r4, r5, r2);
        r3[r0] = r7;
        r1.playTogether(r3);
        r2 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r1.setDuration(r2);
        r7 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT;
        r1.setInterpolator(r7);
        r1.start();
    L_0x0076:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.showSearchPlacesButton(boolean):void");
    }

    private Bitmap createUserBitmap(LiveLocation liveLocation) {
        Bitmap createBitmap;
        Throwable th;
        try {
            TLObject tLObject = (liveLocation.user == null || liveLocation.user.photo == null) ? (liveLocation.chat == null || liveLocation.chat.photo == null) ? null : liveLocation.chat.photo.photo_small : liveLocation.user.photo.photo_small;
            createBitmap = Bitmap.createBitmap(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f), Config.ARGB_8888);
            try {
                createBitmap.eraseColor(0);
                Canvas canvas = new Canvas(createBitmap);
                Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(NUM);
                drawable.setBounds(0, 0, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f));
                drawable.draw(canvas);
                Paint paint = new Paint(1);
                RectF rectF = new RectF();
                canvas.save();
                if (tLObject != null) {
                    Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(tLObject, true).toString());
                    if (decodeFile != null) {
                        BitmapShader bitmapShader = new BitmapShader(decodeFile, TileMode.CLAMP, TileMode.CLAMP);
                        Matrix matrix = new Matrix();
                        float dp = ((float) AndroidUtilities.dp(52.0f)) / ((float) decodeFile.getWidth());
                        matrix.postTranslate((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f));
                        matrix.postScale(dp, dp);
                        paint.setShader(bitmapShader);
                        bitmapShader.setLocalMatrix(matrix);
                        rectF.set((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(57.0f), (float) AndroidUtilities.dp(57.0f));
                        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(26.0f), (float) AndroidUtilities.dp(26.0f), paint);
                    }
                } else {
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    if (liveLocation.user != null) {
                        avatarDrawable.setInfo(liveLocation.user);
                    } else if (liveLocation.chat != null) {
                        avatarDrawable.setInfo(liveLocation.chat);
                    }
                    canvas.translate((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f));
                    avatarDrawable.setBounds(0, 0, AndroidUtilities.dp(52.2f), AndroidUtilities.dp(52.2f));
                    avatarDrawable.draw(canvas);
                }
                canvas.restore();
                try {
                    canvas.setBitmap(null);
                } catch (Exception unused) {
                }
            } catch (Throwable th2) {
                th = th2;
                FileLog.e(th);
                return createBitmap;
            }
        } catch (Throwable th3) {
            th = th3;
            createBitmap = null;
            FileLog.e(th);
            return createBitmap;
        }
        return createBitmap;
    }

    private int getMessageId(Message message) {
        int i = message.from_id;
        if (i != 0) {
            return i;
        }
        return (int) MessageObject.getDialogId(message);
    }

    private void openShareLiveLocation() {
        if (this.delegate != null && getParentActivity() != null && this.myLocation != null) {
            User user = null;
            if (((int) this.dialogId) > 0) {
                user = getMessagesController().getUser(Integer.valueOf((int) this.dialogId));
            }
            showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new -$$Lambda$LocationActivity$srTh8Ql0Vqy9r9VJ0JHRDDb8TWg(this)));
        }
    }

    public /* synthetic */ void lambda$openShareLiveLocation$16$LocationActivity(int i) {
        TL_messageMediaGeoLive tL_messageMediaGeoLive = new TL_messageMediaGeoLive();
        tL_messageMediaGeoLive.geo = new TL_geoPoint();
        tL_messageMediaGeoLive.geo.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        tL_messageMediaGeoLive.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        tL_messageMediaGeoLive.period = i;
        this.delegate.didSelectLocation(tL_messageMediaGeoLive, this.locationType, true, 0);
        finishFragment();
    }

    private Bitmap createPlaceBitmap(int i) {
        Bitmap[] bitmapArr = this.bitmapCache;
        int i2 = i % 7;
        if (bitmapArr[i2] != null) {
            return bitmapArr[i2];
        }
        try {
            Paint paint = new Paint(1);
            paint.setColor(-1);
            Bitmap createBitmap = Bitmap.createBitmap(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawCircle((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), paint);
            paint.setColor(LocationCell.getColorForIndex(i));
            canvas.drawCircle((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(5.0f), paint);
            canvas.setBitmap(null);
            this.bitmapCache[i % 7] = createBitmap;
            return createBitmap;
        } catch (Throwable th) {
            FileLog.e(th);
            return null;
        }
    }

    private void updatePlacesMarkers(ArrayList<TL_messageMediaVenue> arrayList) {
        if (arrayList != null) {
            int size = this.placeMarkers.size();
            for (int i = 0; i < size; i++) {
                ((VenueLocation) this.placeMarkers.get(i)).marker.remove();
            }
            this.placeMarkers.clear();
            size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                TL_messageMediaVenue tL_messageMediaVenue = (TL_messageMediaVenue) arrayList.get(i2);
                try {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(tL_messageMediaVenue.geo.lat, tL_messageMediaVenue.geo._long));
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createPlaceBitmap(i2)));
                    markerOptions.anchor(0.5f, 0.5f);
                    markerOptions.title(tL_messageMediaVenue.title);
                    markerOptions.snippet(tL_messageMediaVenue.address);
                    VenueLocation venueLocation = new VenueLocation();
                    venueLocation.num = i2;
                    venueLocation.marker = this.googleMap.addMarker(markerOptions);
                    venueLocation.venue = tL_messageMediaVenue;
                    venueLocation.marker.setTag(venueLocation);
                    this.placeMarkers.add(venueLocation);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    private LiveLocation addUserMarker(Message message) {
        GeoPoint geoPoint = message.media.geo;
        LatLng latLng = new LatLng(geoPoint.lat, geoPoint._long);
        LiveLocation liveLocation = (LiveLocation) this.markersMap.get(message.from_id);
        if (liveLocation == null) {
            liveLocation = new LiveLocation();
            liveLocation.object = message;
            if (liveLocation.object.from_id != 0) {
                liveLocation.user = getMessagesController().getUser(Integer.valueOf(liveLocation.object.from_id));
                liveLocation.id = liveLocation.object.from_id;
            } else {
                int dialogId = (int) MessageObject.getDialogId(message);
                if (dialogId > 0) {
                    liveLocation.user = getMessagesController().getUser(Integer.valueOf(dialogId));
                    liveLocation.id = dialogId;
                } else {
                    liveLocation.chat = getMessagesController().getChat(Integer.valueOf(-dialogId));
                    liveLocation.id = dialogId;
                }
            }
            try {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                Bitmap createUserBitmap = createUserBitmap(liveLocation);
                if (createUserBitmap != null) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createUserBitmap));
                    markerOptions.anchor(0.5f, 0.907f);
                    liveLocation.marker = this.googleMap.addMarker(markerOptions);
                    this.markers.add(liveLocation);
                    this.markersMap.put(liveLocation.id, liveLocation);
                    SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
                    if (liveLocation.id == getUserConfig().getClientUserId() && sharingLocationInfo != null && liveLocation.object.id == sharingLocationInfo.mid && this.myLocation != null) {
                        liveLocation.marker.setPosition(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            liveLocation.object = message;
            liveLocation.marker.setPosition(latLng);
        }
        return liveLocation;
    }

    private LiveLocation addUserMarker(TL_channelLocation tL_channelLocation) {
        GeoPoint geoPoint = tL_channelLocation.geo_point;
        LatLng latLng = new LatLng(geoPoint.lat, geoPoint._long);
        LiveLocation liveLocation = new LiveLocation();
        int i = (int) this.dialogId;
        if (i > 0) {
            liveLocation.user = getMessagesController().getUser(Integer.valueOf(i));
            liveLocation.id = i;
        } else {
            liveLocation.chat = getMessagesController().getChat(Integer.valueOf(-i));
            liveLocation.id = i;
        }
        try {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            Bitmap createUserBitmap = createUserBitmap(liveLocation);
            if (createUserBitmap != null) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createUserBitmap));
                markerOptions.anchor(0.5f, 0.907f);
                liveLocation.marker = this.googleMap.addMarker(markerOptions);
                this.markers.add(liveLocation);
                this.markersMap.put(liveLocation.id, liveLocation);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return liveLocation;
    }

    private void onMapInit() {
        if (this.googleMap != null) {
            TL_channelLocation tL_channelLocation = this.chatLocation;
            if (tL_channelLocation != null) {
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker(tL_channelLocation).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
            } else {
                MessageObject messageObject = this.messageObject;
                if (messageObject == null) {
                    this.userLocation = new Location("network");
                    tL_channelLocation = this.initialLocation;
                    if (tL_channelLocation != null) {
                        GeoPoint geoPoint = tL_channelLocation.geo_point;
                        LatLng latLng = new LatLng(geoPoint.lat, geoPoint._long);
                        GoogleMap googleMap = this.googleMap;
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, googleMap.getMaxZoomLevel() - 4.0f));
                        this.userLocation.setLatitude(this.initialLocation.geo_point.lat);
                        this.userLocation.setLongitude(this.initialLocation.geo_point._long);
                        this.adapter.setCustomLocation(this.userLocation);
                    } else {
                        this.userLocation.setLatitude(20.659322d);
                        this.userLocation.setLongitude(-11.40625d);
                    }
                } else if (messageObject.isLiveLocation()) {
                    LiveLocation addUserMarker = addUserMarker(this.messageObject.messageOwner);
                    if (!getRecentLocations()) {
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else {
                    LatLng latLng2 = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                    try {
                        GoogleMap googleMap2 = this.googleMap;
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng2);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(NUM));
                        googleMap2.addMarker(markerOptions);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, this.googleMap.getMaxZoomLevel() - 4.0f));
                    this.firstFocus = false;
                    getRecentLocations();
                }
            }
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnCameraMoveStartedListener(new -$$Lambda$LocationActivity$JeG4EpM3Wq4Gfm0e-jyA6gOwqcE(this));
            this.googleMap.setOnMyLocationChangeListener(new -$$Lambda$LocationActivity$qOvIualOdMBI8ayI-U81yeQXaOw(this));
            this.googleMap.setOnMarkerClickListener(new -$$Lambda$LocationActivity$T_dTqj2RNRahXSE26OYo3M1zUN4(this));
            this.googleMap.setOnCameraMoveListener(new -$$Lambda$LocationActivity$uoiU27jkFNklKKwS5dhfEkCoN4M(this));
            Location lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
            if (this.checkGpsEnabled && getParentActivity() != null) {
                this.checkGpsEnabled = false;
                if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                    try {
                        if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                            Builder builder = new Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("GpsDisabledAlertTitle", NUM));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new -$$Lambda$LocationActivity$DyZnCLfaZujX8t7RetY4mhXCK3w(this));
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                            showDialog(builder.create());
                        }
                    } catch (Exception e22) {
                        FileLog.e(e22);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$onMapInit$17$LocationActivity(int i) {
        if (i == 1) {
            showSearchPlacesButton(true);
            removeInfoView();
            if (!this.scrolling) {
                i = this.locationType;
                if ((i == 0 || i == 1) && this.listView.getChildCount() > 0) {
                    View childAt = this.listView.getChildAt(0);
                    if (childAt != null) {
                        ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(childAt);
                        if (findContainingViewHolder != null && findContainingViewHolder.getAdapterPosition() == 0) {
                            int dp = this.locationType == 0 ? 0 : AndroidUtilities.dp(66.0f);
                            i = childAt.getTop();
                            if (i < (-dp)) {
                                CameraPosition cameraPosition = this.googleMap.getCameraPosition();
                                this.forceUpdate = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom);
                                this.listView.smoothScrollBy(0, i + dp);
                            }
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$onMapInit$18$LocationActivity(Location location) {
        positionMarker(location);
        getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    public /* synthetic */ boolean lambda$onMapInit$19$LocationActivity(Marker marker) {
        if (!(marker.getTag() instanceof VenueLocation)) {
            return true;
        }
        this.markerImageView.setVisibility(4);
        if (!this.userLocationMoved) {
            String str = "location_actionIcon";
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            this.locationButton.setTag(str);
            this.userLocationMoved = true;
        }
        this.overlayView.addInfoView(marker);
        return true;
    }

    public /* synthetic */ void lambda$onMapInit$20$LocationActivity() {
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null) {
            mapOverlayView.updatePositions();
        }
    }

    public /* synthetic */ void lambda$onMapInit$21$LocationActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception unused) {
            }
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

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", NUM));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$LocationActivity$8UbzZ5ifzrfvxVGpxILU6iQpqg4(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$22$LocationActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2) {
            try {
                if (this.mapView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mapView.getParent()).removeView(this.mapView);
                }
            } catch (Exception unused) {
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
                    } catch (Exception unused2) {
                    }
                    this.mapViewClip.addView(this.overlayView, 1, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                }
                updateClipView();
                return;
            }
            View view = this.fragmentView;
            if (view != null) {
                ((FrameLayout) view).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
            }
        }
    }

    private void showResults() {
        if (this.adapter.getItemCount() != 0 && this.layoutManager.findFirstVisibleItemPosition() == 0) {
            int dp = AndroidUtilities.dp(258.0f) + this.listView.getChildAt(0).getTop();
            if (dp >= 0 && dp <= AndroidUtilities.dp(258.0f)) {
                this.listView.smoothScrollBy(0, dp);
            }
        }
    }

    private void updateClipView() {
        int y;
        int i;
        ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            y = (int) findViewHolderForAdapterPosition.itemView.getY();
            i = this.overScrollHeight + (y < 0 ? y : 0);
        } else {
            y = -this.mapViewClip.getMeasuredHeight();
            i = 0;
        }
        if (((LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
            MapOverlayView mapOverlayView;
            if (i <= 0) {
                if (this.mapView.getVisibility() == 0) {
                    this.mapView.setVisibility(4);
                    this.mapViewClip.setVisibility(4);
                    MapOverlayView mapOverlayView2 = this.overlayView;
                    if (mapOverlayView2 != null) {
                        mapOverlayView2.setVisibility(4);
                    }
                }
            } else if (this.mapView.getVisibility() == 4) {
                this.mapView.setVisibility(0);
                this.mapViewClip.setVisibility(0);
                mapOverlayView = this.overlayView;
                if (mapOverlayView != null) {
                    mapOverlayView.setVisibility(0);
                }
            }
            this.mapViewClip.setTranslationY((float) Math.min(0, y));
            y = -y;
            int i2 = y / 2;
            this.mapView.setTranslationY((float) Math.max(0, i2));
            mapOverlayView = this.overlayView;
            if (mapOverlayView != null) {
                mapOverlayView.setTranslationY((float) Math.max(0, i2));
            }
            int measuredHeight = this.overScrollHeight - this.mapTypeButton.getMeasuredHeight();
            int i3 = this.locationType;
            i3 = (i3 == 0 || i3 == 1) ? 30 : 10;
            float min = (float) Math.min(measuredHeight - AndroidUtilities.dp((float) (64 + i3)), y);
            this.mapTypeButton.setTranslationY(min);
            SearchButton searchButton = this.searchAreaButton;
            if (searchButton != null) {
                searchButton.setTranslation(min);
            }
            View view = this.markerImageView;
            if (view != null) {
                y = (y - AndroidUtilities.dp(view.getTag() == null ? 48.0f : 69.0f)) + (i / 2);
                this.markerTop = y;
                view.setTranslationY((float) y);
            }
            LayoutParams layoutParams = (LayoutParams) this.mapView.getLayoutParams();
            if (!(layoutParams == null || layoutParams.height == this.overScrollHeight + AndroidUtilities.dp(10.0f))) {
                layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                GoogleMap googleMap = this.googleMap;
                if (googleMap != null) {
                    googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                }
                this.mapView.setLayoutParams(layoutParams);
            }
            MapOverlayView mapOverlayView3 = this.overlayView;
            if (mapOverlayView3 != null) {
                layoutParams = (LayoutParams) mapOverlayView3.getLayoutParams();
                if (layoutParams != null && layoutParams.height != this.overScrollHeight + AndroidUtilities.dp(10.0f)) {
                    layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    this.overlayView.setLayoutParams(layoutParams);
                }
            }
        }
    }

    private void fixLayoutInternal(boolean z) {
        if (this.listView != null) {
            int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int measuredHeight = this.fragmentView.getMeasuredHeight();
            if (measuredHeight != 0) {
                if (this.locationType == 2) {
                    this.overScrollHeight = (measuredHeight - AndroidUtilities.dp(73.0f)) - currentActionBarHeight;
                } else {
                    this.overScrollHeight = (measuredHeight - AndroidUtilities.dp(66.0f)) - currentActionBarHeight;
                }
                LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.mapViewClip.getLayoutParams();
                layoutParams.topMargin = currentActionBarHeight;
                layoutParams.height = this.overScrollHeight;
                this.mapViewClip.setLayoutParams(layoutParams);
                RecyclerListView recyclerListView = this.searchListView;
                if (recyclerListView != null) {
                    layoutParams = (LayoutParams) recyclerListView.getLayoutParams();
                    layoutParams.topMargin = currentActionBarHeight;
                    this.searchListView.setLayoutParams(layoutParams);
                }
                this.adapter.setOverScrollHeight(this.overScrollHeight);
                LayoutParams layoutParams2 = (LayoutParams) this.mapView.getLayoutParams();
                if (layoutParams2 != null) {
                    layoutParams2.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    GoogleMap googleMap = this.googleMap;
                    if (googleMap != null) {
                        googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams2);
                }
                MapOverlayView mapOverlayView = this.overlayView;
                if (mapOverlayView != null) {
                    layoutParams2 = (LayoutParams) mapOverlayView.getLayoutParams();
                    if (layoutParams2 != null) {
                        layoutParams2.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                        this.overlayView.setLayoutParams(layoutParams2);
                    }
                }
                this.adapter.notifyDataSetChanged();
                if (z) {
                    int i = this.locationType;
                    i = i == 3 ? 73 : (i == 1 || i == 2) ? 66 : 0;
                    this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) i));
                    updateClipView();
                    this.listView.post(new -$$Lambda$LocationActivity$vFndq5_S07zuMd7iX_GTmFqv9no(this, i));
                } else {
                    updateClipView();
                }
            }
        }
    }

    public /* synthetic */ void lambda$fixLayoutInternal$23$LocationActivity(int i) {
        this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) i));
        updateClipView();
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
            LiveLocation liveLocation = (LiveLocation) this.markersMap.get(getUserConfig().getClientUserId());
            SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (!(liveLocation == null || sharingLocationInfo == null || liveLocation.object.id != sharingLocationInfo.mid)) {
                liveLocation.marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            if (this.messageObject == null && this.chatLocation == null && this.googleMap != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                LocationActivityAdapter locationActivityAdapter = this.adapter;
                if (locationActivityAdapter != null) {
                    if (!(this.searchedForCustomLocations || this.locationType == 4)) {
                        locationActivityAdapter.searchPlacesWithQuery(null, this.myLocation, true);
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
            } else {
                this.adapter.setGpsLocation(this.myLocation);
            }
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        this.messageObject = messageObject;
        this.dialogId = this.messageObject.getDialogId();
    }

    public void setChatLocation(int i, TL_channelLocation tL_channelLocation) {
        this.dialogId = (long) (-i);
        this.chatLocation = tL_channelLocation;
    }

    public void setDialogId(long j) {
        this.dialogId = j;
    }

    public void setInitialLocation(TL_channelLocation tL_channelLocation) {
        this.initialLocation = tL_channelLocation;
    }

    private static LatLng move(LatLng latLng, double d, double d2) {
        d2 = meterToLongitude(d2, latLng.latitude);
        return new LatLng(latLng.latitude + meterToLatitude(d), latLng.longitude + d2);
    }

    private static double meterToLongitude(double d, double d2) {
        return Math.toDegrees(d / (Math.cos(Math.toRadians(d2)) * 6366198.0d));
    }

    private static double meterToLatitude(double d) {
        return Math.toDegrees(d / 6366198.0d);
    }

    private void fetchRecentLocations(ArrayList<Message> arrayList) {
        LatLngBounds.Builder builder = this.firstFocus ? new LatLngBounds.Builder() : null;
        int currentTime = getConnectionsManager().getCurrentTime();
        for (int i = 0; i < arrayList.size(); i++) {
            Message message = (Message) arrayList.get(i);
            int i2 = message.date;
            MessageMedia messageMedia = message.media;
            if (i2 + messageMedia.period > currentTime) {
                if (builder != null) {
                    GeoPoint geoPoint = messageMedia.geo;
                    builder.include(new LatLng(geoPoint.lat, geoPoint._long));
                }
                addUserMarker(message);
            }
        }
        if (builder != null) {
            this.firstFocus = false;
            this.adapter.setLiveLocations(this.markers);
            if (this.messageObject.isLiveLocation()) {
                try {
                    LatLng center = builder.build().getCenter();
                    LatLng move = move(center, 100.0d, 100.0d);
                    builder.include(move(center, -100.0d, -100.0d));
                    builder.include(move);
                    LatLngBounds build = builder.build();
                    if (arrayList.size() > 1) {
                        try {
                            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(build, AndroidUtilities.dp(60.0f)));
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                } catch (Exception unused) {
                }
            }
        }
    }

    private boolean getRecentLocations() {
        ArrayList arrayList = (ArrayList) getLocationController().locationsCache.get(this.messageObject.getDialogId());
        if (arrayList == null || !arrayList.isEmpty()) {
            arrayList = null;
        } else {
            fetchRecentLocations(arrayList);
        }
        int i = (int) this.dialogId;
        boolean z = false;
        if (i < 0) {
            Chat chat = getMessagesController().getChat(Integer.valueOf(-i));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TL_messages_getRecentLocations tL_messages_getRecentLocations = new TL_messages_getRecentLocations();
        long dialogId = this.messageObject.getDialogId();
        tL_messages_getRecentLocations.peer = getMessagesController().getInputPeer((int) dialogId);
        tL_messages_getRecentLocations.limit = 100;
        getConnectionsManager().sendRequest(tL_messages_getRecentLocations, new -$$Lambda$LocationActivity$mUtxBXce22gm9LBjol2U5VYR5cg(this, dialogId));
        if (arrayList != null) {
            z = true;
        }
        return z;
    }

    public /* synthetic */ void lambda$getRecentLocations$26$LocationActivity(long j, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$Jw5MCrQO-YzrfsOLkQBuDWM8ZN8(this, tLObject, j));
        }
    }

    public /* synthetic */ void lambda$null$25$LocationActivity(TLObject tLObject, long j) {
        if (this.googleMap != null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            int i = 0;
            while (i < messages_messages.messages.size()) {
                if (!(((Message) messages_messages.messages.get(i)).media instanceof TL_messageMediaGeoLive)) {
                    messages_messages.messages.remove(i);
                    i--;
                }
                i++;
            }
            getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            getMessagesController().putUsers(messages_messages.users, false);
            getMessagesController().putChats(messages_messages.chats, false);
            getLocationController().locationsCache.put(j, messages_messages.messages);
            getNotificationCenter().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(j));
            fetchRecentLocations(messages_messages.messages);
            getLocationController().markLiveLoactionsAsRead(this.dialogId);
            if (this.markAsReadRunnable == null) {
                this.markAsReadRunnable = new -$$Lambda$LocationActivity$8eI0rX6uf3PbyfhaoHRzQHHkcG4(this);
                AndroidUtilities.runOnUIThread(this.markAsReadRunnable, 5000);
            }
        }
    }

    public /* synthetic */ void lambda$null$24$LocationActivity() {
        getLocationController().markLiveLoactionsAsRead(this.dialogId);
        if (!this.isPaused) {
            Runnable runnable = this.markAsReadRunnable;
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable, 5000);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.locationPermissionGranted) {
            GoogleMap googleMap = this.googleMap;
            if (googleMap != null) {
                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } else {
            int i3 = 0;
            LocationActivityAdapter locationActivityAdapter;
            if (i == NotificationCenter.didReceiveNewMessages) {
                if (!((Boolean) objArr[2]).booleanValue() && ((Long) objArr[0]).longValue() == this.dialogId && this.messageObject != null) {
                    ArrayList arrayList = (ArrayList) objArr[1];
                    Object obj = null;
                    while (i3 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(i3);
                        if (messageObject.isLiveLocation()) {
                            addUserMarker(messageObject.messageOwner);
                            obj = 1;
                        }
                        i3++;
                    }
                    if (obj != null) {
                        locationActivityAdapter = this.adapter;
                        if (locationActivityAdapter != null) {
                            locationActivityAdapter.setLiveLocations(this.markers);
                        }
                    }
                }
            } else if (i == NotificationCenter.replaceMessagesObjects) {
                long longValue = ((Long) objArr[0]).longValue();
                if (longValue == this.dialogId && this.messageObject != null) {
                    ArrayList arrayList2 = (ArrayList) objArr[1];
                    Object obj2 = null;
                    while (i3 < arrayList2.size()) {
                        MessageObject messageObject2 = (MessageObject) arrayList2.get(i3);
                        if (messageObject2.isLiveLocation()) {
                            LiveLocation liveLocation = (LiveLocation) this.markersMap.get(getMessageId(messageObject2.messageOwner));
                            if (liveLocation != null) {
                                SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(longValue);
                                if (sharingLocationInfo == null || sharingLocationInfo.mid != messageObject2.getId()) {
                                    Message message = messageObject2.messageOwner;
                                    liveLocation.object = message;
                                    Marker marker = liveLocation.marker;
                                    GeoPoint geoPoint = message.media.geo;
                                    marker.setPosition(new LatLng(geoPoint.lat, geoPoint._long));
                                }
                                obj2 = 1;
                            }
                        }
                        i3++;
                    }
                    if (obj2 != null) {
                        locationActivityAdapter = this.adapter;
                        if (locationActivityAdapter != null) {
                            locationActivityAdapter.updateLiveLocations();
                        }
                    }
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onPause();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = false;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            try {
                mapView.onResume();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        this.onResumeCalled = true;
        GoogleMap googleMap = this.googleMap;
        if (googleMap != null) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (Exception e) {
                FileLog.e(e);
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
        Runnable runnable = this.markAsReadRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            AndroidUtilities.runOnUIThread(this.markAsReadRunnable, 5000);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        MapView mapView = this.mapView;
        if (mapView != null && this.mapsInitialized) {
            mapView.onLowMemory();
        }
    }

    public void setDelegate(LocationActivityDelegate locationActivityDelegate) {
        this.delegate = locationActivityDelegate;
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

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$LocationActivity$IZD5Tgwx4r4Oclwb3ty-v8H-B6o -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o = new -$$Lambda$LocationActivity$IZD5Tgwx4r4Oclwb3ty-v8H-B6o(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[66];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o, "dialogBackground");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "dialogBackground");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "dialogButtonSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "chat_messagePanelHint");
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptionArr[8] = new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "dialogTextBlack");
        -$$Lambda$LocationActivity$IZD5Tgwx4r4Oclwb3ty-v8H-B6o -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2 = -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o;
        themeDescriptionArr[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "actionBarDefaultSubmenuBackground");
        themeDescriptionArr[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "actionBarDefaultSubmenuItem");
        themeDescriptionArr[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "actionBarDefaultSubmenuItemIcon");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[14] = new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "dialogEmptyImage");
        themeDescriptionArr[15] = new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "dialogEmptyText");
        themeDescriptionArr[16] = new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "dialogEmptyText");
        themeDescriptionArr[17] = new ThemeDescription(this.shadow, 0, null, null, null, null, "key_sheet_scrollUp");
        themeDescriptionArr[18] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "location_actionIcon");
        themeDescriptionArr[19] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "location_actionActiveIcon");
        themeDescriptionArr[20] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "location_actionBackground");
        themeDescriptionArr[21] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "location_actionPressedBackground");
        themeDescriptionArr[22] = new ThemeDescription(this.mapTypeButton, 0, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "location_actionIcon");
        themeDescriptionArr[23] = new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "location_actionBackground");
        themeDescriptionArr[24] = new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "location_actionPressedBackground");
        themeDescriptionArr[25] = new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "location_actionActiveIcon");
        themeDescriptionArr[26] = new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "location_actionBackground");
        themeDescriptionArr[27] = new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "location_actionPressedBackground");
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_savedDrawable}, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "avatar_text");
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "avatar_backgroundRed");
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "avatar_backgroundOrange");
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "avatar_backgroundViolet");
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "avatar_backgroundGreen");
        themeDescriptionArr[33] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "avatar_backgroundCyan");
        themeDescriptionArr[34] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "avatar_backgroundBlue");
        themeDescriptionArr[35] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_izd5tgwx4r4oclwb3ty-v8h-b6o2, "avatar_backgroundPink");
        themeDescriptionArr[36] = new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress");
        themeDescriptionArr[37] = new ThemeDescription(null, 0, null, null, null, null, "location_placeLocationBackground");
        themeDescriptionArr[38] = new ThemeDescription(null, 0, null, null, null, null, "dialog_liveLocationProgress");
        View view = this.listView;
        int i = ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG;
        Class[] clsArr = new Class[]{SendLocationCell.class};
        String[] strArr = new String[1];
        strArr[0] = "imageView";
        themeDescriptionArr[39] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "location_sendLocationIcon");
        themeDescriptionArr[40] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLiveLocationIcon");
        themeDescriptionArr[41] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLocationBackground");
        themeDescriptionArr[42] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLiveLocationBackground");
        themeDescriptionArr[43] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        view = this.listView;
        i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{SendLocationCell.class};
        strArr = new String[1];
        strArr[0] = "titleTextView";
        themeDescriptionArr[44] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "location_sendLiveLocationText");
        themeDescriptionArr[45] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, null, null, null, "location_sendLocationText");
        themeDescriptionArr[46] = new ThemeDescription(this.listView, 0, new Class[]{LocationDirectionCell.class}, new String[]{"buttonTextView"}, null, null, null, "featuredStickers_buttonText");
        view = this.listView;
        i = ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE;
        clsArr = new Class[]{LocationDirectionCell.class};
        strArr = new String[1];
        strArr[0] = "frameLayout";
        themeDescriptionArr[47] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "featuredStickers_addButton");
        themeDescriptionArr[48] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, null, null, null, "featuredStickers_addButtonPressed");
        themeDescriptionArr[49] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[50] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        view = this.listView;
        clsArr = new Class[]{HeaderCell.class};
        strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[51] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "dialogTextBlue2");
        themeDescriptionArr[52] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        view = this.listView;
        clsArr = new Class[]{LocationCell.class};
        strArr = new String[1];
        strArr[0] = "nameTextView";
        themeDescriptionArr[53] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{LocationCell.class};
        strArr = new String[1];
        strArr[0] = "addressTextView";
        themeDescriptionArr[54] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[55] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[56] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[57] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[58] = new ThemeDescription(this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[59] = new ThemeDescription(this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"distanceTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[60] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle");
        themeDescriptionArr[61] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[62] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[63] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[64] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, null, null, null, "dialogEmptyImage");
        themeDescriptionArr[65] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, null, null, null, "dialogEmptyText");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$27$LocationActivity() {
        this.mapTypeButton.setIconColor(Theme.getColor("location_actionIcon"));
        this.mapTypeButton.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), Mode.MULTIPLY));
        this.shadow.invalidate();
        if (this.googleMap == null) {
            return;
        }
        if (isActiveThemeDark()) {
            if (!this.currentMapStyleDark) {
                this.currentMapStyleDark = true;
                this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, NUM));
            }
        } else if (this.currentMapStyleDark) {
            this.currentMapStyleDark = false;
            this.googleMap.setMapStyle(null);
        }
    }
}
