import { RECEIVE_SUGGESTIONS, RECEIVE_POPULAR_MOVIES, RECEIVE_BEST_RATED_MOVIES, RECEIVE_POPULAR_BY_GENRE } from '../actions/types';

const initialState = {
    suggestions: [],
    popularMovies: [],
    bestRatedMovies: [],
    popularByGenre: {
        Drama: [],
        Comedy: [],
        Action: [],
        Documentary: [],
        Family: [],
        Horror: [],
        Thriller: []
    }
};

function suggestionsData(state = initialState, action) {
    switch(action.type) {
        case RECEIVE_BEST_RATED_MOVIES:
            return Object.assign({}, state, { bestRatedMovies: action.bestRatedMovies });
        case RECEIVE_POPULAR_MOVIES:
            return Object.assign({}, state, { popularMovies: action.popularMovies });
        case RECEIVE_SUGGESTIONS:
            return Object.assign({}, state, { suggestions: action.suggestions });
        case RECEIVE_POPULAR_BY_GENRE:
            return Object.assign({}, state, { popularByGenre: action.popularByGenre });
        default:
            return state;
    }
}

export default suggestionsData;