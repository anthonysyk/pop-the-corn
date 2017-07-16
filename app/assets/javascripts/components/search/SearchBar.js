import React, {Component} from 'react';
import {Button, FormControl, Row, Col} from 'react-bootstrap';
import {SuggestComponent} from './SuggestComponent';
import ReactDOM from 'react-dom';

class SearchBar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            keywords: "",
            showSuggestion: false
        };
    }

    hideSuggestions(event) {
        if (!ReactDOM.findDOMNode(this).contains(event.target)) {
            this.setState({showSuggestion: false});
        }
    }

    applySuggestion(keywords) {
        this.setState({keywords: keywords, showSuggestion: false})
    }

    handleSubmit(event, keywords) {
        const {onSubmit} = this.props;
        event.preventDefault();
        onSubmit(keywords);
        this.setState({showSuggestion: false});
    }

    render() {
        const {onChange, suggestions} = this.props;

        return (
            <Row>
                <Col md={6} xs={6} className="search-container">
                    <div className="form-group">
                        <form id="search-form" onSubmit={ event => this.handleSubmit(event, this.state.keywords) }>
                            <div className="search-bar">
                                <FormControl className="search-input"
                                             placeholder="Avengers ..."
                                             value={this.state.keywords}
                                             onChange={event => {
                                                 this.setState({keywords: event.target.value});
                                                 onChange(event.target.value);
                                             }}
                                             onFocus={() => this.setState({showSuggestion: true})}
                                />
                                { this.state.showSuggestion &&
                                <SuggestComponent input={this.state.keywords} suggestions={suggestions}
                                                  showSuggestions={this.state.showSuggestion}
                                                  hideSuggestions={ this.hideSuggestions.bind(this) }
                                                  applySuggestion={ this.applySuggestion.bind(this) }
                                />
                                }
                            </div>
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