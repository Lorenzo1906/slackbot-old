package com.example.slackbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardTaskFetchValue<T> {
    @JsonProperty(value = "type")
    private Object type = "task/fetch";

    @JsonProperty(value = "data")
    private T data;

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}