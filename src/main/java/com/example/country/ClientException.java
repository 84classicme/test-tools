package com.example.country;

import lombok.Getter;

@Getter
public class ClientException extends RuntimeException{
    private int code;
    public ClientException(String msg, int code){
        super(msg);
        this.code = code;
    }
}
