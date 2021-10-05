package org.runhare.theappthatneverlies.Models;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GrpcClientViewModel extends ViewModel {

    private final MutableLiveData<GrpcClientModel> captureLiveData = new MutableLiveData<>();

    GrpcClientModel captureModel = new GrpcClientModel();

    public androidx.lifecycle.LiveData<GrpcClientModel> getCapture() {
        return captureLiveData;
    }

    public void setErrorMessage(String message) {
        captureModel.setErrorMessage(message);
        captureLiveData.postValue(captureModel);
    }

    public void setProgress(int progress) {
        captureModel.setProgress(progress);
        captureLiveData.postValue(captureModel);
    }

    public void setIsComplete(boolean isComplete) {
        captureModel.setComplete(isComplete);
        captureLiveData.postValue(captureModel);
    }

}
