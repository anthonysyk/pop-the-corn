import React from 'react';
import {Image, Row, Col} from 'react-bootstrap';
import no_image from '../../../images/no-image.svg';
import ReactStars from 'react-stars';
import {Link} from 'react-router-dom'
import ReactCSSTransitionGroup from 'react-addons-css-transition-group';


const ratingChanged = (newRating) => {
    console.log(newRating)
};

const MovieResult = ({movie}) => (
    <Link className="movie-title" to={`/details/${movie.id}`}>
        <ReactCSSTransitionGroup
            transitionName="apparition"
            transitionAppear={true}
            transitionEnterTimeout={500}
            transitionLeaveTimeout={300}
            transitionAppearTimeout={300}
        >
            <div className="search-result">
                <Row className="search-row">
                    <Col xs={4} md={2}>
                        <Image src={movie.poster ? movie.poster : no_image} rounded/>
                    </Col>
                    <Col xs={8} md={10}>
                        <Row>
                            <Col xs={12} md={12}>
                                <h2>
                                    {movie.title}
                                </h2>
                            </Col>
                        </Row>
                        <p className="lead"><i>{movie.genres}</i></p>
                        <Row>
                            <Col xs={12} md={12}>
                                <p>{movie.overview.substring(0, 400)}{movie.overview.length > 400 ? "..." : ""}</p>
                            </Col>
                        </Row>
                        <Row>
                            <Col xs={8} md={10}/>
                            <Col xs={4} md={2}>
                                <ReactStars className="rating-stars" value={movie.vote_average / 2} count={5}
                                            onChange={ ratingChanged }
                                            size={24} color2={'#ffd700'}/>
                                <span className="small-info">{movie.vote_count} votes</span>
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </div>
        </ReactCSSTransitionGroup>
    </Link>
);

export default MovieResult;