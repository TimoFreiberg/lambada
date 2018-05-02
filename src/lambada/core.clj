(ns lambada.core
  (:import [com.amazonaws.services.lambda.runtime RequestStreamHandler]))

(defmacro deflambdafn
  "Create a named class that can be invoked as a AWS Lambda function."
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
