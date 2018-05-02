(ns lambada.core
  (:import [com.amazonaws.services.lambda.runtime
            RequestStreamHandler RequestHandler]))

(defmacro deflambdafn
  "Create a named class that can be invoked as a AWS Lambda function.
  `name` must be a symbol defining a fully qualified class name.
  `args` must be a vector of 2 or 3 arguments, 
  `body` is the body that will be executed by your lambda function.

  Args:
  You can use 2 or 3 arguments: [in context] or [in out context].
  in contains the arguments passed to your lambda function.
  context (a `com.amazonaws.services.lambda.runtime.Context`) contains the context supplied by AWS Lambda.
  If you use out (a `java.io.OutputStream`), your response must be written to it.
  Also, in will be a `java.io.InputStream`.
  If you don't use out, the return value of your lambda function will be used as a response.

  Example:
  Two args, return value as response
  (deflambdafn com.mycompany.somedomain.MyFunctionHandler
    [in ctx]
    (println \"Request:\" in)
    (handle-request in ctx))

  Three args, writing response to OutputStream
  (deflambdafn com.mycompany.otherdomain.OtherFunctionHandler
    [in out ctx]
    ;; write to output stream
    (.write (io/writer out) (str (handle-request in ctx)))
    (cheshire/generate-stream (handle-request in ctx) (io/writer out)))
  "
  [name args & body]
  (assert (symbol? name) "Lambda handler class name must be a symbol")
  (assert (and (vector? args)
               (or (= 2 (count args))
                   (= 3 (count args))))
          "Lambda handler args must be a vector of 2 or 3 elements")
  (let [prefix (gensym)
        handleRequestMethod (symbol (str prefix "handleRequest"))
        aws-interface (if (= 3 (count args))
                        com.amazonaws.services.lambda.runtime.RequestStreamHandler
                        com.amazonaws.services.lambda.runtime.RequestHandler)]
    `(do
       (gen-class
        :name ~name
        :prefix ~prefix
        :implements [~aws-interface])
       (defn ~handleRequestMethod
         ~(into ['this] args)
         ~@body))))
