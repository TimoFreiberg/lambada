# lambada 

Use AWS Lambda in Clojure way.

Current version:

TODO

## Usage

Define a Lambda handler using the `deflambdafn` macro provided by `lambada.core`:

```clojure
(ns example.lambda
  (:require [lambada.core :refer [deflambdafn]]))

(deflambdafn example.lambda.MyLambdaFn
  [in out ctx]
  (println "AWS Lambda function called!"))
```

When this namespace is AOT compiled, it will generate a class called
`example.lambda.MyLambdaFn` that implements the AWS Lambda
[`RequestStreamHandler`](http://docs.aws.amazon.com/lambda/latest/dg/java-handler-using-predefined-interfaces.html)
interface using the args and body provided.
To enable AOT compilation for the uberjar profile, see the following example:
```clojure
(defproject my-lambda-project "0.1.0-SNAPSHOT"
  :description "Example lambada project."
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [lambada "1.0.0"]]
  :profiles {:uberjar {:aot :all}}
  :uberjar-name "my-lambda-project.jar")
```

Simplest way to deploy is to create an uberjar using leiningen or boot and then use that as the JAR you upload to AWS Lambda. Assuming you have an uberjar called `my-lambda-project.jar` in `target`, the following commands will do the job:

```
$ aws lambda create-function \
    --region eu-west-1 \
    --function-name my-lambda-project \
    --zip-file fileb://$(pwd)/target/my-lambda-project.jar \
    --role arn:aws:iam::YOUR-AWS-ACCOUNT-ID:role/lambda_basic_execution \
    --handler example.lambda.MyLambdaFn \
    --runtime java8 \
    --timeout 15 \
    --memory-size 512
...

$ aws lambda invoke \
    --invocation-type RequestResponse \
    --function-name my-lambda-project \
    --region eu-west-1 \
    --log-type Tail \
    --payload '{"some":"input"}' \
    outfile.txt
...

```

See [example](https://github.com/atsman/lambada/tree/master/example) for an example project.

## Startup time

While using aws lambda with Clojure i noticed, that cold start of application can take up to 5 seconds. That was a big problem in my case. You can't pass jvm args and somehow tune JVM. You can only play with memory size in aws console.

The only solution is to ping your function every 5 minutes to keep it warm. The question is, who should ping your function, which application, and where is it deployed. The answer - you can use Cloud Watch service and create scheduled warm up event for your lambda function.

Setup instraction:
1. Open `CloudWatch Management Console`.
2. Go to `Events`.
3. Click `Create rule`
4. Check Event Source as `Schedule`.
5. In my case the default value of `Fixed rate of` was 5 minutes.
6. Click on `Add target`.
7. Select your lambda function.
8. Click on `Configure details`.
9. Give name to event.
10. Click `Create rule`.

Congratulation. You created warm up event for your lambda function. Enjoy quick response time.

# License

Copyright © 2017 Timo Freiberg

Distributed under the Eclipse Public License, the same as Clojure.
