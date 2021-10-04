package org.runhare.theappthatneverlies.Repositories;

import android.content.Context;

import androidx.datastore.rxjava2.RxDataStore;

import org.runhare.theappthatneverlies.RouteCapture;

public class RouteCaptureRepository {

    private RxDataStore<RouteCapture> routeCaptureRxDataStore;
    private Context context;

    public RouteCaptureRepository (RxDataStore<RouteCapture> routeCaptureRxDataStore, Context context) {
        this.context = context;
        this.routeCaptureRxDataStore = routeCaptureRxDataStore;
    }
}
