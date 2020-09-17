package tracing

import io.jaegertracing.Configuration
import io.jaegertracing.internal.JaegerTracer
import io.opentracing.util.GlobalTracer

object TraceConfig {

  /** Tracer Implementation */
  private val tracer: JaegerTracer = {
    val samplerConfig = Configuration.SamplerConfiguration.fromEnv.withType("const").withParam(1)
    val reporterConfig = Configuration.ReporterConfiguration.fromEnv.withLogSpans(true)
    val config = new Configuration("OrderRegistryRequestTracing").withSampler(samplerConfig).withReporter(reporterConfig)
    config.getTracer
  }

  /** First register the tracer, only one time */
  def getTracer: JaegerTracer = {
    GlobalTracer.registerIfAbsent(tracer)
    tracer
  }

}
