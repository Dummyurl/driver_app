package com.rideeaze.services.network.model.response;

public class JsonResponse<T> {

    public T Content;
    public Boolean IsSuccess = false;
    public String Message;
    public Integer ResponseCode;
}
