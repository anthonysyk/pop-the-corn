import React, {Component} from "react";
import {Nav, NavItem} from "react-bootstrap";
import TfidfComponent from './TfidfComponent';
import {connect} from 'react-redux';
import QuickRatingComponent from "./QuickRatingComponent";
import UserProfileRecommendationComponent from "./UserProfileRecommendationComponent";

@connect(
    state => ({
        machineLearningData: state.machineLearningData
    })
)

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
                    <NavItem eventKey="1">Users Preferences</NavItem>
                    <NavItem eventKey="2">TFIDF Similarity</NavItem>
                    <NavItem eventKey="3">LDA</NavItem>
                </Nav>
                <div className="recommendation-tab">
                    {
                        this.state.activeKey === "1" &&
                        <div>
                            <QuickRatingComponent testFeature="true"/>
                            {
                                this.props.machineLearningData.recommendations.length !== 0 &&
                                <UserProfileRecommendationComponent test="true"/>
                            }
                        </div>
                    }
                    {
                        this.state.activeKey === "2" &&
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