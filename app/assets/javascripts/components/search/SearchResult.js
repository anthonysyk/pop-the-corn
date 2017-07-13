import React, {Component} from 'react';
import Movie from './Movie';

class SearchResult extends Component {

    constructor(props, context) {
        super(props, context);
    }

    render() {
        const { movies } = this.props;
        return (
            <div className="search-result-section">
                {
                    movies.map((movie, index) =>
                        <Movie movie={movie} key={index}/>
                    )
                }
            </div>
        )
    }

}

export default SearchResult