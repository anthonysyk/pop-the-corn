import React, {Component} from 'react';
import SearchBar from './SearchBar';
import SearchResult from './SearchResult';

class SearchComponent extends Component {
    constructor(props){
        super(props);
        this.state = {
            movies: []
        }
    }

    render() {
        const { movies } = this.state.movies;

        function submit(values) {
            console.log(values);
        }

        return (
            <div>
                <SearchBar onSubmit={ submit } />
                <SearchResult movies={ movies }/>
            </div>
        );
    }
}

export default SearchComponent;