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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
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

public class ChatAttachAlertLocationLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public LocationActivityAdapter adapter;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private Bitmap[] bitmapCache = new Bitmap[7];
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
    /* access modifiers changed from: private */
    public LinearLayout emptyView;
    private boolean first = true;
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
    /* access modifiers changed from: private */
    public int locationType;
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
    private boolean onResumeCalled;
    /* access modifiers changed from: private */
    public ActionBarMenuItem otherItem;
    private int overScrollHeight = ((AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f));
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

    static /* synthetic */ boolean lambda$new$4(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
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
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
                frameLayout.addView(ChatAttachAlertLocationLayout.this.lastPressedMarkerView, LayoutHelper.createFrame(-2, 71.0f));
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setAlpha(0.0f);
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.setOnClickListener(new View.OnClickListener(venueLocation) {
                    private final /* synthetic */ ChatAttachAlertLocationLayout.VenueLocation f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        ChatAttachAlertLocationLayout.MapOverlayView.this.lambda$addInfoView$1$ChatAttachAlertLocationLayout$MapOverlayView(this.f$1, view);
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
                ChatAttachAlertLocationLayout.this.lastPressedMarkerView.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 10.0f, 18.0f, 0.0f));
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 14.0f);
                textView2.setMaxLines(1);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                textView2.setSingleLine(true);
                textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
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

        public /* synthetic */ void lambda$addInfoView$1$ChatAttachAlertLocationLayout$MapOverlayView(VenueLocation venueLocation, View view) {
            ChatActivity chatActivity = (ChatActivity) ChatAttachAlertLocationLayout.this.parentAlert.baseFragment;
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(ChatAttachAlertLocationLayout.this.getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(venueLocation) {
                    private final /* synthetic */ ChatAttachAlertLocationLayout.VenueLocation f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertLocationLayout.MapOverlayView.this.lambda$null$0$ChatAttachAlertLocationLayout$MapOverlayView(this.f$1, z, i);
                    }
                });
                return;
            }
            ChatAttachAlertLocationLayout.this.delegate.didSelectLocation(venueLocation.venue, ChatAttachAlertLocationLayout.this.locationType, true, 0);
            ChatAttachAlertLocationLayout.this.parentAlert.dismiss();
        }

        public /* synthetic */ void lambda$null$0$ChatAttachAlertLocationLayout$MapOverlayView(VenueLocation venueLocation, boolean z, int i) {
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
    public ChatAttachAlertLocationLayout(ChatAttachAlert chatAttachAlert, Context context) {
        super(chatAttachAlert, context);
        Context context2 = context;
        ChatActivity chatActivity = (ChatActivity) this.parentAlert.baseFragment;
        this.dialogId = chatActivity.getDialogId();
        if (chatActivity.getCurrentEncryptedChat() != null || chatActivity.isInScheduleMode() || UserObject.isUserSelf(chatActivity.getCurrentUser())) {
            this.locationType = 0;
        } else {
            this.locationType = 1;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
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
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        this.overlayView = new MapOverlayView(context2);
        ActionBarMenuItem addItem = createMenu.addItem(0, NUM);
        addItem.setIsSearchField(true);
        addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = ChatAttachAlertLocationLayout.this.searching = true;
                ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = ChatAttachAlertLocationLayout.this;
                chatAttachAlertLocationLayout.parentAlert.makeFocusable(chatAttachAlertLocationLayout.searchItem.getSearchField());
            }

            public void onSearchCollapse() {
                boolean unused = ChatAttachAlertLocationLayout.this.searching = false;
                boolean unused2 = ChatAttachAlertLocationLayout.this.searchWas = false;
                ChatAttachAlertLocationLayout.this.searchAdapter.searchDelayed((String) null, (Location) null);
                ChatAttachAlertLocationLayout.this.updateEmptyView();
            }

            public void onTextChanged(EditText editText) {
                if (ChatAttachAlertLocationLayout.this.searchAdapter != null) {
                    String obj = editText.getText().toString();
                    boolean z = false;
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
                        if (chatAttachAlertLocationLayout.searchAdapter.getItemCount() == 0) {
                            z = true;
                        }
                        boolean unused2 = chatAttachAlertLocationLayout.searchInProgress = z;
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
        this.searchItem = addItem;
        addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor("dialogTextBlack"));
        searchField.setCursorColor(Theme.getColor("dialogTextBlack"));
        searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(21.0f)).gravity = 83;
        AnonymousClass2 r0 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (ChatAttachAlertLocationLayout.this.overlayView != null) {
                    ChatAttachAlertLocationLayout.this.overlayView.updatePositions();
                }
            }
        };
        this.mapViewClip = r0;
        r0.setBackgroundColor(Theme.getColor("dialogBackground"));
        View view = new View(context2);
        this.loadingMapView = view;
        view.setBackgroundDrawable(new MapPlaceholderDrawable());
        SearchButton searchButton = new SearchButton(context2);
        this.searchAreaButton = searchButton;
        searchButton.setTranslationX((float) (-AndroidUtilities.dp(80.0f)));
        this.searchAreaButton.setVisibility(4);
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorRoundRectDrawable, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            combinedDrawable.setFullsize(true);
            createSimpleSelectorRoundRectDrawable = combinedDrawable;
        } else {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.searchAreaButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.searchAreaButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
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
        this.mapViewClip.addView(this.searchAreaButton, LayoutHelper.createFrame(-2, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 49, 80.0f, 12.0f, 80.0f, 0.0f));
        this.searchAreaButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatAttachAlertLocationLayout.this.lambda$new$0$ChatAttachAlertLocationLayout(view);
            }
        });
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
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
            mutate2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(mutate2, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable2.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable2;
        } else {
            StateListAnimator stateListAnimator2 = new StateListAnimator();
            stateListAnimator2.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.mapTypeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator2.addState(new int[0], ObjectAnimator.ofFloat(this.mapTypeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
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
        this.mapViewClip.addView(this.mapTypeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
        this.mapTypeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatAttachAlertLocationLayout.this.lambda$new$1$ChatAttachAlertLocationLayout(view);
            }
        });
        this.mapTypeButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
            public final void onItemClick(int i) {
                ChatAttachAlertLocationLayout.this.lambda$new$2$ChatAttachAlertLocationLayout(i);
            }
        });
        this.locationButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor("location_actionBackground"), Theme.getColor("location_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate3 = context.getResources().getDrawable(NUM).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable3 = new CombinedDrawable(mutate3, createSimpleSelectorCircleDrawable2, 0, 0);
            combinedDrawable3.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            createSimpleSelectorCircleDrawable2 = combinedDrawable3;
        } else {
            StateListAnimator stateListAnimator3 = new StateListAnimator();
            stateListAnimator3.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator3.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
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
        this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40.0f : 44.0f, 85, 0.0f, 0.0f, 12.0f, 12.0f));
        this.locationButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatAttachAlertLocationLayout.this.lambda$new$3$ChatAttachAlertLocationLayout(view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(1);
        this.emptyView.setPadding(0, AndroidUtilities.dp(160.0f), 0, 0);
        this.emptyView.setVisibility(8);
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener($$Lambda$ChatAttachAlertLocationLayout$qGhsN4kabObw58Cg6o2Z4HtUjc.INSTANCE);
        ImageView imageView = new ImageView(context2);
        this.emptyImageView = imageView;
        imageView.setImageResource(NUM);
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
        AnonymousClass6 r02 = new RecyclerListView(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                ChatAttachAlertLocationLayout.this.updateClipView();
            }
        };
        this.listView = r02;
        r02.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        LocationActivityAdapter locationActivityAdapter2 = new LocationActivityAdapter(context, this.locationType, this.dialogId, true);
        this.adapter = locationActivityAdapter2;
        recyclerListView.setAdapter(locationActivityAdapter2);
        this.adapter.setUpdateRunnable(new Runnable() {
            public final void run() {
                ChatAttachAlertLocationLayout.this.updateClipView();
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass7 r03 = new FillLastLinearLayoutManager(context, 1, false, 0, recyclerListView2) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int i) {
                        return super.calculateDyToMakeVisible(view, i) - ChatAttachAlertLocationLayout.this.listView.getPaddingTop();
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int i) {
                        return super.calculateTimeForDeceleration(i) * 2;
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
                    if (((ChatAttachAlertLocationLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - dp) + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertLocationLayout.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > 0) {
                        ChatAttachAlertLocationLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop());
                    }
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatAttachAlertLocationLayout.this.updateClipView();
                if (ChatAttachAlertLocationLayout.this.forceUpdate != null) {
                    ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = ChatAttachAlertLocationLayout.this;
                    float unused = chatAttachAlertLocationLayout.yOffset = chatAttachAlertLocationLayout.yOffset + ((float) i2);
                }
                ChatAttachAlertLocationLayout chatAttachAlertLocationLayout2 = ChatAttachAlertLocationLayout.this;
                chatAttachAlertLocationLayout2.parentAlert.updateLayout(chatAttachAlertLocationLayout2, true);
            }
        });
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(chatActivity) {
            private final /* synthetic */ ChatActivity f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                ChatAttachAlertLocationLayout.this.lambda$new$7$ChatAttachAlertLocationLayout(this.f$1, view, i);
            }
        });
        this.adapter.setDelegate(this.dialogId, new BaseLocationAdapter.BaseLocationAdapterDelegate() {
            public final void didLoadSearchResult(ArrayList arrayList) {
                ChatAttachAlertLocationLayout.this.updatePlacesMarkers(arrayList);
            }
        });
        this.adapter.setOverScrollHeight(this.overScrollHeight);
        addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
        AnonymousClass9 r04 = new MapView(context2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return super.onTouchEvent(motionEvent);
            }

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
                        ChatAttachAlertLocationLayout.this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_actionIcon"), PorterDuff.Mode.MULTIPLY));
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
        new Thread(new Runnable(r04) {
            private final /* synthetic */ MapView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChatAttachAlertLocationLayout.this.lambda$new$12$ChatAttachAlertLocationLayout(this.f$1);
            }
        }).start();
        ImageView imageView2 = new ImageView(context2);
        this.markerImageView = imageView2;
        imageView2.setImageResource(NUM);
        this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(28, 48, 49));
        RecyclerListView recyclerListView3 = new RecyclerListView(context2);
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
        r05.setDelegate(0, new BaseLocationAdapter.BaseLocationAdapterDelegate() {
            public final void didLoadSearchResult(ArrayList arrayList) {
                ChatAttachAlertLocationLayout.this.lambda$new$13$ChatAttachAlertLocationLayout(arrayList);
            }
        });
        addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && ChatAttachAlertLocationLayout.this.searching && ChatAttachAlertLocationLayout.this.searchWas) {
                    AndroidUtilities.hideKeyboard(ChatAttachAlertLocationLayout.this.parentAlert.getCurrentFocus());
                }
            }
        });
        this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(chatActivity) {
            private final /* synthetic */ ChatActivity f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                ChatAttachAlertLocationLayout.this.lambda$new$15$ChatAttachAlertLocationLayout(this.f$1, view, i);
            }
        });
        updateEmptyView();
    }

    public /* synthetic */ void lambda$new$0$ChatAttachAlertLocationLayout(View view) {
        showSearchPlacesButton(false);
        this.adapter.searchPlacesWithQuery((String) null, this.userLocation, true, true);
        this.searchedForCustomLocations = true;
        showResults();
    }

    public /* synthetic */ void lambda$new$1$ChatAttachAlertLocationLayout(View view) {
        this.mapTypeButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$2$ChatAttachAlertLocationLayout(int i) {
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

    public /* synthetic */ void lambda$new$3$ChatAttachAlertLocationLayout(View view) {
        Activity parentActivity;
        if (Build.VERSION.SDK_INT < 23 || (parentActivity = getParentActivity()) == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
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
            removeInfoView();
            return;
        }
        showPermissionAlert(false);
    }

    public /* synthetic */ void lambda$new$7$ChatAttachAlertLocationLayout(ChatActivity chatActivity, View view, int i) {
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
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(tLRPC$TL_messageMediaGeo) {
                        private final /* synthetic */ TLRPC$TL_messageMediaGeo f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            ChatAttachAlertLocationLayout.this.lambda$null$5$ChatAttachAlertLocationLayout(this.f$1, z, i);
                        }
                    });
                    return;
                }
                this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeo, this.locationType, true, 0);
                this.parentAlert.dismiss();
            }
        } else if (i != 2 || this.locationType != 1) {
            Object item = this.adapter.getItem(i);
            if (item instanceof TLRPC$TL_messageMediaVenue) {
                if (chatActivity.isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(item) {
                        private final /* synthetic */ Object f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void didSelectDate(boolean z, int i) {
                            ChatAttachAlertLocationLayout.this.lambda$null$6$ChatAttachAlertLocationLayout(this.f$1, z, i);
                        }
                    });
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
        } else {
            openShareLiveLocation();
        }
    }

    public /* synthetic */ void lambda$null$5$ChatAttachAlertLocationLayout(TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo, boolean z, int i) {
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaGeo, this.locationType, z, i);
        this.parentAlert.dismiss();
    }

    public /* synthetic */ void lambda$null$6$ChatAttachAlertLocationLayout(Object obj, boolean z, int i) {
        this.delegate.didSelectLocation((TLRPC$TL_messageMediaVenue) obj, this.locationType, z, i);
        this.parentAlert.dismiss();
    }

    public /* synthetic */ void lambda$new$12$ChatAttachAlertLocationLayout(MapView mapView2) {
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
                ChatAttachAlertLocationLayout.this.lambda$null$11$ChatAttachAlertLocationLayout(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$11$ChatAttachAlertLocationLayout(MapView mapView2) {
        if (this.mapView != null && getParentActivity() != null) {
            try {
                mapView2.onCreate((Bundle) null);
                MapsInitializer.initialize(ApplicationLoader.applicationContext);
                this.mapView.getMapAsync(new OnMapReadyCallback() {
                    public final void onMapReady(GoogleMap googleMap) {
                        ChatAttachAlertLocationLayout.this.lambda$null$10$ChatAttachAlertLocationLayout(googleMap);
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

    public /* synthetic */ void lambda$null$10$ChatAttachAlertLocationLayout(GoogleMap googleMap2) {
        this.googleMap = googleMap2;
        googleMap2.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            public final void onMapLoaded() {
                ChatAttachAlertLocationLayout.this.lambda$null$9$ChatAttachAlertLocationLayout();
            }
        });
        if (isActiveThemeDark()) {
            this.currentMapStyleDark = true;
            this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationLoader.applicationContext, NUM));
        }
        this.googleMap.setPadding(AndroidUtilities.dp(70.0f), 0, AndroidUtilities.dp(70.0f), AndroidUtilities.dp(10.0f));
        onMapInit();
    }

    public /* synthetic */ void lambda$null$9$ChatAttachAlertLocationLayout() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                ChatAttachAlertLocationLayout.this.lambda$null$8$ChatAttachAlertLocationLayout();
            }
        });
    }

    public /* synthetic */ void lambda$null$8$ChatAttachAlertLocationLayout() {
        this.loadingMapView.setTag(1);
        this.loadingMapView.animate().alpha(0.0f).setDuration(180).start();
    }

    public /* synthetic */ void lambda$new$13$ChatAttachAlertLocationLayout(ArrayList arrayList) {
        this.searchInProgress = false;
        updateEmptyView();
    }

    public /* synthetic */ void lambda$new$15$ChatAttachAlertLocationLayout(ChatActivity chatActivity, View view, int i) {
        TLRPC$TL_messageMediaVenue item = this.searchAdapter.getItem(i);
        if (item != null && this.delegate != null) {
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate(item) {
                    private final /* synthetic */ TLRPC$TL_messageMediaVenue f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertLocationLayout.this.lambda$null$14$ChatAttachAlertLocationLayout(this.f$1, z, i);
                    }
                });
                return;
            }
            this.delegate.didSelectLocation(item, this.locationType, true, 0);
            this.parentAlert.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$14$ChatAttachAlertLocationLayout(TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, boolean z, int i) {
        this.delegate.didSelectLocation(tLRPC$TL_messageMediaVenue, this.locationType, z, i);
        this.parentAlert.dismiss();
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
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x0030 */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0034 A[Catch:{ Exception -> 0x003d }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0049  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDestroy() {
        /*
            r2 = this;
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.locationPermissionGranted
            r0.removeObserver(r2, r1)
            com.google.android.gms.maps.GoogleMap r0 = r2.googleMap     // Catch:{ Exception -> 0x0014 }
            if (r0 == 0) goto L_0x0018
            com.google.android.gms.maps.GoogleMap r0 = r2.googleMap     // Catch:{ Exception -> 0x0014 }
            r1 = 0
            r0.setMyLocationEnabled(r1)     // Catch:{ Exception -> 0x0014 }
            goto L_0x0018
        L_0x0014:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0018:
            com.google.android.gms.maps.MapView r0 = r2.mapView
            if (r0 == 0) goto L_0x0027
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.y
            int r1 = -r1
            int r1 = r1 * 3
            float r1 = (float) r1
            r0.setTranslationY(r1)
        L_0x0027:
            com.google.android.gms.maps.MapView r0 = r2.mapView     // Catch:{ Exception -> 0x0030 }
            if (r0 == 0) goto L_0x0030
            com.google.android.gms.maps.MapView r0 = r2.mapView     // Catch:{ Exception -> 0x0030 }
            r0.onPause()     // Catch:{ Exception -> 0x0030 }
        L_0x0030:
            com.google.android.gms.maps.MapView r0 = r2.mapView     // Catch:{ Exception -> 0x003d }
            if (r0 == 0) goto L_0x003e
            com.google.android.gms.maps.MapView r0 = r2.mapView     // Catch:{ Exception -> 0x003d }
            r0.onDestroy()     // Catch:{ Exception -> 0x003d }
            r0 = 0
            r2.mapView = r0     // Catch:{ Exception -> 0x003d }
            goto L_0x003e
        L_0x003d:
        L_0x003e:
            org.telegram.ui.Adapters.LocationActivityAdapter r0 = r2.adapter
            if (r0 == 0) goto L_0x0045
            r0.destroy()
        L_0x0045:
            org.telegram.ui.Adapters.LocationActivitySearchAdapter r0 = r2.searchAdapter
            if (r0 == 0) goto L_0x004c
            r0.destroy()
        L_0x004c:
            org.telegram.ui.Components.ChatAttachAlert r0 = r2.parentAlert
            org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r2.searchItem
            r0.removeView(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertLocationLayout.onDestroy():void");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: protected */
    public void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        view.measure(FrameLayout.getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + i2, marginLayoutParams.width), FrameLayout.getChildMeasureSpec(i3, getPaddingTop() + getPaddingBottom() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin + i4, marginLayoutParams.height));
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
            int y = (int) holder.itemView.getY();
            i = Math.max(y, 0);
            if (y >= 0) {
                i = y;
            }
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
    /* JADX WARNING: Removed duplicated region for block: B:12:0x003b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPreMeasure(int r4, int r5) {
        /*
            r3 = this;
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            org.telegram.ui.ActionBar.ActionBar r4 = r4.actionBar
            boolean r4 = r4.isSearchFieldVisible()
            r0 = 1
            r1 = 0
            if (r4 != 0) goto L_0x0042
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r4.sizeNotifierFrameLayout
            int r4 = r4.getKeyboardHeight()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            if (r4 <= r2) goto L_0x001d
            goto L_0x0042
        L_0x001d:
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 != 0) goto L_0x002e
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r4.x
            int r4 = r4.y
            if (r2 <= r4) goto L_0x002e
            int r5 = r5 / 6
            goto L_0x0032
        L_0x002e:
            int r5 = r5 / 5
            int r5 = r5 * 2
        L_0x0032:
            r4 = 1112539136(0x42500000, float:52.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r5 - r4
            if (r5 >= 0) goto L_0x003c
            r5 = 0
        L_0x003c:
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            r4.setAllowNestedScroll(r0)
            goto L_0x0048
        L_0x0042:
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            r4.setAllowNestedScroll(r1)
            r5 = 0
        L_0x0048:
            org.telegram.ui.Components.RecyclerListView r4 = r3.listView
            int r4 = r4.getPaddingTop()
            if (r4 == r5) goto L_0x0059
            r3.ignoreLayout = r0
            org.telegram.ui.Components.RecyclerListView r4 = r3.listView
            r4.setPadding(r1, r5, r1, r1)
            r3.ignoreLayout = r1
        L_0x0059:
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

    private void openShareLiveLocation() {
        if (this.delegate != null && getParentActivity() != null && this.myLocation != null) {
            TLRPC$User tLRPC$User = null;
            if (((int) this.dialogId) > 0) {
                tLRPC$User = this.parentAlert.baseFragment.getMessagesController().getUser(Integer.valueOf((int) this.dialogId));
            }
            AlertsCreator.createLocationUpdateDialog(getParentActivity(), tLRPC$User, new MessagesStorage.IntCallback() {
                public final void run(int i) {
                    ChatAttachAlertLocationLayout.this.lambda$openShareLiveLocation$16$ChatAttachAlertLocationLayout(i);
                }
            }).show();
        }
    }

    public /* synthetic */ void lambda$openShareLiveLocation$16$ChatAttachAlertLocationLayout(int i) {
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
                    markerOptions.position(new LatLng(tLRPC$TL_messageMediaVenue.geo.lat, tLRPC$TL_messageMediaVenue.geo._long));
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
            this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                public final void onCameraMoveStarted(int i) {
                    ChatAttachAlertLocationLayout.this.lambda$onMapInit$17$ChatAttachAlertLocationLayout(i);
                }
            });
            this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                public final void onMyLocationChange(Location location) {
                    ChatAttachAlertLocationLayout.this.lambda$onMapInit$18$ChatAttachAlertLocationLayout(location);
                }
            });
            this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public final boolean onMarkerClick(Marker marker) {
                    return ChatAttachAlertLocationLayout.this.lambda$onMapInit$19$ChatAttachAlertLocationLayout(marker);
                }
            });
            this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                public final void onCameraMove() {
                    ChatAttachAlertLocationLayout.this.lambda$onMapInit$20$ChatAttachAlertLocationLayout();
                }
            });
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChatAttachAlertLocationLayout.this.lambda$onMapInit$21$ChatAttachAlertLocationLayout();
                }
            }, 2000);
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
                                    ChatAttachAlertLocationLayout.this.lambda$onMapInit$22$ChatAttachAlertLocationLayout(dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            builder.show();
                        }
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$onMapInit$17$ChatAttachAlertLocationLayout(int i) {
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

    public /* synthetic */ void lambda$onMapInit$18$ChatAttachAlertLocationLayout(Location location) {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (chatAttachAlert != null && chatAttachAlert.baseFragment != null) {
            positionMarker(location);
            getLocationController().setGoogleMapLocation(location, this.isFirstLocation);
            this.isFirstLocation = false;
        }
    }

    public /* synthetic */ boolean lambda$onMapInit$19$ChatAttachAlertLocationLayout(Marker marker) {
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

    public /* synthetic */ void lambda$onMapInit$20$ChatAttachAlertLocationLayout() {
        MapOverlayView mapOverlayView = this.overlayView;
        if (mapOverlayView != null) {
            mapOverlayView.updatePositions();
        }
    }

    public /* synthetic */ void lambda$onMapInit$21$ChatAttachAlertLocationLayout() {
        if (this.loadingMapView.getTag() == null) {
            this.loadingMapView.animate().alpha(0.0f).setDuration(180).start();
        }
    }

    public /* synthetic */ void lambda$onMapInit$22$ChatAttachAlertLocationLayout(DialogInterface dialogInterface, int i) {
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
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", NUM));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatAttachAlertLocationLayout.this.lambda$showPermissionAlert$23$ChatAttachAlertLocationLayout(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$23$ChatAttachAlertLocationLayout(DialogInterface dialogInterface, int i) {
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
        boolean z;
        int i;
        int i2;
        FrameLayout.LayoutParams layoutParams;
        if (this.mapView != null && this.mapViewClip != null) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition != null) {
                i2 = (int) findViewHolderForAdapterPosition.itemView.getY();
                i = this.overScrollHeight + Math.min(i2, 0);
                z = false;
            } else {
                i2 = -this.mapViewClip.getMeasuredHeight();
                i = 0;
                z = true;
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
                this.mapViewClip.setTranslationY((float) Math.min(this.listView.getPaddingTop(), i2));
                if (z) {
                    this.mapView.setTranslationY((float) i2);
                } else {
                    this.mapView.setTranslationY((float) Math.max(0, (-i2) / 2));
                }
                MapOverlayView mapOverlayView3 = this.overlayView;
                if (mapOverlayView3 != null) {
                    mapOverlayView3.setTranslationY((float) Math.max(0, (-i2) / 2));
                }
                float min = (float) Math.min((this.overScrollHeight - this.mapTypeButton.getMeasuredHeight()) - AndroidUtilities.dp(94.0f), -Math.min(i2, 0));
                this.mapTypeButton.setTranslationY(min);
                this.searchAreaButton.setTranslation(min);
                ImageView imageView = this.markerImageView;
                int dp = ((-Math.min(i2, 0)) - AndroidUtilities.dp(this.markerImageView.getTag() == null ? 48.0f : 69.0f)) + (i / 2);
                this.markerTop = dp;
                imageView.setTranslationY((float) dp);
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

    private void fixLayoutInternal(boolean z) {
        FrameLayout.LayoutParams layoutParams;
        if (getMeasuredHeight() != 0 && this.mapView != null) {
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
            this.overScrollHeight = AndroidUtilities.dp(189.0f);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            layoutParams2.topMargin = currentActionBarHeight;
            this.listView.setLayoutParams(layoutParams2);
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mapViewClip.getLayoutParams();
            layoutParams3.topMargin = currentActionBarHeight;
            layoutParams3.height = this.overScrollHeight;
            this.mapViewClip.setLayoutParams(layoutParams3);
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.searchListView.getLayoutParams();
            layoutParams4.topMargin = currentActionBarHeight;
            this.searchListView.setLayoutParams(layoutParams4);
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
        GoogleMap googleMap2;
        if (i == NotificationCenter.locationPermissionGranted && (googleMap2 = this.googleMap) != null) {
            try {
                googleMap2.setMyLocationEnabled(true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
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
    public void onShow() {
        Activity parentActivity;
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
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (parentActivity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            }
        }
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        updateClipView();
    }

    public void setDelegate(LocationActivityDelegate locationActivityDelegate) {
        this.delegate = locationActivityDelegate;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ChatAttachAlertLocationLayout$KehSoJwr2jmVPiEE6flIIaKp6P0 r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatAttachAlertLocationLayout.this.lambda$getThemeDescriptions$24$ChatAttachAlertLocationLayout();
            }
        };
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
        $$Lambda$ChatAttachAlertLocationLayout$KehSoJwr2jmVPiEE6flIIaKp6P0 r8 = r10;
        arrayList.add(new ThemeDescription(this.mapTypeButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "location_actionIcon"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionBackground"));
        arrayList.add(new ThemeDescription(this.mapTypeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "location_actionPressedBackground"));
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
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SharingLiveLocationCell.class}, new String[]{"distanceTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$24$ChatAttachAlertLocationLayout() {
        this.mapTypeButton.setIconColor(Theme.getColor("location_actionIcon"));
        this.mapTypeButton.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        this.mapTypeButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
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
