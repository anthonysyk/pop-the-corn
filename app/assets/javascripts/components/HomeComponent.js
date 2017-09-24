import React, {Component} from 'react';
import {withRouter} from 'react-router'
import {Row, Col} from 'react-bootstrap';
import {Link} from 'react-router-dom'

class HomeComponent extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        const {data} = this.props;


        return (
            <div>
                {console.log(data)}
            </div>
        );
    }
}

export default HomeComponent;