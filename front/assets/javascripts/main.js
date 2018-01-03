import React from "react";
import {render} from "react-dom";
import {applyMiddleware, createStore, compose} from "redux";
import {Provider} from "react-redux";
import thunk from "redux-thunk";
import {Route} from "react-router";
import {ConnectedRouter, routerMiddleware} from "react-router-redux";
import SearchComponent from "./components/search/SearchComponent";
import DetailPage from "./components/pages/DetailPage";
import RecommendationComponent from "./components/recommendation/RecommendationComponent";
import UserProfileRecommendationComponent from "./components/recommendation/UserProfileRecommendationComponent";
import MainComponent from "./components/MainComponent";
import createHistory from "history/createHashHistory";
import reducer from "./reducers/reducers";

import "../stylesheets/main.scss";

const history = createHistory();

const store = createStore(
    reducer,
    compose(
        applyMiddleware(routerMiddleware(history), thunk),
        window.devToolsExtension ? window.devToolsExtension() : f => f
    )
);

render(
    <Provider store={store}>
        <ConnectedRouter history={history}>
            <MainComponent>
                <Route exact path="/" component={SearchComponent}/>
                <Route exact path="/details/:id" component={DetailPage}/>
                <Route exact path="/content-based" component={RecommendationComponent}/>
                <Route exact path="/recommendations" component={UserProfileRecommendationComponent}/>
            </MainComponent>
        </ConnectedRouter>
    </Provider>
    ,
    document.querySelector('#app-container')
);