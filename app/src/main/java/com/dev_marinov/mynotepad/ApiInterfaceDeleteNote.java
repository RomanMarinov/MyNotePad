package com.dev_marinov.mynotepad;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterfaceDeleteNote {
    // для доступа к скрипту serverGetListNote на сервере
    // чтобы запрашивать список заметок notes


    // ResponseBody класс, который говорит ретрофиту получить от сервера код в сыром виде
    // иначе бы мы указали другой класс, в котором написали поля (они ключи),
    // которые мы хотим получить от сервера

    @FormUrlEncoded // FormUrlEncoded
    @POST("/server/serverRetrofitTest/serverDeleteNote.php")
    Call<ResponseBody> myParamsApiInterfaceDeleteNote(
            @Field("id_row") String id_row,
            @Field("google_id") String google_id);
}
