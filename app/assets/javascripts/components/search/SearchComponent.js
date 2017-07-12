import React, {Component} from 'react';
import SearchBar from './SearchBar';
import SearchResult from './SearchResult';
import {connect} from 'react-redux'
import {searchMovies} from '../../actions/search';
import {Loader} from 'react-loaders';

@connect(
    state => ({
        moviesData: state.moviesData
    })
)

class SearchComponent extends Component {
    render() {
        const { dispatch, moviesData} = this.props;
        function handleSubmit(keywords) {
            dispatch(searchMovies(keywords));
        }
        return (
            <div>
                <Loader type="pacman" />
                <SearchBar onSubmit={ handleSubmit } />
                <SearchResult movies={ moviesData.movies }/>
            </div>
        );
    }
}

export default SearchComponent;