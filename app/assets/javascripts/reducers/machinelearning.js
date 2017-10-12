import { RECEIVE_TFIDF_MOVIES, RECEIVE_QUICK_RATING_MOVIES, RECEIVE_RECOMMENDATIONS } from '../actions/types';

const initialState = {
    tfidfMovies: [],
    quickRatingMovies: [],
    userProfileMovies: []
};

function machineLearningData(state = initialState, action) {
    switch(action.type) {
        case RECEIVE_TFIDF_MOVIES:
            return Object.assign({}, state, { tfidfMovies: action.tfidfMovies });
        case RECEIVE_QUICK_RATING_MOVIES:
            return Object.assign({}, state, { quickRatingMovies: action.quickRatingMovies });
        case RECEIVE_RECOMMENDATIONS:
            return Object.assign({}, state, { userProfileMovies: action.userProfileMovies });
        default:
            return state;
    }
}

export default machineLearningData;