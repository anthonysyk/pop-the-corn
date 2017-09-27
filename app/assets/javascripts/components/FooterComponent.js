import React, {Component} from 'react'

class FooterComponent extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
          <div className="footer">
              <span className="footer-title">Pop the corn</span>
              <a href="/"><div className="circle-border-logo"><i className="logo"/></div></a>
          </div>
        );
    }
}

export default FooterComponent;