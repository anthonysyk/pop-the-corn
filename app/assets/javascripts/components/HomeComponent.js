import React, {Component} from 'react';
import {withRouter} from 'react-router'
import {Row, Col} from 'react-bootstrap';
import {Link} from 'react-router-dom'
import SliderComponent from './SliderComponent'

class HomeComponent extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        const {data} = this.props;

        return (
            <div>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <SliderComponent title="Best Rated Movies"
                                         movies={data.bestRatedMovies}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <SliderComponent title="Comedy"
                                         movies={data.popularByGenre.Comedy}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <SliderComponent title="Family"
                                         movies={data.popularByGenre.Family}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <SliderComponent title="Drama"
                                         movies={data.popularByGenre.Drama}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <SliderComponent title="Thriller"
                                         movies={data.popularByGenre.Thriller}/>
                    </Col>
                </Row>
                <Row className="slider-row">
                    <Col md={12} xs={12}>
                        <SliderComponent title="Documentary"
                                         movies={data.popularByGenre.Documentary}/>
                    </Col>
                </Row>
            </div>
        );
    }
}

export default HomeComponent;