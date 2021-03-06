

## Introduction

Distributed tracing is particularly well-suited for debugging and monitoring microservices. [Contributed libraries](https://github.com/opentracing-contrib?utf8=%E2%9C%93&q=scala&type=&language=) are available in different languages.

OrderRegistryAPI is a Rest API written in Scala, and is based on Akka Actor framework and Akka HTTP modules. This application has the simple functionality of registering orders and is meant to show how to implement tracing for Akka Actors and Akka HTTP.  


## Tracer
A Tracer is the actual implementation that will record the Spans and publish them somewhere. 
In this project [Jaeger](https://www.jaegertracing.io/docs/1.16/getting-started/) client library has been used.


## Jaeger
[Jaeger](https://www.jaegertracing.io/docs/1.16/getting-started/) is a distributed tracing platform. Jaeger backend with multiple storage backends is designed to have no single points of failure. [One Example](https://github.com/yurishkuro/opentracing-tutorial/tree/master/java). [Here](https://medium.com/velotio-perspectives/a-comprehensive-tutorial-to-implementing-opentracing-with-jaeger-a01752e1a8ce) You will find a comprehensive tutorial to implementing openTracing with Jaeger.

Starting Jaeger backend in docker: 
```
docker run \
  --rm \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 16686:16686 \
  jaegertracing/all-in-one:1.7 \
  --log-level=debug

Jaeger GUI on localhost http://localhost:16686
```

## Running the application
You can run the application through Intellij or with ``` sbt run ``` command. The application will be available at ```http://localhost:9001/```.
