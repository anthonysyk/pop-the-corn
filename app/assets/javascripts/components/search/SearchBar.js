import React, {Component} from 'react';
import {Button, FormControl} from 'react-bootstrap';

class SearchBar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            keywords: ""
        }
    }
    render() {
        const {onSubmit} = this.props;
        function handleSubmit(event, keywords) {
            event.preventDefault();
            onSubmit(keywords)
        }
        return (
            <div className="col-md-12 form-group search-container">
                <form id="search-form" onSubmit={ event => handleSubmit(event, this.state.keywords) }>
                    <FormControl className="form-control search-input"
                                 name="searchInput"
                                 placeholder="Avengers ..."
                                 onChange={event => this.setState({keywords: event.target.value})}
                    />
                    <Button className="btn btn-success search-button"
                            type="submit">
                        <i className="fa fa-search"/>
                    </Button>
                </form>
            </div>
        );
    }
}

export default SearchBar;