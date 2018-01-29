import React from 'react'
import * as variables from '../variables';
import {Row, Col} from 'react-bootstrap'
import {logoFile} from "../variables";
import * as globalstyle from '../globalstyle';
import {style, classes} from 'typestyle';


const FooterComponent = ({values}) => <div className={footer}>
    <Row className="section">
        {
            values.map( (category,index)=>
                <Col key={index} lg={4}>
                    <h3>{category.title}</h3>
                    <ul>
                        {category.pages.map((page, key) => <li key={key}><a className={classes(globalstyle.hover__underline,'link__dark')} href={page.url}>{page.name}</a></li>)}
                    </ul>
                </Col>
            )
        }
    </Row>
    <div className={copyright_container}><i className="logo"/><span
        className={brand_copyright}>© {variables.site_name}</span></div>
</div>;


export default FooterComponent;

const footer = style({
    textAlign: 'center',
    borderTop: `1px solid ${variables.lightgrey}`,
    padding: '2rem 0',
    marginTop: '3rem',
    $nest: {
        '& .logo' : {
            background: `url(${logoFile}) no-repeat center`,
            backgroundSize: 'contain',
            display: 'inline-block',
            width: '40px',
            height: '40px',
            verticalAlign: 'middle'
        },
        ul: {
            listStyleType: 'none',
            color: variables.darkgrey,
            padding: 0
        },
        '& .link__dark': {
            color: variables.darkgrey
        }
    }
});

const copyright_container = style({
    width: '80%',
    margin: 'auto',
    borderTop: `1px solid ${variables.lightgrey}`,
    paddingTop: '2rem',
});

const brand_copyright = style({
    paddingLeft: '1rem',
    color: variables.darkgrey
});