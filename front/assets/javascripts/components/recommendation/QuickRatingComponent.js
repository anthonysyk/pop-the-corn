import React, {Component} from 'react'
import {Button, ProgressBar, Image} from 'react-bootstrap'
import {getQuickRatingMovies, sendQuickRatingResult, getRecommendation} from '../../actions/actions';
import {connect} from 'react-redux';
import {Link} from 'react-router-dom';
import {Loader} from "react-loaders";
import CustomModal from "../generic/CustomModal";


@connect(
    state => ({
        machineLearningData: state.machineLearningData
    })
)

class QuickRatingComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {
            showModal: false,
            position: 0,
            result: []
        }
    }

    componentWillMount() {
        this.props.dispatch(getQuickRatingMovies());
    }

    onShow() {
        this.setState({showModal: true})
    }

    onHide() {
        this.setState({showModal: false})
    }

    onClick(movie, rating) {
        movie.rating = rating;
        this.state.result.push(movie);
        this.setState({
            position: this.state.position + 1
        });
        if (this.state.position === this.props.machineLearningData.quickRatingMovies.length - 2) {
            this.props.dispatch(sendQuickRatingResult(this.state.result))
        }
    }

    render() {

        const movies = this.props.machineLearningData.quickRatingMovies;
        const {testFeature} = this.props;

        function sortProperties(obj) {
            // convert object into array
            const sortable = [];
            for (const key in obj)
                if (obj.hasOwnProperty(key))
                    sortable.push([key, obj[key]]); // each item is an array in format [key, value]

            // sort items by value
            sortable.sort(function (a, b) {
                return a[1] - b[1]; // compare numbers
            });
            return sortable; // array in format [ [ key1, val1 ], [ key2, val2 ], ... ]
        }

        const sortedGenres = Object.keys(this.props.machineLearningData.userProfile.genres).length !== 0 && sortProperties(this.props.machineLearningData.userProfile.genres);
        // const genreLike = sortedGenres !== undefined && sortedGenres.reverse.slice(5);
        // const genreDislike = sortedGenres !== undefined && sortedGenres.slice(5);


        return (
            <div>
                {this.state.position !== movies.length - 1 && this.props.machineLearningData.recommendations.length === 0 &&
                <Button bsSize="large" bsStyle="success" onClick={() => this.onShow()}>Get Me a Movie !</Button>

                }
                {
                    this.state.showModal === true &&
                    <CustomModal onHide={this.onHide.bind(this)}>
                        <div className="quick-rating">
                            {movies.length > 0 && this.state.position < movies.length - 1 &&
                            <div>
                                <div className="quick-rating-image">
                                    <span className="quick-rating-text">Let's know each other ...</span>
                                    <ProgressBar className="no-margin-bottom" bsStyle="info" now={this.state.position}
                                                 max={movies.length - 1}/>
                                    <Image responsive src={movies[this.state.position].poster}/>
                                    <span className="poster-name">{movies[this.state.position].title}</span>
                                    <div className="button__bad"
                                         onClick={() => this.onClick(movies[this.state.position], 1)}>Bad
                                    </div>
                                    <div className="button__meh"
                                         onClick={() => this.onClick(movies[this.state.position], 2)}>Meh
                                    </div>
                                    <div className="button__okay"
                                         onClick={() => this.onClick(movies[this.state.position], 3)}>Okay
                                    </div>
                                    <div className="button__good"
                                         onClick={() => this.onClick(movies[this.state.position], 4)}>Good
                                    </div>
                                </div>
                            </div>
                            }
                            {this.state.position === movies.length - 1 &&
                            <div>
                                {
                                    Object.keys(this.props.machineLearningData.userProfile.genres).length !== 0 &&
                                    <div className="custom-modal-content__white">
                                        {console.log(this.props.machineLearningData.userProfile)}
                                        <p>It looks like you are more of
                                            a {sortedGenres.reverse().filter(item => item[1] !== 0).slice(0, 3).map(values => values[0]).join(", ")} person</p>
                                        <p>It seems like we can throw movies
                                            with {sortedGenres.reverse().filter(item => item[1] !== 0).slice(0, 3).map(values => values[0]).join(", ")} to the garbage</p>
                                        { this.props.machineLearningData.recommendations.length === 0 &&
                                        <div>
                                            <div className="loader-container_small">
                                                <Loader type="ball-scale-ripple-multiple" active/>
                                            </div>
                                        </div>
                                        }
                                        <div className="custom-modal-footer">
                                            <Link to={testFeature !== undefined ? "#" : "/recommendations"} replace>
                                                <Button bsStyle="primary" onClick={() => this.onHide()}
                                                        disabled={this.props.machineLearningData.recommendations.length === 0}
                                                >View Recommendations</Button>
                                            </Link>
                                        </div>
                                    </div>
                                }
                            </div>
                            }
                        </div>
                    </CustomModal>
                }
            </div>
        );
    }

}

export default QuickRatingComponent;