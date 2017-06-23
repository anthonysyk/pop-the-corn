import React, {Component} from 'react';
import "./searchBar.scss";
import {Button} from 'react-bootstrap';

class SearchBar extends Component {
    render() {
        const {handleSubmit} = this.props;
        return (
            <div className="col-md-12 form-group search-container">
                <form id="search-form" onSubmit={handleSubmit}>
                    <input className="form-control search-input"
                           name="searchInput"
                           placeholder="Avengers ..."/>
                    <Button className="btn btn-success search-button"
                            type="submit"
                            onClick="">
                        <i className="fa fa-search"/>
                    </Button>
                </form>
            </div>
        );
    }
}

export default SearchBar;