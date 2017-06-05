import React from 'react';
import "./searchBar.scss";

const SearchBar = props => {

    return (
        <div className="col-md-12 form-group search-container">
            <form id="search-form">
                    <input className="form-control search-input" name="searchInput"  placeholder="Avengers ..." value={props.keywords}/>
                    <button className="btn btn-success search-button" type="submit"><i className="fa fa-search"/></button>
            </form>
        </div>
    );

};

export default SearchBar;