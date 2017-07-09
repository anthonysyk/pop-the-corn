import React from 'react';
import {Thumbnail, Row, Col} from 'react-bootstrap';

const Movie = ({movie}) => (
    <div className="search-result">
        <Col xs={6} md={4}>
            <Thumbnail src={movie.poster} alt="242x200">
                <h5>{movie.title}</h5>
                <p className="lead">{movie.genres}</p>
                <p>{movie.overview}</p>
            </Thumbnail>
        </Col>
    </div>
);

export default Movie;