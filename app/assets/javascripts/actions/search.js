import {LOAD_MOVIES, RECEIVE_MOVIES, ERROR_RECEIVE_MOVIES, RECEIVE_SUGGESTIONS} from './types';

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

export { searchMovies, suggest }