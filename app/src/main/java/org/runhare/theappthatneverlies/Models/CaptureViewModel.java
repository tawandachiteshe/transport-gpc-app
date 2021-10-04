package org.runhare.theappthatneverlies.Models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CaptureViewModel extends ViewModel{

    private final MutableLiveData<CaptureModel> captureLiveData = new MutableLiveData<>();

    CaptureModel captureModel = new CaptureModel();

    public androidx.lifecycle.LiveData<CaptureModel> getCapture() {
        return captureLiveData;
    }

    public CaptureViewModel() {
        // trigger user load.
    }

    public void setDistance(double distance) {
        Log.i("Tawanda", "Distance" + distance);
        // depending on the action, do necessary business logic calls and update the
        captureModel.setDistance(captureModel.getDistance() + distance);
        captureLiveData.postValue(captureModel);
    }

    public void setGpsAccuracy(float accuracy) {
        Log.i("Tawanda", "Distance" + accuracy);
        // depending on the action, do necessary business logic calls and update the
        captureModel.setGpsAccuracy(accuracy);
        captureLiveData.postValue(captureModel);
    }

    public void setCaptureModel(CaptureModel model) {
        captureLiveData.postValue(captureModel);
    }



}
