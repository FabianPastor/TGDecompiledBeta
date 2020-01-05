package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
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
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
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
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;
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
    private LocationActivitySearchAdapter searchAdapter;
    private boolean searchInProgress;
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchWas;
    private boolean searching;
    private View shadow;
    private Drawable shadowDrawable;
    private Runnable updateRunnable;
    private Location userLocation;
    private boolean userLocationMoved;
    private boolean wasResults;

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
                LocationActivity.this.lastPressedMarkerView.setOnClickListener(new -$$Lambda$LocationActivity$MapOverlayView$DFjop8_CAzMgsRKRBvTPY2g4lpk(this, marker2));
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
                FrameLayout access$200 = LocationActivity.this.lastPressedMarkerView;
                if (!LocaleController.isRTL) {
                    i = 3;
                }
                access$200.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, i | 48, 18.0f, 32.0f, 18.0f, 0.0f));
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

        public /* synthetic */ void lambda$addInfoView$1$LocationActivity$MapOverlayView(Marker marker, View view) {
            VenueLocation venueLocation = (VenueLocation) marker.getTag();
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

    public View createView(Context context) {
        LayoutParams layoutParams;
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
        String str = "dialogBackground";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        String str2 = "dialogTextBlack";
        this.actionBar.setTitleColor(Theme.getColor(str2));
        this.actionBar.setItemsColor(Theme.getColor(str2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAddToContainer(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                String str = ",";
                if (i == -1) {
                    LocationActivity.this.finishFragment();
                } else if (i == 1) {
                    try {
                        double d = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                        double d2 = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                        Activity parentActivity = LocationActivity.this.getParentActivity();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("geo:");
                        stringBuilder.append(d);
                        stringBuilder.append(str);
                        stringBuilder.append(d2);
                        stringBuilder.append("?q=");
                        stringBuilder.append(d);
                        stringBuilder.append(str);
                        stringBuilder.append(d2);
                        parentActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
                    } catch (Exception e) {
                        FileLog.e(e);
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
            MessageObject messageObject = this.messageObject;
            if (messageObject == null) {
                this.actionBar.setTitle(LocaleController.getString("ShareLocation", NUM));
                if (this.locationType != 4) {
                    this.overlayView = new MapOverlayView(context2);
                    this.searchItem = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                        public void onSearchExpand() {
                            LocationActivity.this.searching = true;
                        }

                        public void onSearchCollapse() {
                            LocationActivity.this.searching = false;
                            LocationActivity.this.searchWas = false;
                            LocationActivity.this.searchAdapter.searchDelayed(null, null);
                            LocationActivity.this.updateEmptyView();
                        }

                        public void onTextChanged(EditText editText) {
                            if (LocationActivity.this.searchAdapter != null) {
                                String obj = editText.getText().toString();
                                boolean z = false;
                                if (obj.length() != 0) {
                                    LocationActivity.this.searchWas = true;
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
                                    locationActivity.searchInProgress = z;
                                    LocationActivity.this.updateEmptyView();
                                } else {
                                    if (LocationActivity.this.otherItem != null) {
                                        LocationActivity.this.otherItem.setVisibility(0);
                                    }
                                    LocationActivity.this.listView.setVisibility(0);
                                    LocationActivity.this.mapViewClip.setVisibility(0);
                                    LocationActivity.this.searchListView.setAdapter(null);
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
                    searchField.setTextColor(Theme.getColor(str2));
                    searchField.setCursorColor(Theme.getColor(str2));
                    searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
                }
            } else if (messageObject.isLiveLocation()) {
                this.actionBar.setTitle(LocaleController.getString("AttachLiveLocation", NUM));
            } else {
                str2 = this.messageObject.messageOwner.media.title;
                if (str2 == null || str2.length() <= 0) {
                    this.actionBar.setTitle(LocaleController.getString("ChatLocation", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SharedPlace", NUM));
                }
                this.otherItem = createMenu.addItem(0, NUM);
                this.otherItem.addSubItem(1, NUM, LocaleController.getString("OpenInExternalApp", NUM));
                if (!getLocationController().isSharingLocation(this.dialogId)) {
                    this.otherItem.addSubItem(5, NUM, LocaleController.getString("SendLiveLocationMenu", NUM));
                }
                this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            }
        }
        this.fragmentView = new FrameLayout(context2) {
            private boolean first = true;

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (z) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                }
            }

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == LocationActivity.this.actionBar && LocationActivity.this.parentLayout != null) {
                    LocationActivity.this.parentLayout.drawHeaderShadow(canvas, LocationActivity.this.actionBar.getMeasuredHeight());
                }
                return drawChild;
            }
        };
        View view = this.fragmentView;
        FrameLayout frameLayout = (FrameLayout) view;
        view.setBackgroundColor(Theme.getColor(str));
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        final Rect rect = new Rect();
        this.shadowDrawable.getPadding(rect);
        int i = this.locationType;
        if (i == 0 || i == 1) {
            layoutParams = new LayoutParams(-1, AndroidUtilities.dp(21.0f) + rect.top);
        } else {
            layoutParams = new LayoutParams(-1, AndroidUtilities.dp(6.0f) + rect.top);
        }
        LayoutParams layoutParams2 = layoutParams;
        layoutParams2.gravity = 83;
        this.mapViewClip = new FrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (LocationActivity.this.overlayView != null) {
                    LocationActivity.this.overlayView.updatePositions();
                }
            }
        };
        this.mapViewClip.setBackgroundDrawable(new MapPlaceholderDrawable());
        String str3 = "location_actionIcon";
        this.mapTypeButton = new ActionBarMenuItem(context2, null, 0, Theme.getColor(str3));
        this.mapTypeButton.setSubMenuOpenSide(2);
        this.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        this.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        this.mapTypeButton.addSubItem(2, NUM, LocaleController.getString("Map", NUM));
        this.mapTypeButton.addSubItem(3, NUM, LocaleController.getString("Satellite", NUM));
        this.mapTypeButton.addSubItem(4, NUM, LocaleController.getString("Hybrid", NUM));
        this.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        } else {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.mapTypeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.mapTypeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.mapTypeButton.setStateListAnimator(stateListAnimator);
            this.mapTypeButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.mapTypeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.mapTypeButton.setIcon(NUM);
        this.mapViewClip.addView(this.mapTypeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 40 : 44, VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        this.mapTypeButton.setDelegate(new -$$Lambda$LocationActivity$lsbOYzPtHjRXF9H-Kq3dcakYtX8(this));
        this.locationButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
            mutate2.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            createSimpleSelectorCircleDrawable = new CombinedDrawable(mutate2, createSimpleSelectorCircleDrawable2, 0, 0);
            createSimpleSelectorCircleDrawable.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable2 = createSimpleSelectorCircleDrawable;
        } else {
            StateListAnimator stateListAnimator2 = new StateListAnimator();
            stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator2.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.locationButton.setStateListAnimator(stateListAnimator2);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.locationButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable2);
        this.locationButton.setImageResource(NUM);
        this.locationButton.setScaleType(ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionActiveIcon"), Mode.MULTIPLY));
        this.locationButton.setTag("location_actionActiveIcon");
        this.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", NUM));
        layoutParams = LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 40 : 44, VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f);
        layoutParams.bottomMargin += layoutParams2.height - rect.top;
        this.mapViewClip.addView(this.locationButton, layoutParams);
        this.locationButton.setOnClickListener(new -$$Lambda$LocationActivity$iSP4KzPqZWtLKfNQxqDBN2NA67w(this));
        this.emptyView = new LinearLayout(context2);
        this.emptyView.setOrientation(1);
        this.emptyView.setGravity(1);
        this.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        this.emptyView.setVisibility(8);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(-$$Lambda$LocationActivity$7JosHnhNDq6OqDta5axVkW1FxBE.INSTANCE);
        this.emptyImageView = new ImageView(context2);
        this.emptyImageView.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), Mode.MULTIPLY));
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
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass7 anonymousClass7 = new LocationActivityAdapter(context, this.locationType, this.dialogId) {
            /* Access modifiers changed, original: protected */
            public void onDirectionClick() {
                if (VERSION.SDK_INT >= 23) {
                    Activity parentActivity = LocationActivity.this.getParentActivity();
                    if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                        LocationActivity.this.showPermissionAlert(true);
                        return;
                    }
                }
                if (LocationActivity.this.myLocation != null) {
                    try {
                        Intent intent;
                        String str = "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f";
                        String str2 = "android.intent.action.VIEW";
                        if (LocationActivity.this.messageObject != null) {
                            intent = new Intent(str2, Uri.parse(String.format(Locale.US, str, new Object[]{Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo.lat), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo._long)})));
                        } else {
                            intent = new Intent(str2, Uri.parse(String.format(Locale.US, str, new Object[]{Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.chatLocation.geo_point.lat), Double.valueOf(LocationActivity.this.chatLocation.geo_point._long)})));
                        }
                        LocationActivity.this.getParentActivity().startActivity(intent);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        };
        this.adapter = anonymousClass7;
        recyclerListView.setAdapter(anonymousClass7);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass8 anonymousClass8 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass8;
        recyclerListView2.setLayoutManager(anonymousClass8);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (LocationActivity.this.adapter.getItemCount() != 0) {
                    int findFirstVisibleItemPosition = LocationActivity.this.layoutManager.findFirstVisibleItemPosition();
                    if (findFirstVisibleItemPosition != -1) {
                        LocationActivity.this.updateClipView(findFirstVisibleItemPosition);
                    }
                }
            }
        });
        this.listView.setOnItemClickListener(new -$$Lambda$LocationActivity$HVAELQZCkOqxphNTTfgbVTBbk4o(this));
        this.adapter.setDelegate(this.dialogId, new -$$Lambda$LocationActivity$IPQvEgGc0E_OyZ6XMjHW9Pf7Ywc(this));
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        frameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        this.mapView = new MapView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (LocationActivity.this.messageObject == null && LocationActivity.this.chatLocation == null) {
                    AnimatorSet access$3200;
                    Animator[] animatorArr;
                    if (motionEvent.getAction() == 0) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$3200 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) (LocationActivity.this.markerTop - AndroidUtilities.dp(10.0f))});
                        access$3200.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    } else if (motionEvent.getAction() == 1) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$3200 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) LocationActivity.this.markerTop});
                        access$3200.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                        LocationActivity.this.adapter.fetchLocationAddress();
                    }
                    if (motionEvent.getAction() == 2) {
                        if (!LocationActivity.this.userLocationMoved) {
                            String str = "location_actionIcon";
                            LocationActivity.this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
                            LocationActivity.this.locationButton.setTag(str);
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
        new Thread(new -$$Lambda$LocationActivity$AdCK4zpyE84fcTNsEXiiZGEiP6k(this, this.mapView)).start();
        this.shadow = new View(context2) {
            private RectF rect = new RectF();

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                LocationActivity.this.shadowDrawable.setBounds(-rect.left, 0, getMeasuredWidth() + rect.right, getMeasuredHeight());
                LocationActivity.this.shadowDrawable.draw(canvas);
                if (LocationActivity.this.locationType == 0 || LocationActivity.this.locationType == 1) {
                    int dp = AndroidUtilities.dp(36.0f);
                    int dp2 = rect.top + AndroidUtilities.dp(10.0f);
                    this.rect.set((float) ((getMeasuredWidth() - dp) / 2), (float) dp2, (float) ((getMeasuredWidth() + dp) / 2), (float) (dp2 + AndroidUtilities.dp(4.0f)));
                    dp = Theme.getColor("key_sheet_scrollUp");
                    Color.alpha(dp);
                    Theme.dialogs_onlineCirclePaint.setColor(dp);
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
            }
        };
        this.mapViewClip.addView(this.shadow, layoutParams2);
        if (this.messageObject == null && this.chatLocation == null) {
            if (this.locationType == 4 && this.dialogId != 0) {
                Object chat = getMessagesController().getChat(Integer.valueOf(-((int) this.dialogId)));
                if (chat != null) {
                    FrameLayout frameLayout2 = new FrameLayout(context2);
                    frameLayout2.setBackgroundResource(NUM);
                    this.mapViewClip.addView(frameLayout2, LayoutHelper.createFrame(62, 76, 49));
                    BackupImageView backupImageView = new BackupImageView(context2);
                    backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
                    backupImageView.setImage(ImageLocation.getForChat(chat, false), "50_50", new AvatarDrawable((Chat) chat), chat);
                    frameLayout2.addView(backupImageView, LayoutHelper.createFrame(52, 52.0f, 51, 5.0f, 5.0f, 0.0f, 0.0f));
                    this.markerImageView = frameLayout2;
                    this.markerImageView.setTag(Integer.valueOf(1));
                }
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
            this.searchAdapter.setDelegate(0, new -$$Lambda$LocationActivity$RDE_AyqFfWLCB57jJ4-Ietll0Gg(this));
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener(new -$$Lambda$LocationActivity$2QBhZwF7ERCLASSNAMEZPEjaj7nZMqlo(this));
        } else {
            MessageObject messageObject2 = this.messageObject;
            if (!((messageObject2 == null || messageObject2.isLiveLocation()) && this.chatLocation == null)) {
                TL_channelLocation tL_channelLocation = this.chatLocation;
                if (tL_channelLocation != null) {
                    this.adapter.setChatLocation(tL_channelLocation);
                } else {
                    messageObject2 = this.messageObject;
                    if (messageObject2 != null) {
                        this.adapter.setMessageObject(messageObject2);
                    }
                }
            }
        }
        if (this.messageObject == null && this.chatLocation == null && this.initialLocation != null) {
            this.userLocationMoved = true;
            this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
            this.locationButton.setTag(str3);
        }
        frameLayout.addView(this.actionBar);
        updateEmptyView();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$LocationActivity(int i) {
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

    public /* synthetic */ void lambda$createView$1$LocationActivity(View view) {
        if (VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                showPermissionAlert(false);
                return;
            }
        }
        if (this.messageObject != null || this.chatLocation != null) {
            Location location = this.myLocation;
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
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude())));
        }
        Marker marker = this.lastPressedMarker;
        if (marker != null) {
            marker.hideInfoWindow();
            this.lastPressedMarker = null;
            this.lastPressedVenue = null;
            this.lastPressedMarkerView = null;
            this.markerImageView.setVisibility(0);
        }
    }

    public /* synthetic */ void lambda$createView$8$LocationActivity(View view, int i) {
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
                        alertDialogArr[0].setOnCancelListener(new -$$Lambda$LocationActivity$vzIiRQlVTgkENDCSn6OTJyzil_U(this, getConnectionsManager().sendRequest(tL_channels_editLocation, new -$$Lambda$LocationActivity$JVIyk85f2x-4hTwIuL_DgIdN8K4(this, alertDialogArr, tL_messageMediaVenue))));
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
                            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new -$$Lambda$LocationActivity$EJDktTJg40rY8vKIBJu6CDfRzRU(this, item));
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
                        AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new -$$Lambda$LocationActivity$ZzHw8ZvhrTH6kqirw6236b0c3Ig(this, tL_messageMediaGeo));
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$4$LocationActivity(AlertDialog[] alertDialogArr, TL_messageMediaVenue tL_messageMediaVenue, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$f3wHditFD1WYSdA2ujOtjIj5sPw(this, alertDialogArr, tL_messageMediaVenue));
    }

    public /* synthetic */ void lambda$null$3$LocationActivity(AlertDialog[] alertDialogArr, TL_messageMediaVenue tL_messageMediaVenue) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        this.delegate.didSelectLocation(tL_messageMediaVenue, 4, true, 0);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$5$LocationActivity(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }

    public /* synthetic */ void lambda$null$6$LocationActivity(TL_messageMediaGeo tL_messageMediaGeo, boolean z, int i) {
        this.delegate.didSelectLocation(tL_messageMediaGeo, this.locationType, z, i);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$7$LocationActivity(Object obj, boolean z, int i) {
        this.delegate.didSelectLocation((TL_messageMediaVenue) obj, this.locationType, z, i);
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$11$LocationActivity(MapView mapView) {
        try {
            mapView.onCreate(null);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$o5Y_j8wmGYATxVbbYz9DzT-Mhac(this, mapView));
    }

    public /* synthetic */ void lambda$null$10$LocationActivity(MapView mapView) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                mapView.onCreate(null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new -$$Lambda$LocationActivity$1KWS2HAI2Bi3vUmcllfKEx-BbgE(this));
                this.mapsInitialized = true;
                if (this.onResumeCalled) {
                    this.mapView.onResume();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$null$9$LocationActivity(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (Theme.getCurrentTheme().isDark() || Theme.isCurrentThemeNight()) {
            this.currentMapStyleDark = true;
            this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, NUM));
        }
        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        onMapInit();
    }

    public /* synthetic */ void lambda$createView$12$LocationActivity(ArrayList arrayList) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    public /* synthetic */ void lambda$createView$14$LocationActivity(View view, int i) {
        TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item != null && this.delegate != null) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                this.delegate.didSelectLocation(item, this.locationType, true, 0);
                finishFragment();
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.parentFragment.getDialogId(), new -$$Lambda$LocationActivity$JtoaOKJltSIQVMr8dVAJ4UTCyI0(this, item));
        }
    }

    public /* synthetic */ void lambda$null$13$LocationActivity(TL_messageMediaVenue tL_messageMediaVenue, boolean z, int i) {
        this.delegate.didSelectLocation(tL_messageMediaVenue, this.locationType, z, i);
        finishFragment();
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
            showDialog(AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new -$$Lambda$LocationActivity$TNNyUU8iPw-rt-gfxNj5y_L2w7s(this)));
        }
    }

    public /* synthetic */ void lambda$openShareLiveLocation$15$LocationActivity(int i) {
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
            this.googleMap.setOnCameraMoveStartedListener(new -$$Lambda$LocationActivity$s7Uu0vU9oDVu_Xmi4WxH781E5tM(this));
            this.googleMap.setOnMyLocationChangeListener(new -$$Lambda$LocationActivity$mxPTh8cTielbUWro4l5IfzxMVZU(this));
            this.googleMap.setOnMarkerClickListener(new -$$Lambda$LocationActivity$xaLC8G3FvrqkrgVLJnWcDUqWa-4(this));
            this.googleMap.setOnCameraMoveListener(new -$$Lambda$LocationActivity$aImz2LI7a9bCwgsAYWBVYz5F5uE(this));
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
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new -$$Lambda$LocationActivity$RRcZ98Qrys-ynKzTnwwJcZfzDHU(this));
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

    public /* synthetic */ void lambda$onMapInit$16$LocationActivity(int i) {
        if (i == 1 && this.lastPressedMarker != null) {
            this.markerImageView.setVisibility(0);
            this.overlayView.removeInfoView(this.lastPressedMarker);
            this.lastPressedMarker = null;
            this.lastPressedVenue = null;
            this.lastPressedMarkerView = null;
        }
    }

    public /* synthetic */ void lambda$onMapInit$17$LocationActivity(Location location) {
        positionMarker(location);
        getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    public /* synthetic */ boolean lambda$onMapInit$18$LocationActivity(Marker marker) {
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

    public /* synthetic */ void lambda$onMapInit$19$LocationActivity() {
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null) {
            mapOverlayView.updatePositions();
        }
    }

    public /* synthetic */ void lambda$onMapInit$20$LocationActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception unused) {
            }
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
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$LocationActivity$LuZvcZvYiCt7959147H12nnMxHU(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$21$LocationActivity(DialogInterface dialogInterface, int i) {
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
                updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                return;
            }
            View view = this.fragmentView;
            if (view != null) {
                ((FrameLayout) view).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
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
                    i = -this.mapViewClip.getMeasuredHeight();
                    i2 = 0;
                }
                if (((LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
                    MapOverlayView mapOverlayView;
                    if (i2 <= 0) {
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
                    this.mapViewClip.setTranslationY((float) Math.min(0, i));
                    i = -i;
                    int i3 = i / 2;
                    this.mapView.setTranslationY((float) Math.max(0, i3));
                    mapOverlayView = this.overlayView;
                    if (mapOverlayView != null) {
                        mapOverlayView.setTranslationY((float) Math.max(0, i3));
                    }
                    ActionBarMenuItem actionBarMenuItem = this.mapTypeButton;
                    i3 = this.overScrollHeight - actionBarMenuItem.getMeasuredHeight();
                    int i4 = this.locationType;
                    i4 = (i4 == 0 || i4 == 1) ? 30 : 10;
                    actionBarMenuItem.setTranslationY((float) Math.min(i3 - AndroidUtilities.dp((float) (64 + i4)), i));
                    View view = this.markerImageView;
                    if (view != null) {
                        i = (i - AndroidUtilities.dp(view.getTag() == null ? 48.0f : 69.0f)) + (i2 / 2);
                        this.markerTop = i;
                        view.setTranslationY((float) i);
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
                        if (!(layoutParams == null || layoutParams.height == this.overScrollHeight + AndroidUtilities.dp(10.0f))) {
                            layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                            this.overlayView.setLayoutParams(layoutParams);
                        }
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
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                    this.listView.post(new -$$Lambda$LocationActivity$MlUKH69SpKpH8iog-VunLymYMDo(this, i));
                } else {
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                }
            }
        }
    }

    public /* synthetic */ void lambda$fixLayoutInternal$22$LocationActivity(int i) {
        this.layoutManager.scrollToPositionWithOffset(0, -AndroidUtilities.dp((float) i));
        updateClipView(this.layoutManager.findFirstVisibleItemPosition());
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
                    if (this.locationType != 4) {
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
        getConnectionsManager().sendRequest(tL_messages_getRecentLocations, new -$$Lambda$LocationActivity$072wp83o1Q2wzTgrS3jXV5Uf7Ik(this, dialogId));
        if (arrayList != null) {
            z = true;
        }
        return z;
    }

    public /* synthetic */ void lambda$getRecentLocations$25$LocationActivity(long j, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivity$LmNOLr2AJYmYL_43NjisXUW4LEE(this, tLObject, j));
        }
    }

    public /* synthetic */ void lambda$null$24$LocationActivity(TLObject tLObject, long j) {
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
                this.markAsReadRunnable = new -$$Lambda$LocationActivity$ejTRfsEmwQYDFNAW9cj-8-p0t5w(this);
                AndroidUtilities.runOnUIThread(this.markAsReadRunnable, 5000);
            }
        }
    }

    public /* synthetic */ void lambda$null$23$LocationActivity() {
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
        -$$Lambda$LocationActivity$CZ3Pu0rPK5kurcqC8czSxwJYGPo -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo = new -$$Lambda$LocationActivity$CZ3Pu0rPK5kurcqC8czSxwJYGPo(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[62];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo, "dialogBackground");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "dialogBackground");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "dialogButtonSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "dialogTextBlack");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "chat_messagePanelHint");
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptionArr[8] = new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "dialogTextBlack");
        -$$Lambda$LocationActivity$CZ3Pu0rPK5kurcqC8czSxwJYGPo -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2 = -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo;
        themeDescriptionArr[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "actionBarDefaultSubmenuBackground");
        themeDescriptionArr[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "actionBarDefaultSubmenuItem");
        themeDescriptionArr[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "actionBarDefaultSubmenuItemIcon");
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
        themeDescriptionArr[22] = new ThemeDescription(this.mapTypeButton, 0, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "location_actionIcon");
        themeDescriptionArr[23] = new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "location_actionBackground");
        themeDescriptionArr[24] = new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "location_actionPressedBackground");
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_savedDrawable}, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "avatar_text");
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "avatar_backgroundRed");
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "avatar_backgroundOrange");
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "avatar_backgroundViolet");
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "avatar_backgroundGreen");
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "avatar_backgroundCyan");
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "avatar_backgroundBlue");
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, -__lambda_locationactivity_cz3pu0rpk5kurcqc8czsxwjygpo2, "avatar_backgroundPink");
        themeDescriptionArr[33] = new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress");
        themeDescriptionArr[34] = new ThemeDescription(null, 0, null, null, null, null, "location_placeLocationBackground");
        themeDescriptionArr[35] = new ThemeDescription(null, 0, null, null, null, null, "dialog_liveLocationProgress");
        View view = this.listView;
        int i = ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG;
        Class[] clsArr = new Class[]{SendLocationCell.class};
        String[] strArr = new String[1];
        strArr[0] = "imageView";
        themeDescriptionArr[36] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "location_sendLocationIcon");
        themeDescriptionArr[37] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLiveLocationIcon");
        themeDescriptionArr[38] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLocationBackground");
        themeDescriptionArr[39] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE) | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, "location_sendLiveLocationBackground");
        themeDescriptionArr[40] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        view = this.listView;
        i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{SendLocationCell.class};
        strArr = new String[1];
        strArr[0] = "titleTextView";
        themeDescriptionArr[41] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "location_sendLiveLocationText");
        themeDescriptionArr[42] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, null, null, null, "location_sendLocationText");
        themeDescriptionArr[43] = new ThemeDescription(this.listView, 0, new Class[]{LocationDirectionCell.class}, new String[]{"buttonTextView"}, null, null, null, "featuredStickers_buttonText");
        view = this.listView;
        i = ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE;
        clsArr = new Class[]{LocationDirectionCell.class};
        strArr = new String[1];
        strArr[0] = "frameLayout";
        themeDescriptionArr[44] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "featuredStickers_addButton");
        themeDescriptionArr[45] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, null, null, null, "featuredStickers_addButtonPressed");
        themeDescriptionArr[46] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[47] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        view = this.listView;
        clsArr = new Class[]{HeaderCell.class};
        strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[48] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "dialogTextBlue2");
        themeDescriptionArr[49] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        view = this.listView;
        clsArr = new Class[]{LocationCell.class};
        strArr = new String[1];
        strArr[0] = "nameTextView";
        themeDescriptionArr[50] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{LocationCell.class};
        strArr = new String[1];
        strArr[0] = "addressTextView";
        themeDescriptionArr[51] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[52] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[53] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[54] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[55] = new ThemeDescription(this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[56] = new ThemeDescription(this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"distanceTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[57] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle");
        themeDescriptionArr[58] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[59] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[60] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[61] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, null, null, null, "windowBackgroundWhiteGrayText3");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$26$LocationActivity() {
        this.mapTypeButton.setIconColor(Theme.getColor("location_actionIcon"));
        this.mapTypeButton.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), Mode.MULTIPLY));
        this.shadow.invalidate();
        if (Theme.getCurrentTheme().isDark() || Theme.isCurrentThemeNight()) {
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
