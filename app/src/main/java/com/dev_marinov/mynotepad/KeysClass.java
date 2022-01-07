package com.dev_marinov.mynotepad;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class KeysClass {
    //  класс от получения конктретного ответа от сервера
    // (особенность retrofit)



    // Переменные мы аннотировали с помощью @SerializedName(), передав туда имя.
    // Эти имена фактически являются ключами в возвращаемых из API JSON-данных,
    // поэтому вы можете как угодно изменять имя переменной, но убедитесь, что имя,
    // переданное в аннотацию @SerializedName(), точно присутствует в JSON.

    @SerializedName("result") // ключ который придет с сервера при записи данных
    @Expose
    public String result;


}
