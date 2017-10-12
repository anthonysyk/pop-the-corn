import React, {Component} from 'react'
import {Modal, Button, ProgressBar, Image} from 'react-bootstrap'
import {getQuickRatingMovies, sendQuickRatingResult} from '../../actions/actions';
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

        return (
            <div>
                {testFeature !== undefined && this.state.showModal === false && this.state.position === movies.length - 1 ?
                    <div className="loader-container">
                        <Loader type="ball-scale-ripple-multiple" active/>
                    </div> :
                    <Button bsSize="large" bsStyle="success" onClick={() => this.onShow()}>Get Me a Movie !</Button>

                }
                {console.log(this.state.showModal)}
                {
                    this.state.showModal === true &&
                    <CustomModal onHide={this.onHide.bind(this)}>
                        <div className="quick-rating">
                            {movies.length > 0 && this.state.position < movies.length - 1 &&
                            <div>
                                <div className="quick-rating-image">
                                    <ProgressBar className="no-margin-bottom" bsStyle="info" now={this.state.position} max={movies.length - 1}/>
                                    <Image responsive src={movies[this.state.position].poster}/>
                                    <span className="poster-name">{movies[this.state.position].title}</span>
                                    <div className="button__bad"
                                         onClick={() => this.onClick(movies[this.state.position], -2)}>Bad
                                    </div>
                                    <div className="button__meh"
                                         onClick={() => this.onClick(movies[this.state.position], -1)}>Meh
                                    </div>
                                    <div className="button__okay"
                                         onClick={() => this.onClick(movies[this.state.position], 1)}>Okay
                                    </div>
                                    <div className="button__good"
                                         onClick={() => this.onClick(movies[this.state.position], 2)}>Good
                                    </div>
                                </div>
                            </div>
                            }
                            {this.state.position === movies.length - 1 &&
                            <div>
                                {testFeature !== undefined ?
                                    <Button bsStyle="primary" onClick={() => this.onHide()}>View
                                        Recommendations</Button>
                                    :
                                    <Link to="/recommendations"><Button bsStyle="primary" onClick={() => this.onHide()}>View
                                        Recommendations</Button></Link>
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