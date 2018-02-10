import React from 'react';
import {style, classes} from 'typestyle';
import {Row} from 'react-bootstrap';
import * as variables from '../../variables';

/**
 *
 * @param children
 * @param onHide
 * @param white
 * @param title
 * @param size : small, medium or full
 * @returns {*}
 * @constructor
 */
const CustomModal = ({children, onHide, white, title, size}) =>
    <div className={modal}>
        <div className={classes(`custom_modal_container__${size}`, white && font_grey, white && white_bg)}>
            <div className="modal_container">
                <div className={classes('close_modal', !white && font_white)}><span
                    onClick={() => onHide()}>&times;</span></div>
                {title ?
                    <Row className="modal_header">
                        <h3 className='modal_title'>title</h3>
                    </Row>
                    : null}
                <Row className="modal_body">
                    {children}
                </Row>
            </div>
        </div>
    </div>;

export default CustomModal;

const font_white = style({color: 'white'});

const font_grey = style({color: variables.darkgrey});

const white_bg = style({backgroundColor: 'white'});

const modal = style({
    /* The Modal (background) */
    textAlign: 'justify',
    position: 'fixed', /* Stay in place */
    zIndex: '15', /* Sit on top */
    top: '0',
    left: '0',
    right: '0',
    bottom: '0',
    width: '100%', /* Full width */
    height: '100%', /* Full height */
    overflow: 'auto', /* Enable scroll if needed */
    // backgroundColor: rgb(0,0,0), /* Fallback color */
    backgroundColor: 'rgba(0, 0, 0, 0.7)' /* Black w/ opacity */,
    $nest: {
        /* Modal Content/Box */
        '& .custom_modal_container__small': {
            margin: '5% auto', /* 15% from the top and centered */
            width: '40%%', /* Could be more or less, depending on screen size */
            overflow: 'hidden',
            borderRadius: '1rem',
            position: 'relative',
            transform: 'translate(0,0)'
        },
        '& .custom_modal_container__medium': {
            margin: '5% auto', /* 15% from the top and centered */
            width: '80%', /* Could be more or less, depending on screen size */
            overflow: 'hidden',
            borderRadius: '1rem',
            position: 'relative',
            transform: 'translate(0,0)'
        },
        '& .custom_modal_container__full': {
            margin: '5% auto', /* 15% from the top and centered */
            width: '100%', /* Could be more or less, depending on screen size */
            height: '100%',
            overflow: 'hidden',
            borderRadius: '1rem',
            position: 'relative',
            transform: 'translate(0,0)'
        },
        '& .modal_container': {
            padding: '3.5rem 1.5rem',
            height: '100%'
        },
        '& .modal_title': {
            padding: '.5rem 0 1.5rem',
            borderBottom: `1px solid ${variables.lightgrey}`,
            paddingBottom: '1.5rem',
            paddingLeft: '1.5rem',
            margin: 'auto'
        },
        /* The Close Button */
        '& .close_modal': {
            fontSize: '40px',
            position: 'relative',
            float: 'right',
            top: '-4rem',
            right: '1rem',
            '&:hover': {
                textDecoration: 'none',
                transform: 'scale(1.1)',
                cursor: 'pointer'
            },
            '&:focus': {
                textDecoration: 'none',
                transform: 'scale(1.1)',
                cursor: 'pointer'
            }
        },
        '& .modal_body': {
            padding: '1.5rem',
            textAlign: '-webkit-center'
        },
        '& .modal_header': {
            padding: '1.5rem'
        }
    }
});