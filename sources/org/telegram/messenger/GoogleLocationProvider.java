package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import androidx.core.util.Consumer;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import org.telegram.messenger.ILocationServiceProvider;
import org.telegram.messenger.PushListenerController;

@SuppressLint({"MissingPermission"})
public class GoogleLocationProvider implements ILocationServiceProvider {
    private FusedLocationProviderClient locationProviderClient;
    private SettingsClient settingsClient;

    public void init(Context context) {
        this.locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.settingsClient = new SettingsClient(context);
    }

    public ILocationServiceProvider.ILocationRequest onCreateLocationRequest() {
        return new GoogleLocationRequest(LocationRequest.create());
    }

    public void getLastLocation(Consumer<Location> consumer) {
        this.locationProviderClient.getLastLocation().addOnCompleteListener(new GoogleLocationProvider$$ExternalSyntheticLambda1(consumer));
    }

    public void requestLocationUpdates(ILocationServiceProvider.ILocationRequest iLocationRequest, final ILocationServiceProvider.ILocationListener iLocationListener) {
        this.locationProviderClient.requestLocationUpdates(((GoogleLocationRequest) iLocationRequest).request, new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                iLocationListener.onLocationChanged(locationResult.getLastLocation());
            }
        }, Looper.getMainLooper());
    }

    public void removeLocationUpdates(final ILocationServiceProvider.ILocationListener iLocationListener) {
        this.locationProviderClient.removeLocationUpdates(new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                iLocationListener.onLocationChanged(locationResult.getLastLocation());
            }
        });
    }

    public void checkLocationSettings(ILocationServiceProvider.ILocationRequest iLocationRequest, Consumer<Integer> consumer) {
        this.settingsClient.checkLocationSettings(new LocationSettingsRequest.Builder().addLocationRequest(((GoogleLocationRequest) iLocationRequest).request).build()).addOnCompleteListener(new GoogleLocationProvider$$ExternalSyntheticLambda2(consumer));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkLocationSettings$1(Consumer consumer, Task task) {
        try {
            task.getResult(ApiException.class);
            consumer.accept(0);
        } catch (ApiException e) {
            int statusCode = e.getStatusCode();
            if (statusCode == 6) {
                consumer.accept(1);
            } else if (statusCode == 8502) {
                consumer.accept(2);
            }
        }
    }

    public ILocationServiceProvider.IMapApiClient onCreateLocationServicesAPI(Context context, final ILocationServiceProvider.IAPIConnectionCallbacks iAPIConnectionCallbacks, ILocationServiceProvider.IAPIOnConnectionFailedListener iAPIOnConnectionFailedListener) {
        return new GoogleApiClientImpl(new GoogleApiClient.Builder(ApplicationLoader.applicationContext).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            public void onConnected(Bundle bundle) {
                iAPIConnectionCallbacks.onConnected(bundle);
            }

            public void onConnectionSuspended(int i) {
                iAPIConnectionCallbacks.onConnectionSuspended(i);
            }
        }).addOnConnectionFailedListener(new GoogleLocationProvider$$ExternalSyntheticLambda0(iAPIOnConnectionFailedListener)).build());
    }

    public boolean checkServices() {
        return PushListenerController.GooglePushListenerServiceProvider.INSTANCE.hasServices();
    }

    public static final class GoogleLocationRequest implements ILocationServiceProvider.ILocationRequest {
        /* access modifiers changed from: private */
        public LocationRequest request;

        private GoogleLocationRequest(LocationRequest locationRequest) {
            this.request = locationRequest;
        }

        public void setPriority(int i) {
            this.request.setPriority(i != 1 ? i != 2 ? i != 3 ? 100 : 105 : 104 : 102);
        }

        public void setInterval(long j) {
            this.request.setInterval(j);
        }

        public void setFastestInterval(long j) {
            this.request.setFastestInterval(j);
        }
    }

    public static final class GoogleApiClientImpl implements ILocationServiceProvider.IMapApiClient {
        private GoogleApiClient apiClient;

        private GoogleApiClientImpl(GoogleApiClient googleApiClient) {
            this.apiClient = googleApiClient;
        }

        public void connect() {
            this.apiClient.connect();
        }

        public void disconnect() {
            this.apiClient.disconnect();
        }
    }
}
