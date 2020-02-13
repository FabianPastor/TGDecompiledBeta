package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LocationActivity;

public class LocationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
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
    /* access modifiers changed from: private */
    public LocationActivityAdapter adapter;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private Bitmap[] bitmapCache = new Bitmap[7];
    /* access modifiers changed from: private */
    public TLRPC.TL_channelLocation chatLocation;
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
    private Runnable updateRunnable;
    /* access modifiers changed from: private */
    public Location userLocation;
    /* access modifiers changed from: private */
    public boolean userLocationMoved;
    private boolean wasResults;
    /* access modifiers changed from: private */
    public float yOffset;

    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2);
    }

    static /* synthetic */ boolean lambda$createView$3(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    public class VenueLocation {
        public Marker marker;
        public int num;
        public TLRPC.TL_messageMediaVenue venue;

        public VenueLocation() {
        }
    }

    public class LiveLocation {
        public TLRPC.Chat chat;
        public int id;
        public Marker marker;
        public TLRPC.Message object;
        public TLRPC.User user;

        public LiveLocation() {
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

    public class MapOverlayView extends FrameLayout {
        private HashMap<Marker, View> views = new HashMap<>();

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
                    private final /* synthetic */ LocationActivity.VenueLocation f$1;

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

        public /* synthetic */ void lambda$addInfoView$1$LocationActivity$MapOverlayView(VenueLocation venueLocation, View view) {
            if (LocationActivity.this.parentFragment == null || !LocationActivity.this.parentFragment.isInScheduleMode()) {
                LocationActivity.this.delegate.didSelectLocation(venueLocation.venue, LocationActivity.this.locationType, true, 0);
                LocationActivity.this.finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(LocationActivity.this.getParentActivity(), LocationActivity.this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(venueLocation) {
                private final /* synthetic */ LocationActivity.VenueLocation f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectDate(boolean z, int i) {
                    LocationActivity.MapOverlayView.this.lambda$null$0$LocationActivity$MapOverlayView(this.f$1, z, i);
                }
            });
        }

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
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
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
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.replaceMessagesObjects);
        try {
            if (this.googleMap != null) {
                this.googleMap.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            if (this.mapView != null) {
                this.mapView.onDestroy();
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
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

    public View createView(Context context) {
        FrameLayout.LayoutParams layoutParams;
        FrameLayout frameLayout;
        String str;
        CombinedDrawable combinedDrawable;
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
            this.userLocation = new Location("network");
            this.userLocation.setLatitude(this.chatLocation.geo_point.lat);
            this.userLocation.setLongitude(this.chatLocation.geo_point._long);
        } else if (this.messageObject != null) {
            this.userLocation = new Location("network");
            this.userLocation.setLatitude(this.messageObject.messageOwner.media.geo.lat);
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
                    LocationActivity.this.openShareLiveLocation();
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
                    this.searchItem = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                                    LocationActivity.this.updateEmptyView();
                                } else {
                                    if (LocationActivity.this.otherItem != null) {
                                        LocationActivity.this.otherItem.setVisibility(0);
                                    }
                                    LocationActivity.this.listView.setVisibility(0);
                                    LocationActivity.this.mapViewClip.setVisibility(0);
                                    LocationActivity.this.searchListView.setAdapter((RecyclerView.Adapter) null);
                                    LocationActivity.this.searchListView.setVisibility(8);
                                    LocationActivity.this.updateEmptyView();
                                }
                                LocationActivity.this.searchAdapter.searchDelayed(obj, LocationActivity.this.userLocation);
                            }
                        }
                    });
                    this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
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
                this.otherItem = createMenu.addItem(0, NUM);
                this.otherItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("OpenInExternalApp", NUM));
                if (!getLocationController().isSharingLocation(this.dialogId)) {
                    this.otherItem.addSubItem(5, NUM, (CharSequence) LocaleController.getString("SendLiveLocationMenu", NUM));
                }
                this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            }
        }
        this.fragmentView = new FrameLayout(context2) {
            private boolean first = true;

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (z) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                }
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
        View view = this.fragmentView;
        FrameLayout frameLayout2 = (FrameLayout) view;
        view.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        final Rect rect = new Rect();
        this.shadowDrawable.getPadding(rect);
        int i2 = this.locationType;
        if (i2 == 0 || i2 == 1) {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f) + rect.top);
        } else {
            layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(6.0f) + rect.top);
        }
        FrameLayout.LayoutParams layoutParams2 = layoutParams;
        layoutParams2.gravity = 83;
        this.mapViewClip = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (LocationActivity.this.overlayView != null) {
                    LocationActivity.this.overlayView.updatePositions();
                }
            }
        };
        this.mapViewClip.setBackgroundDrawable(new MapPlaceholderDrawable());
        if (this.messageObject == null && ((i = this.locationType) == 0 || i == 1)) {
            this.searchAreaButton = new SearchButton(context2);
            this.searchAreaButton.setTranslationX((float) (-AndroidUtilities.dp(80.0f)));
            Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable mutate = context.getResources().getDrawable(NUM).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable2 = new CombinedDrawable(mutate, createSimpleSelectorRoundRectDrawable, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
                combinedDrawable2.setFullsize(true);
                createSimpleSelectorRoundRectDrawable = combinedDrawable2;
                frameLayout = frameLayout2;
            } else {
                StateListAnimator stateListAnimator = new StateListAnimator();
                frameLayout = frameLayout2;
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.searchAreaButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.searchAreaButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.searchAreaButton.setStateListAnimator(stateListAnimator);
                this.searchAreaButton.setOutlineProvider(new ViewOutlineProvider() {
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
            this.mapViewClip.addView(this.searchAreaButton, LayoutHelper.createFrame(-2, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
            this.searchAreaButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    LocationActivity.this.lambda$createView$0$LocationActivity(view);
                }
            });
        } else {
            frameLayout = frameLayout2;
        }
        this.mapTypeButton = new ActionBarMenuItem(context2, (ActionBarMenu) null, 0, Theme.getColor("location_actionIcon"));
        this.mapTypeButton.setClickable(true);
        this.mapTypeButton.setSubMenuOpenSide(2);
        this.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        this.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        this.mapTypeButton.addSubItem(2, NUM, (CharSequence) LocaleController.getString("Map", NUM));
        this.mapTypeButton.addSubItem(3, NUM, (CharSequence) LocaleController.getString("Satellite", NUM));
        this.mapTypeButton.addSubItem(4, NUM, (CharSequence) LocaleController.getString("Hybrid", NUM));
        this.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
            mutate2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(mutate2, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable3.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            str = "location_actionPressedBackground";
            combinedDrawable = combinedDrawable3;
        } else {
            StateListAnimator stateListAnimator2 = new StateListAnimator();
            str = "location_actionPressedBackground";
            stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.mapTypeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator2.addState(new int[0], ObjectAnimator.ofFloat(this.mapTypeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.mapTypeButton.setStateListAnimator(stateListAnimator2);
            this.mapTypeButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
            combinedDrawable = createSimpleSelectorCircleDrawable;
        }
        this.mapTypeButton.setBackgroundDrawable(combinedDrawable);
        this.mapTypeButton.setIcon(NUM);
        this.mapViewClip.addView(this.mapTypeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        this.mapTypeButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
            public final void onItemClick(int i) {
                LocationActivity.this.lambda$createView$1$LocationActivity(i);
            }
        });
        this.locationButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor(str));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate3 = context.getResources().getDrawable(NUM).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable4 = new CombinedDrawable(mutate3, createSimpleSelectorCircleDrawable2, 0, 0);
            combinedDrawable4.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable2 = combinedDrawable4;
        } else {
            StateListAnimator stateListAnimator3 = new StateListAnimator();
            stateListAnimator3.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator3.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.locationButton.setStateListAnimator(stateListAnimator3);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider() {
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
        FrameLayout.LayoutParams createFrame = LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f);
        createFrame.bottomMargin += layoutParams2.height - rect.top;
        this.mapViewClip.addView(this.locationButton, createFrame);
        this.locationButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                LocationActivity.this.lambda$createView$2$LocationActivity(view);
            }
        });
        this.emptyView = new LinearLayout(context2);
        this.emptyView.setOrientation(1);
        this.emptyView.setGravity(1);
        this.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        this.emptyView.setVisibility(8);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener($$Lambda$LocationActivity$imGE6wrjywjW00_01AfMqekBLBw.INSTANCE);
        this.emptyImageView = new ImageView(context2);
        this.emptyImageView.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        this.emptyTitleTextView = new TextView(context2);
        this.emptyTitleTextView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setText(LocaleController.getString("NoPlacesFound", NUM));
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        this.emptySubtitleTextView = new TextView(context2);
        this.emptySubtitleTextView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        this.listView = new RecyclerListView(context2);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass8 r0 = new LocationActivityAdapter(context, this.locationType, this.dialogId) {
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
        this.adapter = r0;
        recyclerListView.setAdapter(r0);
        this.adapter.setUpdateRunnable(new Runnable() {
            public final void run() {
                LocationActivity.this.updateClipView();
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                boolean unused = LocationActivity.this.scrolling = i != 0;
                if (!LocationActivity.this.scrolling && LocationActivity.this.forceUpdate != null) {
                    CameraUpdate unused2 = LocationActivity.this.forceUpdate = null;
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                LocationActivity.this.updateClipView();
                if (LocationActivity.this.forceUpdate != null) {
                    LocationActivity locationActivity = LocationActivity.this;
                    float unused = locationActivity.yOffset = locationActivity.yOffset + ((float) i2);
                }
            }
        });
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                LocationActivity.this.lambda$createView$9$LocationActivity(view, i);
            }
        });
        this.adapter.setDelegate(this.dialogId, new BaseLocationAdapter.BaseLocationAdapterDelegate() {
            public final void didLoadSearchResult(ArrayList arrayList) {
                LocationActivity.this.updatePlacesMarkers(arrayList);
            }
        });
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        this.mapView = new MapView(context2) {
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
        };
        new Thread(new Runnable(this.mapView) {
            private final /* synthetic */ MapView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationActivity.this.lambda$createView$12$LocationActivity(this.f$1);
            }
        }).start();
        if (this.messageObject == null && this.chatLocation == null) {
            if (!(this.locationType != 4 || this.dialogId == 0 || (chat = getMessagesController().getChat(Integer.valueOf(-((int) this.dialogId)))) == null)) {
                FrameLayout frameLayout3 = new FrameLayout(context2);
                frameLayout3.setBackgroundResource(NUM);
                this.mapViewClip.addView(frameLayout3, LayoutHelper.createFrame(62, 76, 49));
                BackupImageView backupImageView = new BackupImageView(context2);
                backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
                backupImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) new AvatarDrawable(chat), (Object) chat);
                frameLayout3.addView(backupImageView, LayoutHelper.createFrame(52, 52.0f, 51, 5.0f, 5.0f, 0.0f, 0.0f));
                this.markerImageView = frameLayout3;
                this.markerImageView.setTag(1);
            }
            if (this.markerImageView == null) {
                ImageView imageView = new ImageView(context2);
                imageView.setImageResource(NUM);
                this.mapViewClip.addView(imageView, LayoutHelper.createFrame(28, 48, 49));
                this.markerImageView = imageView;
            }
            this.searchListView = new RecyclerListView(context2);
            this.searchListView.setVisibility(8);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            this.searchAdapter = new LocationActivitySearchAdapter(context2) {
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
            this.searchAdapter.setDelegate(0, new BaseLocationAdapter.BaseLocationAdapterDelegate() {
                public final void didLoadSearchResult(ArrayList arrayList) {
                    LocationActivity.this.lambda$createView$13$LocationActivity(arrayList);
                }
            });
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    LocationActivity.this.lambda$createView$15$LocationActivity(view, i);
                }
            });
        } else {
            MessageObject messageObject3 = this.messageObject;
            if ((messageObject3 != null && !messageObject3.isLiveLocation()) || this.chatLocation != null) {
                TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
                if (tL_channelLocation != null) {
                    this.adapter.setChatLocation(tL_channelLocation);
                } else {
                    MessageObject messageObject4 = this.messageObject;
                    if (messageObject4 != null) {
                        this.adapter.setMessageObject(messageObject4);
                    }
                }
            }
        }
        this.shadow = new View(context2) {
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
        this.mapViewClip.addView(this.shadow, layoutParams2);
        if (this.messageObject == null && this.chatLocation == null && this.initialLocation != null) {
            this.userLocationMoved = true;
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag("location_actionIcon");
        }
        frameLayout.addView(this.actionBar);
        updateEmptyView();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$LocationActivity(View view) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery((String) null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    public /* synthetic */ void lambda$createView$1$LocationActivity(int i) {
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

    public /* synthetic */ void lambda$createView$2$LocationActivity(View view) {
        GoogleMap googleMap2;
        Activity parentActivity;
        if (Build.VERSION.SDK_INT < 23 || (parentActivity = getParentActivity()) == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            if (this.messageObject != null || this.chatLocation != null) {
                Location location = this.myLocation;
                if (!(location == null || (googleMap2 = this.googleMap) == null)) {
                    googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), this.myLocation.getLongitude()), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            } else if (!(this.myLocation == null || this.googleMap == null)) {
                this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
                this.locationButton.setTag("location_actionActiveIcon");
                this.adapter.setCustomLocation((Location) null);
                this.userLocationMoved = false;
                showSearchPlacesButton(false);
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude())));
                if (this.searchedForCustomLocations) {
                    Location location2 = this.myLocation;
                    if (location2 != null) {
                        this.adapter.searchPlacesWithQuery((String) null, location2, true, true);
                    }
                    this.searchedForCustomLocations = false;
                    showResults();
                }
            }
            removeInfoView();
            return;
        }
        showPermissionAlert(false);
    }

    public /* synthetic */ void lambda$createView$9$LocationActivity(View view, int i) {
        MessageObject messageObject2;
        TLRPC.TL_messageMediaVenue tL_messageMediaVenue;
        int i2 = this.locationType;
        if (i2 == 4) {
            if (i == 1 && (tL_messageMediaVenue = (TLRPC.TL_messageMediaVenue) this.adapter.getItem(i)) != null) {
                if (this.dialogId == 0) {
                    this.delegate.didSelectLocation(tL_messageMediaVenue, 4, true, 0);
                    finishFragment();
                    return;
                }
                AlertDialog[] alertDialogArr = {new AlertDialog(getParentActivity(), 3)};
                TLRPC.TL_channels_editLocation tL_channels_editLocation = new TLRPC.TL_channels_editLocation();
                tL_channels_editLocation.address = tL_messageMediaVenue.address;
                tL_channels_editLocation.channel = getMessagesController().getInputChannel(-((int) this.dialogId));
                tL_channels_editLocation.geo_point = new TLRPC.TL_inputGeoPoint();
                TLRPC.InputGeoPoint inputGeoPoint = tL_channels_editLocation.geo_point;
                TLRPC.GeoPoint geoPoint = tL_messageMediaVenue.geo;
                inputGeoPoint.lat = geoPoint.lat;
                inputGeoPoint._long = geoPoint._long;
                alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(tL_channels_editLocation, new RequestDelegate(alertDialogArr, tL_messageMediaVenue) {
                    private final /* synthetic */ AlertDialog[] f$1;
                    private final /* synthetic */ TLRPC.TL_messageMediaVenue f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LocationActivity.this.lambda$null$5$LocationActivity(this.f$1, this.f$2, tLObject, tL_error);
                    }
                })) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        LocationActivity.this.lambda$null$6$LocationActivity(this.f$1, dialogInterface);
                    }
                });
                showDialog(alertDialogArr[0]);
            }
        } else if (i2 == 5) {
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                TLRPC.GeoPoint geoPoint2 = this.chatLocation.geo_point;
                googleMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPoint2.lat, geoPoint2._long), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (i == 1 && (messageObject2 = this.messageObject) != null && !messageObject2.isLiveLocation()) {
            GoogleMap googleMap3 = this.googleMap;
            if (googleMap3 != null) {
                TLRPC.GeoPoint geoPoint3 = this.messageObject.messageOwner.media.geo;
                googleMap3.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPoint3.lat, geoPoint3._long), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (i != 1 || this.locationType == 2) {
            if ((i != 2 || this.locationType != 1) && ((i != 1 || this.locationType != 2) && (i != 3 || this.locationType != 3))) {
                Object item = this.adapter.getItem(i);
                if (item instanceof TLRPC.TL_messageMediaVenue) {
                    ChatActivity chatActivity = this.parentFragment;
                    if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                        this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) item, this.locationType, true, 0);
                        finishFragment();
                        return;
                    }
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(item) {
                        private final /* synthetic */ Object f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            LocationActivity.this.lambda$null$8$LocationActivity(this.f$1, z, i);
                        }
                    });
                } else if (item instanceof LiveLocation) {
                    this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation) item).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
                }
            } else if (getLocationController().isSharingLocation(this.dialogId)) {
                getLocationController().removeSharingLocation(this.dialogId);
                finishFragment();
            } else {
                openShareLiveLocation();
            }
        } else if (this.delegate != null && this.userLocation != null) {
            FrameLayout frameLayout = this.lastPressedMarkerView;
            if (frameLayout != null) {
                frameLayout.callOnClick();
                return;
            }
            TLRPC.TL_messageMediaGeo tL_messageMediaGeo = new TLRPC.TL_messageMediaGeo();
            tL_messageMediaGeo.geo = new TLRPC.TL_geoPoint();
            tL_messageMediaGeo.geo.lat = AndroidUtilities.fixLocationCoord(this.userLocation.getLatitude());
            tL_messageMediaGeo.geo._long = AndroidUtilities.fixLocationCoord(this.userLocation.getLongitude());
            ChatActivity chatActivity2 = this.parentFragment;
            if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
                this.delegate.didSelectLocation(tL_messageMediaGeo, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(tL_messageMediaGeo) {
                private final /* synthetic */ TLRPC.TL_messageMediaGeo f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectDate(boolean z, int i) {
                    LocationActivity.this.lambda$null$7$LocationActivity(this.f$1, z, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$5$LocationActivity(AlertDialog[] alertDialogArr, TLRPC.TL_messageMediaVenue tL_messageMediaVenue, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr, tL_messageMediaVenue) {
            private final /* synthetic */ AlertDialog[] f$1;
            private final /* synthetic */ TLRPC.TL_messageMediaVenue f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LocationActivity.this.lambda$null$4$LocationActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$LocationActivity(AlertDialog[] alertDialogArr, TLRPC.TL_messageMediaVenue tL_messageMediaVenue) {
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

    public /* synthetic */ void lambda$null$7$LocationActivity(TLRPC.TL_messageMediaGeo tL_messageMediaGeo, boolean z, int i) {
        this.delegate.didSelectLocation(tL_messageMediaGeo, this.locationType, z, i);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$8$LocationActivity(Object obj, boolean z, int i) {
        this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) obj, this.locationType, z, i);
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$12$LocationActivity(MapView mapView2) {
        try {
            mapView2.onCreate((Bundle) null);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new Runnable(mapView2) {
            private final /* synthetic */ MapView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationActivity.this.lambda$null$11$LocationActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$11$LocationActivity(MapView mapView2) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                mapView2.onCreate((Bundle) null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new OnMapReadyCallback() {
                    public final void onMapReady(GoogleMap googleMap) {
                        LocationActivity.this.lambda$null$10$LocationActivity(googleMap);
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

    public /* synthetic */ void lambda$null$10$LocationActivity(GoogleMap googleMap2) {
        this.googleMap = googleMap2;
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
        TLRPC.TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item != null && this.delegate != null) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                this.delegate.didSelectLocation(item, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(item) {
                private final /* synthetic */ TLRPC.TL_messageMediaVenue f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectDate(boolean z, int i) {
                    LocationActivity.this.lambda$null$14$LocationActivity(this.f$1, z, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$14$LocationActivity(TLRPC.TL_messageMediaVenue tL_messageMediaVenue, boolean z, int i) {
        this.delegate.didSelectLocation(tL_messageMediaVenue, this.locationType, z, i);
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

    private Bitmap createUserBitmap(LiveLocation liveLocation) {
        Bitmap bitmap;
        TLRPC.FileLocation fileLocation;
        try {
            if (liveLocation.user == null || liveLocation.user.photo == null) {
                fileLocation = (liveLocation.chat == null || liveLocation.chat.photo == null) ? null : liveLocation.chat.photo.photo_small;
            } else {
                fileLocation = liveLocation.user.photo.photo_small;
            }
            bitmap = Bitmap.createBitmap(AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f), Bitmap.Config.ARGB_8888);
            try {
                bitmap.eraseColor(0);
                Canvas canvas = new Canvas(bitmap);
                Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(NUM);
                drawable.setBounds(0, 0, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(76.0f));
                drawable.draw(canvas);
                Paint paint = new Paint(1);
                RectF rectF = new RectF();
                canvas.save();
                if (fileLocation != null) {
                    Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(fileLocation, true).toString());
                    if (decodeFile != null) {
                        BitmapShader bitmapShader = new BitmapShader(decodeFile, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
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
                    AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                    if (liveLocation.user != null) {
                        avatarDrawable2.setInfo(liveLocation.user);
                    } else if (liveLocation.chat != null) {
                        avatarDrawable2.setInfo(liveLocation.chat);
                    }
                    canvas.translate((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f));
                    avatarDrawable2.setBounds(0, 0, AndroidUtilities.dp(52.2f), AndroidUtilities.dp(52.2f));
                    avatarDrawable2.draw(canvas);
                }
                canvas.restore();
                try {
                    canvas.setBitmap((Bitmap) null);
                } catch (Exception unused) {
                }
            } catch (Throwable th) {
                th = th;
                FileLog.e(th);
                return bitmap;
            }
        } catch (Throwable th2) {
            th = th2;
            bitmap = null;
            FileLog.e(th);
            return bitmap;
        }
        return bitmap;
    }

    private int getMessageId(TLRPC.Message message) {
        int i = message.from_id;
        if (i != 0) {
            return i;
        }
        return (int) MessageObject.getDialogId(message);
    }

    /* access modifiers changed from: private */
    public void openShareLiveLocation() {
        if (this.delegate != null && getParentActivity() != null && this.myLocation != null) {
            TLRPC.User user = null;
            if (((int) this.dialogId) > 0) {
                user = getMessagesController().getUser(Integer.valueOf((int) this.dialogId));
            }
            showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new MessagesStorage.IntCallback() {
                public final void run(int i) {
                    LocationActivity.this.lambda$openShareLiveLocation$16$LocationActivity(i);
                }
            }));
        }
    }

    public /* synthetic */ void lambda$openShareLiveLocation$16$LocationActivity(int i) {
        TLRPC.TL_messageMediaGeoLive tL_messageMediaGeoLive = new TLRPC.TL_messageMediaGeoLive();
        tL_messageMediaGeoLive.geo = new TLRPC.TL_geoPoint();
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
    public void updatePlacesMarkers(ArrayList<TLRPC.TL_messageMediaVenue> arrayList) {
        if (arrayList != null) {
            int size = this.placeMarkers.size();
            for (int i = 0; i < size; i++) {
                this.placeMarkers.get(i).marker.remove();
            }
            this.placeMarkers.clear();
            int size2 = arrayList.size();
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC.TL_messageMediaVenue tL_messageMediaVenue = arrayList.get(i2);
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
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    private LiveLocation addUserMarker(TLRPC.Message message) {
        TLRPC.GeoPoint geoPoint = message.media.geo;
        LatLng latLng = new LatLng(geoPoint.lat, geoPoint._long);
        LiveLocation liveLocation = this.markersMap.get(message.from_id);
        if (liveLocation == null) {
            liveLocation = new LiveLocation();
            liveLocation.object = message;
            if (liveLocation.object.from_id != 0) {
                liveLocation.user = getMessagesController().getUser(Integer.valueOf(liveLocation.object.from_id));
                liveLocation.id = liveLocation.object.from_id;
            } else {
                int dialogId2 = (int) MessageObject.getDialogId(message);
                if (dialogId2 > 0) {
                    liveLocation.user = getMessagesController().getUser(Integer.valueOf(dialogId2));
                    liveLocation.id = dialogId2;
                } else {
                    liveLocation.chat = getMessagesController().getChat(Integer.valueOf(-dialogId2));
                    liveLocation.id = dialogId2;
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
                    LocationController.SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(this.dialogId);
                    if (liveLocation.id == getUserConfig().getClientUserId() && sharingLocationInfo != null && liveLocation.object.id == sharingLocationInfo.mid && this.myLocation != null) {
                        liveLocation.marker.setPosition(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            liveLocation.object = message;
            liveLocation.marker.setPosition(latLng);
        }
        return liveLocation;
    }

    private LiveLocation addUserMarker(TLRPC.TL_channelLocation tL_channelLocation) {
        TLRPC.GeoPoint geoPoint = tL_channelLocation.geo_point;
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
            FileLog.e((Throwable) e);
        }
        return liveLocation;
    }

    private void onMapInit() {
        if (this.googleMap != null) {
            TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
            if (tL_channelLocation != null) {
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addUserMarker(tL_channelLocation).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
            } else {
                MessageObject messageObject2 = this.messageObject;
                if (messageObject2 == null) {
                    this.userLocation = new Location("network");
                    TLRPC.TL_channelLocation tL_channelLocation2 = this.initialLocation;
                    if (tL_channelLocation2 != null) {
                        TLRPC.GeoPoint geoPoint = tL_channelLocation2.geo_point;
                        LatLng latLng = new LatLng(geoPoint.lat, geoPoint._long);
                        GoogleMap googleMap2 = this.googleMap;
                        googleMap2.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, googleMap2.getMaxZoomLevel() - 4.0f));
                        this.userLocation.setLatitude(this.initialLocation.geo_point.lat);
                        this.userLocation.setLongitude(this.initialLocation.geo_point._long);
                        this.adapter.setCustomLocation(this.userLocation);
                    } else {
                        this.userLocation.setLatitude(20.659322d);
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
                    LocationActivity.this.lambda$onMapInit$17$LocationActivity(i);
                }
            });
            this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                public final void onMyLocationChange(Location location) {
                    LocationActivity.this.lambda$onMapInit$18$LocationActivity(location);
                }
            });
            this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public final boolean onMarkerClick(Marker marker) {
                    return LocationActivity.this.lambda$onMapInit$19$LocationActivity(marker);
                }
            });
            this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                public final void onCameraMove() {
                    LocationActivity.this.lambda$onMapInit$20$LocationActivity();
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
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                            builder.setTitle(LocaleController.getString("GpsDisabledAlertTitle", NUM));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    LocationActivity.this.lambda$onMapInit$21$LocationActivity(dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            showDialog(builder.create());
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$onMapInit$17$LocationActivity(int i) {
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
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag("location_actionIcon");
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
                    LocationActivity.this.lambda$showPermissionAlert$22$LocationActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$22$LocationActivity(DialogInterface dialogInterface, int i) {
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

    /* access modifiers changed from: private */
    public void updateClipView() {
        int i;
        int i2;
        FrameLayout.LayoutParams layoutParams;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            i2 = (int) findViewHolderForAdapterPosition.itemView.getY();
            i = this.overScrollHeight + (i2 < 0 ? i2 : 0);
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

    /* access modifiers changed from: private */
    public void fixLayoutInternal(boolean z) {
        FrameLayout.LayoutParams layoutParams;
        if (this.listView != null) {
            int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int measuredHeight = this.fragmentView.getMeasuredHeight();
            if (measuredHeight != 0) {
                if (this.locationType == 2) {
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
                    int i = this.locationType;
                    int i2 = i == 3 ? 73 : (i == 1 || i == 2) ? 66 : 0;
                    this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) i2));
                    updateClipView();
                    this.listView.post(new Runnable(i2) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            LocationActivity.this.lambda$fixLayoutInternal$23$LocationActivity(this.f$1);
                        }
                    });
                    return;
                }
                updateClipView();
            }
        }
    }

    public /* synthetic */ void lambda$fixLayoutInternal$23$LocationActivity(int i) {
        this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) i));
        updateClipView();
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
                liveLocation.marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            if (this.messageObject == null && this.chatLocation == null && this.googleMap != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
                        this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        return;
                    }
                    this.firstWas = true;
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
                    return;
                }
                return;
            }
            this.adapter.setGpsLocation(this.myLocation);
        }
    }

    public void setMessageObject(MessageObject messageObject2) {
        this.messageObject = messageObject2;
        this.dialogId = this.messageObject.getDialogId();
    }

    public void setChatLocation(int i, TLRPC.TL_channelLocation tL_channelLocation) {
        this.dialogId = (long) (-i);
        this.chatLocation = tL_channelLocation;
    }

    public void setDialogId(long j) {
        this.dialogId = j;
    }

    public void setInitialLocation(TLRPC.TL_channelLocation tL_channelLocation) {
        this.initialLocation = tL_channelLocation;
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

    private void fetchRecentLocations(ArrayList<TLRPC.Message> arrayList) {
        LatLngBounds.Builder builder = this.firstFocus ? new LatLngBounds.Builder() : null;
        int currentTime = getConnectionsManager().getCurrentTime();
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.Message message = arrayList.get(i);
            int i2 = message.date;
            TLRPC.MessageMedia messageMedia = message.media;
            if (i2 + messageMedia.period > currentTime) {
                if (builder != null) {
                    TLRPC.GeoPoint geoPoint = messageMedia.geo;
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
                            FileLog.e((Throwable) e);
                        }
                    }
                } catch (Exception unused) {
                }
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
            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(-i));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                return false;
            }
        }
        TLRPC.TL_messages_getRecentLocations tL_messages_getRecentLocations = new TLRPC.TL_messages_getRecentLocations();
        long dialogId2 = this.messageObject.getDialogId();
        tL_messages_getRecentLocations.peer = getMessagesController().getInputPeer((int) dialogId2);
        tL_messages_getRecentLocations.limit = 100;
        getConnectionsManager().sendRequest(tL_messages_getRecentLocations, new RequestDelegate(dialogId2) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LocationActivity.this.lambda$getRecentLocations$26$LocationActivity(this.f$1, tLObject, tL_error);
            }
        });
        if (arrayList != null) {
            return true;
        }
        return false;
    }

    public /* synthetic */ void lambda$getRecentLocations$26$LocationActivity(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject, j) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    LocationActivity.this.lambda$null$25$LocationActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$25$LocationActivity(TLObject tLObject, long j) {
        if (this.googleMap != null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            int i = 0;
            while (i < messages_messages.messages.size()) {
                if (!(messages_messages.messages.get(i).media instanceof TLRPC.TL_messageMediaGeoLive)) {
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
                this.markAsReadRunnable = new Runnable() {
                    public final void run() {
                        LocationActivity.this.lambda$null$24$LocationActivity();
                    }
                };
                AndroidUtilities.runOnUIThread(this.markAsReadRunnable, 5000);
            }
        }
    }

    public /* synthetic */ void lambda$null$24$LocationActivity() {
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
        } else {
            int i3 = 0;
            if (i == NotificationCenter.didReceiveNewMessages) {
                if (!objArr[2].booleanValue() && objArr[0].longValue() == this.dialogId && this.messageObject != null) {
                    ArrayList arrayList = objArr[1];
                    boolean z = false;
                    while (i3 < arrayList.size()) {
                        MessageObject messageObject2 = (MessageObject) arrayList.get(i3);
                        if (messageObject2.isLiveLocation()) {
                            addUserMarker(messageObject2.messageOwner);
                            z = true;
                        }
                        i3++;
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
                    while (i3 < arrayList2.size()) {
                        MessageObject messageObject3 = (MessageObject) arrayList2.get(i3);
                        if (messageObject3.isLiveLocation() && (liveLocation = this.markersMap.get(getMessageId(messageObject3.messageOwner))) != null) {
                            LocationController.SharingLocationInfo sharingLocationInfo = getLocationController().getSharingLocationInfo(longValue);
                            if (sharingLocationInfo == null || sharingLocationInfo.mid != messageObject3.getId()) {
                                TLRPC.Message message = messageObject3.messageOwner;
                                liveLocation.object = message;
                                Marker marker = liveLocation.marker;
                                TLRPC.GeoPoint geoPoint = message.media.geo;
                                marker.setPosition(new LatLng(geoPoint.lat, geoPoint._long));
                            }
                            z2 = true;
                        }
                        i3++;
                    }
                    if (z2 && (locationActivityAdapter = this.adapter) != null) {
                        locationActivityAdapter.updateLiveLocations();
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
        this.onResumeCalled = false;
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
        $$Lambda$LocationActivity$IZD5Tgwx4r4Oclwb3tyv8HB6o r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                LocationActivity.this.lambda$getThemeDescriptions$27$LocationActivity();
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[66];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, r9, "dialogBackground");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint");
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptionArr[8] = new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        $$Lambda$LocationActivity$IZD5Tgwx4r4Oclwb3tyv8HB6o r7 = r9;
        themeDescriptionArr[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuBackground");
        themeDescriptionArr[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuItem");
        themeDescriptionArr[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuItemIcon");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider");
        themeDescriptionArr[14] = new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage");
        themeDescriptionArr[15] = new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText");
        themeDescriptionArr[16] = new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText");
        themeDescriptionArr[17] = new ThemeDescription(this.shadow, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp");
        themeDescriptionArr[18] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionIcon");
        themeDescriptionArr[19] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon");
        themeDescriptionArr[20] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground");
        themeDescriptionArr[21] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground");
        themeDescriptionArr[22] = new ThemeDescription(this.mapTypeButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "location_actionIcon");
        themeDescriptionArr[23] = new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground");
        themeDescriptionArr[24] = new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground");
        themeDescriptionArr[25] = new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon");
        themeDescriptionArr[26] = new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground");
        themeDescriptionArr[27] = new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground");
        themeDescriptionArr[28] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, r7, "avatar_text");
        themeDescriptionArr[29] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed");
        themeDescriptionArr[30] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange");
        themeDescriptionArr[31] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet");
        themeDescriptionArr[32] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen");
        themeDescriptionArr[33] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan");
        themeDescriptionArr[34] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue");
        themeDescriptionArr[35] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink");
        themeDescriptionArr[36] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_liveLocationProgress");
        themeDescriptionArr[37] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_placeLocationBackground");
        themeDescriptionArr[38] = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialog_liveLocationProgress");
        themeDescriptionArr[39] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationIcon");
        themeDescriptionArr[40] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationIcon");
        themeDescriptionArr[41] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationBackground");
        themeDescriptionArr[42] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationBackground");
        themeDescriptionArr[43] = new ThemeDescription((View) this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[44] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationText");
        themeDescriptionArr[45] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationText");
        themeDescriptionArr[46] = new ThemeDescription((View) this.listView, 0, new Class[]{LocationDirectionCell.class}, new String[]{"buttonTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText");
        themeDescriptionArr[47] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton");
        themeDescriptionArr[48] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed");
        themeDescriptionArr[49] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow");
        themeDescriptionArr[50] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray");
        themeDescriptionArr[51] = new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue2");
        themeDescriptionArr[52] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[53] = new ThemeDescription((View) this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[54] = new ThemeDescription((View) this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[55] = new ThemeDescription((View) this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[56] = new ThemeDescription((View) this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[57] = new ThemeDescription((View) this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[58] = new ThemeDescription((View) this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[59] = new ThemeDescription((View) this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"distanceTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[60] = new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle");
        themeDescriptionArr[61] = new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[62] = new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[63] = new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[64] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage");
        themeDescriptionArr[65] = new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$27$LocationActivity() {
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
            }
        } else if (this.currentMapStyleDark) {
            this.currentMapStyleDark = false;
            this.googleMap.setMapStyle((MapStyleOptions) null);
        }
    }
}
