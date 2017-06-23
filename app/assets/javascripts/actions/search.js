import axios from 'axios';

function searchByName(keyword) {
    axios.get('/search?q=${keyword}')
        .then(res => res.data)
}

export default {searchByName};