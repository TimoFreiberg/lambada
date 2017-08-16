(ns lambada.core
  (:require [clojure.spec.alpha :as s])
  (:import [com.amazonaws.services.lambda.runtime RequestStreamHandler]))

(s/def ::name symbol?)
(s/def ::args vector?)
(s/def ::body (s/coll-of list?))
(s/def ::arguments (s/cat :name ::name :args ::args :body ::body))

(defn- assert-args [args]
  (assert (s/valid? ::arguments args)
          (s/explain ::arguments args)))

(defmacro def-lambda-fn
  "Create a named class that can be invoked as a AWS Lambda function."
  [name args & body]
  (assert-args [name args body])
  (let [prefix (gensym)
        handleRequestMethod (symbol (str prefix "handleRequest"))]
    `(do
       (gen-class
        :name ~name
        :prefix ~prefix
        :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
       (defn ~handleRequestMethod
         ~(into ['this] args)
         ~@body))))
