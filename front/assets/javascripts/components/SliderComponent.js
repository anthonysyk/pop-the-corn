import React, {Component} from 'react';
import {Image, Row, Col} from 'react-bootstrap';
import ReactCSSTransitionGroup from 'react-addons-css-transition-group';
import {Motion, spring} from 'react-motion';
import {Link} from 'react-router-dom'


class SliderComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {
            position: 0
        };
    }

    slideRight() {
        this.setState({position: this.state.position - 182.6});
    }

    slideLeft() {
        this.setState({position: this.state.position + 182.6});
    }


    render() {

        const {movies, title} = this.props;
        const translateX = this.state.position;


        return (
            <div>
                <h2>{title}</h2>
                { this.state.position < 0 &&
                <div className="left-arrow">
                    <i className="fa fa-chevron-left" aria-hidden="true"
                       onClick={() => this.slideLeft()}
                    />
                </div>
                }
                <div className="slider-container">
                    <Motion style={{x: spring(translateX)}}>
                        {({x}) =>
                            <div className="slider-carousel" style={{transform: `translateX(${x}px)`}}>
                                <ReactCSSTransitionGroup
                                    transitionName="apparition"
                                    transitionAppear={true}
                                    transitionEnterTimeout={500}
                                    transitionLeaveTimeout={300}
                                    transitionAppearTimeout={300}
                                >
                                    {
                                        movies.map((movie, index) =>
                                            <div key={index} className="slider-card">
                                                <Link to={`/details/${movie.id}`}>
                                                    <Image src={movie.poster}/>
                                                </Link>
                                            </div>
                                        )
                                    }
                                </ReactCSSTransitionGroup>
                            </div>
                        }</Motion>
                </div>
                { this.state.position < movies.length - 1 && movies.length > 6 &&
                    <div className="right-arrow">
                        <i className="fa fa-chevron-right" aria-hidden="true"
                           onClick={() => this.slideRight()}
                        />
                    </div>
                }
            </div>
        );
    }
}

export default SliderComponent;