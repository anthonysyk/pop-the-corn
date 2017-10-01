import axios from 'axios';

function searchByTitle(keyword) {
    return axios.get(`/search?q=${keyword}`)
        .then(res => res.data)
}

function suggest(keyword) {
    return axios.get(`/suggest?q=${keyword}`)
        .then (res => res.data)
}

function getMovieDetails(id) {
    return axios.get(`/movie/${id}`)
        .then (res => res.data)
}

function getPopularMovies() {
    return axios.get(`/popular`)
        .then (res => res.data)
}

function getBestRatedMovies() {
    return axios.get(`/bestrated`)
        .then (res => res.data)
}

function getPopularByGenre() {
    return axios.get(`/popularByGenre`)
        .then (res => res.data)
}

function getSimilarMoviesTfidf(id) {
    return axios.get(`/tfidf/${id}`)
        .then(res => res.data)
}

export { searchByTitle, suggest, getMovieDetails, getPopularMovies, getBestRatedMovies, getPopularByGenre, getSimilarMoviesTfidf };