import React, {Component} from 'react';
import {getPopularMovies} from '../actions/actions';
import {connect} from 'react-redux'
import {withRouter} from 'react-router'
import {Row, Col} from 'react-bootstrap';
import {Link} from 'react-router-dom';
import ReactCSSTransitionGroup from 'react-addons-css-transition-group';
import NavbarComponent from './NavbarComponent';
import QuickRatingComponent from "./recommendation/QuickRatingComponent";
import * as variables from '../variables';

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
        const {suggestionsData, children} = this.props;

        return (
            <div className="popthecorn-header">
                <NavbarComponent />
                <Row>
                    <div className="main-title__white">
                        <a href="/"><h1>&nbsp;&nbsp;{variables.site_name}</h1></a>
                        <p className="lead">{variables.slogan}</p>
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
                            <div className="horizontal-list">
                                <span className="search__popular-title">Popular Movies:</span>
                                <ul className="search__tags">
                                    {
                                        suggestionsData.popularMovies.slice(0, 6).map(movie =>
                                            <li key={movie.id}><Link to={`/details/${movie.id}`}>
                                                <span>{movie.title}</span></Link></li>
                                        )
                                    }
                                </ul>
                            </div>
                        </ReactCSSTransitionGroup>
                    </Col>
                </Row>
                <Row className="padding-top">
                    <Col xs={12} md={12}>
                        <QuickRatingComponent/>
                    </Col>
                </Row>
            </div>
        );
    }
}

export default HeaderComponent;