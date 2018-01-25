import React, {Component} from 'react';
import {Navbar, Nav, NavDropdown, MenuItem} from 'react-bootstrap';
import {Link} from 'react-router-dom';
import {style} from 'typestyle';

class NavbarComponent extends Component {

    render() {
        return (
            <Navbar inverse collapseOnSelect className={navbar_container}>
                <Navbar.Header>
                    <Navbar.Brand>
                        <Link to="/#">Pop The Corn </Link>
                    </Navbar.Brand>
                    <Navbar.Toggle />
                </Navbar.Header>
                <Navbar.Collapse>
                    <Nav>
                        <NavDropdown eventKey={1} title="Data Science" id="basic-nav-dropdown">
                            <MenuItem eventKey={1.1} href="/#/content-based">Content-Based Recommendation</MenuItem>
                            <MenuItem divider/>
                            <MenuItem eventKey={1.2} href="#">Collaborative Recommendation</MenuItem>
                        </NavDropdown>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );
    }
}

export default NavbarComponent;

const navbar_container = style({
    width: '100%',
    position: 'fixed',
    zIndex: '15',
    top: '0',
    left: '0'
});