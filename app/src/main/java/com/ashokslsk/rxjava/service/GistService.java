package com.ashokslsk.rxjava.service;

import com.ashokslsk.rxjava.model.Gist;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * @author Praveen2Gemini on 24/06/17.
 */

public interface GistService {

    String BASE_URL = "https://api.github.com/";

    @GET("gists/59488f02db24ebd83450289e0b0f9ff7")
    Observable<Gist> fetchGistInformation();
}
