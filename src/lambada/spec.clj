(ns lambada.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::name symbol?)
(s/def ::args vector?)
(s/def ::body (s/coll-of list?))
(s/def ::arguments (s/cat :name ::name :args ::args :body ::body)) 

(defn assert-args [args]
  (assert (s/valid? ::arguments args)
          (s/explain ::arguments args)))
