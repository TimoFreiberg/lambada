(ns lambada.example
  (:require
   [clojure.test :refer (deftest)]
   [lambada.core :refer [deflambdafn]]
   [clojure.data.json :as json]
   [clojure.java.io :as io]))

(deftest definitions-should-compile

  (defn handle-event
    [event]
    (println "Got the following event: " (pr-str event))
    {:status "ok"})

  (deflambdafn example.lambda.OtherLambdaFn
    [in ctx]
    (let [event (json/read (io/reader in))
          res (handle-event event)]
      (json/write-str res)))

  )
