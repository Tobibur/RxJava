package com.ashokslsk.rxjava.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ashokslsk.rxjava.R;
import com.ashokslsk.rxjava.model.Gist;
import com.ashokslsk.rxjava.model.GistRepo;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author ashok.kumar - Created
 * @author Praveen2Gemini - Modified
 */

public class SimpleRxJavaActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView responseView;
    ProgressBar progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_output);
        responseView = (TextView) findViewById(R.id.txt_hello_world);
        progressView = (ProgressBar) findViewById(R.id.progress_bar);
        showProgress();
        getGistObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Gist>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        responseView.setText("Error occurred! Check your Logcat!");
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace(); // Just to see complete log information. we can comment if not necessary!
                    }

                    @Override
                    public void onNext(Gist gist) {

                        StringBuilder sb = new StringBuilder();
                        // Output
                        for (Map.Entry<String, GistRepo> entry : gist.files.entrySet()) {
                            sb.append(entry.getKey());
                            sb.append(" - ");
                            sb.append("Length of file ");
                            sb.append(entry.getValue().content.length());
                            sb.append("\n");
                        }
                        hideProgress();
                        responseView.setText(sb.toString());
                    }
                });
    }

    private void hideProgress() {
        progressView.setVisibility(View.GONE);
        responseView.setVisibility(View.VISIBLE);
    }

    private void showProgress() {
        progressView.setVisibility(View.VISIBLE);
        responseView.setVisibility(View.GONE);
    }


    /**
     * This will return an {@link Observable} object which holds the task inside it.
     *
     * @return
     */
    public Observable<Gist> getGistObservable() {
        return Observable.defer(new Callable<ObservableSource<Gist>>() { // Observable.defer(...) will always run your task in WorkerThread.
            @Override
            public Observable<Gist> call() {
                try {
                    if (null == getGist()) {
                        throw new NullPointerException("Service service returns Invalid Gist object!, Something went wrong!");
                    }
                    return Observable.just(getGist());
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        });
    }

    /**
     * It makes service call overhere and return response in expected model class.
     *
     * @return Gist
     * @throws IOException - If service failed dure IO operation.
     */
    @Nullable
    private Gist getGist() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/gists/59488f02db24ebd83450289e0b0f9ff7")
                .build();

        Response response = client.newCall(request).execute();

        // Avoid using isSuccessful(), Because if failure happen it simply give false.
        // Exception we've to handle it in separate way.

//        if (response.isSuccessful()) {
        return new Gson().fromJson(response.body().charStream(), Gist.class);
//        }
    }

}
