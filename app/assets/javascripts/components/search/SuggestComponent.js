import React, {Component} from 'react';
import {Media} from 'react-bootstrap';
import match from 'autosuggest-highlight/match'
import parse from 'autosuggest-highlight/parse'


function renderSuggestion(suggestion, input) {
    const matches = match(suggestion, input);
    const parts = parse(suggestion, matches);

    return (
        <span className="suggestion-content">
            <span className="name">
        {
            parts.map((part, index) => {
                const className = part.highlight ? 'highlight' : null;

                return (
                    <span className={className} key={index}>{part.text}</span>
                );
            })
        }
            </span>
        </span>
    );
}


export class SuggestComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
            input: "",
            suggestions: []
        };
    }

    componentWillMount() {
        // add event listener for clicks
        document.addEventListener('click', this.handleClick, false);
    }

    componentWillUnmount() {
        // make sure you remove the listener when the component is destroyed
        document.removeEventListener('click', this.handleClick, false);
    }

    handleClick = e => {
        const {hideSuggestions} = this.props;
        hideSuggestions(e)
    };

    render() {
        const {input, suggestions, applySuggestion} = this.props;
        return (
            <div className="suggest-list">
                {input &&
                <ul className="no-padding">
                    {suggestions.map((suggestion, index) =>
                        <li key={index} onClick={() => applySuggestion(suggestion.title, suggestion.id)}>
                            <Media>
                                <Media.Left align="middle">
                                    <img height={60} width={45} src={suggestion.url}/>
                                </Media.Left>
                                <Media.Body>
                                    <span>{renderSuggestion(suggestion.title, input)}</span>
                                </Media.Body>
                            </Media>
                        </li>
                    )}
                </ul>
                }
            </div>
        )
    }

}