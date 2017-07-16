package indexer.mapping

import com.sksamuel.elastic4s.ElasticDsl.{create, mapping, _}
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.{AsciiFoldingTokenFilter, CustomAnalyzerDefinition, _}

case class EsIndexDefinition(indexName: String, indexDefinition: CreateIndexDefinition)

object FullMovieIndexDefinition {
  val IndexName = "full_movie"
  val FullMovieDefinition: CreateIndexDefinition = create index IndexName mappings {
    mapping("movie") as(
      nestedField("movie") as(
        "externalId" typed StringType index "not_analyzed",
        "id" typed IntegerType,
        "title" typed StringType analyzer "default",
        "color" typed StringType index "not_analyzed",
        "duration" typed IntegerType,
        "budget" typed DoubleType,
        "gross" typed DoubleType,
        "genres" typed StringType,
        "contentRating" typed IntegerType index "not_analyzed",
        "faceNumbersInPoster" typed IntegerType,
        "language" typed StringType index "not_analyzed",
        "country" typed StringType index "not_analyzed",
        "titleYear" typed StringType index "not_analyzed",
        "aspectRatio" typed StringType index "not_analyzed",
        "castTotalFacebookLikes" typed IntegerType,
        "plotKeywords" typed StringType index "not_analyzed",
        "movieUrl" typed StringType index "not_analyzed",
        nestedField("casting") as(
          "role" typed StringType index "not_analyzed",
          "name" typed StringType index "not_analyzed",
          "facebookLikes" typed IntegerType
        ),
        nestedField("rating") as(
          "score" typed DoubleType,
          "numberOfReviews" typed IntegerType,
          "numberOfVotes" typed IntegerType,
          "numberOfCriticsForReviews" typed IntegerType
        )
      ),
      nestedField("movie_details") as(
        "adult" typed BooleanType,
        nestedField("belongs_to_collection") as(
          "id" typed IntegerType,
          "name" typed StringType index "not_analyzed",
          "poster_path" typed StringType index "not_analyzed",
          "backdropath" typed StringType index "not_analyzed"
        ),
        "budget" typed FloatType,
        nestedField("genres") as(
          "id" typed IntegerType,
          "name" typed StringType index "not_analyzed"
        ),
        "id" typed IntegerType,
        "imdb_id" typed StringType index "not_analyzed",
        "original_language" typed StringType index "not_analyzed",
        "original_title" typed StringType index "not_analyzed",
        "overview" typed StringType index "not_analyzed",
        "popularity" typed FloatType,
        "poster_path" typed StringType,
        nestedField("production_companies") as(
          "name" typed StringType index "not_analyzed",
          "id" typed IntegerType
        ),
        nestedField("production_countries") as(
          "iso_3166_1" typed StringType index "not_analyzed",
          "name" typed StringType index "not_analyzed"
        ),
        "release_date" typed StringType index "not_analyzed",
        "revenue" typed IntegerType,
        "runtime" typed IntegerType,
        nestedField("spoken_languages") as(
          "iso_639_1" typed StringType index "not_analyzed",
          "name" typed StringType index "not_analyzed"
        ),
        "status" typed StringType index "not_analyzed",
        "title" typed StringType index "not_analyzed",
        "vote_average" typed FloatType,
        "vote_count" typed IntegerType,
        nestedField("movie_details") as(
          "adult" typed BooleanType,
          nestedField("belongs_to_collection") as(
            "id" typed IntegerType,
            "name" typed StringType index "not_analyzed",
            "poster_path" typed StringType index "not_analyzed",
            "backdropath" typed StringType index "not_analyzed"
          ),
          "budget" typed FloatType,
          nestedField("genres") as(
            "id" typed IntegerType,
            "name" typed StringType index "not_analyzed"
          ),
          "id" typed IntegerType,
          "imdb_id" typed StringType index "not_analyzed",
          "original_language" typed StringType index "not_analyzed",
          "original_title" typed StringType index "not_analyzed",
          "overview" typed StringType index "not_analyzed",
          "popularity" typed FloatType,
          "poster_path" typed StringType,
          nestedField("production_companies") as(
            "name" typed StringType index "not_analyzed",
            "id" typed IntegerType
          ),
          nestedField("production_countries") as(
            "iso_3166_1" typed StringType index "not_analyzed",
            "name" typed StringType index "not_analyzed"
          ),
          "release_date" typed StringType index "not_analyzed",
          "revenue" typed IntegerType,
          "runtime" typed IntegerType,
          nestedField("spoken_languages") as(
            "iso_639_1" typed StringType index "not_analyzed",
            "name" typed StringType index "not_analyzed"
          ),
          "status" typed StringType index "not_analyzed",
          "title" typed StringType index "not_analyzed",
          "vote_average" typed FloatType,
          "vote_count" typed IntegerType
        )
      )
    )
  } analysis {
    CustomAnalyzerDefinition(
      "default",
      WhitespaceTokenizer,
      LowercaseTokenFilter,
      AsciiFoldingTokenFilter)
  }
  val esIndexConfiguration = EsIndexDefinition(IndexName, FullMovieDefinition)
}

