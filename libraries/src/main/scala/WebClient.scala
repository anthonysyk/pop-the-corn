package ptc.libraries

import java.io.{BufferedReader, DataOutputStream, IOException, InputStreamReader}
import java.net.{HttpURLConnection, URL}

import scala.util.{Failure, Success, Try}

object WebClient {
  import scala.collection.JavaConversions._

  def doGet(urlpath: String, headers:Map[String, String], disableCache: Boolean = true) = {
    println(s"""${new java.util.Date()} - Calling $urlpath""")
    var in: BufferedReader = null
    var conn: HttpURLConnection = null
    val url: URL = new URL(urlpath)
    val result: StringBuffer = new StringBuffer
    try {
      conn = url.openConnection().asInstanceOf[HttpURLConnection]
      conn.setDoOutput(true)
      conn.setDoInput(true)
      conn.setInstanceFollowRedirects(false)
      conn.setRequestMethod("GET")
      conn.setUseCaches(false)
      conn.setConnectTimeout(300000)
      conn.setReadTimeout(300000)
      if(disableCache){
        conn.setRequestProperty("Cache-Control","no-cache, no-store, must-revalidate")
        conn.setRequestProperty("Pragma","no-cache")
        conn.setRequestProperty("Expires","0")
      }
      headers.foreach(h => conn.setRequestProperty(h._1, h._2))
      in = new BufferedReader(new InputStreamReader(conn.getInputStream))
      var line: String = in.readLine()
      while (line != null) {
        result.append(line)
        result.append("\n")
        line = in.readLine()
      }
      Success(result.toString)
    }
    catch {
      case e: Exception =>  Failure(e)
    }
    finally {
      if (in != null) {
        try {
          in.close()
        } catch {
          case e: IOException =>
        }
      }
      if (conn != null) {
        conn.disconnect()
      }
    }
  }


  def doPostRetrieveHeaders(urlpath: String, content: String): Try[Map[String, List[String]]] = {
    var in: BufferedReader = null
    var conn: HttpURLConnection = null
    val url: URL = new URL(urlpath)
    val result: StringBuffer = new StringBuffer
    try {
      conn = url.openConnection().asInstanceOf[HttpURLConnection]

      val utf8Content = content.getBytes("UTF-8")
      val contentType = "application/json; charset=UTF-8"

      conn.setDoOutput(true)
      conn.setDoInput(true)
      conn.setInstanceFollowRedirects(true)
      conn.setRequestProperty("Content-Type", contentType)
      conn.setRequestMethod("POST")
      conn.setUseCaches(false)
      conn.setConnectTimeout(30000)
      conn.setReadTimeout(30000)
      val wr: DataOutputStream = new DataOutputStream(conn.getOutputStream)
      wr.write(utf8Content)
      wr.flush
      wr.close
      in = new BufferedReader(new InputStreamReader(conn.getInputStream))
      Success(conn.getHeaderFields.toMap.map(t => t._1 -> t._2.toList))
    }
    catch {
      case e: Exception =>  Failure(e)
    }
    finally {
      if (in != null) {
        try {
          in.close()
        } catch {
          case e: IOException =>
        }
      }
      if (conn != null) {
        conn.disconnect()
      }
    }

  }



  def doPost(urlpath: String, content: String, contentType:String = "application/json; charset=UTF-8", headers:Map[String, String]= Map.empty, timeout: Int = 30000): Try[String] = {
    println(s"""${new java.util.Date()} - Posting $content to $urlpath""")
    var in: BufferedReader = null
    var conn: HttpURLConnection = null
    val url: URL = new URL(urlpath)
    val result: StringBuffer = new StringBuffer
    try {
      conn = url.openConnection().asInstanceOf[HttpURLConnection]

      val utf8Content = content.getBytes("UTF-8")

      conn.setDoOutput(true)
      conn.setDoInput(true)
      conn.setInstanceFollowRedirects(true)
      conn.setRequestProperty("Content-Type", contentType)
      conn.setRequestMethod("POST")
      conn.setUseCaches(false)
      conn.setConnectTimeout(timeout)
      conn.setReadTimeout(timeout)
      headers.foreach(h => conn.setRequestProperty(h._1, h._2))
      val wr: DataOutputStream = new DataOutputStream(conn.getOutputStream)
      wr.write(utf8Content)
      wr.flush
      wr.close
      in = new BufferedReader(new InputStreamReader(conn.getInputStream))
      var line: String = in.readLine()
      while (line != null) {
        result.append(line)
        result.append("\n")
        line = in.readLine()
      }
      Success(result.toString)
    }
    catch {
      case e: Exception =>  Failure(e)
    }
    finally {
      if (in != null) {
        try {
          in.close()
        } catch {
          case e: IOException =>
        }
      }
      if (conn != null) {
        conn.disconnect()
      }
    }

  }

}