import React, {Component} from 'react'
import {Button, ProgressBar, Image} from 'react-bootstrap'
import {getQuickRatingMovies, sendQuickRatingResult, getRecommendation} from '../../actions/actions';
import {connect} from 'react-redux';
import {Link} from 'react-router-dom';
import {Loader} from "react-loaders";
import CustomModal from "../generic/CustomModal";
import {style, media, classes} from 'typestyle';
import {desktop, mobile, smallMobile} from "../mediaquery";


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
                {this.state.showModal === true && movies.length > 0 && this.state.position < movies.length - 1 &&
                <CustomModal onHide={this.onHide.bind(this)} size="full">
                    <div className={quickRatingContainer}>
                        <div className={quickRatingImage}>
                            <span className="quick-rating-text">Let's know each other ...</span>
                            <ProgressBar className="progress-bar-custom" bsStyle="info"
                                         now={this.state.position}
                                         max={movies.length - 1}/>
                            <img src={movies[this.state.position].poster}/>
                        </div>
                        <div className={quickRatingTitle}>{movies[this.state.position].title}</div>
                        <div className={buttonContainer}>
                            <div className={classes(quickRatingButton, buttonBad)}
                                 onClick={() => this.onClick(movies[this.state.position], 1)}>Bad
                            </div>
                            <div className={classes(quickRatingButton, buttonMeh)}
                                 onClick={() => this.onClick(movies[this.state.position], 2)}>Meh
                            </div>
                            <div className={classes(quickRatingButton, buttonOkay)}
                                 onClick={() => this.onClick(movies[this.state.position], 3)}>Okay
                            </div>
                            <div className={classes(quickRatingButton, buttonGood)}
                                 onClick={() => this.onClick(movies[this.state.position], 4)}>Good
                            </div>
                        </div>
                    </div>
                </CustomModal>
                }
                {this.state.showModal === true && this.state.position === movies.length - 1 &&
                <CustomModal onHide={this.onHide.bind(this)} white size="medium">
                    <div>
                        {
                            Object.keys(this.props.machineLearningData.userProfile.genres).length !== 0 &&
                            <div>
                                {console.log(this.props.machineLearningData.userProfile)}
                                <p>It looks like you are more of
                                    a {sortedGenres.reverse().filter(item => item[1] !== 0).slice(0, 3).map(values => values[0]).join(", ")} person</p>
                                <p>It seems like we can throw movies
                                    with {sortedGenres.reverse().filter(item => item[1] !== 0).slice(0, 3).map(values => values[0]).join(", ")} to
                                    the garbage</p>
                                {/*{ this.props.machineLearningData.recommendations.length === 0 &&*/}
                                {/*<div>*/}
                                {/*<div className="loader-container_small">*/}
                                {/*<Loader type="ball-scale-ripple-multiple" active/>*/}
                                {/*</div>*/}
                                {/*</div>*/}
                                {/*}*/}
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
                </CustomModal>
                }
            </div>
        );
    }

}

export default QuickRatingComponent;

const quickRatingTitle = style({
    position: 'absolute',
    zIndex: '1',
    width: '100%',
    top: '110%'
});

const buttonContainer = style({
    width: '100%',
    position: 'fixed',
    left: '0',
    display: 'flex',
    justifyContent: 'center',
    background: 'linear-gradient(to bottom, transparent 0, rgba(0, 0, 0, 0.7) 20%, #000 100%)',
    alignItems: 'center',
    top: '50%',
    height: '60%',
    paddingBottom: '10%'
});

const quickRatingImage = style({
    img: {
        width: '100%'
    },
    '& .quick-rating-text': {
        fontWeight: '400',
        textAlign: 'center',
        fontSize: '24px',
        color: '#ccc',
        userSelect: 'none',
        whiteSpace: 'nowrap'
    }
});

const quickRatingContainer = style({
    position: 'relative',
    fontWeight: '400',
    textAlign: 'center',
    fontSize: '24px',
    color: '#ccc',
    '& .progress-bar-custom': {overflow: 'initial', marginBottom: '0'},
    width: 'min-content',
    margin: 'auto'
});

const quickRatingButton = style({
    '&:hover': {
        zIndex: '2'
    },
    textAlign: 'center',
    position: 'relative',
    padding: '4rem 0',
    fontWeight: '500',
    fontSize: '14px',
    color: '#fff',
    border: '3px solid #fff',
    backfaceVisibility: 'hidden',
    cursor: 'pointer',
    zIndex: '1',
    outline: '0',
    overflow: 'hidden',
    borderRadius: '999rem',
    transition: 'all .2s cubic-bezier(.175,.885,.32,2)',
    animation: 'animate-controls .2s ease-in-out',
    transform: 'translatez(0)',
    transformOrigin: 'center',
    userSelect: 'none',
    display: 'table-cell',
    width: '90px',
    height: '90px'
});

const buttonBad = style({
    backgroundColor: 'indianred',
    padding: '3.2rem 0',
    transform: 'scale(1.2)',
    '&:hover': {
        transform: 'scale(1.5)'
    }
});

const buttonMeh = style({
    backgroundColor: 'slategrey',
    '&:hover': {
        transform: 'scale(1.1)'
    }
});

const buttonOkay = style({
    backgroundColor: 'darkseagreen',
    '&:hover': {
        transform: 'scale(1.1)'
    }
});

const buttonGood = style({
    backgroundColor: 'forestgreen',
    padding: '3.2rem 0',
    transform: 'scale(1.2)',
    '&:hover': {
        transform: 'scale(1.5)'
    }
});

