import { LOAD_MOVIES, RECEIVE_MOVIES, ERROR_RECEIVE_MOVIES, RECEIVE_SUGGESTIONS } from '../actions/types';

const initialState = {
    movies: [],
    suggestions: [],
    hits: 0,
    isLoading: false,
    error: null
};

function moviesData(state = initialState, action) {
    switch(action.type) {
        case LOAD_MOVIES:
            return Object.assign({}, state, { isLoading: true });
        case RECEIVE_MOVIES:
            return Object.assign({}, state, { movies: action.movies, hits: action.hits, isLoading: false, error: null });
        case ERROR_RECEIVE_MOVIES:
            return Object.assign({}, state, { movies: [], isLoading: false, error: action.error });
        case RECEIVE_SUGGESTIONS:
            return Object.assign({}, state, { suggestions: action.suggestions });
        default:
            return state;
    }
}

export default moviesData;