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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.IMapsProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
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
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertLocationLayout;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class ChatAttachAlertLocationLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
    private Paint backgroundPaint;
    private Bitmap[] bitmapCache;
    private boolean checkBackgroundPermission;
    private boolean checkGpsEnabled;
    private boolean checkPermission;
    private int clipSize;
    private boolean currentMapStyleDark;
    private LocationActivityDelegate delegate;
    private long dialogId;
    private ImageView emptyImageView;
    private TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    private boolean first;
    private boolean firstWas;
    private IMapsProvider.ICameraUpdate forceUpdate;
    private boolean ignoreLayout;
    private boolean isFirstLocation;
    private IMapsProvider.IMarker lastPressedMarker;
    private FrameLayout lastPressedMarkerView;
    private VenueLocation lastPressedVenue;
    private FillLastLinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private View loadingMapView;
    private ImageView locationButton;
    private boolean locationDenied;
    private int locationType;
    private IMapsProvider.IMap map;
    private int mapHeight;
    private ActionBarMenuItem mapTypeButton;
    private IMapsProvider.IMapView mapView;
    private FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private ImageView markerImageView;
    private int markerTop;
    private Location myLocation;
    private int nonClipSize;
    private boolean onResumeCalled;
    private ActionBarMenuItem otherItem;
    private int overScrollHeight;
    private MapOverlayView overlayView;
    private ArrayList<VenueLocation> placeMarkers;
    private boolean scrolling;
    private LocationActivitySearchAdapter searchAdapter;
    private SearchButton searchAreaButton;
    private boolean searchInProgress;
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchWas;
    private boolean searchedForCustomLocations;
    private boolean searching;
    private Location userLocation;
    private boolean userLocationMoved;
    private float yOffset;

    /* loaded from: classes3.dex */
    public static class LiveLocation {
        public IMapsProvider.IMarker marker;
    }

    /* loaded from: classes3.dex */
    public interface LocationActivityDelegate {
        void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2);
    }

    /* loaded from: classes3.dex */
    public static class VenueLocation {
        public IMapsProvider.IMarker marker;
        public int num;
        public TLRPC$TL_messageMediaVenue venue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$4(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int needsActionBar() {
        return 1;
    }

    static /* synthetic */ float access$2816(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout, float f) {
        float f2 = chatAttachAlertLocationLayout.yOffset + f;
        chatAttachAlertLocationLayout.yOffset = f2;
        return f2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class SearchButton extends TextView {
        private float additionanTranslationY;
        private float currentTranslationY;

        public SearchButton(Context context) {
            super(context);
        }

        @Override // android.view.View
        public float getTranslationX() {
            return this.additionanTranslationY;
        }

        @Override // android.view.View
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

    /* loaded from: classes3.dex */
    public class MapOverlayView extends FrameLayout {
        private HashMap<IMapsProvider.IMarker, View> views;

        public MapOverlayView(Context context) {
            super(context);
            this.views = new HashMap<>();
        }

        public void addInfoView(IMapsProvider.IMarker iMarker) {
            final VenueLocation venueLocation = (VenueLocation) iMarker.getTag();
            if (ChatAttachAlertLocationLayout.this.lastPressedVenue == venueLocation) {
                return;
            }
            ChatAttachAlertLocationLayout.this.showSearchPlacesButton(false);
            if (ChatAttachAlertLocationLayout.this.lastPressedMarker != null) {
                removeInfoView(ChatAttachAlertLocationLayout.this.lastPressedMarker);
                ChatAttachAlertLocationLayout.this.lastPressedMarker = null;
            }
            ChatAttachAlertLocationLayout.this.lastPressedVenue = venueLocation;
            ChatAttachAlertLocationLayout.this.lastPressedMarker = iMarker;
            Context context = getContext();
            FrameLayout frameLayout = new FrameLayout(context);
            addView(frameLayout, LayoutHelper.createFrame(-2, 114.0f));
            ChatAttachAlertLocationLayout.this.lastPressedMarkerView = new FrameLayout(context);
            ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setBackgroundResource(R.drawable.venue_tooltip);
            ChatAttachAlertLocationLayout.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(ChatAttachAlertLocationLayout.this.getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
            frameLayout.addView(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
            ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setAlpha(0.0f);
            ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$MapOverlayView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatAttachAlertLocationLayout.MapOverlayView.this.lambda$addInfoView$1(venueLocation, view);
                }
            });
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
            FrameLayout frameLayout2 = ChatAttachAlertLocationLayout.this.lastPressedMarkerView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            frameLayout2.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, i | 48, 18.0f, 32.0f, 18.0f, 0.0f));
            textView.setText(venueLocation.venue.title);
            textView2.setText(LocaleController.getString("TapToSendLocation", R.string.TapToSendLocation));
            final FrameLayout frameLayout3 = new FrameLayout(context);
            frameLayout3.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(36.0f), LocationCell.getColorForIndex(venueLocation.num)));
            frameLayout.addView(frameLayout3, LayoutHelper.createFrame(36, 36.0f, 81, 0.0f, 0.0f, 0.0f, 4.0f));
            BackupImageView backupImageView = new BackupImageView(context);
            backupImageView.setImage("https://ss3.4sqi.net/img/categories_v2/" + venueLocation.venue.venue_type + "_64.png", null, null);
            frameLayout3.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.MapOverlayView.1
                private final float[] animatorValues = {0.0f, 1.0f};
                private boolean startedInner;

                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float interpolation;
                    float lerp = AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction());
                    if (lerp >= 0.7f && !this.startedInner && ChatAttachAlertLocationLayout.this.lastPressedMarkerView != null) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.SCALE_X, 0.0f, 1.0f), ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.SCALE_Y, 0.0f, 1.0f), ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.ALPHA, 0.0f, 1.0f));
                        animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
                        animatorSet.setDuration(250L);
                        animatorSet.start();
                        this.startedInner = true;
                    }
                    if (lerp <= 0.5f) {
                        interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(lerp / 0.5f) * 1.1f;
                    } else if (lerp <= 0.75f) {
                        interpolation = 1.1f - (CubicBezierInterpolator.EASE_OUT.getInterpolation((lerp - 0.5f) / 0.25f) * 0.2f);
                    } else {
                        interpolation = (CubicBezierInterpolator.EASE_OUT.getInterpolation((lerp - 0.75f) / 0.25f) * 0.1f) + 0.9f;
                    }
                    frameLayout3.setScaleX(interpolation);
                    frameLayout3.setScaleY(interpolation);
                }
            });
            ofFloat.setDuration(360L);
            ofFloat.start();
            this.views.put(iMarker, frameLayout);
            ChatAttachAlertLocationLayout.this.map.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLng(iMarker.getPosition()), 300, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addInfoView$1(final VenueLocation venueLocation, View view) {
            ChatActivity chatActivity = (ChatActivity) ChatAttachAlertLocationLayout.this.parentAlert.baseFragment;
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(ChatAttachAlertLocationLayout.this.getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$MapOverlayView$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertLocationLayout.MapOverlayView.this.lambda$addInfoView$0(venueLocation, z, i);
                    }
                }, ChatAttachAlertLocationLayout.this.resourcesProvider);
                return;
            }
            ChatAttachAlertLocationLayout.this.delegate.didSelectLocation(venueLocation.venue, ChatAttachAlertLocationLayout.this.locationType, true, 0);
            ChatAttachAlertLocationLayout.this.parentAlert.dismiss(true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addInfoView$0(VenueLocation venueLocation, boolean z, int i) {
            ChatAttachAlertLocationLayout.this.delegate.didSelectLocation(venueLocation.venue, ChatAttachAlertLocationLayout.this.locationType, z, i);
            ChatAttachAlertLocationLayout.this.parentAlert.dismiss(true);
        }

        public void removeInfoView(IMapsProvider.IMarker iMarker) {
            View view = this.views.get(iMarker);
            if (view != null) {
                removeView(view);
                this.views.remove(iMarker);
            }
        }

        public void updatePositions() {
            if (ChatAttachAlertLocationLayout.this.map == null) {
                return;
            }
            IMapsProvider.IProjection projection = ChatAttachAlertLocationLayout.this.map.getProjection();
            for (Map.Entry<IMapsProvider.IMarker, View> entry : this.views.entrySet()) {
                View value = entry.getValue();
                android.graphics.Point screenLocation = projection.toScreenLocation(entry.getKey().getPosition());
                value.setTranslationX(screenLocation.x - (value.getMeasuredWidth() / 2));
                value.setTranslationY((screenLocation.y - value.getMeasuredHeight()) + AndroidUtilities.dp(22.0f));
            }
        }
    }

    public ChatAttachAlertLocationLayout(ChatAttachAlert chatAttachAlert, Context context, final Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        final ChatActivity chatActivity;
        this.checkGpsEnabled = true;
        this.locationDenied = false;
        this.isFirstLocation = true;
        this.backgroundPaint = new Paint();
        this.placeMarkers = new ArrayList<>();
        this.checkPermission = true;
        this.checkBackgroundPermission = true;
        int currentActionBarHeight = (AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f);
        this.overScrollHeight = currentActionBarHeight;
        this.mapHeight = currentActionBarHeight;
        this.first = true;
        this.bitmapCache = new Bitmap[7];
        AndroidUtilities.fixGoogleMapsBug();
        ChatActivity chatActivity2 = (ChatActivity) this.parentAlert.baseFragment;
        this.dialogId = chatActivity2.getDialogId();
        if (chatActivity2.getCurrentEncryptedChat() == null && !chatActivity2.isInScheduleMode() && !UserObject.isUserSelf(chatActivity2.getCurrentUser())) {
            this.locationType = 1;
        } else {
            this.locationType = 0;
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
        this.overlayView = new MapOverlayView(context);
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.1
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                ChatAttachAlertLocationLayout.this.searching = true;
                ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = ChatAttachAlertLocationLayout.this;
                chatAttachAlertLocationLayout.parentAlert.makeFocusable(chatAttachAlertLocationLayout.searchItem.getSearchField(), true);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                ChatAttachAlertLocationLayout.this.searching = false;
                ChatAttachAlertLocationLayout.this.searchWas = false;
                ChatAttachAlertLocationLayout.this.searchAdapter.searchDelayed(null, null);
                ChatAttachAlertLocationLayout.this.updateEmptyView();
                if (ChatAttachAlertLocationLayout.this.otherItem != null) {
                    ChatAttachAlertLocationLayout.this.otherItem.setVisibility(0);
                }
                ChatAttachAlertLocationLayout.this.listView.setVisibility(0);
                ChatAttachAlertLocationLayout.this.mapViewClip.setVisibility(0);
                ChatAttachAlertLocationLayout.this.searchListView.setVisibility(8);
                ChatAttachAlertLocationLayout.this.emptyView.setVisibility(8);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                if (ChatAttachAlertLocationLayout.this.searchAdapter == null) {
                    return;
                }
                String obj = editText.getText().toString();
                if (obj.length() != 0) {
                    ChatAttachAlertLocationLayout.this.searchWas = true;
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
                    chatAttachAlertLocationLayout.searchInProgress = chatAttachAlertLocationLayout.searchAdapter.isEmpty();
                    ChatAttachAlertLocationLayout.this.updateEmptyView();
                } else {
                    if (ChatAttachAlertLocationLayout.this.otherItem != null) {
                        ChatAttachAlertLocationLayout.this.otherItem.setVisibility(0);
                    }
                    ChatAttachAlertLocationLayout.this.listView.setVisibility(0);
                    ChatAttachAlertLocationLayout.this.mapViewClip.setVisibility(0);
                    ChatAttachAlertLocationLayout.this.searchListView.setAdapter(null);
                    ChatAttachAlertLocationLayout.this.searchListView.setVisibility(8);
                    ChatAttachAlertLocationLayout.this.emptyView.setVisibility(8);
                }
                ChatAttachAlertLocationLayout.this.searchAdapter.searchDelayed(obj, ChatAttachAlertLocationLayout.this.userLocation);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setVisibility(this.locationDenied ? 8 : 0);
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        int i2 = R.string.Search;
        actionBarMenuItem.setSearchFieldHint(LocaleController.getString("Search", i2));
        this.searchItem.setContentDescription(LocaleController.getString("Search", i2));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(getThemedColor("dialogTextBlack"));
        searchField.setCursorColor(getThemedColor("dialogTextBlack"));
        searchField.setHintTextColor(getThemedColor("chat_messagePanelHint"));
        new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f)).gravity = 83;
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.2
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int i3, int i4) {
                super.onMeasure(i3, i4);
                if (ChatAttachAlertLocationLayout.this.overlayView != null) {
                    ChatAttachAlertLocationLayout.this.overlayView.updatePositions();
                }
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View view, long j) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                ChatAttachAlertLocationLayout.this.backgroundPaint.setColor(ChatAttachAlertLocationLayout.this.getThemedColor("dialogBackground"));
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize, ChatAttachAlertLocationLayout.this.backgroundPaint);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getY() > getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize) {
                    return false;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getY() > getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize) {
                    return false;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.mapViewClip = frameLayout;
        frameLayout.setWillNotDraw(false);
        View view = new View(context);
        this.loadingMapView = view;
        view.setBackgroundDrawable(new MapPlaceholderDrawable());
        SearchButton searchButton = new SearchButton(context);
        this.searchAreaButton = searchButton;
        searchButton.setTranslationX(-AndroidUtilities.dp(80.0f));
        this.searchAreaButton.setVisibility(4);
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), getThemedColor("location_actionBackground"), getThemedColor("location_actionPressedBackground"));
        if (i < 21) {
            Drawable mutate = context.getResources().getDrawable(R.drawable.places_btn).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorRoundRectDrawable, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            combinedDrawable.setFullsize(true);
            createSimpleSelectorRoundRectDrawable = combinedDrawable;
        } else {
            StateListAnimator stateListAnimator = new StateListAnimator();
            SearchButton searchButton2 = this.searchAreaButton;
            Property property = View.TRANSLATION_Z;
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(searchButton2, property, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.searchAreaButton, property, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.searchAreaButton.setStateListAnimator(stateListAnimator);
            this.searchAreaButton.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.3
                @Override // android.view.ViewOutlineProvider
                @SuppressLint({"NewApi"})
                public void getOutline(View view2, Outline outline) {
                    outline.setRoundRect(0, 0, view2.getMeasuredWidth(), view2.getMeasuredHeight(), view2.getMeasuredHeight() / 2);
                }
            });
        }
        this.searchAreaButton.setBackgroundDrawable(createSimpleSelectorRoundRectDrawable);
        this.searchAreaButton.setTextColor(getThemedColor("location_actionActiveIcon"));
        this.searchAreaButton.setTextSize(1, 14.0f);
        this.searchAreaButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchAreaButton.setText(LocaleController.getString("PlacesInThisArea", R.string.PlacesInThisArea));
        this.searchAreaButton.setGravity(17);
        this.searchAreaButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.mapViewClip.addView(this.searchAreaButton, LayoutHelper.createFrame(-2, i >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
        this.searchAreaButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatAttachAlertLocationLayout.this.lambda$new$0(view2);
            }
        });
        ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, getThemedColor("location_actionIcon"), resourcesProvider);
        this.mapTypeButton = actionBarMenuItem2;
        actionBarMenuItem2.setClickable(true);
        this.mapTypeButton.setSubMenuOpenSide(2);
        this.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        this.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        this.mapTypeButton.addSubItem(2, R.drawable.msg_map, LocaleController.getString("Map", R.string.Map), resourcesProvider);
        this.mapTypeButton.addSubItem(3, R.drawable.msg_satellite, LocaleController.getString("Satellite", R.string.Satellite), resourcesProvider);
        this.mapTypeButton.addSubItem(4, R.drawable.msg_hybrid, LocaleController.getString("Hybrid", R.string.Hybrid), resourcesProvider);
        this.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), getThemedColor("location_actionBackground"), getThemedColor("location_actionPressedBackground"));
        if (i < 21) {
            Drawable mutate2 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
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
            stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(actionBarMenuItem3, property2, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            stateListAnimator2.addState(new int[0], ObjectAnimator.ofFloat(this.mapTypeButton, property2, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.mapTypeButton.setStateListAnimator(stateListAnimator2);
            this.mapTypeButton.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.4
                @Override // android.view.ViewOutlineProvider
                @SuppressLint({"NewApi"})
                public void getOutline(View view2, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.mapTypeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.mapTypeButton.setIcon(R.drawable.msg_map_type);
        this.mapViewClip.addView(this.mapTypeButton, LayoutHelper.createFrame(i >= 21 ? 40 : 44, i >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        this.mapTypeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatAttachAlertLocationLayout.this.lambda$new$1(view2);
            }
        });
        this.mapTypeButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda21
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i3) {
                ChatAttachAlertLocationLayout.this.lambda$new$2(i3);
            }
        });
        this.locationButton = new ImageView(context);
        Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), getThemedColor("location_actionBackground"), getThemedColor("location_actionPressedBackground"));
        if (i < 21) {
            Drawable mutate3 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(mutate3, createSimpleSelectorCircleDrawable2, 0, 0);
            combinedDrawable3.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable2 = combinedDrawable3;
        } else {
            StateListAnimator stateListAnimator3 = new StateListAnimator();
            ImageView imageView = this.locationButton;
            Property property3 = View.TRANSLATION_Z;
            stateListAnimator3.addState(new int[]{16842919}, ObjectAnimator.ofFloat(imageView, property3, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            stateListAnimator3.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, property3, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.locationButton.setStateListAnimator(stateListAnimator3);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.5
                @Override // android.view.ViewOutlineProvider
                @SuppressLint({"NewApi"})
                public void getOutline(View view2, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        this.locationButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable2);
        this.locationButton.setImageResource(R.drawable.msg_current_location);
        this.locationButton.setScaleType(ImageView.ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
        this.locationButton.setTag("location_actionActiveIcon");
        this.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", R.string.AccDescrMyLocation));
        this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(i >= 21 ? 40 : 44, i >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f));
        this.locationButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatAttachAlertLocationLayout.this.lambda$new$3(view2);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(1);
        this.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        this.emptyView.setVisibility(8);
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(ChatAttachAlertLocationLayout$$ExternalSyntheticLambda4.INSTANCE);
        ImageView imageView2 = new ImageView(context);
        this.emptyImageView = imageView2;
        imageView2.setImageResource(R.drawable.location_empty);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context);
        this.emptyTitleTextView = textView;
        textView.setTextColor(getThemedColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setText(LocaleController.getString("NoPlacesFound", R.string.NoPlacesFound));
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context);
        this.emptySubtitleTextView = textView2;
        textView2.setTextColor(getThemedColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.6
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z, int i3, int i4, int i5, int i6) {
                super.onLayout(z, i3, i4, i5, i6);
                ChatAttachAlertLocationLayout.this.updateClipView();
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = this.listView;
        LocationActivityAdapter locationActivityAdapter2 = new LocationActivityAdapter(context, this.locationType, this.dialogId, true, resourcesProvider);
        this.adapter = locationActivityAdapter2;
        recyclerListView2.setAdapter(locationActivityAdapter2);
        this.adapter.setUpdateRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.updateClipView();
            }
        });
        this.adapter.setMyLocationDenied(this.locationDenied);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView3 = this.listView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context, 1, false, 0, recyclerListView3) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.7
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i3) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.7.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateDyToMakeVisible(View view2, int i4) {
                        return super.calculateDyToMakeVisible(view2, i4) - (ChatAttachAlertLocationLayout.this.listView.getPaddingTop() - (ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight));
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateTimeForDeceleration(int i4) {
                        return super.calculateTimeForDeceleration(i4) * 4;
                    }
                };
                linearSmoothScroller.setTargetPosition(i3);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView3.setLayoutManager(fillLastLinearLayoutManager);
        addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.8
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                RecyclerListView.Holder holder;
                ChatAttachAlertLocationLayout.this.scrolling = i3 != 0;
                if (!ChatAttachAlertLocationLayout.this.scrolling && ChatAttachAlertLocationLayout.this.forceUpdate != null) {
                    ChatAttachAlertLocationLayout.this.forceUpdate = null;
                }
                if (i3 == 0) {
                    int dp = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertLocationLayout.this.parentAlert.getBackgroundPaddingTop();
                    if (((ChatAttachAlertLocationLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - dp) + backgroundPaddingTop >= ActionBar.getCurrentActionBarHeight() || (holder = (RecyclerListView.Holder) ChatAttachAlertLocationLayout.this.listView.findViewHolderForAdapterPosition(0)) == null || holder.itemView.getTop() <= ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight) {
                        return;
                    }
                    ChatAttachAlertLocationLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - (ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight));
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                ChatAttachAlertLocationLayout.this.updateClipView();
                if (ChatAttachAlertLocationLayout.this.forceUpdate != null) {
                    ChatAttachAlertLocationLayout.access$2816(ChatAttachAlertLocationLayout.this, i4);
                }
                ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = ChatAttachAlertLocationLayout.this;
                chatAttachAlertLocationLayout.parentAlert.updateLayout(chatAttachAlertLocationLayout, true, i4);
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda28
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i3) {
                ChatAttachAlertLocationLayout.this.lambda$new$7(chatActivity, resourcesProvider, view2, i3);
            }
        });
        this.adapter.setDelegate(this.dialogId, new BaseLocationAdapter.BaseLocationAdapterDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda23
            @Override // org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate
            public final void didLoadSearchResult(ArrayList arrayList) {
                ChatAttachAlertLocationLayout.this.updatePlacesMarkers(arrayList);
            }
        });
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        IMapsProvider.IMapView onCreateMapView = ApplicationLoader.getMapsProvider().onCreateMapView(context);
        this.mapView = onCreateMapView;
        onCreateMapView.setOnDispatchTouchEventInterceptor(new IMapsProvider.ITouchInterceptor() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda16
            @Override // org.telegram.messenger.IMapsProvider.ITouchInterceptor
            public final boolean onInterceptTouchEvent(MotionEvent motionEvent, IMapsProvider.ICallableMethod iCallableMethod) {
                boolean lambda$new$8;
                lambda$new$8 = ChatAttachAlertLocationLayout.this.lambda$new$8(motionEvent, iCallableMethod);
                return lambda$new$8;
            }
        });
        this.mapView.setOnInterceptTouchEventInterceptor(new IMapsProvider.ITouchInterceptor() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda17
            @Override // org.telegram.messenger.IMapsProvider.ITouchInterceptor
            public final boolean onInterceptTouchEvent(MotionEvent motionEvent, IMapsProvider.ICallableMethod iCallableMethod) {
                boolean lambda$new$9;
                lambda$new$9 = ChatAttachAlertLocationLayout.this.lambda$new$9(motionEvent, iCallableMethod);
                return lambda$new$9;
            }
        });
        final IMapsProvider.IMapView iMapView = this.mapView;
        new Thread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.lambda$new$14(iMapView);
            }
        }).start();
        ImageView imageView3 = new ImageView(context);
        this.markerImageView = imageView3;
        imageView3.setImageResource(R.drawable.map_pin2);
        this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(28, 48, 49));
        RecyclerListView recyclerListView4 = new RecyclerListView(context, resourcesProvider);
        this.searchListView = recyclerListView4;
        recyclerListView4.setVisibility(8);
        this.searchListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        LocationActivitySearchAdapter locationActivitySearchAdapter2 = new LocationActivitySearchAdapter(context) { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.9
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void notifyDataSetChanged() {
                if (ChatAttachAlertLocationLayout.this.searchItem != null) {
                    ChatAttachAlertLocationLayout.this.searchItem.setShowSearchProgress(ChatAttachAlertLocationLayout.this.searchAdapter.isSearching());
                }
                if (ChatAttachAlertLocationLayout.this.emptySubtitleTextView != null) {
                    ChatAttachAlertLocationLayout.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoPlacesFoundInfo", R.string.NoPlacesFoundInfo, ChatAttachAlertLocationLayout.this.searchAdapter.getLastSearchString())));
                }
                super.notifyDataSetChanged();
            }
        };
        this.searchAdapter = locationActivitySearchAdapter2;
        locationActivitySearchAdapter2.setDelegate(0L, new BaseLocationAdapter.BaseLocationAdapterDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda24
            @Override // org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate
            public final void didLoadSearchResult(ArrayList arrayList) {
                ChatAttachAlertLocationLayout.this.lambda$new$15(arrayList);
            }
        });
        this.searchListView.setItemAnimator(null);
        addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout.10
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                if (i3 != 1 || !ChatAttachAlertLocationLayout.this.searching || !ChatAttachAlertLocationLayout.this.searchWas) {
                    return;
                }
                AndroidUtilities.hideKeyboard(ChatAttachAlertLocationLayout.this.parentAlert.getCurrentFocus());
            }
        });
        this.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda29
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i3) {
                ChatAttachAlertLocationLayout.this.lambda$new$17(chatActivity, resourcesProvider, view2, i3);
            }
        });
        updateEmptyView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery(null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        this.mapTypeButton.toggleSubMenu();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(int i) {
        IMapsProvider.IMap iMap = this.map;
        if (iMap == null) {
            return;
        }
        if (i == 2) {
            iMap.setMapType(0);
        } else if (i == 3) {
            iMap.setMapType(1);
        } else if (i != 4) {
        } else {
            iMap.setMapType(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        Activity parentActivity;
        if (Build.VERSION.SDK_INT >= 23 && (parentActivity = getParentActivity()) != null && parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
            return;
        }
        if (this.myLocation != null && this.map != null) {
            this.locationButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag("location_actionActiveIcon");
            this.adapter.setCustomLocation(null);
            this.userLocationMoved = false;
            showSearchPlacesButton(false);
            this.map.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLng(new IMapsProvider.LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude())));
            if (this.searchedForCustomLocations) {
                Location location = this.myLocation;
                if (location != null) {
                    this.adapter.searchPlacesWithQuery(null, location, true, true);
                }
                this.searchedForCustomLocations = false;
                showResults();
            }
        }
        removeInfoView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view, int i) {
        if (i == 1) {
            if (this.delegate != null && this.userLocation != null) {
                FrameLayout frameLayout = this.lastPressedMarkerView;
                if (frameLayout != null) {
                    frameLayout.callOnClick();
                    return;
                }
                final TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo = new TLRPC$TL_messageMediaGeo();
                TLRPC$TL_geoPoint tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint();
                tLRPC$TL_messageMediaGeo.geo = tLRPC$TL_geoPoint;
                tLRPC$TL_geoPoint.lat = AndroidUtilities.fixLocationCoord(this.userLocation.getLatitude());
                tLRPC$TL_messageMediaGeo.geo._long = AndroidUtilities.fixLocationCoord(this.userLocation.getLongitude());
                if (chatActivity.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda26
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i2) {
                            ChatAttachAlertLocationLayout.this.lambda$new$5(tLRPC$TL_messageMediaGeo, z, i2);
                        }
                    }, resourcesProvider);
                    return;
                }
                this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeo, this.locationType, true, 0);
                this.parentAlert.dismiss(true);
            } else if (!this.locationDenied) {
            } else {
                AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
            }
        } else if (i == 2 && this.locationType == 1) {
            if (getLocationController().isSharingLocation(this.dialogId)) {
                getLocationController().removeSharingLocation(this.dialogId);
                this.parentAlert.dismiss(true);
            } else if (this.myLocation == null && this.locationDenied) {
                AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
            } else {
                openShareLiveLocation();
            }
        } else {
            final Object item = this.adapter.getItem(i);
            if (item instanceof TLRPC$TL_messageMediaVenue) {
                if (chatActivity.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda25
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i2) {
                            ChatAttachAlertLocationLayout.this.lambda$new$6(item, z, i2);
                        }
                    }, resourcesProvider);
                    return;
                }
                this.delegate.didSelectLocation((TLRPC$TL_messageMediaVenue) item, this.locationType, true, 0);
                this.parentAlert.dismiss(true);
            } else if (!(item instanceof LiveLocation)) {
            } else {
                LiveLocation liveLocation = (LiveLocation) item;
                this.map.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(new IMapsProvider.LatLng(liveLocation.marker.getPosition().latitude, liveLocation.marker.getPosition().longitude), this.map.getMaxZoomLevel() - 4.0f));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo, boolean z, int i) {
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeo, this.locationType, z, i);
        this.parentAlert.dismiss(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(Object obj, boolean z, int i) {
        this.delegate.didSelectLocation((TLRPC$TL_messageMediaVenue) obj, this.locationType, z, i);
        this.parentAlert.dismiss(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$8(MotionEvent motionEvent, IMapsProvider.ICallableMethod iCallableMethod) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$9(MotionEvent motionEvent, IMapsProvider.ICallableMethod iCallableMethod) {
        Location location;
        if (motionEvent.getAction() == 0) {
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animatorSet = animatorSet2;
            animatorSet2.setDuration(200L);
            this.animatorSet.playTogether(ObjectAnimator.ofFloat(this.markerImageView, View.TRANSLATION_Y, this.markerTop - AndroidUtilities.dp(10.0f)));
            this.animatorSet.start();
        } else if (motionEvent.getAction() == 1) {
            AnimatorSet animatorSet3 = this.animatorSet;
            if (animatorSet3 != null) {
                animatorSet3.cancel();
            }
            this.yOffset = 0.0f;
            AnimatorSet animatorSet4 = new AnimatorSet();
            this.animatorSet = animatorSet4;
            animatorSet4.setDuration(200L);
            this.animatorSet.playTogether(ObjectAnimator.ofFloat(this.markerImageView, View.TRANSLATION_Y, this.markerTop));
            this.animatorSet.start();
            this.adapter.fetchLocationAddress();
        }
        if (motionEvent.getAction() == 2) {
            if (!this.userLocationMoved) {
                this.locationButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
                this.locationButton.setTag("location_actionIcon");
                this.userLocationMoved = true;
            }
            IMapsProvider.IMap iMap = this.map;
            if (iMap != null && (location = this.userLocation) != null) {
                location.setLatitude(iMap.getCameraPosition().target.latitude);
                this.userLocation.setLongitude(this.map.getCameraPosition().target.longitude);
            }
            this.adapter.setCustomLocation(this.userLocation);
        }
        return ((Boolean) iCallableMethod.call(motionEvent)).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$14(final IMapsProvider.IMapView iMapView) {
        try {
            iMapView.onCreate(null);
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.lambda$new$13(iMapView);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$13(IMapsProvider.IMapView iMapView) {
        if (this.mapView == null || getParentActivity() == null) {
            return;
        }
        try {
            iMapView.onCreate(null);
            ApplicationLoader.getMapsProvider().initializeMaps(ApplicationLoader.applicationContext);
            this.mapView.getMapAsync(new Consumer() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda6
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    ChatAttachAlertLocationLayout.this.lambda$new$12((IMapsProvider.IMap) obj);
                }
            });
            this.mapsInitialized = true;
            if (!this.onResumeCalled) {
                return;
            }
            this.mapView.onResume();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$12(IMapsProvider.IMap iMap) {
        this.map = iMap;
        iMap.setOnMapLoadedCallback(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.lambda$new$11();
            }
        });
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            this.map.setMapStyle(ApplicationLoader.getMapsProvider().loadRawResourceStyle(ApplicationLoader.applicationContext, R.raw.mapstyle_night));
        }
        onMapInit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$11() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.lambda$new$10();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10() {
        this.loadingMapView.setTag(1);
        this.loadingMapView.animate().alpha(0.0f).setDuration(180L).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$15(ArrayList arrayList) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$17(ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view, int i) {
        final TLRPC$TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item == null || this.delegate == null) {
            return;
        }
        if (chatActivity.isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda27
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z, int i2) {
                    ChatAttachAlertLocationLayout.this.lambda$new$16(item, z, i2);
                }
            }, resourcesProvider);
            return;
        }
        this.delegate.didSelectLocation(item, this.locationType, true, 0);
        this.parentAlert.dismiss(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$16(TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, boolean z, int i) {
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaVenue, this.locationType, z, i);
        this.parentAlert.dismiss(true);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    boolean shouldHideBottomButtons() {
        return !this.locationDenied;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onPause() {
        IMapsProvider.IMapView iMapView = this.mapView;
        if (iMapView != null && this.mapsInitialized) {
            try {
                iMapView.onPause();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionDenied);
        try {
            IMapsProvider.IMap iMap = this.map;
            if (iMap != null) {
                iMap.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        IMapsProvider.IMapView iMapView = this.mapView;
        if (iMapView != null) {
            iMapView.getView().setTranslationY((-AndroidUtilities.displaySize.y) * 3);
        }
        try {
            IMapsProvider.IMapView iMapView2 = this.mapView;
            if (iMapView2 != null) {
                iMapView2.onPause();
            }
        } catch (Exception unused) {
        }
        try {
            IMapsProvider.IMapView iMapView3 = this.mapView;
            if (iMapView3 != null) {
                iMapView3.onDestroy();
                this.mapView = null;
            }
        } catch (Exception unused2) {
        }
        LocationActivityAdapter locationActivityAdapter = this.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = this.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        this.parentAlert.actionBar.closeSearchField();
        this.parentAlert.actionBar.createMenu().removeView(this.searchItem);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHide() {
        this.searchItem.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onDismiss() {
        onDestroy();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
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

    @Override // android.view.View
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
        updateClipView();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x003e  */
    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void onPreMeasure(int r4, int r5) {
        /*
            r3 = this;
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            org.telegram.ui.ActionBar.ActionBar r4 = r4.actionBar
            boolean r4 = r4.isSearchFieldVisible()
            r0 = 1
            r1 = 0
            if (r4 != 0) goto L45
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r4.sizeNotifierFrameLayout
            int r4 = r4.measureKeyboardHeight()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            if (r4 <= r2) goto L1d
            goto L45
        L1d:
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 != 0) goto L31
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r4.x
            int r4 = r4.y
            if (r2 <= r4) goto L31
            float r4 = (float) r5
            r5 = 1080033280(0x40600000, float:3.5)
            float r4 = r4 / r5
            int r4 = (int) r4
            goto L35
        L31:
            int r5 = r5 / 5
            int r4 = r5 * 2
        L35:
            r5 = 1112539136(0x42500000, float:52.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            if (r4 >= 0) goto L3f
            r4 = 0
        L3f:
            org.telegram.ui.Components.ChatAttachAlert r5 = r3.parentAlert
            r5.setAllowNestedScroll(r0)
            goto L4f
        L45:
            int r4 = r3.mapHeight
            int r5 = r3.overScrollHeight
            int r4 = r4 - r5
            org.telegram.ui.Components.ChatAttachAlert r5 = r3.parentAlert
            r5.setAllowNestedScroll(r1)
        L4f:
            org.telegram.ui.Components.RecyclerListView r5 = r3.listView
            int r5 = r5.getPaddingTop()
            if (r5 == r4) goto L60
            r3.ignoreLayout = r0
            org.telegram.ui.Components.RecyclerListView r5 = r3.listView
            r5.setPadding(r1, r4, r1, r1)
            r3.ignoreLayout = r1
        L60:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertLocationLayout.onPreMeasure(int, int):void");
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            fixLayoutInternal(this.first);
            this.first = false;
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getButtonsHideOffset() {
        return AndroidUtilities.dp(56.0f);
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    private boolean isActiveThemeDark() {
        return Theme.getActiveTheme().isDark() || AndroidUtilities.computePerceivedBrightness(getThemedColor("windowBackgroundWhite")) < 0.721f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEmptyView() {
        if (this.searching) {
            if (this.searchInProgress) {
                this.searchListView.setEmptyView(null);
                this.emptyView.setVisibility(8);
                return;
            }
            this.searchListView.setEmptyView(this.emptyView);
            return;
        }
        this.emptyView.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSearchPlacesButton(boolean z) {
        SearchButton searchButton;
        Location location;
        Location location2;
        if (z && (searchButton = this.searchAreaButton) != null && searchButton.getTag() == null && ((location = this.myLocation) == null || (location2 = this.userLocation) == null || location2.distanceTo(location) < 300.0f)) {
            z = false;
        }
        SearchButton searchButton2 = this.searchAreaButton;
        if (searchButton2 != null) {
            if (z && searchButton2.getTag() != null) {
                return;
            }
            if (!z && this.searchAreaButton.getTag() == null) {
                return;
            }
            this.searchAreaButton.setVisibility(z ? 0 : 4);
            this.searchAreaButton.setTag(z ? 1 : null);
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            SearchButton searchButton3 = this.searchAreaButton;
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            fArr[0] = z ? 0.0f : -AndroidUtilities.dp(80.0f);
            animatorArr[0] = ObjectAnimator.ofFloat(searchButton3, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(180L);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.start();
        }
    }

    public void openShareLiveLocation() {
        Activity parentActivity;
        if (this.delegate == null || getParentActivity() == null || this.myLocation == null) {
            return;
        }
        if (this.checkBackgroundPermission && Build.VERSION.SDK_INT >= 29 && (parentActivity = getParentActivity()) != null) {
            this.checkBackgroundPermission = false;
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (Math.abs((System.currentTimeMillis() / 1000) - globalMainSettings.getInt("backgroundloc", 0)) > 86400 && parentActivity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
                globalMainSettings.edit().putInt("backgroundloc", (int) (System.currentTimeMillis() / 1000)).commit();
                AlertsCreator.createBackgroundLocationPermissionDialog(parentActivity, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertLocationLayout.this.openShareLiveLocation();
                    }
                }, this.resourcesProvider).show();
                return;
            }
        }
        TLRPC$User tLRPC$User = null;
        if (DialogObject.isUserDialog(this.dialogId)) {
            tLRPC$User = this.parentAlert.baseFragment.getMessagesController().getUser(Long.valueOf(this.dialogId));
        }
        AlertsCreator.createLocationUpdateDialog(getParentActivity(), tLRPC$User, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda20
            @Override // org.telegram.messenger.MessagesStorage.IntCallback
            public final void run(int i) {
                ChatAttachAlertLocationLayout.this.lambda$openShareLiveLocation$18(i);
            }
        }, this.resourcesProvider).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$openShareLiveLocation$18(int i) {
        TLRPC$TL_messageMediaGeoLive tLRPC$TL_messageMediaGeoLive = new TLRPC$TL_messageMediaGeoLive();
        TLRPC$TL_geoPoint tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint();
        tLRPC$TL_messageMediaGeoLive.geo = tLRPC$TL_geoPoint;
        tLRPC$TL_geoPoint.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        tLRPC$TL_messageMediaGeoLive.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        tLRPC$TL_messageMediaGeoLive.period = i;
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeoLive, this.locationType, true, 0);
        this.parentAlert.dismiss(true);
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
            canvas.drawCircle(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), paint);
            paint.setColor(LocationCell.getColorForIndex(i));
            canvas.drawCircle(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(5.0f), paint);
            canvas.setBitmap(null);
            this.bitmapCache[i % 7] = createBitmap;
            return createBitmap;
        } catch (Throwable th) {
            FileLog.e(th);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePlacesMarkers(ArrayList<TLRPC$TL_messageMediaVenue> arrayList) {
        if (arrayList == null) {
            return;
        }
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
                FileLog.e(e);
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

    /* JADX INFO: Access modifiers changed from: private */
    public Activity getParentActivity() {
        BaseFragment baseFragment;
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (chatAttachAlert == null || (baseFragment = chatAttachAlert.baseFragment) == null) {
            return null;
        }
        return baseFragment.getParentActivity();
    }

    private void onMapInit() {
        if (this.map == null) {
            return;
        }
        Location location = new Location("network");
        this.userLocation = location;
        location.setLatitude(20.659322d);
        this.userLocation.setLongitude(-11.40625d);
        try {
            this.map.setMyLocationEnabled(true);
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.map.getUiSettings().setMyLocationButtonEnabled(false);
        this.map.getUiSettings().setZoomControlsEnabled(false);
        this.map.getUiSettings().setCompassEnabled(false);
        this.map.setOnCameraMoveStartedListener(new IMapsProvider.OnCameraMoveStartedListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda18
            @Override // org.telegram.messenger.IMapsProvider.OnCameraMoveStartedListener
            public final void onCameraMoveStarted(int i) {
                ChatAttachAlertLocationLayout.this.lambda$onMapInit$19(i);
            }
        });
        this.map.setOnMyLocationChangeListener(new Consumer() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda5
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                ChatAttachAlertLocationLayout.this.lambda$onMapInit$20((Location) obj);
            }
        });
        this.map.setOnMarkerClickListener(new IMapsProvider.OnMarkerClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda19
            @Override // org.telegram.messenger.IMapsProvider.OnMarkerClickListener
            public final boolean onClick(IMapsProvider.IMarker iMarker) {
                boolean lambda$onMapInit$21;
                lambda$onMapInit$21 = ChatAttachAlertLocationLayout.this.lambda$onMapInit$21(iMarker);
                return lambda$onMapInit$21;
            }
        });
        this.map.setOnCameraMoveListener(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.lambda$onMapInit$22();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.lambda$onMapInit$23();
            }
        }, 200L);
        Location lastLocation = getLastLocation();
        this.myLocation = lastLocation;
        positionMarker(lastLocation);
        if (this.checkGpsEnabled && getParentActivity() != null) {
            this.checkGpsEnabled = false;
            if (!getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                return;
            }
            try {
                if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
                    builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor("dialogTopBackground"));
                    builder.setMessage(LocaleController.getString("GpsDisabledAlertText", R.string.GpsDisabledAlertText));
                    builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatAttachAlertLocationLayout.this.lambda$onMapInit$24(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder.show();
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        updateClipView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$19(int i) {
        View childAt;
        RecyclerView.ViewHolder findContainingViewHolder;
        if (i == 1) {
            showSearchPlacesButton(true);
            removeInfoView();
            if (this.scrolling || this.listView.getChildCount() <= 0 || (childAt = this.listView.getChildAt(0)) == null || (findContainingViewHolder = this.listView.findContainingViewHolder(childAt)) == null || findContainingViewHolder.getAdapterPosition() != 0) {
                return;
            }
            int dp = this.locationType == 0 ? 0 : AndroidUtilities.dp(66.0f);
            int top = childAt.getTop();
            if (top >= (-dp)) {
                return;
            }
            IMapsProvider.CameraPosition cameraPosition = this.map.getCameraPosition();
            this.forceUpdate = ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(cameraPosition.target, cameraPosition.zoom);
            this.listView.smoothScrollBy(0, top + dp);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$20(Location location) {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (chatAttachAlert == null || chatAttachAlert.baseFragment == null) {
            return;
        }
        positionMarker(location);
        getLocationController().setMapLocation(location, this.isFirstLocation);
        this.isFirstLocation = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onMapInit$21(IMapsProvider.IMarker iMarker) {
        if (!(iMarker.getTag() instanceof VenueLocation)) {
            return true;
        }
        this.markerImageView.setVisibility(4);
        if (!this.userLocationMoved) {
            this.locationButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
            this.locationButton.setTag("location_actionIcon");
            this.userLocationMoved = true;
        }
        this.overlayView.addInfoView(iMarker);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$22() {
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null) {
            mapOverlayView.updatePositions();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$23() {
        if (this.loadingMapView.getTag() == null) {
            this.loadingMapView.animate().alpha(0.0f).setDuration(180L).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onMapInit$24(DialogInterface dialogInterface, int i) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } catch (Exception unused) {
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
            if (dp < 0 || dp > AndroidUtilities.dp(258.0f)) {
                return;
            }
            this.listView.smoothScrollBy(0, dp);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateClipView() {
        int i;
        int i2;
        IMapsProvider.LatLng latLng;
        if (this.mapView == null || this.mapViewClip == null) {
            return;
        }
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            i = (int) findViewHolderForAdapterPosition.itemView.getY();
            i2 = this.overScrollHeight + Math.min(i, 0);
        } else {
            i = -this.mapViewClip.getMeasuredHeight();
            i2 = 0;
        }
        if (((FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams()) == null) {
            return;
        }
        if (i2 <= 0) {
            if (this.mapView.getView().getVisibility() == 0) {
                this.mapView.getView().setVisibility(4);
                this.mapViewClip.setVisibility(4);
                MapOverlayView mapOverlayView = this.overlayView;
                if (mapOverlayView != null) {
                    mapOverlayView.setVisibility(4);
                }
            }
            this.mapView.getView().setTranslationY(i);
            return;
        }
        if (this.mapView.getView().getVisibility() == 4) {
            this.mapView.getView().setVisibility(0);
            this.mapViewClip.setVisibility(0);
            MapOverlayView mapOverlayView2 = this.overlayView;
            if (mapOverlayView2 != null) {
                mapOverlayView2.setVisibility(0);
            }
        }
        int max = Math.max(0, (-((i - this.mapHeight) + this.overScrollHeight)) / 2);
        int i3 = this.mapHeight - this.overScrollHeight;
        float max2 = 1.0f - Math.max(0.0f, Math.min(1.0f, (this.listView.getPaddingTop() - i) / (this.listView.getPaddingTop() - i3)));
        int i4 = this.clipSize;
        if (this.locationDenied && isTypeSend()) {
            i3 += Math.min(i, this.listView.getPaddingTop());
        }
        this.clipSize = (int) (i3 * max2);
        float f = max;
        this.mapView.getView().setTranslationY(f);
        this.nonClipSize = i3 - this.clipSize;
        this.mapViewClip.invalidate();
        this.mapViewClip.setTranslationY(i - this.nonClipSize);
        IMapsProvider.IMap iMap = this.map;
        if (iMap != null) {
            iMap.setPadding(0, AndroidUtilities.dp(6.0f), 0, this.clipSize + AndroidUtilities.dp(6.0f));
        }
        MapOverlayView mapOverlayView3 = this.overlayView;
        if (mapOverlayView3 != null) {
            mapOverlayView3.setTranslationY(f);
        }
        float min = Math.min(Math.max(this.nonClipSize - i, 0), (this.mapHeight - this.mapTypeButton.getMeasuredHeight()) - AndroidUtilities.dp(80.0f));
        this.mapTypeButton.setTranslationY(min);
        this.searchAreaButton.setTranslation(min);
        this.locationButton.setTranslationY(-this.clipSize);
        ImageView imageView = this.markerImageView;
        int dp = (((this.mapHeight - this.clipSize) / 2) - AndroidUtilities.dp(48.0f)) + max;
        this.markerTop = dp;
        imageView.setTranslationY(dp);
        if (i4 != this.clipSize) {
            IMapsProvider.IMarker iMarker = this.lastPressedMarker;
            if (iMarker != null) {
                latLng = new IMapsProvider.LatLng(iMarker.getPosition().latitude, this.lastPressedMarker.getPosition().longitude);
            } else if (this.userLocationMoved) {
                latLng = new IMapsProvider.LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
            } else {
                Location location = this.myLocation;
                latLng = location != null ? new IMapsProvider.LatLng(location.getLatitude(), this.myLocation.getLongitude()) : null;
            }
            if (latLng != null) {
                this.map.moveCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLng(latLng));
            }
        }
        if (!this.locationDenied || !isTypeSend()) {
            return;
        }
        int itemCount = this.adapter.getItemCount();
        for (int i5 = 1; i5 < itemCount; i5++) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(i5);
            if (findViewHolderForAdapterPosition2 != null) {
                findViewHolderForAdapterPosition2.itemView.setTranslationY(this.listView.getPaddingTop() - i);
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
        if (getMeasuredHeight() == 0 || this.mapView == null) {
            return;
        }
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
        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.mapView.getView().getLayoutParams();
        if (layoutParams5 != null) {
            layoutParams5.height = this.mapHeight + AndroidUtilities.dp(10.0f);
            this.mapView.getView().setLayoutParams(layoutParams5);
        }
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null && (layoutParams = (FrameLayout.LayoutParams) mapOverlayView.getLayoutParams()) != null) {
            layoutParams.height = this.mapHeight + AndroidUtilities.dp(10.0f);
            this.overlayView.setLayoutParams(layoutParams);
        }
        this.adapter.notifyDataSetChanged();
        updateClipView();
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
        if (location == null) {
            return;
        }
        Location location2 = new Location(location);
        this.myLocation = location2;
        if (this.map != null) {
            IMapsProvider.LatLng latLng = new IMapsProvider.LatLng(location.getLatitude(), location.getLongitude());
            LocationActivityAdapter locationActivityAdapter = this.adapter;
            if (locationActivityAdapter != null) {
                if (!this.searchedForCustomLocations) {
                    locationActivityAdapter.searchPlacesWithQuery(null, this.myLocation, true);
                }
                this.adapter.setGpsLocation(this.myLocation);
            }
            if (this.userLocationMoved) {
                return;
            }
            this.userLocation = new Location(location);
            if (this.firstWas) {
                this.map.animateCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLng(latLng));
                return;
            }
            this.firstWas = true;
            this.map.moveCamera(ApplicationLoader.getMapsProvider().newCameraUpdateLatLngZoom(latLng, this.map.getMaxZoomLevel() - 4.0f));
            return;
        }
        this.adapter.setGpsLocation(location2);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.locationPermissionGranted) {
            this.locationDenied = false;
            LocationActivityAdapter locationActivityAdapter = this.adapter;
            if (locationActivityAdapter != null) {
                locationActivityAdapter.setMyLocationDenied(false);
            }
            IMapsProvider.IMap iMap = this.map;
            if (iMap != null) {
                try {
                    iMap.setMyLocationEnabled(true);
                } catch (Exception e) {
                    FileLog.e(e);
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

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onResume() {
        IMapsProvider.IMapView iMapView = this.mapView;
        if (iMapView != null && this.mapsInitialized) {
            try {
                iMapView.onResume();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        this.onResumeCalled = true;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        this.parentAlert.actionBar.setTitle(LocaleController.getString("ShareLocation", R.string.ShareLocation));
        if (this.mapView.getView().getParent() == null) {
            this.mapViewClip.addView(this.mapView.getView(), 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
            this.mapViewClip.addView(this.overlayView, 1, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
            this.mapViewClip.addView(this.loadingMapView, 2, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.searchItem.setVisibility(0);
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
                FileLog.e(e);
            }
        }
        fixLayoutInternal(true);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertLocationLayout.this.lambda$onShow$25();
            }
        }, this.parentAlert.delegate.needEnterComment() ? 200L : 0L);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        updateClipView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onShow$25() {
        Activity parentActivity;
        if (!this.checkPermission || Build.VERSION.SDK_INT < 23 || (parentActivity = getParentActivity()) == null) {
            return;
        }
        this.checkPermission = false;
        if (parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            return;
        }
        parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
    }

    public void setDelegate(LocationActivityDelegate locationActivityDelegate) {
        this.delegate = locationActivityDelegate;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertLocationLayout$$ExternalSyntheticLambda22
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatAttachAlertLocationLayout.this.lambda$getThemeDescriptions$26();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.mapViewClip, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "dialogScrollGlow"));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        arrayList.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "dialogEmptyImage"));
        arrayList.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "location_actionActiveIcon"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, 0, null, null, null, themeDescriptionDelegate, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "location_actionActiveIcon"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "location_actionPressedBackground"));
        arrayList.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, themeDescriptionDelegate, "avatar_text"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "location_liveLocationProgress"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "location_placeLocationBackground"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, "dialog_liveLocationProgress"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationBackground"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLiveLocationText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_sendLocationText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LocationDirectionCell.class}, new String[]{"buttonTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{LocationDirectionCell.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlue2"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"distanceTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$26() {
        this.mapTypeButton.setIconColor(getThemedColor("location_actionIcon"));
        this.mapTypeButton.redrawPopup(getThemedColor("actionBarDefaultSubmenuBackground"));
        this.mapTypeButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItemIcon"), true);
        this.mapTypeButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItem"), false);
        if (this.map != null) {
            if (isActiveThemeDark()) {
                if (this.currentMapStyleDark) {
                    return;
                }
                this.currentMapStyleDark = true;
                this.map.setMapStyle(ApplicationLoader.getMapsProvider().loadRawResourceStyle(ApplicationLoader.applicationContext, R.raw.mapstyle_night));
            } else if (!this.currentMapStyleDark) {
            } else {
                this.currentMapStyleDark = false;
                this.map.setMapStyle(null);
            }
        }
    }
}
