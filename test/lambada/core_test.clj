(ns lambada.core-test
  (:require [clojure.test :refer :all]
            [lambada.core :as l]))

(def name-bad-values [nil "" 1 () []])
(def args-bad-values [nil "" 1 ()])
(def body-bad-values [nil "" 1 []])

(deftest deflambdafn-test
  (testing "should throw assert error if name is not a symbol"
    (doseq [bad-value name-bad-values]
      (is (thrown? java.lang.AssertionError
                   (macroexpand
                    `(l/deflambdafn ~bad-value
                       [in out context]
                       (println "hello world")))))))

  (testing "should throw assert error if args is not a vector"
    (doseq [bad-value args-bad-values]
      (is (thrown? java.lang.AssertionError
                   (macroexpand
                    `(l/deflambdafn org.test.lambda
                       ~bad-value
                       (println "hello world")))))))

  (testing "should generate function with 3 params"
    (is (macroexpand
         `(l/deflambdafn org.test.lambda
            [in out context]
            "return value"))))

  (testing "should generate function with 2 params"
    (is (macroexpand
         `(l/deflambdafn org.test.lambda
            [in context]
            "return value"))))


  (testing "using 3 params, the class implements RequestStreamHandler"
    (let [expanded (macroexpand
                    `(l/deflambdafn org.test.lambda
                       [in out context]
                       "return value"))
          gen-class-expr (second expanded)

          _ (is (some #(= :implements %) gen-class-expr)
                (str "The gen-class expression should contain an :implements keyword and vector, but was:\n" gen-class-expr))

          implements-vec  (->> gen-class-expr
                               (drop-while #(not= :implements %))
                               second)]
      (is (some
           #(= com.amazonaws.services.lambda.runtime.RequestStreamHandler %)
           implements-vec)
          (str "The vector of generated class should contain RequestStreamHandler, but was:\n"
               implements-vec))))

  (testing "using 2 params, the class implements RequestHandler"
    (let [expanded (macroexpand
                    `(l/deflambdafn org.test.lambda
                       [in context]
                       "return value"))
          gen-class-expr (second expanded)

          _ (is (some #(= :implements %) gen-class-expr)
                (str "The gen-class expression should contain an :implements keyword and vector, but was:\n" gen-class-expr))

          implements-vec  (->> gen-class-expr
                               (drop-while #(not= :implements %))
                               second)]
      (is (some
           #(= com.amazonaws.services.lambda.runtime.RequestHandler %)
           implements-vec)
          (str "The vector of generated class should contain RequestHandler, but was:\n"
               implements-vec)))))
