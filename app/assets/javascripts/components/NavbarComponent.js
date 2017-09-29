import React, {Component} from 'react';
import {Navbar, Nav, NavItem, NavDropdown, MenuItem} from 'react-bootstrap';
import {Link} from 'react-router-dom';

class NavbarComponent extends Component {

    render() {
        return (
            <Navbar inverse collapseOnSelect className="navbar-container">
                <Navbar.Header>
                    <Navbar.Brand>
                        <Link to="/#">Pop The Corn </Link>
                    </Navbar.Brand>
                    <Navbar.Toggle />
                </Navbar.Header>
                <Navbar.Collapse>
                    <Nav>
                        <NavDropdown eventKey={1} title="Data Science" id="basic-nav-dropdown">
                            <MenuItem eventKey={1.1}>Content-Based Recommendation</MenuItem>
                            <MenuItem eventKey={1.2}>Natural Language Processing</MenuItem>
                            <MenuItem divider/>
                            <MenuItem eventKey={1.3}>Collaborative Recommendation</MenuItem>
                        </NavDropdown>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        );
    }
}

export default NavbarComponent;