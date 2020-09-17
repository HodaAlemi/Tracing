// Inspired by https://gist.github.com/chadselph/65f21fc86f873d6569f4cfe4f96ce036
package tracing

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{extractRequest, mapResponse, provide}
import io.opentracing.propagation.Format
import io.opentracing.tag.{StringTag, Tags}
import io.opentracing.{Span, SpanContext, Tracer}
import scala.util.Try

trait AkkaHttpTraceDirectives {

  /** This method will throw an IllegalArgumentException for a bad
  tracer header, or return null for no header. Handle both cases as None */
  def getParentSpanContext(request: HttpRequest, tracer: Tracer): Option[SpanContext] = {
    Try (
      tracer.extract(Format.Builtin.HTTP_HEADERS, new AkkaHttpHeaderExtractor(request.headers))
    ).filter(_ != null).toOption
  }

  def withTrace(tracer: Tracer)(operationName: String): Directive1[Span] = {
    extractRequest.flatMap { request =>
      val parent: Option[SpanContext] = getParentSpanContext(request, tracer)

      val span: Span = parent.fold(
        tracer.buildSpan(operationName).start())(
        p => tracer.buildSpan(operationName).asChildOf(p).start()
      )

      mapResponse { response =>
        span.setTag(new StringTag("http.request"), request.entity.toString)
        span.setTag(new StringTag("trace.id"), span.context().toTraceId)
        span.setTag(Tags.HTTP_STATUS.getKey, response.status.intValue())
        span.setTag(Tags.HTTP_URL.getKey, request.effectiveUri(securedConnection = false).toString())
        span.setTag(Tags.HTTP_METHOD.getKey, request.method.value)
        span.finish()
        response
      } & provide(span)
    }
  }


}
