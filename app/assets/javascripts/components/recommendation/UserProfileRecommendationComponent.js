import React, {Component} from "react";
import {Image} from "react-bootstrap";
import {Loader} from "react-loaders";
import {connect} from "react-redux";
import ReactCSSTransitionGroup from "react-addons-css-transition-group";


@connect(
    state => ({
        machineLearningData: state.machineLearningData
    })
)

class UserProfileRecommendationComponent extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        const movies = this.props.machineLearningData.userProfileMovies;

        return (
            <div>
                {movies.length === 0 ?
                    <div className="loader-container">
                        <Loader type="ball-scale-ripple-multiple" active/>
                    </div>
                    :
                    <div className="recommendation-container">
                        <div className="recommendation-results">
                            <ReactCSSTransitionGroup
                                transitionName="apparition"
                                transitionAppear={true}
                                transitionEnterTimeout={500}
                                transitionLeaveTimeout={300}
                                transitionAppearTimeout={300}
                            >
                                {
                                    movies.map((movie, index) =>
                                        <div key={index} className="recommendation-item">
                                            <Image src={movie.poster}/>
                                        </div>
                                    )
                                }
                            </ReactCSSTransitionGroup>
                        </div>
                    </div>
                }
            </div>
        );
    }

}

export default UserProfileRecommendationComponent;