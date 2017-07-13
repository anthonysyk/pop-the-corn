import React from "react";
import {render} from "react-dom";
import {applyMiddleware, createStore} from "redux";
import {Provider} from "react-redux";
import thunk from "redux-thunk";
import {Route} from "react-router";
import {ConnectedRouter, routerMiddleware} from "react-router-redux";
import SearchComponent from "./components/search/SearchComponent";
import createHistory from "history/createHashHistory";
import reducer from "./reducers/reducers";


import "../stylesheets/main.scss";

const history = createHistory();

const store = createStore(
    reducer,
    applyMiddleware(routerMiddleware(history), thunk)
);

const Main = (props) => (
    <div id="wrapper">
        <div className="page-header main-title">
            <h1><i className="fa fa-film"/>&nbsp;&nbsp;Pop the corn</h1>
            <p className="lead">Get me the movie, I'm making popcorn !</p>
        </div>

        <div id="page-wrapper">
            <div className="container-fluid">
                { props.children }
            </div>
        </div>
    </div>
);

render(
    <Provider store={ store }>
        <ConnectedRouter history={ history }>
            <Main>
                <Route exact path="/" component={SearchComponent}/>
            </Main>
        </ConnectedRouter>
    </Provider>
    ,
    document.querySelector('#app-container')
);