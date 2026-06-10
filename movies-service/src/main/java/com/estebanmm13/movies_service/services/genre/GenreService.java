package com.estebanmm13.movies_service.services.genre;


import com.estebanmm13.movies_service.dtoModels.response.GenreResponseDTO;
import com.estebanmm13.movies_service.models.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GenreService {

    Page<GenreResponseDTO> findAllGenres(Pageable pageable);

    GenreResponseDTO findGenreById(Long id);

    Genre createGenre(Genre genre);

    Genre updateGenre(Long id, Genre genre);

    void deleteGenre(Long id);

    Page<GenreResponseDTO> findGenreByName(String name, Pageable pageable);

    GenreResponseDTO findGenreByExactName(String name);
}
