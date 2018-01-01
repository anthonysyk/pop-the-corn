import React, {Component} from 'react'
import * as variables from '../../variables';
import { Row, Col } from 'react-bootstrap'

class FooterComponent extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className="footer">
                <Row className="section">
                    <Col lg={4}>
                        <h3>About This Site</h3>
                        <ul>
                            <li>Jobs</li>
                            <li>Rules</li>
                            <li>Help</li>
                            <li>History</li>
                        </ul>
                    </Col>
                    <Col lg={4}>
                        <h3>Discover</h3>
                        <ul>
                            <li>Jobs</li>
                            <li>Rules</li>
                            <li>Help</li>
                            <li>History</li>
                        </ul>
                    </Col>
                    <Col lg={4}>
                        <h3>Contact</h3>
                        <ul>
                            <li>Jobs</li>
                            <li>Rules</li>
                            <li>Help</li>
                            <li>History</li>
                        </ul>
                    </Col>
                </Row>
                <div className="copyright-container"><i className="logo"/><span className="brand-copyright">© {variables.site_name}</span></div>
            </div>
        );
    }
}

export default FooterComponent;
