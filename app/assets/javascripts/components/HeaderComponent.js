import React, {Component} from 'react';
import {getPopularMovies} from '../actions/search';
import {connect} from 'react-redux'
import {withRouter} from 'react-router'
import {Row, Col} from 'react-bootstrap';
import {Link} from 'react-router-dom';
import ReactCSSTransitionGroup from 'react-addons-css-transition-group';


@withRouter
@connect(
    state => ({
        suggestionsData: state.suggestionsData,
        movieData: state.movieData
    })
)

class HeaderComponent extends Component {

    constructor(props) {
        super(props);
    }

    componentWillMount() {
        return this.props.dispatch(getPopularMovies())
    }

    render() {
        const {suggestionsData, movieData, children} = this.props;

        const divImage = {
            backgroundImage: "url(" + movieData.movie.backdrop + ")"
        };

        return (
            <div className="popthecorn-header" style={movieData.movie.backdrop && divImage}>
                <Row>
                    <div className="main-title__white">
                        <a href="/"><h1>&nbsp;&nbsp;Pop the corn</h1></a>
                        <p className="lead">Get me the movie, I'm making popcorn !</p>
                    </div>
                </Row>
                {children}
                <Row>
                    <Col md={6} xs={6} className="search-container">
                        <ReactCSSTransitionGroup
                            transitionName="apparition"
                            transitionAppear={true}
                            transitionEnterTimeout={500}
                            transitionLeaveTimeout={300}
                            transitionAppearTimeout={300}
                        >
                            <div className="popular-movies">
                                <span className="search__popular-title">Popular Movies:</span>
                                <ul className="search__tags">
                                    {
                                        suggestionsData.popularMovies.slice(0, 6).map(movie =>
                                            <li key={movie.id}><Link to={`/details/${movie.id}`}><span
                                                className="popular-movie-label">{movie.title}</span></Link></li>
                                        )
                                    }
                                </ul>
                            </div>
                        </ReactCSSTransitionGroup>
                    </Col>
                </Row>
            </div>
        );
    }
}

export default HeaderComponent;