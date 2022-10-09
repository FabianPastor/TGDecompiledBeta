package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.util.Consumer;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.UiSettings;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.GoogleMapsProvider;
import org.telegram.messenger.IMapsProvider;
/* loaded from: classes.dex */
public class GoogleMapsProvider implements IMapsProvider {
    @Override // org.telegram.messenger.IMapsProvider
    public String getMapsAppPackageName() {
        return "com.google.android.apps.maps";
    }

    @Override // org.telegram.messenger.IMapsProvider
    public void initializeMaps(Context context) {
        MapsInitializer.initialize(context);
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.IMapView onCreateMapView(Context context) {
        return new GoogleMapView(context);
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ICameraUpdate newCameraUpdateLatLng(IMapsProvider.LatLng latLng) {
        return new GoogleCameraUpdate(CameraUpdateFactory.newLatLng(new LatLng(latLng.latitude, latLng.longitude)));
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ICameraUpdate newCameraUpdateLatLngZoom(IMapsProvider.LatLng latLng, float f) {
        return new GoogleCameraUpdate(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), f));
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ICameraUpdate newCameraUpdateLatLngBounds(IMapsProvider.ILatLngBounds iLatLngBounds, int i) {
        return new GoogleCameraUpdate(CameraUpdateFactory.newLatLngBounds(((GoogleLatLngBounds) iLatLngBounds).bounds, i));
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ILatLngBoundsBuilder onCreateLatLngBoundsBuilder() {
        return new GoogleLatLngBoundsBuilder();
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.IMapStyleOptions loadRawResourceStyle(Context context, int i) {
        return new GoogleMapStyleOptions(MapStyleOptions.loadRawResourceStyle(context, i));
    }

    @Override // org.telegram.messenger.IMapsProvider
    public int getInstallMapsString() {
        return R.string.InstallGoogleMaps;
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.IMarkerOptions onCreateMarkerOptions() {
        return new GoogleMarkerOptions();
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ICircleOptions onCreateCircleOptions() {
        return new GoogleCircleOptions();
    }

    /* loaded from: classes.dex */
    public static final class GoogleMapImpl implements IMapsProvider.IMap {
        private GoogleMap googleMap;
        private Map<Circle, GoogleCircle> implToAbsCircleMap;
        private Map<Marker, GoogleMarker> implToAbsMarkerMap;

        private GoogleMapImpl(GoogleMap googleMap) {
            this.implToAbsMarkerMap = new HashMap();
            this.implToAbsCircleMap = new HashMap();
            this.googleMap = googleMap;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setMapType(int i) {
            if (i == 0) {
                this.googleMap.setMapType(1);
            } else if (i == 1) {
                this.googleMap.setMapType(2);
            } else if (i != 2) {
            } else {
                this.googleMap.setMapType(4);
            }
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public float getMaxZoomLevel() {
            return this.googleMap.getMaxZoomLevel();
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        @SuppressLint({"MissingPermission"})
        public void setMyLocationEnabled(boolean z) {
            this.googleMap.setMyLocationEnabled(z);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.IUISettings getUiSettings() {
            return new GoogleUISettings(this.googleMap.getUiSettings());
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnCameraMoveStartedListener(final IMapsProvider.OnCameraMoveStartedListener onCameraMoveStartedListener) {
            this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() { // from class: org.telegram.messenger.GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda1
                @Override // com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
                public final void onCameraMoveStarted(int i) {
                    GoogleMapsProvider.GoogleMapImpl.lambda$setOnCameraMoveStartedListener$0(IMapsProvider.OnCameraMoveStartedListener.this, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$setOnCameraMoveStartedListener$0(IMapsProvider.OnCameraMoveStartedListener onCameraMoveStartedListener, int i) {
            int i2 = 3;
            if (i == 2) {
                i2 = 2;
            } else if (i != 3) {
                i2 = 1;
            }
            onCameraMoveStartedListener.onCameraMoveStarted(i2);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.CameraPosition getCameraPosition() {
            CameraPosition cameraPosition = this.googleMap.getCameraPosition();
            LatLng latLng = cameraPosition.target;
            return new IMapsProvider.CameraPosition(new IMapsProvider.LatLng(latLng.latitude, latLng.longitude), cameraPosition.zoom);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnMapLoadedCallback(final Runnable runnable) {
            GoogleMap googleMap = this.googleMap;
            runnable.getClass();
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() { // from class: org.telegram.messenger.GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda2
                @Override // com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
                public final void onMapLoaded() {
                    runnable.run();
                }
            });
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.IProjection getProjection() {
            return new GoogleProjection(this.googleMap.getProjection());
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setPadding(int i, int i2, int i3, int i4) {
            this.googleMap.setPadding(i, i2, i3, i4);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setMapStyle(IMapsProvider.IMapStyleOptions iMapStyleOptions) {
            this.googleMap.setMapStyle(iMapStyleOptions == null ? null : ((GoogleMapStyleOptions) iMapStyleOptions).mapStyleOptions);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.IMarker addMarker(IMapsProvider.IMarkerOptions iMarkerOptions) {
            Marker addMarker = this.googleMap.addMarker(((GoogleMarkerOptions) iMarkerOptions).markerOptions);
            GoogleMarker googleMarker = new GoogleMarker(addMarker);
            this.implToAbsMarkerMap.put(addMarker, googleMarker);
            return googleMarker;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.ICircle addCircle(IMapsProvider.ICircleOptions iCircleOptions) {
            Circle addCircle = this.googleMap.addCircle(((GoogleCircleOptions) iCircleOptions).circleOptions);
            GoogleCircle googleCircle = new GoogleCircle(addCircle);
            this.implToAbsCircleMap.put(addCircle, googleCircle);
            return googleCircle;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnMyLocationChangeListener(final Consumer<Location> consumer) {
            GoogleMap googleMap = this.googleMap;
            consumer.getClass();
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() { // from class: org.telegram.messenger.GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda4
                @Override // com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener
                public final void onMyLocationChange(Location location) {
                    Consumer.this.accept(location);
                }
            });
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnMarkerClickListener(final IMapsProvider.OnMarkerClickListener onMarkerClickListener) {
            this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // from class: org.telegram.messenger.GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda3
                @Override // com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
                public final boolean onMarkerClick(Marker marker) {
                    boolean lambda$setOnMarkerClickListener$1;
                    lambda$setOnMarkerClickListener$1 = GoogleMapsProvider.GoogleMapImpl.this.lambda$setOnMarkerClickListener$1(onMarkerClickListener, marker);
                    return lambda$setOnMarkerClickListener$1;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$setOnMarkerClickListener$1(IMapsProvider.OnMarkerClickListener onMarkerClickListener, Marker marker) {
            GoogleMarker googleMarker = this.implToAbsMarkerMap.get(marker);
            if (googleMarker == null) {
                googleMarker = new GoogleMarker(marker);
                this.implToAbsMarkerMap.put(marker, googleMarker);
            }
            return onMarkerClickListener.onClick(googleMarker);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnCameraMoveListener(final Runnable runnable) {
            GoogleMap googleMap = this.googleMap;
            runnable.getClass();
            googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() { // from class: org.telegram.messenger.GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda0
                @Override // com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
                public final void onCameraMove() {
                    runnable.run();
                }
            });
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void animateCamera(IMapsProvider.ICameraUpdate iCameraUpdate) {
            this.googleMap.animateCamera(((GoogleCameraUpdate) iCameraUpdate).cameraUpdate);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void animateCamera(IMapsProvider.ICameraUpdate iCameraUpdate, final IMapsProvider.ICancelableCallback iCancelableCallback) {
            this.googleMap.animateCamera(((GoogleCameraUpdate) iCameraUpdate).cameraUpdate, iCancelableCallback == null ? null : new GoogleMap.CancelableCallback() { // from class: org.telegram.messenger.GoogleMapsProvider.GoogleMapImpl.1
                @Override // com.google.android.gms.maps.GoogleMap.CancelableCallback
                public void onFinish() {
                    iCancelableCallback.onFinish();
                }

                @Override // com.google.android.gms.maps.GoogleMap.CancelableCallback
                public void onCancel() {
                    iCancelableCallback.onCancel();
                }
            });
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void animateCamera(IMapsProvider.ICameraUpdate iCameraUpdate, int i, final IMapsProvider.ICancelableCallback iCancelableCallback) {
            this.googleMap.animateCamera(((GoogleCameraUpdate) iCameraUpdate).cameraUpdate, i, iCancelableCallback == null ? null : new GoogleMap.CancelableCallback() { // from class: org.telegram.messenger.GoogleMapsProvider.GoogleMapImpl.2
                @Override // com.google.android.gms.maps.GoogleMap.CancelableCallback
                public void onFinish() {
                    iCancelableCallback.onFinish();
                }

                @Override // com.google.android.gms.maps.GoogleMap.CancelableCallback
                public void onCancel() {
                    iCancelableCallback.onCancel();
                }
            });
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void moveCamera(IMapsProvider.ICameraUpdate iCameraUpdate) {
            this.googleMap.moveCamera(((GoogleCameraUpdate) iCameraUpdate).cameraUpdate);
        }

        /* loaded from: classes.dex */
        public final class GoogleMarker implements IMapsProvider.IMarker {
            private Marker marker;

            private GoogleMarker(Marker marker) {
                this.marker = marker;
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public Object getTag() {
                return this.marker.getTag();
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setTag(Object obj) {
                this.marker.setTag(obj);
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public IMapsProvider.LatLng getPosition() {
                LatLng position = this.marker.getPosition();
                return new IMapsProvider.LatLng(position.latitude, position.longitude);
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setPosition(IMapsProvider.LatLng latLng) {
                this.marker.setPosition(new LatLng(latLng.latitude, latLng.longitude));
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setRotation(int i) {
                this.marker.setRotation(i);
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setIcon(Bitmap bitmap) {
                this.marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setIcon(int i) {
                this.marker.setIcon(BitmapDescriptorFactory.fromResource(i));
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void remove() {
                this.marker.remove();
                GoogleMapImpl.this.implToAbsMarkerMap.remove(this.marker);
            }
        }

        /* loaded from: classes.dex */
        public final class GoogleCircle implements IMapsProvider.ICircle {
            private Circle circle;

            private GoogleCircle(Circle circle) {
                this.circle = circle;
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void setStrokeColor(int i) {
                this.circle.setStrokeColor(i);
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void setFillColor(int i) {
                this.circle.setFillColor(i);
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void setRadius(double d) {
                this.circle.setRadius(d);
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public double getRadius() {
                return this.circle.getRadius();
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void setCenter(IMapsProvider.LatLng latLng) {
                this.circle.setCenter(new LatLng(latLng.latitude, latLng.longitude));
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void remove() {
                this.circle.remove();
                GoogleMapImpl.this.implToAbsCircleMap.remove(this.circle);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class GoogleProjection implements IMapsProvider.IProjection {
        private Projection projection;

        private GoogleProjection(Projection projection) {
            this.projection = projection;
        }

        @Override // org.telegram.messenger.IMapsProvider.IProjection
        public Point toScreenLocation(IMapsProvider.LatLng latLng) {
            return this.projection.toScreenLocation(new LatLng(latLng.latitude, latLng.longitude));
        }
    }

    /* loaded from: classes.dex */
    public static final class GoogleUISettings implements IMapsProvider.IUISettings {
        private UiSettings uiSettings;

        private GoogleUISettings(UiSettings uiSettings) {
            this.uiSettings = uiSettings;
        }

        @Override // org.telegram.messenger.IMapsProvider.IUISettings
        public void setMyLocationButtonEnabled(boolean z) {
            this.uiSettings.setMyLocationButtonEnabled(z);
        }

        @Override // org.telegram.messenger.IMapsProvider.IUISettings
        public void setZoomControlsEnabled(boolean z) {
            this.uiSettings.setZoomControlsEnabled(z);
        }

        @Override // org.telegram.messenger.IMapsProvider.IUISettings
        public void setCompassEnabled(boolean z) {
            this.uiSettings.setCompassEnabled(z);
        }
    }

    /* loaded from: classes.dex */
    public static final class GoogleCircleOptions implements IMapsProvider.ICircleOptions {
        private CircleOptions circleOptions;

        private GoogleCircleOptions() {
            this.circleOptions = new CircleOptions();
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions center(IMapsProvider.LatLng latLng) {
            this.circleOptions.center(new LatLng(latLng.latitude, latLng.longitude));
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions radius(double d) {
            this.circleOptions.radius(d);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions strokeColor(int i) {
            this.circleOptions.strokeColor(i);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions fillColor(int i) {
            this.circleOptions.fillColor(i);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions strokePattern(List<IMapsProvider.PatternItem> list) {
            ArrayList arrayList = new ArrayList();
            for (IMapsProvider.PatternItem patternItem : list) {
                if (patternItem instanceof IMapsProvider.PatternItem.Gap) {
                    arrayList.add(new Gap(((IMapsProvider.PatternItem.Gap) patternItem).length));
                } else if (patternItem instanceof IMapsProvider.PatternItem.Dash) {
                    arrayList.add(new Dash(((IMapsProvider.PatternItem.Dash) patternItem).length));
                }
            }
            this.circleOptions.strokePattern(arrayList);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions strokeWidth(int i) {
            this.circleOptions.strokeWidth(i);
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static final class GoogleMarkerOptions implements IMapsProvider.IMarkerOptions {
        private MarkerOptions markerOptions;

        private GoogleMarkerOptions() {
            this.markerOptions = new MarkerOptions();
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions position(IMapsProvider.LatLng latLng) {
            this.markerOptions.position(new LatLng(latLng.latitude, latLng.longitude));
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions icon(Bitmap bitmap) {
            this.markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions icon(int i) {
            this.markerOptions.icon(BitmapDescriptorFactory.fromResource(i));
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions anchor(float f, float f2) {
            this.markerOptions.anchor(f, f2);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions title(String str) {
            this.markerOptions.title(str);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions snippet(String str) {
            this.markerOptions.snippet(str);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions flat(boolean z) {
            this.markerOptions.flat(z);
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static final class GoogleLatLngBoundsBuilder implements IMapsProvider.ILatLngBoundsBuilder {
        private LatLngBounds.Builder builder;

        private GoogleLatLngBoundsBuilder() {
            this.builder = new LatLngBounds.Builder();
        }

        @Override // org.telegram.messenger.IMapsProvider.ILatLngBoundsBuilder
        public IMapsProvider.ILatLngBoundsBuilder include(IMapsProvider.LatLng latLng) {
            this.builder.include(new LatLng(latLng.latitude, latLng.longitude));
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ILatLngBoundsBuilder
        public IMapsProvider.ILatLngBounds build() {
            return new GoogleLatLngBounds(this.builder.build());
        }
    }

    /* loaded from: classes.dex */
    public static final class GoogleLatLngBounds implements IMapsProvider.ILatLngBounds {
        private LatLngBounds bounds;

        private GoogleLatLngBounds(LatLngBounds latLngBounds) {
            this.bounds = latLngBounds;
        }

        @Override // org.telegram.messenger.IMapsProvider.ILatLngBounds
        public IMapsProvider.LatLng getCenter() {
            LatLng center = this.bounds.getCenter();
            return new IMapsProvider.LatLng(center.latitude, center.longitude);
        }
    }

    /* loaded from: classes.dex */
    public static final class GoogleMapView implements IMapsProvider.IMapView {
        private IMapsProvider.ITouchInterceptor dispatchInterceptor;
        private IMapsProvider.ITouchInterceptor interceptInterceptor;
        private MapView mapView;
        private Runnable onLayoutListener;

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public /* synthetic */ GLSurfaceView getGlSurfaceView() {
            return IMapsProvider.IMapView.CC.$default$getGlSurfaceView(this);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.messenger.GoogleMapsProvider$GoogleMapView$1  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass1 extends MapView {
            AnonymousClass1(Context context) {
                super(context);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (GoogleMapView.this.dispatchInterceptor != null) {
                    return GoogleMapView.this.dispatchInterceptor.onInterceptTouchEvent(motionEvent, new IMapsProvider.ICallableMethod() { // from class: org.telegram.messenger.GoogleMapsProvider$GoogleMapView$1$$ExternalSyntheticLambda1
                        @Override // org.telegram.messenger.IMapsProvider.ICallableMethod
                        public final Object call(Object obj) {
                            Boolean lambda$dispatchTouchEvent$0;
                            lambda$dispatchTouchEvent$0 = GoogleMapsProvider.GoogleMapView.AnonymousClass1.this.lambda$dispatchTouchEvent$0((MotionEvent) obj);
                            return lambda$dispatchTouchEvent$0;
                        }
                    });
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ Boolean lambda$dispatchTouchEvent$0(MotionEvent motionEvent) {
                return Boolean.valueOf(super.dispatchTouchEvent(motionEvent));
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (GoogleMapView.this.interceptInterceptor != null) {
                    return GoogleMapView.this.interceptInterceptor.onInterceptTouchEvent(motionEvent, new IMapsProvider.ICallableMethod() { // from class: org.telegram.messenger.GoogleMapsProvider$GoogleMapView$1$$ExternalSyntheticLambda0
                        @Override // org.telegram.messenger.IMapsProvider.ICallableMethod
                        public final Object call(Object obj) {
                            Boolean lambda$onInterceptTouchEvent$1;
                            lambda$onInterceptTouchEvent$1 = GoogleMapsProvider.GoogleMapView.AnonymousClass1.this.lambda$onInterceptTouchEvent$1((MotionEvent) obj);
                            return lambda$onInterceptTouchEvent$1;
                        }
                    });
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ Boolean lambda$onInterceptTouchEvent$1(MotionEvent motionEvent) {
                return Boolean.valueOf(super.onInterceptTouchEvent(motionEvent));
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (GoogleMapView.this.onLayoutListener != null) {
                    GoogleMapView.this.onLayoutListener.run();
                }
            }
        }

        private GoogleMapView(Context context) {
            this.mapView = new AnonymousClass1(context);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void setOnDispatchTouchEventInterceptor(IMapsProvider.ITouchInterceptor iTouchInterceptor) {
            this.dispatchInterceptor = iTouchInterceptor;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void setOnInterceptTouchEventInterceptor(IMapsProvider.ITouchInterceptor iTouchInterceptor) {
            this.interceptInterceptor = iTouchInterceptor;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void setOnLayoutListener(Runnable runnable) {
            this.onLayoutListener = runnable;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public View getView() {
            return this.mapView;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$getMapAsync$0(Consumer consumer, GoogleMap googleMap) {
            consumer.accept(new GoogleMapImpl(googleMap));
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void getMapAsync(final Consumer<IMapsProvider.IMap> consumer) {
            this.mapView.getMapAsync(new OnMapReadyCallback() { // from class: org.telegram.messenger.GoogleMapsProvider$GoogleMapView$$ExternalSyntheticLambda0
                @Override // com.google.android.gms.maps.OnMapReadyCallback
                public final void onMapReady(GoogleMap googleMap) {
                    GoogleMapsProvider.GoogleMapView.lambda$getMapAsync$0(Consumer.this, googleMap);
                }
            });
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onPause() {
            this.mapView.onPause();
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onResume() {
            this.mapView.onResume();
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onCreate(Bundle bundle) {
            this.mapView.onCreate(bundle);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onDestroy() {
            this.mapView.onDestroy();
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onLowMemory() {
            this.mapView.onLowMemory();
        }
    }

    /* loaded from: classes.dex */
    public static final class GoogleCameraUpdate implements IMapsProvider.ICameraUpdate {
        private CameraUpdate cameraUpdate;

        private GoogleCameraUpdate(CameraUpdate cameraUpdate) {
            this.cameraUpdate = cameraUpdate;
        }
    }

    /* loaded from: classes.dex */
    public static final class GoogleMapStyleOptions implements IMapsProvider.IMapStyleOptions {
        private MapStyleOptions mapStyleOptions;

        private GoogleMapStyleOptions(MapStyleOptions mapStyleOptions) {
            this.mapStyleOptions = mapStyleOptions;
        }
    }
}
