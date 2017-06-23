package models

import models.kaggle.MovieData
import models.themoviedb.MovieDetails

case class Movie(
                  movieData: MovieData,
                  movieDetails: Seq[MovieDetails]
                )
