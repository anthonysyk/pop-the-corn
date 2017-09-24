import React, {Component} from 'react';
import SearchBar from './search/SearchBar';
import {searchMovies, suggest, getPopularMovies, getBestRatedMovies} from '../actions/search';
import {connect} from 'react-redux'
import {withRouter} from 'react-router'
import {Row, Col} from 'react-bootstrap';
import {Link} from 'react-router-dom'
import HeaderComponent from './HeaderComponent';
import HomeComponent from './HomeComponent';

@withRouter
@connect(
    state => ({
        suggestionsData: state.suggestionsData,
        movieData: state.movieData,
    })
)

class MainComponent extends Component {

    constructor(props) {
        super(props);
    }

    componentWillMount() {
        this.props.dispatch(getPopularMovies());
        this.props.dispatch(getBestRatedMovies());
    }

    render() {
        const {dispatch, suggestionsData, children} = this.props;

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
                        <HomeComponent data={suggestionsData}/>
                        {children}
                    </div>
                </div>
            </div>
        );
    }
}

export default MainComponent;