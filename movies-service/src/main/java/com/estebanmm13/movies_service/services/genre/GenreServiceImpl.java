package com.estebanmm13.movies_service.services.genre;


import com.estebanmm13.movies_service.config.CacheConfig;
import com.estebanmm13.movies_service.dtoModels.response.GenreResponseDTO;
import com.estebanmm13.movies_service.error.notFound.GenreNotFoundException;
import com.estebanmm13.movies_service.mapper.GenreMapper;
import com.estebanmm13.movies_service.models.Genre;
import com.estebanmm13.movies_service.repositories.GenreRepository;
import com.estebanmm13.movies_service.support.RestPage;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GenreServiceImpl implements GenreService {

    private static final String NOT_FOUND_BY_ID = "Genre not found with id: %d";
    private static final String NOT_FOUND_BY_NAME = "Genre not found with name: %s";

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreServiceImpl(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Override
    @Cacheable(value = CacheConfig.CACHE_GENRES, key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<GenreResponseDTO> findAllGenres(Pageable pageable) {
        return new RestPage<>(genreRepository.findAll(pageable).map(genreMapper::toResponseDTO));
    }

    @Override
    @Cacheable(value = CacheConfig.CACHE_GENRE, key = "#id")
    public GenreResponseDTO findGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_ID, id)));
        return genreMapper.toResponseDTO(genre);
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_GENRES, allEntries = true)
    public Genre createGenre(Genre genre) {
        if (genreRepository.existsByNameIgnoreCase(genre.getName())) {
            throw new IllegalArgumentException("Genre already exists with name: " + genre.getName());
        }
        return genreRepository.save(genre);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CACHE_GENRE, key = "#id"),
            @CacheEvict(value = CacheConfig.CACHE_GENRES, allEntries = true)
    })
    public Genre updateGenre(Long id, Genre genre) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_ID, id)));

        if (!existingGenre.getName().equalsIgnoreCase(genre.getName()) &&
                genreRepository.existsByNameIgnoreCase(genre.getName())) {
            throw new IllegalArgumentException("Another genre already exists with name: " + genre.getName());
        }

        genre.setId(id);
        return genreRepository.save(genre);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CACHE_GENRE, key = "#id"),
            @CacheEvict(value = CacheConfig.CACHE_GENRES, allEntries = true)
    })
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new GenreNotFoundException(String.format(NOT_FOUND_BY_ID, id));
        }
        genreRepository.deleteById(id);
    }

    @Override
    public Page<GenreResponseDTO> findGenreByName(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be empty");
        }
        return genreRepository.findByNameContainingIgnoreCase(name.trim(), pageable)
                .map(genreMapper::toResponseDTO);
    }

    @Override
    public GenreResponseDTO findGenreByExactName(String name) {
        Genre genre = genreRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new GenreNotFoundException(String.format(NOT_FOUND_BY_NAME, name)));
        return genreMapper.toResponseDTO(genre);
    }
}
