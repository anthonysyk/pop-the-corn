import React, {Component} from 'react'

class CustomModal extends Component {

    render() {
        const {children, onHide} = this.props;

        return (
            <div>
                <div className="modal">
                    <span className="close-modal" onClick={() => onHide()}>&times;</span>
                    <div className="custom-modal-content">
                        {children}
                    </div>
                </div>
            </div>
        );
    }

}

export default CustomModal;