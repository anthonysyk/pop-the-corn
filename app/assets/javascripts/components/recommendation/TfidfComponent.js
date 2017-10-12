import React, {Component} from 'react'
import SearchBar from "../search/SearchBar";
import {getSimilarMoviesTfidf} from '../../actions/actions';
import {Table} from 'react-bootstrap';
import {connect} from 'react-redux'

@connect(
    state => ({
        machineLearningData: state.machineLearningData
    })
)

class TfidfComponent extends Component {

    constructor(props) {
        super(props);
    }

    handleSubmit(event, id) {
        event.preventDefault();
        this.props.dispatch(getSimilarMoviesTfidf(id));
        this.setState({showSuggestion: false});
    }

    render() {

        const {machineLearningData} = this.props;

        return (
            <div>
                <SearchBar customHandleSubmit={this.handleSubmit.bind(this)}/>
                {
                    machineLearningData.tfidfMovies.length > 0 &&
                    <Table responsive striped bordered condensed>
                        <thead>
                        <tr>
                            <th className="text-center" >#</th>
                            <th className="text-center" >Title</th>
                            <th className="text-center" >Cos Similarity</th>
                        </tr>
                        </thead>
                        <tbody>
                        {machineLearningData.tfidfMovies.map(
                            (movie, key) =>
                                <tr key={key}>
                                    <td>{movie.id}</td>
                                    <td>{movie.title}</td>
                                    <td>{movie.tfidfSimilarity ? movie.tfidfSimilarity.toString().substring(0, 6) : "Input"}</td>
                                </tr>
                        )}
                        </tbody>
                    </Table>
                }
            </div>
        );
    }

}

export default TfidfComponent;