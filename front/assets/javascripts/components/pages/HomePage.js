import React, {Component} from 'react';
import {Row, Col} from 'react-bootstrap';
import Slider from '../SliderComponent'

class HomePage extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        const {data} = this.props;

        return (
            <div>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <Slider title="Best Rated Movies"
                                cards={data.bestRatedMovies}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <Slider title="Comedy"
                                cards={data.popularByGenre.Comedy}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <Slider title="Family"
                                cards={data.popularByGenre.Family}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <Slider title="Drama"
                                cards={data.popularByGenre.Drama}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <Slider title="Thriller"
                                cards={data.popularByGenre.Thriller}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <Slider title="Documentary"
                                cards={data.popularByGenre.Documentary}/>
                    </Col>
                </Row>
            </div>
        );
    }
}

export default HomePage;