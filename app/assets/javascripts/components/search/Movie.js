import React from 'react';

const Movie = ({movie}) => (
    <div className="search-result">
        <h5>{movie.title}</h5>
        <p className="lead">{movie.genres}</p>
        <p>{movie.description}</p>
        <hr />
    </div>
);

export default Movie;