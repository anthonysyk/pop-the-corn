import React from 'react';

const Movie = props => {
    return (
        <div className="search-result">
            <h5>{props.movie.title}</h5>
            <p className="lead">{props.movie.genres}</p>
            <p>{props.movie.description}</p>
            <hr />
        </div>
    )
};

export default Movie;