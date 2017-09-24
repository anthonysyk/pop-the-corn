import React, {Component} from 'react';
import SearchResult from './SearchResult';
import {connect} from 'react-redux'
import {Loader} from 'react-loaders';
import { withRouter } from 'react-router'

@withRouter
@connect(
    state => ({
        moviesData: state.moviesData
    })
)

class SearchComponent extends Component {
    render() {
        const {moviesData} = this.props;

        return (
            <div>
                { moviesData.isLoading ?
                    <div className="loader-container">
                        <Loader type="ball-scale-ripple-multiple" active/>
                    </div>
                    : <SearchResult movies={ moviesData.movies }/>
                }
            </div>
        );
    }
}

export default SearchComponent;