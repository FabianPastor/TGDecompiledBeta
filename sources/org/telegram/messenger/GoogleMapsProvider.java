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
import org.telegram.messenger.IMapsProvider;

public class GoogleMapsProvider implements IMapsProvider {
    public String getMapsAppPackageName() {
        return "com.google.android.apps.maps";
    }

    public void initializeMaps(Context context) {
        MapsInitializer.initialize(context);
    }

    public IMapsProvider.IMapView onCreateMapView(Context context) {
        return new GoogleMapView(context);
    }

    public IMapsProvider.ICameraUpdate newCameraUpdateLatLng(IMapsProvider.LatLng latLng) {
        return new GoogleCameraUpdate(CameraUpdateFactory.newLatLng(new LatLng(latLng.latitude, latLng.longitude)));
    }

    public IMapsProvider.ICameraUpdate newCameraUpdateLatLngZoom(IMapsProvider.LatLng latLng, float f) {
        return new GoogleCameraUpdate(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), f));
    }

    public IMapsProvider.ICameraUpdate newCameraUpdateLatLngBounds(IMapsProvider.ILatLngBounds iLatLngBounds, int i) {
        return new GoogleCameraUpdate(CameraUpdateFactory.newLatLngBounds(((GoogleLatLngBounds) iLatLngBounds).bounds, i));
    }

    public IMapsProvider.ILatLngBoundsBuilder onCreateLatLngBoundsBuilder() {
        return new GoogleLatLngBoundsBuilder();
    }

    public IMapsProvider.IMapStyleOptions loadRawResourceStyle(Context context, int i) {
        return new GoogleMapStyleOptions(MapStyleOptions.loadRawResourceStyle(context, i));
    }

    public int getInstallMapsString() {
        return R.string.InstallGoogleMaps;
    }

    public IMapsProvider.IMarkerOptions onCreateMarkerOptions() {
        return new GoogleMarkerOptions();
    }

    public IMapsProvider.ICircleOptions onCreateCircleOptions() {
        return new GoogleCircleOptions();
    }

    public static final class GoogleMapImpl implements IMapsProvider.IMap {
        private GoogleMap googleMap;
        /* access modifiers changed from: private */
        public Map<Circle, GoogleCircle> implToAbsCircleMap;
        /* access modifiers changed from: private */
        public Map<Marker, GoogleMarker> implToAbsMarkerMap;

        private GoogleMapImpl(GoogleMap googleMap2) {
            this.implToAbsMarkerMap = new HashMap();
            this.implToAbsCircleMap = new HashMap();
            this.googleMap = googleMap2;
        }

        public void setMapType(int i) {
            if (i == 0) {
                this.googleMap.setMapType(1);
            } else if (i == 1) {
                this.googleMap.setMapType(2);
            } else if (i == 2) {
                this.googleMap.setMapType(4);
            }
        }

        public float getMaxZoomLevel() {
            return this.googleMap.getMaxZoomLevel();
        }

        @SuppressLint({"MissingPermission"})
        public void setMyLocationEnabled(boolean z) {
            this.googleMap.setMyLocationEnabled(z);
        }

        public IMapsProvider.IUISettings getUiSettings() {
            return new GoogleUISettings(this.googleMap.getUiSettings());
        }

        public void setOnCameraMoveStartedListener(IMapsProvider.OnCameraMoveStartedListener onCameraMoveStartedListener) {
            this.googleMap.setOnCameraMoveStartedListener(new GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda1(onCameraMoveStartedListener));
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$setOnCameraMoveStartedListener$0(IMapsProvider.OnCameraMoveStartedListener onCameraMoveStartedListener, int i) {
            int i2 = 3;
            if (i == 2) {
                i2 = 2;
            } else if (i != 3) {
                i2 = 1;
            }
            onCameraMoveStartedListener.onCameraMoveStarted(i2);
        }

        public IMapsProvider.CameraPosition getCameraPosition() {
            CameraPosition cameraPosition = this.googleMap.getCameraPosition();
            LatLng latLng = cameraPosition.target;
            return new IMapsProvider.CameraPosition(new IMapsProvider.LatLng(latLng.latitude, latLng.longitude), cameraPosition.zoom);
        }

        public void setOnMapLoadedCallback(Runnable runnable) {
            GoogleMap googleMap2 = this.googleMap;
            runnable.getClass();
            googleMap2.setOnMapLoadedCallback(new GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda2(runnable));
        }

        public IMapsProvider.IProjection getProjection() {
            return new GoogleProjection(this.googleMap.getProjection());
        }

        public void setPadding(int i, int i2, int i3, int i4) {
            this.googleMap.setPadding(i, i2, i3, i4);
        }

        public void setMapStyle(IMapsProvider.IMapStyleOptions iMapStyleOptions) {
            this.googleMap.setMapStyle(iMapStyleOptions == null ? null : ((GoogleMapStyleOptions) iMapStyleOptions).mapStyleOptions);
        }

        public IMapsProvider.IMarker addMarker(IMapsProvider.IMarkerOptions iMarkerOptions) {
            Marker addMarker = this.googleMap.addMarker(((GoogleMarkerOptions) iMarkerOptions).markerOptions);
            GoogleMarker googleMarker = new GoogleMarker(addMarker);
            this.implToAbsMarkerMap.put(addMarker, googleMarker);
            return googleMarker;
        }

        public IMapsProvider.ICircle addCircle(IMapsProvider.ICircleOptions iCircleOptions) {
            Circle addCircle = this.googleMap.addCircle(((GoogleCircleOptions) iCircleOptions).circleOptions);
            GoogleCircle googleCircle = new GoogleCircle(addCircle);
            this.implToAbsCircleMap.put(addCircle, googleCircle);
            return googleCircle;
        }

        public void setOnMyLocationChangeListener(Consumer<Location> consumer) {
            GoogleMap googleMap2 = this.googleMap;
            consumer.getClass();
            googleMap2.setOnMyLocationChangeListener(new GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda4(consumer));
        }

        public void setOnMarkerClickListener(IMapsProvider.OnMarkerClickListener onMarkerClickListener) {
            this.googleMap.setOnMarkerClickListener(new GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda3(this, onMarkerClickListener));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$setOnMarkerClickListener$1(IMapsProvider.OnMarkerClickListener onMarkerClickListener, Marker marker) {
            GoogleMarker googleMarker = this.implToAbsMarkerMap.get(marker);
            if (googleMarker == null) {
                googleMarker = new GoogleMarker(marker);
                this.implToAbsMarkerMap.put(marker, googleMarker);
            }
            return onMarkerClickListener.onClick(googleMarker);
        }

        public void setOnCameraMoveListener(Runnable runnable) {
            GoogleMap googleMap2 = this.googleMap;
            runnable.getClass();
            googleMap2.setOnCameraMoveListener(new GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda0(runnable));
        }

        public void animateCamera(IMapsProvider.ICameraUpdate iCameraUpdate) {
            this.googleMap.animateCamera(((GoogleCameraUpdate) iCameraUpdate).cameraUpdate);
        }

        public void animateCamera(IMapsProvider.ICameraUpdate iCameraUpdate, final IMapsProvider.ICancelableCallback iCancelableCallback) {
            this.googleMap.animateCamera(((GoogleCameraUpdate) iCameraUpdate).cameraUpdate, iCancelableCallback == null ? null : new GoogleMap.CancelableCallback() {
                public void onFinish() {
                    iCancelableCallback.onFinish();
                }

                public void onCancel() {
                    iCancelableCallback.onCancel();
                }
            });
        }

        public void animateCamera(IMapsProvider.ICameraUpdate iCameraUpdate, int i, final IMapsProvider.ICancelableCallback iCancelableCallback) {
            this.googleMap.animateCamera(((GoogleCameraUpdate) iCameraUpdate).cameraUpdate, i, iCancelableCallback == null ? null : new GoogleMap.CancelableCallback() {
                public void onFinish() {
                    iCancelableCallback.onFinish();
                }

                public void onCancel() {
                    iCancelableCallback.onCancel();
                }
            });
        }

        public void moveCamera(IMapsProvider.ICameraUpdate iCameraUpdate) {
            this.googleMap.moveCamera(((GoogleCameraUpdate) iCameraUpdate).cameraUpdate);
        }

        public final class GoogleMarker implements IMapsProvider.IMarker {
            private Marker marker;

            private GoogleMarker(Marker marker2) {
                this.marker = marker2;
            }

            public Object getTag() {
                return this.marker.getTag();
            }

            public void setTag(Object obj) {
                this.marker.setTag(obj);
            }

            public IMapsProvider.LatLng getPosition() {
                LatLng position = this.marker.getPosition();
                return new IMapsProvider.LatLng(position.latitude, position.longitude);
            }

            public void setPosition(IMapsProvider.LatLng latLng) {
                this.marker.setPosition(new LatLng(latLng.latitude, latLng.longitude));
            }

            public void setRotation(int i) {
                this.marker.setRotation((float) i);
            }

            public void setIcon(Bitmap bitmap) {
                this.marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            }

            public void setIcon(int i) {
                this.marker.setIcon(BitmapDescriptorFactory.fromResource(i));
            }

            public void remove() {
                this.marker.remove();
                GoogleMapImpl.this.implToAbsMarkerMap.remove(this.marker);
            }
        }

        public final class GoogleCircle implements IMapsProvider.ICircle {
            private Circle circle;

            private GoogleCircle(Circle circle2) {
                this.circle = circle2;
            }

            public void setStrokeColor(int i) {
                this.circle.setStrokeColor(i);
            }

            public void setFillColor(int i) {
                this.circle.setFillColor(i);
            }

            public void setRadius(double d) {
                this.circle.setRadius(d);
            }

            public double getRadius() {
                return this.circle.getRadius();
            }

            public void setCenter(IMapsProvider.LatLng latLng) {
                this.circle.setCenter(new LatLng(latLng.latitude, latLng.longitude));
            }

            public void remove() {
                this.circle.remove();
                GoogleMapImpl.this.implToAbsCircleMap.remove(this.circle);
            }
        }
    }

    public static final class GoogleProjection implements IMapsProvider.IProjection {
        private Projection projection;

        private GoogleProjection(Projection projection2) {
            this.projection = projection2;
        }

        public Point toScreenLocation(IMapsProvider.LatLng latLng) {
            return this.projection.toScreenLocation(new LatLng(latLng.latitude, latLng.longitude));
        }
    }

    public static final class GoogleUISettings implements IMapsProvider.IUISettings {
        private UiSettings uiSettings;

        private GoogleUISettings(UiSettings uiSettings2) {
            this.uiSettings = uiSettings2;
        }

        public void setMyLocationButtonEnabled(boolean z) {
            this.uiSettings.setMyLocationButtonEnabled(z);
        }

        public void setZoomControlsEnabled(boolean z) {
            this.uiSettings.setZoomControlsEnabled(z);
        }

        public void setCompassEnabled(boolean z) {
            this.uiSettings.setCompassEnabled(z);
        }
    }

    public static final class GoogleCircleOptions implements IMapsProvider.ICircleOptions {
        /* access modifiers changed from: private */
        public CircleOptions circleOptions;

        private GoogleCircleOptions() {
            this.circleOptions = new CircleOptions();
        }

        public IMapsProvider.ICircleOptions center(IMapsProvider.LatLng latLng) {
            this.circleOptions.center(new LatLng(latLng.latitude, latLng.longitude));
            return this;
        }

        public IMapsProvider.ICircleOptions radius(double d) {
            this.circleOptions.radius(d);
            return this;
        }

        public IMapsProvider.ICircleOptions strokeColor(int i) {
            this.circleOptions.strokeColor(i);
            return this;
        }

        public IMapsProvider.ICircleOptions fillColor(int i) {
            this.circleOptions.fillColor(i);
            return this;
        }

        public IMapsProvider.ICircleOptions strokePattern(List<IMapsProvider.PatternItem> list) {
            ArrayList arrayList = new ArrayList();
            for (IMapsProvider.PatternItem next : list) {
                if (next instanceof IMapsProvider.PatternItem.Gap) {
                    arrayList.add(new Gap((float) ((IMapsProvider.PatternItem.Gap) next).length));
                } else if (next instanceof IMapsProvider.PatternItem.Dash) {
                    arrayList.add(new Dash((float) ((IMapsProvider.PatternItem.Dash) next).length));
                }
            }
            this.circleOptions.strokePattern(arrayList);
            return this;
        }

        public IMapsProvider.ICircleOptions strokeWidth(int i) {
            this.circleOptions.strokeWidth((float) i);
            return this;
        }
    }

    public static final class GoogleMarkerOptions implements IMapsProvider.IMarkerOptions {
        /* access modifiers changed from: private */
        public MarkerOptions markerOptions;

        private GoogleMarkerOptions() {
            this.markerOptions = new MarkerOptions();
        }

        public IMapsProvider.IMarkerOptions position(IMapsProvider.LatLng latLng) {
            this.markerOptions.position(new LatLng(latLng.latitude, latLng.longitude));
            return this;
        }

        public IMapsProvider.IMarkerOptions icon(Bitmap bitmap) {
            this.markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            return this;
        }

        public IMapsProvider.IMarkerOptions icon(int i) {
            this.markerOptions.icon(BitmapDescriptorFactory.fromResource(i));
            return this;
        }

        public IMapsProvider.IMarkerOptions anchor(float f, float f2) {
            this.markerOptions.anchor(f, f2);
            return this;
        }

        public IMapsProvider.IMarkerOptions title(String str) {
            this.markerOptions.title(str);
            return this;
        }

        public IMapsProvider.IMarkerOptions snippet(String str) {
            this.markerOptions.snippet(str);
            return this;
        }

        public IMapsProvider.IMarkerOptions flat(boolean z) {
            this.markerOptions.flat(z);
            return this;
        }
    }

    public static final class GoogleLatLngBoundsBuilder implements IMapsProvider.ILatLngBoundsBuilder {
        private LatLngBounds.Builder builder;

        private GoogleLatLngBoundsBuilder() {
            this.builder = new LatLngBounds.Builder();
        }

        public IMapsProvider.ILatLngBoundsBuilder include(IMapsProvider.LatLng latLng) {
            this.builder.include(new LatLng(latLng.latitude, latLng.longitude));
            return this;
        }

        public IMapsProvider.ILatLngBounds build() {
            return new GoogleLatLngBounds(this.builder.build());
        }
    }

    public static final class GoogleLatLngBounds implements IMapsProvider.ILatLngBounds {
        /* access modifiers changed from: private */
        public LatLngBounds bounds;

        private GoogleLatLngBounds(LatLngBounds latLngBounds) {
            this.bounds = latLngBounds;
        }

        public IMapsProvider.LatLng getCenter() {
            LatLng center = this.bounds.getCenter();
            return new IMapsProvider.LatLng(center.latitude, center.longitude);
        }
    }

    public static final class GoogleMapView implements IMapsProvider.IMapView {
        /* access modifiers changed from: private */
        public IMapsProvider.ITouchInterceptor dispatchInterceptor;
        /* access modifiers changed from: private */
        public IMapsProvider.ITouchInterceptor interceptInterceptor;
        private MapView mapView;
        /* access modifiers changed from: private */
        public Runnable onLayoutListener;

        public /* synthetic */ GLSurfaceView getGlSurfaceView() {
            return IMapsProvider.IMapView.CC.$default$getGlSurfaceView(this);
        }

        private GoogleMapView(Context context) {
            this.mapView = new MapView(context) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    if (GoogleMapView.this.dispatchInterceptor != null) {
                        return GoogleMapView.this.dispatchInterceptor.onInterceptTouchEvent(motionEvent, new GoogleMapsProvider$GoogleMapView$1$$ExternalSyntheticLambda1(this));
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ Boolean lambda$dispatchTouchEvent$0(MotionEvent motionEvent) {
                    return Boolean.valueOf(super.dispatchTouchEvent(motionEvent));
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (GoogleMapView.this.interceptInterceptor != null) {
                        return GoogleMapView.this.interceptInterceptor.onInterceptTouchEvent(motionEvent, new GoogleMapsProvider$GoogleMapView$1$$ExternalSyntheticLambda0(this));
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ Boolean lambda$onInterceptTouchEvent$1(MotionEvent motionEvent) {
                    return Boolean.valueOf(super.onInterceptTouchEvent(motionEvent));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    if (GoogleMapView.this.onLayoutListener != null) {
                        GoogleMapView.this.onLayoutListener.run();
                    }
                }
            };
        }

        public void setOnDispatchTouchEventInterceptor(IMapsProvider.ITouchInterceptor iTouchInterceptor) {
            this.dispatchInterceptor = iTouchInterceptor;
        }

        public void setOnInterceptTouchEventInterceptor(IMapsProvider.ITouchInterceptor iTouchInterceptor) {
            this.interceptInterceptor = iTouchInterceptor;
        }

        public void setOnLayoutListener(Runnable runnable) {
            this.onLayoutListener = runnable;
        }

        public View getView() {
            return this.mapView;
        }

        public void getMapAsync(Consumer<IMapsProvider.IMap> consumer) {
            this.mapView.getMapAsync(new GoogleMapsProvider$GoogleMapView$$ExternalSyntheticLambda0(consumer));
        }

        public void onPause() {
            this.mapView.onPause();
        }

        public void onResume() {
            this.mapView.onResume();
        }

        public void onCreate(Bundle bundle) {
            this.mapView.onCreate(bundle);
        }

        public void onDestroy() {
            this.mapView.onDestroy();
        }

        public void onLowMemory() {
            this.mapView.onLowMemory();
        }
    }

    public static final class GoogleCameraUpdate implements IMapsProvider.ICameraUpdate {
        /* access modifiers changed from: private */
        public CameraUpdate cameraUpdate;

        private GoogleCameraUpdate(CameraUpdate cameraUpdate2) {
            this.cameraUpdate = cameraUpdate2;
        }
    }

    public static final class GoogleMapStyleOptions implements IMapsProvider.IMapStyleOptions {
        /* access modifiers changed from: private */
        public MapStyleOptions mapStyleOptions;

        private GoogleMapStyleOptions(MapStyleOptions mapStyleOptions2) {
            this.mapStyleOptions = mapStyleOptions2;
        }
    }
}
