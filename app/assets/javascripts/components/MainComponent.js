import React, {Component} from 'react';
import SearchBar from './search/SearchBar';
import {searchMovies, suggest, getPopularMovies, getBestRatedMovies, getPopularByGenre} from '../actions/search';
import {connect} from 'react-redux'
import {withRouter} from 'react-router'
import HeaderComponent from './HeaderComponent';
import HomeComponent from './HomeComponent';
import FooterComponent from './FooterComponent';

@withRouter
@connect(
    state => ({
        suggestionsData: state.suggestionsData,
        moviesData: state.moviesData
    })
)

class MainComponent extends Component {

    constructor(props) {
        super(props);
    }

    componentWillMount() {
        this.props.dispatch(getPopularMovies());
        this.props.dispatch(getBestRatedMovies());
        this.props.dispatch(getPopularByGenre());
    }

    render() {
        const {dispatch, suggestionsData, moviesData, children} = this.props;

        function handleSubmit(keywords) {
            dispatch(searchMovies(keywords.toLowerCase()));
        }

        function handleSuggest(keywords) {
            dispatch(suggest(keywords))
        }

        return (
            <div id="wrapper">
                <div id="page-wrapper">
                    <div className="container-fluid">
                        <HeaderComponent>
                            <SearchBar onSubmit={ handleSubmit } onChange={ handleSuggest }
                                       suggestions={ suggestionsData.suggestions }/>
                        </HeaderComponent>
                        { window.location.hash === "#/" && moviesData.movies.length === 0 &&
                            <HomeComponent data={suggestionsData}/>}
                        {children}
                        <FooterComponent />
                    </div>
                </div>
            </div>
        );
    }
}

export default MainComponent;