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
import android.graphics.Matrix;
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
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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
import java.nio.ByteBuffer;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.IMapsProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
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
    private LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
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
    public IMapsProvider.ICameraUpdate forceUpdate;
    private boolean hasScreenshot;
    private HintView hintView;
    private TLRPC$TL_channelLocation initialLocation;
    private boolean isFirstLocation = true;
    /* access modifiers changed from: private */
    public IMapsProvider.IMarker lastPressedMarker;
    /* access modifiers changed from: private */
    public FrameLayout lastPressedMarkerView;
    /* access modifiers changed from: private */
    public VenueLocation lastPressedVenue;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ImageView locationButton;
    private boolean locationDenied = false;
    /* access modifiers changed from: private */
    public int locationType;
    /* access modifiers changed from: private */
    public IMapsProvider.IMap map;
    private ActionBarMenuItem mapTypeButton;
    private IMapsProvider.IMapView mapView;
    /* access modifiers changed from: private */
    public FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private Runnable markAsReadRunnable;
    private View markerImageView;
    private int markerTop;
    private ArrayList<LiveLocation> markers = new ArrayList<>();
    private LongSparseArray<LiveLocation> markersMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public MessageObject messageObject;
    private IMapsProvider.ICameraUpdate moveToBounds;
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
    private IMapsProvider.ICircle proximityCircle;
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
    private boolean userLocationMoved;
    private float yOffset;

    public static class LiveLocation {
        public TLRPC$Chat chat;
        public IMapsProvider.IMarker directionMarker;
        public boolean hasRotation;
        public long id;
        public IMapsProvider.IMarker marker;
        public TLRPC$Message object;
        public TLRPC$User user;
    }

    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2);
    }

    public static class VenueLocation {
        public IMapsProvider.IMarker marker;
        public int num;
        public TLRPC$TL_messageMediaVenue venue;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$7(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean hasForceLightStatusBar() {
        return true;
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    static /* synthetic */ float access$3316(LocationActivity locationActivity, float f) {
        float f2 = locationActivity.yOffset + f;
        locationActivity.yOffset = f2;
        return f2;
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
        private HashMap<IMapsProvider.IMarker, View> views = new HashMap<>();

        public MapOverlayView(Context context) {
            super(context);
        }

        public void addInfoView(IMapsProvider.IMarker iMarker) {
            IMapsProvider.IMarker iMarker2 = iMarker;
            VenueLocation venueLocation = (VenueLocation) iMarker.getTag();
            if (venueLocation != null && LocationActivity.this.lastPressedVenue != venueLocation) {
                LocationActivity.this.showSearchPlacesButton(false);
                if (LocationActivity.this.lastPressedMarker != null) {
                    removeInfoView(LocationActivity.this.lastPressedMarker);
                    IMapsProvider.IMarker unused = LocationActivity.this.lastPressedMarker = null;
                }
                VenueLocation unused2 = LocationActivity.this.lastPressedVenue = venueLocation;
                IMapsProvider.IMarker unused3 = LocationActivity.this.lastPressedMarker = iMarker2;
                Context context = getContext();
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createFrame(-2, 114.0f));
                FrameLayout unused4 = LocationActivity.this.lastPressedMarkerView = new FrameLayout(context);
                LocationActivity.this.lastPressedMarkerView.setBackgroundResource(R.drawable.venue_tooltip);
                LocationActivity.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
                frameLayout.addView(LocationActivity.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
                LocationActivity.this.lastPressedMarkerView.setAlpha(0.0f);
                LocationActivity.this.lastPressedMarkerView.setOnClickListener(new LocationActivity$MapOverlayView$$ExternalSyntheticLambda0(this, venueLocation));
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
                textView2.setText(LocaleController.getString("TapToSendLocation", R.string.TapToSendLocation));
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
                this.views.put(iMarker2, frameLayout);
                LocationActivity.this.map.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLng(iMarker.getPosition()), 300, (IMapsProvider.ICancelableCallback) null);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$addInfoView$1(VenueLocation venueLocation, View view) {
            if (LocationActivity.this.parentFragment == null || !LocationActivity.this.parentFragment.isInScheduleMode()) {
                LocationActivity.this.delegate.didSelectLocation(venueLocation.venue, LocationActivity.this.locationType, true, 0);
                LocationActivity.this.finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(LocationActivity.this.getParentActivity(), LocationActivity.this.parentFragment.getDialogId(), new LocationActivity$MapOverlayView$$ExternalSyntheticLambda1(this, venueLocation));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$addInfoView$0(VenueLocation venueLocation, boolean z, int i) {
            LocationActivity.this.delegate.didSelectLocation(venueLocation.venue, LocationActivity.this.locationType, z, i);
            LocationActivity.this.finishFragment();
        }

        public void removeInfoView(IMapsProvider.IMarker iMarker) {
            View view = this.views.get(iMarker);
            if (view != null) {
                removeView(view);
                this.views.remove(iMarker);
            }
        }

        public void updatePositions() {
            if (LocationActivity.this.map != null) {
                IMapsProvider.IProjection projection = LocationActivity.this.map.getProjection();
                for (Map.Entry next : this.views.entrySet()) {
                    View view = (View) next.getValue();
                    Point screenLocation = projection.toScreenLocation(((IMapsProvider.IMarker) next.getKey()).getPosition());
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
            IMapsProvider.IMap iMap = this.map;
            if (iMap != null) {
                iMap.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            IMapsProvider.IMapView iMapView = this.mapView;
            if (iMapView != null) {
                iMapView.onDestroy();
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
        String str;
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
        int i2 = Build.VERSION.SDK_INT;
        this.locationDenied = (i2 < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) ? false : true;
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
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
            this.actionBar.setTitle(LocaleController.getString("ChatLocation", R.string.ChatLocation));
        } else {
            MessageObject messageObject2 = this.messageObject;
            if (messageObject2 == null) {
                this.actionBar.setTitle(LocaleController.getString("ShareLocation", R.string.ShareLocation));
                if (this.locationType != 4) {
                    this.overlayView = new MapOverlayView(context2);
                    ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                    this.searchItem = actionBarMenuItemSearchListener;
                    int i3 = R.string.Search;
                    actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", i3));
                    this.searchItem.setContentDescription(LocaleController.getString("Search", i3));
                    EditTextBoldCursor searchField = this.searchItem.getSearchField();
                    searchField.setTextColor(Theme.getColor("dialogTextBlack"));
                    searchField.setCursorColor(Theme.getColor("dialogTextBlack"));
                    searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
                }
            } else if (messageObject2.isLiveLocation()) {
                this.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation));
            } else {
                String str2 = this.messageObject.messageOwner.media.title;
                if (str2 == null || str2.length() <= 0) {
                    this.actionBar.setTitle(LocaleController.getString("ChatLocation", R.string.ChatLocation));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SharedPlace", R.string.SharedPlace));
                }
                ActionBarMenuItem addItem = createMenu.addItem(0, R.drawable.ic_ab_other);
                this.otherItem = addItem;
                addItem.addSubItem(1, R.drawable.msg_openin, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
                if (!getLocationController().isSharingLocation(this.dialogId)) {
                    this.otherItem.addSubItem(5, R.drawable.msg_location, LocaleController.getString("SendLiveLocationMenu", R.string.SendLiveLocationMenu));
                }
                this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            }
        }
        AnonymousClass3 r1 = new FrameLayout(context2) {
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
        this.fragmentView = r1;
        FrameLayout frameLayout = r1;
        r1.setBackgroundColor(Theme.getColor("dialogBackground"));
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        final Rect rect = new Rect();
        this.shadowDrawable.getPadding(rect);
        int i4 = this.locationType;
        if (i4 == 0 || i4 == 1) {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f) + rect.top);
        } else {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(6.0f) + rect.top);
        }
        FrameLayout.LayoutParams layoutParams2 = layoutParams;
        layoutParams2.gravity = 83;
        AnonymousClass4 r12 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (LocationActivity.this.overlayView != null) {
                    LocationActivity.this.overlayView.updatePositions();
                }
            }
        };
        this.mapViewClip = r12;
        r12.setBackgroundDrawable(new MapPlaceholderDrawable());
        FrameLayout frameLayout2 = frameLayout;
        if (this.messageObject == null && ((i = this.locationType) == 0 || i == 1)) {
            SearchButton searchButton = new SearchButton(context2);
            this.searchAreaButton = searchButton;
            searchButton.setTranslationX((float) (-AndroidUtilities.dp(80.0f)));
            Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
            if (i2 < 21) {
                Drawable mutate2 = context.getResources().getDrawable(R.drawable.places_btn).mutate();
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
            this.searchAreaButton.setText(LocaleController.getString("PlacesInThisArea", R.string.PlacesInThisArea));
            this.searchAreaButton.setGravity(17);
            this.searchAreaButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.mapViewClip.addView(this.searchAreaButton, LayoutHelper.createFrame(-2, i2 >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
            this.searchAreaButton.setOnClickListener(new LocationActivity$$ExternalSyntheticLambda5(this));
        }
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, 0, Theme.getColor("location_actionIcon"));
        this.mapTypeButton = actionBarMenuItem;
        actionBarMenuItem.setClickable(true);
        this.mapTypeButton.setSubMenuOpenSide(2);
        this.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        this.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        this.mapTypeButton.addSubItem(2, R.drawable.msg_map, LocaleController.getString("Map", R.string.Map));
        this.mapTypeButton.addSubItem(3, R.drawable.msg_satellite, LocaleController.getString("Satellite", R.string.Satellite));
        this.mapTypeButton.addSubItem(4, R.drawable.msg_hybrid, LocaleController.getString("Hybrid", R.string.Hybrid));
        this.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (i2 < 21) {
            Drawable mutate3 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(mutate3, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable2.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable2;
            str = "location_actionIcon";
        } else {
            StateListAnimator stateListAnimator2 = new StateListAnimator();
            ActionBarMenuItem actionBarMenuItem2 = this.mapTypeButton;
            Property property2 = View.TRANSLATION_Z;
            str = "location_actionIcon";
            stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(actionBarMenuItem2, property2, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            ActionBarMenuItem actionBarMenuItem3 = this.mapTypeButton;
            float[] fArr = new float[2];
            fArr[0] = (float) AndroidUtilities.dp(4.0f);
            float[] fArr2 = fArr;
            fArr2[1] = (float) AndroidUtilities.dp(2.0f);
            stateListAnimator2.addState(new int[0], ObjectAnimator.ofFloat(actionBarMenuItem3, property2, fArr2).setDuration(200));
            this.mapTypeButton.setStateListAnimator(stateListAnimator2);
            this.mapTypeButton.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.mapTypeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.mapTypeButton.setIcon(R.drawable.msg_map_type);
        this.mapViewClip.addView(this.mapTypeButton, LayoutHelper.createFrame(i2 >= 21 ? 40 : 44, i2 >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        this.mapTypeButton.setOnClickListener(new LocationActivity$$ExternalSyntheticLambda6(this));
        this.mapTypeButton.setDelegate(new LocationActivity$$ExternalSyntheticLambda35(this));
        this.locationButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (i2 < 21) {
            Drawable mutate4 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
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
        this.locationButton.setImageResource(R.drawable.msg_current_location);
        this.locationButton.setScaleType(ImageView.ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
        this.locationButton.setTag("location_actionActiveIcon");
        this.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", R.string.AccDescrMyLocation));
        FrameLayout.LayoutParams createFrame = LayoutHelper.createFrame(i2 >= 21 ? 40 : 44, i2 >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f);
        createFrame.bottomMargin += layoutParams2.height - rect.top;
        this.mapViewClip.addView(this.locationButton, createFrame);
        this.locationButton.setOnClickListener(new LocationActivity$$ExternalSyntheticLambda4(this));
        Context context3 = context;
        this.proximityButton = new ImageView(context3);
        Drawable createSimpleSelectorCircleDrawable3 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (i2 < 21) {
            Drawable mutate5 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            mutate5.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable4 = new CombinedDrawable(mutate5, createSimpleSelectorCircleDrawable3, 0, 0);
            combinedDrawable4.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable3 = combinedDrawable4;
        } else {
            StateListAnimator stateListAnimator4 = new StateListAnimator();
            ImageView imageView2 = this.proximityButton;
            Property property4 = View.TRANSLATION_Z;
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
        this.proximityButton.setContentDescription(LocaleController.getString("AccDescrLocationNotify", R.string.AccDescrLocationNotify));
        this.mapViewClip.addView(this.proximityButton, LayoutHelper.createFrame(i2 >= 21 ? 40 : 44, i2 >= 21 ? 40.0f : 44.0f, 53, 0.0f, 62.0f, 12.0f, 0.0f));
        this.proximityButton.setOnClickListener(new LocationActivity$$ExternalSyntheticLambda7(this));
        TLRPC$Chat tLRPC$Chat = null;
        if (DialogObject.isChatDialog(this.dialogId)) {
            tLRPC$Chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
        }
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        MessageObject messageObject3 = this.messageObject;
        if (messageObject3 == null || !messageObject3.isLiveLocation() || this.messageObject.isExpiredLiveLocation(getConnectionsManager().getCurrentTime()) || (ChatObject.isChannel(tLRPC$Chat2) && !tLRPC$Chat2.megagroup)) {
            this.proximityButton.setVisibility(8);
            this.proximityButton.setImageResource(R.drawable.msg_location_alert);
        } else {
            LocationController.SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
            if (sharingLocationInfo == null || sharingLocationInfo.proximityMeters <= 0) {
                if (DialogObject.isUserDialog(this.dialogId) && this.messageObject.getFromChatId() == getUserConfig().getClientUserId()) {
                    this.proximityButton.setVisibility(4);
                    this.proximityButton.setAlpha(0.0f);
                    this.proximityButton.setScaleX(0.4f);
                    this.proximityButton.setScaleY(0.4f);
                }
                this.proximityButton.setImageResource(R.drawable.msg_location_alert);
            } else {
                this.proximityButton.setImageResource(R.drawable.msg_location_alert2);
            }
        }
        HintView hintView2 = new HintView(context3, 6, true);
        this.hintView = hintView2;
        hintView2.setVisibility(4);
        this.hintView.setShowingDuration(4000);
        this.mapViewClip.addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context3);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(1);
        this.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        this.emptyView.setVisibility(8);
        FrameLayout frameLayout3 = frameLayout2;
        frameLayout3.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(LocationActivity$$ExternalSyntheticLambda8.INSTANCE);
        ImageView imageView3 = new ImageView(context3);
        this.emptyImageView = imageView3;
        imageView3.setImageResource(R.drawable.location_empty);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context3);
        this.emptyTitleTextView = textView;
        textView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setText(LocaleController.getString("NoPlacesFound", R.string.NoPlacesFound));
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context3);
        this.emptySubtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context3);
        this.listView = recyclerListView;
        AnonymousClass9 r13 = r0;
        FrameLayout.LayoutParams layoutParams3 = layoutParams2;
        AnonymousClass9 r0 = new LocationActivityAdapter(context, this.locationType, this.dialogId, false, (Theme.ResourcesProvider) null) {
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
        this.adapter = r13;
        recyclerListView.setAdapter(r13);
        this.adapter.setMyLocationDenied(this.locationDenied);
        this.adapter.setUpdateRunnable(new LocationActivity$$ExternalSyntheticLambda11(this));
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context3, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout3.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                boolean unused = LocationActivity.this.scrolling = i != 0;
                if (!LocationActivity.this.scrolling && LocationActivity.this.forceUpdate != null) {
                    IMapsProvider.ICameraUpdate unused2 = LocationActivity.this.forceUpdate = null;
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                LocationActivity.this.updateClipView(false);
                if (LocationActivity.this.forceUpdate != null) {
                    LocationActivity.access$3316(LocationActivity.this, (float) i2);
                }
            }
        });
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LocationActivity$$ExternalSyntheticLambda44(this));
        this.adapter.setDelegate(this.dialogId, new LocationActivity$$ExternalSyntheticLambda37(this));
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout3.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        IMapsProvider.IMapView onCreateMapView = ApplicationLoader.getMapsProvider().onCreateMapView(context3);
        this.mapView = onCreateMapView;
        onCreateMapView.setOnDispatchTouchEventInterceptor(new LocationActivity$$ExternalSyntheticLambda28(this));
        this.mapView.setOnInterceptTouchEventInterceptor(new LocationActivity$$ExternalSyntheticLambda29(this));
        this.mapView.setOnLayoutListener(new LocationActivity$$ExternalSyntheticLambda16(this));
        new Thread(new LocationActivity$$ExternalSyntheticLambda23(this, this.mapView)).start();
        MessageObject messageObject4 = this.messageObject;
        if (messageObject4 == null && this.chatLocation == null) {
            if (!(tLRPC$Chat2 == null || this.locationType != 4 || this.dialogId == 0)) {
                FrameLayout frameLayout4 = new FrameLayout(context3);
                frameLayout4.setBackgroundResource(R.drawable.livepin);
                this.mapViewClip.addView(frameLayout4, LayoutHelper.createFrame(62, 76, 49));
                BackupImageView backupImageView = new BackupImageView(context3);
                backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
                backupImageView.setForUserOrChat(tLRPC$Chat2, new AvatarDrawable(tLRPC$Chat2));
                frameLayout4.addView(backupImageView, LayoutHelper.createFrame(52, 52.0f, 51, 5.0f, 5.0f, 0.0f, 0.0f));
                this.markerImageView = frameLayout4;
                frameLayout4.setTag(1);
            }
            if (this.markerImageView == null) {
                ImageView imageView4 = new ImageView(context3);
                imageView4.setImageResource(R.drawable.map_pin2);
                this.mapViewClip.addView(imageView4, LayoutHelper.createFrame(28, 48, 49));
                this.markerImageView = imageView4;
            }
            RecyclerListView recyclerListView3 = new RecyclerListView(context3);
            this.searchListView = recyclerListView3;
            recyclerListView3.setVisibility(8);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context3, 1, false));
            AnonymousClass11 r02 = new LocationActivitySearchAdapter(context3) {
                public void notifyDataSetChanged() {
                    if (LocationActivity.this.searchItem != null) {
                        LocationActivity.this.searchItem.setShowSearchProgress(LocationActivity.this.searchAdapter.isSearching());
                    }
                    if (LocationActivity.this.emptySubtitleTextView != null) {
                        LocationActivity.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoPlacesFoundInfo", R.string.NoPlacesFoundInfo, LocationActivity.this.searchAdapter.getLastSearchString())));
                    }
                    super.notifyDataSetChanged();
                }
            };
            this.searchAdapter = r02;
            r02.setDelegate(0, new LocationActivity$$ExternalSyntheticLambda38(this));
            frameLayout3.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LocationActivity$$ExternalSyntheticLambda45(this));
        } else if ((messageObject4 != null && !messageObject4.isLiveLocation()) || this.chatLocation != null) {
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
        MessageObject messageObject6 = this.messageObject;
        if (messageObject6 != null && this.locationType == 6) {
            this.adapter.setMessageObject(messageObject6);
        }
        for (int i5 = 0; i5 < 2; i5++) {
            this.undoView[i5] = new UndoView(context3);
            this.undoView[i5].setAdditionalTranslationY((float) AndroidUtilities.dp(10.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.undoView[i5].setTranslationZ((float) AndroidUtilities.dp(5.0f));
            }
            this.mapViewClip.addView(this.undoView[i5], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        AnonymousClass13 r03 = new View(context3) {
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
        this.shadow = r03;
        if (Build.VERSION.SDK_INT >= 21) {
            r03.setTranslationZ((float) AndroidUtilities.dp(6.0f));
        }
        this.mapViewClip.addView(this.shadow, layoutParams3);
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
    public /* synthetic */ void lambda$createView$0(View view) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery((String) null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        this.mapTypeButton.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(int i) {
        IMapsProvider.IMap iMap = this.map;
        if (iMap != null) {
            if (i == 2) {
                iMap.setMapType(0);
            } else if (i == 3) {
                iMap.setMapType(1);
            } else if (i == 4) {
                iMap.setMapType(2);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        IMapsProvider.IMap iMap;
        Activity parentActivity;
        if (Build.VERSION.SDK_INT >= 23 && (parentActivity = getParentActivity()) != null && parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            showPermissionAlert(false);
        } else if (checkGpsEnabled()) {
            if (this.messageObject == null && this.chatLocation == null) {
                if (!(this.myLocation == null || this.map == null)) {
                    this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
                    this.locationButton.setTag("location_actionActiveIcon");
                    this.adapter.setCustomLocation((Location) null);
                    this.userLocationMoved = false;
                    showSearchPlacesButton(false);
                    this.map.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLng(new IMapsProvider.LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude())));
                    if (this.searchedForCustomLocations) {
                        Location location = this.myLocation;
                        if (location != null) {
                            this.adapter.searchPlacesWithQuery((String) null, location, true, true);
                        }
                        this.searchedForCustomLocations = false;
                        showResults();
                    }
                }
            } else if (!(this.myLocation == null || (iMap = this.map) == null)) {
                iMap.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(new IMapsProvider.LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()), this.map.getMaxZoomLevel() - 4.0f));
            }
            removeInfoView();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(View view) {
        if (getParentActivity() != null && this.myLocation != null && checkGpsEnabled() && this.map != null) {
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
            this.proximityButton.setImageResource(R.drawable.msg_location_alert);
            IMapsProvider.ICircle iCircle = this.proximityCircle;
            if (iCircle != null) {
                iCircle.remove();
                this.proximityCircle = null;
            }
            this.canUndo = true;
            getUndoView().showWithAction(0, 25, (Object) 0, (Object) null, (Runnable) new LocationActivity$$ExternalSyntheticLambda12(this), (Runnable) new LocationActivity$$ExternalSyntheticLambda25(this, sharingLocationInfo));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4() {
        getLocationController().setProximityLocation(this.dialogId, 0, true);
        this.canUndo = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(LocationController.SharingLocationInfo sharingLocationInfo) {
        this.proximityButton.setImageResource(R.drawable.msg_location_alert2);
        createCircle(sharingLocationInfo.proximityMeters);
        this.canUndo = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8() {
        updateClipView(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$14(View view, int i) {
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
                tLRPC$TL_channels_editLocation.channel = getMessagesController().getInputChannel(-this.dialogId);
                TLRPC$TL_inputGeoPoint tLRPC$TL_inputGeoPoint = new TLRPC$TL_inputGeoPoint();
                tLRPC$TL_channels_editLocation.geo_point = tLRPC$TL_inputGeoPoint;
                TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$TL_messageMediaVenue.geo;
                tLRPC$TL_inputGeoPoint.lat = tLRPC$GeoPoint.lat;
                tLRPC$TL_inputGeoPoint._long = tLRPC$GeoPoint._long;
                alertDialogArr[0].setOnCancelListener(new LocationActivity$$ExternalSyntheticLambda0(this, getConnectionsManager().sendRequest(tLRPC$TL_channels_editLocation, new LocationActivity$$ExternalSyntheticLambda34(this, alertDialogArr, tLRPC$TL_messageMediaVenue))));
                showDialog(alertDialogArr[0]);
            }
        } else if (i2 == 5) {
            IMapsProvider.IMap iMap = this.map;
            if (iMap != null) {
                IMapsProvider mapsProvider = ApplicationLoader.getMapsProvider();
                TLRPC$GeoPoint tLRPC$GeoPoint2 = this.chatLocation.geo_point;
                iMap.animateCamera(mapsProvider.newCameraUpdateLatLngZoom(new IMapsProvider.LatLng(tLRPC$GeoPoint2.lat, tLRPC$GeoPoint2._long), this.map.getMaxZoomLevel() - 4.0f));
            }
        } else if (i == 1 && (messageObject2 = this.messageObject) != null && (!messageObject2.isLiveLocation() || this.locationType == 6)) {
            IMapsProvider.IMap iMap2 = this.map;
            if (iMap2 != null) {
                IMapsProvider mapsProvider2 = ApplicationLoader.getMapsProvider();
                TLRPC$GeoPoint tLRPC$GeoPoint3 = this.messageObject.messageOwner.media.geo;
                iMap2.animateCamera(mapsProvider2.newCameraUpdateLatLngZoom(new IMapsProvider.LatLng(tLRPC$GeoPoint3.lat, tLRPC$GeoPoint3._long), this.map.getMaxZoomLevel() - 4.0f));
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
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new LocationActivity$$ExternalSyntheticLambda39(this, item));
                } else if (item instanceof LiveLocation) {
                    this.map.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(((LiveLocation) item).marker.getPosition(), this.map.getMaxZoomLevel() - 4.0f));
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
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new LocationActivity$$ExternalSyntheticLambda40(this, tLRPC$TL_messageMediaGeo));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$10(AlertDialog[] alertDialogArr, TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LocationActivity$$ExternalSyntheticLambda27(this, alertDialogArr, tLRPC$TL_messageMediaVenue));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(AlertDialog[] alertDialogArr, TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaVenue, 4, true, 0);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$12(TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo, boolean z, int i) {
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeo, this.locationType, z, i);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(Object obj, boolean z, int i) {
        this.delegate.didSelectLocation((TLRPC$TL_messageMediaVenue) obj, this.locationType, z, i);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$15(MotionEvent motionEvent, IMapsProvider.ICallableMethod iCallableMethod) {
        MotionEvent motionEvent2;
        if (this.yOffset != 0.0f) {
            motionEvent = MotionEvent.obtain(motionEvent);
            motionEvent.offsetLocation(0.0f, (-this.yOffset) / 2.0f);
            motionEvent2 = motionEvent;
        } else {
            motionEvent2 = null;
        }
        boolean booleanValue = ((Boolean) iCallableMethod.call(motionEvent)).booleanValue();
        if (motionEvent2 != null) {
            motionEvent2.recycle();
        }
        return booleanValue;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$16(MotionEvent motionEvent, IMapsProvider.ICallableMethod iCallableMethod) {
        Location location;
        if (this.messageObject == null && this.chatLocation == null) {
            if (motionEvent.getAction() == 0) {
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.setDuration(200);
                this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.markerImageView, View.TRANSLATION_Y, new float[]{(float) (this.markerTop - AndroidUtilities.dp(10.0f))})});
                this.animatorSet.start();
            } else if (motionEvent.getAction() == 1) {
                AnimatorSet animatorSet4 = this.animatorSet;
                if (animatorSet4 != null) {
                    animatorSet4.cancel();
                }
                this.yOffset = 0.0f;
                AnimatorSet animatorSet5 = new AnimatorSet();
                this.animatorSet = animatorSet5;
                animatorSet5.setDuration(200);
                this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.markerImageView, View.TRANSLATION_Y, new float[]{(float) this.markerTop})});
                this.animatorSet.start();
                this.adapter.fetchLocationAddress();
            }
            if (motionEvent.getAction() == 2) {
                if (!this.userLocationMoved) {
                    this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
                    this.locationButton.setTag("location_actionIcon");
                    this.userLocationMoved = true;
                }
                IMapsProvider.IMap iMap = this.map;
                if (!(iMap == null || (location = this.userLocation) == null)) {
                    location.setLatitude(iMap.getCameraPosition().target.latitude);
                    this.userLocation.setLongitude(this.map.getCameraPosition().target.longitude);
                }
                this.adapter.setCustomLocation(this.userLocation);
            }
        }
        return ((Boolean) iCallableMethod.call(motionEvent)).booleanValue();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$18() {
        AndroidUtilities.runOnUIThread(new LocationActivity$$ExternalSyntheticLambda15(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$17() {
        IMapsProvider.ICameraUpdate iCameraUpdate = this.moveToBounds;
        if (iCameraUpdate != null) {
            this.map.moveCamera(iCameraUpdate);
            this.moveToBounds = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$21(IMapsProvider.IMapView iMapView) {
        try {
            iMapView.onCreate((Bundle) null);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new LocationActivity$$ExternalSyntheticLambda24(this, iMapView));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$20(IMapsProvider.IMapView iMapView) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                iMapView.onCreate((Bundle) null);
                ApplicationLoader.getMapsProvider().initializeMaps(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new LocationActivity$$ExternalSyntheticLambda10(this));
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
    public /* synthetic */ void lambda$createView$19(IMapsProvider.IMap iMap) {
        this.map = iMap;
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            this.map.setMapStyle(ApplicationLoader.getMapsProvider().loadRawResourceStyle(ApplicationLoader.applicationContext, R.raw.mapstyle_night));
        }
        this.map.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        onMapInit();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$22(ArrayList arrayList) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$24(View view, int i) {
        TLRPC$TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item != null && this.delegate != null) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                this.delegate.didSelectLocation(item, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new LocationActivity$$ExternalSyntheticLambda41(this, item));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$23(TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, boolean z, int i) {
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
            org.telegram.tgnet.TLRPC$User r1 = r11.user     // Catch:{ all -> 0x0102 }
            if (r1 == 0) goto L_0x000c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r1.photo     // Catch:{ all -> 0x0102 }
            if (r1 == 0) goto L_0x000c
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ all -> 0x0102 }
            goto L_0x0018
        L_0x000c:
            org.telegram.tgnet.TLRPC$Chat r1 = r11.chat     // Catch:{ all -> 0x0102 }
            if (r1 == 0) goto L_0x0017
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r1.photo     // Catch:{ all -> 0x0102 }
            if (r1 == 0) goto L_0x0017
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ all -> 0x0102 }
            goto L_0x0018
        L_0x0017:
            r1 = r0
        L_0x0018:
            r2 = 1115160576(0x42780000, float:62.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x0102 }
            r4 = 1118437376(0x42aa0000, float:85.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x0102 }
            android.graphics.Bitmap$Config r6 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0102 }
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r3, r5, r6)     // Catch:{ all -> 0x0102 }
            r5 = 0
            r3.eraseColor(r5)     // Catch:{ all -> 0x00ff }
            android.graphics.Canvas r6 = new android.graphics.Canvas     // Catch:{ all -> 0x00ff }
            r6.<init>(r3)     // Catch:{ all -> 0x00ff }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x00ff }
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ all -> 0x00ff }
            int r8 = org.telegram.messenger.R.drawable.map_pin_photo     // Catch:{ all -> 0x00ff }
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)     // Catch:{ all -> 0x00ff }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x00ff }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x00ff }
            r7.setBounds(r5, r5, r2, r4)     // Catch:{ all -> 0x00ff }
            r7.draw(r6)     // Catch:{ all -> 0x00ff }
            android.graphics.Paint r2 = new android.graphics.Paint     // Catch:{ all -> 0x00ff }
            r4 = 1
            r2.<init>(r4)     // Catch:{ all -> 0x00ff }
            android.graphics.RectF r7 = new android.graphics.RectF     // Catch:{ all -> 0x00ff }
            r7.<init>()     // Catch:{ all -> 0x00ff }
            r6.save()     // Catch:{ all -> 0x00ff }
            r8 = 1112014848(0x42480000, float:50.0)
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r1 == 0) goto L_0x00c9
            org.telegram.messenger.FileLoader r11 = r10.getFileLoader()     // Catch:{ all -> 0x00ff }
            java.io.File r11 = r11.getPathToAttach(r1, r4)     // Catch:{ all -> 0x00ff }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x00ff }
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeFile(r11)     // Catch:{ all -> 0x00ff }
            if (r11 == 0) goto L_0x00f8
            android.graphics.BitmapShader r1 = new android.graphics.BitmapShader     // Catch:{ all -> 0x00ff }
            android.graphics.Shader$TileMode r4 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x00ff }
            r1.<init>(r11, r4, r4)     // Catch:{ all -> 0x00ff }
            android.graphics.Matrix r4 = new android.graphics.Matrix     // Catch:{ all -> 0x00ff }
            r4.<init>()     // Catch:{ all -> 0x00ff }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x00ff }
            float r5 = (float) r5     // Catch:{ all -> 0x00ff }
            int r11 = r11.getWidth()     // Catch:{ all -> 0x00ff }
            float r11 = (float) r11     // Catch:{ all -> 0x00ff }
            float r5 = r5 / r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00ff }
            float r11 = (float) r11     // Catch:{ all -> 0x00ff }
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00ff }
            float r8 = (float) r8     // Catch:{ all -> 0x00ff }
            r4.postTranslate(r11, r8)     // Catch:{ all -> 0x00ff }
            r4.postScale(r5, r5)     // Catch:{ all -> 0x00ff }
            r2.setShader(r1)     // Catch:{ all -> 0x00ff }
            r1.setLocalMatrix(r4)     // Catch:{ all -> 0x00ff }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00ff }
            float r11 = (float) r11     // Catch:{ all -> 0x00ff }
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00ff }
            float r1 = (float) r1     // Catch:{ all -> 0x00ff }
            r4 = 1113587712(0x42600000, float:56.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x00ff }
            float r5 = (float) r5     // Catch:{ all -> 0x00ff }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x00ff }
            float r4 = (float) r4     // Catch:{ all -> 0x00ff }
            r7.set(r11, r1, r5, r4)     // Catch:{ all -> 0x00ff }
            r11 = 1103626240(0x41CLASSNAME, float:25.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x00ff }
            float r1 = (float) r1     // Catch:{ all -> 0x00ff }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x00ff }
            float r11 = (float) r11     // Catch:{ all -> 0x00ff }
            r6.drawRoundRect(r7, r1, r11, r2)     // Catch:{ all -> 0x00ff }
            goto L_0x00f8
        L_0x00c9:
            org.telegram.ui.Components.AvatarDrawable r1 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x00ff }
            r1.<init>()     // Catch:{ all -> 0x00ff }
            org.telegram.tgnet.TLRPC$User r2 = r11.user     // Catch:{ all -> 0x00ff }
            if (r2 == 0) goto L_0x00d6
            r1.setInfo((org.telegram.tgnet.TLRPC$User) r2)     // Catch:{ all -> 0x00ff }
            goto L_0x00dd
        L_0x00d6:
            org.telegram.tgnet.TLRPC$Chat r11 = r11.chat     // Catch:{ all -> 0x00ff }
            if (r11 == 0) goto L_0x00dd
            r1.setInfo((org.telegram.tgnet.TLRPC$Chat) r11)     // Catch:{ all -> 0x00ff }
        L_0x00dd:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00ff }
            float r11 = (float) r11     // Catch:{ all -> 0x00ff }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x00ff }
            float r2 = (float) r2     // Catch:{ all -> 0x00ff }
            r6.translate(r11, r2)     // Catch:{ all -> 0x00ff }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x00ff }
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x00ff }
            r1.setBounds(r5, r5, r11, r2)     // Catch:{ all -> 0x00ff }
            r1.draw(r6)     // Catch:{ all -> 0x00ff }
        L_0x00f8:
            r6.restore()     // Catch:{ all -> 0x00ff }
            r6.setBitmap(r0)     // Catch:{ Exception -> 0x0107 }
            goto L_0x0107
        L_0x00ff:
            r11 = move-exception
            r0 = r3
            goto L_0x0103
        L_0x0102:
            r11 = move-exception
        L_0x0103:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            r3 = r0
        L_0x0107:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LocationActivity.createUserBitmap(org.telegram.ui.LocationActivity$LiveLocation):android.graphics.Bitmap");
    }

    private long getMessageId(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message.from_id != null) {
            return MessageObject.getFromChatId(tLRPC$Message);
        }
        return MessageObject.getDialogId(tLRPC$Message);
    }

    private void openProximityAlert() {
        IMapsProvider.ICircle iCircle = this.proximityCircle;
        if (iCircle == null) {
            createCircle(500);
        } else {
            this.previousRadius = iCircle.getRadius();
        }
        TLRPC$User user = DialogObject.isUserDialog(this.dialogId) ? getMessagesController().getUser(Long.valueOf(this.dialogId)) : null;
        ProximitySheet proximitySheet2 = new ProximitySheet(getParentActivity(), user, new LocationActivity$$ExternalSyntheticLambda42(this), new LocationActivity$$ExternalSyntheticLambda43(this, user), new LocationActivity$$ExternalSyntheticLambda14(this));
        this.proximitySheet = proximitySheet2;
        ((FrameLayout) this.fragmentView).addView(proximitySheet2, LayoutHelper.createFrame(-1, -1.0f));
        this.proximitySheet.show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$openProximityAlert$25(boolean z, int i) {
        IMapsProvider.ICircle iCircle = this.proximityCircle;
        if (iCircle != null) {
            iCircle.setRadius((double) i);
            if (z) {
                moveToBounds(i, true, true);
            }
        }
        if (DialogObject.isChatDialog(this.dialogId)) {
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
    public /* synthetic */ boolean lambda$openProximityAlert$27(TLRPC$User tLRPC$User, boolean z, int i) {
        if (getLocationController().getSharingLocationInfo(this.dialogId) == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("ShareLocationAlertTitle", R.string.ShareLocationAlertTitle));
            builder.setMessage(LocaleController.getString("ShareLocationAlertText", R.string.ShareLocationAlertText));
            builder.setPositiveButton(LocaleController.getString("ShareLocationAlertButton", R.string.ShareLocationAlertButton), new LocationActivity$$ExternalSyntheticLambda3(this, tLRPC$User, i));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
            return false;
        }
        this.proximitySheet.setRadiusSet();
        this.proximityButton.setImageResource(R.drawable.msg_location_alert2);
        getUndoView().showWithAction(0, 24, (Object) Integer.valueOf(i), (Object) tLRPC$User, (Runnable) null, (Runnable) null);
        getLocationController().setProximityLocation(this.dialogId, i, true);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openProximityAlert$26(TLRPC$User tLRPC$User, int i, DialogInterface dialogInterface, int i2) {
        lambda$openShareLiveLocation$30(tLRPC$User, 900, i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openProximityAlert$28() {
        IMapsProvider.IMap iMap = this.map;
        if (iMap != null) {
            iMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        }
        if (!this.proximitySheet.getRadiusSet()) {
            double d = this.previousRadius;
            if (d > 0.0d) {
                this.proximityCircle.setRadius(d);
            } else {
                IMapsProvider.ICircle iCircle = this.proximityCircle;
                if (iCircle != null) {
                    iCircle.remove();
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
                    AlertsCreator.createBackgroundLocationPermissionDialog(parentActivity, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), new LocationActivity$$ExternalSyntheticLambda18(this), (Theme.ResourcesProvider) null).show();
                    return;
                }
            }
            TLRPC$User user = DialogObject.isUserDialog(this.dialogId) ? getMessagesController().getUser(Long.valueOf(this.dialogId)) : null;
            showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new LocationActivity$$ExternalSyntheticLambda32(this, user, i), (Theme.ResourcesProvider) null));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openShareLiveLocation$29() {
        openShareLiveLocation(this.askWithRadius);
    }

    /* access modifiers changed from: private */
    /* renamed from: shareLiveLocation */
    public void lambda$openShareLiveLocation$30(TLRPC$User tLRPC$User, int i, int i2) {
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
            this.proximityButton.setImageResource(R.drawable.msg_location_alert2);
            ProximitySheet proximitySheet2 = this.proximitySheet;
            if (proximitySheet2 != null) {
                proximitySheet2.dismiss();
            }
            getUndoView().showWithAction(0, 24, (Object) Integer.valueOf(i2), (Object) tLRPC$User, (Runnable) null, (Runnable) null);
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
                    IMapsProvider.IMarkerOptions onCreateMarkerOptions = ApplicationLoader.getMapsProvider().onCreateMarkerOptions();
                    TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$TL_messageMediaVenue.geo;
                    IMapsProvider.IMarkerOptions position = onCreateMarkerOptions.position(new IMapsProvider.LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long));
                    position.icon(createPlaceBitmap(i2));
                    position.anchor(0.5f, 0.5f);
                    position.title(tLRPC$TL_messageMediaVenue.title);
                    position.snippet(tLRPC$TL_messageMediaVenue.address);
                    VenueLocation venueLocation = new VenueLocation();
                    venueLocation.num = i2;
                    IMapsProvider.IMarker addMarker = this.map.addMarker(position);
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
        Location location;
        TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$Message.media.geo;
        IMapsProvider.LatLng latLng = new IMapsProvider.LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long);
        LiveLocation liveLocation = this.markersMap.get(MessageObject.getFromChatId(tLRPC$Message));
        if (liveLocation == null) {
            liveLocation = new LiveLocation();
            liveLocation.object = tLRPC$Message;
            if (tLRPC$Message.from_id instanceof TLRPC$TL_peerUser) {
                liveLocation.user = getMessagesController().getUser(Long.valueOf(liveLocation.object.from_id.user_id));
                liveLocation.id = liveLocation.object.from_id.user_id;
            } else {
                long dialogId2 = MessageObject.getDialogId(tLRPC$Message);
                if (DialogObject.isUserDialog(dialogId2)) {
                    liveLocation.user = getMessagesController().getUser(Long.valueOf(dialogId2));
                } else {
                    liveLocation.chat = getMessagesController().getChat(Long.valueOf(-dialogId2));
                }
                liveLocation.id = dialogId2;
            }
            try {
                IMapsProvider.IMarkerOptions position = ApplicationLoader.getMapsProvider().onCreateMarkerOptions().position(latLng);
                Bitmap createUserBitmap = createUserBitmap(liveLocation);
                if (createUserBitmap != null) {
                    position.icon(createUserBitmap);
                    position.anchor(0.5f, 0.907f);
                    liveLocation.marker = this.map.addMarker(position);
                    if (!UserObject.isUserSelf(liveLocation.user)) {
                        IMapsProvider.IMarkerOptions flat = ApplicationLoader.getMapsProvider().onCreateMarkerOptions().position(latLng).flat(true);
                        flat.anchor(0.5f, 0.5f);
                        IMapsProvider.IMarker addMarker = this.map.addMarker(flat);
                        liveLocation.directionMarker = addMarker;
                        int i = tLRPC$Message.media.heading;
                        if (i != 0) {
                            addMarker.setRotation(i);
                            liveLocation.directionMarker.setIcon(R.drawable.map_pin_cone2);
                            liveLocation.hasRotation = true;
                        } else {
                            addMarker.setRotation(0);
                            liveLocation.directionMarker.setIcon(R.drawable.map_pin_circle);
                            liveLocation.hasRotation = false;
                        }
                    }
                    this.markers.add(liveLocation);
                    this.markersMap.put(liveLocation.id, liveLocation);
                    LocationController.SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
                    if (liveLocation.id == getUserConfig().getClientUserId() && sharingLocationInfo != null && liveLocation.object.id == sharingLocationInfo.mid && (location = this.myLocation) != null) {
                        liveLocation.marker.setPosition(new IMapsProvider.LatLng(location.getLatitude(), this.myLocation.getLongitude()));
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
        IMapsProvider.LatLng latLng = new IMapsProvider.LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long);
        LiveLocation liveLocation = new LiveLocation();
        if (DialogObject.isUserDialog(this.dialogId)) {
            liveLocation.user = getMessagesController().getUser(Long.valueOf(this.dialogId));
        } else {
            liveLocation.chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
        }
        liveLocation.id = this.dialogId;
        try {
            IMapsProvider.IMarkerOptions position = ApplicationLoader.getMapsProvider().onCreateMarkerOptions().position(latLng);
            Bitmap createUserBitmap = createUserBitmap(liveLocation);
            if (createUserBitmap != null) {
                position.icon(createUserBitmap);
                position.anchor(0.5f, 0.907f);
                liveLocation.marker = this.map.addMarker(position);
                if (!UserObject.isUserSelf(liveLocation.user)) {
                    IMapsProvider.IMarkerOptions flat = ApplicationLoader.getMapsProvider().onCreateMarkerOptions().position(latLng).flat(true);
                    flat.icon(R.drawable.map_pin_circle);
                    flat.anchor(0.5f, 0.5f);
                    liveLocation.directionMarker = this.map.addMarker(flat);
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
        if (this.map != null) {
            TLRPC$TL_channelLocation tLRPC$TL_channelLocation = this.chatLocation;
            if (tLRPC$TL_channelLocation != null) {
                this.map.moveCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(addUserMarker(tLRPC$TL_channelLocation).marker.getPosition(), this.map.getMaxZoomLevel() - 4.0f));
            } else {
                MessageObject messageObject2 = this.messageObject;
                if (messageObject2 == null) {
                    Location location = new Location("network");
                    this.userLocation = location;
                    TLRPC$TL_channelLocation tLRPC$TL_channelLocation2 = this.initialLocation;
                    if (tLRPC$TL_channelLocation2 != null) {
                        TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$TL_channelLocation2.geo_point;
                        this.map.moveCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(new IMapsProvider.LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long), this.map.getMaxZoomLevel() - 4.0f));
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
                        this.map.moveCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(addUserMarker.marker.getPosition(), this.map.getMaxZoomLevel() - 4.0f));
                    }
                } else {
                    IMapsProvider.LatLng latLng = new IMapsProvider.LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                    try {
                        this.map.addMarker(ApplicationLoader.getMapsProvider().onCreateMarkerOptions().position(latLng).icon(R.drawable.map_pin2));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    this.map.moveCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(latLng, this.map.getMaxZoomLevel() - 4.0f));
                    this.firstFocus = false;
                    getRecentLocations();
                }
            }
            try {
                this.map.setMyLocationEnabled(true);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2, false);
            }
            this.map.getUiSettings().setMyLocationButtonEnabled(false);
            this.map.getUiSettings().setZoomControlsEnabled(false);
            this.map.getUiSettings().setCompassEnabled(false);
            this.map.setOnCameraMoveStartedListener(new LocationActivity$$ExternalSyntheticLambda30(this));
            this.map.setOnMyLocationChangeListener(new LocationActivity$$ExternalSyntheticLambda9(this));
            this.map.setOnMarkerClickListener(new LocationActivity$$ExternalSyntheticLambda31(this));
            this.map.setOnCameraMoveListener(new LocationActivity$$ExternalSyntheticLambda13(this));
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
    public /* synthetic */ void lambda$onMapInit$31(int i) {
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
                        IMapsProvider.CameraPosition cameraPosition = this.map.getCameraPosition();
                        this.forceUpdate = ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(cameraPosition.target, cameraPosition.zoom);
                        this.listView.smoothScrollBy(0, top + dp);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$32(Location location) {
        positionMarker(location);
        getLocationController().setMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onMapInit$33(IMapsProvider.IMarker iMarker) {
        if (!(iMarker.getTag() instanceof VenueLocation)) {
            return true;
        }
        this.markerImageView.setVisibility(4);
        if (!this.userLocationMoved) {
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag("location_actionIcon");
            this.userLocationMoved = true;
        }
        this.overlayView.addInfoView(iMarker);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$34() {
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
                builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor("dialogTopBackground"));
                builder.setMessage(LocaleController.getString("GpsDisabledAlertText", R.string.GpsDisabledAlertText));
                builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new LocationActivity$$ExternalSyntheticLambda1(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
                return false;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkGpsEnabled$35(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception unused) {
            }
        }
    }

    private void createCircle(int i) {
        if (this.map != null) {
            List asList = Arrays.asList(new IMapsProvider.PatternItem[]{new IMapsProvider.PatternItem.Gap(20), new IMapsProvider.PatternItem.Dash(20)});
            IMapsProvider.ICircleOptions onCreateCircleOptions = ApplicationLoader.getMapsProvider().onCreateCircleOptions();
            onCreateCircleOptions.center(new IMapsProvider.LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
            onCreateCircleOptions.radius((double) i);
            if (isActiveThemeDark()) {
                onCreateCircleOptions.strokeColor(-NUM);
                onCreateCircleOptions.fillColor(NUM);
            } else {
                onCreateCircleOptions.strokeColor(-NUM);
                onCreateCircleOptions.fillColor(NUM);
            }
            onCreateCircleOptions.strokePattern(asList);
            onCreateCircleOptions.strokeWidth(2);
            this.proximityCircle = this.map.addCircle(onCreateCircleOptions);
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
            builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor("dialogTopBackground"));
            if (z) {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("PermissionNoLocationNavigation", R.string.PermissionNoLocationNavigation)));
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("PermissionNoLocationFriends", R.string.PermissionNoLocationFriends)));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new LocationActivity$$ExternalSyntheticLambda2(this));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPermissionAlert$36(DialogInterface dialogInterface, int i) {
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
                if (this.mapView.getView().getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mapView.getView().getParent()).removeView(this.mapView.getView());
                }
            } catch (Exception unused) {
            }
            FrameLayout frameLayout = this.mapViewClip;
            if (frameLayout != null) {
                frameLayout.addView(this.mapView.getView(), 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
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
                ((FrameLayout) view).addView(this.mapView.getView(), 0, LayoutHelper.createFrame(-1, -1, 51));
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
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r0)
            if (r0 == 0) goto L_0x0056
            org.telegram.messenger.MessagesController r0 = r7.getMessagesController()
            long r5 = r7.dialogId
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            org.telegram.ui.Components.HintView r1 = r7.hintView
            int r3 = org.telegram.messenger.R.string.ProximityTooltioUser
            java.lang.Object[] r5 = new java.lang.Object[r4]
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            r5[r2] = r0
            java.lang.String r0 = "ProximityTooltioUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r5)
            r1.setOverrideText(r0)
            goto L_0x0063
        L_0x0056:
            org.telegram.ui.Components.HintView r0 = r7.hintView
            int r1 = org.telegram.messenger.R.string.ProximityTooltioGroup
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
                if (this.mapView.getView().getVisibility() == 0) {
                    this.mapView.getView().setVisibility(4);
                    this.mapViewClip.setVisibility(4);
                    MapOverlayView mapOverlayView = this.overlayView;
                    if (mapOverlayView != null) {
                        mapOverlayView.setVisibility(4);
                    }
                }
            } else if (this.mapView.getView().getVisibility() == 4) {
                this.mapView.getView().setVisibility(0);
                this.mapViewClip.setVisibility(0);
                MapOverlayView mapOverlayView2 = this.overlayView;
                if (mapOverlayView2 != null) {
                    mapOverlayView2.setVisibility(0);
                }
            }
            this.mapViewClip.setTranslationY((float) Math.min(0, i2));
            int i3 = -i2;
            int i4 = i3 / 2;
            this.mapView.getView().setTranslationY((float) Math.max(0, i4));
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
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mapView.getView().getLayoutParams();
                if (!(layoutParams2 == null || layoutParams2.height == this.overScrollHeight + AndroidUtilities.dp(10.0f))) {
                    layoutParams2.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    IMapsProvider.IMap iMap = this.map;
                    if (iMap != null) {
                        iMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.getView().setLayoutParams(layoutParams2);
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
                FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.mapView.getView().getLayoutParams();
                if (layoutParams5 != null) {
                    layoutParams5.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    IMapsProvider.IMap iMap = this.map;
                    if (iMap != null) {
                        iMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.getView().setLayoutParams(layoutParams5);
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
                    this.listView.post(new LocationActivity$$ExternalSyntheticLambda19(this, i3));
                    return;
                }
                updateClipView(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fixLayoutInternal$37(int i) {
        this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) i));
        updateClipView(false);
    }

    @SuppressLint({"MissingPermission"})
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
                IMapsProvider.LatLng latLng = new IMapsProvider.LatLng(location.getLatitude(), location.getLongitude());
                liveLocation.marker.setPosition(latLng);
                IMapsProvider.IMarker iMarker = liveLocation.directionMarker;
                if (iMarker != null) {
                    iMarker.setPosition(latLng);
                }
            }
            if (this.messageObject == null && this.chatLocation == null && this.map != null) {
                IMapsProvider.LatLng latLng2 = new IMapsProvider.LatLng(location.getLatitude(), location.getLongitude());
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
                        this.map.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLng(latLng2));
                    } else {
                        this.firstWas = true;
                        this.map.moveCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(latLng2, this.map.getMaxZoomLevel() - 4.0f));
                    }
                }
            } else {
                this.adapter.setGpsLocation(this.myLocation);
            }
            ProximitySheet proximitySheet2 = this.proximitySheet;
            if (proximitySheet2 != null) {
                proximitySheet2.updateText(true, true);
            }
            IMapsProvider.ICircle iCircle = this.proximityCircle;
            if (iCircle != null) {
                iCircle.setCenter(new IMapsProvider.LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
            }
        }
    }

    public void setMessageObject(MessageObject messageObject2) {
        this.messageObject = messageObject2;
        this.dialogId = messageObject2.getDialogId();
    }

    public void setChatLocation(long j, TLRPC$TL_channelLocation tLRPC$TL_channelLocation) {
        this.dialogId = -j;
        this.chatLocation = tLRPC$TL_channelLocation;
    }

    public void setDialogId(long j) {
        this.dialogId = j;
    }

    public void setInitialLocation(TLRPC$TL_channelLocation tLRPC$TL_channelLocation) {
        this.initialLocation = tLRPC$TL_channelLocation;
    }

    private static IMapsProvider.LatLng move(IMapsProvider.LatLng latLng, double d, double d2) {
        double meterToLongitude = meterToLongitude(d2, latLng.latitude);
        return new IMapsProvider.LatLng(latLng.latitude + meterToLatitude(d), latLng.longitude + meterToLongitude);
    }

    private static double meterToLongitude(double d, double d2) {
        return Math.toDegrees(d / (Math.cos(Math.toRadians(d2)) * 6366198.0d));
    }

    private static double meterToLatitude(double d) {
        return Math.toDegrees(d / 6366198.0d);
    }

    private void fetchRecentLocations(ArrayList<TLRPC$Message> arrayList) {
        IMapsProvider.ILatLngBoundsBuilder onCreateLatLngBoundsBuilder = this.firstFocus ? ApplicationLoader.getMapsProvider().onCreateLatLngBoundsBuilder() : null;
        int currentTime = getConnectionsManager().getCurrentTime();
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$Message tLRPC$Message = arrayList.get(i);
            int i2 = tLRPC$Message.date;
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (i2 + tLRPC$MessageMedia.period > currentTime) {
                if (onCreateLatLngBoundsBuilder != null) {
                    TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$MessageMedia.geo;
                    onCreateLatLngBoundsBuilder.include(new IMapsProvider.LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long));
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
        if (onCreateLatLngBoundsBuilder != null) {
            if (this.firstFocus) {
                this.listView.smoothScrollBy(0, AndroidUtilities.dp(99.0f));
            }
            this.firstFocus = false;
            this.adapter.setLiveLocations(this.markers);
            if (this.messageObject.isLiveLocation()) {
                try {
                    IMapsProvider.LatLng center = onCreateLatLngBoundsBuilder.build().getCenter();
                    IMapsProvider.LatLng move = move(center, 100.0d, 100.0d);
                    onCreateLatLngBoundsBuilder.include(move(center, -100.0d, -100.0d));
                    onCreateLatLngBoundsBuilder.include(move);
                    IMapsProvider.ILatLngBounds build = onCreateLatLngBoundsBuilder.build();
                    if (arrayList.size() > 1) {
                        try {
                            IMapsProvider.ICameraUpdate newCameraUpdateLatLngBounds = ApplicationLoader.getMapsProvider().newCameraUpdateLatLngBounds(build, AndroidUtilities.dp(113.0f));
                            this.moveToBounds = newCameraUpdateLatLngBounds;
                            this.map.moveCamera(newCameraUpdateLatLngBounds);
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
        IMapsProvider.ILatLngBoundsBuilder onCreateLatLngBoundsBuilder = ApplicationLoader.getMapsProvider().onCreateLatLngBoundsBuilder();
        onCreateLatLngBoundsBuilder.include(new IMapsProvider.LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
        if (z) {
            try {
                int max = Math.max(i, 250);
                IMapsProvider.LatLng center = onCreateLatLngBoundsBuilder.build().getCenter();
                double d = (double) max;
                IMapsProvider.LatLng move = move(center, d, d);
                double d2 = (double) (-max);
                onCreateLatLngBoundsBuilder.include(move(center, d2, d2));
                onCreateLatLngBoundsBuilder.include(move);
                IMapsProvider.ILatLngBounds build = onCreateLatLngBoundsBuilder.build();
                try {
                    this.map.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), (int) (((float) (this.proximitySheet.getCustomView().getMeasuredHeight() - AndroidUtilities.dp(40.0f))) + this.mapViewClip.getTranslationY()));
                    if (z2) {
                        this.map.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngBounds(build, 0), 500, (IMapsProvider.ICancelableCallback) null);
                    } else {
                        this.map.moveCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngBounds(build, 0));
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
                int i3 = tLRPC$Message.date;
                TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                if (i3 + tLRPC$MessageMedia.period > currentTime) {
                    TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$MessageMedia.geo;
                    onCreateLatLngBoundsBuilder.include(new IMapsProvider.LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long));
                }
            }
            IMapsProvider.LatLng center2 = onCreateLatLngBoundsBuilder.build().getCenter();
            IMapsProvider.LatLng move2 = move(center2, 100.0d, 100.0d);
            onCreateLatLngBoundsBuilder.include(move(center2, -100.0d, -100.0d));
            onCreateLatLngBoundsBuilder.include(move2);
            IMapsProvider.ILatLngBounds build2 = onCreateLatLngBoundsBuilder.build();
            try {
                this.map.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), this.proximitySheet.getCustomView().getMeasuredHeight() - AndroidUtilities.dp(100.0f));
                this.map.moveCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngBounds(build2, 0));
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
        if (DialogObject.isChatDialog(this.dialogId)) {
            TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TLRPC$TL_messages_getRecentLocations tLRPC$TL_messages_getRecentLocations = new TLRPC$TL_messages_getRecentLocations();
        long dialogId2 = this.messageObject.getDialogId();
        tLRPC$TL_messages_getRecentLocations.peer = getMessagesController().getInputPeer(dialogId2);
        tLRPC$TL_messages_getRecentLocations.limit = 100;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getRecentLocations, new LocationActivity$$ExternalSyntheticLambda33(this, dialogId2));
        if (arrayList != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getRecentLocations$40(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new LocationActivity$$ExternalSyntheticLambda26(this, tLObject, j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getRecentLocations$39(TLObject tLObject, long j) {
        if (this.map != null) {
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
                LocationActivity$$ExternalSyntheticLambda17 locationActivity$$ExternalSyntheticLambda17 = new LocationActivity$$ExternalSyntheticLambda17(this);
                this.markAsReadRunnable = locationActivity$$ExternalSyntheticLambda17;
                AndroidUtilities.runOnUIThread(locationActivity$$ExternalSyntheticLambda17, 5000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getRecentLocations$38() {
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
            return;
        }
        if (i == NotificationCenter.locationPermissionGranted) {
            this.locationDenied = false;
            LocationActivityAdapter locationActivityAdapter3 = this.adapter;
            if (locationActivityAdapter3 != null) {
                locationActivityAdapter3.setMyLocationDenied(false);
            }
            IMapsProvider.IMap iMap = this.map;
            if (iMap != null) {
                try {
                    iMap.setMyLocationEnabled(true);
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
            if (!objArr[2].booleanValue() && objArr[0].longValue() == this.dialogId && this.messageObject != null) {
                ArrayList arrayList = objArr[1];
                boolean z = false;
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    MessageObject messageObject2 = (MessageObject) arrayList.get(i3);
                    if (messageObject2.isLiveLocation()) {
                        addUserMarker(messageObject2.messageOwner);
                        z = true;
                    } else if ((messageObject2.messageOwner.action instanceof TLRPC$TL_messageActionGeoProximityReached) && DialogObject.isUserDialog(messageObject2.getDialogId())) {
                        this.proximityButton.setImageResource(R.drawable.msg_location_alert);
                        IMapsProvider.ICircle iCircle = this.proximityCircle;
                        if (iCircle != null) {
                            iCircle.remove();
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
                            TLRPC$Message tLRPC$Message = messageObject3.messageOwner;
                            liveLocation.object = tLRPC$Message;
                            TLRPC$GeoPoint tLRPC$GeoPoint = tLRPC$Message.media.geo;
                            IMapsProvider.LatLng latLng = new IMapsProvider.LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long);
                            liveLocation.marker.setPosition(latLng);
                            IMapsProvider.IMarker iMarker = liveLocation.directionMarker;
                            if (iMarker != null) {
                                iMarker.getPosition();
                                liveLocation.directionMarker.setPosition(latLng);
                                int i5 = messageObject3.messageOwner.media.heading;
                                if (i5 != 0) {
                                    liveLocation.directionMarker.setRotation(i5);
                                    if (!liveLocation.hasRotation) {
                                        liveLocation.directionMarker.setIcon(R.drawable.map_pin_cone2);
                                        liveLocation.hasRotation = true;
                                    }
                                } else if (liveLocation.hasRotation) {
                                    liveLocation.directionMarker.setRotation(0);
                                    liveLocation.directionMarker.setIcon(R.drawable.map_pin_circle);
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

    public void onPause() {
        super.onPause();
        IMapsProvider.IMapView iMapView = this.mapView;
        if (iMapView != null && this.mapsInitialized) {
            try {
                iMapView.onPause();
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
        if (proximitySheet2 != null) {
            proximitySheet2.dismiss();
            return false;
        } else if (onCheckGlScreenshot()) {
            return false;
        } else {
            return super.onBackPressed();
        }
    }

    public void finishFragment(boolean z) {
        if (!onCheckGlScreenshot()) {
            super.finishFragment(z);
        }
    }

    private boolean onCheckGlScreenshot() {
        IMapsProvider.IMapView iMapView = this.mapView;
        if (iMapView == null || iMapView.getGlSurfaceView() == null || this.hasScreenshot) {
            return false;
        }
        GLSurfaceView glSurfaceView = this.mapView.getGlSurfaceView();
        glSurfaceView.queueEvent(new LocationActivity$$ExternalSyntheticLambda21(this, glSurfaceView));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCheckGlScreenshot$43(GLSurfaceView gLSurfaceView) {
        if (gLSurfaceView.getWidth() != 0 && gLSurfaceView.getHeight() != 0) {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(gLSurfaceView.getWidth() * gLSurfaceView.getHeight() * 4);
            GLES20.glReadPixels(0, 0, gLSurfaceView.getWidth(), gLSurfaceView.getHeight(), 6408, 5121, allocateDirect);
            Bitmap createBitmap = Bitmap.createBitmap(gLSurfaceView.getWidth(), gLSurfaceView.getHeight(), Bitmap.Config.ARGB_8888);
            createBitmap.copyPixelsFromBuffer(allocateDirect);
            Matrix matrix = new Matrix();
            matrix.preScale(1.0f, -1.0f);
            Bitmap createBitmap2 = Bitmap.createBitmap(createBitmap, 0, 0, createBitmap.getWidth(), createBitmap.getHeight(), matrix, false);
            createBitmap.recycle();
            AndroidUtilities.runOnUIThread(new LocationActivity$$ExternalSyntheticLambda20(this, createBitmap2, gLSurfaceView));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCheckGlScreenshot$42(Bitmap bitmap, GLSurfaceView gLSurfaceView) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bitmap);
        ViewGroup viewGroup = (ViewGroup) gLSurfaceView.getParent();
        try {
            viewGroup.addView(imageView, viewGroup.indexOfChild(gLSurfaceView));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AndroidUtilities.runOnUIThread(new LocationActivity$$ExternalSyntheticLambda22(this, viewGroup, gLSurfaceView), 100);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCheckGlScreenshot$41(ViewGroup viewGroup, GLSurfaceView gLSurfaceView) {
        try {
            viewGroup.removeView(gLSurfaceView);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.hasScreenshot = true;
        finishFragment();
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
        IMapsProvider.IMapView iMapView = this.mapView;
        if (iMapView != null && this.mapsInitialized) {
            try {
                iMapView.onResume();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        this.onResumeCalled = true;
        IMapsProvider.IMap iMap = this.map;
        if (iMap != null) {
            try {
                iMap.setMyLocationEnabled(true);
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
        IMapsProvider.IMapView iMapView = this.mapView;
        if (iMapView != null && this.mapsInitialized) {
            iMapView.onLowMemory();
        }
    }

    public void setDelegate(LocationActivityDelegate locationActivityDelegate) {
        this.delegate = locationActivityDelegate;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        LocationActivity$$ExternalSyntheticLambda36 locationActivity$$ExternalSyntheticLambda36 = new LocationActivity$$ExternalSyntheticLambda36(this);
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
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda36, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint"));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        arrayList.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        LocationActivity$$ExternalSyntheticLambda36 locationActivity$$ExternalSyntheticLambda362 = locationActivity$$ExternalSyntheticLambda36;
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "actionBarDefaultSubmenuItemIcon"));
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
        arrayList.add(new ThemeDescription(this.mapTypeButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.proximityButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.proximityButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.proximityButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, locationActivity$$ExternalSyntheticLambda362, "avatar_text"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, locationActivity$$ExternalSyntheticLambda362, "avatar_backgroundPink"));
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
    public /* synthetic */ void lambda$getThemeDescriptions$44() {
        this.mapTypeButton.setIconColor(Theme.getColor("location_actionIcon"));
        this.mapTypeButton.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.shadow.invalidate();
        if (this.map == null) {
            return;
        }
        if (isActiveThemeDark()) {
            if (!this.currentMapStyleDark) {
                this.currentMapStyleDark = true;
                this.map.setMapStyle(ApplicationLoader.getMapsProvider().loadRawResourceStyle(ApplicationLoader.applicationContext, R.raw.mapstyle_night));
                IMapsProvider.ICircle iCircle = this.proximityCircle;
                if (iCircle != null) {
                    iCircle.setStrokeColor(-1);
                    this.proximityCircle.setFillColor(NUM);
                }
            }
        } else if (this.currentMapStyleDark) {
            this.currentMapStyleDark = false;
            this.map.setMapStyle((IMapsProvider.IMapStyleOptions) null);
            IMapsProvider.ICircle iCircle2 = this.proximityCircle;
            if (iCircle2 != null) {
                iCircle2.setStrokeColor(-16777216);
                this.proximityCircle.setFillColor(NUM);
            }
        }
    }
}
