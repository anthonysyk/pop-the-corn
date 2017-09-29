import {LOAD_MOVIES, RECEIVE_MOVIES, ERROR_RECEIVE_MOVIES, RECEIVE_SUGGESTIONS, RECEIVE_MOVIE_DETAILS, RECEIVE_POPULAR_MOVIES, RECEIVE_BEST_RATED_MOVIES, RECEIVE_POPULAR_BY_GENRE} from './types';

import * as searchAPI  from '../api/search';

function loadMovies() {
    return {
        type: LOAD_MOVIES
    }
}

function receiveSuggestions(suggestions) {
    return {
        type: RECEIVE_SUGGESTIONS,
        suggestions: suggestions
    }

}

function receiveMovies(moviesData) {
    return {
        type: RECEIVE_MOVIES,
        movies: moviesData.movies,
        hits: moviesData.hits
    };
}

function receiveMovieDetails(movie) {
    return {
        type: RECEIVE_MOVIE_DETAILS,
        movie: movie
    };
}

function receivePopularMovies(popularMovies) {
    return {
        type: RECEIVE_POPULAR_MOVIES,
        popularMovies: popularMovies
    }
}

function receiveBestRatedMovies(bestRatedMovies) {
    return {
        type: RECEIVE_BEST_RATED_MOVIES,
        bestRatedMovies: bestRatedMovies
    }
}

function receivePopularByGenre(popularByGenre) {
    return {
        type: RECEIVE_POPULAR_BY_GENRE,
        popularByGenre: popularByGenre
    }
}

function error(message) {
    return {
        type: ERROR_RECEIVE_MOVIES,
        error: message
    }
}

function searchMovies(keywords) {
    return (dispatch) => {
        dispatch(loadMovies());

        return searchAPI.searchByTitle(keywords)
            .then(movies => dispatch(receiveMovies(movies)))
            .catch(e => error("Une erreur est survenue lors de la recherche"))
    }
}

function suggest(keywords) {
    return (dispatch) => searchAPI.suggest(keywords)
        .then(suggestions => dispatch(receiveSuggestions(suggestions)))
}

function getMovieDetails(id) {
    return (dispatch) => searchAPI.getMovieDetails(id)
        .then(movie => dispatch(receiveMovieDetails(movie)))
}

function getPopularMovies(){
    return (dispatch) => searchAPI.getPopularMovies()
        .then(popularMovies => dispatch(receivePopularMovies(popularMovies)))
}

function getBestRatedMovies() {
    return (dispatch) => searchAPI.getBestRatedMovies()
        .then(bestRatedMovies => dispatch(receiveBestRatedMovies(bestRatedMovies)))
}

function getPopularByGenre() {
    return (dispatch) => searchAPI.getPopularByGenre()
        .then(popularByGenre => dispatch(receivePopularByGenre(popularByGenre)))
}

export { searchMovies, suggest, getMovieDetails, getPopularMovies, getBestRatedMovies, getPopularByGenre }