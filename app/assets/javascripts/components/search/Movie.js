import React from 'react';
import {Image, Row, Col} from 'react-bootstrap';

const Movie = ({movie}) => (
    <div className="search-result">
        <Row className="search-row">
            <Col xs={4} md={2}>
                <Image src={movie.poster} rounded/>
            </Col>
            <Col xs={8} md={10}>
                <h2>{movie.title}</h2>
                <p className="lead"><i>{movie.genres}</i></p>
                <p>{movie.overview}</p>
                <p>Note : {movie.note} ({movie.votes})</p>
            </Col>
        </Row>
    </div>
);

export default Movie;