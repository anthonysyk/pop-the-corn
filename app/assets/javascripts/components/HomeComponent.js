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
            </div>
        );
    }
}

export default HomeComponent;