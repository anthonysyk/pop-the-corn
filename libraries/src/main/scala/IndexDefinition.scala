package ptc.libraries

import com.sksamuel.elastic4s.ElasticDsl.{create, mapping, _}
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.{AsciiFoldingTokenFilter, CustomAnalyzerDefinition, _}

sealed trait IndexDefinition {
  val IndexName: String
  val TypeName: String
}

case class EsIndexDefinition(indexName: String, indexDefinition: CreateIndexDefinition)

object MovieIndexDefinition extends IndexDefinition {
  val IndexName = "movies"
  val TypeName = "movie"
  val MovieDefinition: CreateIndexDefinition = create index IndexName mappings {
    mapping(TypeName) as(
      "adult" typed BooleanType,
      "backdrop_path" typed StringType index "not_analyzed",
      nestedField("belongs_to_collection") as (
        "id" typed IntegerType,
        "name" typed StringType index "not_analyzed",
        "poster_path" typed StringType index "not_analyzed",
        "backdrop_path" typed StringType index "not_analyzed"
      ),
      "budget" typed DoubleType,
      nestedField("genres") as (
        "id" typed IntegerType,
        "name" typed StringType index "not_analyzed"
      ),
      "homepage" typed StringType index "not_analyzed",
      "id" typed IntegerType,
      "imdb_id" typed StringType index "not_analyzed",
      "original_language" typed StringType index "not_analyzed",
      "original_title" typed StringType index "not_analyzed",
      "overview" typed StringType index "not_analyzed",
      "popularity" typed DoubleType,
      "poster_path" typed StringType index "not_analyzed",
      nestedField("production_companies") as (
        "iso_3166_1" typed StringType index "not_analyzed",
        "name" typed StringType index "not_analyzed"
      ),
      nestedField("production_countries") as (
        "name" typed StringType index "not_analyzed",
        "id" typed IntegerType
      ),
      "release_date" typed StringType index "not_analyzed",
      "revenue" typed IntegerType,
      "runtime" typed IntegerType,
      nestedField("spoken_languages") as (
        "iso_639_1" typed StringType index "not_analyzed",
        "name" typed StringType index "not_analyzed"
      ),
      "status" typed StringType index "not_analyzed",
      "title" typed StringType analyzer "default",
      "vote_average" typed DoubleType,
      "vote_count" typed IntegerType
    )
  } analysis {
    CustomAnalyzerDefinition(
      "default",
      WhitespaceTokenizer,
      WordDelimiterTokenFilter("word_delimiter"),
      ElisionTokenFilter("elision", Iterable("l", "m", "t", "qu", "n", "s", "j")),
      LowercaseTokenFilter,
      StopTokenFilter("myTokenFilter1", enablePositionIncrements = true, ignoreCase = true),
      AsciiFoldingTokenFilter
    )
  }
  val esIndexConfiguration = EsIndexDefinition(IndexName, MovieDefinition)
}

object SuggestIndexDefinition extends IndexDefinition{
  val IndexName = "suggest_movies"
  val TypeName = "suggest"
  val SuggestionMapping: CreateIndexDefinition = create index IndexName mappings {
    mapping(TypeName) as(
      nestedField("suggest") as(
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

object DiscoveredMovieIndexDefinition extends IndexDefinition{
  val IndexName = "discovered_movies"
  val TypeName = "movie"
  val DiscoveredMovieMapping: CreateIndexDefinition = create index IndexName mappings {
    mapping(TypeName) as(
      "poster_path" typed StringType index "not_analyzed",
      "adult" typed BooleanType,
      "overview" typed StringType index "not_analyzed",
      "release_date" typed StringType index "not_analyzed",
      "genre_ids" typed StringType index "not_analyzed",
      "id" typed StringType index "not_analyzed",
      "original_title" typed StringType index "not_analyzed",
      "original_language" typed StringType index "not_analyzed",
      "title" typed StringType index "not_analyzed",
      "backdrop_path" typed StringType index "not_analyzed",
      "popularity" typed DoubleType,
      "vote_count" typed IntegerType,
      "video" typed BooleanType,
      "vote_average" typed DoubleType
    )
  }
  val esIndexConfiguration = EsIndexDefinition(IndexName, DiscoveredMovieMapping)
}