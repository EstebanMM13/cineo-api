package com.estebanmm13.movies_service.error.notFound;

public class VoteNotFoundException extends RuntimeException{

    public VoteNotFoundException(String message) {
        super(message);
    }
}
