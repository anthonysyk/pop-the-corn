import React, {Component} from 'react';
import SearchBar from './SearchBar';
import SearchResult from './SearchResult';
import {connect} from 'react-redux'
import {searchMovies, suggest} from '../../actions/search';
import {Loader} from 'react-loaders';

@connect(
    state => ({
        moviesData: state.moviesData
    })
)

class SearchComponent extends Component {
    render() {
        const {dispatch, moviesData} = this.props;

        function handleSubmit(keywords) {
            dispatch(searchMovies(keywords.toLowerCase()));
        }

        function handleSuggest(keywords) {
            dispatch(suggest(keywords))
        }

        return (
            <div>
                <SearchBar onSubmit={ handleSubmit } onChange={ handleSuggest } suggestions={ moviesData.suggestions }/>
                { moviesData.isLoading ?
                    <div className="loader-container">
                        <Loader type="ball-scale-ripple-multiple" active/>
                    </div>
                    : <SearchResult movies={ moviesData.movies }/>
                }
            </div>
        );
    }
}

export default SearchComponent;