package com.movies.service;

import com.movies.client.ArtistClient;
import com.movies.exception.ObjectNotFoundException;
import com.movies.exception.ObjectPresentException;
import com.movies.model.DTO.ArtistDTO;
import com.movies.model.DTO.MovieDTO;
import com.movies.model.Movie;
import com.movies.repository.MoviesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService {
    private final ArtistClient artistClientService;
    private final MoviesRepository moviesRepository;

    public Movie saveMovie(MovieDTO movieRequest) {
        alreadyExistMovie(movieRequest);
        List<ArtistDTO> listArtist = movieRequest.getArtists()
                .stream()
                .map(e -> artistClientService.findArtistsByNameAndSubname(e.getName(), e.getSubName()))
                .collect(Collectors.toList());
        movieRequest.setArtists(listArtist);
        movieRequest.setArtistsId(listArtist.stream().map(ArtistDTO::getArtistId).collect(Collectors.toList()));
        return moviesRepository.save(Movie.converter(movieRequest));
    }

    public void alreadyExistMovie(MovieDTO movieRequest) {
        moviesRepository.findByName(movieRequest.getName())
                .ifPresent(e -> {throw new ObjectPresentException("Movie already exist: " + e.getName());});
    }

    public Movie findById(String id){
        return moviesRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ObjectNotFoundException("Movie not exist: " +id));
    }
}
