package com.seasidechachacha.common.payment;

import com.google.gson.Gson;

@Deprecated
public abstract class Request {
    private final String type = this.getClass().getSimpleName();

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getType() {
        return type;
    };
}
