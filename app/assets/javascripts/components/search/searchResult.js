import React, {Component} from 'react';
import {render} from 'react-dom';
import Movie from './Movie';

import "./searchResult.scss";

class SearchResult extends Component {

    constructor(props, context) {
        super(props, context);

        this.state = {
            results: [
                {
                    id: "1",
                    title: "Avengers",
                    genres: "Action Aventure",
                    description: "Nulla vitae elit libero, a pharetra augue. Maecenas sed diam eget risus varius blandit sit amet non magna. Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Nullam id dolor id nibh ultricies vehicula ut id elit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras mattis consectetur purus sit amet fermentum."
                },
                {
                    id: "2",
                    title: "Batman: Dark night rises",
                    genres: "Action Aventure",
                    description: "Nulla vitae elit libero, a pharetra augue. Maecenas sed diam eget risus varius blandit sit amet non magna. Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Nullam id dolor id nibh ultricies vehicula ut id elit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras mattis consectetur purus sit amet fermentum."

                }
            ]
        }
    }


    render() {
        return (
            <div className="search-result-section">
                {
                    this.state.results.map(movie =>
                        <Movie movie={movie} key={movie.id}/>
                    )
                }
            </div>
        )
    }

}

export default SearchResult