import React, {Component} from 'react';
import {connect} from 'react-redux'
import {getMovieDetails} from '../../actions/actions';
import {Image, Row, Col} from 'react-bootstrap';
import no_image from '../../../images/no-image.svg';
import { withRouter } from 'react-router'

@withRouter
@connect(
    state => ({
        movieData: state.movieData
    }),
)

class DetailComponent extends Component {

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
            <div className="movie-details">
                <Row>
                    <Col xs={12} md={9}>
                        <Row>
                            <Col xs={12} md={12}>
                                <h1>{movieData.movie.title}</h1>
                            </Col>
                        </Row>
                        <Row className="movie-poster">
                            <Col xs={4} md={2}>
                                <Image src={movieData.movie.poster ? movieData.movie.poster : no_image} rounded/>
                            </Col>
                            <Col xs={8} md={10}>
                                {movieData.movie.overview}
                            </Col>
                        </Row>
                    </Col>
                    <Col xs={0} md={3} className="details__sidebar">
                        <div>
                            <h3>Related Movies:</h3>
                            <ul>
                                <li>
                                    Under construction ...
                                </li>
                            </ul>
                        </div>
                    </Col>
                </Row>
            </div>
        );
    }

}

export default DetailComponent;
