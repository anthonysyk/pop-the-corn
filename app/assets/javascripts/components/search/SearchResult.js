import React, {Component} from 'react';
import MovieResult from './MovieResult';

class SearchResult extends Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        const { movies } = this.props;
        return (
            <div className="search-result-section">
                {
                    movies.map((movie, key) =>
                        <MovieResult movie={movie} key={key}/>
                    )
                }
            </div>
        )
    }

}

export default SearchResult