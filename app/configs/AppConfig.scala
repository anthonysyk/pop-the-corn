package configs

import javax.inject.{Inject, Singleton}

import play.api.Configuration

@Singleton
class AppConfig @Inject()(
                         config: Configuration
                         ) {

  val tmdbFindBaseUri = config.getString("tmdb_find_base_uri")

  val tmdbFindParameters = config.getString("tmdb_find_parameters")

  val tmdbSearchBaseUri = config.getString("tmdb_search_base_uri")

  val tmdbSearchParameters = config.getString("tmdb_search_parameters")



}
