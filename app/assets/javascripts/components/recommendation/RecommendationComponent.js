import React, {Component} from "react";
import {Nav, NavItem} from "react-bootstrap";
import TfidfComponent from './TfidfComponent';

class RecommendationComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {
            activeKey: "1"
        }
    }

    handleSelect(eventKey) {
        event.preventDefault();
        this.setState({activeKey: eventKey});
    }


    render() {

        return (
            <div className="recommendation">
                <Nav bsStyle="tabs" justified activeKey={this.state.activeKey} onSelect={this.handleSelect.bind(this)}>
                    <NavItem eventKey="1">TFIDF Similarity</NavItem>
                    <NavItem eventKey="2">Users Preferences</NavItem>
                    <NavItem eventKey="3">LDA</NavItem>
                </Nav>
                <div className="recommendation-tab">
                    {
                        this.state.activeKey === "1" &&
                        <TfidfComponent />
                    }
                    {
                        this.state.activeKey === "3" &&
                        <div>
                            under construction ...
                        </div>

                    }
                </div>
            </div>
        );

    }
}

export default RecommendationComponent;