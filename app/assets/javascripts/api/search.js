import axios from 'axios';

function searchByTitle(keyword) {
    return axios.get(`/search?q=${keyword}`)
        .then(res => res.data)
}

function suggest(keyword) {
    return axios.get(`/suggest?q=${keyword}`)
        .then (res => res.data)
}

export { searchByTitle, suggest };