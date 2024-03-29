import React, {Component} from 'react';
import SearchBar from './search/SearchBar';
import {getPopularMovies, getBestRatedMovies, getPopularByGenre} from '../actions/actions';
import {connect} from 'react-redux'
import {withRouter} from 'react-router'
import HeaderComponent from './HeaderComponent';
import HomePage from './pages/HomePage';
import FooterComponent from './FooterComponent';
import {mockedKeywords, mockedFooterValues, suggestions} from "../mocked";


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
        const {suggestionsData, moviesData, children, location} = this.props;

        return (
            <div id="wrapper">
                <div id="page-wrapper">
                    <div className="container-fluid">
                        <HeaderComponent>
                            <SearchBar />
                        </HeaderComponent>
                        { location.pathname === "/" && moviesData.movies.length === 0 &&
                            <HomePage data={suggestionsData}/>
                        }
                        {children}
                        <FooterComponent values={mockedFooterValues}/>
                    </div>
                </div>
            </div>
        );
    }
}

export default MainComponent;