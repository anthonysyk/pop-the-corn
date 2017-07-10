import React, {Component} from 'react';
import {Button, FormControl, Row, Col} from 'react-bootstrap';

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
            <Row>
                <Col md={12} xs={12}>
                    <div className="form-group search-container">
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
                </Col>
            </Row>
        );
    }
}

export default SearchBar;