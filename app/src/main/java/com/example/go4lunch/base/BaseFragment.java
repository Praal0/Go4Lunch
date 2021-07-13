package com.example.go4lunch.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.HttpException;
import com.example.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { return getView(); }

    // -----------------
    // UTILS
    // -----------------

    protected String getTodayDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    @Nullable
    public FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected void handleError(Throwable throwable) {
        getActivity().runOnUiThread(() -> {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                int statusCode = httpException.getStatusCode();
                Log.e("HttpException", "Error code : " + statusCode);
                Toast.makeText(getContext(), getResources().getString(R.string.http_error_message,statusCode), Toast.LENGTH_SHORT).show();
            } else if (throwable instanceof SocketTimeoutException) {
                Log.e("SocketTimeoutException", "Timeout from retrofit");
                Toast.makeText(getContext(), getResources().getString(R.string.timeout_error_message), Toast.LENGTH_SHORT).show();
            } else if (throwable instanceof IOException) {
                Log.e("IOException", "Error");
                Toast.makeText(getContext(), getResources().getString(R.string.exception_error_message), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Generic handleError", "Error");
                Toast.makeText(getContext(), getResources().getString(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
