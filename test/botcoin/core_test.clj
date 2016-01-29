(ns botcoin.core-test
  (:require [clojure.test :refer :all]
            [botcoin.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(deftest sign-test
  (testing "Signing a string"
    (is (= (sign "my key" "string to sign")
           "my key:string to sign"))))

;;;; begin coin & tran tests
(deftest valid?-test
  (testing "Verifying signatures"
    (is (valid? []))
    (is (valid? [{:payee-pub-key "first recipient"
                  :signature "foobar"}]))
    (is (valid? [{:payee-pub-key "second recipient",
                  :signature "first recipient:332913733"}
                 {:payee-pub-key "first recipient",
                  :signature "foobar"}]))
    (is (not (valid? "my key" "my key:bogus string" "string to sign")))
    (is (not (valid? "my key" "not my key:string to sign" "string to sign")))))

(deftest transfer-test
  (testing "Transfering a new coin"
    
    (is (= [{:payee-pub-key "new owner key",
             :signature "previous owner key:-1422918588"}]
           (transfer [] {:symmetric-key "previous owner key"}
                     {:symmetric-key "new owner key"}))))
  (testing "Transferring an existing coin"
    (is (= [{:payee-pub-key "second recipient",
             :signature "first recipient:332913733"}
            {:payee-pub-key "first recipient",
             :signature "foobar"}]
         (transfer [{:payee-pub-key "first recipient"
                     :signature "foobar"}]
                   {:symmetric-key "first recipient"}
                   {:symmetric-key "second recipient"})))))

;;;; end coin & tran tests

;;;; begin block tests
(deftest valid-block?-test
  (testing "Valid blocks"
    (is (valid-block? 1234 [[4882 "" nil]]))
    (is (valid-block? 1234 [[4882 "" nil] [4882 "" nil]]))
    (is (valid-block? 1234
                      [[5524 "1234031466"
                        {:payee-pub-key "yolo",
                         :signature "ted:332913733"}
                        {:payee-pub-key "zebulon",
                         :signature "ulrich:332913733"}]
                       [4611 ""
                        {:payee-pub-key "alice",
                         :signature "zero:332913733"}
                        {:payee-pub-key "charlie",
                         :signature "hector:332913733"}]]))))


;;;; end block tests

;;;; begin blockchain tests
(deftest new-blockchain?-test
  (testing "Is new blockchain"
    (is (new-blockchain? []))
    (is (new-blockchain? [[4882 "" nil]])))
  (testing "Is not new blockchain"
    (is (new-blockchain? [[4882 "" nil] [4882 "" nil]]))))
;;;; end blockchain tests
