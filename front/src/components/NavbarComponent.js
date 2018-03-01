import React, {Component} from 'react';
import {classes, media, style} from 'typestyle';
import {mobile} from "./mediaquery";


class NavbarComponent extends Component {
    constructor(props) {
        super(props);
        this.handleMenuToggle = this.handleMenuToggle.bind(this);
        this.resetMobileFormat = this.resetMobileFormat.bind(this);
        this.handleOutsideClick = this.handleOutsideClick.bind(this);
        this.hideOnScroll = this.hideOnScroll.bind(this);
        this.state = {
            shouldHideMenu: true,
            height: props.height,
            width: props.width
        }
    }

    componentDidMount() {
        window.addEventListener('resize', this.resetMobileFormat);
        window.addEventListener('scroll', this.hideOnScroll);
    }

    componentWillUnmount() {
        window.removeEventListener('resize', this.resetMobileFormat);
        window.addEventListener('scroll', this.hideOnScroll);
    }

    hideOnScroll() {
        if(window.pageYOffset > 30) this.setState({shouldHideMenu: true})
    }

    handleMenuToggle() {
        if (this.state.shouldHideMenu) {
            // attach/remove event handler
            document.addEventListener('click', this.handleOutsideClick, false);
        } else {
            document.removeEventListener('click', this.handleOutsideClick, false);
        }
        this.setState({shouldHideMenu: !this.state.shouldHideMenu})
    }

    handleOutsideClick(e) {
        // ignore clicks on the component itself
        if (this.menu.contains(e.target)) {
            return;
        }

        this.handleMenuToggle();
    }

    resetMobileFormat() {
        this.setState({height: window.innerHeight, width: window.innerWidth});
        console.log(this.state.width >= mobile.intInPixel);
        if (this.state.width >= mobile.intInPixel) {
            this.setState({shouldHideMenu: true})
        }
    }

    render() {
        return (
            <div className={navbar_container}>
                <div className={classes(grid_container, this.state.shouldHideMenu ? hide_menu : slow)}
                     ref={menu => {
                         this.menu = menu;
                     }}>
                    <div className={grid_child}><span className={company_name_title}>Pop The Corn</span></div>
                    <div className={grid_child}><a href='/content-based'>Data Science</a></div>
                    <div className={grid_child}><i className="fa fa-user-circle" aria-hidden="true"/>&nbsp;&nbsp;Sign In</div>
                </div>
                <button type="button" className={classes("navbar-toggle", !this.state.shouldHideMenu && position_fixed)} data-toggle="collapse"
                        onClick={this.handleMenuToggle}>
                    <span className="icon-bar"/>
                    <span className="icon-bar"/>
                    <span className="icon-bar"/>
                </button>
            </div>
        );
    }
}

export default NavbarComponent;

const navbar_container = style({
    minHeight: '5rem',
    zIndex: '10',
    display: 'flex',
    justifyContent: 'flex-end',
    position: 'relative',
    top: '0',
    right: '0',
    '& .icon-bar': {
        background: 'white !important'
    },
    '& .navbar-toggle': {
        zIndex: '15'
    },
    a: {
        textDecoration: 'none',
        color: 'inherit'
    }
}, media(mobile, {
    width: '100%',
    position: 'relative',
    zIndex: '15',
    top: '0',
    left: '0',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-end',
    padding: '0 2rem',
}));

const position_fixed = style({
    position: 'fixed',
    right: '0'
});

const grid_container = style({
    display: 'flex',
    flexDirection: 'column',
    flexWrap: 'nowrap',
    justifyContent: 'flex-start',
    alignItems: 'stretch',
    alignContent: 'space-between',
    position: 'fixed',
    background: '#222',
    zIndex: '1',
    width: '100vw',
    overflow: 'hidden',
    maxHeight: '100rem',
    transition: 'max-height .3s ease'
}, media(mobile, {
    display: 'flex',
    // justifyContent: 'space-evenly',
    justifyContent: 'flex-end',
    position: 'relative',
    background: 'transparent',
    flexDirection: 'row',
    maxHeight: 'max-content'
}));

const slow = style({
    transition: 'max-height 1s ease-in'
});

const grid_child = style({
    fontSize: '1.6rem',
    cursor: 'pointer',
    '&:hover': {
        // background: 'rgba(25, 25, 25, 0.4)',
        color: 'white'
    },
    color: '#ccc',
    textAlign: 'left',
    padding: '1.5rem'
}, media(mobile, {
    padding: '1.5rem 1rem',
    fontSize: '1.5rem',
    cursor: 'pointer',
    '&:hover': {
        // background: 'rgba(25, 25, 25, 0.4)',
        color: 'white'
    },
    color: '#ccc',
}));

const hide_menu = style({
    maxHeight: '0'
}, media(mobile, {maxHeight: 'max-content'}));

const company_name_title = style({
    margin: '-1px',
    fontSize: '2rem',
    whiteSpace: 'nowrap',
    fontWeight: "bold"
}, media(mobile, {display: 'none'}));