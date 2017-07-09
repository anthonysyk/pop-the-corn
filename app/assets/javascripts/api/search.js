import axios from 'axios';

function searchByTitle(keyword) {
    return axios.get(`/search?q=${keyword}`)
        .then(res => res.data)
}

export { searchByTitle };