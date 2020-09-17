package tracing

import io.opentracing.tag.StringTag
import io.opentracing.{Span, Tracer}

case class TraceContext(tracer: Tracer, span: Span)

object CustomeTracing {

  def buildChildSpan(traceContext: TraceContext, operationName: String, parentSpan: Span) = {
    traceContext.tracer.buildSpan(operationName)
      .withTag(new StringTag("trace.id"), traceContext.span.context().toTraceId)
      .asChildOf(parentSpan).start()
  }


}
