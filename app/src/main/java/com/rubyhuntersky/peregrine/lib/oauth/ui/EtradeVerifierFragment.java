package com.rubyhuntersky.peregrine.lib.oauth.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rubyhuntersky.peregrine.lib.oauth.model.OauthToken;
import com.rubyhuntersky.peregrine.R;

import org.json.JSONException;
import org.json.JSONObject;


public class EtradeVerifierFragment extends DialogFragment {
    public static final String ARG_APP_KEY = "arg-app-key";
    public static final String ARG_REQUEST_KEY = "arg-request-key";
    public static final String TAG = EtradeVerifierFragment.class.getSimpleName();
    private Listener listener;
    private WebView verifierWeb;

    public EtradeVerifierFragment() {
        // Required empty public constructor
    }

    public static EtradeVerifierFragment newInstance(OauthToken requestToken, Listener listener) {
        EtradeVerifierFragment fragment = new EtradeVerifierFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_KEY, requestToken.appToken.appKey);
        args.putString(ARG_REQUEST_KEY, requestToken.key);
        fragment.setArguments(args);
        fragment.setListener(listener);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        final Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View inflate = inflater.inflate(R.layout.fragment_etrade_verifier, container, false);
        final String url = getUrl();
        Log.d(TAG, "Url: " + url);

        verifierWeb = (WebView) inflate.findViewById(R.id.verifierWeb);
        verifierWeb.getSettings().setUseWideViewPort(true);
        verifierWeb.getSettings().setSupportZoom(false);
        verifierWeb.getSettings().setJavaScriptEnabled(true);
        verifierWeb.getSettings().setLoadWithOverviewMode(true);
        verifierWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                alertJs(url, message, result);
                return true;
            }
        });
        verifierWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: " + url);
                checkPageForVerifier();
            }
        });
        verifierWeb.loadUrl(url);
        return inflate;
    }

    private void checkPageForVerifier() {
        //noinspection SpellCheckingInspection
        final String containsCredentialInputs = "javascript:(function() {" +
              "  var customerInfoForms = document.getElementsByName('CustInfo');" +
              "  if (customerInfoForms.length === 1) {" +
              "    var accept = customerInfoForms[0].querySelector(\"input[value='Accept'][type='submit']\");" +
              "    if (!accept) {return {error:'NO_ACCEPT'}};" +
              "    accept.click();" +
              "    return {action:'wait'};" +
              "  }" +
              "  var verifier = document.querySelector('.api-inner-container [type=\"text\"]');" +
              "  if (verifier) {" +
              "    return {action:'done', verifier: verifier.value};" +
              "  }" +
              "  return {action:'credentials'};" +
              "})();";
        verifierWeb.evaluateJavascript(containsCredentialInputs, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.d(TAG, "onReceiveValue: " + value);
                try {
                    final JSONObject jsonObject = new JSONObject(value);
                    final String error = jsonObject.optString("error");
                    if (error.isEmpty()) {
                        final String action = jsonObject.optString("action");
                        if (action.equals("done")) {
                            final String verifier = jsonObject.optString("verifier");
                            Log.d(TAG, "Verifier: " + verifier);
                            dismiss();
                            if (listener != null) {
                                listener.onVerifier(verifier);
                            }
                        }
                    } else {
                        alertError("Contains credential inputs", error);
                    }
                } catch (JSONException e) {
                    alertError("Contains credential inputs", e.getMessage());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        verifierWeb.requestFocus();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private String getUrl() {
        final String appKey = getArguments().getString(ARG_APP_KEY);
        final String requestKey = getArguments().getString(ARG_REQUEST_KEY);
        //noinspection SpellCheckingInspection
        final String format = "https://us.etrade.com/e/t/etws/authorize?key=%s&token=%s";
        return String.format(format, Uri.encode(appKey), Uri.encode(requestKey));
    }

    private void alertJs(String title, String message, final JsResult result) {
        new AlertDialog.Builder(getActivity()).setTitle(title)
              .setMessage(message)
              .setOnCancelListener(new DialogInterface.OnCancelListener() {
                  @Override
                  public void onCancel(DialogInterface dialog) {
                      result.cancel();
                  }
              })
              .setOnDismissListener(new DialogInterface.OnDismissListener() {
                  @Override
                  public void onDismiss(DialogInterface dialog) {
                      result.confirm();
                  }
              })
              .show();
    }

    private void alertError(String title, String message) {
        new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message).show();
    }

    public interface Listener {
        void onVerifier(String verifier);
    }
}
