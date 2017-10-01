import { RECEIVE_TFIDF_MOVIES } from '../actions/types';

const initialState = {
    tfidfMovies: []
};

function machineLearningData(state = initialState, action) {
    switch(action.type) {
        case RECEIVE_TFIDF_MOVIES:
            return Object.assign({}, state, { tfidfMovies: action.tfidfMovies });
        default:
            return state;
    }
}

export default machineLearningData;