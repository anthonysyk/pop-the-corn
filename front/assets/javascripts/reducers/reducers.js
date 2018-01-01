import {combineReducers} from 'redux';
import {routerReducer} from 'react-router-redux';

import moviesData from './movies';
import movieData from './movie';
import suggestionsData from './suggestions';
import machineLearningData from './machinelearning'

export default combineReducers({
    moviesData,
    movieData,
    suggestionsData,
    machineLearningData,
    router: routerReducer
});