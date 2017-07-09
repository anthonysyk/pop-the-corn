import {combineReducers} from 'redux';
import {routerReducer} from 'react-router-redux';

import moviesData from './movies';

export default combineReducers({
    moviesData,
    router: routerReducer
});