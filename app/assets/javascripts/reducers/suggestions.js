import { RECEIVE_SUGGESTIONS, RECEIVE_POPULAR_MOVIES, RECEIVE_BEST_RATED_MOVIES } from '../actions/types';

const initialState = {
    suggestions: [],
    popularMovies: [],
    bestRatedMovies: []
};

function suggestionsData(state = initialState, action) {
    switch(action.type) {
        case RECEIVE_BEST_RATED_MOVIES:
            return Object.assign({}, state, { bestRatedMovies: action.bestRatedMovies });
        case RECEIVE_POPULAR_MOVIES:
            return Object.assign({}, state, { popularMovies: action.popularMovies });
        case RECEIVE_SUGGESTIONS:
            return Object.assign({}, state, { suggestions: action.suggestions });
        default:
            return state;
    }
}

export default suggestionsData;