(defproject lambada "1.0.3"
  :description "The messy parts for running clojure on AWS Lambda."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.amazonaws/aws-lambda-java-core "1.1.0"]]
  :profiles {:test {:dependencies [[org.clojure/data.json "0.2.6"]]}})
