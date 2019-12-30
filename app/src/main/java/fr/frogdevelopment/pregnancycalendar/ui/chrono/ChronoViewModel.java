package fr.frogdevelopment.pregnancycalendar.ui.chrono;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChronoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ChronoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}