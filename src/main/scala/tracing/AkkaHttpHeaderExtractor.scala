// credit to: https://gist.github.com/chadselph/65f21fc86f873d6569f4cfe4f96ce036
package tracing

import java.util
import java.util.Map.Entry
import akka.http.scaladsl.model.HttpHeader
import io.opentracing.propagation.TextMap

import scala.collection.JavaConverters.asJavaIteratorConverter

/**
 * Used to extract an iterator of Entry[String, String] to the
 * io.opentracing API from akka-http request
 */
class AkkaHttpHeaderExtractor(headers: Seq[HttpHeader]) extends TextMap {

  val headersEntries: Seq[Entry[String, String]] = headers.map(header => new Entry[String, String] {
    override def getValue: String = header.value()
    override def getKey: String = header.name()

    override def setValue(value: String): String = throw new UnsupportedOperationException("Cannot set a value")
  })

  override def put(key: String, value: String): Unit = {
    throw new UnsupportedOperationException("AkkaHttpHeaderExtractor should only be used with Tracer.extract()")
  }

  override def iterator(): util.Iterator[Entry[String, String]] = headersEntries.iterator.asJava
}