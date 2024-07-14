package com.appmanager.myproject.retrofit;


import com.appmanager.myproject.model.NotiResponse;
import com.appmanager.myproject.model.NotiSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNotification {
    @Headers(

            {
                    "Content-Type: application/json",
                    "Authorization: Bearer ya29.ElqKBGN2Ri_Uz...HnS_uNreA"
            }
    )
    @POST("v1/projects/myproject-b5ae1/messages:send")
    Observable<NotiResponse> sendNotification(@Body NotiSendData data);
}
