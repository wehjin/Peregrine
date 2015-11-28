package com.rubyhuntersky.peregrine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class VerifierFragment extends Fragment {
    public static final String ARG_APP_KEY = "arg-app-key";
    public static final String ARG_REQUEST_KEY = "arg-request-key";
    public static final String TAG = VerifierFragment.class.getSimpleName();
    private Button continueButton;
    private EditText verifierEditText;
    private Listener listener;

    public VerifierFragment() {
        // Required empty public constructor
    }

    public static VerifierFragment newInstance(String appKey, String requestKey) {
        VerifierFragment fragment = new VerifierFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_KEY, appKey);
        args.putString(ARG_REQUEST_KEY, requestKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchVerifierEntryPageInBrowser();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.fragment_verifier, container, false);
        continueButton = (Button) inflate.findViewById(R.id.button_continue);
        verifierEditText = (EditText) inflate.findViewById(R.id.edittext_verifier);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String verifier = verifierEditText.getText().toString();
                if (verifier.isEmpty() || listener == null) {
                    return;
                }
                listener.onVerifier(verifier);
            }
        });
        return inflate;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void launchVerifierEntryPageInBrowser() {
        final String appKey = getArguments().getString(ARG_APP_KEY);
        final String requestKey = getArguments().getString(ARG_REQUEST_KEY);
        final String format = "https://us.etrade.com/e/t/etws/authorize?key=%s&token=%s";
        final String url = String.format(format, Uri.encode(appKey), Uri.encode(requestKey));
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public interface Listener {
        void onVerifier(String verifier);
    }
}
