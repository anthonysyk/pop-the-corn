import React, {Component} from 'react';
import {Row, Col} from 'react-bootstrap';
import Slider from '../SliderComponent';
import {style} from 'typestyle';

const HomePage = ({data}) =>
    <div className="section">
        <Row className={slider_row}>
            <Col md={12} xs={12}>
                <Slider title="Best Rated Movies"
                        cards={data.bestRatedMovies}/>
            </Col>
        </Row>
        <Row className={slider_row}>
            <Col md={12} xs={12}>
                <Slider title="Comedy"
                        cards={data.popularByGenre.Comedy}/>
            </Col>
        </Row>
        <Row className={slider_row}>
            <Col md={12} xs={12}>
                <Slider title="Family"
                        cards={data.popularByGenre.Family}/>
            </Col>
        </Row>
        <Row className={slider_row}>
            <Col md={12} xs={12}>
                <Slider title="Drama"
                        cards={data.popularByGenre.Drama}/>
            </Col>
        </Row>
        <Row className={slider_row}>
            <Col md={12} xs={12}>
                <Slider title="Thriller"
                        cards={data.popularByGenre.Thriller}/>
            </Col>
        </Row>
        <Row className={slider_row}>
            <Col md={12} xs={12}>
                <Slider title="Documentary"
                        cards={data.popularByGenre.Documentary}/>
            </Col>
        </Row>
    </div>;

export default HomePage;

const slider_row = style({
    width: '100%',
    padding: '2rem 8rem',
    margin: '0'
});