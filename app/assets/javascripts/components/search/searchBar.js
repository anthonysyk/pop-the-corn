import React from 'react';
import "searchBar.scss";

const SearchBar = props => {

    return (
    <div className="col-md-12 form-group">
        <form>
            <input name="searchBar" className="form-control" placeholder="Avengers ..." value={props.keywords}/>
            <button type="submit"></button>
        </form>
    </div>
    );
};

export default SearchBar;