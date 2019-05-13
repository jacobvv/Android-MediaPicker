package org.jacobvv.mediapicker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jacobvv.mediapicker.TakePhoto;
import org.jacobvv.mediapicker.TakePhotoAgent;
import org.jacobvv.mediapicker.data.model.Result;

/**
 * @author yinhui
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TakePhotoDemoActivity";
    RecyclerView mRecyclerView;
    ResultAdapter mAdapter;
    TakePhotoAgent mTakePhoto;
    private Result mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_result);
        mAdapter = new ResultAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mTakePhoto = TakePhoto.with(this)
                // Add listener in take photo, usually it is necessary.
                .setTakePhotoListener(new TakePhotoResult())
                // Enable it so that return compressed result.
                .enableCompress(true)
                .enableAlbumSelect(true)
                // Enable it so that there will be a take photo button in images list.
                .enableCaptureInPick(true)
                // Enable it so that image picker can show images as album you choose.
                .enableCaptureInPickReturn(true)
                // Picture picker will limit the number of images that you pick.
                .setPickLimit(9)
                .build();
        mTakePhoto.onCreate(savedInstanceState);

        Button btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTakePhoto.takePhoto();
            }
        });

        Button btnPickSinglePhoto = findViewById(R.id.btn_pick_single_media);
        btnPickSinglePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTakePhoto.pickSinglePhoto();
            }
        });

        Button btnPickMultiPhoto = findViewById(R.id.btn_pick_multi_media);
        btnPickMultiPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTakePhoto.pickMultiPhotos(mResult);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mTakePhoto.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTakePhoto.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTakePhoto.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mTakePhoto.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public class TakePhotoResult implements TakePhoto.TakePhotoListener {

        @Override
        public void onProcessStart(Result result) {

        }

        @Override
        public void onSuccess(Result result) {
            mResult = result;
            mAdapter.setData(mResult.getImages());
        }

        @Override
        public void onError(int errorCode, String msg) {
            Log.d(TAG, "Take photo failed! Code: " + errorCode + ", " + msg);
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "User cancel!");
            Toast.makeText(MainActivity.this, "User cancel.", Toast.LENGTH_SHORT).show();
        }

    }

}
