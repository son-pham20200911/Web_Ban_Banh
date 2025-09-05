package com.example.web_ban_banh.Exception.BadRequestEx_400;

public class BadRequestExceptionCustom extends RuntimeException{
    public BadRequestExceptionCustom(String message){
        super(message);
    }
}
