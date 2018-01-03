import React, {Component} from 'react';
import ReactCSSTransitionGroup from 'react-addons-css-transition-group';
import {Motion, spring} from 'react-motion';
import {Link} from 'react-router-dom'


// https://www.andrewhfarmer.com/react-image-gallery/
function imagesLoaded(parentNode) {
    const imgElements = parentNode.querySelectorAll('img');
    for (const img of imgElements) {
        if (!img.complete) {
            return false;
        }
    }
    return true;
}


class SliderComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {
            position: 0,
            loading: true
        };
    }

    slideRight() {
        this.setState({position: this.state.position - 182.6});
    }

    slideLeft() {
        this.setState({position: this.state.position + 182.6});
    }

    handleImageChange() {
        this.setState({
            loading: imagesLoaded(this.sliderCarouselRef)
        })
    };


    // TODO: FIX ARROWS

    render() {

        const {cards, title} = this.props;

        const translateX = this.state.position;

        return (
            <div>
                <h2>{title}</h2>
                {/*{ this.state.position < 0 &&*/}
                {/*<div className="left-arrow">*/}
                {/*<i className="fa fa-chevron-left" aria-hidden="true"*/}
                {/*onClick={() => this.slideLeft()}*/}
                {/*/>*/}
                {/*</div>*/}
                {/*}*/}
                <div className="slider-container">
                    <Motion style={{x: spring(translateX)}}>
                        {({x}) =>
                            <div className="slider-carousel"
                                 ref={(ref) => this.sliderCarouselRef = ref}
                                 style={{transform: `translateX(${x}px)`}}>
                                <ReactCSSTransitionGroup
                                    transitionName="apparition"
                                    transitionAppear={true}
                                    transitionEnterTimeout={500}
                                    transitionLeaveTimeout={300}
                                    transitionAppearTimeout={300}
                                >
                                    {
                                        cards.map((card, index) =>
                                            <div key={index} className="slider-card">
                                                <Link to={`/details/${card.id}`}>
                                                    <img
                                                        src={card.poster}
                                                        onLoad={this.handleImageChange.bind(this)}
                                                    />
                                                </Link>
                                            </div>
                                        )
                                    }
                                </ReactCSSTransitionGroup>
                            </div>
                        }</Motion>
                </div>
                {/*{ this.state.position < cards.length - 1 && cards.length > 6 &&*/}
                {/*<div className="right-arrow">*/}
                {/*<i className="fa fa-chevron-right" aria-hidden="true"*/}
                {/*onClick={() => this.slideRight()}*/}
                {/*/>*/}
                {/*</div>*/}
                {/*}*/}
            </div>
        );
    }
}

export default SliderComponent;