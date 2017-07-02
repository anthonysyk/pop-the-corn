package models

import models.kaggle.Movie
import models.themoviedb.MovieDetails

case class FullMovie(
                  movie: Movie,
                  movieDetails: Seq[MovieDetails] = Seq.empty[MovieDetails]
                )
