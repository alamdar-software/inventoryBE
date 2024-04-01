package com.inventory.project.exception;

public class InsufficientQuantityException extends  RuntimeException{
    public InsufficientQuantityException(String message) {
        super(message);
    }
}