object MovieIndexDefinition {
  val IndexName = "movies_index"
  val MovieDefinition: CreateIndexDefinition = create index "movies_index" mappings {
    mapping ("movie") as (
      "externalId" typed StringType index "not_analyzed",
      "id" typed IntegerType,
      "title" typed StringType analyzer "default",
      "color" typed StringType index "not_analyzed",
      "duration" typed IntegerType,
      "budget" typed DoubleType,
      "gross" typed DoubleType,
      "genres" typed StringType,
      "contentRating" typed IntegerType index "not_analyzed",
      "faceNumbersInPoster" typed IntegerType,
      "language" typed StringType index "not_analyzed",
      "country" typed StringType index "not_analyzed",
      "titleYear" typed StringType index "not_analyzed",
      "aspectRatio" typed StringType index "not_analyzed",
      "castTotalFacebookLikes" typed IntegerType,
      "plotKeywords" typed StringType index "not_analyzed",
      "movieUrl" typed StringType index "not_analyzed",
      nestedField ("casting") as (
        "role" typed StringType index "not_analyzed",
        "name" typed StringType index "not_analyzed",
        "facebookLikes" typed IntegerType
      ),
      nestedField ("rating") as (
        "score" typed DoubleType,
        "numberOfReviews" typed IntegerType,
        "numberOfVotes" typed IntegerType,
        "numberOfCriticsForReviews" typed IntegerType
      )
    )
  } analysis {
    CustomAnalyzerDefinition (
      "default",
      WhitespaceTokenizer,
      LowercaseTokenFilter,
      AsciiFoldingTokenFilter)
  }
  val esIndexConfiguration = EsIndexDefinition(IndexName, MovieDefinition)
}

object SuggestIndexDefinition {
  val IndexName = "suggest_movies"
  val SuggestionMapping: CreateIndexDefinition = create index IndexName mappings {
    mapping("suggest") as(
      nestedField("suggest") as (
        "id" typed StringType index "not_analyzed",
        "title" typed StringType index "not_analyzed",
        "url" typed StringType index "not_analyzed",
        "vote_average" typed DoubleType,
        "votes" typed IntegerType
      ),
      "ngram" typed StringType analyzer "ngram",
      "ngram_folded" typed StringType analyzer "ngram_folded",
      "votes" typed IntegerType
    )
  } analysis(
    CustomAnalyzerDefinition(
      "suggest",
      LowercaseTokenizer,
      WordDelimiterTokenFilter("word_delimiter"),
      AsciiFoldingTokenFilter,
      ElisionTokenFilter("elision", Iterable("l", "m", "t", "qu", "n", "s", "j"))
    ),
    CustomAnalyzerDefinition(
      "ngram",
      LowercaseTokenizer,
      WordDelimiterTokenFilter("word_delimiter"),
      EdgeNGramTokenFilter("edge_ngram", 1, 20),
      ElisionTokenFilter("elision", Iterable("l", "m", "t", "qu", "n", "s", "j"))
    ),
    CustomAnalyzerDefinition(
      "ngram_folded",
      LowercaseTokenizer,
      AsciiFoldingTokenFilter,
      EdgeNGramTokenFilter("edge_ngram", 1, 20),
      ElisionTokenFilter("elision", Iterable("l", "m", "t", "qu", "n", "s", "j"))
    )
  )
  val esIndexConfiguration = EsIndexDefinition(IndexName, SuggestionMapping)
}