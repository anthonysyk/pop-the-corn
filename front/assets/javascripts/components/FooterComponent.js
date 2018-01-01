import React, {Component} from 'react'

class FooterComponent extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
          <div className="footer">
              <span className="footer-title">Pop the corn</span>
              <div className="circle-border-logo"><a href="/"><i className="logo"/></a></div>
          </div>
        );
    }
}

export default FooterComponent;