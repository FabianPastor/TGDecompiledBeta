package org.telegram.ui.Components;

import android.animation.Animator;
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
import com.google.android.gms.maps.model.CircleOptions;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
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
    public static final int LOCATION_TYPE_SEND = 0;
    public static final int LOCATION_TYPE_SEND_WITH_LIVE = 1;
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    /* access modifiers changed from: private */
    public LocationActivityAdapter adapter;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public Paint backgroundPaint;
    private Bitmap[] bitmapCache;
    private boolean checkBackgroundPermission;
    private boolean checkGpsEnabled = true;
    private boolean checkPermission;
    private CircleOptions circleOptions;
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
    private boolean firstFocus = true;
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
    private ArrayList<VenueLocation> placeMarkers;
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
    private boolean wasResults;
    /* access modifiers changed from: private */
    public float yOffset;

    public static class LiveLocation {
        public TLRPC.Chat chat;
        public int id;
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

    static /* synthetic */ float access$2816(ChatAttachAlertLocationLayout x0, float x1) {
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
            if (ChatAttachAlertLocationLayout.this.lastPressedVenue != location) {
                ChatAttachAlertLocationLayout.this.showSearchPlacesButton(false);
                if (ChatAttachAlertLocationLayout.this.lastPressedMarker != null) {
                    removeInfoView(ChatAttachAlertLocationLayout.this.lastPressedMarker);
                    Marker unused = ChatAttachAlertLocationLayout.this.lastPressedMarker = null;
                }
                VenueLocation unused2 = ChatAttachAlertLocationLayout.this.lastPressedVenue = location;
                Marker unused3 = ChatAttachAlertLocationLayout.this.lastPressedMarker = marker2;
                Context context = getContext();
                FrameLayout frameLayout = new FrameLayout(context);
                addView(frameLayout, LayoutHelper.createFrame(-2, 114.0f));
                FrameLayout unused4 = ChatAttachAlertLocationLayout.this.lastPressedMarkerView = new FrameLayout(context);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setBackgroundResource(NUM);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(ChatAttachAlertLocationLayout.this.getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
                frameLayout.addView(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setAlpha(0.0f);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setOnClickListener(new ChatAttachAlertLocationLayout$MapOverlayView$$ExternalSyntheticLambda0(this, location));
                TextView nameTextView = new TextView(context);
                nameTextView.setTextSize(1, 16.0f);
                nameTextView.setMaxLines(1);
                nameTextView.setEllipsize(TextUtils.TruncateAt.END);
                nameTextView.setSingleLine(true);
                nameTextView.setTextColor(ChatAttachAlertLocationLayout.this.getThemedColor("windowBackgroundWhiteBlackText"));
                nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                int i = 5;
                nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.addView(nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 10.0f, 18.0f, 0.0f));
                TextView addressTextView = new TextView(context);
                addressTextView.setTextSize(1, 14.0f);
                addressTextView.setMaxLines(1);
                addressTextView.setEllipsize(TextUtils.TruncateAt.END);
                addressTextView.setSingleLine(true);
                addressTextView.setTextColor(ChatAttachAlertLocationLayout.this.getThemedColor("windowBackgroundWhiteGrayText3"));
                addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
                FrameLayout access$300 = ChatAttachAlertLocationLayout.this.lastPressedMarkerView;
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
                        if (value >= 0.7f && !this.startedInner && ChatAttachAlertLocationLayout.this.lastPressedMarkerView != null) {
                            AnimatorSet animatorSet1 = new AnimatorSet();
                            animatorSet1.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.SCALE_Y, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, View.ALPHA, new float[]{0.0f, 1.0f})});
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
                ChatAttachAlertLocationLayout.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 300, (GoogleMap.CancelableCallback) null);
            }
        }

        /* renamed from: lambda$addInfoView$1$org-telegram-ui-Components-ChatAttachAlertLocationLayout$MapOverlayView  reason: not valid java name */
        public /* synthetic */ void m824x73b338e0(VenueLocation location, View v) {
            ChatActivity chatActivity = (ChatActivity) ChatAttachAlertLocationLayout.this.parentAlert.baseFragment;
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog((Context) ChatAttachAlertLocationLayout.this.getParentActivity(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertLocationLayout$MapOverlayView$$ExternalSyntheticLambda1(this, location), ChatAttachAlertLocationLayout.this.resourcesProvider);
                return;
            }
            ChatAttachAlertLocationLayout.this.delegate.didSelectLocation(location.venue, ChatAttachAlertLocationLayout.this.locationType, true, 0);
            ChatAttachAlertLocationLayout.this.parentAlert.dismiss(true);
        }

        /* renamed from: lambda$addInfoView$0$org-telegram-ui-Components-ChatAttachAlertLocationLayout$MapOverlayView  reason: not valid java name */
        public /* synthetic */ void m823xaca751df(VenueLocation location, boolean notify, int scheduleDate) {
            ChatAttachAlertLocationLayout.this.delegate.didSelectLocation(location.venue, ChatAttachAlertLocationLayout.this.locationType, notify, scheduleDate);
            ChatAttachAlertLocationLayout.this.parentAlert.dismiss(true);
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
                for (Map.Entry<Marker, View> entry : this.views.entrySet()) {
                    View view = entry.getValue();
                    Point point = projection.toScreenLocation(entry.getKey().getPosition());
                    view.setTranslationX((float) (point.x - (view.getMeasuredWidth() / 2)));
                    view.setTranslationY((float) ((point.y - view.getMeasuredHeight()) + AndroidUtilities.dp(22.0f)));
                }
            }
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatAttachAlertLocationLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        Drawable drawable;
        Drawable drawable2;
        ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = this;
        Context context2 = context;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        chatAttachAlertLocationLayout.backgroundPaint = new Paint();
        chatAttachAlertLocationLayout.placeMarkers = new ArrayList<>();
        chatAttachAlertLocationLayout.checkPermission = true;
        chatAttachAlertLocationLayout.checkBackgroundPermission = true;
        int currentActionBarHeight = (AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f);
        chatAttachAlertLocationLayout.overScrollHeight = currentActionBarHeight;
        chatAttachAlertLocationLayout.mapHeight = currentActionBarHeight;
        chatAttachAlertLocationLayout.first = true;
        chatAttachAlertLocationLayout.bitmapCache = new Bitmap[7];
        AndroidUtilities.fixGoogleMapsBug();
        ChatActivity chatActivity = (ChatActivity) chatAttachAlertLocationLayout.parentAlert.baseFragment;
        chatAttachAlertLocationLayout.dialogId = chatActivity.getDialogId();
        if (chatActivity.getCurrentEncryptedChat() != null || chatActivity.isInScheduleMode() || UserObject.isUserSelf(chatActivity.getCurrentUser())) {
            chatAttachAlertLocationLayout.locationType = 0;
        } else {
            chatAttachAlertLocationLayout.locationType = 1;
        }
        NotificationCenter.getGlobalInstance().addObserver(chatAttachAlertLocationLayout, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(chatAttachAlertLocationLayout, NotificationCenter.locationPermissionDenied);
        chatAttachAlertLocationLayout.searchWas = false;
        chatAttachAlertLocationLayout.searching = false;
        chatAttachAlertLocationLayout.searchInProgress = false;
        LocationActivityAdapter locationActivityAdapter = chatAttachAlertLocationLayout.adapter;
        if (locationActivityAdapter != null) {
            locationActivityAdapter.destroy();
        }
        LocationActivitySearchAdapter locationActivitySearchAdapter = chatAttachAlertLocationLayout.searchAdapter;
        if (locationActivitySearchAdapter != null) {
            locationActivitySearchAdapter.destroy();
        }
        chatAttachAlertLocationLayout.locationDenied = (Build.VERSION.SDK_INT < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) ? false : true;
        ActionBarMenu menu = chatAttachAlertLocationLayout.parentAlert.actionBar.createMenu();
        chatAttachAlertLocationLayout.overlayView = new MapOverlayView(context2);
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = ChatAttachAlertLocationLayout.this.searching = true;
                ChatAttachAlertLocationLayout.this.parentAlert.makeFocusable(ChatAttachAlertLocationLayout.this.searchItem.getSearchField(), true);
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
                    String text = editText.getText().toString();
                    if (text.length() != 0) {
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
                    ChatAttachAlertLocationLayout.this.searchAdapter.searchDelayed(text, ChatAttachAlertLocationLayout.this.userLocation);
                }
            }
        });
        chatAttachAlertLocationLayout.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setVisibility(chatAttachAlertLocationLayout.locationDenied ? 8 : 0);
        chatAttachAlertLocationLayout.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        chatAttachAlertLocationLayout.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        EditTextBoldCursor editText = chatAttachAlertLocationLayout.searchItem.getSearchField();
        editText.setTextColor(chatAttachAlertLocationLayout.getThemedColor("dialogTextBlack"));
        editText.setCursorColor(chatAttachAlertLocationLayout.getThemedColor("dialogTextBlack"));
        editText.setHintTextColor(chatAttachAlertLocationLayout.getThemedColor("chat_messagePanelHint"));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f));
        layoutParams.gravity = 83;
        AnonymousClass2 r0 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (ChatAttachAlertLocationLayout.this.overlayView != null) {
                    ChatAttachAlertLocationLayout.this.overlayView.updatePositions();
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize);
                boolean result = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return result;
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                ChatAttachAlertLocationLayout.this.backgroundPaint.setColor(ChatAttachAlertLocationLayout.this.getThemedColor("dialogBackground"));
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize), ChatAttachAlertLocationLayout.this.backgroundPaint);
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getY() > ((float) (getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize))) {
                    return false;
                }
                return super.onInterceptTouchEvent(ev);
            }

            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ev.getY() > ((float) (getMeasuredHeight() - ChatAttachAlertLocationLayout.this.clipSize))) {
                    return false;
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        chatAttachAlertLocationLayout.mapViewClip = r0;
        r0.setWillNotDraw(false);
        View view = new View(context2);
        chatAttachAlertLocationLayout.loadingMapView = view;
        view.setBackgroundDrawable(new MapPlaceholderDrawable());
        SearchButton searchButton = new SearchButton(context2);
        chatAttachAlertLocationLayout.searchAreaButton = searchButton;
        searchButton.setTranslationX((float) (-AndroidUtilities.dp(80.0f)));
        chatAttachAlertLocationLayout.searchAreaButton.setVisibility(4);
        Drawable drawable3 = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), chatAttachAlertLocationLayout.getThemedColor("location_actionBackground"), chatAttachAlertLocationLayout.getThemedColor("location_actionPressedBackground"));
        EditTextBoldCursor editTextBoldCursor = editText;
        String str = "location_actionBackground";
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable3, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            combinedDrawable.setFullsize(true);
            drawable = combinedDrawable;
        } else {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.searchAreaButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.searchAreaButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            chatAttachAlertLocationLayout.searchAreaButton.setStateListAnimator(animator);
            chatAttachAlertLocationLayout.searchAreaButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (float) (view.getMeasuredHeight() / 2));
                }
            });
            drawable = drawable3;
        }
        chatAttachAlertLocationLayout.searchAreaButton.setBackgroundDrawable(drawable);
        chatAttachAlertLocationLayout.searchAreaButton.setTextColor(chatAttachAlertLocationLayout.getThemedColor("location_actionActiveIcon"));
        chatAttachAlertLocationLayout.searchAreaButton.setTextSize(1, 14.0f);
        chatAttachAlertLocationLayout.searchAreaButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chatAttachAlertLocationLayout.searchAreaButton.setText(LocaleController.getString("PlacesInThisArea", NUM));
        chatAttachAlertLocationLayout.searchAreaButton.setGravity(17);
        chatAttachAlertLocationLayout.searchAreaButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        chatAttachAlertLocationLayout.mapViewClip.addView(chatAttachAlertLocationLayout.searchAreaButton, LayoutHelper.createFrame(-2, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
        chatAttachAlertLocationLayout.searchAreaButton.setOnClickListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda11(chatAttachAlertLocationLayout));
        String str2 = "location_actionPressedBackground";
        String str3 = str;
        ActionBarMenuItem actionBarMenuItem = r0;
        FrameLayout.LayoutParams layoutParams2 = layoutParams;
        ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, chatAttachAlertLocationLayout.getThemedColor("location_actionIcon"), resourcesProvider);
        chatAttachAlertLocationLayout.mapTypeButton = actionBarMenuItem;
        actionBarMenuItem.setClickable(true);
        chatAttachAlertLocationLayout.mapTypeButton.setSubMenuOpenSide(2);
        chatAttachAlertLocationLayout.mapTypeButton.setAdditionalXOffset(AndroidUtilities.dp(10.0f));
        chatAttachAlertLocationLayout.mapTypeButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        chatAttachAlertLocationLayout.mapTypeButton.addSubItem(2, NUM, (CharSequence) LocaleController.getString("Map", NUM), resourcesProvider2);
        chatAttachAlertLocationLayout.mapTypeButton.addSubItem(3, NUM, (CharSequence) LocaleController.getString("Satellite", NUM), resourcesProvider2);
        chatAttachAlertLocationLayout.mapTypeButton.addSubItem(4, NUM, (CharSequence) LocaleController.getString("Hybrid", NUM), resourcesProvider2);
        chatAttachAlertLocationLayout.mapTypeButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        Drawable drawable4 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), chatAttachAlertLocationLayout.getThemedColor(str3), chatAttachAlertLocationLayout.getThemedColor(str2));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable2 = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(shadowDrawable2, drawable4, 0, 0);
            combinedDrawable2.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            drawable4 = combinedDrawable2;
        } else {
            StateListAnimator animator2 = new StateListAnimator();
            animator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.mapTypeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            chatAttachAlertLocationLayout = this;
            animator2.addState(new int[0], ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.mapTypeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            chatAttachAlertLocationLayout.mapTypeButton.setStateListAnimator(animator2);
            chatAttachAlertLocationLayout.mapTypeButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
        }
        chatAttachAlertLocationLayout.mapTypeButton.setBackgroundDrawable(drawable4);
        chatAttachAlertLocationLayout.mapTypeButton.setIcon(NUM);
        chatAttachAlertLocationLayout.mapViewClip.addView(chatAttachAlertLocationLayout.mapTypeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        chatAttachAlertLocationLayout.mapTypeButton.setOnClickListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda20(chatAttachAlertLocationLayout));
        chatAttachAlertLocationLayout.mapTypeButton.setDelegate(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda10(chatAttachAlertLocationLayout));
        chatAttachAlertLocationLayout.locationButton = new ImageView(context2);
        Drawable drawable5 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), chatAttachAlertLocationLayout.getThemedColor(str3), chatAttachAlertLocationLayout.getThemedColor(str2));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable3 = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(shadowDrawable3, drawable5, 0, 0);
            combinedDrawable3.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            drawable2 = combinedDrawable3;
        } else {
            StateListAnimator animator3 = new StateListAnimator();
            animator3.addState(new int[]{16842919}, ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator3.addState(new int[0], ObjectAnimator.ofFloat(chatAttachAlertLocationLayout.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            chatAttachAlertLocationLayout.locationButton.setStateListAnimator(animator3);
            chatAttachAlertLocationLayout.locationButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                }
            });
            drawable2 = drawable5;
        }
        chatAttachAlertLocationLayout.locationButton.setBackgroundDrawable(drawable2);
        chatAttachAlertLocationLayout.locationButton.setImageResource(NUM);
        chatAttachAlertLocationLayout.locationButton.setScaleType(ImageView.ScaleType.CENTER);
        chatAttachAlertLocationLayout.locationButton.setColorFilter(new PorterDuffColorFilter(chatAttachAlertLocationLayout.getThemedColor("location_actionActiveIcon"), PorterDuff.Mode.MULTIPLY));
        chatAttachAlertLocationLayout.locationButton.setTag("location_actionActiveIcon");
        chatAttachAlertLocationLayout.locationButton.setContentDescription(LocaleController.getString("AccDescrMyLocation", NUM));
        chatAttachAlertLocationLayout.mapViewClip.addView(chatAttachAlertLocationLayout.locationButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f));
        chatAttachAlertLocationLayout.locationButton.setOnClickListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda21(chatAttachAlertLocationLayout));
        LinearLayout linearLayout = new LinearLayout(context2);
        chatAttachAlertLocationLayout.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        chatAttachAlertLocationLayout.emptyView.setGravity(1);
        chatAttachAlertLocationLayout.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        chatAttachAlertLocationLayout.emptyView.setVisibility(8);
        chatAttachAlertLocationLayout.addView(chatAttachAlertLocationLayout.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        chatAttachAlertLocationLayout.emptyView.setOnTouchListener(ChatAttachAlertLocationLayout$$ExternalSyntheticLambda22.INSTANCE);
        ImageView imageView = new ImageView(context2);
        chatAttachAlertLocationLayout.emptyImageView = imageView;
        imageView.setImageResource(NUM);
        chatAttachAlertLocationLayout.emptyImageView.setColorFilter(new PorterDuffColorFilter(chatAttachAlertLocationLayout.getThemedColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        chatAttachAlertLocationLayout.emptyView.addView(chatAttachAlertLocationLayout.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context2);
        chatAttachAlertLocationLayout.emptyTitleTextView = textView;
        textView.setTextColor(chatAttachAlertLocationLayout.getThemedColor("dialogEmptyText"));
        chatAttachAlertLocationLayout.emptyTitleTextView.setGravity(17);
        chatAttachAlertLocationLayout.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        chatAttachAlertLocationLayout.emptyTitleTextView.setTextSize(1, 17.0f);
        chatAttachAlertLocationLayout.emptyTitleTextView.setText(LocaleController.getString("NoPlacesFound", NUM));
        chatAttachAlertLocationLayout.emptyView.addView(chatAttachAlertLocationLayout.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context2);
        chatAttachAlertLocationLayout.emptySubtitleTextView = textView2;
        textView2.setTextColor(chatAttachAlertLocationLayout.getThemedColor("dialogEmptyText"));
        chatAttachAlertLocationLayout.emptySubtitleTextView.setGravity(17);
        chatAttachAlertLocationLayout.emptySubtitleTextView.setTextSize(1, 15.0f);
        chatAttachAlertLocationLayout.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        chatAttachAlertLocationLayout.emptyView.addView(chatAttachAlertLocationLayout.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        AnonymousClass6 r02 = new RecyclerListView(context2, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                ChatAttachAlertLocationLayout.this.updateClipView();
            }
        };
        chatAttachAlertLocationLayout.listView = r02;
        r02.setClipToPadding(false);
        RecyclerListView recyclerListView = chatAttachAlertLocationLayout.listView;
        Drawable drawable6 = drawable2;
        LocationActivityAdapter locationActivityAdapter2 = new LocationActivityAdapter(context, chatAttachAlertLocationLayout.locationType, chatAttachAlertLocationLayout.dialogId, true, resourcesProvider);
        chatAttachAlertLocationLayout.adapter = locationActivityAdapter2;
        recyclerListView.setAdapter(locationActivityAdapter2);
        chatAttachAlertLocationLayout.adapter.setUpdateRunnable(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda6(chatAttachAlertLocationLayout));
        chatAttachAlertLocationLayout.adapter.setMyLocationDenied(chatAttachAlertLocationLayout.locationDenied);
        chatAttachAlertLocationLayout.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = chatAttachAlertLocationLayout.listView;
        AnonymousClass7 r03 = new FillLastLinearLayoutManager(context, 1, false, 0, chatAttachAlertLocationLayout.listView) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        return super.calculateDyToMakeVisible(view, snapPreference) - (ChatAttachAlertLocationLayout.this.listView.getPaddingTop() - (ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 4;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        chatAttachAlertLocationLayout.layoutManager = r03;
        recyclerListView2.setLayoutManager(r03);
        chatAttachAlertLocationLayout.addView(chatAttachAlertLocationLayout.listView, LayoutHelper.createFrame(-1, -1, 51));
        chatAttachAlertLocationLayout.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerListView.Holder holder;
                boolean unused = ChatAttachAlertLocationLayout.this.scrolling = newState != 0;
                if (!ChatAttachAlertLocationLayout.this.scrolling && ChatAttachAlertLocationLayout.this.forceUpdate != null) {
                    CameraUpdate unused2 = ChatAttachAlertLocationLayout.this.forceUpdate = null;
                }
                if (newState == 0) {
                    int offset = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertLocationLayout.this.parentAlert.getBackgroundPaddingTop();
                    if (((ChatAttachAlertLocationLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - offset) + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertLocationLayout.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight) {
                        ChatAttachAlertLocationLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - (ChatAttachAlertLocationLayout.this.mapHeight - ChatAttachAlertLocationLayout.this.overScrollHeight));
                    }
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatAttachAlertLocationLayout.this.updateClipView();
                if (ChatAttachAlertLocationLayout.this.forceUpdate != null) {
                    ChatAttachAlertLocationLayout.access$2816(ChatAttachAlertLocationLayout.this, (float) dy);
                }
                ChatAttachAlertLocationLayout.this.parentAlert.updateLayout(ChatAttachAlertLocationLayout.this, true, dy);
            }
        });
        chatAttachAlertLocationLayout.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda19(chatAttachAlertLocationLayout, chatActivity, resourcesProvider2));
        chatAttachAlertLocationLayout.adapter.setDelegate(chatAttachAlertLocationLayout.dialogId, new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda14(chatAttachAlertLocationLayout));
        chatAttachAlertLocationLayout.adapter.setOverScrollHeight(chatAttachAlertLocationLayout.overScrollHeight);
        chatAttachAlertLocationLayout.addView(chatAttachAlertLocationLayout.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        chatAttachAlertLocationLayout.mapView = new MapView(context2) {
            public boolean dispatchTouchEvent(MotionEvent ev) {
                MotionEvent eventToRecycle = null;
                if (ChatAttachAlertLocationLayout.this.yOffset != 0.0f) {
                    MotionEvent obtain = MotionEvent.obtain(ev);
                    eventToRecycle = obtain;
                    ev = obtain;
                    eventToRecycle.offsetLocation(0.0f, (-ChatAttachAlertLocationLayout.this.yOffset) / 2.0f);
                }
                boolean result = super.dispatchTouchEvent(ev);
                if (eventToRecycle != null) {
                    eventToRecycle.recycle();
                }
                return result;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0) {
                    if (ChatAttachAlertLocationLayout.this.animatorSet != null) {
                        ChatAttachAlertLocationLayout.this.animatorSet.cancel();
                    }
                    AnimatorSet unused = ChatAttachAlertLocationLayout.this.animatorSet = new AnimatorSet();
                    ChatAttachAlertLocationLayout.this.animatorSet.setDuration(200);
                    ChatAttachAlertLocationLayout.this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlertLocationLayout.this.markerImageView, View.TRANSLATION_Y, new float[]{(float) (ChatAttachAlertLocationLayout.this.markerTop - AndroidUtilities.dp(10.0f))})});
                    ChatAttachAlertLocationLayout.this.animatorSet.start();
                } else if (ev.getAction() == 1) {
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
                if (ev.getAction() == 2) {
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
                return super.onInterceptTouchEvent(ev);
            }
        };
        new Thread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda8(chatAttachAlertLocationLayout, chatAttachAlertLocationLayout.mapView)).start();
        ImageView imageView2 = new ImageView(context2);
        chatAttachAlertLocationLayout.markerImageView = imageView2;
        imageView2.setImageResource(NUM);
        chatAttachAlertLocationLayout.mapViewClip.addView(chatAttachAlertLocationLayout.markerImageView, LayoutHelper.createFrame(28, 48, 49));
        RecyclerListView recyclerListView3 = new RecyclerListView(context2, resourcesProvider2);
        chatAttachAlertLocationLayout.searchListView = recyclerListView3;
        recyclerListView3.setVisibility(8);
        chatAttachAlertLocationLayout.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        AnonymousClass10 r2 = new LocationActivitySearchAdapter(context2) {
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
        chatAttachAlertLocationLayout.searchAdapter = r2;
        r2.setDelegate(0, new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda13(chatAttachAlertLocationLayout));
        chatAttachAlertLocationLayout.searchListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        chatAttachAlertLocationLayout.addView(chatAttachAlertLocationLayout.searchListView, LayoutHelper.createFrame(-1, -1, 51));
        chatAttachAlertLocationLayout.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && ChatAttachAlertLocationLayout.this.searching && ChatAttachAlertLocationLayout.this.searchWas) {
                    AndroidUtilities.hideKeyboard(ChatAttachAlertLocationLayout.this.parentAlert.getCurrentFocus());
                }
            }
        });
        chatAttachAlertLocationLayout.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda18(chatAttachAlertLocationLayout, chatActivity, resourcesProvider2));
        updateEmptyView();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m800x293a5bd2(View v) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery((String) null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m801xb62772f1(View v) {
        this.mapTypeButton.toggleSubMenu();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m808x43148a10(int id) {
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

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m809xd001a12f(View v) {
        Activity activity;
        if (Build.VERSION.SDK_INT < 23 || (activity = getParentActivity()) == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
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

    static /* synthetic */ boolean lambda$new$4(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m812x3b5fdab(ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view, int position) {
        if (position == 1) {
            if (this.delegate != null && this.userLocation != null) {
                FrameLayout frameLayout = this.lastPressedMarkerView;
                if (frameLayout != null) {
                    frameLayout.callOnClick();
                    return;
                }
                TLRPC.TL_messageMediaGeo location = new TLRPC.TL_messageMediaGeo();
                location.geo = new TLRPC.TL_geoPoint();
                location.geo.lat = AndroidUtilities.fixLocationCoord(this.userLocation.getLatitude());
                location.geo._long = AndroidUtilities.fixLocationCoord(this.userLocation.getLongitude());
                if (chatActivity.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog((Context) getParentActivity(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda16(this, location), resourcesProvider);
                    return;
                }
                this.delegate.didSelectLocation(location, this.locationType, true, 0);
                this.parentAlert.dismiss(true);
            } else if (this.locationDenied) {
                AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
            }
        } else if (position != 2 || this.locationType != 1) {
            Object object = this.adapter.getItem(position);
            if (object instanceof TLRPC.TL_messageMediaVenue) {
                if (chatActivity.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog((Context) getParentActivity(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda15(this, object), resourcesProvider);
                    return;
                }
                this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, true, 0);
                this.parentAlert.dismiss(true);
            } else if (object instanceof LiveLocation) {
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(((LiveLocation) object).marker.getPosition(), this.googleMap.getMaxZoomLevel() - 4.0f));
            }
        } else if (getLocationController().isSharingLocation(this.dialogId)) {
            getLocationController().removeSharingLocation(this.dialogId);
            this.parentAlert.dismiss(true);
        } else if (this.myLocation != null || !this.locationDenied) {
            openShareLiveLocation();
        } else {
            AlertsCreator.createLocationRequiredDialog(getParentActivity(), true).show();
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m810xe9dbcf6d(TLRPC.TL_messageMediaGeo location, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(location, this.locationType, notify, scheduleDate);
        this.parentAlert.dismiss(true);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m811x76c8e68c(Object object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation((TLRPC.TL_messageMediaVenue) object, this.locationType, notify, scheduleDate);
        this.parentAlert.dismiss(true);
    }

    /* renamed from: lambda$new$12$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m804xc8a66f1f(MapView map) {
        try {
            map.onCreate((Bundle) null);
        } catch (Exception e) {
        }
        AndroidUtilities.runOnUIThread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda7(this, map));
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m803x3bb95800(MapView map) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                map.onCreate((Bundle) null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda1(this));
                this.mapsInitialized = true;
                if (this.onResumeCalled) {
                    this.mapView.onResume();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* renamed from: lambda$new$10$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m802xaecCLASSNAMEe1(GoogleMap map1) {
        this.googleMap = map1;
        map1.setOnMapLoadedCallback(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda25(this));
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, NUM));
        }
        onMapInit();
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m814x1d902be9() {
        AndroidUtilities.runOnUIThread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda2(this));
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m813x90a314ca() {
        this.loadingMapView.setTag(1);
        this.loadingMapView.animate().alpha(0.0f).setDuration(180).start();
    }

    /* renamed from: lambda$new$13$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m805x5593863e(ArrayList places) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    /* renamed from: lambda$new$15$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m807x6f6db47c(ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view, int position) {
        TLRPC.TL_messageMediaVenue object = this.searchAdapter.getItem(position);
        if (object != null && this.delegate != null) {
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog((Context) getParentActivity(), chatActivity.getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda17(this, object), resourcesProvider);
                return;
            }
            this.delegate.didSelectLocation(object, this.locationType, true, 0);
            this.parentAlert.dismiss(true);
        }
    }

    /* renamed from: lambda$new$14$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m806xe2809d5d(TLRPC.TL_messageMediaVenue object, boolean notify, int scheduleDate) {
        this.delegate.didSelectLocation(object, this.locationType, notify, scheduleDate);
        this.parentAlert.dismiss(true);
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
    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionDenied);
        try {
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                googleMap2.setMyLocationEnabled(false);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        MapView mapView2 = this.mapView;
        if (mapView2 != null) {
            mapView2.setTranslationY((float) ((-AndroidUtilities.displaySize.y) * 3));
        }
        try {
            MapView mapView3 = this.mapView;
            if (mapView3 != null) {
                mapView3.onPause();
            }
        } catch (Exception e2) {
        }
        try {
            MapView mapView4 = this.mapView;
            if (mapView4 != null) {
                mapView4.onDestroy();
                this.mapView = null;
            }
        } catch (Exception e3) {
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

    /* access modifiers changed from: package-private */
    public void onHide() {
        this.searchItem.setVisibility(8);
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
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
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(0);
        int newOffset = 0;
        if (holder != null) {
            newOffset = Math.max(((int) holder.itemView.getY()) - this.nonClipSize, 0);
        }
        return AndroidUtilities.dp(56.0f) + newOffset;
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
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
    public void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        int padding2;
        if (this.parentAlert.actionBar.isSearchFieldVisible() || this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = this.mapHeight - this.overScrollHeight;
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
                padding2 = (availableHeight / 5) * 2;
            } else {
                padding2 = (int) (((float) availableHeight) / 3.5f);
            }
            padding = padding2 - AndroidUtilities.dp(52.0f);
            if (padding < 0) {
                padding = 0;
            }
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != padding) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, padding, 0, 0);
            this.ignoreLayout = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
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
            this.searchAreaButton.setVisibility(show ? 0 : 4);
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

    public void openShareLiveLocation() {
        Activity activity;
        if (this.delegate != null && getParentActivity() != null && this.myLocation != null) {
            if (this.checkBackgroundPermission && Build.VERSION.SDK_INT >= 29 && (activity = getParentActivity()) != null) {
                this.checkBackgroundPermission = false;
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (Math.abs((System.currentTimeMillis() / 1000) - ((long) preferences.getInt("backgroundloc", 0))) > 86400 && activity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
                    preferences.edit().putInt("backgroundloc", (int) (System.currentTimeMillis() / 1000)).commit();
                    AlertsCreator.createBackgroundLocationPermissionDialog(activity, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda5(this), this.resourcesProvider).show();
                    return;
                }
            }
            TLRPC.User user = null;
            if (DialogObject.isUserDialog(this.dialogId)) {
                user = this.parentAlert.baseFragment.getMessagesController().getUser(Long.valueOf(this.dialogId));
            }
            AlertsCreator.createLocationUpdateDialog(getParentActivity(), user, new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda9(this), this.resourcesProvider).show();
        }
    }

    /* renamed from: lambda$openShareLiveLocation$16$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m822xvar_(int param) {
        TLRPC.TL_messageMediaGeoLive location = new TLRPC.TL_messageMediaGeoLive();
        location.geo = new TLRPC.TL_geoPoint();
        location.geo.lat = AndroidUtilities.fixLocationCoord(this.myLocation.getLatitude());
        location.geo._long = AndroidUtilities.fixLocationCoord(this.myLocation.getLongitude());
        location.period = param;
        this.delegate.didSelectLocation(location, this.locationType, true, 0);
        this.parentAlert.dismiss(true);
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
        if (this.parentAlert == null || this.parentAlert.baseFragment == null) {
            return null;
        }
        return this.parentAlert.baseFragment.getParentActivity();
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
            this.googleMap.setOnCameraMoveStartedListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda24(this));
            this.googleMap.setOnMyLocationChangeListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda27(this));
            this.googleMap.setOnMarkerClickListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda26(this));
            this.googleMap.setOnCameraMoveListener(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda23(this));
            AndroidUtilities.runOnUIThread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda3(this), 200);
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

    /* renamed from: lambda$onMapInit$17$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m815xeba4ab07(int reason) {
        View view;
        RecyclerView.ViewHolder holder;
        if (reason == 1) {
            showSearchPlacesButton(true);
            removeInfoView();
            if (!this.scrolling && this.listView.getChildCount() > 0 && (view = this.listView.getChildAt(0)) != null && (holder = this.listView.findContainingViewHolder(view)) != null && holder.getAdapterPosition() == 0) {
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

    /* renamed from: lambda$onMapInit$18$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m816x7891CLASSNAME(Location location) {
        if (this.parentAlert != null && this.parentAlert.baseFragment != null) {
            positionMarker(location);
            getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
            this.isFirstLocation = false;
        }
    }

    /* renamed from: lambda$onMapInit$19$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ boolean m817x57ed945(Marker marker) {
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

    /* renamed from: lambda$onMapInit$20$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m818x21ded5ef() {
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null) {
            mapOverlayView.updatePositions();
        }
    }

    /* renamed from: lambda$onMapInit$21$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m819xaecbed0e() {
        if (this.loadingMapView.getTag() == null) {
            this.loadingMapView.animate().alpha(0.0f).setDuration(180).start();
        }
    }

    /* renamed from: lambda$onMapInit$22$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m820x3bb9042d(DialogInterface dialog, int id) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception e) {
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
            int offset = AndroidUtilities.dp(258.0f) + this.listView.getChildAt(0).getTop();
            if (offset >= 0 && offset <= AndroidUtilities.dp(258.0f)) {
                this.listView.smoothScrollBy(0, offset);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateClipView() {
        int height;
        int top;
        LatLng location;
        if (this.mapView != null && this.mapViewClip != null) {
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(0);
            if (holder != null) {
                top = (int) holder.itemView.getY();
                height = this.overScrollHeight + Math.min(top, 0);
            } else {
                top = -this.mapViewClip.getMeasuredHeight();
                height = 0;
            }
            if (((FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams()) == null) {
                return;
            }
            if (height <= 0) {
                if (this.mapView.getVisibility() == 0) {
                    this.mapView.setVisibility(4);
                    this.mapViewClip.setVisibility(4);
                    MapOverlayView mapOverlayView = this.overlayView;
                    if (mapOverlayView != null) {
                        mapOverlayView.setVisibility(4);
                    }
                }
                this.mapView.setTranslationY((float) top);
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
            int trY = Math.max(0, (-((top - this.mapHeight) + this.overScrollHeight)) / 2);
            int maxClipSize = this.mapHeight - this.overScrollHeight;
            float moveProgress = 1.0f - Math.max(0.0f, Math.min(1.0f, ((float) (this.listView.getPaddingTop() - top)) / ((float) (this.listView.getPaddingTop() - maxClipSize))));
            int prevClipSize = this.clipSize;
            if (this.locationDenied && isTypeSend()) {
                maxClipSize += Math.min(top, this.listView.getPaddingTop());
            }
            this.clipSize = (int) (((float) maxClipSize) * moveProgress);
            this.mapView.setTranslationY((float) trY);
            this.nonClipSize = maxClipSize - this.clipSize;
            this.mapViewClip.invalidate();
            this.mapViewClip.setTranslationY((float) (top - this.nonClipSize));
            GoogleMap googleMap2 = this.googleMap;
            if (googleMap2 != null) {
                googleMap2.setPadding(0, AndroidUtilities.dp(6.0f), 0, this.clipSize + AndroidUtilities.dp(6.0f));
            }
            MapOverlayView mapOverlayView3 = this.overlayView;
            if (mapOverlayView3 != null) {
                mapOverlayView3.setTranslationY((float) trY);
            }
            float translationY = (float) Math.min(Math.max(this.nonClipSize - top, 0), (this.mapHeight - this.mapTypeButton.getMeasuredHeight()) - AndroidUtilities.dp(80.0f));
            this.mapTypeButton.setTranslationY(translationY);
            this.searchAreaButton.setTranslation(translationY);
            this.locationButton.setTranslationY((float) (-this.clipSize));
            ImageView imageView = this.markerImageView;
            int dp = (((this.mapHeight - this.clipSize) / 2) - AndroidUtilities.dp(48.0f)) + trY;
            this.markerTop = dp;
            imageView.setTranslationY((float) dp);
            if (prevClipSize != this.clipSize) {
                Marker marker = this.lastPressedMarker;
                if (marker != null) {
                    location = marker.getPosition();
                } else if (this.userLocationMoved) {
                    location = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                } else if (this.myLocation != null) {
                    location = new LatLng(this.myLocation.getLatitude(), this.myLocation.getLongitude());
                } else {
                    location = null;
                }
                if (location != null) {
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                }
            }
            if (this.locationDenied && isTypeSend()) {
                int count = this.adapter.getItemCount();
                for (int i = 1; i < count; i++) {
                    RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(i);
                    if (holder2 != null) {
                        holder2.itemView.setTranslationY((float) (this.listView.getPaddingTop() - top));
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
        int buttonsHeight = AndroidUtilities.dp(66.0f);
        if (this.locationType == 1) {
            return buttonsHeight + AndroidUtilities.dp(66.0f);
        }
        return buttonsHeight;
    }

    private void fixLayoutInternal(boolean resume) {
        FrameLayout.LayoutParams layoutParams;
        if (getMeasuredHeight() != 0 && this.mapView != null) {
            int height = ActionBar.getCurrentActionBarHeight();
            int maxMapHeight = ((AndroidUtilities.displaySize.y - height) - buttonsHeight()) - AndroidUtilities.dp(90.0f);
            int dp = AndroidUtilities.dp(189.0f);
            this.overScrollHeight = dp;
            this.mapHeight = Math.max(dp, (!this.locationDenied || !isTypeSend()) ? Math.min(AndroidUtilities.dp(310.0f), maxMapHeight) : maxMapHeight);
            if (this.locationDenied && isTypeSend()) {
                this.overScrollHeight = this.mapHeight;
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            layoutParams2.topMargin = height;
            this.listView.setLayoutParams(layoutParams2);
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams();
            layoutParams3.topMargin = height;
            layoutParams3.height = this.mapHeight;
            this.mapViewClip.setLayoutParams(layoutParams3);
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.searchListView.getLayoutParams();
            layoutParams4.topMargin = height;
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

    public void didReceivedNotification(int id, int account, Object... args) {
        int i = 0;
        if (id == NotificationCenter.locationPermissionGranted) {
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
        } else if (id == NotificationCenter.locationPermissionDenied) {
            this.locationDenied = true;
            LocationActivityAdapter locationActivityAdapter2 = this.adapter;
            if (locationActivityAdapter2 != null) {
                locationActivityAdapter2.setMyLocationDenied(true);
            }
        }
        fixLayoutInternal(true);
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (this.locationDenied) {
            i = 8;
        }
        actionBarMenuItem.setVisibility(i);
    }

    public void onResume() {
        MapView mapView2 = this.mapView;
        if (mapView2 != null && this.mapsInitialized) {
            try {
                mapView2.onResume();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = true;
    }

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
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
        AndroidUtilities.runOnUIThread(new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda4(this), this.parentAlert.delegate.needEnterComment() ? 200 : 0);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        updateClipView();
    }

    /* renamed from: lambda$onShow$23$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m821x500ca7b5() {
        Activity activity;
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            }
        }
    }

    public void setDelegate(LocationActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ChatAttachAlertLocationLayout$$ExternalSyntheticLambda12(this);
        themeDescriptions.add(new ThemeDescription(this.mapViewClip, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptions.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        themeDescriptions.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        themeDescriptions.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionIcon"));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon"));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        themeDescriptions.add(new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "location_actionIcon"));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        themeDescriptions.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        themeDescriptions.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionActiveIcon"));
        themeDescriptions.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        themeDescriptions.add(new ThemeDescription(this.searchAreaButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, themeDescriptionDelegate, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundRed"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundPink"));
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

    /* renamed from: lambda$getThemeDescriptions$24$org-telegram-ui-Components-ChatAttachAlertLocationLayout  reason: not valid java name */
    public /* synthetic */ void m799x10876942() {
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
