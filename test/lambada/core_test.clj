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

  (testing "should throw assert error if body is not a vector"
    (doseq [bad-value body-bad-values]
      (is (thrown? java.lang.AssertionError
                   (macroexpand
                    `(l/deflambdafn org.test.lambda
                       [in out context]
                       ~bad-value))))))

  (testing "should generate function"
    (is (macroexpand
         `(l/deflambdafn org.test.lambda
            [in out context]
            ())))))
