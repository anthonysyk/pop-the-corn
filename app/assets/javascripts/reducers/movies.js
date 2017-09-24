import { LOAD_MOVIES, RECEIVE_MOVIES, ERROR_RECEIVE_MOVIES } from '../actions/types';

const initialState = {
    movies: [],
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
        default:
            return state;
    }
}

export default moviesData;