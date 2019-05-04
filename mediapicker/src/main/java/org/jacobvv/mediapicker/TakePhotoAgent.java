package org.jacobvv.mediapicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jacobvv.mediapicker.data.model.Result;

/**
 * Created by Jacob on 17-11-27.
 */

public interface TakePhotoAgent {

    void pickSinglePhoto();

    void pickMultiPhotos(Result haveSelected);

    void takePhoto();

    void setPickLimit(int limit);

    void onCreate(@Nullable Bundle savedInstanceState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults);

}
