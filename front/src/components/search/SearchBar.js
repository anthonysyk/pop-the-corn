import React, {Component} from 'react';
import {Button, FormControl, Row, Col} from 'react-bootstrap';
import {SuggestComponent} from './SuggestComponent';
import {searchMovies, suggest, getSimilarMoviesTfidf} from '../../actions/actions';
import ReactDOM from 'react-dom';
import {withRouter} from 'react-router';
import {connect} from 'react-redux';
import {style} from 'typestyle';


@withRouter
@connect(
    state => ({
        suggestionsData: state.suggestionsData
    })
)

class SearchBar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            keywords: "",
            showSuggestion: false,
            id: ""
        };
    }

    hideSuggestions(event) {
        if (!ReactDOM.findDOMNode(this).contains(event.target)) {
            this.setState({showSuggestion: false});
        }
    }

    applySuggestion(keywords, id) {
        this.setState({keywords: keywords, showSuggestion: false, id: id})
    }

    handleSubmit(event, keywords) {
        event.preventDefault();
        this.props.dispatch(searchMovies(keywords.toLowerCase()));
        this.setState({showSuggestion: false});
        window.location.hash !== '#/' && this.props.history.push('/');
    }

    render() {
        const {dispatch, suggestionsData, customHandleSubmit} = this.props;

        function handleSuggest(keywords) {
            dispatch(suggest(keywords))
        }

        return (
            <div>
                <Row>
                    <Col md={6} xs={12} className={searchContainer}>
                        <div className="form-group">
                            <form id="search-form"
                                  onSubmit={event => customHandleSubmit === undefined ? this.handleSubmit(event, this.state.keywords) : customHandleSubmit(event, this.state.id)}>
                                <div className="search-bar">
                                    <FormControl className="search-input"
                                                 placeholder="Avengers ..."
                                                 value={this.state.keywords}
                                                 onChange={event => {
                                                     this.setState({keywords: event.target.value});
                                                     handleSuggest(event.target.value);
                                                 }}
                                                 onFocus={() => this.setState({showSuggestion: true})}
                                    />
                                    {this.state.showSuggestion &&
                                    <SuggestComponent input={this.state.keywords}
                                                      suggestions={suggestionsData.suggestions}
                                                      showSuggestions={this.state.showSuggestion}
                                                      hideSuggestions={this.hideSuggestions.bind(this)}
                                                      applySuggestion={this.applySuggestion.bind(this)}
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
            </div>
        );
    }
}

export default SearchBar;

const searchContainer = style({
    textAlign: 'center',
    float: 'none',
    margin: '1rem auto',
    '& #search-form': {
        display: 'inline-flex',
        width: '100%',
        '& .search-bar': {
            width: '100%',
            height: '34px'
        },
        '& .search-input': {
            borderBottomRightRadius: 0,
            borderTopRightRadius: 0,
            outline: 'none !important',
            borderColor: 'lightgrey',
            boxShadow: 'none'
        },
        '& .search-input__with-suggest': {
            borderBottomRightRadius: 0,
            borderTopRightRadius: 0,
            outline: 'none !important',
            borderColor: 'lightgrey',
            boxShadow: 'none',
            borderBottomLeftRadius: 0
        },
        '& .search-button': {
            width: '17%',
            borderBottomLeftRadius: 0,
            borderTopLeftRadius: 0
        }
    }
});