import { RECEIVE_MOVIE_DETAILS } from '../actions/types';

const initialState = {
    movie: {},
    isLoading: false,
    error: null
};

function moviesData(state = initialState, action) {
    switch(action.type) {
        case RECEIVE_MOVIE_DETAILS:
            return Object.assign({}, state, { isLoading: false, movie: action.movie });
        default:
            return state;
    }
}

export default moviesData;