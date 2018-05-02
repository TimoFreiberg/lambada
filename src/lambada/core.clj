(ns lambada.core
  (:import [com.amazonaws.services.lambda.runtime RequestHandler]))

(defmacro deflambdafn
  "Create a named class that can be invoked as a AWS Lambda function.
  `name` must be a symbol defining a fully qualified class name.
  `args` must be a vector of 2 arguments.
  `body` is the body that will be executed by your lambda function.

  Arguments:
  The 2 arguments are
  1. The input argument to your lambda function
  2. The context of your lambda function invocation, supplied by the AWS infrastructure. This is a `com.amazonaws.services.lambda.runtime.Context`.

  The return value of your handler function will be used as the response.

  Example:
  (deflambdafn com.mycompany.somedomain.MyFunctionHandler handleRequest
    [in ctx]
    (println \"Request:\" in)
    (handle-request in ctx))

  "
  [name args & body]
  (assert (symbol? name) "Lambda handler class name must be a symbol")
  (assert (and (vector? args)
               (= 2 (count args)))
          "Lambda handler args must be a vector of 2 elements")
  (let [method-name (symbol "-handleRequest")]
    `(do
       (gen-class
        :name ~name
        :implements [com.amazonaws.services.lambda.runtime.RequestHandler])
       (defn ~method-name
         ~(into ['this] args)
         ~@body))))
