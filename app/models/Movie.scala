package models

import models.kaggle.MovieData
import models.themoviedb.TMDBMovie

case class Movie(
                  movieData: MovieData,
                  movieDetails: Seq[TMDBMovie]
                )
