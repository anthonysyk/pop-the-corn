import React, {Component} from 'react';
import {render} from 'react-dom';
import SearchComponent from './components/search/SearchComponent';

import '../stylesheets/main.scss';

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
    <Main>
        <SearchComponent />
    </Main>
    ,
    document.querySelector('#app-container')
);