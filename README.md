Pop The Corn
===================

Description
----------
Side-Project to apply what I learned

Data
----------

###Content-Based Recommendation

 - Natural Language Processing (NLP)
	 - TF-IDF
	 - Latent Dirichlet Allocation (LDA)
 - Clustering
	 - ...

### Collaborative Recommendation

 - ... Not enough data yet ...

----------
Backend
----------

### Enriching and Indexing in ES with Akka Actors And Spark

 - Akka Actor System : restriction : 40 queries per second allowed by external API
	 - ==> System based on a supervisor sending batch of movies to some workers to enrich the movie and indexing it
 - Spark : Creating autocomplete index from movie titles in ES to ES
	 - ==> Backpressure with ```.coalesce(20)```

----------

# Front

React/Redux