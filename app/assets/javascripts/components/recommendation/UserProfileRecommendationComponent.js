import React, {Component} from "react";
import {Image, Table} from "react-bootstrap";
import {Loader} from "react-loaders";
import {connect} from "react-redux";
import ReactCSSTransitionGroup from "react-addons-css-transition-group";
import {getRecommendation} from '../../actions/actions';


@connect(
    state => ({
        machineLearningData: state.machineLearningData
    })
)

class UserProfileRecommendationComponent extends Component {

    constructor(props) {
        super(props)
    }

    componentWillMount() {
        this.props.machineLearningData.recommendations.length === 0 && this.props.dispatch(getRecommendation(this.props.machineLearningData.userProfile.movieId, 3));
    }

    render() {
        const userProfile = this.props.machineLearningData.userProfile;
        const movies = this.props.machineLearningData.recommendations;
        const {test} = this.props;

        return (
            <div>
                {movies.length !== 0 &&
                    <div className="recommendation-container">
                        {
                            test !== undefined &&
                            <Table striped bordered condensed hover responsive>
                                <thead>
                                <tr className="text-center">
                                    <th>#</th>
                                    <th>Title</th>
                                    <th>Popularity</th>
                                    {Object.keys(userProfile.genres).map((genre, k) =>
                                        <th key={k}>{genre}</th>
                                    )
                                    }
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td>Input</td>
                                    <td>User Profile</td>
                                    <td>NaN</td>
                                    {Object.values(userProfile.genres).map((value, k) => <td key={k}>{Math.round(value * 100) / 100}</td>)}
                                </tr>
                                {
                                    movies.map((movie, key) =>
                                        <tr key={key}>
                                            <td>{movie.id}</td>
                                            <td>{movie.title}</td>
                                            <td>{movie.popularity}</td>
                                            {
                                                Object.values(movie.genreWeights).map((value, k) =>
                                                    <td key={k}>{Math.round(value * 100) / 100}</td>
                                                )
                                            }
                                        </tr>
                                    )
                                }
                                </tbody>
                            </Table>
                        }
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