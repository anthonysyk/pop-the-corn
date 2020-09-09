# Pop The Corn

## Description

Side-Project to apply what I learned

```
.
├── api
├── data
├── front
├── indexer
├── libraries
└── mlapi
```

## Data

### Content-Based Recommendation

 - Natural Language Processing (NLP)
	 - TF-IDF
	 - Latent Dirichlet Allocation (LDA)
 - Users Preferences
    - Features to consider : Genres, Actors, Directors, etc ... (For the moment I only use Genre)
    - Based on the user ratings of several movie profiles, we establish a user profile
    - Cosine Similarity to find the similarity between user profile and movie profile

### Collaborative Recommendation

 - ... Not enough data yet ...

----------
## Backend

### Enriching and Indexing in ES with Akka Actors And Spark

 - Akka Actor System : restriction : 40 queries per second allowed by external API
	 - System based on a supervisor sending batch of movies to some workers to enrich the movie and indexing it
 - Spark : Creating autocomplete index from movie titles in ES to ES
     - Backpressure with ```.coalesce(20)```
 - GraphQL : Use to query the machine learning API with Sangria

----------

## Front

React/Redux

![portfolio-popthecorn](https://user-images.githubusercontent.com/6373950/197738651-bd1807a5-2e3c-448f-a9e8-c2b453df5cc0.jpg)

