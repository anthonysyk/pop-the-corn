import React, {Component} from 'react';
import {connect} from 'react-redux'
import {getMovieDetails} from '../../actions/actions';
import {Col, Row} from 'react-bootstrap';
import {withRouter} from 'react-router'
import {media, style} from 'typestyle';
import ReactStars from 'react-stars';
import {darkgrey, lightgrey} from "../../variables";
import {desktop, mobile} from "../mediaquery";

@withRouter
@connect(
    state => ({
        movieData: state.movieData
    }),
)

class DetailPage extends Component {

    constructor(props) {
        super(props)
    }

    componentWillMount() {
        const id = this.props.match.params.id;
        this.props.dispatch(getMovieDetails(id))
    }

    componentWillUpdate(nextProps) {
        const nextId = nextProps.match.params.id;
        const currentId = this.props.match.params.id;
        currentId !== nextId && this.props.dispatch(getMovieDetails(nextId))
    }

    render() {
        const {movieData} = this.props;

        return (
            <div className="section"
                 style={{
                     background: `radial-gradient(circle at 20% 50%, rgba(16.08%, 17.25%, 18.43%, 0.98) 0%, rgba(16.08%, 17.25%, 18.43%, 0.88) 100%),url(${movieData.movie.backdrop}) no-repeat center`,
                     backgroundSize: 'cover'
                 }}>
                {movieData.movie.title &&
                <div className={container}>
                    <div className={movie_card}>
                        <div>
                            <img className={image}
                                 src={movieData.movie.poster ? movieData.movie.poster : "/assets/images/no-image.svg"}/>
                        </div>
                        <div className={description}>
                            <Row>
                                <Col md={12} sm={12} className={header}>
                                    <span className="title">
                                    <h1><b>{movieData.movie.title}</b></h1>
                                        &nbsp;
                                        <h2>({movieData.movie.release_date.substr(0, 4)})</h2>
                                    </span>
                                    <ReactStars
                                        className={rating}
                                        count={5}
                                        value={movieData.movie.vote_average / 2}
                                        size={30}
                                        edit={false}
                                        color2={'#ffd700'}/>
                                </Col>
                            </Row>
                            <div className={body}>
                                <h3>Overview</h3>
                                <br/>
                                <p>{movieData.movie.overview}</p>
                            </div>
                        </div>
                    </div>
                </div>
                }
            </div>

        );
    }

}

export default DetailPage;

const container = style({
    color: 'white'
});

const header = style({
        display: 'flex',
        justifyContent: 'center',
        flexWrap: 'wrap',
        h1: {
            display: 'inline'
        },
        h2: {
            display: 'inline',
            color: lightgrey
        },
        '& .title': {
            marginBottom: '2rem'
        }
    }, media(mobile, {display: 'block'})
);

const body = style({
}, media(mobile, {marginTop: '7rem'}));

const image = style({
    width: '50vw'
}, media(mobile, {width: '25rem'}));

const movie_card = style({
    display: 'flex',
    alignItems: 'center',
    flexDirection: 'column'
}, media(mobile, {flexDirection: 'row'}));

const description = style({
    height: '40rem',
    padding: '2rem',
    textAlign: 'center'
}, media(mobile, {textAlign: "initial"}));

const rating = style({
    float: 'right'
});