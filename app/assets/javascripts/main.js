import React from "react";
import {render} from "react-dom";
import {applyMiddleware, createStore} from "redux";
import {Provider} from "react-redux";
import thunk from "redux-thunk";
import {Route} from "react-router";
import {ConnectedRouter, routerMiddleware} from "react-router-redux";
import SearchComponent from "./components/search/SearchComponent";
import DetailComponent from "./components/details/DetailComponent";
import MainComponent from "./components/MainComponent";
import createHistory from "history/createHashHistory";
import reducer from "./reducers/reducers";

import "../stylesheets/main.scss";

const history = createHistory();

const store = createStore(
    reducer,
    applyMiddleware(routerMiddleware(history), thunk)
);

render(
    <Provider store={ store }>
        <ConnectedRouter history={ history }>
            <MainComponent>
                <Route exact path="/" component={SearchComponent}/>
                <Route exact path="/details/:id" component={DetailComponent}/>
            </MainComponent>
        </ConnectedRouter>
    </Provider>
    ,
    document.querySelector('#app-container')
);