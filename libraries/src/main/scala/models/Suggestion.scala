package models

case class Suggestion (
                        id: Option[Int],
                        title: String,
                        url: Option[String],
                        votes_average: Option[Double],
                        votes: Int
                      )

case class SuggestionES(
                         suggest: Suggestion,
                         ngram: String,
                         ngram_folded: String,
                         votes: Int
                       )