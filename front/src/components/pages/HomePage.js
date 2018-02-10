import React, {Component} from 'react';
import {Row, Col} from 'react-bootstrap';
import Slider from '../SliderComponent';
import {style} from 'typestyle';

const HomePage = ({data}) => {
    const mapping = new Map();

    mapping.set("Best Rated Movies", data.bestRatedMovies);
    mapping.set("Action", data.popularByGenre.Action);
    mapping.set("Comedy", data.popularByGenre.Comedy);
    mapping.set("Family", data.popularByGenre.Family);
    mapping.set("Drama", data.popularByGenre.Drama);
    mapping.set("Thriller", data.popularByGenre.Thriller);
    mapping.set("Horror", data.popularByGenre.Horror);
    mapping.set("Documentary", data.popularByGenre.Documentary);

    return (
        <div>
            {
                // Pour pouvoir utiliser map
                Array.from(mapping).filter(([key, value]) => value !== undefined).map(([key, value]) =>
                    <Row className={slider_row} key={key}>
                        <Col md={12} xs={12}>
                            <Slider title={key}
                                    cards={value}/>
                        </Col>
                    </Row>
                )
            }
        </div>
    );
};

export default HomePage;

const slider_row = style({
    width: '100%',
    padding: '2rem 8rem',
    margin: '0'
});