package com.estebanmm13.movies_service.mapper;


import com.estebanmm13.movies_service.dtoModels.response.GenreResponseDTO;
import com.estebanmm13.movies_service.models.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreResponseDTO toResponseDTO(Genre genre) {
        if (genre == null) return null;
        return new GenreResponseDTO(genre.getId(), genre.getName());
    }
}