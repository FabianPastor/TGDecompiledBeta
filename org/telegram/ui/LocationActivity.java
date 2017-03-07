package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.BaseLocationAdapter.BaseLocationAdapterDelegate;
import org.telegram.ui.Adapters.LocationActivityAdapter;
import org.telegram.ui.Adapters.LocationActivitySearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MapPlaceholderDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class LocationActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int map_list_menu_hybrid = 4;
    private static final int map_list_menu_map = 2;
    private static final int map_list_menu_satellite = 3;
    private static final int share = 1;
    private LocationActivityAdapter adapter;
    private AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private FrameLayout bottomView;
    private boolean checkPermission = true;
    private CircleOptions circleOptions;
    private LocationActivityDelegate delegate;
    private TextView distanceTextView;
    private EmptyTextProgressView emptyView;
    private boolean firstWas = false;
    private GoogleMap googleMap;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ImageView locationButton;
    private MapView mapView;
    private FrameLayout mapViewClip;
    private boolean mapsInitialized;
    private ImageView markerImageView;
    private int markerTop;
    private ImageView markerXImageView;
    private MessageObject messageObject;
    private Location myLocation;
    private TextView nameTextView;
    private boolean onResumeCalled;
    private int overScrollHeight = ((AndroidUtilities.displaySize.x - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(66.0f));
    private ImageView routeButton;
    private LocationActivitySearchAdapter searchAdapter;
    private RecyclerListView searchListView;
    private boolean searchWas;
    private boolean searching;
    private Location userLocation;
    private boolean userLocationMoved = false;
    private boolean wasResults;

    public interface LocationActivityDelegate {
        void didSelectLocation(MessageMedia messageMedia);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.swipeBackEnabled = false;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        if (this.messageObject != null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        try {
            if (this.mapView != null) {
                this.mapView.onDestroy();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (this.adapter != null) {
            this.adapter.destroy();
        }
        if (this.searchAdapter != null) {
            this.searchAdapter.destroy();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAddToContainer(this.messageObject != null);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    LocationActivity.this.finishFragment();
                } else if (id == 2) {
                    if (LocationActivity.this.googleMap != null) {
                        LocationActivity.this.googleMap.setMapType(1);
                    }
                } else if (id == 3) {
                    if (LocationActivity.this.googleMap != null) {
                        LocationActivity.this.googleMap.setMapType(2);
                    }
                } else if (id == 4) {
                    if (LocationActivity.this.googleMap != null) {
                        LocationActivity.this.googleMap.setMapType(4);
                    }
                } else if (id == 1) {
                    try {
                        double lat = LocationActivity.this.messageObject.messageOwner.media.geo.lat;
                        double lon = LocationActivity.this.messageObject.messageOwner.media.geo._long;
                        LocationActivity.this.getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.messageObject != null) {
            if (this.messageObject.messageOwner.media.title == null || this.messageObject.messageOwner.media.title.length() <= 0) {
                this.actionBar.setTitle(LocaleController.getString("ChatLocation", R.string.ChatLocation));
            } else {
                this.actionBar.setTitle(this.messageObject.messageOwner.media.title);
                if (this.messageObject.messageOwner.media.address != null && this.messageObject.messageOwner.media.address.length() > 0) {
                    this.actionBar.setSubtitle(this.messageObject.messageOwner.media.address);
                }
            }
            menu.addItem(1, (int) R.drawable.share);
        } else {
            this.actionBar.setTitle(LocaleController.getString("ShareLocation", R.string.ShareLocation));
            menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    LocationActivity.this.searching = true;
                    LocationActivity.this.listView.setVisibility(8);
                    LocationActivity.this.mapViewClip.setVisibility(8);
                    LocationActivity.this.searchListView.setVisibility(0);
                    LocationActivity.this.searchListView.setEmptyView(LocationActivity.this.emptyView);
                }

                public void onSearchCollapse() {
                    LocationActivity.this.searching = false;
                    LocationActivity.this.searchWas = false;
                    LocationActivity.this.searchListView.setEmptyView(null);
                    LocationActivity.this.listView.setVisibility(0);
                    LocationActivity.this.mapViewClip.setVisibility(0);
                    LocationActivity.this.searchListView.setVisibility(8);
                    LocationActivity.this.emptyView.setVisibility(8);
                    LocationActivity.this.searchAdapter.searchDelayed(null, null);
                }

                public void onTextChanged(EditText editText) {
                    if (LocationActivity.this.searchAdapter != null) {
                        String text = editText.getText().toString();
                        if (text.length() != 0) {
                            LocationActivity.this.searchWas = true;
                        }
                        LocationActivity.this.searchAdapter.searchDelayed(text, LocationActivity.this.userLocation);
                    }
                }
            }).getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        }
        ActionBarMenuItem item = menu.addItem(0, (int) R.drawable.ic_ab_other);
        item.addSubItem(2, LocaleController.getString("Map", R.string.Map));
        item.addSubItem(3, LocaleController.getString("Satellite", R.string.Satellite));
        item.addSubItem(4, LocaleController.getString("Hybrid", R.string.Hybrid));
        this.fragmentView = new FrameLayout(context) {
            private boolean first = true;

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (changed) {
                    LocationActivity.this.fixLayoutInternal(this.first);
                    this.first = false;
                }
            }
        };
        FrameLayout frameLayout = this.fragmentView;
        this.locationButton = new ImageView(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.locationButton.setBackgroundDrawable(drawable);
        this.locationButton.setImageResource(R.drawable.myloc_on);
        this.locationButton.setScaleType(ScaleType.CENTER);
        this.locationButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.locationButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.locationButton.setStateListAnimator(animator);
            this.locationButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        final MapView map;
        if (this.messageObject != null) {
            float f;
            float f2;
            float f3;
            this.mapView = new MapView(context);
            frameLayout.setBackgroundDrawable(new MapPlaceholderDrawable());
            map = this.mapView;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        map.onCreate(null);
                    } catch (Exception e) {
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (LocationActivity.this.mapView != null && LocationActivity.this.getParentActivity() != null) {
                                try {
                                    map.onCreate(null);
                                    MapsInitializer.initialize(LocationActivity.this.getParentActivity());
                                    LocationActivity.this.mapView.getMapAsync(new OnMapReadyCallback() {
                                        public void onMapReady(GoogleMap map) {
                                            LocationActivity.this.googleMap = map;
                                            LocationActivity.this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                                            LocationActivity.this.onMapInit();
                                        }
                                    });
                                    LocationActivity.this.mapsInitialized = true;
                                    if (LocationActivity.this.onResumeCalled) {
                                        LocationActivity.this.mapView.onResume();
                                    }
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            }
                        }
                    });
                }
            }).start();
            this.bottomView = new FrameLayout(context);
            Drawable background = context.getResources().getDrawable(R.drawable.location_panel);
            background.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhite), Mode.MULTIPLY));
            this.bottomView.setBackgroundDrawable(background);
            frameLayout.addView(this.bottomView, LayoutHelper.createFrame(-1, 60, 83));
            this.bottomView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (LocationActivity.this.userLocation != null) {
                        LatLng latLng = new LatLng(LocationActivity.this.userLocation.getLatitude(), LocationActivity.this.userLocation.getLongitude());
                        if (LocationActivity.this.googleMap != null) {
                            LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
                        }
                    }
                }
            });
            this.avatarImageView = new BackupImageView(context);
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            this.bottomView.addView(this.avatarImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 12.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
            this.nameTextView = new TextView(context);
            this.nameTextView.setTextSize(1, 16.0f);
            this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setEllipsize(TruncateAt.END);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.bottomView.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 12.0f : 72.0f, 10.0f, LocaleController.isRTL ? 72.0f : 12.0f, 0.0f));
            this.distanceTextView = new TextView(context);
            this.distanceTextView.setTextSize(1, 14.0f);
            this.distanceTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            this.distanceTextView.setMaxLines(1);
            this.distanceTextView.setEllipsize(TruncateAt.END);
            this.distanceTextView.setSingleLine(true);
            this.distanceTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            FrameLayout frameLayout2 = this.bottomView;
            View view = this.distanceTextView;
            int i = (LocaleController.isRTL ? 5 : 3) | 48;
            if (LocaleController.isRTL) {
                f = 12.0f;
            } else {
                f = 72.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 72.0f;
            } else {
                f2 = 12.0f;
            }
            frameLayout2.addView(view, LayoutHelper.createFrame(-2, -2.0f, i, f, 33.0f, f2, 0.0f));
            this.userLocation = new Location("network");
            this.userLocation.setLatitude(this.messageObject.messageOwner.media.geo.lat);
            this.userLocation.setLongitude(this.messageObject.messageOwner.media.geo._long);
            this.routeButton = new ImageView(context);
            drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
            if (VERSION.SDK_INT < 21) {
                shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                drawable = combinedDrawable;
            }
            this.routeButton.setBackgroundDrawable(drawable);
            this.routeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
            this.routeButton.setImageResource(R.drawable.navigate);
            this.routeButton.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.routeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.routeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.routeButton.setStateListAnimator(animator);
                this.routeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            View view2 = this.routeButton;
            int i2 = VERSION.SDK_INT >= 21 ? 56 : 60;
            if (VERSION.SDK_INT >= 21) {
                f3 = 56.0f;
            } else {
                f3 = BitmapDescriptorFactory.HUE_YELLOW;
            }
            if (LocaleController.isRTL) {
                i = 3;
            } else {
                i = 5;
            }
            i |= 80;
            if (LocaleController.isRTL) {
                f = 14.0f;
            } else {
                f = 0.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 0.0f;
            } else {
                f2 = 14.0f;
            }
            frameLayout.addView(view2, LayoutHelper.createFrame(i2, f3, i, f, 0.0f, f2, 28.0f));
            this.routeButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (VERSION.SDK_INT >= 23) {
                        Activity activity = LocationActivity.this.getParentActivity();
                        if (!(activity == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                            LocationActivity.this.showPermissionAlert(true);
                            return;
                        }
                    }
                    if (LocationActivity.this.myLocation != null) {
                        try {
                            LocationActivity.this.getParentActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(String.format(Locale.US, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", new Object[]{Double.valueOf(LocationActivity.this.myLocation.getLatitude()), Double.valueOf(LocationActivity.this.myLocation.getLongitude()), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo.lat), Double.valueOf(LocationActivity.this.messageObject.messageOwner.media.geo._long)}))));
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                }
            });
            view2 = this.locationButton;
            i2 = VERSION.SDK_INT >= 21 ? 56 : 60;
            if (VERSION.SDK_INT >= 21) {
                f3 = 56.0f;
            } else {
                f3 = BitmapDescriptorFactory.HUE_YELLOW;
            }
            if (LocaleController.isRTL) {
                i = 3;
            } else {
                i = 5;
            }
            i |= 80;
            if (LocaleController.isRTL) {
                f = 14.0f;
            } else {
                f = 0.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 0.0f;
            } else {
                f2 = 14.0f;
            }
            frameLayout.addView(view2, LayoutHelper.createFrame(i2, f3, i, f, 0.0f, f2, 100.0f));
            this.locationButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (VERSION.SDK_INT >= 23) {
                        Activity activity = LocationActivity.this.getParentActivity();
                        if (!(activity == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                            LocationActivity.this.showPermissionAlert(true);
                            return;
                        }
                    }
                    if (LocationActivity.this.myLocation != null && LocationActivity.this.googleMap != null) {
                        LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude()), LocationActivity.this.googleMap.getMaxZoomLevel() - 4.0f));
                    }
                }
            });
        } else {
            this.searchWas = false;
            this.searching = false;
            this.mapViewClip = new FrameLayout(context);
            this.mapViewClip.setBackgroundDrawable(new MapPlaceholderDrawable());
            if (this.adapter != null) {
                this.adapter.destroy();
            }
            if (this.searchAdapter != null) {
                this.searchAdapter.destroy();
            }
            this.listView = new RecyclerListView(context);
            RecyclerListView recyclerListView = this.listView;
            Adapter locationActivityAdapter = new LocationActivityAdapter(context);
            this.adapter = locationActivityAdapter;
            recyclerListView.setAdapter(locationActivityAdapter);
            this.listView.setVerticalScrollBarEnabled(false);
            recyclerListView = this.listView;
            LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
            this.layoutManager = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (LocationActivity.this.adapter.getItemCount() != 0) {
                        int position = LocationActivity.this.layoutManager.findFirstVisibleItemPosition();
                        if (position != -1) {
                            LocationActivity.this.updateClipView(position);
                        }
                    }
                }
            });
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    if (position == 1) {
                        if (!(LocationActivity.this.delegate == null || LocationActivity.this.userLocation == null)) {
                            TL_messageMediaGeo location = new TL_messageMediaGeo();
                            location.geo = new TL_geoPoint();
                            location.geo.lat = LocationActivity.this.userLocation.getLatitude();
                            location.geo._long = LocationActivity.this.userLocation.getLongitude();
                            LocationActivity.this.delegate.didSelectLocation(location);
                        }
                        LocationActivity.this.finishFragment();
                        return;
                    }
                    TL_messageMediaVenue object = LocationActivity.this.adapter.getItem(position);
                    if (!(object == null || LocationActivity.this.delegate == null)) {
                        LocationActivity.this.delegate.didSelectLocation(object);
                    }
                    LocationActivity.this.finishFragment();
                }
            });
            this.adapter.setDelegate(new BaseLocationAdapterDelegate() {
                public void didLoadedSearchResult(ArrayList<TL_messageMediaVenue> places) {
                    if (!LocationActivity.this.wasResults && !places.isEmpty()) {
                        LocationActivity.this.wasResults = true;
                    }
                }
            });
            this.adapter.setOverScrollHeight(this.overScrollHeight);
            frameLayout.addView(this.mapViewClip, LayoutHelper.createFrame(-1, -1, 51));
            this.mapView = new MapView(context) {
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    AnimatorSet access$2200;
                    Animator[] animatorArr;
                    if (ev.getAction() == 0) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$2200 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", new float[]{(float) (LocationActivity.this.markerTop + (-AndroidUtilities.dp(10.0f)))});
                        animatorArr[1] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", new float[]{1.0f});
                        access$2200.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    } else if (ev.getAction() == 1) {
                        if (LocationActivity.this.animatorSet != null) {
                            LocationActivity.this.animatorSet.cancel();
                        }
                        LocationActivity.this.animatorSet = new AnimatorSet();
                        LocationActivity.this.animatorSet.setDuration(200);
                        access$2200 = LocationActivity.this.animatorSet;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(LocationActivity.this.markerImageView, "translationY", new float[]{(float) LocationActivity.this.markerTop});
                        animatorArr[1] = ObjectAnimator.ofFloat(LocationActivity.this.markerXImageView, "alpha", new float[]{0.0f});
                        access$2200.playTogether(animatorArr);
                        LocationActivity.this.animatorSet.start();
                    }
                    if (ev.getAction() == 2) {
                        if (!LocationActivity.this.userLocationMoved) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.setDuration(200);
                            animatorSet.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[]{1.0f}));
                            animatorSet.start();
                            LocationActivity.this.userLocationMoved = true;
                        }
                        if (!(LocationActivity.this.googleMap == null || LocationActivity.this.userLocation == null)) {
                            LocationActivity.this.userLocation.setLatitude(LocationActivity.this.googleMap.getCameraPosition().target.latitude);
                            LocationActivity.this.userLocation.setLongitude(LocationActivity.this.googleMap.getCameraPosition().target.longitude);
                        }
                        LocationActivity.this.adapter.setCustomLocation(LocationActivity.this.userLocation);
                    }
                    return super.onInterceptTouchEvent(ev);
                }
            };
            map = this.mapView;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        map.onCreate(null);
                    } catch (Exception e) {
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (LocationActivity.this.mapView != null && LocationActivity.this.getParentActivity() != null) {
                                try {
                                    map.onCreate(null);
                                    MapsInitializer.initialize(LocationActivity.this.getParentActivity());
                                    LocationActivity.this.mapView.getMapAsync(new OnMapReadyCallback() {
                                        public void onMapReady(GoogleMap map) {
                                            LocationActivity.this.googleMap = map;
                                            LocationActivity.this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                                            LocationActivity.this.onMapInit();
                                        }
                                    });
                                    LocationActivity.this.mapsInitialized = true;
                                    if (LocationActivity.this.onResumeCalled) {
                                        LocationActivity.this.mapView.onResume();
                                    }
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            }
                        }
                    });
                }
            }).start();
            View view3 = new View(context);
            view3.setBackgroundResource(R.drawable.header_shadow_reverse);
            this.mapViewClip.addView(view3, LayoutHelper.createFrame(-1, 3, 83));
            this.markerImageView = new ImageView(context);
            this.markerImageView.setImageResource(R.drawable.map_pin);
            this.mapViewClip.addView(this.markerImageView, LayoutHelper.createFrame(24, 42, 49));
            this.markerXImageView = new ImageView(context);
            this.markerXImageView.setAlpha(0.0f);
            this.markerXImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_markerX), Mode.MULTIPLY));
            this.markerXImageView.setImageResource(R.drawable.place_x);
            this.mapViewClip.addView(this.markerXImageView, LayoutHelper.createFrame(14, 14, 49));
            this.mapViewClip.addView(this.locationButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : BitmapDescriptorFactory.HUE_YELLOW, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
            this.locationButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (VERSION.SDK_INT >= 23) {
                        Activity activity = LocationActivity.this.getParentActivity();
                        if (!(activity == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                            LocationActivity.this.showPermissionAlert(false);
                            return;
                        }
                    }
                    if (LocationActivity.this.myLocation != null && LocationActivity.this.googleMap != null) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.setDuration(200);
                        animatorSet.play(ObjectAnimator.ofFloat(LocationActivity.this.locationButton, "alpha", new float[]{0.0f}));
                        animatorSet.start();
                        LocationActivity.this.adapter.setCustomLocation(null);
                        LocationActivity.this.userLocationMoved = false;
                        LocationActivity.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(LocationActivity.this.myLocation.getLatitude(), LocationActivity.this.myLocation.getLongitude())));
                    }
                }
            });
            this.locationButton.setAlpha(0.0f);
            this.emptyView = new EmptyTextProgressView(context);
            this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
            this.emptyView.setShowAtCenter(true);
            this.emptyView.setVisibility(8);
            frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.searchListView = new RecyclerListView(context);
            this.searchListView.setVisibility(8);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
            recyclerListView = this.searchListView;
            locationActivityAdapter = new LocationActivitySearchAdapter(context);
            this.searchAdapter = locationActivityAdapter;
            recyclerListView.setAdapter(locationActivityAdapter);
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1 && LocationActivity.this.searching && LocationActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(LocationActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    TL_messageMediaVenue object = LocationActivity.this.searchAdapter.getItem(position);
                    if (!(object == null || LocationActivity.this.delegate == null)) {
                        LocationActivity.this.delegate.didSelectLocation(object);
                    }
                    LocationActivity.this.finishFragment();
                }
            });
            frameLayout.addView(this.actionBar);
        }
        return this.fragmentView;
    }

    private void onMapInit() {
        if (this.googleMap != null) {
            if (this.messageObject != null) {
                LatLng latLng = new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude());
                try {
                    this.googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, this.googleMap.getMaxZoomLevel() - 4.0f));
            } else {
                this.userLocation = new Location("network");
                this.userLocation.setLatitude(20.659322d);
                this.userLocation.setLongitude(-11.40625d);
            }
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setCompassEnabled(false);
            this.googleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
                public void onMyLocationChange(Location location) {
                    LocationActivity.this.positionMarker(location);
                }
            });
            Location lastLocation = getLastLocation();
            this.myLocation = lastLocation;
            positionMarker(lastLocation);
        }
    }

    private void showPermissionAlert(boolean byButton) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (byButton) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", R.string.PermissionNoLocationPosition));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", R.string.PermissionNoLocation));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                @TargetApi(9)
                public void onClick(DialogInterface dialog, int which) {
                    if (LocationActivity.this.getParentActivity() != null) {
                        try {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                            LocationActivity.this.getParentActivity().startActivity(intent);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            try {
                if (this.mapView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.mapView.getParent()).removeView(this.mapView);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.mapViewClip != null) {
                this.mapViewClip.addView(this.mapView, 0, LayoutHelper.createFrame(-1, this.overScrollHeight + AndroidUtilities.dp(10.0f), 51));
                updateClipView(this.layoutManager.findFirstVisibleItemPosition());
            } else if (this.fragmentView != null) {
                ((FrameLayout) this.fragmentView).addView(this.mapView, 0, LayoutHelper.createFrame(-1, -1, 51));
            }
        }
    }

    private void updateClipView(int firstVisibleItem) {
        if (firstVisibleItem != -1) {
            int height = 0;
            int top = 0;
            View child = this.listView.getChildAt(0);
            if (child != null) {
                int i;
                if (firstVisibleItem == 0) {
                    int i2;
                    top = child.getTop();
                    i = this.overScrollHeight;
                    if (top < 0) {
                        i2 = top;
                    } else {
                        i2 = 0;
                    }
                    height = i + i2;
                }
                if (((LayoutParams) this.mapViewClip.getLayoutParams()) != null) {
                    if (height <= 0) {
                        if (this.mapView.getVisibility() == 0) {
                            this.mapView.setVisibility(4);
                            this.mapViewClip.setVisibility(4);
                        }
                    } else if (this.mapView.getVisibility() == 4) {
                        this.mapView.setVisibility(0);
                        this.mapViewClip.setVisibility(0);
                    }
                    this.mapViewClip.setTranslationY((float) Math.min(0, top));
                    this.mapView.setTranslationY((float) Math.max(0, (-top) / 2));
                    ImageView imageView = this.markerImageView;
                    i = ((-top) - AndroidUtilities.dp(42.0f)) + (height / 2);
                    this.markerTop = i;
                    imageView.setTranslationY((float) i);
                    this.markerXImageView.setTranslationY((float) (((-top) - AndroidUtilities.dp(7.0f)) + (height / 2)));
                    LayoutParams layoutParams = (LayoutParams) this.mapView.getLayoutParams();
                    if (layoutParams != null && layoutParams.height != this.overScrollHeight + AndroidUtilities.dp(10.0f)) {
                        layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                        if (this.googleMap != null) {
                            this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                        }
                        this.mapView.setLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    private void fixLayoutInternal(boolean resume) {
        if (this.listView != null) {
            int height = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
            int viewHeight = this.fragmentView.getMeasuredHeight();
            if (viewHeight != 0) {
                this.overScrollHeight = (viewHeight - AndroidUtilities.dp(66.0f)) - height;
                LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
                layoutParams.topMargin = height;
                this.listView.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.mapViewClip.getLayoutParams();
                layoutParams.topMargin = height;
                layoutParams.height = this.overScrollHeight;
                this.mapViewClip.setLayoutParams(layoutParams);
                layoutParams = (LayoutParams) this.searchListView.getLayoutParams();
                layoutParams.topMargin = height;
                this.searchListView.setLayoutParams(layoutParams);
                this.adapter.setOverScrollHeight(this.overScrollHeight);
                layoutParams = (LayoutParams) this.mapView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = this.overScrollHeight + AndroidUtilities.dp(10.0f);
                    if (this.googleMap != null) {
                        this.googleMap.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                    }
                    this.mapView.setLayoutParams(layoutParams);
                }
                this.adapter.notifyDataSetChanged();
                if (resume) {
                    this.layoutManager.scrollToPositionWithOffset(0, -((int) ((((float) AndroidUtilities.dp(56.0f)) * 2.5f) + ((float) AndroidUtilities.dp(102.0f)))));
                    updateClipView(this.layoutManager.findFirstVisibleItemPosition());
                    this.listView.post(new Runnable() {
                        public void run() {
                            LocationActivity.this.layoutManager.scrollToPositionWithOffset(0, -((int) ((((float) AndroidUtilities.dp(56.0f)) * 2.5f) + ((float) AndroidUtilities.dp(102.0f)))));
                            LocationActivity.this.updateClipView(LocationActivity.this.layoutManager.findFirstVisibleItemPosition());
                        }
                    });
                    return;
                }
                updateClipView(this.layoutManager.findFirstVisibleItemPosition());
            }
        }
    }

    private Location getLastLocation() {
        LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService(Param.LOCATION);
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation((String) providers.get(i));
            if (l != null) {
                break;
            }
        }
        return l;
    }

    private void updateUserData() {
        if (this.messageObject != null && this.avatarImageView != null) {
            int fromId = this.messageObject.messageOwner.from_id;
            if (this.messageObject.isForwarded()) {
                if (this.messageObject.messageOwner.fwd_from.channel_id != 0) {
                    fromId = -this.messageObject.messageOwner.fwd_from.channel_id;
                } else {
                    fromId = this.messageObject.messageOwner.fwd_from.from_id;
                }
            }
            String name = "";
            TLObject photo = null;
            this.avatarDrawable = null;
            if (fromId > 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(fromId));
                if (user != null) {
                    if (user.photo != null) {
                        photo = user.photo.photo_small;
                    }
                    this.avatarDrawable = new AvatarDrawable(user);
                    name = UserObject.getUserName(user);
                }
            } else {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-fromId));
                if (chat != null) {
                    if (chat.photo != null) {
                        photo = chat.photo.photo_small;
                    }
                    this.avatarDrawable = new AvatarDrawable(chat);
                    name = chat.title;
                }
            }
            if (this.avatarDrawable != null) {
                this.avatarImageView.setImage(photo, null, this.avatarDrawable);
                this.nameTextView.setText(name);
                return;
            }
            this.avatarImageView.setImageDrawable(null);
        }
    }

    private void positionMarker(Location location) {
        if (location != null) {
            this.myLocation = new Location(location);
            if (this.messageObject != null) {
                if (this.userLocation != null && this.distanceTextView != null) {
                    if (location.distanceTo(this.userLocation) < 1000.0f) {
                        this.distanceTextView.setText(String.format("%d %s", new Object[]{Integer.valueOf((int) distance), LocaleController.getString("MetersAway", R.string.MetersAway)}));
                        return;
                    }
                    this.distanceTextView.setText(String.format("%.2f %s", new Object[]{Float.valueOf(distance / 1000.0f), LocaleController.getString("KMetersAway", R.string.KMetersAway)}));
                }
            } else if (this.googleMap != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (this.adapter != null) {
                    this.adapter.searchGooglePlacesWithQuery(null, this.myLocation);
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
                }
            }
        }
    }

    public void setMessageObject(MessageObject message) {
        this.messageObject = message;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0) {
                updateUserData();
            }
        } else if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.locationPermissionGranted && this.googleMap != null) {
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mapView != null && this.mapsInitialized) {
            try {
                this.mapView.onPause();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = false;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.mapView != null && this.mapsInitialized) {
            try {
                this.mapView.onResume();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        this.onResumeCalled = true;
        if (this.googleMap != null) {
            try {
                this.googleMap.setMyLocationEnabled(true);
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
        updateUserData();
        fixLayoutInternal(true);
        if (this.checkPermission && VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                if (activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                }
            }
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        if (this.mapView != null && this.mapsInitialized) {
            this.mapView.onLowMemory();
        }
    }

    public void setDelegate(LocationActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private void updateSearchInterface() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor(int color) {
                LocationActivity.this.updateUserData();
            }
        };
        r10 = new ThemeDescription[49];
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r10[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r10[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r10[14] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon);
        r10[15] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground);
        r10[16] = new ThemeDescription(this.locationButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground);
        r10[17] = new ThemeDescription(this.bottomView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhite);
        r10[18] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[19] = new ThemeDescription(this.distanceTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r10[20] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon);
        r10[21] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground);
        r10[22] = new ThemeDescription(this.routeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground);
        r10[23] = new ThemeDescription(this.markerXImageView, 0, null, null, null, null, Theme.key_location_markerX);
        r10[24] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        r10[26] = new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable}, сellDelegate, Theme.key_avatar_text);
        r10[27] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        r10[28] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange);
        r10[29] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundViolet);
        r10[30] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundGreen);
        r10[31] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundCyan);
        r10[32] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundBlue);
        r10[33] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundPink);
        r10[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLocationIcon);
        r10[35] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SendLocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_location_sendLocationBackground);
        r10[36] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText7);
        r10[37] = new ThemeDescription(this.listView, 0, new Class[]{SendLocationCell.class}, new String[]{"accurateTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[38] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[39] = new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[40] = new ThemeDescription(this.listView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[41] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LocationCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[42] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[43] = new ThemeDescription(this.searchListView, 0, new Class[]{LocationCell.class}, new String[]{"addressTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[44] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r10[45] = new ThemeDescription(this.listView, 0, new Class[]{LocationLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[46] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[47] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r10[48] = new ThemeDescription(this.listView, 0, new Class[]{LocationPoweredCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        return r10;
    }
}
