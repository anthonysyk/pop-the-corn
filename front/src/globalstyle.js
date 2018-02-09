import {style} from "typestyle";

export const hover__black = style({
    '&, &:hover, &:active, &:visited, &:focus': {
        textDecoration: 'none',
        color: 'white',
        padding: '0.4rem',
        lineHeight: '3.2rem',
        borderRadius: '4px'
    },
    '&:hover': {backgroundColor: 'rgba(0, 0, 0, .5)'}
});

export const hover__underline = style({
    '&:hover, &:focus': {
        textDecoration: 'underline',
    }
});

export const section = style({
    padding: '3.5rem 6.5rem 2rem 6.5rem',
    marginRight: '0' // Fixes right margin space
});