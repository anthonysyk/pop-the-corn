import { RECEIVE_TFIDF_MOVIES, RECEIVE_QUICK_RATING_MOVIES, RECEIVE_RECOMMENDATIONS, RECEIVE_USER_PROFILE } from '../actions/types';

const initialState = {
    tfidfMovies: [],
    quickRatingMovies: [],
    userProfile: {
        genres: {}
    },
    recommendations: []
};

function machineLearningData(state = initialState, action) {
    switch(action.type) {
        case RECEIVE_TFIDF_MOVIES:
            return Object.assign({}, state, { tfidfMovies: action.tfidfMovies });
        case RECEIVE_QUICK_RATING_MOVIES:
            return Object.assign({}, state, { quickRatingMovies: action.quickRatingMovies });
        case RECEIVE_USER_PROFILE:
            return Object.assign({}, state, {userProfile: action.userProfile});
        case RECEIVE_RECOMMENDATIONS:
            return Object.assign({}, state, { recommendations: action.recommendations.recommendations, userProfile: action.recommendations.userProfile });
        default:
            return state;
    }
}

export default machineLearningData;