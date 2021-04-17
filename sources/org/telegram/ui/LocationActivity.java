package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$GeoPoint;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$TL_channelLocation;
import org.telegram.tgnet.TLRPC$TL_channels_editLocation;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_geoPoint;
import org.telegram.tgnet.TLRPC$TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_messages_getRecentLocations;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.BaseLocationAdapter;
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
import org.telegram.ui.LocationActivity;

public class LocationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public LocationActivityAdapter adapter;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private int askWithRadius;
    private Bitmap[] bitmapCache = new Bitmap[7];
    private boolean canUndo;
    /* access modifiers changed from: private */
    public TLRPC$TL_channelLocation chatLocation;
    private boolean checkBackgroundPermission = true;
    private boolean checkGpsEnabled = true;
    private boolean checkPermission = true;
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
    private TLRPC$TL_channelLocation initialLocation;
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
    private SparseArray<LiveLocation> markersMap = new SparseArray<>();
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
    /* access modifiers changed from: private */
    public float yOffset;

    public static class LiveLocation {
        public TLRPC$Chat chat;
        public Marker directionMarker;
        public boolean hasRotation;
        public int id;
        public Marker marker;
        public TLRPC$Message object;
        public TLRPC$User user;
    }

    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2);
    }

    public static class VenueLocation {
        public Marker marker;
        public int num;
        public TLRPC$TL_messageMediaVenue venue;
    }

    static /* synthetic */ boolean lambda$createView$7(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
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

    public class MapOverlayView extends FrameLayout {
        private HashMap<Marker, View> views = new HashMap<>();

        public MapOverlayView(Context context) {
            super(context);
        }

        public void addInfoView(Marker marker) {
            Marker marker2 = marker;
            VenueLocation venueLocation = (VenueLocation) marker.getTag();
            if (venueLocation != null && LocationActivity.this.lastPressedVenue != venueLocation) {
                LocationActivity.this.showSearchPlacesButton(false);
                if (LocationActivity.this.lastPressedMarker != null) {
                    removeInfoView(LocationActivity.this.lastPressedMarker);
                    Marker unused = LocationActivity.this.lastPressedMarker = null;
                }
                VenueLocation unused2 = LocationActivity.this.lastPressedVenue = venueLocation;
                Marker unused3 = LocationActivity.this.lastPressedMarker = marker2;
                Context context = getContext();
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createFrame(-2, 114.0f));
                FrameLayout unused4 = LocationActivity.this.lastPressedMarkerView = new FrameLayout(context);
                LocationActivity.this.lastPressedMarkerView.setBackgroundResource(NUM);
                LocationActivity.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
                frameLayout.addView(LocationActivity.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
                LocationActivity.this.lastPressedMarkerView.setAlpha(0.0f);
                LocationActivity.this.lastPressedMarkerView.setOnClickListener(new View.OnClickListener(venueLocation) {
                    public final /* synthetic */ LocationActivity.VenueLocation f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        LocationActivity.MapOverlayView.this.lambda$addInfoView$1$LocationActivity$MapOverlayView(this.f$1, view);
                    }
                });
                TextView textView = new TextView(context);
                textView.setTextSize(1, 16.0f);
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine(true);
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                int i = 5;
                textView.setGravity(LocaleController.isRTL ? 5 : 3);
                LocationActivity.this.lastPressedMarkerView.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 10.0f, 18.0f, 0.0f));
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 14.0f);
                textView2.setMaxLines(1);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
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
                backupImageView.setImage("https://ss3.4sqi.net/img/categories_v2/" + venueLocation.venue.venue_type + "_64.png", (String) null, (Drawable) null);
                frameLayout2.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    private final float[] animatorValues = {0.0f, 1.0f};
                    private boolean startedInner;

                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float f;
                        float lerp = AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction());
                        if (lerp >= 0.7f && !this.startedInner && LocationActivity.this.lastPressedMarkerView != null) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.SCALE_Y, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(LocationActivity.this.lastPressedMarkerView, View.ALPHA, new float[]{0.0f, 1.0f})});
                            animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
                            animatorSet.setDuration(250);
                            animatorSet.start();
                            this.startedInner = true;
                        }
                        if (lerp <= 0.5f) {
                            f = CubicBezierInterpolator.EASE_OUT.getInterpolation(lerp / 0.5f) * 1.1f;
                        } else if (lerp <= 0.75f) {
                            f = 1.1f - (CubicBezierInterpolator.EASE_OUT.getInterpolation((lerp - 0.5f) / 0.25f) * 0.2f);
                        } else {
                            f = (CubicBezierInterpolator.EASE_OUT.getInterpolation((lerp - 0.75f) / 0.25f) * 0.1f) + 0.9f;
                        }
                        frameLayout2.setScaleX(f);
                        frameLayout2.setScaleY(f);
                    }
                });
                ofFloat.setDuration(360);
                ofFloat.start();
                this.views.put(marker2, frameLayout);
                LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, (GoogleMap.CancelableCallback) null);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$addInfoView$1 */
        public /* synthetic */ void lambda$addInfoView$1$LocationActivity$MapOverlayView(VenueLocation venueLocation, View view) {
            if (LocationActivity.this.parentFragment == null || !LocationActivity.this.parentFragment.isInScheduleMode()) {
                LocationActivity.this.delegate.didSelectLocation(venueLocation.venue, LocationActivity.this.locationType, true, 0);
                LocationActivity.this.finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(LocationActivity.this.getParentActivity(), LocationActivity.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(venueLocation) {
                public final /* synthetic */ LocationActivity.VenueLocation f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectDate(boolean z, int i) {
                    LocationActivity.MapOverlayView.this.lambda$null$0$LocationActivity$MapOverlayView(this.f$1, z, i);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$LocationActivity$MapOverlayView(VenueLocation venueLocation, boolean z, int i) {
            LocationActivity.this.delegate.didSelectLocation(venueLocation.venue, LocationActivity.this.locationType, z, i);
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
                for (Map.Entry next : this.views.entrySet()) {
                    View view = (View) next.getValue();
                    Point screenLocation = projection.toScreenLocation(((Marker) next.getKey()).getPosition());
                    view.setTranslationX((float) (screenLocation.x - (view.getMeasuredWidth() / 2)));
                    view.setTranslationY((float) ((screenLocation.y - view.getMeasuredHeight()) + AndroidUtilities.dp(22.0f)));
                }
            }
        }
    }

    public LocationActivity(int i) {
        this.locationType = i;
        AndroidUtilities.fixGoogleMapsBug();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
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
            UndoView undoView2 = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = undoView2;
            undoView2.hide(true, 2);
            this.mapViewClip.removeView(this.undoView[0]);
            this.mapViewClip.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public View createView(Context context) {
        FrameLayout.LayoutParams layoutParams;
        FrameLayout frameLayout;
        String str;
        FrameLayout.LayoutParams layoutParams2;
        int i;
        int i2;
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
            public void onItemClick(int i) {
                if (i == -1) {
                    LocationActivity.this.finishFragment();
                } else if (i == 1) {
                    try {
                        double d = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                        double d2 = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                        Activity parentActivity = LocationActivity.this.getParentActivity();
                        parentActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + d + "," + d2 + "?q=" + d + "," + d2)));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else if (i == 5) {
                    LocationActivity.this.openShareLiveLocation(0);
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.chatLocation != null) {
            this.actionBar.setTitle(LocaleController.getString("ChatLocation", NUM));
        } else {
            MessageObject messageObject2 = this.messageObject;
            if (messageObject2 == null) {
                this.actionBar.setTitle(LocaleController.getString("ShareLocation", NUM));
                if (this.locationType != 4) {
                    this.overlayView = new MapOverlayView(context2);
                    ActionBarMenuItem addItem = createMenu.addItem(0, NUM);
                    addItem.setIsSearchField(true);
                    addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                                String obj = editText.getText().toString();
                                boolean z = false;
                                if (obj.length() != 0) {
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
                                LocationActivity.this.searchAdapter.searchDelayed(obj, LocationActivity.this.userLocation);
                            }
                        }
                    });
                    this.searchItem = addItem;
                    addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
                    this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
                    EditTextBoldCursor searchField = this.searchItem.getSearchField();
                    searchField.setTextColor(Theme.getColor("dialogTextBlack"));
                    searchField.setCursorColor(Theme.getColor("dialogTextBlack"));
                    searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
                }
            } else if (messageObject2.isLiveLocation()) {
                this.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", NUM));
            } else {
                String str2 = this.messageObject.messageOwner.media.title;
                if (str2 == null || str2.length() <= 0) {
                    this.actionBar.setTitle(LocaleController.getString("ChatLocation", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SharedPlace", NUM));
                }
                ActionBarMenuItem addItem2 = createMenu.addItem(0, NUM);
                this.otherItem = addItem2;
                addItem2.addSubItem(1, NUM, LocaleController.getString("OpenInExternalApp", NUM));
                if (!getLocationController().isSharingLocation(this.dialogId)) {
                    this.otherItem.addSubItem(5, NUM, LocaleController.getString("SendLiveLocationMenu", NUM));
                }
                this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            }
        }
        AnonymousClass3 r0 = new FrameLayout(context2) {
            private boolean first = true;

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (z) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                    return;
                }
                LocationActivity.this.updateClipView(true);
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == LocationActivity.this.actionBar && LocationActivity.this.parentLayout != null) {
                    LocationActivity.this.parentLayout.drawHeaderShadow(canvas, LocationActivity.this.actionBar.getMeasuredHeight());
                }
                return drawChild;
            }
        };
        this.fragmentView = r0;
        FrameLayout frameLayout2 = r0;
        r0.setBackgroundColor(Theme.getColor("dialogBackground"));
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        final Rect rect = new Rect();
        this.shadowDrawable.getPadding(rect);
        int i3 = this.locationType;
        if (i3 == 0 || i3 == 1) {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f) + rect.top);
        } else {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(6.0f) + rect.top);
        }
        FrameLayout.LayoutParams layoutParams3 = layoutParams;
        layoutParams3.gravity = 83;
        AnonymousClass4 r02 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (LocationActivity.this.overlayView != null) {
                    LocationActivity.this.overlayView.updatePositions();
                }
            }
        };
        this.mapViewClip = r02;
        r02.setBackgroundDrawable(new MapPlaceholderDrawable());
        FrameLayout.LayoutParams layoutParams4 = layoutParams3;
        if (this.messageObject == null && ((i2 = this.locationType) == 0 || i2 == 1)) {
            SearchButton searchButton = new SearchButton(context2);
            this.searchAreaButton = searchButton;
            searchButton.setTranslationX((float) (-AndroidUtilities.dp(80.0f)));
            Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
            int i4 = Build.VERSION.SDK_INT;
            if (i4 < 21) {
                Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
                mutate2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate2, createSimpleSelectorRoundRectDrawable, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
                combinedDrawable.setFullsize(true);
                createSimpleSelectorRoundRectDrawable = combinedDrawable;
            } else {
                StateListAnimator stateListAnimator = new StateListAnimator();
                SearchButton searchButton2 = this.searchAreaButton;
                Property property = View.TRANSLATION_Z;
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(searchButton2, property, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.searchAreaButton, property, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.searchAreaButton.setStateListAnimator(stateListAnimator);
                this.searchAreaButton.setOutlineProvider(new ViewOutlineProvider(this) {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) (view.getMeasuredHeight() / 2));
                    }
                });
            }
            this.searchAreaButton.setBackgroundDrawable(createSimpleSelectorRoundRectDrawable);
            this.searchAreaButton.setTextColor(Theme.getColor("location_actionActiveIcon"));
            this.searchAreaButton.setTextSize(1, 14.0f);
            this.searchAreaButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.searchAreaButton.setText(LocaleController.getString("PlacesInThisArea", NUM));
            this.searchAreaButton.setGravity(17);
            this.searchAreaButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.mapViewClip.addView(this.searchAreaButton, LayoutHelper.createFrame(-2, i4 >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
            this.searchAreaButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    LocationActivity.this.lambda$createView$0$LocationActivity(view);
                }
            });
        }
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, 0, Theme.getColor("location_actionIcon"));
        this.mapTypeButton = actionBarMenuItem;
        actionBarMenuItem.setClickable(true);
        this.mapTypeButton.setSubMenuOpenSide(2);
        this.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        this.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        this.mapTypeButton.addSubItem(2, NUM, LocaleController.getString("Map", NUM));
        this.mapTypeButton.addSubItem(3, NUM, LocaleController.getString("Satellite", NUM));
        this.mapTypeButton.addSubItem(4, NUM, LocaleController.getString("Hybrid", NUM));
        this.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        int i5 = Build.VERSION.SDK_INT;
        if (i5 < 21) {
            Drawable mutate3 = context.getResources().getDrawable(NUM).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(mutate3, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable2.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable2;
            str = "location_actionIcon";
            frameLayout = frameLayout2;
        } else {
            StateListAnimator stateListAnimator2 = new StateListAnimator();
            ActionBarMenuItem actionBarMenuItem2 = this.mapTypeButton;
            Property property2 = View.TRANSLATION_Z;
            str = "location_actionIcon";
            stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(actionBarMenuItem2, property2, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            frameLayout = frameLayout2;
            stateListAnimator2.addState(new int[0], ObjectAnimator.ofFloat(this.mapTypeButton, property2, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.mapTypeButton.setStateListAnimator(stateListAnimator2);
            this.mapTypeButton.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.mapTypeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.mapTypeButton.setIcon(NUM);
        this.mapViewClip.addView(this.mapTypeButton, LayoutHelper.createFrame(i5 >= 21 ? 40 : 44, i5 >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        this.mapTypeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                LocationActivity.this.lambda$createView$1$LocationActivity(view);
            }
        });
        this.mapTypeButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
            public final void onItemClick(int i) {
                LocationActivity.this.lambda$createView$2$LocationActivity(i);
            }
        });
        this.locationButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (i5 < 21) {
            Drawable mutate4 = context.getResources().getDrawable(NUM).mutate();
            mutate4.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(mutate4, createSimpleSelectorCircleDrawable2, 0, 0);
            combinedDrawable3.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable2 = combinedDrawable3;
        } else {
            StateListAnimator stateListAnimator3 = new StateListAnimator();
            ImageView imageView = this.locationButton;
            Property property3 = View.TRANSLATION_Z;
            stateListAnimator3.addState(new int[]{16842919}, ObjectAnimator.ofFloat(imageView, property3, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator3.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, property3, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.locationButton.setStateListAnimator(stateListAnimator3);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.locationButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable2);
        this.locationButton.setImageResource(NUM);
        this.locationButton.setScaleType(ImageView.ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
        this.locationButton.setTag("location_actionActiveIcon");
        this.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", NUM));
        FrameLayout.LayoutParams createFrame = LayoutHelper.createFrame(i5 >= 21 ? 40 : 44, i5 >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f);
        FrameLayout.LayoutParams layoutParams5 = layoutParams4;
        createFrame.bottomMargin += layoutParams5.height - rect.top;
        this.mapViewClip.addView(this.locationButton, createFrame);
        this.locationButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                LocationActivity.this.lambda$createView$3$LocationActivity(view);
            }
        });
        this.proximityButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable3 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (i5 < 21) {
            Drawable mutate5 = context.getResources().getDrawable(NUM).mutate();
            mutate5.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable4 = new CombinedDrawable(mutate5, createSimpleSelectorCircleDrawable3, 0, 0);
            combinedDrawable4.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable3 = combinedDrawable4;
            layoutParams2 = layoutParams5;
        } else {
            StateListAnimator stateListAnimator4 = new StateListAnimator();
            ImageView imageView2 = this.proximityButton;
            Property property4 = View.TRANSLATION_Z;
            layoutParams2 = layoutParams5;
            stateListAnimator4.addState(new int[]{16842919}, ObjectAnimator.ofFloat(imageView2, property4, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator4.addState(new int[0], ObjectAnimator.ofFloat(this.proximityButton, property4, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.proximityButton.setStateListAnimator(stateListAnimator4);
            this.proximityButton.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.proximityButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
        this.proximityButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable3);
        this.proximityButton.setScaleType(ImageView.ScaleType.CENTER);
        this.proximityButton.setContentDescription(LocaleController.getString("AccDescrLocationNotify", NUM));
        this.mapViewClip.addView(this.proximityButton, LayoutHelper.createFrame(i5 >= 21 ? 40 : 44, i5 >= 21 ? 40.0f : 44.0f, 53, 0.0f, 62.0f, 12.0f, 0.0f));
        this.proximityButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                LocationActivity.this.lambda$createView$6$LocationActivity(view);
            }
        });
        TLRPC$Chat tLRPC$Chat = null;
        if (((int) this.dialogId) < 0) {
            tLRPC$Chat = getMessagesController().getChat(Integer.valueOf(-((int) this.dialogId)));
        }
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        MessageObject messageObject3 = this.messageObject;
        if (messageObject3 == null || !messageObject3.isLiveLocation() || this.messageObject.isExpiredLiveLocation(getConnectionsManager().getCurrentTime()) || (ChatObject.isChannel(tLRPC$Chat2) && !tLRPC$Chat2.megagroup)) {
            this.proximityButton.setVisibility(8);
            this.proximityButton.setImageResource(NUM);
        } else {
            LocationController.SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (sharingLocationInfo == null || sharingLocationInfo.proximityMeters <= 0) {
                if (((int) this.dialogId) > 0 && this.messageObject.getFromChatId() == getUserConfig().getClientUserId()) {
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
        FrameLayout frameLayout3 = frameLayout;
        frameLayout3.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener($$Lambda$LocationActivity$u7MvSmMkgdFfduHBmEIeEkTxpcg.INSTANCE);
        ImageView imageView3 = new ImageView(context2);
        this.emptyImageView = imageView3;
        imageView3.setImageResource(NUM);
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
        AnonymousClass9 r9 = r0;
        AnonymousClass9 r03 = new LocationActivityAdapter(context, this.locationType, this.dialogId, false) {
            /* access modifiers changed from: protected */
            public void onDirectionClick() {
                Intent intent;
                Activity parentActivity;
                if (Build.VERSION.SDK_INT >= 23 && (parentActivity = LocationActivity.this.getParentActivity()) != null && parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
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
        this.adapter = r9;
        recyclerListView.setAdapter(r9);
        this.adapter.setUpdateRunnable(new Runnable() {
            public final void run() {
                LocationActivity.this.lambda$createView$8$LocationActivity();
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout3.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                boolean unused = LocationActivity.this.scrolling = i != 0;
                if (!LocationActivity.this.scrolling && LocationActivity.this.forceUpdate != null) {
                    CameraUpdate unused2 = LocationActivity.this.forceUpdate = null;
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                LocationActivity.this.updateClipView(false);
                if (LocationActivity.this.forceUpdate != null) {
                    LocationActivity locationActivity = LocationActivity.this;
                    float unused = locationActivity.yOffset = locationActivity.yOffset + ((float) i2);
                }
            }
        });
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                LocationActivity.this.lambda$createView$14$LocationActivity(view, i);
            }
        });
        this.adapter.setDelegate(this.dialogId, new BaseLocationAdapter.BaseLocationAdapterDelegate() {
            public final void didLoadSearchResult(ArrayList arrayList) {
                LocationActivity.this.updatePlacesMarkers(arrayList);
            }
        });
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout3.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        AnonymousClass11 r04 = new MapView(context2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return super.onTouchEvent(motionEvent);
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                MotionEvent motionEvent2;
                if (LocationActivity.this.yOffset != 0.0f) {
                    motionEvent = MotionEvent.obtain(motionEvent);
                    motionEvent.offsetLocation(0.0f, (-LocationActivity.this.yOffset) / 2.0f);
                    motionEvent2 = motionEvent;
                } else {
                    motionEvent2 = null;
                }
                boolean dispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
                if (motionEvent2 != null) {
                    motionEvent2.recycle();
                }
                return dispatchTouchEvent;
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (LocationActivity.this.messageObject == null && LocationActivity.this.chatLocation == null) {
                    if (motionEvent.getAction() == 0) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        AnimatorSet unused = LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        LocationActivity.this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) (LocationActivity.this.markerTop - AndroidUtilities.dp(10.0f))})});
                        LocationActivity.this.animatorSet.start();
                    } else if (motionEvent.getAction() == 1) {
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
                    if (motionEvent.getAction() == 2) {
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
                return super.onInterceptTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        LocationActivity.AnonymousClass11.this.lambda$onLayout$0$LocationActivity$11();
                    }
                });
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onLayout$0 */
            public /* synthetic */ void lambda$onLayout$0$LocationActivity$11() {
                if (LocationActivity.this.moveToBounds != null) {
                    LocationActivity.this.googleMap.moveCamera(LocationActivity.this.moveToBounds);
                    CameraUpdate unused = LocationActivity.this.moveToBounds = null;
                }
            }
        };
        this.mapView = r04;
        new Thread(new Runnable(r04) {
            public final /* synthetic */ MapView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationActivity.this.lambda$createView$17$LocationActivity(this.f$1);
            }
        }).start();
        MessageObject messageObject4 = this.messageObject;
        if (messageObject4 == null && this.chatLocation == null) {
            if (!(tLRPC$Chat2 == null || this.locationType != 4 || this.dialogId == 0)) {
                FrameLayout frameLayout4 = new FrameLayout(context2);
                frameLayout4.setBackgroundResource(NUM);
                this.mapViewClip.addView(frameLayout4, LayoutHelper.createFrame(62, 76, 49));
                BackupImageView backupImageView = new BackupImageView(context2);
                backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
                backupImageView.setForUserOrChat(tLRPC$Chat2, new AvatarDrawable(tLRPC$Chat2));
                frameLayout4.addView(backupImageView, LayoutHelper.createFrame(52, 52.0f, 51, 5.0f, 5.0f, 0.0f, 0.0f));
                this.markerImageView = frameLayout4;
                frameLayout4.setTag(1);
            }
            if (this.markerImageView == null) {
                ImageView imageView4 = new ImageView(context2);
                imageView4.setImageResource(NUM);
                this.mapViewClip.addView(imageView4, LayoutHelper.createFrame(28, 48, 49));
                this.markerImageView = imageView4;
            }
            RecyclerListView recyclerListView3 = new RecyclerListView(context2);
            this.searchListView = recyclerListView3;
            recyclerListView3.setVisibility(8);
            i = 0;
            this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            AnonymousClass12 r05 = new LocationActivitySearchAdapter(context2) {
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
            this.searchAdapter = r05;
            r05.setDelegate(0, new BaseLocationAdapter.BaseLocationAdapterDelegate() {
                public final void didLoadSearchResult(ArrayList arrayList) {
                    LocationActivity.this.lambda$createView$18$LocationActivity(arrayList);
                }
            });
            frameLayout3.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    LocationActivity.this.lambda$createView$20$LocationActivity(view, i);
                }
            });
        } else {
            i = 0;
            if ((messageObject4 != null && !messageObject4.isLiveLocation()) || this.chatLocation != null) {
                TLRPC$TL_channelLocation tLRPC$TL_channelLocation = this.chatLocation;
                if (tLRPC$TL_channelLocation != null) {
                    this.adapter.setChatLocation(tLRPC$TL_channelLocation);
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
        while (i < 2) {
            this.undoView[i] = new UndoView(context2);
            this.undoView[i].setAdditionalTranslationY((float) AndroidUtilities.dp(10.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.undoView[i].setTranslationZ((float) AndroidUtilities.dp(5.0f));
            }
            this.mapViewClip.addView(this.undoView[i], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
            i++;
        }
        AnonymousClass14 r06 = new View(context2) {
            private RectF rect = new RectF();

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                LocationActivity.this.shadowDrawable.setBounds(-rect.left, 0, getMeasuredWidth() + rect.right, getMeasuredHeight());
                LocationActivity.this.shadowDrawable.draw(canvas);
                if (LocationActivity.this.locationType == 0 || LocationActivity.this.locationType == 1) {
                    int dp = AndroidUtilities.dp(36.0f);
                    int dp2 = rect.top + AndroidUtilities.dp(10.0f);
                    this.rect.set((float) ((getMeasuredWidth() - dp) / 2), (float) dp2, (float) ((getMeasuredWidth() + dp) / 2), (float) (dp2 + AndroidUtilities.dp(4.0f)));
                    int color = Theme.getColor("key_sheet_scrollUp");
                    Color.alpha(color);
                    Theme.dialogs_onlineCirclePaint.setColor(color);
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
            }
        };
        this.shadow = r06;
        if (Build.VERSION.SDK_INT >= 21) {
            r06.setTranslationZ((float) AndroidUtilities.dp(6.0f));
        }
        this.mapViewClip.addView(this.shadow, layoutParams2);
        if (this.messageObject == null && this.chatLocation == null && this.initialLocation != null) {
            this.userLocationMoved = true;
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag(str);
        }
        frameLayout3.addView(this.actionBar);
        updateEmptyView();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$0 */
    public /* synthetic */ void lambda$createView$0$LocationActivity(View view) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery((String) null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ void lambda$createView$1$LocationActivity(View view) {
        this.mapTypeButton.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$LocationActivity(int i) {
        GoogleMap googleMap2 = this.googleMap;
        if (googleMap2 != null) {
            if (i == 2) {
                googleMap2.setMapType(1);
            } else if (i == 3) {
                googleMap2.setMapType(2);
            } else if (i == 4) {
                googleMap2.setMapType(4);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$LocationActivity(View view) {
        GoogleMap googleMap2;
        Activity parentActivity;
        if (Build.VERSION.SDK_INT >= 23 && (parentActivity = getParentActivity()) != null && parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$LocationActivity(View view) {
        if (getParentActivity() != null && this.myLocation != null && checkGpsEnabled() && this.googleMap != null) {
            HintView hintView2 = this.hintView;
            if (hintView2 != null) {
                hintView2.hide();
            }
            MessagesController.getGlobalMainSettings().edit().putInt("proximityhint", 3).commit();
            LocationController.SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (this.canUndo) {
                this.undoView[0].hide(true, 1);
            }
            if (sharingLocationInfo == null || sharingLocationInfo.proximityMeters <= 0) {
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
            getUndoView().showWithAction(0, 25, 0, (Object) null, new Runnable() {
                public final void run() {
                    LocationActivity.this.lambda$null$4$LocationActivity();
                }
            }, new Runnable(sharingLocationInfo) {
                public final /* synthetic */ LocationController.SharingLocationInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationActivity.this.lambda$null$5$LocationActivity(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$LocationActivity() {
        getLocationController().setProximityLocation(this.dialogId, 0, true);
        this.canUndo = false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$LocationActivity(LocationController.SharingLocationInfo sharingLocationInfo) {
        this.proximityButton.setImageResource(NUM);
        createCircle(sharingLocationInfo.proximityMeters);
        this.canUndo = false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$LocationActivity() {
        updateClipView(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$14 */
    public /* synthetic */ void lambda$createView$14$LocationActivity(View view, int i) {
        MessageObject messageObject2;
        TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue;
        int i2 = this.locationType;
        if (i2 == 4) {
            if (i == 1 && (tLRPC$TL_messageMediaVenue = (TLRPC$TL_messageMediaVenue) this.adapter.getItem(i)) != null) {
                if (this.dialogId == 0) {
                    this.delegate.didSelectLocation(tLRPC$TL_messageMediaVenue, 4, true, 0);
                    finishFragment();
                    return;
                }
                AlertDialog[] alertDialogArr = {new AlertDialog(getParentActivity(), 3)};
                TLRPC$TL_channels_editLocation tLRPC$TL_channels_editLocation = new TLRPC$TL_channels_editLocation();
                tLRPC$TL_channels_editLocation.address = tLRPC$TL_messageMediaVenue.address;
                tLRPC$TL_channels_editLocation.channel = getMessagesController().getInputChannel(-((int) this.dialogId));
                TLRPC$TL_inputGeoPoint tLRPC$TL_inputGeoPoint = new TLRPC$TL_inputGeoPoint();
                tLRPC$TL_channels_editLocation.geo_point = tLRPC$TL_inputGeoPoint;
                TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$TL_messageMediaVenue.geo;
                tLRPC$TL_inputGeoPoint.lat = tLRPC$GeoPoint.lat;
                tLRPC$TL_inputGeoPoint._long = tLRPC$GeoPoint._long;
                alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(tLRPC$TL_channels_editLocation, new RequestDelegate(alertDialogArr, tLRPC$TL_messageMediaVenue) {
                    public final /* synthetic */ AlertDialog[] f$1;
                    public final /* synthetic */ TLRPC$TL_messageMediaVenue f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        LocationActivity.this.lambda$null$10$LocationActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                })) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        LocationActivity.this.lambda$null$11$LocationActivity(this.f$1, dialogInterface);
                    }
                });
                showDialog(alertDialogArr[0]);
            }
        } else if (i2 == 5) {
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                TLRPC$GeoPoint tLRPC$GeoPoint2 = this.chatLocation.geo_point;
                googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tLRPC$GeoPoint2.lat, tLRPC$GeoPoint2._long), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (i == 1 && (messageObject2 = this.messageObject) != null && (!messageObject2.isLiveLocation() || this.locationType == 6)) {
            GoogleMap googleMap3 = this.googleMap;
            if (googleMap3 != null) {
                TLRPC$GeoPoint tLRPC$GeoPoint3 = this.messageObject.messageOwner.media.geo;
                googleMap3.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tLRPC$GeoPoint3.lat, tLRPC$GeoPoint3._long), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (i != 1 || this.locationType == 2) {
            if ((i != 2 || this.locationType != 1) && ((i != 1 || this.locationType != 2) && (i != 3 || this.locationType != 3))) {
                Object item = this.adapter.getItem(i);
                if (item instanceof TLRPC$TL_messageMediaVenue) {
                    ChatActivity chatActivity = this.parentFragment;
                    if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                        this.delegate.didSelectLocation((TLRPC$TL_messageMediaVenue) item, this.locationType, true, 0);
                        finishFragment();
                        return;
                    }
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(item) {
                        public final /* synthetic */ Object f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            LocationActivity.this.lambda$null$13$LocationActivity(this.f$1, z, i);
                        }
                    });
                } else if (item instanceof LiveLocation) {
                    this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation) item).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
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
            TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo = new TLRPC$TL_messageMediaGeo();
            TLRPC$TL_geoPoint tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint();
            tLRPC$TL_messageMediaGeo.geo = tLRPC$TL_geoPoint;
            tLRPC$TL_geoPoint.lat = AndroidUtilities.fixLocationCoord(this.userLocation.getLatitude());
            tLRPC$TL_messageMediaGeo.geo._long = AndroidUtilities.fixLocationCoord(this.userLocation.getLongitude());
            ChatActivity chatActivity2 = this.parentFragment;
            if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
                this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeo, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(tLRPC$TL_messageMediaGeo) {
                public final /* synthetic */ TLRPC$TL_messageMediaGeo f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectDate(boolean z, int i) {
                    LocationActivity.this.lambda$null$12$LocationActivity(this.f$1, z, i);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$LocationActivity(AlertDialog[] alertDialogArr, TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr, tLRPC$TL_messageMediaVenue) {
            public final /* synthetic */ AlertDialog[] f$1;
            public final /* synthetic */ TLRPC$TL_messageMediaVenue f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LocationActivity.this.lambda$null$9$LocationActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$9 */
    public /* synthetic */ void lambda$null$9$LocationActivity(AlertDialog[] alertDialogArr, TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaVenue, 4, true, 0);
        finishFragment();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$11 */
    public /* synthetic */ void lambda$null$11$LocationActivity(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$12 */
    public /* synthetic */ void lambda$null$12$LocationActivity(TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo, boolean z, int i) {
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeo, this.locationType, z, i);
        finishFragment();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$13 */
    public /* synthetic */ void lambda$null$13$LocationActivity(Object obj, boolean z, int i) {
        this.delegate.didSelectLocation((TLRPC$TL_messageMediaVenue) obj, this.locationType, z, i);
        finishFragment();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$17 */
    public /* synthetic */ void lambda$createView$17$LocationActivity(MapView mapView2) {
        try {
            mapView2.onCreate((Bundle) null);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new Runnable(mapView2) {
            public final /* synthetic */ MapView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationActivity.this.lambda$null$16$LocationActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$16 */
    public /* synthetic */ void lambda$null$16$LocationActivity(MapView mapView2) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                mapView2.onCreate((Bundle) null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new OnMapReadyCallback() {
                    public final void onMapReady(GoogleMap googleMap) {
                        LocationActivity.this.lambda$null$15$LocationActivity(googleMap);
                    }
                });
                this.mapsInitialized = true;
                if (this.onResumeCalled) {
                    this.mapView.onResume();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$15 */
    public /* synthetic */ void lambda$null$15$LocationActivity(GoogleMap googleMap2) {
        this.googleMap = googleMap2;
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, NUM));
        }
        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        onMapInit();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$18 */
    public /* synthetic */ void lambda$createView$18$LocationActivity(ArrayList arrayList) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$20 */
    public /* synthetic */ void lambda$createView$20$LocationActivity(View view, int i) {
        TLRPC$TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item != null && this.delegate != null) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                this.delegate.didSelectLocation(item, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(item) {
                public final /* synthetic */ TLRPC$TL_messageMediaVenue f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectDate(boolean z, int i) {
                    LocationActivity.this.lambda$null$19$LocationActivity(this.f$1, z, i);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$19 */
    public /* synthetic */ void lambda$null$19$LocationActivity(TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, boolean z, int i) {
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaVenue, this.locationType, z, i);
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
    public void showSearchPlacesButton(boolean z) {
        SearchButton searchButton;
        Location location;
        Location location2;
        if (z && (searchButton = this.searchAreaButton) != null && searchButton.getTag() == null && ((location = this.myLocation) == null || (location2 = this.userLocation) == null || location2.distanceTo(location) < 300.0f)) {
            z = false;
        }
        SearchButton searchButton2 = this.searchAreaButton;
        if (searchButton2 == null) {
            return;
        }
        if (z && searchButton2.getTag() != null) {
            return;
        }
        if (z || this.searchAreaButton.getTag() != null) {
            this.searchAreaButton.setTag(z ? 1 : null);
            AnimatorSet animatorSet2 = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            SearchButton searchButton3 = this.searchAreaButton;
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            fArr[0] = z ? 0.0f : (float) (-AndroidUtilities.dp(80.0f));
            animatorArr[0] = ObjectAnimator.ofFloat(searchButton3, property, fArr);
            animatorSet2.playTogether(animatorArr);
            animatorSet2.setDuration(180);
            animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet2.start();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0010, code lost:
        r1 = r1.photo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.Bitmap createUserBitmap(org.telegram.ui.LocationActivity.LiveLocation r11) {
        /*
            r10 = this;
            r0 = 0
            org.telegram.tgnet.TLRPC$User r1 = r11.user     // Catch:{ all -> 0x00ff }
            if (r1 == 0) goto L_0x000c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r1.photo     // Catch:{ all -> 0x00ff }
            if (r1 == 0) goto L_0x000c
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ all -> 0x00ff }
            goto L_0x0018
        L_0x000c:
            org.telegram.tgnet.TLRPC$Chat r1 = r11.chat     // Catch:{ all -> 0x00ff }
            if (r1 == 0) goto L_0x0017
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r1.photo     // Catch:{ all -> 0x00ff }
            if (r1 == 0) goto L_0x0017
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ all -> 0x00ff }
            goto L_0x0018
        L_0x0017:
            r1 = r0
        L_0x0018:
            r2 = 1115160576(0x42780000, float:62.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x00ff }
            r4 = 1118437376(0x42aa0000, float:85.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x00ff }
            android.graphics.Bitmap$Config r6 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x00ff }
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r3, r5, r6)     // Catch:{ all -> 0x00ff }
            r5 = 0
            r3.eraseColor(r5)     // Catch:{ all -> 0x00fc }
            android.graphics.Canvas r6 = new android.graphics.Canvas     // Catch:{ all -> 0x00fc }
            r6.<init>(r3)     // Catch:{ all -> 0x00fc }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x00fc }
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ all -> 0x00fc }
            r8 = 2131165611(0x7var_ab, float:1.7945444E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)     // Catch:{ all -> 0x00fc }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x00fc }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x00fc }
            r7.setBounds(r5, r5, r2, r4)     // Catch:{ all -> 0x00fc }
            r7.draw(r6)     // Catch:{ all -> 0x00fc }
            android.graphics.Paint r2 = new android.graphics.Paint     // Catch:{ all -> 0x00fc }
            r4 = 1
            r2.<init>(r4)     // Catch:{ all -> 0x00fc }
            android.graphics.RectF r7 = new android.graphics.RectF     // Catch:{ all -> 0x00fc }
            r7.<init>()     // Catch:{ all -> 0x00fc }
            r6.save()     // Catch:{ all -> 0x00fc }
            r8 = 1112014848(0x42480000, float:50.0)
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r1 == 0) goto L_0x00c6
            java.io.File r11 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r4)     // Catch:{ all -> 0x00fc }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x00fc }
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeFile(r11)     // Catch:{ all -> 0x00fc }
            if (r11 == 0) goto L_0x00f5
            android.graphics.BitmapShader r1 = new android.graphics.BitmapShader     // Catch:{ all -> 0x00fc }
            android.graphics.Shader$TileMode r4 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x00fc }
            r1.<init>(r11, r4, r4)     // Catch:{ all -> 0x00fc }
            android.graphics.Matrix r4 = new android.graphics.Matrix     // Catch:{ all -> 0x00fc }
            r4.<init>()     // Catch:{ all -> 0x00fc }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x00fc }
            float r5 = (float) r5     // Catch:{ all -> 0x00fc }
            int r11 = r11.getWidth()     // Catch:{ all -> 0x00fc }
            float r11 = (float) r11     // Catch:{ all -> 0x00fc }
            float r5 = r5 / r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00fc }
            float r11 = (float) r11     // Catch:{ all -> 0x00fc }
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00fc }
            float r8 = (float) r8     // Catch:{ all -> 0x00fc }
            r4.postTranslate(r11, r8)     // Catch:{ all -> 0x00fc }
            r4.postScale(r5, r5)     // Catch:{ all -> 0x00fc }
            r2.setShader(r1)     // Catch:{ all -> 0x00fc }
            r1.setLocalMatrix(r4)     // Catch:{ all -> 0x00fc }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00fc }
            float r11 = (float) r11     // Catch:{ all -> 0x00fc }
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00fc }
            float r1 = (float) r1     // Catch:{ all -> 0x00fc }
            r4 = 1113587712(0x42600000, float:56.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x00fc }
            float r5 = (float) r5     // Catch:{ all -> 0x00fc }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x00fc }
            float r4 = (float) r4     // Catch:{ all -> 0x00fc }
            r7.set(r11, r1, r5, r4)     // Catch:{ all -> 0x00fc }
            r11 = 1103626240(0x41CLASSNAME, float:25.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x00fc }
            float r1 = (float) r1     // Catch:{ all -> 0x00fc }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x00fc }
            float r11 = (float) r11     // Catch:{ all -> 0x00fc }
            r6.drawRoundRect(r7, r1, r11, r2)     // Catch:{ all -> 0x00fc }
            goto L_0x00f5
        L_0x00c6:
            org.telegram.ui.Components.AvatarDrawable r1 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x00fc }
            r1.<init>()     // Catch:{ all -> 0x00fc }
            org.telegram.tgnet.TLRPC$User r2 = r11.user     // Catch:{ all -> 0x00fc }
            if (r2 == 0) goto L_0x00d3
            r1.setInfo((org.telegram.tgnet.TLRPC$User) r2)     // Catch:{ all -> 0x00fc }
            goto L_0x00da
        L_0x00d3:
            org.telegram.tgnet.TLRPC$Chat r11 = r11.chat     // Catch:{ all -> 0x00fc }
            if (r11 == 0) goto L_0x00da
            r1.setInfo((org.telegram.tgnet.TLRPC$Chat) r11)     // Catch:{ all -> 0x00fc }
        L_0x00da:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00fc }
            float r11 = (float) r11     // Catch:{ all -> 0x00fc }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00fc }
            float r2 = (float) r2     // Catch:{ all -> 0x00fc }
            r6.translate(r11, r2)     // Catch:{ all -> 0x00fc }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x00fc }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x00fc }
            r1.setBounds(r5, r5, r11, r2)     // Catch:{ all -> 0x00fc }
            r1.draw(r6)     // Catch:{ all -> 0x00fc }
        L_0x00f5:
            r6.restore()     // Catch:{ all -> 0x00fc }
            r6.setBitmap(r0)     // Catch:{ Exception -> 0x0104 }
            goto L_0x0104
        L_0x00fc:
            r11 = move-exception
            r0 = r3
            goto L_0x0100
        L_0x00ff:
            r11 = move-exception
        L_0x0100:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            r3 = r0
        L_0x0104:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.createUserBitmap(org.telegram.ui.LocationActivity$LiveLocation):android.graphics.Bitmap");
    }

    private int getMessageId(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message.from_id != null) {
            return MessageObject.getFromChatId(tLRPC$Message);
        }
        return (int) MessageObject.getDialogId(tLRPC$Message);
    }

    private void openProximityAlert() {
        Circle circle = this.proximityCircle;
        if (circle == null) {
            createCircle(500);
        } else {
            this.previousRadius = circle.getRadius();
        }
        TLRPC$User user = ((int) this.dialogId) > 0 ? getMessagesController().getUser(Integer.valueOf((int) this.dialogId)) : null;
        ProximitySheet proximitySheet2 = new ProximitySheet(getParentActivity(), user, new ProximitySheet.onRadiusPickerChange() {
            public final boolean run(boolean z, int i) {
                return LocationActivity.this.lambda$openProximityAlert$21$LocationActivity(z, i);
            }
        }, new ProximitySheet.onRadiusPickerChange(user) {
            public final /* synthetic */ TLRPC$User f$1;

            {
                this.f$1 = r2;
            }

            public final boolean run(boolean z, int i) {
                return LocationActivity.this.lambda$openProximityAlert$23$LocationActivity(this.f$1, z, i);
            }
        }, new Runnable() {
            public final void run() {
                LocationActivity.this.lambda$openProximityAlert$24$LocationActivity();
            }
        });
        this.proximitySheet = proximitySheet2;
        ((FrameLayout) this.fragmentView).addView(proximitySheet2, LayoutHelper.createFrame(-1, -1.0f));
        this.proximitySheet.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openProximityAlert$21 */
    public /* synthetic */ boolean lambda$openProximityAlert$21$LocationActivity(boolean z, int i) {
        Circle circle = this.proximityCircle;
        if (circle != null) {
            circle.setRadius((double) i);
            if (z) {
                moveToBounds(i, true, true);
            }
        }
        if (((int) this.dialogId) < 0) {
            return true;
        }
        int size = this.markers.size();
        for (int i2 = 0; i2 < size; i2++) {
            LiveLocation liveLocation = this.markers.get(i2);
            if (liveLocation.object != null && !UserObject.isUserSelf(liveLocation.user)) {
                TLRPC$GeoPoint tLRPC$GeoPoint = liveLocation.object.media.geo;
                Location location = new Location("network");
                location.setLatitude(tLRPC$GeoPoint.lat);
                location.setLongitude(tLRPC$GeoPoint._long);
                if (this.myLocation.distanceTo(location) > ((float) i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openProximityAlert$23 */
    public /* synthetic */ boolean lambda$openProximityAlert$23$LocationActivity(TLRPC$User tLRPC$User, boolean z, int i) {
        if (getLocationController().getSharingLocationInfo(this.dialogId) == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("ShareLocationAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("ShareLocationAlertText", NUM));
            builder.setPositiveButton(LocaleController.getString("ShareLocationAlertButton", NUM), new DialogInterface.OnClickListener(tLRPC$User, i) {
                public final /* synthetic */ TLRPC$User f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    LocationActivity.this.lambda$null$22$LocationActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
            return false;
        }
        this.proximitySheet.setRadiusSet();
        this.proximityButton.setImageResource(NUM);
        getUndoView().showWithAction(0, 24, Integer.valueOf(i), tLRPC$User, (Runnable) null, (Runnable) null);
        getLocationController().setProximityLocation(this.dialogId, i, true);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$22 */
    public /* synthetic */ void lambda$null$22$LocationActivity(TLRPC$User tLRPC$User, int i, DialogInterface dialogInterface, int i2) {
        lambda$openShareLiveLocation$26(tLRPC$User, 900, i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openProximityAlert$24 */
    public /* synthetic */ void lambda$openProximityAlert$24$LocationActivity() {
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
    public void openShareLiveLocation(int i) {
        Activity parentActivity;
        if (this.delegate != null && getParentActivity() != null && this.myLocation != null && checkGpsEnabled()) {
            if (this.checkBackgroundPermission && Build.VERSION.SDK_INT >= 29 && (parentActivity = getParentActivity()) != null) {
                this.askWithRadius = i;
                this.checkBackgroundPermission = false;
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (Math.abs((System.currentTimeMillis() / 1000) - ((long) globalMainSettings.getInt("backgroundloc", 0))) > 86400 && parentActivity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
                    globalMainSettings.edit().putInt("backgroundloc", (int) (System.currentTimeMillis() / 1000)).commit();
                    AlertsCreator.createBackgroundLocationPermissionDialog(parentActivity, getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), new Runnable() {
                        public final void run() {
                            LocationActivity.this.lambda$openShareLiveLocation$25$LocationActivity();
                        }
                    }).show();
                    return;
                }
            }
            TLRPC$User user = ((int) this.dialogId) > 0 ? getMessagesController().getUser(Integer.valueOf((int) this.dialogId)) : null;
            showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new MessagesStorage.IntCallback(user, i) {
                public final /* synthetic */ TLRPC$User f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(int i) {
                    LocationActivity.this.lambda$openShareLiveLocation$26$LocationActivity(this.f$1, this.f$2, i);
                }
            }));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openShareLiveLocation$25 */
    public /* synthetic */ void lambda$openShareLiveLocation$25$LocationActivity() {
        openShareLiveLocation(this.askWithRadius);
    }

    /* access modifiers changed from: private */
    /* renamed from: shareLiveLocation */
    public void lambda$openShareLiveLocation$26(TLRPC$User tLRPC$User, int i, int i2) {
        TLRPC$TL_messageMediaGeoLive tLRPC$TL_messageMediaGeoLive = new TLRPC$TL_messageMediaGeoLive();
        TLRPC$TL_geoPoint tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint();
        tLRPC$TL_messageMediaGeoLive.geo = tLRPC$TL_geoPoint;
        tLRPC$TL_geoPoint.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        tLRPC$TL_messageMediaGeoLive.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        tLRPC$TL_messageMediaGeoLive.heading = LocationController.getHeading(this.myLocation);
        int i3 = tLRPC$TL_messageMediaGeoLive.flags | 1;
        tLRPC$TL_messageMediaGeoLive.flags = i3;
        tLRPC$TL_messageMediaGeoLive.period = i;
        tLRPC$TL_messageMediaGeoLive.proximity_notification_radius = i2;
        tLRPC$TL_messageMediaGeoLive.flags = i3 | 8;
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeoLive, this.locationType, true, 0);
        if (i2 > 0) {
            this.proximitySheet.setRadiusSet();
            this.proximityButton.setImageResource(NUM);
            ProximitySheet proximitySheet2 = this.proximitySheet;
            if (proximitySheet2 != null) {
                proximitySheet2.dismiss();
            }
            getUndoView().showWithAction(0, 24, Integer.valueOf(i2), tLRPC$User, (Runnable) null, (Runnable) null);
            return;
        }
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
            Bitmap createBitmap = Bitmap.createBitmap(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawCircle((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), paint);
            paint.setColor(LocationCell.getColorForIndex(i));
            canvas.drawCircle((float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(5.0f), paint);
            canvas.setBitmap((Bitmap) null);
            this.bitmapCache[i % 7] = createBitmap;
            return createBitmap;
        } catch (Throwable th) {
            FileLog.e(th);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void updatePlacesMarkers(ArrayList<TLRPC$TL_messageMediaVenue> arrayList) {
        if (arrayList != null) {
            int size = this.placeMarkers.size();
            for (int i = 0; i < size; i++) {
                this.placeMarkers.get(i).marker.remove();
            }
            this.placeMarkers.clear();
            int size2 = arrayList.size();
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue = arrayList.get(i2);
                try {
                    MarkerOptions markerOptions = new MarkerOptions();
                    TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$TL_messageMediaVenue.geo;
                    markerOptions.position(new LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long));
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createPlaceBitmap(i2)));
                    markerOptions.anchor(0.5f, 0.5f);
                    markerOptions.title(tLRPC$TL_messageMediaVenue.title);
                    markerOptions.snippet(tLRPC$TL_messageMediaVenue.address);
                    VenueLocation venueLocation = new VenueLocation();
                    venueLocation.num = i2;
                    Marker addMarker = this.googleMap.addMarker(markerOptions);
                    venueLocation.marker = addMarker;
                    venueLocation.venue = tLRPC$TL_messageMediaVenue;
                    addMarker.setTag(venueLocation);
                    this.placeMarkers.add(venueLocation);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    private LiveLocation addUserMarker(TLRPC$Message tLRPC$Message) {
        TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$Message.media.geo;
        LatLng latLng = new LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long);
        LiveLocation liveLocation = this.markersMap.get(MessageObject.getFromChatId(tLRPC$Message));
        if (liveLocation == null) {
            liveLocation = new LiveLocation();
            liveLocation.object = tLRPC$Message;
            if (tLRPC$Message.from_id instanceof TLRPC$TL_peerUser) {
                liveLocation.user = getMessagesController().getUser(Integer.valueOf(liveLocation.object.from_id.user_id));
                liveLocation.id = liveLocation.object.from_id.user_id;
            } else {
                int dialogId2 = (int) MessageObject.getDialogId(tLRPC$Message);
                if (dialogId2 > 0) {
                    liveLocation.user = getMessagesController().getUser(Integer.valueOf(dialogId2));
                } else {
                    liveLocation.chat = getMessagesController().getChat(Integer.valueOf(-dialogId2));
                }
                liveLocation.id = dialogId2;
            }
            try {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                Bitmap createUserBitmap = createUserBitmap(liveLocation);
                if (createUserBitmap != null) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createUserBitmap));
                    markerOptions.anchor(0.5f, 0.907f);
                    liveLocation.marker = this.googleMap.addMarker(markerOptions);
                    if (!UserObject.isUserSelf(liveLocation.user)) {
                        MarkerOptions markerOptions2 = new MarkerOptions();
                        markerOptions2.position(latLng);
                        markerOptions2.flat(true);
                        markerOptions2.anchor(0.5f, 0.5f);
                        Marker addMarker = this.googleMap.addMarker(markerOptions2);
                        liveLocation.directionMarker = addMarker;
                        int i = tLRPC$Message.media.heading;
                        if (i != 0) {
                            addMarker.setRotation((float) i);
                            liveLocation.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(NUM));
                            liveLocation.hasRotation = true;
                        } else {
                            addMarker.setRotation(0.0f);
                            liveLocation.directionMarker.setIcon(BitmapDescriptorFactory.fromResource(NUM));
                            liveLocation.hasRotation = false;
                        }
                    }
                    this.markers.add(liveLocation);
                    this.markersMap.put(liveLocation.id, liveLocation);
                    LocationController.SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
                    if (liveLocation.id == getUserConfig().getClientUserId() && sharingLocationInfo != null && liveLocation.object.id == sharingLocationInfo.mid && this.myLocation != null) {
                        liveLocation.marker.setPosition(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            liveLocation.object = tLRPC$Message;
            liveLocation.marker.setPosition(latLng);
        }
        ProximitySheet proximitySheet2 = this.proximitySheet;
        if (proximitySheet2 != null) {
            proximitySheet2.updateText(true, true);
        }
        return liveLocation;
    }

    private LiveLocation addUserMarker(TLRPC$TL_channelLocation tLRPC$TL_channelLocation) {
        TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$TL_channelLocation.geo_point;
        LatLng latLng = new LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long);
        LiveLocation liveLocation = new LiveLocation();
        int i = (int) this.dialogId;
        if (i > 0) {
            liveLocation.user = getMessagesController().getUser(Integer.valueOf(i));
        } else {
            liveLocation.chat = getMessagesController().getChat(Integer.valueOf(-i));
        }
        liveLocation.id = i;
        try {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            Bitmap createUserBitmap = createUserBitmap(liveLocation);
            if (createUserBitmap != null) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createUserBitmap));
                markerOptions.anchor(0.5f, 0.907f);
                liveLocation.marker = this.googleMap.addMarker(markerOptions);
                if (!UserObject.isUserSelf(liveLocation.user)) {
                    MarkerOptions markerOptions2 = new MarkerOptions();
                    markerOptions2.position(latLng);
                    markerOptions2.flat(true);
                    markerOptions2.icon(BitmapDescriptorFactory.fromResource(NUM));
                    markerOptions2.anchor(0.5f, 0.5f);
                    liveLocation.directionMarker = this.googleMap.addMarker(markerOptions2);
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
        LocationController.SharingLocationInfo sharingLocationInfo;
        int i;
        if (this.googleMap != null) {
            TLRPC$TL_channelLocation tLRPC$TL_channelLocation = this.chatLocation;
            if (tLRPC$TL_channelLocation != null) {
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker(tLRPC$TL_channelLocation).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
            } else {
                MessageObject messageObject2 = this.messageObject;
                if (messageObject2 == null) {
                    Location location = new Location("network");
                    this.userLocation = location;
                    if (this.initialLocation != null) {
                        TLRPC$GeoPoint tLRPC$GeoPoint = this.initialLocation.geo_point;
                        LatLng latLng = new LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long);
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
                    LiveLocation addUserMarker = addUserMarker(this.messageObject.messageOwner);
                    if (!getRecentLocations()) {
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker.marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                } else {
                    LatLng latLng2 = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                    try {
                        GoogleMap googleMap3 = this.googleMap;
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng2);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(NUM));
                        googleMap3.addMarker(markerOptions);
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
                FileLog.e((Throwable) e2);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                public final void onCameraMoveStarted(int i) {
                    LocationActivity.this.lambda$onMapInit$27$LocationActivity(i);
                }
            });
            this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                public final void onMyLocationChange(Location location) {
                    LocationActivity.this.lambda$onMapInit$28$LocationActivity(location);
                }
            });
            this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public final boolean onMarkerClick(Marker marker) {
                    return LocationActivity.this.lambda$onMapInit$29$LocationActivity(marker);
                }
            });
            this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                public final void onCameraMove() {
                    LocationActivity.this.lambda$onMapInit$30$LocationActivity();
                }
            });
            Location lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
            if (this.checkGpsEnabled && getParentActivity() != null) {
                this.checkGpsEnabled = false;
                checkGpsEnabled();
            }
            ImageView imageView = this.proximityButton;
            if (imageView != null && imageView.getVisibility() == 0 && (sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId)) != null && (i = sharingLocationInfo.proximityMeters) > 0) {
                createCircle(i);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMapInit$27 */
    public /* synthetic */ void lambda$onMapInit$27$LocationActivity(int i) {
        View childAt;
        RecyclerView.ViewHolder findContainingViewHolder;
        if (i == 1) {
            showSearchPlacesButton(true);
            removeInfoView();
            if (!this.scrolling) {
                int i2 = this.locationType;
                if ((i2 == 0 || i2 == 1) && this.listView.getChildCount() > 0 && (childAt = this.listView.getChildAt(0)) != null && (findContainingViewHolder = this.listView.findContainingViewHolder(childAt)) != null && findContainingViewHolder.getAdapterPosition() == 0) {
                    int dp = this.locationType == 0 ? 0 : AndroidUtilities.dp(66.0f);
                    int top = childAt.getTop();
                    if (top < (-dp)) {
                        CameraPosition cameraPosition = this.googleMap.getCameraPosition();
                        this.forceUpdate = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom);
                        this.listView.smoothScrollBy(0, top + dp);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMapInit$28 */
    public /* synthetic */ void lambda$onMapInit$28$LocationActivity(Location location) {
        positionMarker(location);
        getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMapInit$29 */
    public /* synthetic */ boolean lambda$onMapInit$29$LocationActivity(Marker marker) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMapInit$30 */
    public /* synthetic */ void lambda$onMapInit$30$LocationActivity() {
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
                builder.setTitle(LocaleController.getString("GpsDisabledAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LocationActivity.this.lambda$checkGpsEnabled$31$LocationActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
                return false;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkGpsEnabled$31 */
    public /* synthetic */ void lambda$checkGpsEnabled$31$LocationActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception unused) {
            }
        }
    }

    private void createCircle(int i) {
        if (this.googleMap != null) {
            List asList = Arrays.asList(new PatternItem[]{new Gap(20.0f), new Dash(20.0f)});
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
            circleOptions.radius((double) i);
            if (isActiveThemeDark()) {
                circleOptions.strokeColor(-NUM);
                circleOptions.fillColor(NUM);
            } else {
                circleOptions.strokeColor(-NUM);
                circleOptions.fillColor(NUM);
            }
            circleOptions.strokePattern(asList);
            circleOptions.strokeWidth(2.0f);
            this.proximityCircle = this.googleMap.addCircle(circleOptions);
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
    public void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", NUM));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LocationActivity.this.lambda$showPermissionAlert$32$LocationActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPermissionAlert$32 */
    public /* synthetic */ void lambda$showPermissionAlert$32$LocationActivity(DialogInterface dialogInterface, int i) {
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
                updateClipView(false);
                maybeShowProximityHint();
                return;
            }
            View view = this.fragmentView;
            if (view != null) {
                ((FrameLayout) view).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
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
            r7 = this;
            android.widget.ImageView r0 = r7.proximityButton
            if (r0 == 0) goto L_0x006a
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x006a
            boolean r0 = r7.proximityAnimationInProgress
            if (r0 == 0) goto L_0x000f
            goto L_0x006a
        L_0x000f:
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r1 = "proximityhint"
            r2 = 0
            int r3 = r0.getInt(r1, r2)
            r4 = 3
            if (r3 >= r4) goto L_0x006a
            android.content.SharedPreferences$Editor r0 = r0.edit()
            r4 = 1
            int r3 = r3 + r4
            android.content.SharedPreferences$Editor r0 = r0.putInt(r1, r3)
            r0.commit()
            long r0 = r7.dialogId
            int r1 = (int) r0
            if (r1 <= 0) goto L_0x0055
            org.telegram.messenger.MessagesController r0 = r7.getMessagesController()
            long r5 = r7.dialogId
            int r1 = (int) r5
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            org.telegram.ui.Components.HintView r1 = r7.hintView
            r3 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r5[r2] = r0
            java.lang.String r0 = "ProximityTooltioUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r5)
            r1.setOverrideText(r0)
            goto L_0x0063
        L_0x0055:
            org.telegram.ui.Components.HintView r0 = r7.hintView
            r1 = 2131627044(0x7f0e0CLASSNAME, float:1.8881341E38)
            java.lang.String r2 = "ProximityTooltioGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setOverrideText(r1)
        L_0x0063:
            org.telegram.ui.Components.HintView r0 = r7.hintView
            android.widget.ImageView r1 = r7.proximityButton
            r0.showForView(r1, r4)
        L_0x006a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.maybeShowProximityHint():void");
    }

    private void showResults() {
        if (this.adapter.getItemCount() != 0 && this.layoutManager.findFirstVisibleItemPosition() == 0) {
            int dp = AndroidUtilities.dp(258.0f) + this.listView.getChildAt(0).getTop();
            if (dp >= 0 && dp <= AndroidUtilities.dp(258.0f)) {
                this.listView.smoothScrollBy(0, dp);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateClipView(boolean z) {
        int i;
        int i2;
        FrameLayout.LayoutParams layoutParams;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            i2 = (int) findViewHolderForAdapterPosition.itemView.getY();
            i = this.overScrollHeight + Math.min(i2, 0);
        } else {
            i2 = -this.mapViewClip.getMeasuredHeight();
            i = 0;
        }
        if (((FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
            if (i <= 0) {
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
            this.mapViewClip.setTranslationY((float) Math.min(0, i2));
            int i3 = -i2;
            int i4 = i3 / 2;
            this.mapView.setTranslationY((float) Math.max(0, i4));
            MapOverlayView mapOverlayView3 = this.overlayView;
            if (mapOverlayView3 != null) {
                mapOverlayView3.setTranslationY((float) Math.max(0, i4));
            }
            int measuredHeight = this.overScrollHeight - this.mapTypeButton.getMeasuredHeight();
            int i5 = this.locationType;
            float min = (float) Math.min(measuredHeight - AndroidUtilities.dp((float) (64 + ((i5 == 0 || i5 == 1) ? 30 : 10))), i3);
            this.mapTypeButton.setTranslationY(min);
            this.proximityButton.setTranslationY(min);
            HintView hintView2 = this.hintView;
            if (hintView2 != null) {
                hintView2.setExtraTranslationY(min);
            }
            SearchButton searchButton = this.searchAreaButton;
            if (searchButton != null) {
                searchButton.setTranslation(min);
            }
            View view = this.markerImageView;
            if (view != null) {
                int dp = (i3 - AndroidUtilities.dp(view.getTag() == null ? 48.0f : 69.0f)) + (i / 2);
                this.markerTop = dp;
                view.setTranslationY((float) dp);
            }
            if (!z) {
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
    public void fixLayoutInternal(boolean z) {
        FrameLayout.LayoutParams layoutParams;
        if (this.listView != null) {
            int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int measuredHeight = this.fragmentView.getMeasuredHeight();
            if (measuredHeight != 0) {
                int i = this.locationType;
                if (i == 6) {
                    this.overScrollHeight = (measuredHeight - AndroidUtilities.dp(66.0f)) - currentActionBarHeight;
                } else if (i == 2) {
                    this.overScrollHeight = (measuredHeight - AndroidUtilities.dp(73.0f)) - currentActionBarHeight;
                } else {
                    this.overScrollHeight = (measuredHeight - AndroidUtilities.dp(66.0f)) - currentActionBarHeight;
                }
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
                layoutParams2.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams2);
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams();
                layoutParams3.topMargin = currentActionBarHeight;
                layoutParams3.height = this.overScrollHeight;
                this.mapViewClip.setLayoutParams(layoutParams3);
                RecyclerListView recyclerListView = this.searchListView;
                if (recyclerListView != null) {
                    FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
                    layoutParams4.topMargin = currentActionBarHeight;
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
                if (z) {
                    int i2 = this.locationType;
                    int i3 = i2 == 3 ? 73 : (i2 == 1 || i2 == 2) ? 66 : 0;
                    this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) i3));
                    updateClipView(false);
                    this.listView.post(new Runnable(i3) {
                        public final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            LocationActivity.this.lambda$fixLayoutInternal$33$LocationActivity(this.f$1);
                        }
                    });
                    return;
                }
                updateClipView(false);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$fixLayoutInternal$33 */
    public /* synthetic */ void lambda$fixLayoutInternal$33$LocationActivity(int i) {
        this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) i));
        updateClipView(false);
    }

    private Location getLastLocation() {
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        for (int size = providers.size() - 1; size >= 0; size--) {
            location = locationManager.getLastKnownLocation(providers.get(size));
            if (location != null) {
                break;
            }
        }
        return location;
    }

    private void positionMarker(Location location) {
        if (location != null) {
            this.myLocation = new Location(location);
            LiveLocation liveLocation = this.markersMap.get(getUserConfig().getClientUserId());
            LocationController.SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (!(liveLocation == null || sharingLocationInfo == null || liveLocation.object.id != sharingLocationInfo.mid)) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                liveLocation.marker.setPosition(latLng);
                Marker marker = liveLocation.directionMarker;
                if (marker != null) {
                    marker.setPosition(latLng);
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

    public void setMessageObject(MessageObject messageObject2) {
        this.messageObject = messageObject2;
        this.dialogId = messageObject2.getDialogId();
    }

    public void setChatLocation(int i, TLRPC$TL_channelLocation tLRPC$TL_channelLocation) {
        this.dialogId = (long) (-i);
        this.chatLocation = tLRPC$TL_channelLocation;
    }

    public void setDialogId(long j) {
        this.dialogId = j;
    }

    public void setInitialLocation(TLRPC$TL_channelLocation tLRPC$TL_channelLocation) {
        this.initialLocation = tLRPC$TL_channelLocation;
    }

    private static LatLng move(LatLng latLng, double d, double d2) {
        double meterToLongitude = meterToLongitude(d2, latLng.latitude);
        return new LatLng(latLng.latitude + meterToLatitude(d), latLng.longitude + meterToLongitude);
    }

    private static double meterToLongitude(double d, double d2) {
        return Math.toDegrees(d / (Math.cos(Math.toRadians(d2)) * 6366198.0d));
    }

    private static double meterToLatitude(double d) {
        return Math.toDegrees(d / 6366198.0d);
    }

    private void fetchRecentLocations(ArrayList<TLRPC$Message> arrayList) {
        LatLngBounds.Builder builder = this.firstFocus ? new LatLngBounds.Builder() : null;
        int currentTime = getConnectionsManager().getCurrentTime();
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$Message tLRPC$Message = arrayList.get(i);
            if (tLRPC$Message.date + tLRPC$Message.media.period > currentTime) {
                if (builder != null) {
                    TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$Message.media.geo;
                    builder.include(new LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long));
                }
                addUserMarker(tLRPC$Message);
                if (!(this.proximityButton.getVisibility() == 8 || MessageObject.getFromChatId(tLRPC$Message) == getUserConfig().getClientUserId())) {
                    this.proximityButton.setVisibility(0);
                    this.proximityAnimationInProgress = true;
                    this.proximityButton.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(180).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
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
                    LatLng move = move(center, 100.0d, 100.0d);
                    builder.include(move(center, -100.0d, -100.0d));
                    builder.include(move);
                    LatLngBounds build = builder.build();
                    if (arrayList.size() > 1) {
                        try {
                            CameraUpdate newLatLngBounds = CameraUpdateFactory.newLatLngBounds(build, AndroidUtilities.dp(113.0f));
                            this.moveToBounds = newLatLngBounds;
                            this.googleMap.moveCamera(newLatLngBounds);
                            this.moveToBounds = null;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                } catch (Exception unused) {
                }
            }
        }
    }

    private void moveToBounds(int i, boolean z, boolean z2) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
        if (z) {
            try {
                int max = Math.max(i, 250);
                LatLng center = builder.build().getCenter();
                double d = (double) max;
                LatLng move = move(center, d, d);
                double d2 = (double) (-max);
                builder.include(move(center, d2, d2));
                builder.include(move);
                LatLngBounds build = builder.build();
                try {
                    this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), (int) (((float) (this.proximitySheet.getCustomView().getMeasuredHeight() - AndroidUtilities.dp(40.0f))) + this.mapViewClip.getTranslationY()));
                    if (z2) {
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(build, 0), 500, (GoogleMap.CancelableCallback) null);
                    } else {
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(build, 0));
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } catch (Exception unused) {
            }
        } else {
            int currentTime = getConnectionsManager().getCurrentTime();
            int size = this.markers.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$Message tLRPC$Message = this.markers.get(i2).object;
                if (tLRPC$Message.date + tLRPC$Message.media.period > currentTime) {
                    TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$Message.media.geo;
                    builder.include(new LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long));
                }
            }
            LatLng center2 = builder.build().getCenter();
            LatLng move2 = move(center2, 100.0d, 100.0d);
            builder.include(move(center2, -100.0d, -100.0d));
            builder.include(move2);
            LatLngBounds build2 = builder.build();
            try {
                this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), this.proximitySheet.getCustomView().getMeasuredHeight() - AndroidUtilities.dp(100.0f));
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(build2, 0));
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    private boolean getRecentLocations() {
        ArrayList arrayList = getLocationController().locationsCache.get(this.messageObject.getDialogId());
        if (arrayList == null || !arrayList.isEmpty()) {
            arrayList = null;
        } else {
            fetchRecentLocations(arrayList);
        }
        int i = (int) this.dialogId;
        if (i < 0) {
            TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(-i));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TLRPC$TL_messages_getRecentLocations tLRPC$TL_messages_getRecentLocations = new TLRPC$TL_messages_getRecentLocations();
        long dialogId2 = this.messageObject.getDialogId();
        tLRPC$TL_messages_getRecentLocations.peer = getMessagesController().getInputPeer((int) dialogId2);
        tLRPC$TL_messages_getRecentLocations.limit = 100;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getRecentLocations, new RequestDelegate(dialogId2) {
            public final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                LocationActivity.this.lambda$getRecentLocations$36$LocationActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
        if (arrayList != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getRecentLocations$36 */
    public /* synthetic */ void lambda$getRecentLocations$36$LocationActivity(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject, j) {
                public final /* synthetic */ TLObject f$1;
                public final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    LocationActivity.this.lambda$null$35$LocationActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$35 */
    public /* synthetic */ void lambda$null$35$LocationActivity(TLObject tLObject, long j) {
        if (this.googleMap != null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            int i = 0;
            while (i < tLRPC$messages_Messages.messages.size()) {
                if (!(tLRPC$messages_Messages.messages.get(i).media instanceof TLRPC$TL_messageMediaGeoLive)) {
                    tLRPC$messages_Messages.messages.remove(i);
                    i--;
                }
                i++;
            }
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            getMessagesController().putUsers(tLRPC$messages_Messages.users, false);
            getMessagesController().putChats(tLRPC$messages_Messages.chats, false);
            getLocationController().locationsCache.put(j, tLRPC$messages_Messages.messages);
            getNotificationCenter().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(j));
            fetchRecentLocations(tLRPC$messages_Messages.messages);
            getLocationController().markLiveLoactionsAsRead(this.dialogId);
            if (this.markAsReadRunnable == null) {
                $$Lambda$LocationActivity$2ep522D_VQBQC8cxCTWH11gpBuQ r6 = new Runnable() {
                    public final void run() {
                        LocationActivity.this.lambda$null$34$LocationActivity();
                    }
                };
                this.markAsReadRunnable = r6;
                AndroidUtilities.runOnUIThread(r6, 5000);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$34 */
    public /* synthetic */ void lambda$null$34$LocationActivity() {
        Runnable runnable;
        getLocationController().markLiveLoactionsAsRead(this.dialogId);
        if (!this.isPaused && (runnable = this.markAsReadRunnable) != null) {
            AndroidUtilities.runOnUIThread(runnable, 5000);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        LocationActivityAdapter locationActivityAdapter;
        LiveLocation liveLocation;
        LocationActivityAdapter locationActivityAdapter2;
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.locationPermissionGranted) {
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                try {
                    googleMap2.setMyLocationEnabled(true);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        } else if (i == NotificationCenter.liveLocationsChanged) {
            LocationActivityAdapter locationActivityAdapter3 = this.adapter;
            if (locationActivityAdapter3 != null) {
                locationActivityAdapter3.updateLiveLocationCell();
            }
        } else {
            if (i == NotificationCenter.didReceiveNewMessages) {
                if (!objArr[2].booleanValue() && objArr[0].longValue() == this.dialogId && this.messageObject != null) {
                    ArrayList arrayList = objArr[1];
                    boolean z = false;
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        MessageObject messageObject2 = (MessageObject) arrayList.get(i3);
                        if (messageObject2.isLiveLocation()) {
                            addUserMarker(messageObject2.messageOwner);
                            z = true;
                        } else if ((messageObject2.messageOwner.action instanceof TLRPC$TL_messageActionGeoProximityReached) && ((int) messageObject2.getDialogId()) > 0) {
                            this.proximityButton.setImageResource(NUM);
                            Circle circle = this.proximityCircle;
                            if (circle != null) {
                                circle.remove();
                                this.proximityCircle = null;
                            }
                        }
                    }
                    if (z && (locationActivityAdapter2 = this.adapter) != null) {
                        locationActivityAdapter2.setLiveLocations(this.markers);
                    }
                }
            } else if (i == NotificationCenter.replaceMessagesObjects) {
                long longValue = objArr[0].longValue();
                if (longValue == this.dialogId && this.messageObject != null) {
                    ArrayList arrayList2 = objArr[1];
                    boolean z2 = false;
                    for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                        MessageObject messageObject3 = (MessageObject) arrayList2.get(i4);
                        if (messageObject3.isLiveLocation() && (liveLocation = this.markersMap.get(getMessageId(messageObject3.messageOwner))) != null) {
                            LocationController.SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(longValue);
                            if (sharingLocationInfo == null || sharingLocationInfo.mid != messageObject3.getId()) {
                                liveLocation.object = messageObject3.messageOwner;
                                TLRPC$GeoPoint tLRPC$GeoPoint = messageObject3.messageOwner.media.geo;
                                LatLng latLng = new LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long);
                                liveLocation.marker.setPosition(latLng);
                                Marker marker = liveLocation.directionMarker;
                                if (marker != null) {
                                    marker.getPosition();
                                    liveLocation.directionMarker.setPosition(latLng);
                                    int i5 = messageObject3.messageOwner.media.heading;
                                    if (i5 != 0) {
                                        liveLocation.directionMarker.setRotation((float) i5);
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
                            z2 = true;
                        }
                    }
                    if (z2 && (locationActivityAdapter = this.adapter) != null) {
                        locationActivityAdapter.updateLiveLocations();
                        ProximitySheet proximitySheet2 = this.proximitySheet;
                        if (proximitySheet2 != null) {
                            proximitySheet2.updateText(true, true);
                        }
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
        Activity parentActivity;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        MapView mapView2 = this.mapView;
        if (mapView2 != null && this.mapsInitialized) {
            try {
                mapView2.onResume();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        this.onResumeCalled = true;
        GoogleMap googleMap2 = this.googleMap;
        if (googleMap2 != null) {
            try {
                googleMap2.setMyLocationEnabled(true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        fixLayoutInternal(true);
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (parentActivity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            }
        }
        Runnable runnable = this.markAsReadRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            AndroidUtilities.runOnUIThread(this.markAsReadRunnable, 5000);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 30) {
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

    public void setDelegate(LocationActivityDelegate locationActivityDelegate) {
        this.delegate = locationActivityDelegate;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$LocationActivity$os7Jo3S2bd5XMkv2FrSpJwQ17c r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                LocationActivity.this.lambda$getThemeDescriptions$37$LocationActivity();
            }
        };
        for (int i = 0; i < this.undoView.length; i++) {
            arrayList.add(new ThemeDescription(this.undoView[i], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "BODY", "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Wibe Big", "undo_background"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Wibe Big 3", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Wibe Small", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Body Main.**", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Body Top.**", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Line.**", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Curve Big.**", "undo_infoColor"));
            arrayList.add(new ThemeDescription((View) this.undoView[i], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Curve Small.**", "undo_infoColor"));
        }
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, r10, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint"));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        arrayList.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        $$Lambda$LocationActivity$os7Jo3S2bd5XMkv2FrSpJwQ17c r8 = r10;
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        arrayList.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.shadow, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.proximityButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.proximityButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.proximityButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, r8, "avatar_text"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_liveLocationProgress"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_placeLocationBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialog_liveLocationProgress"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationDirectionCell.class}, new String[]{"buttonTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"distanceTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$37 */
    public /* synthetic */ void lambda$getThemeDescriptions$37$LocationActivity() {
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
