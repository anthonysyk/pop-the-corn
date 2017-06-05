import React, {Component} from 'react';
import {render} from 'react-dom';

class Spa extends Component {

    render() {
        return (
            <div id="wrapper">
                <div className="page-header main-title">
                    <h1><i className="fa fa-film"/>&nbsp;&nbsp;Pop the corn</h1>
                    <p className="lead">Get me the movie, I'm making popcorn !</p>
                </div>


                <div id="page-wrapper">
                    <div className="container-fluid">
                        { this.props.children }
                    </div>
                </div>
            </div>
        );
    }

}

export default Spa;