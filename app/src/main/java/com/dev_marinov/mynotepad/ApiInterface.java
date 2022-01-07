package com.dev_marinov.mynotepad;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    // для доступа к скрипту serverRetrofitTest на сервере чтобы записывать в дб
    // новые или редактируемые заметки note

    @FormUrlEncoded // FormUrlEncoded
    @POST("/server/serverRetrofitTest/serverRetrofitTest.php")
    Call<KeysClass> myParams(
            @Field("id_row") String id_row,
            @Field("subject") String subjectStr,
            @Field("note") String noteStr,
            @Field("google_id") String google_id);
}
