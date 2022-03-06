package org.telegram.ui.Components;

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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$GeoPoint;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$TL_geoPoint;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC$User;
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
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.RecyclerListView;

public class ChatAttachAlertLocationLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public LocationActivityAdapter adapter;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    private Bitmap[] bitmapCache;
    private boolean checkBackgroundPermission = true;
    private boolean checkGpsEnabled = true;
    private boolean checkPermission = true;
    /* access modifiers changed from: private */
    public int clipSize;
    private boolean currentMapStyleDark;
    /* access modifiers changed from: private */
    public LocationActivityDelegate delegate;
    private long dialogId;
    private ImageView emptyImageView;
    /* access modifiers changed from: private */
    public TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    /* access modifiers changed from: private */
    public LinearLayout emptyView;
    private boolean first;
    private boolean firstWas;
    /* access modifiers changed from: private */
    public CameraUpdate forceUpdate;
    /* access modifiers changed from: private */
    public GoogleMap googleMap;
    private boolean ignoreLayout;
    private boolean isFirstLocation = true;
    /* access modifiers changed from: private */
    public Marker lastPressedMarker;
    /* access modifiers changed from: private */
    public FrameLayout lastPressedMarkerView;
    /* access modifiers changed from: private */
    public VenueLocation lastPressedVenue;
    private FillLastLinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private View loadingMapView;
    /* access modifiers changed from: private */
    public ImageView locationButton;
    private boolean locationDenied = false;
    /* access modifiers changed from: private */
    public int locationType;
    /* access modifiers changed from: private */
    public int mapHeight;
    private ActionBarMenuItem mapTypeButton;
    private MapView mapView;
    /* access modifiers changed from: private */
    public FrameLayout mapViewClip;
    private boolean mapsInitialized;
    /* access modifiers changed from: private */
    public ImageView markerImageView;
    /* access modifiers changed from: private */
    public int markerTop;
    private Location myLocation;
    private int nonClipSize;
    private boolean onResumeCalled;
    /* access modifiers changed from: private */
    public ActionBarMenuItem otherItem;
    /* access modifiers changed from: private */
    public int overScrollHeight;
    /* access modifiers changed from: private */
    public MapOverlayView overlayView;
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
    /* access modifiers changed from: private */
    public Location userLocation;
    /* access modifiers changed from: private */
    public boolean userLocationMoved;
    /* access modifiers changed from: private */
    public float yOffset;

    public static class LiveLocation {
        public Marker marker;
    }

    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2);
    }

    public static class VenueLocation {
        public Marker marker;
        public int num;
        public TLRPC$TL_messageMediaVenue venue;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$4(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    static /* synthetic */ float access$2816(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout, float f) {
        float f2 = chatAttachAlertLocationLayout.yOffset + f;
        chatAttachAlertLocationLayout.yOffset = f2;
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
        private HashMap<Marker, View> views = new HashMap<>();

        public MapOverlayView(Context context) {
            super(context);
        }

        public void addInfoView(Marker marker) {
            Marker marker2 = marker;
            VenueLocation venueLocation = (VenueLocation) marker.getTag();
            if (ChatAttachAlertLocationLayout.this.lastPressedVenue != venueLocation) {
                ChatAttachAlertLocationLayout.this.showSearchPlacesButton(false);
                if (ChatAttachAlertLocationLayout.this.lastPressedMarker != null) {
                    removeInfoView(ChatAttachAlertLocationLayout.this.lastPressedMarker);
                    Marker unused = ChatAttachAlertLocationLayout.this.lastPressedMarker = null;
                }
                VenueLocation unused2 = ChatAttachAlertLocationLayout.this.lastPressedVenue = venueLocation;
                Marker unused3 = ChatAttachAlertLocationLayout.this.lastPressedMarker = marker2;
                Context context = getContext();
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createFrame(-2, 114.0f));
                FrameLayout unused4 = ChatAttachAlertLocationLayout.this.lastPressedMarkerView = new FrameLayout(context);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setBackgroundResource(NUM);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(ChatAttachAlertLocationLayout.this.getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
                frameLayout.addView(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setAlpha(0.0f);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setOnClickListener(new ChatAttachAlertLocationLayout$MapOverlayView$$ExternalSyntheticLambda0(this, venueLocation));
                TextView textView = new TextView(context);
                textView.setTextSize(1, 16.0f);
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine(true);
                textView.setTextColor(ChatAttachAlertLocationLayout.this.getThemedColor("windowBackgroundWhiteBlackText"));
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                int i = 5;
                textView.setGravity(LocaleController.isRTL ? 5 : 3);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 10.0f, 18.0f, 0.0f));
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 14.0f);
                textView2.setMaxLines(1);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                textView2.setSingleLine(true);
                textView2.setTextColor(ChatAttachAlertLocationLayout.this.getThemedColor("windowBackgroundWhiteGrayText3"));
                textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                FrameLayout access$300 = ChatAttachAlertLocationLayout.this.lastPressedMarkerView;
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
                        if (lerp >= 0.7f && !this.startedInner && ChatAttachAlertLocationLayout.this.lastPressedMarkerView != null) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.SCALE_Y, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.ALPHA, new float[]{0.0f, 1.0f})});
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
                ChatAttachAlertLocationLayout.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, (GoogleMap.CancelableCallback) null);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$addInfoView$1(VenueLocation venueLocation, View view) {
            ChatActivity chatActivity = (ChatActivity) ChatAttachAlertLocationLayout.this.parentAlert.baseFragment;
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog((Context) ChatAttachAlertLocationLayout.this.getParentActivity(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertLocationLayout$MapOverlayView$$ExternalSyntheticLambda1(this, venueLocation), ChatAttachAlertLocationLayout.this.resourcesProvider);
                return;
            }
            ChatAttachAlertLocationLayout.this.delegate.didSelectLocation(venueLocation.venue, ChatAttachAlertLocationLayout.this.locationType, true, 0);
            ChatAttachAlertLocationLayout.this.parentAlert.dismiss();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$addInfoView$0(VenueLocation venueLocation, boolean z, int i) {
            ChatAttachAlertLocationLayout.this.delegate.didSelectLocation(venueLocation.venue, ChatAttachAlertLocationLayout.this.locationType, z, i);
            ChatAttachAlertLocationLayout.this.parentAlert.dismiss();
        }

        public void removeInfoView(Marker marker) {
            View view = this.views.get(marker);
            if (view != null) {
                removeView(view);
                this.views.remove(marker);
            }
        }

        public void updatePositions() {
            if (ChatAttachAlertLocationLayout.this.googleMap != null) {
                Projection projection = ChatAttachAlertLocationLayout.this.googleMap.getProjection();
                for (Map.Entry next : this.views.entrySet()) {
                    View view = (View) next.getValue();
                    Point screenLocation = projection.toScreenLocation(((Marker) next.getKey()).getPosition());
                    view.setTranslationX((float) (screenLocation.x - (view.getMeasuredWidth() / 2)));
                    view.setTranslationY((float) ((screenLocation.y - view.getMeasuredHeight()) + AndroidUtilities.dp(22.0f)));
                }
            }
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatAttachAlertLocationLayout(ChatAttachAlert chatAttachAlert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        ChatActivity chatActivity;
        Context context2 = context;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        int currentActionBarHeight = (AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f);
        this.overScrollHeight = currentActionBarHeight;
        this.mapHeight = currentActionBarHeight;
        this.first = true;
        this.bitmapCache = new Bitmap[7];
        AndroidUtilities.fixGoogleMapsBug();
        ChatActivity chatActivity2 = (ChatActivity) this.parentAlert.baseFragment;
        this.dialogId = chatActivity2.getDialogId();
        if (chatActivity2.getCurrentEncryptedChat() != null || chatActivity2.isInScheduleMode() || UserObject.isUserSelf(chatActivity2.getCurrentUser())) {
            this.locationType = 0;
        } else {
            this.locationType = 1;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionDenied);
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
        int i = Build.VERSION.SDK_INT;
        this.locationDenied = (i < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) ? false : true;
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        this.overlayView = new MapOverlayView(context2);
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = ChatAttachAlertLocationLayout.this.searching = true;
                ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = ChatAttachAlertLocationLayout.this;
                chatAttachAlertLocationLayout.parentAlert.makeFocusable(chatAttachAlertLocationLayout.searchItem.getSearchField(), true);
            }

            public void onSearchCollapse() {
                boolean unused = ChatAttachAlertLocationLayout.this.searching = false;
                boolean unused2 = ChatAttachAlertLocationLayout.this.searchWas = false;
                ChatAttachAlertLocationLayout.this.searchAdapter.searchDelayed((String) null, (Location) null);
                ChatAttachAlertLocationLayout.this.updateEmptyView();
                if (ChatAttachAlertLocationLayout.this.otherItem != null) {
                    ChatAttachAlertLocationLayout.this.otherItem.setVisibility(0);
                }
                ChatAttachAlertLocationLayout.this.listView.setVisibility(0);
                ChatAttachAlertLocationLayout.this.mapViewClip.setVisibility(0);
                ChatAttachAlertLocationLayout.this.searchListView.setVisibility(8);
                ChatAttachAlertLocationLayout.this.emptyView.setVisibility(8);
            }

            public void onTextChanged(EditText editText) {
                if (ChatAttachAlertLocationLayout.this.searchAdapter != null) {
                    String obj = editText.getText().toString();
                    if (obj.length() != 0) {
                        boolean unused = ChatAttachAlertLocationLayout.this.searchWas = true;
                        ChatAttachAlertLocationLayout.this.searchItem.setShowSearchProgress(true);
                        if (ChatAttachAlertLocationLayout.this.otherItem != null) {
                            ChatAttachAlertLocationLayout.this.otherItem.setVisibility(8);
                        }
                        ChatAttachAlertLocationLayout.this.listView.setVisibility(8);
                        ChatAttachAlertLocationLayout.this.mapViewClip.setVisibility(8);
                        if (ChatAttachAlertLocationLayout.this.searchListView.getAdapter() != ChatAttachAlertLocationLayout.this.searchAdapter) {
                            ChatAttachAlertLocationLayout.this.searchListView.setAdapter(ChatAttachAlertLocationLayout.this.searchAdapter);
                        }
                        ChatAttachAlertLocationLayout.this.searchListView.setVisibility(0);
                        ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = ChatAttachAlertLocationLayout.this;
                        boolean unused2 = chatAttachAlertLocationLayout.searchInProgress = chatAttachAlertLocationLayout.searchAdapter.isEmpty();
                        ChatAttachAlertLocationLayout.this.updateEmptyView();
                    } else {
                        if (ChatAttachAlertLocationLayout.this.otherItem != null) {
                            ChatAttachAlertLocationLayout.this.otherItem.setVisibility(0);
                        }
                        ChatAttachAlertLocationLayout.this.listView.setVisibility(0);
                        ChatAttachAlertLocationLayout.this.mapViewClip.setVisibility(0);
                        ChatAttachAlertLocationLayout.this.searchListView.setAdapter((RecyclerView.Adapter) null);
                        ChatAttachAlertLocationLayout.this.searchListView.setVisibility(8);
                        ChatAttachAlertLocationLayout.this.emptyView.setVisibility(8);
                    }
                    ChatAttachAlertLocationLayout.this.searchAdapter.searchDelayed(obj, ChatAttachAlertLocationLayout.this.userLocation);
                }
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setVisibility(this.locationDenied ? 8 : 0);
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(getThemedColor("dialogTextBlack"));
        searchField.setCursorColor(getThemedColor("dialogTextBlack"));
        searchField.setHintTextColor(getThemedColor("chat_messagePanelHint"));
        new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f)).gravity = 83;
        AnonymousClass2 r0 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (ChatAttachAlertLocationLayout.this.overlayView != null) {
                    ChatAttachAlertLocationLayout.this.overlayView.updatePositions();
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                ChatAttachAlertLocationLayout.this.backgroundPaint.setColor(ChatAttachAlertLocationLayout.this.getThemedColor("dialogBackground"));
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize), ChatAttachAlertLocationLayout.this.backgroundPaint);
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getY() > ((float) (getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize))) {
                    return false;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getY() > ((float) (getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize))) {
                    return false;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.mapViewClip = r0;
        r0.setWillNotDraw(false);
        View view = new View(context2);
        this.loadingMapView = view;
        view.setBackgroundDrawable(new MapPlaceholderDrawable());
        SearchButton searchButton = new SearchButton(context2);
        this.searchAreaButton = searchButton;
        searchButton.setTranslationX((float) (-AndroidUtilities.dp(80.0f)));
        this.searchAreaButton.setVisibility(4);
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), getThemedColor("location_actionBackground"), getThemedColor("location_actionPressedBackground"));
        if (i < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorRoundRectDrawable, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
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
        this.searchAreaButton.setTextColor(getThemedColor("location_actionActiveIcon"));
        this.searchAreaButton.setTextSize(1, 14.0f);
        this.searchAreaButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchAreaButton.setText(LocaleController.getString("PlacesInThisArea", NUM));
        this.searchAreaButton.setGravity(17);
        this.searchAreaButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.mapViewClip.addView(this.searchAreaButton, LayoutHelper.createFrame(-2, i >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
        this.searchAreaButton.setOnClickListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda1(this));
        ActionBarMenuItem actionBarMenuItem = r0;
        String str = "location_actionPressedBackground";
        String str2 = "location_actionBackground";
        ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, getThemedColor("location_actionIcon"), resourcesProvider);
        this.mapTypeButton = actionBarMenuItem;
        actionBarMenuItem.setClickable(true);
        this.mapTypeButton.setSubMenuOpenSide(2);
        this.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        this.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        this.mapTypeButton.addSubItem(2, NUM, (CharSequence) LocaleController.getString("Map", NUM), resourcesProvider2);
        this.mapTypeButton.addSubItem(3, NUM, (CharSequence) LocaleController.getString("Satellite", NUM), resourcesProvider2);
        this.mapTypeButton.addSubItem(4, NUM, (CharSequence) LocaleController.getString("Hybrid", NUM), resourcesProvider2);
        this.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), getThemedColor(str2), getThemedColor(str));
        if (i < 21) {
            Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
            mutate2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(mutate2, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable2.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable2;
            chatActivity = chatActivity2;
        } else {
            StateListAnimator stateListAnimator2 = new StateListAnimator();
            ActionBarMenuItem actionBarMenuItem3 = this.mapTypeButton;
            Property property2 = View.TRANSLATION_Z;
            chatActivity = chatActivity2;
            stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(actionBarMenuItem3, property2, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
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
        this.mapViewClip.addView(this.mapTypeButton, LayoutHelper.createFrame(i >= 21 ? 40 : 44, i >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        this.mapTypeButton.setOnClickListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda2(this));
        this.mapTypeButton.setDelegate(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda19(this));
        this.locationButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), getThemedColor(str2), getThemedColor(str));
        if (i < 21) {
            Drawable mutate3 = context.getResources().getDrawable(NUM).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(mutate3, createSimpleSelectorCircleDrawable2, 0, 0);
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
        this.locationButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
        this.locationButton.setTag("location_actionActiveIcon");
        this.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", NUM));
        this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(i >= 21 ? 40 : 44, i >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f));
        this.locationButton.setOnClickListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda3(this));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(1);
        this.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        this.emptyView.setVisibility(8);
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(ChatAttachAlertLocationLayout$$ExternalSyntheticLambda4.INSTANCE);
        ImageView imageView2 = new ImageView(context2);
        this.emptyImageView = imageView2;
        imageView2.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context2);
        this.emptyTitleTextView = textView;
        textView.setTextColor(getThemedColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setText(LocaleController.getString("NoPlacesFound", NUM));
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context2);
        this.emptySubtitleTextView = textView2;
        textView2.setTextColor(getThemedColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        AnonymousClass6 r02 = new RecyclerListView(context2, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                ChatAttachAlertLocationLayout.this.updateClipView();
            }
        };
        this.listView = r02;
        r02.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        LocationActivityAdapter locationActivityAdapter2 = new LocationActivityAdapter(context, this.locationType, this.dialogId, true, resourcesProvider);
        this.adapter = locationActivityAdapter2;
        recyclerListView.setAdapter(locationActivityAdapter2);
        this.adapter.setUpdateRunnable(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda15(this));
        this.adapter.setMyLocationDenied(this.locationDenied);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass7 r03 = new FillLastLinearLayoutManager(context, 1, false, 0, recyclerListView2) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int i) {
                        return super.calculateDyToMakeVisible(view, i) - (ChatAttachAlertLocationLayout.this.listView.getPaddingTop() - (ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int i) {
                        return super.calculateTimeForDeceleration(i) * 4;
                    }
                };
                r2.setTargetPosition(i);
                startSmoothScroll(r2);
            }
        };
        this.layoutManager = r03;
        recyclerListView2.setLayoutManager(r03);
        addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                RecyclerListView.Holder holder;
                boolean unused = ChatAttachAlertLocationLayout.this.scrolling = i != 0;
                if (!ChatAttachAlertLocationLayout.this.scrolling && ChatAttachAlertLocationLayout.this.forceUpdate != null) {
                    CameraUpdate unused2 = ChatAttachAlertLocationLayout.this.forceUpdate = null;
                }
                if (i == 0) {
                    int dp = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertLocationLayout.this.parentAlert.getBackgroundPaddingTop();
                    if (((ChatAttachAlertLocationLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - dp) + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertLocationLayout.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight) {
                        ChatAttachAlertLocationLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - (ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight));
                    }
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatAttachAlertLocationLayout.this.updateClipView();
                if (ChatAttachAlertLocationLayout.this.forceUpdate != null) {
                    ChatAttachAlertLocationLayout.access$2816(ChatAttachAlertLocationLayout.this, (float) i2);
                }
                ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = ChatAttachAlertLocationLayout.this;
                chatAttachAlertLocationLayout.parentAlert.updateLayout(chatAttachAlertLocationLayout, true, i2);
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda26(this, chatActivity, resourcesProvider2));
        this.adapter.setDelegate(this.dialogId, new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda22(this));
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        AnonymousClass9 r04 = new MapView(context2) {
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                MotionEvent motionEvent2;
                if (ChatAttachAlertLocationLayout.this.yOffset != 0.0f) {
                    motionEvent = MotionEvent.obtain(motionEvent);
                    motionEvent.offsetLocation(0.0f, (-ChatAttachAlertLocationLayout.this.yOffset) / 2.0f);
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
                if (motionEvent.getAction() == 0) {
                    if (ChatAttachAlertLocationLayout.this.animatorSet != null) {
                        ChatAttachAlertLocationLayout.this.animatorSet.cancel();
                    }
                    AnimatorSet unused = ChatAttachAlertLocationLayout.this.animatorSet = new AnimatorSet();
                    ChatAttachAlertLocationLayout.this.animatorSet.setDuration(200);
                    ChatAttachAlertLocationLayout.this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) (ChatAttachAlertLocationLayout.this.markerTop - AndroidUtilities.dp(10.0f))})});
                    ChatAttachAlertLocationLayout.this.animatorSet.start();
                } else if (motionEvent.getAction() == 1) {
                    if (ChatAttachAlertLocationLayout.this.animatorSet != null) {
                        ChatAttachAlertLocationLayout.this.animatorSet.cancel();
                    }
                    float unused2 = ChatAttachAlertLocationLayout.this.yOffset = 0.0f;
                    AnimatorSet unused3 = ChatAttachAlertLocationLayout.this.animatorSet = new AnimatorSet();
                    ChatAttachAlertLocationLayout.this.animatorSet.setDuration(200);
                    ChatAttachAlertLocationLayout.this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) ChatAttachAlertLocationLayout.this.markerTop})});
                    ChatAttachAlertLocationLayout.this.animatorSet.start();
                    ChatAttachAlertLocationLayout.this.adapter.fetchLocationAddress();
                }
                if (motionEvent.getAction() == 2) {
                    if (!ChatAttachAlertLocationLayout.this.userLocationMoved) {
                        ChatAttachAlertLocationLayout.this.locationButton.setColorFilter(new PorterDuffColorFilter(ChatAttachAlertLocationLayout.this.getThemedColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
                        ChatAttachAlertLocationLayout.this.locationButton.setTag("location_actionIcon");
                        boolean unused4 = ChatAttachAlertLocationLayout.this.userLocationMoved = true;
                    }
                    if (!(ChatAttachAlertLocationLayout.this.googleMap == null || ChatAttachAlertLocationLayout.this.userLocation == null)) {
                        ChatAttachAlertLocationLayout.this.userLocation.setLatitude(ChatAttachAlertLocationLayout.this.googleMap.getCameraPosition().target.latitude);
                        ChatAttachAlertLocationLayout.this.userLocation.setLongitude(ChatAttachAlertLocationLayout.this.googleMap.getCameraPosition().target.longitude);
                    }
                    ChatAttachAlertLocationLayout.this.adapter.setCustomLocation(ChatAttachAlertLocationLayout.this.userLocation);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        this.mapView = r04;
        new Thread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda17(this, r04)).start();
        ImageView imageView3 = new ImageView(context2);
        this.markerImageView = imageView3;
        imageView3.setImageResource(NUM);
        this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(28, 48, 49));
        RecyclerListView recyclerListView3 = new RecyclerListView(context2, resourcesProvider2);
        this.searchListView = recyclerListView3;
        recyclerListView3.setVisibility(8);
        this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        AnonymousClass10 r05 = new LocationActivitySearchAdapter(context2) {
            public void notifyDataSetChanged() {
                if (ChatAttachAlertLocationLayout.this.searchItem != null) {
                    ChatAttachAlertLocationLayout.this.searchItem.setShowSearchProgress(ChatAttachAlertLocationLayout.this.searchAdapter.isSearching());
                }
                if (ChatAttachAlertLocationLayout.this.emptySubtitleTextView != null) {
                    ChatAttachAlertLocationLayout.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoPlacesFoundInfo", NUM, ChatAttachAlertLocationLayout.this.searchAdapter.getLastSearchString())));
                }
                super.notifyDataSetChanged();
            }
        };
        this.searchAdapter = r05;
        r05.setDelegate(0, new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda21(this));
        this.searchListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && ChatAttachAlertLocationLayout.this.searching && ChatAttachAlertLocationLayout.this.searchWas) {
                    AndroidUtilities.hideKeyboard(ChatAttachAlertLocationLayout.this.parentAlert.getCurrentFocus());
                }
            }
        });
        this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda27(this, chatActivity, resourcesProvider2));
        updateEmptyView();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery((String) null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        this.mapTypeButton.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(int i) {
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
    public /* synthetic */ void lambda$new$3(View view) {
        Activity parentActivity;
        if (Build.VERSION.SDK_INT < 23 || (parentActivity = getParentActivity()) == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            if (!(this.myLocation == null || this.googleMap == null)) {
                this.locationButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
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
            removeInfoView();
            return;
        }
        AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view, int i) {
        if (i == 1) {
            if (this.delegate != null && this.userLocation != null) {
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
                if (chatActivity.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog((Context) getParentActivity(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda24(this, tLRPC$TL_messageMediaGeo), resourcesProvider);
                    return;
                }
                this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeo, this.locationType, true, 0);
                this.parentAlert.dismiss();
            } else if (this.locationDenied) {
                AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
            }
        } else if (i != 2 || this.locationType != 1) {
            Object item = this.adapter.getItem(i);
            if (item instanceof TLRPC$TL_messageMediaVenue) {
                if (chatActivity.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog((Context) getParentActivity(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda23(this, item), resourcesProvider);
                    return;
                }
                this.delegate.didSelectLocation((TLRPC$TL_messageMediaVenue) item, this.locationType, true, 0);
                this.parentAlert.dismiss();
            } else if (item instanceof LiveLocation) {
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation) item).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (getLocationController().isSharingLocation(this.dialogId)) {
            getLocationController().removeSharingLocation(this.dialogId);
            this.parentAlert.dismiss();
        } else if (this.myLocation != null || !this.locationDenied) {
            openShareLiveLocation();
        } else {
            AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo, boolean z, int i) {
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeo, this.locationType, z, i);
        this.parentAlert.dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(Object obj, boolean z, int i) {
        this.delegate.didSelectLocation((TLRPC$TL_messageMediaVenue) obj, this.locationType, z, i);
        this.parentAlert.dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$12(MapView mapView2) {
        try {
            mapView2.onCreate((Bundle) null);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda16(this, mapView2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$11(MapView mapView2) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                mapView2.onCreate((Bundle) null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda10(this));
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
    public /* synthetic */ void lambda$new$10(GoogleMap googleMap2) {
        this.googleMap = googleMap2;
        googleMap2.setOnMapLoadedCallback(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda7(this));
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, NUM));
        }
        onMapInit();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9() {
        AndroidUtilities.runOnUIThread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda14(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8() {
        this.loadingMapView.setTag(1);
        this.loadingMapView.animate().alpha(0.0f).setDuration(180).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$13(ArrayList arrayList) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$15(ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view, int i) {
        TLRPC$TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item != null && this.delegate != null) {
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog((Context) getParentActivity(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda25(this, item), resourcesProvider);
                return;
            }
            this.delegate.didSelectLocation(item, this.locationType, true, 0);
            this.parentAlert.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$14(TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, boolean z, int i) {
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaVenue, this.locationType, z, i);
        this.parentAlert.dismiss();
    }

    /* access modifiers changed from: package-private */
    public boolean shouldHideBottomButtons() {
        return !this.locationDenied;
    }

    /* access modifiers changed from: package-private */
    public void onPause() {
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

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Can't wrap try/catch for region: R(18:0|1|2|(1:4)|7|(1:9)|10|11|(1:13)|14|15|(1:17)|19|(1:21)|22|(1:24)|25|26) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x0035 */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0039 A[Catch:{ Exception -> 0x0040 }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x004c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDestroy() {
        /*
            r2 = this;
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.locationPermissionGranted
            r0.removeObserver(r2, r1)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.locationPermissionDenied
            r0.removeObserver(r2, r1)
            com.google.android.gms.maps.GoogleMap r0 = r2.googleMap     // Catch:{ Exception -> 0x001b }
            if (r0 == 0) goto L_0x001f
            r1 = 0
            r0.setMyLocationEnabled(r1)     // Catch:{ Exception -> 0x001b }
            goto L_0x001f
        L_0x001b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x001f:
            com.google.android.gms.maps.MapView r0 = r2.mapView
            if (r0 == 0) goto L_0x002e
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.y
            int r1 = -r1
            int r1 = r1 * 3
            float r1 = (float) r1
            r0.setTranslationY(r1)
        L_0x002e:
            com.google.android.gms.maps.MapView r0 = r2.mapView     // Catch:{ Exception -> 0x0035 }
            if (r0 == 0) goto L_0x0035
            r0.onPause()     // Catch:{ Exception -> 0x0035 }
        L_0x0035:
            com.google.android.gms.maps.MapView r0 = r2.mapView     // Catch:{ Exception -> 0x0040 }
            if (r0 == 0) goto L_0x0041
            r0.onDestroy()     // Catch:{ Exception -> 0x0040 }
            r0 = 0
            r2.mapView = r0     // Catch:{ Exception -> 0x0040 }
            goto L_0x0041
        L_0x0040:
        L_0x0041:
            org.telegram.ui.Adapters.LocationActivityAdapter r0 = r2.adapter
            if (r0 == 0) goto L_0x0048
            r0.destroy()
        L_0x0048:
            org.telegram.ui.Adapters.LocationActivitySearchAdapter r0 = r2.searchAdapter
            if (r0 == 0) goto L_0x004f
            r0.destroy()
        L_0x004f:
            org.telegram.ui.Components.ChatAttachAlert r0 = r2.parentAlert
            org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
            r0.closeSearchField()
            org.telegram.ui.Components.ChatAttachAlert r0 = r2.parentAlert
            org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r2.searchItem
            r0.removeView(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertLocationLayout.onDestroy():void");
    }

    /* access modifiers changed from: package-private */
    public void onHide() {
        this.searchItem.setVisibility(8);
    }

    /* access modifiers changed from: package-private */
    public boolean onDismiss() {
        onDestroy();
        return false;
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        int i = 0;
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            i = Math.max(((int) holder.itemView.getY()) - this.nonClipSize, 0);
        }
        return i + AndroidUtilities.dp(56.0f);
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
        updateClipView();
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x003e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPreMeasure(int r4, int r5) {
        /*
            r3 = this;
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            org.telegram.ui.ActionBar.ActionBar r4 = r4.actionBar
            boolean r4 = r4.isSearchFieldVisible()
            r0 = 1
            r1 = 0
            if (r4 != 0) goto L_0x0045
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r4.sizeNotifierFrameLayout
            int r4 = r4.measureKeyboardHeight()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            if (r4 <= r2) goto L_0x001d
            goto L_0x0045
        L_0x001d:
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 != 0) goto L_0x0031
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r4.x
            int r4 = r4.y
            if (r2 <= r4) goto L_0x0031
            float r4 = (float) r5
            r5 = 1080033280(0x40600000, float:3.5)
            float r4 = r4 / r5
            int r4 = (int) r4
            goto L_0x0035
        L_0x0031:
            int r5 = r5 / 5
            int r4 = r5 * 2
        L_0x0035:
            r5 = 1112539136(0x42500000, float:52.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            if (r4 >= 0) goto L_0x003f
            r4 = 0
        L_0x003f:
            org.telegram.ui.Components.ChatAttachAlert r5 = r3.parentAlert
            r5.setAllowNestedScroll(r0)
            goto L_0x004f
        L_0x0045:
            int r4 = r3.mapHeight
            int r5 = r3.overScrollHeight
            int r4 = r4 - r5
            org.telegram.ui.Components.ChatAttachAlert r5 = r3.parentAlert
            r5.setAllowNestedScroll(r1)
        L_0x004f:
            org.telegram.ui.Components.RecyclerListView r5 = r3.listView
            int r5 = r5.getPaddingTop()
            if (r5 == r4) goto L_0x0060
            r3.ignoreLayout = r0
            org.telegram.ui.Components.RecyclerListView r5 = r3.listView
            r5.setPadding(r1, r4, r1, r1)
            r3.ignoreLayout = r1
        L_0x0060:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertLocationLayout.onPreMeasure(int, int):void");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            fixLayoutInternal(this.first);
            this.first = false;
        }
    }

    /* access modifiers changed from: package-private */
    public int getButtonsHideOffset() {
        return AndroidUtilities.dp(56.0f);
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    private boolean isActiveThemeDark() {
        if (!Theme.getActiveTheme().isDark() && AndroidUtilities.computePerceivedBrightness(getThemedColor("windowBackgroundWhite")) >= 0.721f) {
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
            this.searchAreaButton.setVisibility(z ? 0 : 4);
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

    public void openShareLiveLocation() {
        Activity parentActivity;
        if (this.delegate != null && getParentActivity() != null && this.myLocation != null) {
            if (this.checkBackgroundPermission && Build.VERSION.SDK_INT >= 29 && (parentActivity = getParentActivity()) != null) {
                this.checkBackgroundPermission = false;
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (Math.abs((System.currentTimeMillis() / 1000) - ((long) globalMainSettings.getInt("backgroundloc", 0))) > 86400 && parentActivity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
                    globalMainSettings.edit().putInt("backgroundloc", (int) (System.currentTimeMillis() / 1000)).commit();
                    AlertsCreator.createBackgroundLocationPermissionDialog(parentActivity, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda11(this), this.resourcesProvider).show();
                    return;
                }
            }
            TLRPC$User tLRPC$User = null;
            if (DialogObject.isUserDialog(this.dialogId)) {
                tLRPC$User = this.parentAlert.baseFragment.getMessagesController().getUser(Long.valueOf(this.dialogId));
            }
            AlertsCreator.createLocationUpdateDialog(getParentActivity(), tLRPC$User, new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda18(this), this.resourcesProvider).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openShareLiveLocation$16(int i) {
        TLRPC$TL_messageMediaGeoLive tLRPC$TL_messageMediaGeoLive = new TLRPC$TL_messageMediaGeoLive();
        TLRPC$TL_geoPoint tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint();
        tLRPC$TL_messageMediaGeoLive.geo = tLRPC$TL_geoPoint;
        tLRPC$TL_geoPoint.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        tLRPC$TL_messageMediaGeoLive.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        tLRPC$TL_messageMediaGeoLive.period = i;
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeoLive, this.locationType, true, 0);
        this.parentAlert.dismiss();
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
                    MarkerOptions position = markerOptions.position(new LatLng(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long));
                    position.icon(BitmapDescriptorFactory.fromBitmap(createPlaceBitmap(i2)));
                    position.anchor(0.5f, 0.5f);
                    position.title(tLRPC$TL_messageMediaVenue.title);
                    position.snippet(tLRPC$TL_messageMediaVenue.address);
                    VenueLocation venueLocation = new VenueLocation();
                    venueLocation.num = i2;
                    Marker addMarker = this.googleMap.addMarker(position);
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

    private MessagesController getMessagesController() {
        return this.parentAlert.baseFragment.getMessagesController();
    }

    private LocationController getLocationController() {
        return this.parentAlert.baseFragment.getLocationController();
    }

    private UserConfig getUserConfig() {
        return this.parentAlert.baseFragment.getUserConfig();
    }

    /* access modifiers changed from: private */
    public Activity getParentActivity() {
        BaseFragment baseFragment;
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (chatAttachAlert == null || (baseFragment = chatAttachAlert.baseFragment) == null) {
            return null;
        }
        return baseFragment.getParentActivity();
    }

    private void onMapInit() {
        if (this.googleMap != null) {
            Location location = new Location("network");
            this.userLocation = location;
            location.setLatitude(20.659322d);
            this.userLocation.setLongitude(-11.40625d);
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnCameraMoveStartedListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda6(this));
            this.googleMap.setOnMyLocationChangeListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda9(this));
            this.googleMap.setOnMarkerClickListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda8(this));
            this.googleMap.setOnCameraMoveListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda5(this));
            AndroidUtilities.runOnUIThread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda13(this), 200);
            Location lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
            if (this.checkGpsEnabled && getParentActivity() != null) {
                this.checkGpsEnabled = false;
                if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                    try {
                        if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
                            builder.setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground"));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda0(this));
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            builder.show();
                        }
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                } else {
                    return;
                }
            }
            updateClipView();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$17(int i) {
        View childAt;
        RecyclerView.ViewHolder findContainingViewHolder;
        if (i == 1) {
            showSearchPlacesButton(true);
            removeInfoView();
            if (!this.scrolling && this.listView.getChildCount() > 0 && (childAt = this.listView.getChildAt(0)) != null && (findContainingViewHolder = this.listView.findContainingViewHolder(childAt)) != null && findContainingViewHolder.getAdapterPosition() == 0) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$18(Location location) {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (chatAttachAlert != null && chatAttachAlert.baseFragment != null) {
            positionMarker(location);
            getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
            this.isFirstLocation = false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onMapInit$19(Marker marker) {
        if (!(marker.getTag() instanceof VenueLocation)) {
            return true;
        }
        this.markerImageView.setVisibility(4);
        if (!this.userLocationMoved) {
            this.locationButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag("location_actionIcon");
            this.userLocationMoved = true;
        }
        this.overlayView.addInfoView(marker);
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$20() {
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null) {
            mapOverlayView.updatePositions();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$21() {
        if (this.loadingMapView.getTag() == null) {
            this.loadingMapView.animate().alpha(0.0f).setDuration(180).start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$22(DialogInterface dialogInterface, int i) {
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
        LatLng latLng;
        if (this.mapView != null && this.mapViewClip != null) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition != null) {
                i2 = (int) findViewHolderForAdapterPosition.itemView.getY();
                i = this.overScrollHeight + Math.min(i2, 0);
            } else {
                i2 = -this.mapViewClip.getMeasuredHeight();
                i = 0;
            }
            if (((FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams()) == null) {
                return;
            }
            if (i <= 0) {
                if (this.mapView.getVisibility() == 0) {
                    this.mapView.setVisibility(4);
                    this.mapViewClip.setVisibility(4);
                    MapOverlayView mapOverlayView = this.overlayView;
                    if (mapOverlayView != null) {
                        mapOverlayView.setVisibility(4);
                    }
                }
                this.mapView.setTranslationY((float) i2);
                return;
            }
            if (this.mapView.getVisibility() == 4) {
                this.mapView.setVisibility(0);
                this.mapViewClip.setVisibility(0);
                MapOverlayView mapOverlayView2 = this.overlayView;
                if (mapOverlayView2 != null) {
                    mapOverlayView2.setVisibility(0);
                }
            }
            int max = Math.max(0, (-((i2 - this.mapHeight) + this.overScrollHeight)) / 2);
            int i3 = this.mapHeight - this.overScrollHeight;
            float max2 = 1.0f - Math.max(0.0f, Math.min(1.0f, ((float) (this.listView.getPaddingTop() - i2)) / ((float) (this.listView.getPaddingTop() - i3))));
            int i4 = this.clipSize;
            if (this.locationDenied && isTypeSend()) {
                i3 += Math.min(i2, this.listView.getPaddingTop());
            }
            this.clipSize = (int) (((float) i3) * max2);
            float f = (float) max;
            this.mapView.setTranslationY(f);
            this.nonClipSize = i3 - this.clipSize;
            this.mapViewClip.invalidate();
            this.mapViewClip.setTranslationY((float) (i2 - this.nonClipSize));
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                googleMap2.setPadding(0, AndroidUtilities.dp(6.0f), 0, this.clipSize + AndroidUtilities.dp(6.0f));
            }
            MapOverlayView mapOverlayView3 = this.overlayView;
            if (mapOverlayView3 != null) {
                mapOverlayView3.setTranslationY(f);
            }
            float min = (float) Math.min(Math.max(this.nonClipSize - i2, 0), (this.mapHeight - this.mapTypeButton.getMeasuredHeight()) - AndroidUtilities.dp(80.0f));
            this.mapTypeButton.setTranslationY(min);
            this.searchAreaButton.setTranslation(min);
            this.locationButton.setTranslationY((float) (-this.clipSize));
            ImageView imageView = this.markerImageView;
            int dp = (((this.mapHeight - this.clipSize) / 2) - AndroidUtilities.dp(48.0f)) + max;
            this.markerTop = dp;
            imageView.setTranslationY((float) dp);
            if (i4 != this.clipSize) {
                Marker marker = this.lastPressedMarker;
                if (marker != null) {
                    latLng = marker.getPosition();
                } else if (this.userLocationMoved) {
                    latLng = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                } else {
                    latLng = this.myLocation != null ? new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude()) : null;
                }
                if (latLng != null) {
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
            if (this.locationDenied && isTypeSend()) {
                int itemCount = this.adapter.getItemCount();
                for (int i5 = 1; i5 < itemCount; i5++) {
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(i5);
                    if (findViewHolderForAdapterPosition2 != null) {
                        findViewHolderForAdapterPosition2.itemView.setTranslationY((float) (this.listView.getPaddingTop() - i2));
                    }
                }
            }
        }
    }

    private boolean isTypeSend() {
        int i = this.locationType;
        return i == 0 || i == 1;
    }

    private int buttonsHeight() {
        int dp = AndroidUtilities.dp(66.0f);
        return this.locationType == 1 ? dp + AndroidUtilities.dp(66.0f) : dp;
    }

    private void fixLayoutInternal(boolean z) {
        FrameLayout.LayoutParams layoutParams;
        if (getMeasuredHeight() != 0 && this.mapView != null) {
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
            int buttonsHeight = ((AndroidUtilities.displaySize.y - currentActionBarHeight) - buttonsHeight()) - AndroidUtilities.dp(90.0f);
            int dp = AndroidUtilities.dp(189.0f);
            this.overScrollHeight = dp;
            if (!this.locationDenied || !isTypeSend()) {
                buttonsHeight = Math.min(AndroidUtilities.dp(310.0f), buttonsHeight);
            }
            this.mapHeight = Math.max(dp, buttonsHeight);
            if (this.locationDenied && isTypeSend()) {
                this.overScrollHeight = this.mapHeight;
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            layoutParams2.topMargin = currentActionBarHeight;
            this.listView.setLayoutParams(layoutParams2);
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams();
            layoutParams3.topMargin = currentActionBarHeight;
            layoutParams3.height = this.mapHeight;
            this.mapViewClip.setLayoutParams(layoutParams3);
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.searchListView.getLayoutParams();
            layoutParams4.topMargin = currentActionBarHeight;
            this.searchListView.setLayoutParams(layoutParams4);
            this.adapter.setOverScrollHeight((!this.locationDenied || !isTypeSend()) ? this.overScrollHeight : this.overScrollHeight - this.listView.getPaddingTop());
            FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.mapView.getLayoutParams();
            if (layoutParams5 != null) {
                layoutParams5.height = this.mapHeight + AndroidUtilities.dp(10.0f);
                this.mapView.setLayoutParams(layoutParams5);
            }
            MapOverlayView mapOverlayView = this.overlayView;
            if (!(mapOverlayView == null || (layoutParams = (FrameLayout.LayoutParams) mapOverlayView.getLayoutParams()) == null)) {
                layoutParams.height = this.mapHeight + AndroidUtilities.dp(10.0f);
                this.overlayView.setLayoutParams(layoutParams);
            }
            this.adapter.notifyDataSetChanged();
            updateClipView();
        }
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
            Location location2 = new Location(location);
            this.myLocation = location2;
            if (this.googleMap != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                LocationActivityAdapter locationActivityAdapter = this.adapter;
                if (locationActivityAdapter != null) {
                    if (!this.searchedForCustomLocations) {
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
            this.adapter.setGpsLocation(location2);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.locationPermissionGranted) {
            this.locationDenied = false;
            LocationActivityAdapter locationActivityAdapter = this.adapter;
            if (locationActivityAdapter != null) {
                locationActivityAdapter.setMyLocationDenied(false);
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
            LocationActivityAdapter locationActivityAdapter2 = this.adapter;
            if (locationActivityAdapter2 != null) {
                locationActivityAdapter2.setMyLocationDenied(true);
            }
        }
        fixLayoutInternal(true);
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (this.locationDenied) {
            i3 = 8;
        }
        actionBarMenuItem.setVisibility(i3);
    }

    public void onResume() {
        MapView mapView2 = this.mapView;
        if (mapView2 != null && this.mapsInitialized) {
            try {
                mapView2.onResume();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        this.onResumeCalled = true;
    }

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        this.parentAlert.actionBar.setTitle(LocaleController.getString("ShareLocation", NUM));
        if (this.mapView.getParent() == null) {
            this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
            this.mapViewClip.addView(this.overlayView, 1, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
            this.mapViewClip.addView(this.loadingMapView, 2, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.searchItem.setVisibility(0);
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
        AndroidUtilities.runOnUIThread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda12(this), this.parentAlert.delegate.needEnterComment() ? 200 : 0);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        updateClipView();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShow$23() {
        Activity parentActivity;
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (parentActivity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            }
        }
    }

    public void setDelegate(LocationActivityDelegate locationActivityDelegate) {
        this.delegate = locationActivityDelegate;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ChatAttachAlertLocationLayout$$ExternalSyntheticLambda20 chatAttachAlertLocationLayout$$ExternalSyntheticLambda20 = new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda20(this);
        arrayList.add(new ThemeDescription(this.mapViewClip, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        arrayList.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        arrayList.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        ChatAttachAlertLocationLayout$$ExternalSyntheticLambda20 chatAttachAlertLocationLayout$$ExternalSyntheticLambda202 = chatAttachAlertLocationLayout$$ExternalSyntheticLambda20;
        arrayList.add(new ThemeDescription(this.mapTypeButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertLocationLayout$$ExternalSyntheticLambda202, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, chatAttachAlertLocationLayout$$ExternalSyntheticLambda202, "avatar_text"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertLocationLayout$$ExternalSyntheticLambda202, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertLocationLayout$$ExternalSyntheticLambda202, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertLocationLayout$$ExternalSyntheticLambda202, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertLocationLayout$$ExternalSyntheticLambda202, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertLocationLayout$$ExternalSyntheticLambda202, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertLocationLayout$$ExternalSyntheticLambda202, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, chatAttachAlertLocationLayout$$ExternalSyntheticLambda202, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_liveLocationProgress"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_placeLocationBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialog_liveLocationProgress"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationIcon"));
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
    public /* synthetic */ void lambda$getThemeDescriptions$24() {
        this.mapTypeButton.setIconColor(getThemedColor("location_actionIcon"));
        this.mapTypeButton.redrawPopup(getThemedColor("actionBarDefaultSubmenuBackground"));
        this.mapTypeButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItemIcon"), true);
        this.mapTypeButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItem"), false);
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
