import {LOAD_MOVIES, RECEIVE_MOVIES, ERROR_RECEIVE_MOVIES} from './types';

import * as searchAPI  from '../api/search';

function loadMovies() {
    return {
        type: LOAD_MOVIES
    }
}

function receiveMovies(moviesData) {
    return {
        type: RECEIVE_MOVIES,
        movies: moviesData.movies,
        hits: moviesData.hits
    };
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

export { searchMovies }