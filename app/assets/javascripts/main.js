import React from 'react';
import { render } from 'react-dom';
import Spa from './Spa';
import SearchBar from './components/search/searchBar';
import SearchResult from  './components/search/searchResult';

import '../stylesheets/main.scss';

render(
    <Spa>
        <SearchBar />
        <SearchResult />
    </Spa>
    ,
  document.querySelector('#app-container')
);